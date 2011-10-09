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
import org.openmrs.api.context.Context;
import org.openmrs.module.diabetesmanagement.InsulinType;
import org.openmrs.module.diabetesmanagement.service.InsulinTypeService;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for insulinType.form.
 */
public class InsulinTypeFormController extends SimpleFormController {
	
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
		// NumberFormat nf = NumberFormat.getInstance(new Locale("en_US"));
		binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, true));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
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
			InsulinType type = (InsulinType) command;
			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.null");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "parameterS", "error.null");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "parameterA", "error.null");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "parameterB", "error.null");
			
			((InsulinTypeService) Context.getService(InsulinTypeService.class)).saveInsulinType(type);
			view = getSuccessView();
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "diabetesmanagement.insulinType.saved");
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
		InsulinType type = null;
		
		if (Context.isAuthenticated()) {
			String insulinTypeId = request.getParameter("insulinTypeId");
			
			if (insulinTypeId != null && insulinTypeId.length() > 0)
				type = ((InsulinTypeService) Context.getService(InsulinTypeService.class)).getInsulinType(Integer
				        .valueOf(insulinTypeId));
		}
		if (type == null)
			type = new InsulinType();
		
		return type;
	}
}
