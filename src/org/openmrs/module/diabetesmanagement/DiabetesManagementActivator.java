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

import java.io.File;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.Activator;
import org.openmrs.util.OpenmrsUtil;

/**
 * This class contains the logic that is run every time this module is either started or shutdown.
 */
public class DiabetesManagementActivator implements Activator {
	
	/** Log for this class and subclasses. */
	private final Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {
		// log.info("Starting Diabetes Management module");
		
		// Cleaning up old (>30min) serialization data from simulation runs
		Date now = new Date();
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory("diabetesmanagement/simulation");
		for (File f : dir.listFiles()) {
			if (now.getTime() - f.lastModified() > 1800000) {
				// log.info("Deleting old simulation result file: " + f.getName());
				f.delete();
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down Diabetes Management module");
	}
	
}
