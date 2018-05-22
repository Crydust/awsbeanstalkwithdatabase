package be.crydust.spike;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.ServletException;
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

	private static final String PASSWORD = "eP]x74JnsZo>DFW";

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!PASSWORD.equals(request.getParameter("password"))) {
			response.setStatus(SC_FORBIDDEN);
			return;
		}
		final ServletOutputStream out = response.getOutputStream();
		try (final Connection con = ds.getConnection();
			 final PreparedStatement ps = con.prepareStatement("SELECT 'X'");
			 final ResultSet rs = ps.executeQuery()) {
			out.print("con = " + con + "\n");
			out.print("Hello World! " + new Date() + "\n");
			if (rs.next()) {
				out.print("SUCCESS");
				String string = rs.getString(1);
				if (rs.wasNull()) {
					string = null;
				}
				out.print(string);
			}
		} catch (SQLException e) {
			response.setStatus(SC_INTERNAL_SERVER_ERROR);
			out.print("ERROR");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		final ServletOutputStream out = response.getOutputStream();
		out.print("<form method='POST'>" +
				"<input type='password' name='password'/>" +
				"<input type='submit'/>" +
				"</form>");
	}
}
