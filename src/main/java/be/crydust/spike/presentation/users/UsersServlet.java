package be.crydust.spike.presentation.users;

import be.crydust.spike.business.users.boundary.UserFacade;
import be.crydust.spike.business.users.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@WebServlet(name = "UsersServlet", urlPatterns = {"/UsersServlet"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"admin"}))
public class UsersServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UsersServlet.class.getName());

    private static void writeResponse(HttpServletRequest request, HttpServletResponse response, UsersBackingBean model) throws IOException, ServletException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setAttribute("model", model);
        request.getRequestDispatcher("/WEB-INF/jsp/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.info("UsersServlet.doPost");
        final String button = request.getParameter("button");
        LOGGER.info("button = " + button);
        if (button == null || button.isEmpty()) {
            response.setStatus(SC_BAD_REQUEST);
            return;
        }
        final FilteredRequest filteredRequest = new FilteredRequest(button, request.getParameterMap());
        if (button.startsWith("deleteUserRole:")) {
            final InputAndViolations<DeleteUserRoleBackingBean> inputAndViolations = filteredRequest.read(new DeleteUserRoleBackingBean());
            LOGGER.info("inputAndViolations = " + inputAndViolations);
        } else if (button.startsWith("addRoleToUser:")) {
            final InputAndViolations<AddRoleToUserBackingBean> inputAndViolations = filteredRequest.read(new AddRoleToUserBackingBean());
            LOGGER.info("inputAndViolations = " + inputAndViolations);
        } else if (button.startsWith("removeUser:")) {
            final InputAndViolations<RemoveUserBackingBean> inputAndViolations = filteredRequest.read(new RemoveUserBackingBean());
            LOGGER.info("inputAndViolations = " + inputAndViolations);
        } else if (button.startsWith("createUser:")) {
            final InputAndViolations<CreateUserBackingBean> inputAndViolations = filteredRequest.read(new CreateUserBackingBean());
            LOGGER.info("inputAndViolations = " + inputAndViolations);
        } else {
            response.setStatus(SC_NOT_FOUND);
            return;
        }

        final String url = response.encodeRedirectURL("/UsersServlet");
        response.sendRedirect(url);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            final List<User> users = new UserFacade().findAll();
            writeResponse(request, response, UsersBackingBean.create(users));
        } catch (WebApplicationException e) {
            response.setStatus(e.getResponse().getStatus());
            response.getWriter().write(e.getMessage());
        }
    }

}
