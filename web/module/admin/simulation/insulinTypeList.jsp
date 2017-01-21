<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Insulin Types" otherwise="/login.htm" redirect="module/diabetesmanagement/admin/simulation/insulinType.list" />

<%@ include file="/WEB-INF/template/header.jsp" %>

<h2><spring:message code="diabetesmanagement.insulinType.manageInsulinTypes"/></h2>

<a href="insulinType.form"><spring:message code="diabetesmanagement.insulinType.addInsulinType"/></a>

<openmrs:extensionPoint pointId="org.openmrs.module.diabetesmanagement.admin.simulation.insulinTypeList.afterAdd" type="html" />

<br /><br />
<b class="boxHeader"><spring:message code="diabetesmanagement.insulinType.insulinTypeList"/></b>
<form method="post" class="box">
	<table cellspacing="0" cellpadding="2" width="100%">
		<tr>
			<th></th>
			<th><spring:message code="diabetesmanagement.insulinType.name" /></th>
			<th><spring:message code="diabetesmanagement.insulinType.concept" /></th>
			<th><spring:message code="diabetesmanagement.insulinType.parameterS" /></th>
			<th><spring:message code="diabetesmanagement.insulinType.parameterA" /></th>
			<th><spring:message code="diabetesmanagement.insulinType.parameterB" /></th>
		</tr>
		<c:forEach var="type" items="${insulinTypeList}">
			<tr>
				<td><input type="checkbox" name="insulinTypeId" value="${type.insulinTypeId}"></td>
				<td><a href="insulinType.form?insulinTypeId=${type.insulinTypeId}">${type.name}</a></td>
				<td>${type.concept.name}</td>
				<td>${type.parameterS}</td>
				<td>${type.parameterA}</td>
				<td>${type.parameterB}</td>
			</tr>
		</c:forEach>
	</table>
	<br /><input type="submit" value="<spring:message code="diabetesmanagement.insulinType.deleteSelected"/>" name="action">
</form>

<openmrs:extensionPoint pointId="org.openmrs.module.diabetesmanagement.admin.simulation.insulinTypeList.footer" type="html" />

<%@ include file="/WEB-INF/template/footer.jsp" %>
