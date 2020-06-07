<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head lang="ru">
    <meta charset="utf-8"/>
    <title>Meals</title>
    <link rel="stylesheet" href="css/meals-style.css" />
</head>
<body>
<h1>Meals</h1>
<div>
    <table>
        <tr>
            <th>Date/Time</th>
            <th>Description</th>
            <th>Calories</th>
            <th colspan="2">Actions</th>
        </tr>
        <c:forEach var="meal" items="${meals}">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo"/>
            <tr class="${meal.excess ? 'excess' : 'normal'}">
                <td>
                    <fmt:parseDate value="${meal.dateTime}" var="parseDate" pattern="yyyy-MM-dd'T'HH:mm"/>
                    <fmt:formatDate value="${parseDate}" pattern="yyyy-MM-dd HH:mm"/>
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td>
                    <a href="meals?action=edit&id=${meal.id}">
                        <button>Edit</button>
                    </a>
                </td>
                <td>
                    <a href="meals?action=delete&id=${meal.id}">
                        <button>Delete</button>
                    </a></td>
            </tr>
        </c:forEach>
    </table>
</div>
<hr/>
<div class="editForm">
    <h1>${editMeal != null ? "Edit" : "Add"} meal</h1>
    <form method="post" action="meals">
        <input type="hidden" name="meal_id" value="${editMeal != null ? editMeal.id : null}">
        <div class="form-item">
            <label for="dateTime">Date/Time</label>
            <input id="dateTime" name="dateTime" type="datetime-local" value="${editMeal.dateTime}">
        </div>
        <div class="form-item">
            <label for="description">Description</label>
            <input id="description" name="description" type="text" value="${editMeal.description}">
        </div>
        <div class="form-item">
            <label for="calories">Calories</label>
            <input id="calories" name="calories" type="number" value="${editMeal.calories}">
        </div>
        <div class="form-item">
            <input type="submit" value="Save"/>
        </div>
        <c:choose>
            <c:when test="${editMeal != null}">
                <div class="form-item">
                    <a href="meals"><input type="button" value="Cancel"/></a>
                </div>
            </c:when>
        </c:choose>
    </form>
</div>
</body>
</html>
