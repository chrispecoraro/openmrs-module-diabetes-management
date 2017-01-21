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
import org.openmrs.module.web.extension.PortletExt;

/**
 * Adds a new box to problem.form.
 */
public class ProblemFormLinksExt extends PortletExt {
    
	/**
	 * @see org.openmrs.module.web.extension.PortletExt#getMediaType()
	 * @return The media type.
	 */
    public MEDIA_TYPE getMediaType() {
        return Extension.MEDIA_TYPE.html;
    }
    
    /**
	 * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getPortletUrl()
	 * @return The URL that this link should go to.
	 */
    public String getPortletUrl() {
        return "problemFormLinks";
    }

    /**
     * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getPortletParameters()
	 * @return The portlet parameters.
	 */
    public String getPortletParameters() {
        return "";
    }
}
