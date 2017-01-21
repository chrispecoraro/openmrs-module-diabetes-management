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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.diabetesmanagement.service.InsulinTypeService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests methods on the {@link org.openmrs.module.diabetesmanagement.Simulation} class.
 */
public class TestSimulation extends BaseModuleContextSensitiveTest {
	
	/**
	 * Should run a simulation successfully and produce correct result sets.
	 * 
	 * @throws Exception In case of errors.
	 */
	@SuppressWarnings("deprecation")
    @Test
	public void shouldRunSimulationSuccessfully() throws Exception {
		Simulation sim = new Simulation(80.0, 9.0, 100.0, 0.5, 0.5, 4.4);
		
		sim.setInsulinList(((InsulinTypeService) Context.getService(InsulinTypeService.class)).getAllInsulinTypes(false));
		assertEquals(4, sim.getInsulinList().size());
		sim.setInsulin1(sim.getInsulinList().get(0));
		sim.setInsulin2(sim.getInsulinList().get(1));
		
		sim.runSimulation();
		
		assertTrue(sim.getResultsAvailableCurrent());
		assertNotNull(sim.getResultGlucoseCurrent());
		assertNotNull(sim.getResultInsulinCurrent());
		assertEquals(97, sim.getResultInsulinCurrent().size());
		
		// Result sets should contain correct Dates
		for (Entry<Date, Double> e : sim.getResultGlucoseCurrent().entrySet()) {
			assertTrue(e.getKey().getSeconds() == 0);
			//assertEquals(cal.get(Calendar.YEAR), e.getKey().getYear());
			//assertEquals(cal.get(Calendar.MONTH), e.getKey().getMonth());
			//assertEquals(cal.get(Calendar.DAY_OF_MONTH), e.getKey().getDay());
			assertTrue(e.getKey().getMinutes() == 0 || e.getKey().getMinutes() == 15 || e.getKey().getMinutes() == 30
			        || e.getKey().getMinutes() == 45);
			//if(e.getKey().getHours() == 0 && e.getKey().getMinutes() == 0)
			//	assertEquals((Double) 10.0, e.getValue());
		}
	}
	
	/**
	 * Should run another simulation successfully.
	 * 
	 * @throws Exception In case of errors.
	 */
	@Test
	public void shouldRunAnotherSimulationSuccessfully() throws Exception {
		Simulation sim = new Simulation(80.0, 9.0, 100.0, 0.5, 0.5, 4.4);
		
		sim.setInsulinList(((InsulinTypeService) Context.getService(InsulinTypeService.class)).getAllInsulinTypes(false));
		sim.setInsulin1(sim.getInsulinList().get(0));
		sim.setInsulin2(sim.getInsulinList().get(1));
		
		sim.runSimulation();
		
		Map<Date, Double> previousGlu = sim.getResultGlucoseCurrent();
		Map<Date, Double> previousIns = sim.getResultInsulinCurrent();
		
		sim.runSimulation(); // again
		
		assertTrue(sim.getResultsAvailablePrevious());
		assertNotNull(sim.getResultGlucosePrevious());
		assertEquals(97, sim.getResultGlucosePrevious().size());
		assertEquals(previousGlu, sim.getResultGlucosePrevious());
		assertEquals(previousIns, sim.getResultInsulinPrevious());
	}
	
	/**
	 * Should properly convert passed times and carbohydrate values of meals to the model's format
	 * (Map<Date, Double>).
	 * 
	 * @throws Exception In case of errors.
	 */
	@Test
	public void shouldConvertMealsProperly() throws Exception {
		String[] mealTime = { "0800", "1030", "1200", "1900", "2200", "" };
		String[] mealCarbs = { "80", "20", "70", "60", "10", "" };
		Simulation sim = new Simulation(80.0, 9.0, 100.0, 0.5, 0.5, 4.4);
		
		sim.setInsulinList(((InsulinTypeService) Context.getService(InsulinTypeService.class)).getAllInsulinTypes(false));
		sim.setInsulin1(sim.getInsulinList().get(0));
		sim.setInsulin2(sim.getInsulinList().get(1));
		sim.setMealTime1(mealTime[0]);
		sim.setMealTime2(mealTime[1]);
		sim.setMealTime3(mealTime[2]);
		sim.setMealTime4(mealTime[3]);
		sim.setMealTime5(mealTime[4]);
		sim.setMealTime6(mealTime[5]);
		sim.setMealCarbs1(mealCarbs[0]);
		sim.setMealCarbs2(mealCarbs[1]);
		sim.setMealCarbs3(mealCarbs[2]);
		sim.setMealCarbs4(mealCarbs[3]);
		sim.setMealCarbs5(mealCarbs[4]);
		sim.setMealCarbs6(mealCarbs[5]);
		
		sim.runSimulation();
		
		assertNotNull(sim.getMealsCurrent());
		assertEquals(5, sim.getMealsCurrent().size());
		
		// Should contain all previously defined values
		for (int i = 0; i < mealCarbs.length; i++) {
			if (mealCarbs[i] != "")
				Assert.assertTrue(sim.getMealsCurrent().containsValue(Double.valueOf(mealCarbs[i])));
		}
	}
	
	/**
	 * Should properly convert passed times and unit values of insulin injections to the model's
	 * format (Map<Date, Double>).
	 * 
	 * @throws Exception In case of errors.
	 */
	@Test
	public void shouldConvertInjectionsProperly() throws Exception {
		String[] injectionTime = { "0830", "1230", "1930", "2200" };
		String[] injection1Dose = { "3", "5", "4", "" };
		String[] injection2Dose = { "12", "", "", "10" };
		Simulation sim = new Simulation(80.0, 9.0, 100.0, 0.5, 0.5, 4.4);
		
		sim.setInsulinList(((InsulinTypeService) Context.getService(InsulinTypeService.class)).getAllInsulinTypes(false));
		sim.setInsulin1(sim.getInsulinList().get(0));
		sim.setInsulin2(sim.getInsulinList().get(1));
		sim.setInsulinTime1(injectionTime[0]);
		sim.setInsulinTime2(injectionTime[1]);
		sim.setInsulinTime3(injectionTime[2]);
		sim.setInsulinTime4(injectionTime[3]);
		sim.setInsulin1Dose1(injection1Dose[0]);
		sim.setInsulin1Dose2(injection1Dose[1]);
		sim.setInsulin1Dose3(injection1Dose[2]);
		sim.setInsulin1Dose4(injection1Dose[3]);
		sim.setInsulin2Dose1(injection2Dose[0]);
		sim.setInsulin2Dose2(injection2Dose[1]);
		sim.setInsulin2Dose3(injection2Dose[2]);
		sim.setInsulin2Dose4(injection2Dose[3]);
		
		sim.runSimulation();
		
		assertNotNull(sim.getInsulinInjections1());
		assertEquals(3, sim.getInsulinInjections1().size());
		assertEquals(2, sim.getInsulinInjections2().size());
		
		// Should contain all previously defined values
		for (int i = 0; i < injection1Dose.length; i++) {
			if (injection1Dose[i] != "")
				assertTrue(sim.getInsulinInjections1().containsValue(Double.valueOf(injection1Dose[i])));
		}
		for (int i = 0; i < injection2Dose.length; i++) {
			if (injection2Dose[i] != "")
				assertTrue(sim.getInsulinInjections2().containsValue(Double.valueOf(injection2Dose[i])));
		}
	}
	
	/**
	 * Should write results to the files and retrieve exactly the same data afterwards.
	 * 
	 * @throws Exception In case of errors.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void shouldSerializeResultsSuccessfully() throws Exception {
		Simulation sim = new Simulation(80.0, 9.0, 100.0, 0.5, 0.5, 4.4);
		sim.setInitialActiveInsulin(10.0);
		
		sim.setInsulinList(((InsulinTypeService) Context.getService(InsulinTypeService.class)).getAllInsulinTypes(false));
		sim.setInsulin1(sim.getInsulinList().get(0));
		sim.setInsulin2(sim.getInsulinList().get(1));
		
		sim.runSimulation();
		
		File f = new File("glu_cur.bin");
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(sim.getResultGlucoseCurrent());
		out.close();
		fos.close();
		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream in = new ObjectInputStream(fis);
		Map<Date, Double> readGlu = (Map<Date, Double>) in.readObject();
		in.close();
		fis.close();
		
		f = new File("glu_ins.bin");
		fos = new FileOutputStream(f);
		out = new ObjectOutputStream(fos);
		out.writeObject(sim.getResultInsulinCurrent());
		out.close();
		fos.close();
		fis = new FileInputStream(f);
		in = new ObjectInputStream(fis);
		Map<Date, Double> readIns = (Map<Date, Double>) in.readObject();
		in.close();
		fis.close();
		
		assertEquals(sim.getResultGlucoseCurrent(), readGlu);
		assertEquals(sim.getResultInsulinCurrent(), readIns);
	}
}
