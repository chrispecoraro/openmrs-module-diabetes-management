/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.diabetesmanagement.web.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.diabetesmanagement.DiabetesManagementConfig;
import org.openmrs.module.medicalproblem.Problem;
import org.openmrs.module.medicalproblem.service.ProblemService;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.ConceptNameEditor;
import org.openmrs.propertyeditor.DrugEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.OrderEditor;
import org.openmrs.propertyeditor.PersonEditor;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for patientObs.form.
 */
public class PatientObsFormController extends SimpleFormController {
	
	/**
	 * Allows for Integers, Dates and OpenMRS types to be used as values in input tags. Normally,
	 * only strings and lists are expected.
	 * 
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest,
	 *      org.springframework.web.bind.ServletRequestDataBinder)
	 * @param request Current HTTP request.
	 * @param binder The new binder instance.
	 * @throws Exception In case of errors.
	 */
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);
		String datePattern = OpenmrsUtil.getDateFormat().toPattern() + " HH:mm";
		
		binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, true));
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(new SimpleDateFormat(datePattern), true));
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor(true)); // allow
		// for
		// an
		// empty
		// boolean
		// value
		binder.registerCustomEditor(Person.class, new PersonEditor());
		binder.registerCustomEditor(Order.class, new OrderEditor());
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(Drug.class, new DrugEditor());
		binder.registerCustomEditor(ConceptName.class, new ConceptNameEditor());
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#onBind(javax.servlet.http.HttpServletRequest,
	 *      java.lang.Object)
	 * @param request Current servlet request.
	 * @param command Form object with request parameters bound onto it.
	 * @throws Exception In case of errors.
	 */
	@Override
	protected void onBind(HttpServletRequest request, Object command) throws Exception {
		
		Obs obs = (Obs) command;
		
		// set the question concept if only the question concept name is set
		// ABK: Obs.getConceptName() has been removed
		// if (obs.getConcept() == null && obs.getConceptName() != null) {
		// obs.setConcept(obs.getConceptName().getConcept());
		// }
		
		// set the answer concept if only the answer concept name is set
		if (obs.getValueCoded() == null && obs.getValueCodedName() != null) {
			obs.setValueCoded(obs.getValueCodedName().getConcept());
		}
		
		super.onBind(request, command);
	}
	
	/**
	 * The onSubmit method receives the form/command object that was modified by the input form and
	 * saves it to the database.
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 * @param request Current servlet request.
	 * @param response Current servlet response.
	 * @param command Form object with request parameters bound onto it.
	 * @param errors Holder without errors.
	 * @return The prepared model and view, or null.
	 * @throws Exception In case of errors.
	 */
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
	                                BindException errors) throws Exception {
		
		HttpSession httpSession = request.getSession();
		String view = getFormView();
		
		if (Context.isAuthenticated()) {
			Obs obs = (Obs) command;
			
			ObsService os = Context.getObsService();
			// PatientObsService os = (PatientObsService)
			// Context.getService(PatientObsService.class);
			
			try {
				Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
				Context.addProxyPrivilege(OpenmrsConstants.PRIV_ADD_OBS);
				Context.addProxyPrivilege("View Problems");
				Context.addProxyPrivilege("Edit Problems");
				
				// if the user is just editing the observation
				if (request.getParameter("saveObs") != null) {
					String reason = request.getParameter("editReason");
					if (obs.getObsId() != null && (reason == null || reason.length() == 0)) {
						errors.reject("editReason", "Obs.edit.reason.empty");
						return showForm(request, response, errors);
					}
					
					// A bug in Hibernate prevents the proper usage of data created by users who are
					// also patients. As a workaround, the creator id will be set to 1 until that
					// issue is resolved.
					// UPDATE: resolved in 1.5.0.7216
					// obs.setCreator(Context.getUserService().getUser(1));
					
					os.saveObs(obs, reason);
					
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Obs.saved");
					
					// Adding obs to a problem, if one was selected and belongs to the enterer
					String problemId = request.getParameter("problemId");
					if (problemId != null && problemId.length() > 0) {
						ProblemService ps = (ProblemService) Context.getService(ProblemService.class);
						Problem problem = ps.getProblem(Integer.valueOf(problemId));
						if (problem.getPatient().getPatientId().equals(obs.getPersonId())) {
							problem.addObs(obs);
							ps.saveProblem(problem);
							httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
							    "medicalproblem.patient.obsSavedAndAddedToProblem");
						} else
							throw new APIException("Problem and observation records must belong to the same patient.");
					}
				}
			}
			catch (APIException e) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, e.getMessage());
				return showForm(request, response, errors);
			}
			finally {
				Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
				Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_OBS);
				Context.removeProxyPrivilege("Edit Problems");
				Context.removeProxyPrivilege("View Problems");
			}
			view = getSuccessView();
			view = view + "?conceptName=" + request.getParameter("conceptName");
		}
		
		return new ModelAndView(new RedirectView(view));
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest,
	 *      java.lang.Object, org.springframework.validation.BindException)
	 * @param request Current HTTP request.
	 * @param command Form object with request parameters bound onto it.
	 * @param errors Validation errors holder.
	 * @return A Map with reference data entries, or null if none.
	 * @throws Exception In case of invalid state or arguments.
	 */
	protected Map<String, Object> referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
		
		Obs obs = (Obs) command;
		String conceptName = request.getParameter("conceptName");
		
		Map<String, Object> map = new HashMap<String, Object>();
		String defaultVerbose = "false";
		
		if (Context.isAuthenticated() && Context.hasPrivilege("Add Own Observations")) {
			if (obs.getConcept() != null)
				// Cannot just do 'obs.getConcept().getBestName(request.getLocale())' anymore
				// (LazyInitException)
				map.put("conceptName", Context.getConceptService().getConcept(obs.getConcept().getConceptId()).getBestName(
				    request.getLocale()));
			defaultVerbose = Context.getAuthenticatedUser().getUserProperty(OpenmrsConstants.USER_PROPERTY_SHOW_VERBOSE);
		}
		map.put("defaultVerbose", defaultVerbose.equals("true") ? true : false);
		
		String editReason = request.getParameter("editReason");
		if (editReason == null)
			editReason = "";
		
		map.put("editReason", editReason);
		
		// Adding concept if name has been passed
		if (conceptName != null && conceptName.length() > 0) {
			if (conceptName.equals("weight")) {
				String conceptIdWeight = Context.getAdministrationService().getGlobalProperty("concept.weight");
				if (conceptIdWeight != null && conceptIdWeight.length() > 0)
					map.put("selectedConcept", Context.getConceptService().getConcept(Integer.valueOf(conceptIdWeight)));
			} else {
				DiabetesManagementConfig config = new DiabetesManagementConfig();
				map.put("selectedConcept", config.getRelevantConcepts().get(conceptName));
			}
		}
		
		// Adding list of unresolved problems
		try {
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
			Context.addProxyPrivilege("View Problems");
			Patient patient = Context.getPatientService().getPatient(Context.getAuthenticatedUser().getPersonId());
			if (patient != null) {
				List<Problem> problems = ((ProblemService) Context.getService(ProblemService.class))
				        .getOpenProblemsByPatient(patient);
				map.put("problemList", problems);
			}
		}
		finally {
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
			Context.removeProxyPrivilege("View Problems");
		}
		
		String datePattern = OpenmrsUtil.getDateFormat().toPattern() + " HH:mm";
		map.put("datePattern", datePattern);
		String datePatternNoSeparators = datePattern.replace("/", "");
		datePatternNoSeparators = datePatternNoSeparators.replace("-", "");
		datePatternNoSeparators = datePatternNoSeparators.replace(".", "");
		map.put("datePatternNoSeparators", datePatternNoSeparators);
		
		return map;
	}
	
	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the
	 * form/command object to load into the request.
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 * @param request Current HTTP request.
	 * @return The backing object.
	 * @throws ServletException In case of invalid state or arguments.
	 */
	protected Object formBackingObject(HttpServletRequest request) throws ServletException {
		
		Obs obs = null;
		
		if (Context.isAuthenticated() && Context.hasPrivilege("Add Own Observations")) {
			String obsId = request.getParameter("obsId");
			
			try {
				Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);
				Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_LOCATIONS);
				
				if (obsId != null && obsId.length() > 0) {
					obs = Context.getObsService().getObs(Integer.valueOf(obsId));
					if (obs.getPersonId() != Context.getAuthenticatedUser().getPersonId()) {
						obs = null; // cannot access other people's observations
					}
				} else {
					obs = new Obs();
					obs.setPerson(Context.getAuthenticatedUser());
					obs.setLocation(Context.getLocationService().getLocation(1));
				}
			}
			finally {
				Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);
				Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_LOCATIONS);
			}
		}
		
		return obs;
	}
}
