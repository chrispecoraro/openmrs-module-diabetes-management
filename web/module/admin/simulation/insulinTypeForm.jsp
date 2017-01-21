<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Insulin Types" otherwise="/login.htm" redirect="/module/diabetesmanagement/admin/simulations/insulinType.form" />

<%@ include file="/WEB-INF/template/header.jsp" %>

<h2><spring:message code="diabetesmanagement.insulinType.manageInsulinTypes"/></h2>

<openmrs:extensionPoint pointId="org.openmrs.module.diabetesmanagement.admin.simulation.insulinTypeForm.afterTitle" type="html" parameters="insulinTypeId=${type.insulinTypeId}" />

<spring:hasBindErrors name="type">
	<spring:message code="fix.error"/><br />
</spring:hasBindErrors>

<b class="boxHeader"><spring:message code="diabetesmanagement.insulinType.insulinType"/></b>
<div class="box">
<form method="post">
<table cellpadding="3" cellspacing="0">
	<tr>
		<th><spring:message code="diabetesmanagement.insulinType.name"/></th>
		<td colspan="5">
			<spring:bind path="type.name">
				<input type="text" name="name" value="${status.value}" size="15" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.insulinType.concept"/></th>
		<td colspan="5">
			<spring:bind path="type.concept">
				<openmrs_tag:conceptField formFieldName="concept" initialValue="${status.value}" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
		<td class="desription"><spring:message code="diabetesmanagement.help.insulinType.concept"/></td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.insulinType.parameterS"/></th>
		<td colspan="5">
			<spring:bind path="type.parameterS">
				<input type="text" name="parameterS" value="${status.value}" size="15" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
		<td class="desription"><spring:message code="diabetesmanagement.help.insulinType.parameterS"/></td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.insulinType.parameterA"/></th>
		<td colspan="5">
			<spring:bind path="type.parameterA">
				<input type="text" name="parameterA" value="${status.value}" size="15" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
		<td class="desription"><spring:message code="diabetesmanagement.help.insulinType.parameterAB"/></td>
	</tr>
	<tr>
		<th><spring:message code="diabetesmanagement.insulinType.parameterB"/></th>
		<td colspan="5">
			<spring:bind path="type.parameterB">
				<input type="text" name="parameterB" value="${status.value}" size="15" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
		<td class="desription"><spring:message code="diabetesmanagement.help.insulinType.parameterAB"/></td>
	</tr>
	<c:if test="${type.creator != null}">
		<tr>
			<th><spring:message code="general.createdBy" /></th>
			<td>${type.creator.personName} - <openmrs:formatDate date="${type.dateCreated}" type="long" /></td>
		</tr>
	</c:if>
</table>
</div>

<openmrs:extensionPoint pointId="org.openmrs.module.diabetesmanagement.admin.simulation.insulinTypeForm.inForm" type="html" parameters="insulinTypeId=${type.insulinTypeId}" />

<br />
<input type="submit" value="<spring:message code="general.save"/>">
</form>

<openmrs:extensionPoint pointId="org.openmrs.module.diabetesmanagement.admin.simulation.insulinTypeForm.footer" type="html" parameters="insulinTypeId=${type.insulinTypeId}" />

<%@ include file="/WEB-INF/template/footer.jsp" %>