package be.crydust.spike.users;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;

@WebServlet(name = "UsersServlet", urlPatterns = {"/UsersServlet"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"admin"}))
public class UsersServlet extends HttpServlet {

    private static void writeResponse(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException, ServletException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setAttribute("model", model);
        request.getRequestDispatcher("/WEB-INF/jsp/users.jsp").forward(request, response);
    }

    private static DataSource lookupDataSource() throws NamingException {
        final Context ctx = (Context) new InitialContext().lookup("java:comp/env");
        return (DataSource) ctx.lookup("jdbc/MyDataSource");
    }

    private static <T> List<T> sqlToList(DataSource ds, String sql, ParameterSetter parameterSetter, ResultSetMapper<T> resultSetMapper) {
        final List<T> list = new ArrayList<>();
        try (final Connection con = ds.getConnection()) {
            con.setReadOnly(true);
            try (final PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setQueryTimeout(5);
                ps.setFetchSize(100);
                parameterSetter.accept(ps);
                try (final ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(resultSetMapper.map(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Set<String> usernames = new HashSet<>();
        //language=PostgreSQL
        String sql = "select users.user_name, user_roles.role_name\n" +
                "from users\n" +
                "left join user_roles\n" +
                "on users.user_name = user_roles.user_name\n";

        final DataSource ds;
        try {
            ds = lookupDataSource();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        final List<User> users = sqlToList(
                ds,
                sql,
                ps -> {
                },
                rs -> {
                    String user_name = rs.getString("user_name");
                    if (rs.wasNull()) user_name = null;
                    String role_name = rs.getString("role_name");
                    if (rs.wasNull()) role_name = null;
                    return new User(
                            user_name,
                            role_name == null ? emptySet() : singleton(role_name));
                })
                .stream()
                .collect(Collectors.toMap(
                        User::getName,
                        User::getRoles,
                        (a, b) -> {
                            final Set<String> roles = new HashSet<>(a);
                            roles.addAll(b);
                            return roles;
                        }))
                .entrySet()
                .stream()
                .map(it -> new User(it.getKey(), it.getValue()))
                .collect(Collectors.toList());


        writeResponse(request, response, null);
    }

    @FunctionalInterface
    private interface ParameterSetter {
        void accept(PreparedStatement ps) throws SQLException;
    }

    @FunctionalInterface
    private interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
