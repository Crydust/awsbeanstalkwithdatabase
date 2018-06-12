<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>Index</title>
    <meta charset="UTF-8"/>
</head>
<body>
<h1>Index</h1>
<ul>
    <c:url var="url" value="/HelloServlet"/>
    <li><a href="${fn:escapeXml(url)}">HelloServlet</a></li>
    <c:url var="url" value="/EchoServlet"/>
    <li><a href="${fn:escapeXml(url)}">EchoServlet</a></li>
    <c:url var="url" value="/DbServlet"/>
    <li><a href="${fn:escapeXml(url)}">DbServlet</a></li>
    <c:url var="url" value="/WhoamiServlet"/>
    <li><a href="${fn:escapeXml(url)}">WhoamiServlet</a></li>
    <c:url var="url" value="/UsersServlet"/>
    <li><a href="${fn:escapeXml(url)}">UsersServlet</a></li>
    <c:url var="url" value="/monitoring"/>
    <li><a href="${fn:escapeXml(url)}">monitoring</a></li>
</ul>
<c:if test="${not empty pageContext.request.userPrincipal}">
    <c:url var="url" value="/LogoutServlet"/>
    <p><a href="${fn:escapeXml(url)}">Log out</a></p>
</c:if>
</body>
</html>
