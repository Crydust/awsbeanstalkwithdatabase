package be.crydust.spike;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet(name = "DbServlet", urlPatterns = {"/DbServlet"})
public class DbServlet extends HttpServlet {

	@Resource(name = "jdbc/exampleDB")
	private DataSource ds;

	private static void writeResponse(HttpServletRequest request, HttpServletResponse response, Status status) throws IOException {
		if (status == Status.FAILURE) {
			response.setStatus(SC_INTERNAL_SERVER_ERROR);
		}
		response.setContentType("text/html");
		final ServletOutputStream out = response.getOutputStream();
		final String actionUrl = response.encodeURL(request.getContextPath() + "/DbServlet");
		final String indexUrl = response.encodeURL(request.getContextPath() + "/index.jsp");
		out.print("<!DOCTYPE html>\n" +
				"<html>\n" +
				"<head>\n" +
				"<title>DbServlet</title>\n" +
				"<meta charset=\"UTF-8\"/>\n" +
				"</head>\n" +
				"<body>\n" +
				"<h1>DbServlet</h1>\n" +
				"<form action='" + escapeHtml4(actionUrl) + "' method='POST'>\n" +
				"<p>\n" +
				"<input type='submit' value='Test database connection'/>\n" +
				"</p>\n" +
				"</form>\n");
		switch (status) {
			case SUCCESS:
				out.print("<p><b>SUCCESS</b> We were able to connect to the database. :-D</p>\n");
				break;
			case FAILURE:
				out.print("<p><b>FAILURE</b> We were not able to connect to the database. :-(</p>\n");
				break;
			default:
				// NOOP
		}
		out.print("<p><a href='" + escapeHtml4(indexUrl) + "'>Go back to the index</a></p>\n" +
				"</body>\n" +
				"</html>");
	}

	private static boolean executeSql(DataSource ds) {
		boolean success = false;
		try (final Connection con = ds.getConnection()) {
			con.setReadOnly(true);
			try (final PreparedStatement ps = con.prepareStatement("SELECT 'X'")) {
				ps.setQueryTimeout(5);
				ps.setFetchSize(1);
				try (final ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						String string = rs.getString(1);
						if (rs.wasNull()) {
							string = null;
						}
						System.out.println("string = " + string);
						success = true;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return success;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final boolean success = executeSql(ds);
		writeResponse(request, response, success ? Status.SUCCESS : Status.FAILURE);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		writeResponse(request, response, Status.INITIAL);
	}

	private enum Status {
		INITIAL, SUCCESS, FAILURE
	}
}
