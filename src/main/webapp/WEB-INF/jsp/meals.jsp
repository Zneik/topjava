<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<html>
<jsp:include page="fragments/headTag.jsp"/>
<body>
<script type="text/javascript" src="resources/js/topjava.common.js" defer></script>
<script type="text/javascript" src="resources/js/topjava.meals.js" defer></script>
<div>
    <jsp:include page="fragments/bodyHeader.jsp"/>

    <div class="jumbotron pt-4">
        <div class="container">
            <h3 class="text-center"><spring:message code="meal.title"/></h3>
            <div class="card">
                <div class="card-body">
                    <form id="filter">
                        <div class="row">
                            <div class="offset-1 col-2">
                                <label for="startDate"><spring:message
                                        code="meal.startDate"/>:</label>
                                <input class="form-control" type="date" id="startDate" name="startDate"
                                       value="${param.startDate}">
                            </div>
                            <div class="col-2">
                                <label for="endDate"><spring:message
                                        code="meal.endDate"/>:</label>
                                <input class="form-control" type="date" id="endDate" name="endDate"
                                       value="${param.endDate}">
                            </div>
                            <div class="offset-2 col-2">
                                <label for="startTime"><spring:message
                                        code="meal.startTime"/>:</label>
                                <input class="form-control" type="time" id="startTime" name="startTime"
                                       value="${param.startTime}">
                            </div>
                            <div class="col-2">
                                <label for="endTime"><spring:message
                                        code="meal.endTime"/>:</label>
                                <input class="form-control" type="time" id="endTime" name="endTime"
                                       value="${param.endTime}">
                            </div>
                        </div>
                    </form>
                </div>
                <div class="card-footer text-right">
                    <button class="btn btn-primary" onclick="updateTableFilter()"><spring:message
                            code="meal.filter"/></button>
                    <button class="btn btn-danger" onclick="resetFilter()"><spring:message code="meal.cancel"/></button>
                </div>
            </div>

            <hr>
            <button class="btn btn-primary" onclick="add()"><spring:message code="meal.add"/></button>
            <hr>
            <table class="table table-striped" id="datatable">
                <thead>
                <tr>
                    <th><spring:message code="meal.dateTime"/></th>
                    <th><spring:message code="meal.description"/></th>
                    <th><spring:message code="meal.calories"/></th>
                    <th></th>
                </tr>
                </thead>
                <c:forEach items="${meals}" var="meal">
                    <jsp:useBean id="meal" scope="page" type="ru.javawebinar.topjava.to.MealTo"/>
                    <tr data-mealExcess="${meal.excess}">
                        <td>
                                ${fn:formatDateTime(meal.dateTime)}
                        </td>
                        <td>${meal.description}</td>
                        <td>${meal.calories}</td>
                        <td><a onclick="deleteRow(${meal.id})"><span class="fa fa-remove"> <spring:message
                                code="common.delete"/></span></a></td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>

    <div class="modal fade" tabindex="-1" id="editRow">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title"><spring:message code="meal.add"/></h4>
                    <button type="button" class="close" data-dismiss="modal" onclick="closeNoty()">&times;</button>
                </div>
                <div class="modal-body">
                    <form id="detailsForm">
                        <input type="hidden" id="id" name="id">

                        <div class="form-group">
                            <label for="dateTime" class="col-form-label"><spring:message code="meal.dateTime"/></label>
                            <input type="datetime-local" class="form-control" id="dateTime" name="dateTime"
                                   placeholder="<spring:message code="meal.dateTime"/>">
                        </div>

                        <div class="form-group">
                            <label for="description" class="col-form-label"><spring:message
                                    code="meal.description"/></label>
                            <input type="email" class="form-control" id="description" name="description"
                                   placeholder="<spring:message code="meal.description"/>">
                        </div>

                        <div class="form-group">
                            <label for="calories" class="col-form-label"><spring:message code="meal.calories"/></label>
                            <input type="text" value="1000" class="form-control" id="calories" name="calories"
                                   placeholder="<spring:message code="meal.calories"/>">
                        </div>
                    </form>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="closeNoty()">
                        <span class="fa fa-close"></span>
                        <spring:message code="common.cancel"/>
                    </button>
                    <button type="button" class="btn btn-primary" onclick="save()">
                        <span class="fa fa-check"></span>
                        <spring:message code="common.save"/>
                    </button>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="fragments/footer.jsp"/>
</body>
</html>