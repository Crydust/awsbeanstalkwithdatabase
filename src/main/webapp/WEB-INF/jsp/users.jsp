<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
Add role
Remove role
--%>
<h2>Users</h2>
<table>
    <tr>
        <th>Name</th>
        <th>Action</th>
    </tr>
    <c:forEach var="user" items="${users}">
        <tr>
            <td><c:out value="${user}"/></td>
            <td>
                <button type="submit" name="action" value="deleteUser ${fn:escapeXml(user)}">Delete user</button>
            </td>
        </tr>
    </c:forEach>
    <tr>
        <td><label for="user">User name</label><br/>
            <input type="text" name="user" id="user"/></td>
        <td>
            <button type="submit" name="action" value="addUser">Add user</button>
        </td>
    </tr>
</table>

<h2>Roles</h2>
<table>
    <tr>
        <th>Name</th>
        <th>Action</th>
    </tr>
    <c:forEach var="role" items="${roles}">
        <tr>
            <td><c:out value="${role}"/></td>
            <td>
                <button type="submit" name="action" value="deleteRole ${fn:escapeXml(role)}">Delete role</button>
            </td>
        </tr>
    </c:forEach>
    <tr>
        <td><label for="role">Role name</label><br/>
            <input type="text" name="role" id="role"/></td>
        <td>
            <button type="submit" name="action" value="addRole">Add role</button>
        </td>
    </tr>
</table>

<c:url var="url" value="/IndexServlet"/>
<p><a href="${fn:escapeXml(url)}">Go back to the index</a></p>
<c:if test="${not empty pageContext.request.userPrincipal}">
    <c:url var="url" value="/LogoutServlet"/>
    <p><a href="${fn:escapeXml(url)}">Log out</a></p>
</c:if>
</body>
</html>