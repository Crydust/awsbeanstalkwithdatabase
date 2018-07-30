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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;

@WebServlet(name = "UserServlet", urlPatterns = {"/UserServlet"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"admin"}))
public class UserServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UserServlet.class.getName());

    private static void writeResponse(HttpServletRequest request, HttpServletResponse response, UsersViewModel model) throws IOException, ServletException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setAttribute("model", model);
        request.getRequestDispatcher("/WEB-INF/jsp/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(SC_METHOD_NOT_ALLOWED);
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

}
