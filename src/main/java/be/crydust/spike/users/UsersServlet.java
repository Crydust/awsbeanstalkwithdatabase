package be.crydust.spike.users;

import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toMap;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@WebServlet(name = "UsersServlet", urlPatterns = {"/UsersServlet"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"admin"}))
public class UsersServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UsersServlet.class.getName());

    private static void writeResponse(HttpServletRequest request, HttpServletResponse response, UsersViewModel model) throws IOException, ServletException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setAttribute("model", model);
        request.getRequestDispatcher("/WEB-INF/jsp/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        final String button = request.getParameter("button");
        if (button == null || button.isEmpty()) {
            response.setStatus(SC_BAD_REQUEST);
            return;
        }
        final FilteredRequest filteredRequest = new FilteredRequest(button, request.getParameterMap());
        if (button.startsWith("deleteUserRole:")) {
            final String name = filteredRequest.getParameter("name");
            final String role = filteredRequest.getParameter("role");
        } else if (button.startsWith("addRoleToUser:")) {
            final String name = filteredRequest.getParameter("name");
            final String role = filteredRequest.getParameter("role");
        } else if (button.startsWith("removeUser:")) {
            final String name = filteredRequest.getParameter("name");
        } else if (button.startsWith("createUser:")) {
            final String name = filteredRequest.getParameter("name");
            final String password = filteredRequest.getParameter("password");
            final String role = filteredRequest.getParameter("role");
        } else {
            response.setStatus(SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            final DataSource ds = RepositoryCommons.lookupDataSource();
            final UserRepository repository = new UserRepository(ds);
            final List<User> users = repository.findAll();
            writeResponse(request, response, UsersViewModel.createSuccess(users));
        } catch (RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Could not load users.", e);
            writeResponse(request, response, UsersViewModel.createError("Could not load users."));
        }

    }

    private static class FilteredRequest {

        private final String prefix;
        private final Map<String, String[]> parameterMap;

        FilteredRequest(String button, Map<String, String[]> parameterMap) {
            if (button == null || button.isEmpty() || !button.contains(":")) {
                this.prefix = "";
                this.parameterMap = parameterMap;
            } else {
                this.prefix = button.substring(button.indexOf(':') + 1);
                this.parameterMap = parameterMap
                        .entrySet()
                        .stream()
                        .filter(it -> it.getKey().startsWith(prefix + ':'))
                        .collect(toMap(
                                entry -> entry.getKey().substring(prefix.length() + 1),
                                entry -> entry.getValue()
                        ));
            }
        }

        String getParameter(String name) {
            final String[] values = parameterMap.get(name);
            if (values == null || values.length == 0) {
                return null;
            }
            return values[0];
        }

        String[] getParameterValues(String name) {
            return parameterMap.get(name);
        }

        Map<String, String[]> getParameterMap() {
            return parameterMap;
        }

        Enumeration<String> getParameterNames() {
            return Collections.enumeration(parameterMap.keySet());
        }

    }

}
