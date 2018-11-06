<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="model" scope="request" type="be.crydust.spike.presentation.users.UsersBackingBean"/>
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
    <ul>
        <c:forEach var="errorMessage" items="${model.errorMessages}">
            <li><c:out value="${errorMessage}"/></li>
        </c:forEach>
    </ul>
</c:if>

<c:url var="url" value="/UsersServlet"/>
<form action="${fn:escapeXml(url)}" method="post" accept-charset="UTF-8">
    <table border="1">
        <tr>
            <th>Login</th>
            <th>Roles</th>
            <th>Actions</th>
        </tr>
        <c:forEach var="userEntry" items="${model.usersByName}" varStatus="userStatus">
            <c:set var="userName" value="${userEntry.key}"/>
            <c:set var="userRoles" value="${userEntry.value.rolesByName.keySet()}"/>
            <tr>
                <td><c:out value="${userName}"/></td>
                <td>
                    <ul>
                        <c:forEach var="userRole" items="${userRoles}" varStatus="roleStatus">
                            <li>
                                <c:out value="${userRole}"/>
                                    ${' '}
                                <button type="submit" name="deleteUserRoleButton"
                                        value="${fn:escapeXml(
                                        fn:replace(fn:replace(userName, '\\', '\\\\'), ',' , '\\,') +=
                                        ',' +=
                                        fn:replace(fn:replace(userRole, '\\', '\\\\'), ',' , '\\,')
                                        )}">
                                    Remove role
                                </button>
                            </li>
                        </c:forEach>
                    </ul>
                </td>
                <td>
                    <p>
                        <c:set var="escapedName" value="${'addRoleToUser.' += fn:escapeXml(userName) += '.role'}"/>
                        <label for="${escapedName}">Role</label><br/>
                        <input type="text" id="${escapedName}" name="${escapedName}"
                               value="${fn:escapeXml(userEntry.value.addRoleToUser.role)}"/><br/>
                        <button type="submit" name="addRoleToUserButton" value="${fn:escapeXml(userName)}">
                            Add role to user
                        </button>
                    </p>
                    <p>
                        <button type="submit" name="removeUserButton" value="${fn:escapeXml(userName)}">
                            Remove user
                        </button>
                    </p>
                </td>
            </tr>
        </c:forEach>
    </table>

    <h2>New user</h2>

    <p>
        <label for="createUser.name">Login</label><br/>
        <input type="text" id="createUser.name" name="createUser.name"
               value="${fn:escapeXml(model.createUser.name)}"/>
    </p>
    <p>
        <label for="createUser.password">Password</label><br/>
        <input type="password" id="createUser.password" name="createUser.password"
               value="${fn:escapeXml(model.createUser.password)}"/>
    </p>
    <p>
        <label for="createUser.role">Role (optional)</label><br/>
        <input type="text" id="createUser.role" name="createUser.role"
               value="${fn:escapeXml(model.createUser.role)}"/>
    </p>
    <p>
        <button type="submit" name="createUserButton" value="">Create user</button>
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