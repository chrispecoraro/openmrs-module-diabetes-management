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
package org.openmrs.module.diabetesmanagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.medicalproblem.Problem;
import org.openmrs.module.medicalproblem.service.ProblemService;
import org.openmrs.util.OpenmrsConstants;

/**
 * Module config object, contains the relevant concepts in the current installation.
 */
public class DiabetesManagementConfig {
	
	/** Map of existing mapped concepts. */
	private Map<String, Concept> relevantConcepts;
	
	// Constructors
	
	/**
	 * To add new concept mappings, add new elements to the Map in the constructor.
	 */
	public DiabetesManagementConfig() {
		relevantConcepts = new HashMap<String, Concept>();
		
		// Diagnoses
		relevantConcepts.put("t1dm", null);
		relevantConcepts.put("t2dm", null);
		
		// Physical
		relevantConcepts.put("sbp", null); // systolic blood pressure;
		relevantConcepts.put("dbp", null); // diastolic blood pressure;
		relevantConcepts.put("footExam", null);
		relevantConcepts.put("toothInspection", null);
		relevantConcepts.put("eyeExamination", null);
		relevantConcepts.put("fundoscopy", null);
		relevantConcepts.put("visualAcuity", null);
		
		// Lipids
		relevantConcepts.put("glu", null); // blood glucose
		relevantConcepts.put("hba1c", null); // glycosylated hemoglobin
		relevantConcepts.put("chol", null); // total cholesterol
		relevantConcepts.put("hdl", null); // hi-density lipoprotein cholesterol
		relevantConcepts.put("ldl", null); // lo-density lipoprotein cholesterol
		relevantConcepts.put("tg", null); // triglycerides
		relevantConcepts.put("cr", null); // creatinine
		relevantConcepts.put("na", null); // sodium
		relevantConcepts.put("k", null); // potassium
		
		// Simulation parameters
		relevantConcepts.put("rtg", null); // renal threshold
		relevantConcepts.put("ccr", null); // renal function
		relevantConcepts.put("sh", null); // hepatic insulin sensivity
		relevantConcepts.put("sp", null); // peripheral insulin sensivity
		
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_GLOBAL_PROPERTIES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
		populateConcepts();
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_GLOBAL_PROPERTIES);
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
	}
	
	/**
	 * Getter for relevantConcepts.
	 * 
	 * @return this.relevantConcepts
	 */
	public Map<String, Concept> getRelevantConcepts() {
		return relevantConcepts;
	}
	
	/**
	 * Setter for relevantConcepts.
	 * 
	 * @param map the map to set
	 */
	public void setRelevantConcepts(Map<String, Concept> map) {
		this.relevantConcepts = map;
	}
	
	/**
	 * Checks whether the given Patient has at least one diabetes mellitus Problem.
	 * 
	 * @param patientId The ID of the Patient.
	 * @return true if the patient has at least one diabetes mellitus problem; false otherwise.
	 */
	public boolean checkPatientForDiabetesMellitus(Integer patientId) {
		try {
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			Context.addProxyPrivilege("View Problems");
			
			if (patientId != null) {
				Patient patient = Context.getPatientService().getPatient(patientId);
				if (patient != null) {
					List<Problem> problems = ((ProblemService) Context.getService(ProblemService.class))
					        .getOpenProblemsByPatient(patient);
					for (Problem p : problems) {
						if (p.getConcept() != null
						        && (p.getConcept().equals(getRelevantConcepts().get("t1dm")) || p.getConcept().equals(
						            getRelevantConcepts().get("t2dm"))))
							return true;
					}
				}
			}
			return false;
		}
		finally {
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			Context.removeProxyPrivilege("View Problems");
		}
	}
	
	/**
	 * Creates/updates global properties that contain concept IDs of the object's concepts.
	 */
	public void saveConfig() {
		AdministrationService as = Context.getAdministrationService();
		List<GlobalProperty> gpl = as.getAllGlobalProperties();
		Boolean exists = false;
		
		for (Entry<String, Concept> e : relevantConcepts.entrySet()) {
			exists = false;
			if (e.getValue() != null) {
				for (GlobalProperty gp : gpl) {
					if (gp.getProperty().equals("diabetesmanagement.concept." + e.getKey())) {
						gp.setPropertyValue(e.getValue().getConceptId().toString());
						exists = true;
					}
				}
				if (!exists) {
					GlobalProperty gp = new GlobalProperty("diabetesmanagement.concept." + e.getKey(), e.getValue()
					        .getConceptId().toString());
					gpl.add(gp);
				}
			}
		}
		as.saveGlobalProperties(gpl);
	}
	
	/**
	 * Populates Concepts in relevantConcepts from existing global properties.
	 */
	private void populateConcepts() {
		ConceptService cs = Context.getConceptService();
		
		for (Entry<String, Concept> e : relevantConcepts.entrySet()) {
			for (GlobalProperty gp : Context.getAdministrationService().getAllGlobalProperties()) {
				if (gp.getProperty().equals("diabetesmanagement.concept." + e.getKey()))
					e.setValue(cs.getConcept(Integer.valueOf(gp.getPropertyValue())));
			}
		}
	}
}
