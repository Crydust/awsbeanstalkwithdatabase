<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="time" scope="request" type="java.lang.Long"/>
<!DOCTYPE html>
<html>
<head>
    <title>HelloServlet</title>
    <meta charset="UTF-8"/>
</head>
<body>
<h1>HelloServlet</h1>
<p>Hello World! <c:out value="${time}"/></p>
<c:url var="url" value="/IndexServlet"/>
<p><a href="${fn:escapeXml(url)}">Go back to the index</a></p>
<c:if test="${not empty pageContext.request.userPrincipal}">
    <c:url var="url" value="/LogoutServlet"/>
    <p><a href="${fn:escapeXml(url)}">Log out</a></p>
</c:if>
</body>
</html>