<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:htmlInclude file="/scripts/easyAjax.js" />

<openmrs:require privilege="Run Diabetes Simulations" otherwise="/login.htm" redirect="/module/diabetesmanagement/admin/simulation.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:globalProperty key="diabetesmanagement.concept.glu" defaultValue="" var="glu" />
<openmrs:globalProperty key="diabetesmanagement.simulation.rtgLow" defaultValue="" var="rtgLow" />
<openmrs:globalProperty key="diabetesmanagement.simulation.rtgNormal" defaultValue="" var="rtgNormal" />
<openmrs:globalProperty key="diabetesmanagement.simulation.rtgHigh" defaultValue="" var="rtgHigh" />
<openmrs:globalProperty key="diabetesmanagement.simulation.ccrReduced" defaultValue="" var="ccrReduced" />
<openmrs:globalProperty key="diabetesmanagement.simulation.ccrNormal" defaultValue="" var="ccrNormal" />
<openmrs:globalProperty key="diabetesmanagement.simulation.insulinSensivityHepaticReduced" defaultValue="" var="insulinSensivityHepaticReduced" />
<openmrs:globalProperty key="diabetesmanagement.simulation.insulinSensivityHepaticNormal" defaultValue="" var="insulinSensivityHepaticNormal" />
<openmrs:globalProperty key="diabetesmanagement.simulation.insulinSensivityHepaticIncreased" defaultValue="" var="insulinSensivityHepaticIncreased" />
<openmrs:globalProperty key="diabetesmanagement.simulation.insulinSensivityPeripheralReduced" defaultValue="" var="insulinSensivityPeripheralReduced" />
<openmrs:globalProperty key="diabetesmanagement.simulation.insulinSensivityPeripheralNormal" defaultValue="" var="insulinSensivityPeripheralNormal" />
<openmrs:globalProperty key="diabetesmanagement.simulation.insulinSensivityPeripheralIncreased" defaultValue="" var="insulinSensivityPeripheralIncreased" />

<openmrs:extensionPoint pointId="org.openmrs.diabetesmanagement.simulationForm.aboveTitle" type="html" />

<h2><img src="${pageContext.request.contextPath}/moduleResources/diabetesmanagement/images/Blue_circle_for_diabetes.gif" width="32" height="32" border="0" /> <spring:message code="diabetesmanagement.simulation.simulation" /></h2>

<openmrs:extensionPoint pointId="org.openmrs.diabetesmanagement.simulationForm.belowTitle" type="html" />

<spring:hasBindErrors name="sim">
	<spring:message code="fix.error" />
	<br />
</spring:hasBindErrors>

<br />

<c:if test="${patient != null}">
	<div id="patientSummary">
		<table cellpadding="3" cellspacing="0">
			<tr>
				<th align="left"><spring:message code="medicalproblem.problem.patient" /></th>
				<td align="left">${patient.personName}</td>
			</tr>
		</table>
	</div>
</c:if>
<div id="resultBoxes">
	<table width="100%">
		<c:if test="${sim.resultsAvailableCurrent}">
			<tr><td><spring:message code="diabetesmanagement.simulation.executionTime" />: ${sim.executionTime}s</td></tr>
			<tr>
				<td align="center" id="resultBoxPlasmaGlucose">
					<img src="${pageContext.request.contextPath}/moduleServlet/medicalproblem/showCustomDataGraphServlet?hideDate&amp;hideShapes&width=800&amp;fromDate=${today}&amp;toDate=${today}&amp;seriesTitle1=<spring:message code="diabetesmanagement.simulation.current" />&amp;conceptId=${glu}&amp;filename1=${filenamePlasmaGlucoseCurrent}<c:if test="${sim.resultsAvailablePrevious}">&amp;seriesTitle2=<spring:message code="diabetesmanagement.simulation.previous" />&amp;filename2=${filenamePlasmaGlucosePrevious}</c:if>" />
				</td>
			</tr>
			<tr>
				<td align="center" id="resultBoxMeals">
					<img src="${pageContext.request.contextPath}/moduleServlet/medicalproblem/showCustomDataGraphServlet?chartType=bar&amp;hideDate&amp;hideLines&amp;width=800&amp;height=150&amp;maxRange=50&amp;fromDate=${today}&amp;toDate=${today}&amp;seriesTitle1=<spring:message code="diabetesmanagement.simulation.current" />&amp;customTitle=<spring:message code="diabetesmanagement.simulation.meals" />&amp;customUnits=g&filename1=${filenameMealsCurrent}<c:if test="${sim.resultsAvailablePrevious}">&amp;seriesTitle2=<spring:message code="diabetesmanagement.simulation.previous" />&amp;filename2=${filenameMealsPrevious}</c:if>" />
				</td>
			</tr>
			<tr>
				<td align="center" id="resultBoxPlasmaInsulin">
					<img src="${pageContext.request.contextPath}/moduleServlet/medicalproblem/showCustomDataGraphServlet?hideDate&amp;hideShapes&amp;width=800&amp;fromDate=${today}&amp;toDate=${today}&amp;seriesTitle1=<spring:message code="diabetesmanagement.simulation.current" />&amp;customTitle=<spring:message code="diabetesmanagement.simulation.plasmaInsulin" />&amp;customUnits=mU/l&amp;maxRange=50&amp;filename1=${filenamePlasmaInsulinCurrent}<c:if test="${sim.resultsAvailablePrevious}">&amp;seriesTitle2=<spring:message code="diabetesmanagement.simulation.previous" />&amp;filename2=${filenamePlasmaInsulinPrevious}</c:if>" />
				</td>
			</tr>
			<tr>
				<td align="center" id="resultBoxInjections">
					<img src="${pageContext.request.contextPath}/moduleServlet/medicalproblem/showCustomDataGraphServlet?hideDate&amp;hideLines&amp;width=800&amp;height=150&amp;maxRange=10&amp;fromDate=${today}&amp;toDate=${today}&amp;customTitle=<spring:message code="diabetesmanagement.simulation.insulinInjections" />&amp;customUnits=U&amp;seriesTitle1=${sim.insulin1.name}&amp;filename1=${filenameInjections1}&amp;seriesTitle2=${sim.insulin2.name}&amp;filename2=${filenameInjections2}" />
				</td>
			</tr>
		</c:if>
	</table>
</div>
<br />

<openmrs:extensionPoint pointId="org.openmrs.diabetesmanagement.simulationForm.beforeDataEntry" type="html" />

<form method="post">
<div class="boxHeader"><spring:message code="diabetesmanagement.simulation.meals" /></div>
<div class="box">
	<table cellspacing="0" cellpadding="2" width="100%" class="diabetesmanagementMeals">
		<tr>
			<th align="left"><spring:message code="diabetesmanagement.simulation.weight" /></th>
			<td>
				<spring:bind path="sim.weight">
					<input type="text" name="weight" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<th />
			<th align="left"><spring:message code="diabetesmanagement.simulation.mealBreakfast" /></th>
			<th align="left"><spring:message code="diabetesmanagement.simulation.mealSnack" /></th>
			<th align="left"><spring:message code="diabetesmanagement.simulation.mealLunch" /></th>
			<th align="left"><spring:message code="diabetesmanagement.simulation.mealSnack" /></th>
			<th align="left"><spring:message code="diabetesmanagement.simulation.mealDinner" /></th>
			<th align="left"><spring:message code="diabetesmanagement.simulation.mealSnack" /></th>
		</tr>
		<tr>
			<th align="left"><spring:message code="diabetesmanagement.simulation.time" /></th>
			<td>
				<spring:bind path="sim.mealTime1">
					<input type="text" name="mealTime1" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.mealTime2">
					<input type="text" name="mealTime2" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.mealTime3">
					<input type="text" name="mealTime3" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.mealTime4">
					<input type="text" name="mealTime4" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.mealTime5">
					<input type="text" name="mealTime5" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.mealTime6">
					<input type="text" name="mealTime6" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<th align="left"><spring:message code="diabetesmanagement.simulation.carbs" /></th>
			<td>
				<spring:bind path="sim.mealCarbs1">
					<input type="text" name="mealCarbs1" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.mealCarbs2">
					<input type="text" name="mealCarbs2" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.mealCarbs3">
					<input type="text" name="mealCarbs3" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.mealCarbs4">
					<input type="text" name="mealCarbs4" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.mealCarbs5">
					<input type="text" name="mealCarbs5" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.mealCarbs6">
					<input type="text" name="mealCarbs6" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
		</tr>
	</table>
</div>
<br />
<div class="boxHeader"><spring:message code="diabetesmanagement.simulation.insulin" /></div>
<div class="box" id="insulin">
	<table cellspacing="0" cellpadding="2" width="100%">
		<tr>
			<th align="left"><spring:message code="diabetesmanagement.simulation.preparations" /></th>
			<th align="left"><spring:message code="diabetesmanagement.simulation.time" /></th>
			<td>
				<spring:bind path="sim.insulinTime1">
					<input type="text" name="insulinTime1" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.insulinTime2">
					<input type="text" name="insulinTime2" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.insulinTime3">
					<input type="text" name="insulinTime3" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.insulinTime4">
					<input type="text" name="insulinTime4" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td>
				<spring:bind path="sim.insulin1">
					<select name="insulin1">
						<c:forEach var="type" items="${sim.insulinList}">
							<option value="${type.insulinTypeId}" <c:if test="${type.name == status.value}">selected="selected"</c:if>>${type.name}</option>
						</c:forEach>
					</select>
				</spring:bind>	
			</td>
			<th align="left"><spring:message code="diabetesmanagement.simulation.dose" /></th>
			<td>
				<spring:bind path="sim.insulin1Dose1">
					<input type="text" name="insulin1Dose1" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.insulin1Dose2">
					<input type="text" name="insulin1Dose2" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.insulin1Dose3">
					<input type="text" name="insulin1Dose3" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.insulin1Dose4">
					<input type="text" name="insulin1Dose4" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td>
				<spring:bind path="sim.insulin2">
					<select name="insulin2">
						<c:forEach var="type" items="${sim.insulinList}">
							<option value="${type.insulinTypeId}" <c:if test="${type.name == status.value}">selected="selected"</c:if>>${type.name}</option>
						</c:forEach>
					</select>
				</spring:bind>
			</td>
			<th align="left"><spring:message code="diabetesmanagement.simulation.dose" /></th>
			<td>
				<spring:bind path="sim.insulin2Dose1">
					<input type="text" name="insulin2Dose1" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.insulin2Dose2">
					<input type="text" name="insulin2Dose2" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.insulin2Dose3">
					<input type="text" name="insulin2Dose3" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
			<td>
				<spring:bind path="sim.insulin2Dose4">
					<input type="text" name="insulin2Dose4" value="${status.value}" size="5" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
			</td>
		</tr>
	</table>
</div>
<br />
<div class="boxHeader"><spring:message code="diabetesmanagement.simulation.kidneyFunction" /></div>
<div class="box">
	<table cellspacing="0" cellpadding="2" width="100%" class="diabetesmanagementKidneyFunction">
		<tr>
			<th align="left" width="25%"><spring:message code="diabetesmanagement.simulation.renalThreshold" /></th>
			<td align="left" width="25%">
				<spring:bind path="sim.RTG">
					<c:choose>
						<c:when test="${sim.patientSpecificRTG}">
							<input name="RTG" type="hidden" value="${status.value}" />${status.value} *
						</c:when>
						<c:otherwise>
							<select name="RTG">
								<option value="${rtgLow}" <c:if test="${rtgLow == status.value}">selected="selected"</c:if>><spring:message code="diabetesmanagement.simulation.levelLow" /></option>
								<option value="${rtgNormal}" <c:if test="${rtgNormal == status.value}">selected="selected"</c:if>><spring:message code="diabetesmanagement.simulation.levelNormal" /></option>
								<option value="${rtgHigh}" <c:if test="${rtgHigh == status.value}">selected="selected"</c:if>><spring:message code="diabetesmanagement.simulation.levelHigh" /></option>
							</select>
						</c:otherwise>
					</c:choose>
				</spring:bind>
			</td>
			<th align="left" width="25%"><spring:message code="diabetesmanagement.simulation.renalFunction" /></th>
			<td align="left" width="25%">
				<spring:bind path="sim.CCR">
					<c:choose>
						<c:when test="${sim.patientSpecificCCR}">
							<input name="CCR" type="hidden" value="${status.value}" />${status.value} *
						</c:when>
						<c:otherwise>
							<select name="CCR">
								<option value="${ccrReduced}" <c:if test="${ccrReduced == status.value}">selected="selected"</c:if>><spring:message code="diabetesmanagement.simulation.levelReduced" /></option>
								<option value="${ccrNormal}" <c:if test="${ccrNormal == status.value}">selected="selected"</c:if>><spring:message code="diabetesmanagement.simulation.levelNormal" /></option>
							</select>
						</c:otherwise>
					</c:choose>
				</spring:bind>
			</td>
		</tr>
	</table>
</div>
<br />
<div class="boxHeader"><spring:message code="diabetesmanagement.simulation.insulinSensivities" /></div>
<div class="box">
	<table cellspacing="0" cellpadding="2" width="100%" class="diabetesmanagementInsulinSensivities">
		<tr>
			<th align="left" width="25%"><spring:message code="diabetesmanagement.simulation.liver" /></th>
			<td align="left" width="25%">
				<spring:bind path="sim.sh">
					<c:choose>
						<c:when test="${sim.patientSpecificSh}">
							<input name="sh" type="hidden" value="${status.value}" />${status.value} *
						</c:when>
						<c:otherwise>
							<select name="sh">
								<option value="${insulinSensivityHepaticReduced}" <c:if test="${insulinSensivityHepaticReduced == status.value}">selected="selected"</c:if>><spring:message code="diabetesmanagement.simulation.levelReduced" /></option>
								<option value="${insulinSensivityHepaticNormal}" <c:if test="${insulinSensivityHepaticNormal == status.value}">selected="selected"</c:if>><spring:message code="diabetesmanagement.simulation.levelNormal" /></option>
								<option value="${insulinSensivityHepaticIncreased}" <c:if test="${insulinSensivityHepaticIncreased == status.value}">selected="selected"</c:if>><spring:message code="diabetesmanagement.simulation.levelIncreased" /></option>
							</select>
						</c:otherwise>
					</c:choose>
				</spring:bind>
			</td>
			<th align="left" width="25%"><spring:message code="diabetesmanagement.simulation.peripheral" /></th>
			<td align="left" width="25%">
				<spring:bind path="sim.sp">
					<c:choose>
						<c:when test="${sim.patientSpecificSp}">
							<input name="sp" type="hidden" value="${status.value}" />${status.value} *
						</c:when>
						<c:otherwise>
							<select name="sp">
								<option value="${insulinSensivityPeripheralReduced}" <c:if test="${insulinSensivityPeripheralReduced == status.value}">selected="selected"</c:if>><spring:message code="diabetesmanagement.simulation.levelReduced" /></option>
								<option value="${insulinSensivityPeripheralNormal}" <c:if test="${insulinSensivityPeripheralNormal == status.value}">selected="selected"</c:if>><spring:message code="diabetesmanagement.simulation.levelNormal" /></option>
								<option value="${insulinSensivityPeripheralIncreased}" <c:if test="${insulinSensivityPeripheralIncreased == status.value}">selected="selected"</c:if>><spring:message code="diabetesmanagement.simulation.levelIncreased" /></option>
							</select>
						</c:otherwise>
					</c:choose>
				</spring:bind>
			</td>
		</tr>
	</table>
</div>
<br />* <spring:message code="diabetesmanagement.simulation.patientSpecificValue" /></td>
<br /><br />
<input type="submit" id="runSimulationButton" value='<spring:message code="diabetesmanagement.simulation.run" />'>
</form>

<openmrs:extensionPoint pointId="org.openmrs.diabetesmanagement.simulationForm.afterDataEntry" type="html" />

<openmrs:htmlInclude file="/dwr/engine.js" />
<openmrs:htmlInclude file="/dwr/util.js" />

<%@ include file="/WEB-INF/template/footer.jsp"%>
