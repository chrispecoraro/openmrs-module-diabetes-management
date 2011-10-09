<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Observations" otherwise="/login.htm" redirect="/module/diabetesmanagement/admin/graph.form" />

<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude file="/scripts/dojo/dojo.js" />

<script type="text/javascript">	
	dojo.require("dojo.widget.openmrs.ConceptSearch");
	dojo.require("dojo.widget.openmrs.OpenmrsPopup");
	
	dojo.addOnLoad( function() {
		
		searchWidget = dojo.widget.manager.getWidgetById("cSearch");			
		
		dojo.event.topic.subscribe("cSearch/select", 
			function(msg) {
				document.location="?patientId=${patient.patientId}&graphConcept=" + msg.objs[0].conceptId;
			}
		);
	});
</script>

<style>
table#labTestTable {
	border: 2px solid black;
	border-spacing: 0px;
	border-collapse: collapse;
	margin: 2px;
}

table#labTestTable td {
	border: 1px solid black;
	padding: 3px;
}

table#labTestTable th {
	border: 1px solid black;
	padding: 3px;
}
</style>

<openmrs:globalProperty key="diabetesmanagement.concept.glu" defaultValue="" var="glu" />
<openmrs:globalProperty key="diabetesmanagement.concept.hba1c" defaultValue="" var="hba1c" />
<openmrs:globalProperty key="diabetesmanagement.concept.sbp" defaultValue="" var="sbp" />
<openmrs:globalProperty key="diabetesmanagement.concept.dbp" defaultValue="" var="dbp" />
<openmrs:globalProperty key="concept.weight" defaultValue="" var="weight" />
<openmrs:globalProperty key="diabetesmanagement.concept.chol" defaultValue="" var="chol" />
<openmrs:globalProperty key="diabetesmanagement.concept.hdl" defaultValue="" var="hdl" />
<openmrs:globalProperty key="diabetesmanagement.concept.ldl" defaultValue="" var="ldl" />
<openmrs:globalProperty key="diabetesmanagement.concept.tg" defaultValue="" var="tg" />
<openmrs:globalProperty key="diabetesmanagement.concept.cr" defaultValue="" var="cr" />
<openmrs:globalProperty key="diabetesmanagement.concept.na" defaultValue="" var="na" />
<openmrs:globalProperty key="diabetesmanagement.concept.k" defaultValue="" var="k" />
<openmrs:userProperty key="graphConcepts" defaultValue="" var="userConcepts" />

<%
	if (request.getParameter("graphConcept") != null) {
		String userConcepts = (String)pageContext.getAttribute("userConcepts");
		if (request.getParameter("graphConceptRemove") != null) {
			String[] conceptList = userConcepts.split("-");
			userConcepts = "";
			for (String s : conceptList)
				if (!request.getParameter("graphConcept").equals(s) && !"".equals(s))
					userConcepts += "-" + s;
		}
		else {
			if (!userConcepts.contains(request.getParameter("graphConcept")))
				userConcepts += "-" + request.getParameter("graphConcept");
		}
		pageContext.setAttribute("userConcepts", userConcepts);
		org.openmrs.api.context.Context.getUserService().setUserProperty(org.openmrs.api.context.Context.getAuthenticatedUser(), "graphConcepts", userConcepts);
	}
%>

<h2>
	<img src="${pageContext.request.contextPath}/moduleResources/diabetesmanagement/images/Blue_circle_for_diabetes.gif" width="32" height="32" border="0" />
	<spring:message code="diabetesmanagement.title"/>: <spring:message code="diabetesmanagement.obs.graphs"/>
</h2>

<spring:hasBindErrors name="patient">
	<spring:message code="fix.error"/>
	<br />
</spring:hasBindErrors>

<c:set var="graphConceptStringDefault" value="${glu}-${hba1c}-${weight}-${chol}-${hdl}-${ldl}-${tg}-${cr}-${na}-${k}" />
<c:set var="graphConceptStringUser" value="${userConcepts}" />

<form method="get">
	<table width="100%">
		<tr>
			<td>
				<input type="hidden" name="patientId" value="${patient.patientId}">
				<input type="submit" id="updateButton" value='<spring:message code="diabetesmanagement.obs.graphsUpdate"/>'>
			</td>
			<th><spring:message code="general.fromDate"/></th>
			<td>		
				<input type="text" name="fromDate" size="10" onClick="showCalendar(this)" />
				(<spring:message code="general.format"/>: <openmrs:datePattern />) 
			</td>
			<th><spring:message code="general.toDate"/></th>
			<td>		
				<input type="text" name="toDate" size="10" onClick="showCalendar(this)" />
				(<spring:message code="general.format"/>: <openmrs:datePattern />)
			</td>
		</tr>
	</table>
</form>
<div class="boxHeader">${fromDate} - ${toDate}</div>
<div class="box$">
	<table width="100%">
		<tr>
			<td align="center" id="conceptBox-bp">
				<img src="${pageContext.request.contextPath}/moduleServlet/medicalproblem/showGraphNewServlet?patientId=${patient.patientId}&amp;conceptId=${sbp}&amp;conceptId2=${dbp}&amp;fromDate=${fromDate}&amp;toDate=${toDate}" />
			</td>
		</tr>
		<tr><td><br /></td></tr>
		<c:forEach items="${fn:split(graphConceptStringDefault, '-')}" var="conceptId">
			<c:if test="${conceptId != ''}">
				<tr>
					<td align="center" id="conceptBox-${conceptId}">
						<spring:message code="general.loading"/>
					</td>
				</tr>
				<tr><td><br /></td></tr>
			</c:if>
		</c:forEach>
		<c:forEach items="${fn:split(graphConceptStringUser, '-')}" var="conceptId">
			<c:if test="${conceptId != ''}">
				<tr>
					<td align="center" id="conceptBox-${conceptId}">
						<spring:message code="general.loading"/>
					</td>
				</tr>
				<tr>
					<td align="center" valign="top" style="font-size: .9em">
						<a href="?patientId=${patient.patientId}&graphConceptRemove=true&amp;graphConcept=${conceptId}"><spring:message code="general.remove"/></a>
						<br/><br/>
					</td>
				</tr>
			</c:if>
		</c:forEach>
		<tr>
			<td>
				<form>
					<spring:message code="patientGraphs.addNewGraph" />
					<span dojoType="ConceptSearch" widgetId="cSearch" includeDatatypes="Numeric"></span>
					<span dojoType="OpenmrsPopup" searchWidget="cSearch" searchTitle='<spring:message code="Concept.find"/>' changeButtonValue='<spring:message code="general.choose"/>'></span> 
					<br/>
				</form>
			</td>
		</tr>
	</table>
</div>

<script type="text/javascript">
	function loadGraphs() {
		<c:forEach items="${fn:split(graphConceptStringDefault, '-')}" var="conceptId">
			<c:if test="${conceptId != ''}">
				<openmrs:concept conceptId="${conceptId}" var="concept" nameVar="name" numericVar="num">
					document.getElementById('conceptBox-${conceptId}').innerHTML = '<img src="${pageContext.request.contextPath}/moduleServlet/medicalproblem/showGraphNewServlet?patientId=${patient.patientId}&amp;conceptId=${conceptId}&amp;fromDate=${fromDate}&amp;toDate=${toDate}" />';
				</openmrs:concept>
			</c:if>
		</c:forEach>
	}
	window.setTimeout(loadGraphs, 1000);		
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>