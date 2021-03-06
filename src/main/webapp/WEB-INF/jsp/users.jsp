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
    <p>ERROR!</p>
</c:if>
<c:if test="${not empty model.errorMessages}">
    <ul>
        <c:forEach var="errorMessage" items="${model.errorMessages}">
            <li>
                <c:choose>
                    <c:when test="${empty errorMessage.fieldId}">
                        <c:out value="${errorMessage.message}"/>
                    </c:when>
                    <c:otherwise>
                        <a href="#${fn:escapeXml(errorMessage.fieldId)}"><c:out value="${errorMessage.message}"/></a>
                    </c:otherwise>
                </c:choose>
            </li>
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
                                <button type="submit" name="button"
                                        value="${fn:escapeXml(
                                        'deleteUserRole:' +=
                                        'name=' += fn:replace(fn:replace(userName, '\\', '\\\\'), ',' , '\\,') +=
                                        ',' +=
                                        'role=' += fn:replace(fn:replace(userRole, '\\', '\\\\'), ',' , '\\,')
                                        )}">
                                    Remove role
                                </button>
                            </li>
                        </c:forEach>
                    </ul>
                </td>
                <td>
                    <p>
                        <c:set var="prefix" value="${'addRoleToUser[' += userStatus.index += ']:'}"/>
                        <c:set var="fieldName" value="${prefix += 'role'}"/>
                        <label for="${fn:escapeXml(fieldName)}">Role</label><br/>
                        <input type="text" id="${fn:escapeXml(fieldName)}" name="${fn:escapeXml(fieldName)}"
                               value="${fn:escapeXml(userEntry.value.addRoleToUser.role)}"/><br/>
                        <button type="submit" name="button"
                                value="${fn:escapeXml(prefix += 'name=' += fn:replace(fn:replace(userName, '\\', '\\\\'), ',' , '\\,'))}">
                            Add role to user
                        </button>
                    </p>
                    <p>
                        <button type="submit" name="button"
                                value="${fn:escapeXml(
                                'removeUser:' +=
                                'name=' += fn:replace(fn:replace(userName, '\\', '\\\\'), ',' , '\\,')
                                )}">
                            Remove user
                        </button>
                    </p>
                </td>
            </tr>
        </c:forEach>
    </table>

    <h2>New user</h2>

    <p>
        <label for="createUser:name">Login</label><br/>
        <input type="text" id="createUser:name" name="createUser:name"
               value="${fn:escapeXml(model.createUser.name)}"/>
    </p>
    <p>
        <label for="createUser:password">Password</label><br/>
        <input type="password" id="createUser:password" name="createUser:password"
               value="${fn:escapeXml(model.createUser.password)}"/>
    </p>
    <p>
        <label for="createUser:role">Role (optional)</label><br/>
        <input type="text" id="createUser:role" name="createUser:role"
               value="${fn:escapeXml(model.createUser.role)}"/>
    </p>
    <p>
        <button type="submit" name="button" value="createUser:">Create user</button>
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