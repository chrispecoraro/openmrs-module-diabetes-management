<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:htmlInclude file="/scripts/easyAjax.js" />

<openmrs:hasPrivilege privilege="View Problems">
	<c:if test="${model.isDiabetesProblem}">
		<div id="diabetesmanagementProblemFormLinks">
			<br />
			<b class="boxHeader"><spring:message code="diabetesmanagement.title" /></b>
			<div class="box">
				<br /><a href="${pageContext.request.contextPath}/module/diabetesmanagement/admin/graph.form?patientId=${model.problem.patient.patientId}"><spring:message code="diabetesmanagement.obs.graphs" /></a>
				<br /><a href="${pageContext.request.contextPath}/module/diabetesmanagement/admin/simulation/simulation.form?patientId=${model.problem.patient.patientId}"><spring:message code="diabetesmanagement.simulation.simulation" /></a>
			</div>
			<openmrs:htmlInclude file="/dwr/engine.js" />
			<openmrs:htmlInclude file="/dwr/util.js" />
		</div>
	</c:if>
</openmrs:hasPrivilege>

