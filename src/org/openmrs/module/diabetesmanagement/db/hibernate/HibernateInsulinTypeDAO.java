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
package org.openmrs.module.diabetesmanagement.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.openmrs.Concept;
import org.openmrs.module.diabetesmanagement.InsulinType;
import org.openmrs.module.diabetesmanagement.db.InsulinTypeDAO;
import org.openmrs.module.diabetesmanagement.service.InsulinTypeService;

/**
 * Hibernate-specific DAO for the {@link InsulinTypeService}. All calls should be made on the
 * Context.getService(InsulinTypeService.class) object.
 * 
 * @see InsulinTypeDAO
 * @see InsulinTypeService
 */
public class HibernateInsulinTypeDAO implements InsulinTypeDAO {
	
	/** Log for this class and subclasses. */
	private final Log log = LogFactory.getLog(getClass());
	
	/** Hibernate session factory. */
	private SessionFactory sessionFactory;
	
	/** Default constructor. */
	public HibernateInsulinTypeDAO() {
	}
	
	/**
	 * Sets session factory.
	 * 
	 * @param sessionFactory The sessionFactory to set.
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#saveInsulinType(org.openmrs.module.diabetesmanagement.InsulinType)
	 * @param insulinType The InsulinType to save.
	 * @return The saved InsulinType.
	 */
	public InsulinType saveInsulinType(InsulinType insulinType) {
		log.debug("Saving InsulinType: " + insulinType.getInsulinTypeId());
		sessionFactory.getCurrentSession().saveOrUpdate(insulinType);
		
		return insulinType;
	}
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#getInsulinType(java.lang.Integer)
	 * @param insulinTypeId The ID of the InsulinType to get.
	 * @return InsulinType with the given ID.
	 */
	public InsulinType getInsulinType(Integer insulinTypeId) {
		return (InsulinType) sessionFactory.getCurrentSession().get(InsulinType.class, insulinTypeId);
	}
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.db.InsulinTypeDAO#getInsulinTypes(java.lang.String,
	 *      org.openmrs.Concept, java.lang.Boolean)
	 * @param name The name.
	 * @param concept The Concept.
	 * @param includeRetired Include retired InsulinTypes.
	 * @return All InsulinTypes that match the given criteria.
	 */
	@SuppressWarnings("unchecked")
	public List<InsulinType> getInsulinTypes(String name, Concept concept, Boolean includeRetired) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(InsulinType.class);
		if (!includeRetired)
			crit.add(Expression.eq("retired", false));
		if (name != null && name.length() > 0)
			crit.add(Expression.like("name", name, MatchMode.START));
		if (concept != null)
			crit.add(Expression.eq("concept", concept));
		crit.addOrder(Order.asc("insulinTypeId"));
		
		return crit.list();
	}
	
	/**
	 * @see org.openmrs.module.diabetesmanagement.service.InsulinTypeService#purgeInsulinType(org.openmrs.module.diabetesmanagement.InsulinType)
	 * @param insulinType The InsulinType to purge.
	 */
	public void purgeInsulinType(InsulinType insulinType) {
		log.debug("Purging InsulinType: " + insulinType.getInsulinTypeId());
		sessionFactory.getCurrentSession().delete(insulinType);
	}
}
