package be.crydust.spike;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "HelloServlet", urlPatterns = {"/HelloServlet"})
public class HelloServlet extends HttpServlet {

	private static void writeResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		final ServletOutputStream out = response.getOutputStream();
		final String indexUrl = response.encodeURL(request.getContextPath() + "/index.jsp");
		out.print("<!DOCTYPE html>\n" +
				"<html>\n" +
				"<head>\n" +
				"<title>HelloServlet</title>\n" +
				"<meta charset=\"UTF-8\"/>\n" +
				"</head>\n" +
				"<body>\n" +
				"<h1>HelloServlet</h1>\n" +
				"<p>Hello World! " + escapeHtml4(Long.toString(System.currentTimeMillis())) + "</p>\n" +
				"<p><a href='" + escapeHtml4(indexUrl) + "'>Go back to the index</a></p>\n" +
				"</body>\n" +
				"</html>");
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(SC_METHOD_NOT_ALLOWED);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		writeResponse(request, response);
	}
}
