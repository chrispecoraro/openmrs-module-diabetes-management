<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Global Properties" otherwise="/login.htm" redirect="/module/diabetesmanagement/admin/config.form" />

<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/scripts/dojo/dojo.js" />

<script type="text/javascript">
	dojo.require("dojo.widget.openmrs.ConceptSearch");
	dojo.require("dojo.widget.openmrs.OpenmrsPopup");

	var conceptSearch;
	var conceptSelection;
	
	dojo.addOnLoad( function() {
		conceptSelection = dojo.widget.manager.getWidgetById("conceptSelection");
		conceptSearch = dojo.widget.manager.getWidgetById("cSearch");

		dojo.event.topic.subscribe("cSearch/select", 
			function(msg) {
				var conceptSelection = dojo.widget.manager.getWidgetById("conceptSelection");
				conceptSelection.hiddenInputNode.value = msg.objs[0].conceptNameId;
				conceptSelection.displayNode.innerHTML = msg.objs[0].name;
				conceptSelection.descriptionDisplayNode.innerHTML = msg.objs[0].description;
				updateObsValues(msg.objs[0]);
			}
		);
	});
	
</script>

<h2>
	<img src="${pageContext.request.contextPath}/moduleResources/diabetesmanagement/images/Blue_circle_for_diabetes.gif" width="32" height="32" border="0" />
	<spring:message code="diabetesmanagement.title"/>: <spring:message code="diabetesmanagement.config.configureConcepts"/>
</h2>

<openmrs:extensionPoint pointId="org.openmrs.module.diabetesmanagement.admin.diabetesmanagementConfigForm.afterTitle" type="html" parameters="departmentId=${department.departmentId}" />

<spring:hasBindErrors name="config">
	<spring:message code="fix.error"/>
	<br />
</spring:hasBindErrors>

<b class="boxHeader"><spring:message code=""/></b>
<div class="box">
<form method="post">
<table cellpadding="3" cellspacing="0">
    <tr><th><spring:message code="diabetesmanagement.concept.headerDiagnoses"/></th>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.t1dm"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[t1dm]">
					<openmrs_tag:conceptField formFieldName="t1dm" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.t2dm"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[t2dm]">
					<openmrs_tag:conceptField formFieldName="t2dm" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr><td><br /><br /></td></tr>
	<tr><th><spring:message code="diabetesmanagement.concept.headerPhysical"/></th>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.sbp"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[sbp]">
					<openmrs_tag:conceptField formFieldName="sbp" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.dbp"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[dbp]">
					<openmrs_tag:conceptField formFieldName="dbp" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr><td><br /><br /></td></tr>
	<tr><th><spring:message code="diabetesmanagement.concept.headerLipids"/></th>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.glu"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts['glu']">
					<openmrs_tag:conceptField formFieldName="glu" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.hba1c"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[hba1c]">
					<openmrs_tag:conceptField formFieldName="hba1c" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.chol"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[chol]">
					<openmrs_tag:conceptField formFieldName="chol" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.hdl"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[hdl]">
					<openmrs_tag:conceptField formFieldName="hdl" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.ldl"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[ldl]">
					<openmrs_tag:conceptField formFieldName="ldl" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.tg"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[tg]">
					<openmrs_tag:conceptField formFieldName="tg" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.cr"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[cr]">
					<openmrs_tag:conceptField formFieldName="cr" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.na"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[na]">
					<openmrs_tag:conceptField formFieldName="na" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.k"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[k]">
					<openmrs_tag:conceptField formFieldName="k" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr><td><br /><br /></td></tr>
	<tr><th><spring:message code="diabetesmanagement.concept.headerSimulationParameters"/></th>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.rtg"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[rtg]">
					<openmrs_tag:conceptField formFieldName="rtg" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.ccr"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[ccr]">
					<openmrs_tag:conceptField formFieldName="ccr" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.sh"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[sh]">
					<openmrs_tag:conceptField formFieldName="sh" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.concept.sp"/></th>
		<td colspan="5">
			<spring:bind path="config.relevantConcepts[sp]">
					<openmrs_tag:conceptField formFieldName="sp" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
</table>
<br /><br /><a href="${pageContext.request.contextPath}/dictionary/concept.form"><spring:message code="Concept.add"/></a> (Use sparingly)
</div>

<openmrs:extensionPoint pointId="org.openmrs.module.diabetesmanagement.admin.diabetesmanagementConfigForm.inForm" type="html" parameters="" />

<br />
<input type="submit" value="<spring:message code="general.save"/>">
</form>

<openmrs:extensionPoint pointId="org.openmrs.module.diabetesmanagement.admin.diabetesmanagementConfigForm.footer" type="html" parameters="" />

<%@ include file="/WEB-INF/template/footer.jsp" %>