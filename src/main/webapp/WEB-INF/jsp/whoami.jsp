<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>WhoamiServlet</title>
    <meta charset="UTF-8"/>
</head>
<body>
<h1>WhoamiServlet</h1>
<p>Login: <c:out value="${login}"/></p>
<p>Roles:</p>
<ul>
    <c:forEach var="role" items="${roles}">
    <li><c:out value="${role}"/></li>
    </c:forEach>
</ul>
<c:url var="url" value="/IndexServlet"/>
<p><a href="${fn:escapeXml(url)}">Go back to the index</a></p>
<c:if test="${not empty pageContext.request.userPrincipal}">
    <c:url var="url" value="/LogoutServlet"/>
    <p><a href="${fn:escapeXml(url)}">Log out</a></p>
</c:if>
</body>
</html>