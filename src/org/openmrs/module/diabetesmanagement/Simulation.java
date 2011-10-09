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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;

/**
 * Simulation reference object. Contains the simulation parameters and two simulation model
 * instances (current and previous).
 */
public class Simulation {
	
	// Members
	
	/** Simulation model instance for the current run. */
	private SimulationModelAIDA simCurrent;
	
	/** Simulation model instance for the previous run. */
	private SimulationModelAIDA simPrevious;
	
	/** Indicates that results for the current run are available. */
	private Boolean resultsAvailableCurrent;
	
	/** Indicates that results for the previous run are available. */
	private Boolean resultsAvailablePrevious;
	
	/** Simulation runtime in milliseconds. */
	private Double executionTime;
	
	/** Initial plasma glucose level. */
	private Double initialGlucose;
	
	/** Initial arterial glucose level. */
	private Double initialArterialGlucose;
	
	/** Initial plasma insulin level. */
	private Double initialInsulin;
	
	/** Initial active insulin level. */
	private Double initialActiveInsulin;
	
	/** Patient's body weight. (kg) */
	private Double weight;
	
	/** Patient's renal threshold of glucose. (mmol/l) */
	private Double RTG;
	
	/** Patient's creatinine clearance rate (glomerular filtration). (ml/min) */
	private Double CCR;
	
	/** Patient's hepatic insulin sensivity. */
	private Double sh;
	
	/** Patient's peripheral insulin sensivity. */
	private Double sp;
	
	/**
	 * Indicates whether the weight value is generic or an actual observation value of a real
	 * patient.
	 */
	private Boolean patientSpecificWeight = false;
	
	/** Indicates whether the RTG value is generic or an actual observation value of a real patient. */
	private Boolean patientSpecificRTG = false;
	
	/** Indicates whether the CCR value is generic or an actual observation value of a real patient. */
	private Boolean patientSpecificCCR = false;
	
	/** Indicates whether the Sh value is generic or an actual observation value of a real patient. */
	private Boolean patientSpecificSh = false;
	
	/** Indicates whether the Sp value is generic or an actual observation value of a real patient. */
	private Boolean patientSpecificSp = false;
	
	/** Meal #1 time entered by the user. */
	private String mealTime1;
	
	/** Meal #2 time entered by the user. */
	private String mealTime2;
	
	/** Meal #3 time entered by the user. */
	private String mealTime3;
	
	/** Meal #4 time entered by the user. */
	private String mealTime4;
	
	/** Meal #5 time entered by the user. */
	private String mealTime5;
	
	/** Meal #6 time entered by the user. */
	private String mealTime6;
	
	/** Meal #1 carbohydates (g) entered by the user. */
	private String mealCarbs1;
	
	/** Meal #2 carbohydates (g) entered by the user. */
	private String mealCarbs2;
	
	/** Meal #3 carbohydates (g) entered by the user. */
	private String mealCarbs3;
	
	/** Meal #4 carbohydates (g) entered by the user. */
	private String mealCarbs4;
	
	/** Meal #5 carbohydates (g) entered by the user. */
	private String mealCarbs5;
	
	/** Meal #6 carbohydates (g) entered by the user. */
	private String mealCarbs6;
	
	/** Insulin injection #1 time entered by the user. */
	private String insulinTime1;
	
	/** Insulin injection #2 time entered by the user. */
	private String insulinTime2;
	
	/** Insulin injection #3 time entered by the user. */
	private String insulinTime3;
	
	/** Insulin injection #4 time entered by the user. */
	private String insulinTime4;
	
	/** First InsulinType insulin injection #1 dose entered by the user. */
	private String insulin1Dose1;
	
	/** First InsulinType insulin injection #2 dose entered by the user. */
	private String insulin1Dose2;
	
	/** First InsulinType insulin injection #3 dose entered by the user. */
	private String insulin1Dose3;
	
	/** First InsulinType insulin injection #4 dose entered by the user. */
	private String insulin1Dose4;
	
	/** Second InsulinType insulin injection #1 dose entered by the user. */
	private String insulin2Dose1;
	
	/** Second InsulinType insulin injection #2 dose entered by the user. */
	private String insulin2Dose2;
	
	/** Second InsulinType insulin injection #3 dose entered by the user. */
	private String insulin2Dose3;
	
	/** Second InsulinType insulin injection #4 dose entered by the user. */
	private String insulin2Dose4;
	
	/** List of available InsulinTypes. */
	private List<InsulinType> insulinList;
	
	/** First InsulinType selected from the list. */
	private InsulinType insulin1;
	
	/** Second InsulinType selected from the list. */
	private InsulinType insulin2;
	
	/** Needed to determine the locally used units for glucose (mmol/l or mg/dl). */
	private Concept conceptGlucose;
	
	// Constructors
	
	/** Default constructor. */
	public Simulation() {
		conceptGlucose = new DiabetesManagementConfig().getRelevantConcepts().get("glu");
	}
	
	/**
	 * All required parameters constructor.
	 * 
	 * @param weight The patient's weight.
	 * @param RTG The patient's renal threshold of glucose.
	 * @param CCR The patient's creatinine clearance rate.
	 * @param sh The patient's hepatic glucose sensivity.
	 * @param sp The patient's peripheral glucose sensivity.
	 * @param initialActiveInsulin The patient's initial active insulin level.
	 */
	public Simulation(Double weight, Double RTG, Double CCR, Double sh, Double sp, Double initialActiveInsulin) {
		this.weight = weight;
		this.RTG = RTG;
		this.CCR = CCR;
		this.sh = sh;
		this.sp = sp;
		this.initialActiveInsulin = initialActiveInsulin;
		conceptGlucose = new DiabetesManagementConfig().getRelevantConcepts().get("glu");
	}
	
	// Getters/setters
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.executionTime
	 */
	public Double getExecutionTime() {
		return executionTime;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param executionTime The executionTime to set.
	 */
	public void setExecutionTime(Double executionTime) {
		this.executionTime = executionTime;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.initialArterialGlucose
	 */
	public Double getInitialArterialGlucose() {
		return initialArterialGlucose;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param initialArterialGlucose The initialArterialGlucose to set.
	 */
	public void setInitialArterialGlucose(Double initialArterialGlucose) {
		this.initialArterialGlucose = initialArterialGlucose;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.initialGlucose
	 */
	public Double getInitialGlucose() {
		return initialGlucose;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param initialGlucose The initialGlucose to set.
	 */
	public void setInitialGlucose(Double initialGlucose) {
		this.initialGlucose = initialGlucose;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.initialInsulin
	 */
	public Double getInitialInsulin() {
		return initialInsulin;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param initialInsulin The initialInsulin to set.
	 */
	public void setInitialInsulin(Double initialInsulin) {
		this.initialInsulin = initialInsulin;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.initialActiveInsulin
	 */
	public Double getInitialActiveInsulin() {
		return initialActiveInsulin;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param initialActiveInsulin The initialActiveInsulin to set.
	 */
	public void setInitialActiveInsulin(Double initialActiveInsulin) {
		this.initialActiveInsulin = initialActiveInsulin;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.weight
	 */
	public Double getWeight() {
		return weight;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param weight The weight to set.
	 */
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.RTG
	 */
	public Double getRTG() {
		return RTG;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param rtg The rtg to set.
	 */
	public void setRTG(Double rtg) {
		RTG = rtg;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.CCR
	 */
	public Double getCCR() {
		return CCR;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param ccr The ccr to set.
	 */
	public void setCCR(Double ccr) {
		CCR = ccr;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.sh
	 */
	public Double getSh() {
		return sh;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param sh The sh to set.
	 */
	public void setSh(Double sh) {
		this.sh = sh;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.sp
	 */
	public Double getSp() {
		return sp;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param sp The sp to set.
	 */
	public void setSp(Double sp) {
		this.sp = sp;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.patientSpecificWeight
	 */
	public Boolean getPatientSpecificWeight() {
		return patientSpecificWeight;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param patientSpecificWeight The patientSpecificWeight to set.
	 */
	public void setPatientSpecificWeight(Boolean patientSpecificWeight) {
		this.patientSpecificWeight = patientSpecificWeight;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.patientSpecificRTG
	 */
	public Boolean getPatientSpecificRTG() {
		return patientSpecificRTG;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param patientSpecificRTG The patientSpecificRTG to set.
	 */
	public void setPatientSpecificRTG(Boolean patientSpecificRTG) {
		this.patientSpecificRTG = patientSpecificRTG;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.patientSpecificCCR
	 */
	public Boolean getPatientSpecificCCR() {
		return patientSpecificCCR;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param patientSpecificCCR The patientSpecificCCR to set.
	 */
	public void setPatientSpecificCCR(Boolean patientSpecificCCR) {
		this.patientSpecificCCR = patientSpecificCCR;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.patientSpecificSp
	 */
	public Boolean getPatientSpecificSh() {
		return patientSpecificSh;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param patientSpecificSh The patientSpecificSh to set.
	 */
	public void setPatientSpecificSh(Boolean patientSpecificSh) {
		this.patientSpecificSh = patientSpecificSh;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.patientSpecificSp
	 */
	public Boolean getPatientSpecificSp() {
		return patientSpecificSp;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param patientSpecificSp The patientSpecificSp to set.
	 */
	public void setPatientSpecificSp(Boolean patientSpecificSp) {
		this.patientSpecificSp = patientSpecificSp;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.mealTime1
	 */
	public String getMealTime1() {
		return mealTime1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param mealTime1 The mealTime1 to set.
	 */
	public void setMealTime1(String mealTime1) {
		this.mealTime1 = mealTime1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.mealTime2
	 */
	public String getMealTime2() {
		return mealTime2;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param mealTime2 The mealTime2 to set.
	 */
	public void setMealTime2(String mealTime2) {
		this.mealTime2 = mealTime2;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.mealTime3
	 */
	public String getMealTime3() {
		return mealTime3;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param mealTime3 The mealTime3 to set.
	 */
	public void setMealTime3(String mealTime3) {
		this.mealTime3 = mealTime3;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.mealTime4
	 */
	public String getMealTime4() {
		return mealTime4;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param mealTime4 The mealTime4 to set.
	 */
	public void setMealTime4(String mealTime4) {
		this.mealTime4 = mealTime4;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.mealTime5
	 */
	public String getMealTime5() {
		return mealTime5;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param mealTime5 The mealTime5 to set.
	 */
	public void setMealTime5(String mealTime5) {
		this.mealTime5 = mealTime5;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.mealTime6
	 */
	public String getMealTime6() {
		return mealTime6;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param mealTime6 The mealTime6 to set.
	 */
	public void setMealTime6(String mealTime6) {
		this.mealTime6 = mealTime6;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.mealCarbs1
	 */
	public String getMealCarbs1() {
		return mealCarbs1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param mealCarbs1 The mealCarbs1 to set.
	 */
	public void setMealCarbs1(String mealCarbs1) {
		this.mealCarbs1 = mealCarbs1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.mealCarbs2
	 */
	public String getMealCarbs2() {
		return mealCarbs2;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param mealCarbs2 The mealCarbs2 to set.
	 */
	public void setMealCarbs2(String mealCarbs2) {
		this.mealCarbs2 = mealCarbs2;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.mealCarbs3
	 */
	public String getMealCarbs3() {
		return mealCarbs3;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param mealCarbs3 The mealCarbs3 to set.
	 */
	public void setMealCarbs3(String mealCarbs3) {
		this.mealCarbs3 = mealCarbs3;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.mealCarbs4
	 */
	public String getMealCarbs4() {
		return mealCarbs4;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param mealCarbs4 The mealCarbs4 to set.
	 */
	public void setMealCarbs4(String mealCarbs4) {
		this.mealCarbs4 = mealCarbs4;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.mealCarbs5
	 */
	public String getMealCarbs5() {
		return mealCarbs5;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param mealCarbs5 The mealCarbs5 to set.
	 */
	public void setMealCarbs5(String mealCarbs5) {
		this.mealCarbs5 = mealCarbs5;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.mealCarbs6
	 */
	public String getMealCarbs6() {
		return mealCarbs6;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param mealCarbs6 The mealCarbs6 to set.
	 */
	public void setMealCarbs6(String mealCarbs6) {
		this.mealCarbs6 = mealCarbs6;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulinTime1
	 */
	public String getInsulinTime1() {
		return insulinTime1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulinTime1 The insulinTime1 to set.
	 */
	public void setInsulinTime1(String insulinTime1) {
		this.insulinTime1 = insulinTime1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulinTime2
	 */
	public String getInsulinTime2() {
		return insulinTime2;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulinTime2 The insulinTime2 to set.
	 */
	public void setInsulinTime2(String insulinTime2) {
		this.insulinTime2 = insulinTime2;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulinTime3
	 */
	public String getInsulinTime3() {
		return insulinTime3;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulinTime3 The insulinTime3 to set.
	 */
	public void setInsulinTime3(String insulinTime3) {
		this.insulinTime3 = insulinTime3;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulinTime4
	 */
	public String getInsulinTime4() {
		return insulinTime4;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulinTime4 The insulinTime4 to set.
	 */
	public void setInsulinTime4(String insulinTime4) {
		this.insulinTime4 = insulinTime4;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin1Dose1
	 */
	public String getInsulin1Dose1() {
		return insulin1Dose1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin1Dose1 The insulin1Dose1 to set.
	 */
	public void setInsulin1Dose1(String insulin1Dose1) {
		this.insulin1Dose1 = insulin1Dose1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin1Dose2
	 */
	public String getInsulin1Dose2() {
		return insulin1Dose2;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin1Dose2 The insulin1Dose2 to set.
	 */
	public void setInsulin1Dose2(String insulin1Dose2) {
		this.insulin1Dose2 = insulin1Dose2;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin1Dose3
	 */
	public String getInsulin1Dose3() {
		return insulin1Dose3;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin1Dose3 The insulin1Dose3 to set.
	 */
	public void setInsulin1Dose3(String insulin1Dose3) {
		this.insulin1Dose3 = insulin1Dose3;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin1Dose4
	 */
	public String getInsulin1Dose4() {
		return insulin1Dose4;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin1Dose4 The insulin1Dose4 to set.
	 */
	public void setInsulin1Dose4(String insulin1Dose4) {
		this.insulin1Dose4 = insulin1Dose4;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin2Dose1
	 */
	public String getInsulin2Dose1() {
		return insulin2Dose1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin2Dose1 The insulin2Dose1 to set.
	 */
	public void setInsulin2Dose1(String insulin2Dose1) {
		this.insulin2Dose1 = insulin2Dose1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin2Dose2
	 */
	public String getInsulin2Dose2() {
		return insulin2Dose2;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin2Dose2 The insulin2Dose2 to set.
	 */
	public void setInsulin2Dose2(String insulin2Dose2) {
		this.insulin2Dose2 = insulin2Dose2;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin2Dose3
	 */
	public String getInsulin2Dose3() {
		return insulin2Dose3;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin2Dose3 The insulin2Dose3 to set.
	 */
	public void setInsulin2Dose3(String insulin2Dose3) {
		this.insulin2Dose3 = insulin2Dose3;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin2Dose4
	 */
	public String getInsulin2Dose4() {
		return insulin2Dose4;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin2Dose4 The insulin2Dose4 to set.
	 */
	public void setInsulin2Dose4(String insulin2Dose4) {
		this.insulin2Dose4 = insulin2Dose4;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulinList
	 */
	public List<InsulinType> getInsulinList() {
		return insulinList;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulinList The insulinList to set.
	 */
	public void setInsulinList(List<InsulinType> insulinList) {
		this.insulinList = insulinList;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin1
	 */
	public InsulinType getInsulin1() {
		return insulin1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin1 The insulin1 to set.
	 */
	public void setInsulin1(InsulinType insulin1) {
		this.insulin1 = insulin1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin2
	 */
	public InsulinType getInsulin2() {
		return insulin2;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin2 The insulin2 to set.
	 */
	public void setInsulin2(InsulinType insulin2) {
		this.insulin2 = insulin2;
	}
	
	/**
	 * Returns the converted (Map<Date, Double>) meal intake data from simCurrent.
	 * 
	 * @return this.simCurrent.meals
	 */
	public Map<Date, Double> getMealsCurrent() {
		if (simCurrent == null)
			return null;
		else
			return simCurrent.getMeals();
	}
	
	/**
	 * Returns the converted (Map<Date, Double>) meal intake data from simPrevious.
	 * 
	 * @return this.simPrevious.meals
	 */
	public Map<Date, Double> getMealsPrevious() {
		if (simPrevious == null)
			return null;
		else
			return simPrevious.getMeals();
	}
	
	/**
	 * Returns the first InsulinType insulin injection data from simCurrent.
	 * 
	 * @return this.simCurrent.insulinInjection
	 */
	public Map<Date, Double> getInsulinInjections1() {
		return simCurrent.getInsulinInjections1();
	}
	
	/**
	 * Returns the second InsulinType insulin injection data from simCurrent.
	 * 
	 * @return this.simCurrent.insulinInjection
	 */
	public Map<Date, Double> getInsulinInjections2() {
		return simCurrent.getInsulinInjections2();
	}
	
	/**
	 * Returns the plasma glucose result data from simCurrent.
	 * 
	 * @return this.simCurrent.resultGlucoseCurrent
	 */
	public Map<Date, Double> getResultGlucoseCurrent() {
		if (simCurrent == null)
			return null;
		else
			return simCurrent.getResultGlucose();
	}
	
	/**
	 * Returns the plasma glucose result data from simPrevious.
	 * 
	 * @return this.simPrevious.resultGlucoseCurrent
	 */
	public Map<Date, Double> getResultGlucosePrevious() {
		if (simPrevious == null)
			return null;
		else
			return simPrevious.getResultGlucose();
	}
	
	/**
	 * Returns the plasma insulin result data from simCurrent.
	 * 
	 * @return this.simCurrent.resultInsulinCurrent
	 */
	public Map<Date, Double> getResultInsulinCurrent() {
		if (simCurrent == null)
			return null;
		else
			return simCurrent.getResultInsulin();
	}
	
	/**
	 * Returns the plasma insulin result data from simPrevious.
	 * 
	 * @return this.simPrevious.resultInsulinCurrent
	 */
	public Map<Date, Double> getResultInsulinPrevious() {
		if (simPrevious == null)
			return null;
		else
			return simPrevious.getResultInsulin();
	}
	
	/**
	 * Checks whether plasma glucose/insulin results are available for the current simulation run.
	 * 
	 * @return true if results Maps are not null and not empty; false otherwise.
	 */
	public Boolean getResultsAvailableCurrent() {
		return resultsAvailableCurrent;
	}
	
	/**
	 * Checks whether plasma glucose/insulin results are available for the previous simulation run.
	 * 
	 * @return true if results Maps are not null and not empty; false otherwise.
	 */
	public Boolean getResultsAvailablePrevious() {
		return resultsAvailablePrevious;
	}
	
	/**
	 * Runs a fresh simulation with current parameters. Saves previous simulation beforehand.
	 * 
	 * @throws ParseException In case of parsing errors.
	 * @should throw NullPointerException when insulin types or initial values not set
	 * @should contain simCurrent after first run
	 * @should contain current glucose result set after first run
	 * @should contain current insulin result set after first run
	 * @should contain simPrevious after second run
	 * @should contain previous glucose result set after second run
	 * @should contain previous insulin result set after second run
	 */
	public void runSimulation() throws ParseException {
		if (insulin1 == null || insulin2 == null)
			throw new NullPointerException("Insulin types must be set before running!");
		
		Map<Date, Double> meals = new HashMap<Date, Double>();
		Map<Date, Double> insulinInjections1 = new HashMap<Date, Double>();
		Map<Date, Double> insulinInjections2 = new HashMap<Date, Double>();
		Calendar now = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		
		// Convert meals to the model's format (Map<Date, Double>)
		if (mealTime1 != null && mealTime1.length() > 0 && mealCarbs1 != null && mealCarbs1.length() > 0) {
			cal.setTime((new SimpleDateFormat("HHmm")).parse(mealTime1));
			cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
			meals.put(cal.getTime(), Double.valueOf(mealCarbs1));
		}
		if (mealTime2 != null && mealTime2.length() > 0 && mealCarbs2 != null && mealCarbs2.length() > 0) {
			cal.setTime((new SimpleDateFormat("HHmm")).parse(mealTime2));
			cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
			meals.put(cal.getTime(), Double.valueOf(mealCarbs2));
		}
		if (mealTime3 != null && mealTime3.length() > 0 && mealCarbs3 != null && mealCarbs3.length() > 0) {
			cal.setTime((new SimpleDateFormat("HHmm")).parse(mealTime3));
			cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
			meals.put(cal.getTime(), Double.valueOf(mealCarbs3));
		}
		if (mealTime4 != null && mealTime4.length() > 0 && mealCarbs4 != null && mealCarbs4.length() > 0) {
			cal.setTime((new SimpleDateFormat("HHmm")).parse(mealTime4));
			cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
			meals.put(cal.getTime(), Double.valueOf(mealCarbs4));
		}
		if (mealTime5 != null && mealTime5.length() > 0 && mealCarbs5 != null && mealCarbs5.length() > 0) {
			cal.setTime((new SimpleDateFormat("HHmm")).parse(mealTime5));
			cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
			meals.put(cal.getTime(), Double.valueOf(mealCarbs5));
		}
		if (mealTime6 != null && mealTime6.length() > 0 && mealCarbs6 != null && mealCarbs6.length() > 0) {
			cal.setTime((new SimpleDateFormat("HHmm")).parse(mealTime6));
			cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
			meals.put(cal.getTime(), Double.valueOf(mealCarbs6));
		}
		
		// Convert insulin injections to the model's format (Map<Date, Double>)
		if (insulinTime1 != null && insulinTime1.length() > 0) {
			cal.setTime((new SimpleDateFormat("HHmm")).parse(insulinTime1));
			cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
			if (insulin1Dose1 != null && insulin1Dose1.length() > 0)
				insulinInjections1.put(cal.getTime(), Double.valueOf(insulin1Dose1));
			if (insulin2Dose1 != null && insulin2Dose1.length() > 0)
				insulinInjections2.put(cal.getTime(), Double.valueOf(insulin2Dose1));
		}
		if (insulinTime2 != null && insulinTime2.length() > 0) {
			cal.setTime((new SimpleDateFormat("HHmm")).parse(insulinTime2));
			cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
			if (insulin1Dose2 != null && insulin1Dose2.length() > 0)
				insulinInjections1.put(cal.getTime(), Double.valueOf(insulin1Dose2));
			if (insulin2Dose2 != null && insulin2Dose2.length() > 0)
				insulinInjections2.put(cal.getTime(), Double.valueOf(insulin2Dose2));
		}
		if (insulinTime3 != null && insulinTime3.length() > 0) {
			cal.setTime((new SimpleDateFormat("HHmm")).parse(insulinTime3));
			cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
			if (insulin1Dose3 != null && insulin1Dose3.length() > 0)
				insulinInjections1.put(cal.getTime(), Double.valueOf(insulin1Dose3));
			if (insulin2Dose3 != null && insulin2Dose3.length() > 0)
				insulinInjections2.put(cal.getTime(), Double.valueOf(insulin2Dose3));
			
		}
		if (insulinTime4 != null && insulinTime4.length() > 0) {
			cal.setTime((new SimpleDateFormat("HHmm")).parse(insulinTime4));
			cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
			if (insulin1Dose4 != null && insulin1Dose4.length() > 0)
				insulinInjections1.put(cal.getTime(), Double.valueOf(insulin1Dose4));
			if (insulin2Dose4 != null && insulin2Dose4.length() > 0)
				insulinInjections2.put(cal.getTime(), Double.valueOf(insulin2Dose4));
		}
		
		resultsAvailableCurrent = false;
		resultsAvailablePrevious = false;
		if (simCurrent != null)
			simPrevious = simCurrent;
		
		simCurrent = new SimulationModelAIDA(weight, RTG, CCR, sh, sp, insulin1.getParameterS(), insulin1.getParameterA(),
		        insulin1.getParameterB(), insulin2.getParameterS(), insulin2.getParameterA(), insulin2.getParameterB(),
		        meals, insulinInjections1, insulinInjections2, initialArterialGlucose);
		
		simCurrent.runSimulation();
		
		// If both glucose and insulin results are not empty, set the flag
		if (simCurrent != null && simCurrent.getResultGlucose() != null && simCurrent.getResultInsulin() != null
		        && !simCurrent.getResultGlucose().isEmpty() && !simCurrent.getResultInsulin().isEmpty()) {
			
			// Convert mmol/l results to mg/dl, if necessary
			if (conceptGlucose != null && ((ConceptNumeric) conceptGlucose).getUnits() != null) {
				if (((ConceptNumeric) conceptGlucose).getUnits().toLowerCase().equals("mg/dl"))
					simCurrent.convertToMgdl();
			}
			resultsAvailableCurrent = true;
			
		}
		if (simPrevious != null && simPrevious.getResultGlucose() != null && simPrevious.getResultInsulin() != null
		        && !simPrevious.getResultGlucose().isEmpty() && !simPrevious.getResultInsulin().isEmpty())
			resultsAvailablePrevious = true;
	}
}
