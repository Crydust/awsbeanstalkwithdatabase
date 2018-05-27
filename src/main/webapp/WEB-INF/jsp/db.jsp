<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
	<title>DbServlet</title>
	<meta charset="UTF-8"/>
</head>
<body>
<h1>DbServlet</h1>
<c:url var="url" value="/DbServlet"/>
<form action="${fn:escapeXml(url)}" method="POST">
	<p>
		<input type="submit" value="Test database connection"/>
	</p>
</form>
<c:choose>
	<c:when test="${status eq 'SUCCESS'}">
		<p><b>SUCCESS</b> We were able to connect to the database. :-D</p>
	</c:when>
	<c:when test="${status eq 'FAILURE'}">
		<p><b>FAILURE</b> We were not able to connect to the database. :-(</p>
	</c:when>
</c:choose>
<c:url var="url" value="/IndexServlet"/>
<p><a href="${fn:escapeXml(url)}">Go back to the index</a></p>
</body>
</html>