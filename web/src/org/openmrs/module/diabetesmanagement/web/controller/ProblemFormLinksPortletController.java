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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.diabetesmanagement.DiabetesManagementConfig;
import org.openmrs.module.medicalproblem.Problem;
import org.openmrs.module.medicalproblem.service.ProblemService;

/**
 * Controller for problemFormLinks.portlet.
 */
public class ProblemFormLinksPortletController extends org.openmrs.web.controller.PortletController {
	
	/**
	 * @see org.openmrs.web.controller.PortletController#populateModel(javax.servlet.http.
	 *      HttpServletRequest, java.util.Map<java.lang.String, java.lang.Object>)
	 * @param request Current HTTP request.
	 * @param model The model.
	 */
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		if (Context.hasPrivilege("View Problems")) {
			DiabetesManagementConfig config = new DiabetesManagementConfig();
			String problemId = request.getParameter("problemId");
			Boolean isDiabetesProblem = false;
			if (problemId != null) {
				Problem problem = ((ProblemService) Context.getService(ProblemService.class)).getProblem(Integer
				        .valueOf(problemId));
				model.put("problem", problem);
				if (problem.getConcept() != null
				        && (problem.getConcept() == config.getRelevantConcepts().get("t1dm") || problem.getConcept() == config
				                .getRelevantConcepts().get("t2dm")))
					isDiabetesProblem = true;
				model.put("isDiabetesProblem", isDiabetesProblem);
				
			}
		}
	}
}
