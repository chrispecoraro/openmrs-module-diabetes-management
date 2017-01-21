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
package org.openmrs.module.diabetesmanagement.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.LinkExt;

/**
 * Adds a link to the 'DIASTOLIC BLOOD PRESSURE' obs entry page for patients.
 */
public class PatientObsDbpLinkExt extends LinkExt {
    
	/**
	 * @see org.openmrs.module.web.extension.LinkExt#getMediaType()
	 * @return The media type.
	 */
    public MEDIA_TYPE getMediaType() {
        return Extension.MEDIA_TYPE.html;
    }
    
	/**
	 * @see org.openmrs.module.web.extension.LinkExt#getUrl()
	 * @return The URL that this link should go to.
	 */
    public String getUrl() {
        return "module/diabetesmanagement/patientobs/patientObs.form?conceptName=dbp";
    }
    
    /**
     * @see org.openmrs.module.web.Extension.LinkExt#getLabel()
	 * @return The message code of the label of this link.
	 */
    public String getLabel() {
        return "diabetesmanagement.concept.dbp";
    }
    
	/**
	 * @see org.openmrs.module.web.extension.LinkExt#getRequiredPrivilege()
	 * @return The privilege the user must have to see this link.
	 */
    public String getRequiredPrivilege() {
        return "Add Own Observations";
    }
}
