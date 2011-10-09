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
package org.openmrs.module.diabetesmanagement.service;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.diabetesmanagement.InsulinType;
import org.springframework.transaction.annotation.Transactional;

/**
 * InsulinType-related services.
 */
@Transactional
public interface InsulinTypeService {
	
	/**
	 * Saves InsulinType.
	 * 
	 * @param insulinType The InsulinType to save.
	 * @return The saved InsulinType.
	 */
	@Authorized( { "Manage Insulin Types" })
	public InsulinType saveInsulinType(InsulinType insulinType);
	
	/**
	 * Gets InsulinType by ID.
	 * 
	 * @param insulinTypeId The ID of the InsulinType to get
	 * @return InsulinType with the given ID.
	 */
	@Transactional(readOnly = true)
	public InsulinType getInsulinType(Integer insulinTypeId);
	
	/**
	 * Gets all InsulinTypes.
	 * 
	 * @param includeRetired Include retired InsulinTypes.
	 * @return All InsulinTypes in the database.
	 */
	@Transactional(readOnly = true)
	public List<InsulinType> getAllInsulinTypes(Boolean includeRetired);
	
	/**
	 * Gets InsulinType that matches the given name string.
	 * 
	 * @param name The name of the InsulinType to get.
	 * @return InsulinType with the given name.
	 */
	@Transactional(readOnly = true)
	public InsulinType getInsulinTypeByName(String name);
	
	/**
	 * Get InsulinType that has the given Concept mapped to it.
	 * 
	 * @param concept The Concept of the InsulinType to get.
	 * @return InsulinType with the given Concept.
	 */
	@Transactional(readOnly = true)
	public InsulinType getInsulinTypeByConcept(Concept concept);
	
	/**
	 * Purges InsulinType.
	 * 
	 * @param insulinType The InsulinType to purge.
	 */
	@Authorized( { "Manage Insulin Types" })
	public void purgeInsulinType(InsulinType insulinType);
	
	/**
	 * Retires InsulinType.
	 * 
	 * @param insulinType The InsulinType to retire.
	 * @param reason Reason for retiring.
	 * @return The retired InsulinType.
	 */
	@Authorized( { "Manage Insulin Types" })
	public InsulinType retireInsulinType(InsulinType insulinType, String reason);
	
	/**
	 * Unretire InsulinType.
	 * 
	 * @param insulinType The InsulinType to unretire.
	 * @return The unretired InsulinType.
	 */
	@Authorized( { "Manage Insulin Types" })
	public InsulinType unretireInsulinType(InsulinType insulinType);
	
}
