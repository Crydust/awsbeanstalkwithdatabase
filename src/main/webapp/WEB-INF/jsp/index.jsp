<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
</ul>
</body>
</html>
