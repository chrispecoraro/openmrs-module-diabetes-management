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

import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.diabetesmanagement.InsulinType;
import org.openmrs.module.diabetesmanagement.service.InsulinTypeService;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for encounter.list.
 */
public class InsulinTypeListController extends SimpleFormController {
	
	/** Logger for this class and subclasses. */
	private final Log log = LogFactory.getLog(getClass());
	
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
			String[] insulinTypeIds = request.getParameterValues("insulinTypeId");
			InsulinTypeService its = (InsulinTypeService) Context.getService(InsulinTypeService.class);
			
			String success = "";
			String error = "";
			
			MessageSourceAccessor msa = getMessageSourceAccessor();
			String deleted = msa.getMessage("general.deleted");
			String notDeleted = msa.getMessage("general.cannot.delete");
			
			try {
				for (String p : insulinTypeIds) {
					try {
						InsulinType t = its.getInsulinType(Integer.valueOf(p));
						its.purgeInsulinType(t);
						if (!success.equals(""))
							success += "<br/>";
						success += p + " " + deleted;
					}
					catch (APIException e) {
						log.warn("Error deleting InsulinType", e);
						if (!error.equals(""))
							error += "<br/>";
						error += p + " " + notDeleted;
					}
				}
			}
			catch (NullPointerException e) {
				if (!error.equals(""))
					error += "<br/>";
				error += "diabetesmanagement.error.noInsulinTypeSelected";
				showForm(request, response, errors);
			}
			
			view = getSuccessView();
			if (!success.equals(""))
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, success);
			if (!error.equals(""))
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, error);
		}
		return new ModelAndView(new RedirectView(view));
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
		
		// Default empty object
		List<InsulinType> insulinTypeList = new Vector<InsulinType>();
		
		// Only fill the object is the user has authenticated properly
		if (Context.isAuthenticated())
			insulinTypeList = ((InsulinTypeService) Context.getService(InsulinTypeService.class)).getAllInsulinTypes(true);
		
		return insulinTypeList;
	}
}
