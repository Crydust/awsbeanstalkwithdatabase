package be.crydust.spike;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet(name = "DbServlet", urlPatterns = {"/DbServlet"})
public class DbServlet extends HttpServlet {

	@Resource(name = "jdbc/exampleDB")
	private DataSource ds;

	private static void writeResponse(HttpServletRequest request, HttpServletResponse response, Status status) throws IOException, ServletException {
		if (status == Status.FAILURE) {
			response.setStatus(SC_INTERNAL_SERVER_ERROR);
		}
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		request.setAttribute("status", status);
		request.getRequestDispatcher("/WEB-INF/jsp/db.jsp").forward(request, response);
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		final boolean success = executeSql(ds);
		writeResponse(request, response, success ? Status.SUCCESS : Status.FAILURE);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		writeResponse(request, response, Status.INITIAL);
	}

	private enum Status {
		INITIAL, SUCCESS, FAILURE
	}
}
