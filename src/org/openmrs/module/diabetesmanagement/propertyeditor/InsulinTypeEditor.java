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
package org.openmrs.module.diabetesmanagement.propertyeditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.diabetesmanagement.InsulinType;
import org.openmrs.module.diabetesmanagement.service.InsulinTypeService;
import org.springframework.util.StringUtils;

/**
 * Property editor for {@link org.openmrs.module.diabetesmanagement.InsulinType}.
 */
public class InsulinTypeEditor extends PropertyEditorSupport {
	
	/** Log for this class and subclasses. */
	private final Log log = LogFactory.getLog(this.getClass());
	
	/** Default constructor. */
	public InsulinTypeEditor() {
	}
	
	/**
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 * @param text The string to be parsed.
	 */
	public void setAsText(String text) {
		InsulinTypeService its = (InsulinTypeService) Context.getService(InsulinTypeService.class);
		if (StringUtils.hasText(text)) {
			try {
				setValue(its.getInsulinType(Integer.valueOf(text)));
			}
			catch (Exception ex) {
				log.error("Error setting text: " + text, ex);
				throw new IllegalArgumentException("Department not found: " + ex.getMessage());
			}
		} else {
			setValue(null);
		}
	}
	
	/**
	 * @see java.beans.PropertyEditorSupport#getAsText()
	 * @return The property value as a string suitable for presentation to a human to edit. Returns
	 *         "null" is the value can't be expressed as a string. If a non-null value is returned,
	 *         then the PropertyEditor should be prepared to parse that string back in setAsText().
	 */
	public String getAsText() {
		InsulinType t = (InsulinType) getValue();
		if (t == null && Context.isAuthenticated()) {
			return null;
		} else {
			return t.getName().toString();
		}
	}
	
}
