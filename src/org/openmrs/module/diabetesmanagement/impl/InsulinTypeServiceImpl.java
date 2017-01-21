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
package org.openmrs.module.diabetesmanagement.impl;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.diabetesmanagement.InsulinType;
import org.openmrs.module.diabetesmanagement.db.InsulinTypeDAO;
import org.openmrs.module.diabetesmanagement.service.InsulinTypeService;
import org.springframework.transaction.annotation.Transactional;

/**
 * InsulinType-related services (implementation).
 */
@Transactional
public class InsulinTypeServiceImpl implements InsulinTypeService {
	
	/** DAO object for this class. */
	private InsulinTypeDAO dao;
	
	/** Default constructor. */
	public InsulinTypeServiceImpl() {
	}
	
	/**
	 * Sets the UserMapDAO.
	 * 
	 * @param dao the InsulinTypeDAO to set.
	 */
	public void setInsulinTypeDAO(InsulinTypeDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#saveInsulinType(org.openmrs.module.diabetesmanagement.InsulinType)
	 * @param insulinType The InsulinType to save.
	 * @return The saved InsulinType.
	 */
	public InsulinType saveInsulinType(InsulinType insulinType) {
		Date now = new Date();
		User me = Context.getAuthenticatedUser();
		
		if (insulinType.getCreator() == null)
			insulinType.setCreator(me);
		if (insulinType.getDateCreated() == null)
			insulinType.setDateCreated(now);
		
		if (insulinType.getConcept() != null && !insulinType.getConcept().getConceptClass().getName().equals("Drug"))
			throw new APIException("Only concepts of the class 'Drug' are allowed!");
		
		return dao.saveInsulinType(insulinType);
	}
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#getInsulinType(java.lang.Integer)
	 * @param insulinTypeId The ID of the InsulinType to get.
	 * @return InsulinType with the given ID.
	 */
	public InsulinType getInsulinType(Integer insulinTypeId) {
		return dao.getInsulinType(insulinTypeId);
	}
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#getAllInsulinTypes(java.lang.Boolean)
	 * @param includeRetired Include retired InsulinTypes.
	 * @return All InsulinTypes in the database.
	 */
	public List<InsulinType> getAllInsulinTypes(Boolean includeRetired) {
		return dao.getInsulinTypes(null, null, includeRetired);
	}
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#getInsulinTypeByName(java.lang.String)
	 * @param name The name of the InsulinType to get.
	 * @return InsulinType with the given name.
	 */
	public InsulinType getInsulinTypeByName(String name) {
		List<InsulinType> result = dao.getInsulinTypes(name, null, false);
		if (result != null)
			return result.get(0);
		else
			return null;
	}
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#getInsulinTypeByConcept(org.openmrs.Concept)
	 * @param concept The Concept of the InsulinType to get.
	 * @return InsulinType with the given Concept.
	 */
	public InsulinType getInsulinTypeByConcept(Concept concept) {
		List<InsulinType> result = dao.getInsulinTypes(null, concept, false);
		if (result != null)
			return result.get(0);
		else
			return null;
	}
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#purgeInsulinType(org.openmrs.module.diabetesmanagement.InsulinType)
	 * @param insulinType The InsulinType to purge.
	 */
	public void purgeInsulinType(InsulinType insulinType) {
		dao.purgeInsulinType(insulinType);
	}
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#retireInsulinType(org.openmrs.module.diabetesmanagement.InsulinType,
	 *      java.lang.String)
	 * @param insulinType The InsulinType to retire.
	 * @param reason Reason for retiring.
	 * @return The retired InsulinType.
	 */
	public InsulinType retireInsulinType(InsulinType insulinType, String reason) {
		if (reason == null || reason.length() == 0)
			throw new APIException("Reason is required");
		insulinType.setRetired(true);
		insulinType.setRetiredBy(Context.getAuthenticatedUser());
		insulinType.setRetireReason(reason);
		
		return dao.saveInsulinType(insulinType);
	}
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#unretireInsulinType(org.openmrs.module.diabetesmanagement.InsulinType)
	 * @param insulinType The InsulinType to unretire.
	 * @return The unretired InsulinType.
	 */
	public InsulinType unretireInsulinType(InsulinType insulinType) {
		insulinType.setRetired(false);
		insulinType.setRetiredBy(null);
		insulinType.setRetireReason(null);
		
		return dao.saveInsulinType(insulinType);
	}
}
