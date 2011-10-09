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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of the glucose-insulin metabolism simulation model described by Lehmann, Deutsch
 * (1992).
 */
public class SimulationModelAIDA {
	
	/** Log for this class and subclasses. */
	private final Log log = LogFactory.getLog(this.getClass());
	
	// Constants
	
	/** Slope of peripheral glucose utilisation vs insulin line. (mmol/hr/kg/mU^-1 * l) */
	private final Double c = 0.015;
	
	/** Insulin-independent glucose utilisation per kg body weight. (mmol/hr/kg) */
	private final Double GI = 0.54;
	
	/** Reference value for glucose utilisation. (mmol/l) */
	private final Double GX = 5.3;
	
	/** Reference basal level of insulin. (mU/l^-1) */
	private final Double Ibasal = 10.0;
	
	/** Insulin elimination rate constant. (l/hr^-1) */
	private final Double ke = 5.4;
	
	/** Parameter for insulin pharmacodynamics. (/hr^-1) */
	private final Double k1 = 0.025;
	
	/** Parameter for insulin pharmacodynamics. (/hr^-1) */
	private final Double k2 = 1.25;
	
	/** Rate constant for glucose absorption from the gut. (/hr^-1) */
	private final Double kgabs = 1.0;
	
	/** Michaelis constant for enzyme mediated glucose uptake. (mmol/l) */
	private final Double Km = 10.0;
	
	/** Volume of distribution for insulin per kg body weight. (l/kg^-1) */
	private final Double Vi = 0.142;
	
	/** Volume of distribution for glucose per kg body weight. (l/kg^-1) */
	private final Double Vg = 0.22;
	
	/** Maximal rate of gastric emptying. (mmol/hr) */
	private final Double Vmaxge = 120.0;
	
	/**
	 * Net hepatic glucose balance (mmol/hr) as a function of the arterial blood glucose level and
	 * plasma insulin level. Calculated from Guyton et al.
	 */
	private final Double[][] NHGBtable = { { 291.6, 160.0, 78.3 }, { 194.6, 114.6, 53.3 }, { 129.3, 66.0, -1.7 },
	        { 95.7, 46.3, -54.3 }, { 85.0, 22.6, -76.0 }, { 76.3, 4.3, -85.0 }, { 69.0, -10.0, -92.0 },
	        { 62.0, -25.3, -97.3 }, { 52.0, -43.3, -101.0 }, { 48.0, -47.3, -104.0 }, { 41.7, -49.3, -106.7 } };
	
	/** Step size for numerical solution of differential equations. (hr/step) */
	private final Double h = 1.0 / 60.0;
	
	/** Number of iterations per day, depending on 'h'. */
	private final Integer iterations = (int) (24.0 / h);
	
	/** Calendar increase step in minutes. */
	private final Integer calStep = (int) Math.round(60.0 * h);
	
	// Instance-specific parameters
	
	/** Patient's body weight. (kg) */
	private Double weight;
	
	/** Patient's hepatic insulin sensivity. */
	private Double sh;
	
	/** Patient's peripheral insulin sensivity. */
	private Double sp;
	
	/** Patient's renal threshold of glucose. (mmol/l) */
	private Double RTG;
	
	/** Patient's creatinine clearance rate (glomerular filtration). (ml/min) */
	private Double CCR;
	
	/** Insulin preparation-specific parameter. */
	private Double insulin1ParamS;
	
	/** Insulin preparation-specific parameter. */
	private Double insulin1ParamA;
	
	/** Insulin preparation-specific parameter. */
	private Double insulin1ParamB;
	
	/** Insulin preparation-specific parameter. */
	private Double insulin2ParamS;
	
	/** Insulin preparation-specific parameter. */
	private Double insulin2ParamA;
	
	/** Insulin preparation-specific parameter. */
	private Double insulin2ParamB;
	
	/** Time-stamped carbohydrate intake. (g) */
	private Map<Date, Double> meals;
	
	/** Time-stamped insulin injections (1). (U) */
	private Map<Date, Double> insulinInjections1;
	
	/** Time-stamped insulin injections (2). (U) */
	private Map<Date, Double> insulinInjections2;
	
	// Other
	
	/** Plasma glucose level. (mmol/l) */
	private Double G;
	
	/** Plasma insulin concentration. (mU/l) */
	private Double I;
	
	/** Arterial glucose level. (mmol/l) */
	private Double AG;
	
	/** Time-stamped plasma glucose level results. */
	private Map<Date, Double> resultGlucose;
	
	/** Time-stamped plasma insulin level results. */
	private Map<Date, Double> resultInsulin;
	
	// Constructors
	
	/**
	 * Constructor with all relevant simulation parameters.
	 * 
	 * @param weight Body weight in kg.
	 * @param RTG Renal threshold of glucose.
	 * @param CCR Creatinine clearance ratio.
	 * @param sh Hepatic insulin sensivity.
	 * @param sp Peripheral insulin sensivity.
	 * @param insulin1ParamS Insulin parameter S for the first insulin type.
	 * @param insulin1ParamA Insulin parameter A for the first insulin type.
	 * @param insulin1ParamB Insulin parameter B for the first insulin type.
	 * @param insulin2ParamS Insulin parameter S for the second insulin type.
	 * @param insulin2ParamA Insulin parameter A for the second insulin type.
	 * @param insulin2ParamB Insulin parameter B for the second insulin type.
	 * @param meals Times and carbohydrate amounts of meals.
	 * @param insulinInjections1 Injection times and doses if the first insulin type.
	 * @param insulinInjections2 Injection times and doses for the second insulin type.
	 * @param initialArterialGlucose Initial value for the patient's arterial glucose.
	 */
	public SimulationModelAIDA(Double weight, Double RTG, Double CCR, Double sh, Double sp, Double insulin1ParamS,
	    Double insulin1ParamA, Double insulin1ParamB, Double insulin2ParamS, Double insulin2ParamA, Double insulin2ParamB,
	    Map<Date, Double> meals, Map<Date, Double> insulinInjections1, Map<Date, Double> insulinInjections2,
	    Double initialArterialGlucose) {
		this.weight = weight;
		this.RTG = RTG;
		this.CCR = CCR;
		this.sh = sh;
		this.sp = sp;
		this.insulin1ParamS = insulin1ParamS;
		this.insulin1ParamA = insulin1ParamA;
		this.insulin1ParamB = insulin1ParamB;
		this.insulin2ParamS = insulin2ParamS;
		this.insulin2ParamA = insulin2ParamA;
		this.insulin2ParamB = insulin2ParamB;
		this.meals = meals;
		this.insulinInjections1 = insulinInjections1;
		this.insulinInjections2 = insulinInjections2;
		this.AG = initialArterialGlucose;
	}
	
	// Getters/setters
	
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
	 * @return this.meals
	 */
	public Map<Date, Double> getMeals() {
		return meals;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param meals The meals to set.
	 */
	public void setMeals(Map<Date, Double> meals) {
		this.meals = meals;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulinInjections1
	 */
	public Map<Date, Double> getInsulinInjections1() {
		return insulinInjections1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulinInjections1 The insulinInjections1 to set.
	 */
	public void setInsulinInjections1(Map<Date, Double> insulinInjections1) {
		this.insulinInjections1 = insulinInjections1;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulinInjections2
	 */
	public Map<Date, Double> getInsulinInjections2() {
		return insulinInjections2;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulinInjections2 The insulinInjections2 to set.
	 */
	public void setInsulinInjections2(Map<Date, Double> insulinInjections2) {
		this.insulinInjections2 = insulinInjections2;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin1ParamS
	 */
	public Double getInsulin1ParamS() {
		return insulin1ParamS;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin1ParamS The insulin1ParamS to set.
	 */
	public void setInsulin1ParamS(Double insulin1ParamS) {
		this.insulin1ParamS = insulin1ParamS;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin1ParamA
	 */
	public Double getInsulin1ParamA() {
		return insulin1ParamA;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin1ParamA The insulin1ParamA to set.
	 */
	public void getInsulin1ParamA(Double insulin1ParamA) {
		this.insulin1ParamA = insulin1ParamA;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin1ParamB
	 */
	public Double getInsulin1ParamB() {
		return insulin1ParamB;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin1ParamB The insulin1ParamB to set.
	 */
	public void setInsulin1ParamB(Double insulin1ParamB) {
		this.insulin1ParamB = insulin1ParamB;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin2ParamS
	 */
	public Double getInsulin2ParamS() {
		return insulin2ParamS;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin2ParamS The insulin2ParamS to set.
	 */
	public void setInsulin2ParamS(Double insulin2ParamS) {
		this.insulin2ParamS = insulin2ParamS;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin2ParamA
	 */
	public Double getInsulin2ParamA() {
		return insulin2ParamA;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin2ParamA The insulin2ParamA to set.
	 */
	public void setInsulin2ParamA(Double insulin2ParamA) {
		this.insulin2ParamA = insulin2ParamA;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.insulin2ParamB
	 */
	public Double getInsulin2ParamB() {
		return insulin2ParamB;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param insulin2ParamB The insulin2ParamB to set.
	 */
	public void setInsulin2ParamB(Double insulin2ParamB) {
		this.insulin2ParamB = insulin2ParamB;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.resultGlucose
	 */
	public Map<Date, Double> getResultGlucose() {
		return resultGlucose;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.resultInsulin
	 */
	public Map<Date, Double> getResultInsulin() {
		return resultInsulin;
	}
	
	/**
	 * Converts plasma glucose simulation result set to mg/dL.
	 */
	public void convertToMgdl() {
		for (Entry<Date, Double> e : resultGlucose.entrySet())
			e.setValue(e.getValue() * 18.0);
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.G
	 */
	public Double getG() {
		return G;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param g The g to set.
	 */
	public void setG(Double g) {
		G = g;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.I
	 */
	public Double getI() {
		return I;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param i The i to set.
	 */
	public void setI(Double i) {
		I = i;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @return this.AG
	 */
	public Double getAG() {
		return AG;
	}
	
	/**
	 * Getters/setters.
	 * 
	 * @param ag The ag to set.
	 */
	public void setAG(Double ag) {
		this.AG = ag;
	}
	
	/**
	 * Runs a 72-hour simulation in steps of size 'h' * hour (using the Euler method of solving
	 * differential equations numerically). Writes results of the third day run into the result
	 * variables (every 15 minutes).
	 */
	public void runSimulation() {
		if (AG == null || RTG == null || CCR == null || sh == null || sp == null || insulin1ParamS == null
		        || insulin1ParamA == null || insulin1ParamB == null || insulin2ParamS == null || insulin2ParamA == null
		        || insulin2ParamB == null)
			throw new NullPointerException("All patient and insulin parameters must be set before running!");
		
		// Some default values
		if (G == null)
			G = 0.0;
		if (I == null)
			I = 0.0;
		
		Integer Ie = (int) (sh * I / Ibasal); // effective insulin level
		Double Ia = 0.0; // active insulin pool
		Double Gin = 0.0; // glucose input via the gut wall
		Double Gout = 0.0; // overall rate of peripheral and insulin-independent glucose utilisation
		Double Ggut = 0.0; // amount of glucose in the gut
		Double Gempt = 0.0; // gastric emptying rate
		Double Gren = 0.0; // renal glucose excretion
		// Double Iss = 0.0; // steady state insulin profile
		Double Iass = 0.0; // steady state active insulin profile
		Double Iabs = 0.0, Iabs1, Iabs2; // insulin absorbtion rate
		Double Ieq = 0.0; // insulin level in equilibrium with Ia,ss(t)
		Double NHGB; // net hepatic insulin balance
		Double D1 = 0.0, D2 = 0.0; // insulin doses
		Double T50_1 = 0.0, T50_2 = 0.0; // time at which 50% of the insulin dose D1/D2 has been
		// absorbed
		Double Ch = 0.0; // (mmol) glucose equivalent carbohydrate
		Double Tmaxge = 0.0, Tascge = 0.5, Tdesge = 0.5; // durations of gastric emptying curve
		// branches
		Double t_meal_double, t_insulin_double; // Double conversions of t_meal, t_insulinshort,
		// t_insulinlong
		Integer t_meal = 0, t_insulin1 = 0, t_insulin2 = 0; // times elapsed from the last
		// meal/injection
		
		// Previous Ia values for Ieq calculation
		Double[] Ia24 = new Double[iterations];
		Double[] Ia48 = new Double[iterations];
		for (int i = 0; i < iterations; i++) {
			Ia24[i] = 0.0;
			Ia48[i] = 0.0;
		}
		
		// Initialize calendar and save the date
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0); // needed to be in sync with meals and injections
		Calendar ref = (Calendar) cal.clone(); // backup calendar for resetting
		
		// Reset result sets
		resultGlucose = new HashMap<Date, Double>();
		resultInsulin = new HashMap<Date, Double>();
		
		// Outer loop: Run a 3 day simulation to reach steady state, saving the results from the
		// third run
		for (int i = 0; i < 3; ++i) {
			cal.setTime(ref.getTime()); // reset calendar after each run
			
			// Inner loop: 24 hours in 24/h+1 steps
			for (int j = 0; j <= iterations; ++j) {
				// Look for an insulin injection at the given time
				// (4) T50^s = a * D + b
				if (insulinInjections1 != null && insulinInjections1.get(cal.getTime()) != null) {
					t_insulin1 = 0;
					D1 = insulinInjections1.get(cal.getTime());
					T50_1 = insulin1ParamA * D1 + insulin1ParamB;
				} else if (D1 > 0.0)
					t_insulin1++;
				if (insulinInjections2 != null && insulinInjections2.get(cal.getTime()) != null) {
					t_insulin2 = 0;
					D2 = insulinInjections2.get(cal.getTime());
					T50_2 = insulin2ParamA * D2 + insulin2ParamB;
				} else if (D2 > 0.0)
					t_insulin2++;
				
				// Save Ia values from the first two runs to the arrays
				if (i == 0)
					Ia48[i] = Ia;
				else if (i == 1)
					Ia24[i] = Ia;
				
				// (3) Iabs(t) = (s * t^s * T50^s * D) / (t * [T50^s + t^s]^2)
				Iabs1 = 0.0;
				Iabs2 = 0.0;
				if (t_insulin1 > 0) {
					t_insulin_double = t_insulin1 * h;
					Iabs1 = (insulin1ParamS * Math.pow(t_insulin_double, insulin1ParamS) * T50_1 * D1)
					        / (t_insulin_double * Math.pow(T50_1 + Math.pow(t_insulin_double, insulin1ParamS), 2.0));
				}
				if (t_insulin2 > 0) {
					t_insulin_double = t_insulin2 * h;
					Iabs2 = (insulin2ParamS * Math.pow(t_insulin_double, insulin2ParamS) * T50_2 * D2)
					        / (t_insulin_double * Math.pow(T50_2 + Math.pow(t_insulin_double, insulin2ParamS), 2.0));
				}
				Iabs = Iabs1 + Iabs2;
				
				// (5a) Iss(t) = I(t) + I(t + 24) + I(t + 48)
				// Iss = 3.0 * I;
				
				// (5b) Ia,ss(t) = Ia(t) + Ia(t + 24) + Ia(t + 48)
				switch (i) {
					case 0:
						Iass = 3.0 * Ia;
					case 1:
						Iass = 2.0 * Ia + Ia48[i];
					case 2:
						Iass = Ia + Ia24[i] + Ia48[i];
					default:
						Iass = 0.0;
				}
				
				// (6) Ieq(t) = k2 * Ia,ss(t) / k1
				Ieq = k2 * Iass / k1;
				
				// Setting NHGB from NHGBtable, based on the effective insulin (Ie) and arterial
				// glucose (AG) levels
				if (Ie >= 0) {
					if (AG <= 1.1)
						NHGB = NHGBtable[Ie][0];
					else if (AG >= 4.4)
						NHGB = NHGBtable[Ie][2];
					else
						NHGB = NHGBtable[Ie][1];
				} else
					NHGB = null;
				
				// Glucose utilization (Gout) by the central nervous system (insulin-independent)
				// (8) Gout(G,I#eq) = (G * (c * Sp * I#eq + GI) * (Km + GX)) / (GX * (Km + G))
				Gout = (G * ((c * weight) * sp * Ieq + (GI * weight)) * (Km + GX)) / (GX * (Km + G));
				
				// Look for a meal at the given time
				if (meals != null && meals.get(cal.getTime()) != null) {
					// Convert the carbs from g to mmol (mmol = g / 180 * 10^3, assuming the molar
					// weight of glucose is 180)
					Ch = meals.get(cal.getTime()) / 180.0 * 1000.0;
					t_meal = 0;
					
					// (11) Tascge = Tdesge = Ch / Vmaxge (when Ch <= Chcrit (~10g))*
					// * Original equation (11) (Tascge = Tdesge = 2 * Ch / Vmaxge) by Lehmann,
					// Deutsch has been corrected by Fernandez, Villasana
					// (12) Chcrit = ((Tascge + Tdesge) * Vmaxge) / 2
					Double Chcrit = ((Tascge + Tdesge) * Vmaxge) / 2.0;
					if (Ch <= Chcrit) {
						Tascge = Ch / Vmaxge;
						Tdesge = Tascge;
					} else {
						Tascge = 0.5;
						Tdesge = 0.5;
					}
					
					// Duration of Vmaxge (Tmaxge)
					// (10) Tmaxge = (Ch - 1/2 * Vmaxge * 2(Tascge + Tdesge) / Vmaxge
					if (Ch > Chcrit)
						Tmaxge = (Ch - (0.5 * Vmaxge) * (2.0 * (Tascge + Tdesge))) / Vmaxge;
					else
						Tmaxge = 0.0;
				} else if (Ch > 0.0)
					t_meal++;
				
				// (13) Gastric emptying (Gempt)
				t_meal_double = t_meal * h;
				if (t_meal_double < Tascge)
					Gempt = (Vmaxge / Tascge) * t_meal_double;
				else if (Tascge <= t_meal_double && t_meal_double <= Tascge + Tmaxge)
					Gempt = Vmaxge;
				else if (Tascge + Tmaxge <= t_meal_double && t_meal_double < Tascge + Tmaxge + Tdesge)
					Gempt = Vmaxge - (Vmaxge / Tdesge) * (t_meal_double - Tascge - Tmaxge);
				else
					Gempt = 0.0;
				
				// (9) d(Ggut)/dt = Gempt - kgabs * Ggut
				Ggut += h * (Gempt - kgabs * Ggut);
				if (Ggut < 0.0)
					Ggut = 0.0;
				
				// Glucose input (Gin) via the gut wall
				// (14) Gin = kgabs * Ggut
				Gin = kgabs * Ggut;
				
				// Renal glucose excretion (if above RTG)
				// (15a) Gren = CCR * (G - RTG)(if G > RTG)
				// (15b) Gren = 0 (else)
				// CCR: (ml/min) ==> CCR * 60 / 1000 ==> (l/h)
				if (G > RTG)
					Gren = (CCR * 60.0 / 1000.0) * (G - RTG);
				else
					Gren = 0.0;
				
				// Change in plasma glucose concentration for this time period
				// (7) dG/dt = (Gin(t) + NHGB(t) - Gout(t) - Gren(t)) / Vg
				G += h * ((Gin + NHGB - Gout - Gren) / (Vg * weight));
				if (G < 0.0)
					G = 0.0;
				
				// Change in plasma insulin concentration for this time period
				// (1) dI/dt = Iabs / Vi - ke * I
				I += h * ((Iabs * (Vi * weight)) - ke * I);
				if (I < 0.0)
					I = 0.0;
				
				// Build-up and deactivation of the 'active' insulin pool
				// (2) dIa/dt = (k1 * I) - (k2 * Ia)
				Ia += h * (k1 * I - k2 * Ia);
				
				// Effective insulin
				Ie = (int) (sh * I / Ibasal);
				if (Ie > 10)
					Ie = 10;
				
				// When reached a 15-min mark, add current G,I to result sets
				if ((cal.get(Calendar.MINUTE) == 00 || cal.get(Calendar.MINUTE) == 15 || cal.get(Calendar.MINUTE) == 30 || cal
				        .get(Calendar.MINUTE) == 45)
				        && i == 2) {
					resultGlucose.put(cal.getTime(), G);
					resultInsulin.put(cal.getTime(), I);
					
					if (log.isDebugEnabled()) {
						log.debug("(" + j + ") " + "G: " + G);
						log.debug("(" + j + ") " + "I/Ia/Ie: " + I + "/" + Ia + "/" + Ie);
						log.debug("(" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ") " + "Gout: "
						        + Gout);
						log.debug("(" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ") " + "Gren: "
						        + Gren);
						log.debug("(" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ") " + "NHGB: "
						        + NHGB);
						// log.debug("(" + cal.get(Calendar.HOUR_OF_DAY) + ":" +
						// cal.get(Calendar.MINUTE) + ") " + "meal (t/Ch): " + t_meal_double + "/" +
						// Ch);
						// log.debug("(" + cal.get(Calendar.HOUR_OF_DAY) + ":" +
						// cal.get(Calendar.MINUTE) + ") " + "Tascge/Tmaxge/Tdesge: " + Tascge + "/"
						// + Tmaxge + "/" + Tdesge);
						log.debug("(" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ") "
						        + "Gempt/Ggut/Gin: " + Gempt + "/" + Ggut + "/" + Gin);
						// log.debug("(" + cal.get(Calendar.HOUR_OF_DAY) + ":" +
						// cal.get(Calendar.MINUTE) + ") " + "h*Gempt: " + h * Gempt);
						// log.debug("(" + cal.get(Calendar.HOUR_OF_DAY) + ":" +
						// cal.get(Calendar.MINUTE) + ") " + "path: " + path);
						log.debug("(" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ") " + "Iass: "
						        + Iass);
						log.debug("(" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ") "
						        + "short (s/a/b/t/T50/D): " + insulin1ParamS + "/" + insulin1ParamA + "/" + insulin1ParamB
						        + "/" + t_insulin1 + "/" + T50_1 + "/" + D1);
						log.debug("(" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ") "
						        + "long (s/a/b/t/T50/D): " + insulin2ParamS + "/" + insulin2ParamA + "/" + insulin2ParamB
						        + "/" + t_insulin2 + "/" + T50_2 + "/" + D2);
						log.debug("(" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ") " + "Iabs: "
						        + Iabs);
						log.debug("(" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ") " + "Ieq: "
						        + Ieq);
						log.debug("");
					}
				}
				
				// Set 'cal' one step forward (fraction of an hour defined in 'calStep')
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal
				        .get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE) + calStep, 0);
			}
		}
	}
}
