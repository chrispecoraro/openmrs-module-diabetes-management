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

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Concept;

/**
 * Insulin types used for glucose-insulin metabolism simulation. Parameters s, a and b must be
 * provided by literature.
 */
public class InsulinType extends BaseOpenmrsMetadata {
	
	// Members
	
	/** Unique ID of this object. */
	private Integer insulinTypeId;
	
	/** Insulin type name. */
	private String name;
	
	/** Drug concept associated with this insulin type (optional). */
	private Concept concept;
	
	/** Simulation parameter s. */
	private Double parameterS;
	
	/** Simulation parameter a. */
	private Double parameterA;
	
	/** Simulation parameter b. */
	private Double parameterB;
	
	// Constructors
	
	/** Default constructor. */
	public InsulinType() {
	}
	
	/**
	 * All parameters constructor.
	 * 
	 * @param name Insulin type name.
	 * @param concept Optional drug Concept.
	 * @param s Simulation parameter s.
	 * @param a Simulation parameter a.
	 * @param b Simulation parameter b.
	 */
	public InsulinType(String name, Concept concept, Double s, Double a, Double b) {
		this.name = name;
		this.concept = concept;
		this.parameterS = s;
		this.parameterA = a;
		this.parameterB = b;
	}
	
	// Getters/setters
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulinTypeId
	 */
	public Integer getInsulinTypeId() {
		return insulinTypeId;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulinTypeId The insulinTypeId to set.
	 */
	public void setInsulinTypeId(Integer insulinTypeId) {
		this.insulinTypeId = insulinTypeId;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulinTypeId
	 */
	public Concept getConcept() {
		return concept;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param concept The concept to set.
	 */
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.parameterS
	 */
	public Double getParameterS() {
		return parameterS;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param parameterS The parameterS to set.
	 */
	public void setParameterS(Double parameterS) {
		this.parameterS = parameterS;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.parameterA
	 */
	public Double getParameterA() {
		return parameterA;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param parameterA The parameterA to set.
	 */
	public void setParameterA(Double parameterA) {
		this.parameterA = parameterA;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.parameterB
	 */
	public Double getParameterB() {
		return parameterB;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param parameterB The parameterB to set.
	 */
	public void setParameterB(Double parameterB) {
		this.parameterB = parameterB;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 * @return this.problemId
	 */
	public Integer getId() {
		return getInsulinTypeId();
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 * @param id The id to set.
	 */
	public void setId(Integer id) {
		setInsulinTypeId(id);
	}
}
