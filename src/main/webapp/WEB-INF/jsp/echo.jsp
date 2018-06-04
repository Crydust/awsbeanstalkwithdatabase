<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>EchoServlet</title>
    <meta charset="UTF-8"/>
</head>
<body>
<h1>EchoServlet</h1>
<c:url var="url" value="/EchoServlet"/>
<form action="${fn:escapeXml(url)}" method="POST">
    <p><label for="name">What is your name?</label><br/>
        <input type="text" name="name" id="name" value=""/></p>
    <p><input type="submit" value="Answer"/></p>
</form>
<c:if test="${not empty name}">
    <p>Hello <c:out value="${name}"/>!</p>
</c:if>
<c:url var="url" value="/IndexServlet"/>
<p><a href="${fn:escapeXml(url)}">Go back to the index</a></p>
</body>
</html>