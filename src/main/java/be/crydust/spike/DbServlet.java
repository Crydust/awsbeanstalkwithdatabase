package be.crydust.spike;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebServlet(name = "DbServlet", urlPatterns = {"/DbServlet"})
public class DbServlet extends HttpServlet {

	// this also works, but see lookupDataSource for an approach that fails more gracefully
//	@Resource(name = "jdbc/MyDataSource")
//	private DataSource ds;

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
		final long currentTimeMillis = System.currentTimeMillis();
		boolean success = false;
		try (final Connection con = ds.getConnection()) {
			con.setReadOnly(true);
			try (final PreparedStatement ps = con.prepareStatement("SELECT ?")) {
				ps.setLong(1, currentTimeMillis);
				ps.setQueryTimeout(5);
				ps.setFetchSize(1);
				try (final ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						long aLong = rs.getLong(1);
						if (rs.wasNull()) {
							aLong = -1;
						}
						success = (aLong == currentTimeMillis);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return success;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		boolean success = false;
		try {
			final DataSource ds = lookupDataSource();
			success = executeSql(ds);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		writeResponse(request, response, success ? Status.SUCCESS : Status.FAILURE);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		writeResponse(request, response, Status.INITIAL);
	}

	private static DataSource lookupDataSource() throws NamingException {
		final Context ctx = (Context)new InitialContext().lookup("java:comp/env");
		return (DataSource)ctx.lookup("jdbc/MyDataSource");
	}

	private enum Status {
		INITIAL, SUCCESS, FAILURE
	}
}
