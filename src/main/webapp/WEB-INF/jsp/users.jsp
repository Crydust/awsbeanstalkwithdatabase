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
<h2>Existing users</h2>

<c:if test="${model.error}">
    <p><c:out value="${model.errorMessage}"/></p>
</c:if>

<c:url var="url" value="/UsersServlet"/>
<form action="${fn:escapeXml(url)}" method="post" accept-charset="UTF-8">
    <table>
        <tr>
            <th>Login</th>
            <th>Roles</th>
            <th>Actions</th>
        </tr>
        <c:forEach var="user" items="${model.users}" varStatus="userStatus">
            <tr>
                <td><c:out value="${user.name}"/></td>
                <td>
                    <ul>
                        <c:forEach var="role" items="${user.roles}" varStatus="roleStatus">
                            <li>
                                <c:out value="${role}"/>
                                    ${' '}
                                <c:set var="subFormPrefix"
                                       value="deleteUserRole-${userStatus.index}-${roleStatus.index}"/>
                                <input type="hidden" name="${fn:escapeXml(subFormPrefix += ':name')}"
                                       value="${fn:escapeXml(user.name)}"/>
                                <input type="hidden" name="${fn:escapeXml(subFormPrefix += ':role')}"
                                       value="${fn:escapeXml(role)}"/>
                                <button type="submit" name="button"
                                        value="${fn:escapeXml('deleteUserRole:' += subFormPrefix)}">
                                    Remove role
                                </button>
                            </li>
                        </c:forEach>
                    </ul>
                </td>
                <td>
                    <c:set var="subFormPrefix"
                           value="addRoleToUser-${userStatus.index}"/>
                    <input type="hidden" name="${fn:escapeXml(subFormPrefix += ':name')}"
                           value="${fn:escapeXml(user.name)}"/>
                    <p>
                        <c:set var="escapedName" value="${fn:escapeXml(subFormPrefix += ':role')}"/>
                        <label for="${escapedName}">Role</label><br/>
                        <input type="text" id="${escapedName}" name="${escapedName}"/><br/>
                        <button type="submit" name="button"
                                value="${fn:escapeXml('addRoleToUser:' += subFormPrefix)}">
                            Add role to user
                        </button>
                    </p>
                    <p>
                        <c:set var="subFormPrefix"
                               value="removeUser-${userStatus.index}"/>
                        <input type="hidden" name="${fn:escapeXml(subFormPrefix += ':name')}"
                               value="${fn:escapeXml(user.name)}"/>
                        <button type="submit" name="button" value="${fn:escapeXml('removeUser:' += subFormPrefix)}">
                            Remove user
                        </button>
                    </p>
                </td>
            </tr>
        </c:forEach>
    </table>

    <h2>New user</h2>

    <c:set var="subFormPrefix" value="createUser-0"/>
    <p>
        <label for="${fn:escapeXml(subFormPrefix += ':name')}">Login</label><br/>
        <input type="text" id="${fn:escapeXml(subFormPrefix += ':name')}"
               name="${fn:escapeXml(subFormPrefix += ':name')}"/>
    </p>
    <p>
        <label for="${fn:escapeXml(subFormPrefix += ':password')}">Password</label><br/>
        <input type="password" id="${fn:escapeXml(subFormPrefix += ':password')}"
               name="${fn:escapeXml(subFormPrefix += ':password')}"/>
    </p>
    <p>
        <label for="${fn:escapeXml(subFormPrefix += ':role')}">Role (optional)</label><br/>
        <input type="text" id="${fn:escapeXml(subFormPrefix += ':role')}"
               name="${fn:escapeXml(subFormPrefix += ':role')}"/>
    </p>
    <p>
        <button type="submit" name="button" value="${fn:escapeXml('createUser:' += subFormPrefix)}">Create user</button>
    </p>
</form>

<c:url var="url" value="/IndexServlet"/>
<p><a href="${fn:escapeXml(url)}">Go back to the index</a></p>
<c:if test="${not empty pageContext.request.userPrincipal}">
    <c:url var="url" value="/LogoutServlet"/>
    <p><a href="${fn:escapeXml(url)}">Log out</a></p>
</c:if>
</body>
</html>