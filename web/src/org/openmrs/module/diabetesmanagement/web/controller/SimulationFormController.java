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

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.diabetesmanagement.DiabetesManagementConfig;
import org.openmrs.module.diabetesmanagement.Simulation;
import org.openmrs.module.diabetesmanagement.propertyeditor.InsulinTypeEditor;
import org.openmrs.module.diabetesmanagement.service.InsulinTypeService;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * Controller for simulation.form.
 */
public class SimulationFormController extends SimpleFormController {
	
	/** File name for plasma glucose results for the current simulation run. */
	private static final String FILENAME_PLASMA_GLUCOSE_CURRENT = "sim_glu_cur.bin";
	
	/** File name for plasma glucose results for the previous simulation run. */
	private static final String FILENAME_PLASMA_GLUCOSE_PREVIOUS = "sim_glu_pre.bin";
	
	/** File name for plasma insulin results for the current simulation run. */
	private static final String FILENAME_PLASMA_INSULIN_CURRENT = "sim_ins_cur.bin";
	
	/** File name for plasma insulin results for the previous simulation run. */
	private static final String FILENAME_PLASMA_INSULIN_PREVIOUS = "sim_ins_pre.bin";
	
	/** File name for carbohydrate intake for the current simulation run. */
	private static final String FILENAME_MEALS_CURRENT = "sim_mea_cur.bin";
	
	/** File name for carbohydrate intake for the previous simulation run. */
	private static final String FILENAME_MEALS_PREVIOUS = "sim_mea_pre.bin";
	
	/** File name for administered insulin injections (1). */
	private static final String FILENAME_INJECTIONS_1 = "sim_inj_1.bin";
	
	/** File name for administered insulin injections (2). */
	private static final String FILENAME_INJECTIONS_2 = "sim_inj_2.bin";
	
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
		binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, true));
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(OpenmrsUtil.getDateFormat(), true));
		binder.registerCustomEditor(org.openmrs.Concept.class, new ConceptEditor());
		binder.registerCustomEditor(org.openmrs.module.diabetesmanagement.InsulinType.class, new InsulinTypeEditor());
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
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
		try {
			if (Context.isAuthenticated()) {
				StopWatch stopwatch = new StopWatch();
				ObjectOutputStream out = null;
				File f = null;
				File root = OpenmrsUtil.getDirectoryInApplicationDataDirectory("diabetesmanagement/simulation");
				String sessionId = request.getSession().getId() + "_";
				
				// Benchmarking the simulation model run
				Simulation sim = (Simulation) command;
				stopwatch.start();
				sim.runSimulation();
				stopwatch.stop();
				sim.setExecutionTime(stopwatch.getTotalTimeSeconds());
				
				// Serializing current results, if available
				if (sim.getResultsAvailableCurrent()) {
					// Current plasma glucose
					f = new File(root.getAbsolutePath(), sessionId + FILENAME_PLASMA_GLUCOSE_CURRENT);
					f.delete();
					out = new ObjectOutputStream(new FileOutputStream(f));
					out.writeObject(sim.getResultGlucoseCurrent());
					out.close();
					// Current plasma insulin
					f = new File(root.getAbsolutePath(), sessionId + FILENAME_PLASMA_INSULIN_CURRENT);
					f.delete();
					out = new ObjectOutputStream(new FileOutputStream(f));
					out.writeObject(sim.getResultInsulinCurrent());
					out.close();
					// Current meals
					if (sim.getMealsCurrent() != null) {
						f = new File(root.getAbsolutePath(), sessionId + FILENAME_MEALS_CURRENT);
						f.delete();
						out = new ObjectOutputStream(new FileOutputStream(f));
						out.writeObject(sim.getMealsCurrent());
						out.close();
					}
					// Current insulin injections (1)
					if (sim.getInsulinInjections1() != null) {
						f = new File(root.getAbsolutePath(), sessionId + FILENAME_INJECTIONS_1);
						f.delete();
						out = new ObjectOutputStream(new FileOutputStream(f));
						out.writeObject(sim.getInsulinInjections1());
						out.close();
					}
					// Current insulin injections (2)
					if (sim.getInsulinInjections2() != null) {
						f = new File(root.getAbsolutePath(), sessionId + FILENAME_INJECTIONS_2);
						f.delete();
						out = new ObjectOutputStream(new FileOutputStream(f));
						out.writeObject(sim.getInsulinInjections2());
						out.close();
					}
				}
				
				// Serializing previous results, if available
				if (sim.getResultsAvailablePrevious()) {
					// Previous plasma glucose
					f = new File(root.getAbsolutePath(), sessionId + FILENAME_PLASMA_GLUCOSE_PREVIOUS);
					f.delete();
					out = new ObjectOutputStream(new FileOutputStream(f));
					out.writeObject(sim.getResultGlucosePrevious());
					out.close();
					// Previous plasma insulin
					f = new File(root.getAbsolutePath(), sessionId + FILENAME_PLASMA_INSULIN_PREVIOUS);
					f.delete();
					out = new ObjectOutputStream(new FileOutputStream(f));
					out.writeObject(sim.getResultInsulinPrevious());
					out.close();
					// Previous meals
					if (sim.getMealsPrevious() != null) {
						f = new File(root.getAbsolutePath(), sessionId + FILENAME_MEALS_PREVIOUS);
						f.delete();
						out = new ObjectOutputStream(new FileOutputStream(f));
						out.writeObject(sim.getMealsCurrent());
						out.close();
					}
				}
			}
		}
		finally {
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
		}
		
		return showForm(request, response, errors);
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
		Map<String, Object> map = new HashMap<String, Object>();
		String patientId = request.getParameter("patientId");
		String sessionId = request.getSession().getId() + "_";
		
		if (Context.isAuthenticated() && Context.hasPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS) && patientId != null
		        && patientId.length() > 0) {
			Patient p = Context.getPatientService().getPatient(Integer.valueOf(patientId));
			if (p != null)
				map.put("patient", p);
		}
		
		File root = OpenmrsUtil.getDirectoryInApplicationDataDirectory("diabetesmanagement/simulation");
		map.put("filenamePlasmaGlucoseCurrent", URLEncoder.encode(
		    new File(root, sessionId + FILENAME_PLASMA_GLUCOSE_CURRENT).getAbsolutePath(), "utf-8"));
		map.put("filenamePlasmaGlucosePrevious", URLEncoder.encode(new File(root, sessionId
		        + FILENAME_PLASMA_GLUCOSE_PREVIOUS).getAbsolutePath(), "utf-8"));
		map.put("filenamePlasmaInsulinCurrent", URLEncoder.encode(
		    new File(root, sessionId + FILENAME_PLASMA_INSULIN_CURRENT).getAbsolutePath(), "utf-8"));
		map.put("filenamePlasmaInsulinPrevious", URLEncoder.encode(new File(root, sessionId
		        + FILENAME_PLASMA_INSULIN_PREVIOUS).getAbsolutePath(), "utf-8"));
		map.put("filenameMealsCurrent", URLEncoder.encode(new File(root, sessionId + FILENAME_MEALS_CURRENT)
		        .getAbsolutePath(), "utf-8"));
		map.put("filenameMealsPrevious", URLEncoder.encode(new File(root, sessionId + FILENAME_MEALS_PREVIOUS)
		        .getAbsolutePath(), "utf-8"));
		map.put("filenameInjections1", URLEncoder.encode(
		    new File(root, sessionId + FILENAME_INJECTIONS_1).getAbsolutePath(), "utf-8"));
		map.put("filenameInjections2", URLEncoder.encode(
		    new File(root, sessionId + FILENAME_INJECTIONS_2).getAbsolutePath(), "utf-8"));
		
		// Time reference for the graph servlet
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(1), cal.get(2), cal.get(5), 0, 0, 0);
		map.put("today", cal.getTimeInMillis());
		
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
		Simulation sim = null;
		
		if (Context.isAuthenticated() && Context.hasPrivilege("Run Diabetes Simulations")) {
			sim = new Simulation();
			String patientId = request.getParameter("patientId");
			
			sim
			        .setInsulinList(((InsulinTypeService) Context.getService(InsulinTypeService.class))
			                .getAllInsulinTypes(false));
			
			// Initial values
			sim.setInitialArterialGlucose(4.4);
			
			// Retrieve patient-specific parameters from obs, if a patientId has been passed
			if (patientId != null) {
				try {
					Patient p = null;
					if (Context.hasPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS))
						p = Context.getPatientService().getPatient(Integer.valueOf(patientId));
					else if (Integer.valueOf(patientId).equals(Context.getAuthenticatedUser().getPersonId())) {
						// let patients use their own data
						Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
						p = Context.getPatientService().getPatient(Context.getAuthenticatedUser().getPersonId());
					}
					if (p != null) {
						Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);
						DiabetesManagementConfig config = new DiabetesManagementConfig();
						Obs o = null;
						Integer conceptIdWeight = Integer.valueOf(Context.getAdministrationService().getGlobalProperty(
						    "concept.weight"));
						List<Obs> obsWeight = Context.getObsService().getObservationsByPersonAndConcept(p,
						    Context.getConceptService().getConcept(conceptIdWeight));
						List<Obs> obsRTG = Context.getObsService().getObservationsByPersonAndConcept(p,
						    (Concept) config.getRelevantConcepts().get("rtg"));
						List<Obs> obsCCR = Context.getObsService().getObservationsByPersonAndConcept(p,
						    (Concept) config.getRelevantConcepts().get("ccr"));
						List<Obs> obsSh = Context.getObsService().getObservationsByPersonAndConcept(p,
						    (Concept) config.getRelevantConcepts().get("sh"));
						List<Obs> obsSp = Context.getObsService().getObservationsByPersonAndConcept(p,
						    (Concept) config.getRelevantConcepts().get("sp"));
						if (obsWeight != null && obsWeight.size() > 0) {
							o = obsWeight.get(obsWeight.size() - 1);
							sim.setWeight(o.getValueNumeric());
							sim.setPatientSpecificWeight(true);
						}
						if (obsRTG != null && obsRTG.size() > 0) {
							o = obsRTG.get(obsRTG.size() - 1);
							sim.setRTG(o.getValueNumeric());
							sim.setPatientSpecificRTG(true);
						}
						if (obsCCR != null && obsCCR.size() > 0) {
							o = obsCCR.get(obsCCR.size() - 1);
							sim.setCCR(o.getValueNumeric());
							sim.setPatientSpecificCCR(true);
						}
						if (obsSh != null && obsSh.size() > 0) {
							o = obsSh.get(obsSh.size() - 1);
							sim.setSh(o.getValueNumeric());
							sim.setPatientSpecificSh(true);
						}
						if (obsSp != null && obsSp.size() > 0) {
							o = obsSp.get(obsSp.size() - 1);
							sim.setSp(o.getValueNumeric());
							sim.setPatientSpecificSp(true);
						}
					}
				}
				finally {
					Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
					Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);
				}
			}
		}
		
		// If still no values, set default values
		if (sim.getWeight() == null)
			sim.setWeight(75.0);
		if (sim.getRTG() == null)
			sim.setRTG(Double.valueOf(Context.getAdministrationService().getGlobalProperty(
		    "diabetesmanagement.simulation.rtgNormal")));
		if (sim.getCCR() == null)
			sim.setCCR(Double.valueOf(Context.getAdministrationService().getGlobalProperty(
			    "diabetesmanagement.simulation.ccrNormal")));
		if (sim.getSh() == null)
			sim.setSh(Double.valueOf(Context.getAdministrationService().getGlobalProperty(
			    "diabetesmanagement.simulation.insulinSensivityHepaticNormal")));
		if (sim.getSp() == null)
			sim.setSp(Double.valueOf(Context.getAdministrationService().getGlobalProperty(
			    "diabetesmanagement.simulation.insulinSensivityPeripheralNormal")));
		
		return sim;
	}
}
