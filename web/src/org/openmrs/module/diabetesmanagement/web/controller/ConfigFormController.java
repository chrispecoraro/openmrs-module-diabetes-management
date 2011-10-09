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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.diabetesmanagement.DiabetesManagementConfig;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.FormEditor;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for config.form.
 */
public class ConfigFormController extends SimpleFormController {
	
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
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Form.class, new FormEditor());
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#processFormSubmission(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletresponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 * @param request Current servlet request.
	 * @param response Current servlet response.
	 * @param command Form object with request parameters bound onto it.
	 * @param errors Holder without errors.
	 * @return The prepared model and view, or null.
	 * @throws Exception In case of errors.
	 */
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object command,
	                                             BindException errors) throws Exception {
		DiabetesManagementConfig config = (DiabetesManagementConfig) command;
		
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
		try {
			//
		}
		finally {
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
		}
		
		return super.processFormSubmission(request, response, config, errors);
	}
	
	/**
	 * The onSubmit function receives the form/command object that was modified by the input form
	 * and saves it to the database.
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
		
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
		try {
			if (Context.isAuthenticated() && Context.hasPrivilege("Manage Global Properties")) {
				DiabetesManagementConfig config = (DiabetesManagementConfig) command;
				config.saveConfig();
				view = getSuccessView();
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "diabetesmanagement.config.saved");
			} else
				view = getFormView();
		}
		finally {
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
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
		DiabetesManagementConfig config = new DiabetesManagementConfig();
		
		return config;
	}
}
