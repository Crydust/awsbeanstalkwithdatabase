<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="model" scope="request" type="be.crydust.spike.users.UsersViewModel"/>
<!DOCTYPE html>
<html>
<head>
    <title>UsersServlet</title>
    <meta charset="UTF-8"/>
</head>
<body>
<h1>UsersServlet</h1>

<%--
Add user (name, password, roles)
Add role to user
Remove role from user
--%>
<h2>Users</h2>
<table border="1">
    <tr>
        <th>Login</th>
        <th>Roles</th>
    </tr>
    <c:forEach var="user" items="${model.users}">
        <tr>
            <td><c:out value="${user.name}"/></td>
            <td>
                <ul>
                    <c:forEach var="role" items="${user.roles}">
                        <li><c:out value="${role}"/></li>
                    </c:forEach>
                </ul>
            </td>
        </tr>
    </c:forEach>
</table>

<c:url var="url" value="/IndexServlet"/>
<p><a href="${fn:escapeXml(url)}">Go back to the index</a></p>
<c:if test="${not empty pageContext.request.userPrincipal}">
    <c:url var="url" value="/LogoutServlet"/>
    <p><a href="${fn:escapeXml(url)}">Log out</a></p>
</c:if>
</body>
</html>