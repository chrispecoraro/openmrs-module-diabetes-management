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
package org.openmrs.module.diabetesmanagement.db;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.diabetesmanagement.InsulinType;

/**
 * InsulinType-related database functions.
 */
public interface InsulinTypeDAO {
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#saveInsulinType(org.openmrs.module.diabetesmanagement.InsulinType)
	 * @param insulinType The InsulinType to save.
	 * @return The saved InsulinType.
	 */
	public InsulinType saveInsulinType(InsulinType insulinType);
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#getInsulinType(java.lang.Integer)
	 * @param insulinTypeId The ID of the InsulinType to get.
	 * @return InsulinType with the given ID.
	 */
	public InsulinType getInsulinType(Integer insulinTypeId);
	
	/**
	 * Generic getter with all parameters.
	 * 
	 * @param name The name.
	 * @param concept The Concept.
	 * @param includeRetired Include retired InsulinTypes.
	 * @return All InsulinTypes that match the given criteria.
	 */
	public List<InsulinType> getInsulinTypes(String name, Concept concept, Boolean includeRetired);
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#purgeInsulinType(org.openmrs.module.diabetesmanagement.InsulinType)
	 * @param insulinType The InsulinType to purge.
	 */
	public void purgeInsulinType(InsulinType insulinType);
}
