<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="name" scope="request" type="java.lang.String"/>
<!DOCTYPE html>
<html>
<head>
    <title>EchoServlet</title>
    <meta charset="UTF-8"/>
</head>
<body>
<c:url var="url" value="/EchoServlet"/>
<form action="${fn:escapeXml(url)}" method="post" accept-charset="UTF-8">
    <p><label for="name">What is your name?</label><br/>
        <input type="text" name="name" id="name" value=""/></p>
    <p><input type="submit" value="Answer"/></p>
</form>
<h1>EchoServlet</h1>
<c:if test="${not empty name}">
    <p>Hello <c:out value="${name}"/>!</p>
</c:if>
<c:url var="url" value="/IndexServlet"/>
<p><a href="${fn:escapeXml(url)}">Go back to the index</a></p>
<c:if test="${not empty pageContext.request.userPrincipal}">
    <c:url var="url" value="/LogoutServlet"/>
    <p><a href="${fn:escapeXml(url)}">Log out</a></p>
</c:if>
</body>
</html>