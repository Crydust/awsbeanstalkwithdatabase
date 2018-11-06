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
                                <button type="submit" name="deleteUserRoleButton"
                                        value="${fn:escapeXml(
                                        fn:replace(fn:replace(user.name, '\\', '\\\\'), ',' , '\\,') +=
                                        ',' +=
                                        fn:replace(fn:replace(role, '\\', '\\\\'), ',' , '\\,')
                                        )}">
                                    Remove role
                                </button>
                            </li>
                        </c:forEach>
                    </ul>
                </td>
                <td>
                    <p>
                        <c:set var="escapedName" value="${'addRoleToUser.' += fn:escapeXml(user.name) += '.role'}"/>
                        <label for="${escapedName}">Role</label><br/>
                        <input type="text" id="${escapedName}" name="${escapedName}"/><br/>
                        <button type="submit" name="addRoleToUserButton" value="${fn:escapeXml(user.name)}">
                            Add role to user
                        </button>
                    </p>
                    <p>
                        <button type="submit" name="removeUserButton" value="${fn:escapeXml(user.name)}">
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
        <input type="text" id="createUser.name" name="createUser.name"/>
    </p>
    <p>
        <label for="createUser.password">Password</label><br/>
        <input type="password" id="createUser.password" name="createUser.password"/>
    </p>
    <p>
        <label for="createUser.role">Role (optional)</label><br/>
        <input type="text" id="createUser.role" name="createUser.role"/>
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