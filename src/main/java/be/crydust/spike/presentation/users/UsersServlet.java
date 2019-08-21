package be.crydust.spike.presentation.users;

import be.crydust.spike.business.users.WebApplicationException;
import be.crydust.spike.business.users.boundary.UserFacade;
import be.crydust.spike.business.users.entity.User;
import be.crydust.spike.presentation.ErrorMessage;
import be.crydust.spike.presentation.FilteredRequest;
import be.crydust.spike.presentation.InputAndErrorMessages;

import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static java.util.Collections.singletonList;
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LOGGER.info("UsersServlet.doPost");
        final String button = request.getParameter("button");
        LOGGER.info("button = " + button);
        if (button == null || button.isEmpty()) {
            response.setStatus(SC_BAD_REQUEST);
            return;
        }
        final FilteredRequest filteredRequest = new FilteredRequest(button, request.getParameterMap());
        if (button.startsWith("deleteUserRole:")) {
            try {
                final InputAndErrorMessages<DeleteUserRoleBackingBean> inputAndErrorMessages = filteredRequest.read(new DeleteUserRoleBackingBean());
                LOGGER.info("inputAndErrorMessages = " + inputAndErrorMessages);
                final UserFacade userFacade = new UserFacade();
                final DeleteUserRoleBackingBean backingBean = inputAndErrorMessages.getInput();
                final List<ErrorMessage> errorMessages = inputAndErrorMessages.getErrorMessages();
                final boolean valid = errorMessages.isEmpty();
                if (valid && userFacade.deleteUserRole(backingBean.getName(), backingBean.getRole())) {
                    final List<User> users = userFacade.findAll();
                    final UsersBackingBean model = UsersBackingBean.create(users, false, singletonList(new ErrorMessage(null, "Successfully removed role from user.")));
                    writeResponse(request, response, model);
                    return;
                } else {
                    final List<User> users = userFacade.findAll();
                    final UsersBackingBean model = UsersBackingBean.create(users, true, errorMessages);
                    writeResponse(request, response, model);
                    return;
                }
            } catch (WebApplicationException e) {
                response.setStatus(500);
                response.getWriter().write(e.getMessage());
            }
        } else if (button.startsWith("addRoleToUser:") || button.startsWith("addRoleToUser[")) {
            try {
                final InputAndErrorMessages<AddRoleToUserBackingBean> inputAndErrorMessages = filteredRequest.read(new AddRoleToUserBackingBean());
                LOGGER.info("inputAndErrorMessages = " + inputAndErrorMessages);
                final UserFacade userFacade = new UserFacade();
                final AddRoleToUserBackingBean backingBean = inputAndErrorMessages.getInput();
                final List<ErrorMessage> errorMessages = inputAndErrorMessages.getErrorMessages();
                final boolean valid = errorMessages.isEmpty();
                if (valid && userFacade.addRoleToUser(backingBean.getName(), backingBean.getRole())) {
                    final List<User> users = userFacade.findAll();
                    final UsersBackingBean model = UsersBackingBean.create(users, false, singletonList(new ErrorMessage(null, "Successfully added role to user.")));
                    writeResponse(request, response, model);
                    return;
                } else {
                    final List<User> users = userFacade.findAll();
                    final UsersBackingBean model = UsersBackingBean.create(users, true, errorMessages);
                    writeResponse(request, response, model);
                    return;
                }
            } catch (WebApplicationException e) {
                response.setStatus(500);
                response.getWriter().write(e.getMessage());
            }
        } else if (button.startsWith("removeUser:")) {
            final InputAndErrorMessages<RemoveUserBackingBean> inputAndErrorMessages = filteredRequest.read(new RemoveUserBackingBean());
            LOGGER.info("inputAndErrorMessages = " + inputAndErrorMessages);
        } else if (button.startsWith("createUser:")) {
            try {
                final InputAndErrorMessages<CreateUserBackingBean> inputAndErrorMessages = filteredRequest.read(new CreateUserBackingBean());
                LOGGER.info("inputAndErrorMessages = " + inputAndErrorMessages);
                final UserFacade userFacade = new UserFacade();
                final CreateUserBackingBean backingBean = inputAndErrorMessages.getInput();
                final List<ErrorMessage> errorMessages = inputAndErrorMessages.getErrorMessages();
                final boolean valid = errorMessages.isEmpty();
                if (valid) {
                    final User user = userFacade.create(backingBean.getName(), backingBean.getPassword(), backingBean.getRole());
                    System.out.println("user = " + user);
                } else {
                    final List<User> users = userFacade.findAll();
                    final UsersBackingBean model = UsersBackingBean.create(
                            users,
                            backingBean,
                            true,
                            errorMessages);
                    writeResponse(request, response, model);
                    return;
                }
            } catch (WebApplicationException e) {
                response.setStatus(500);
                response.getWriter().write(e.getMessage());
            }
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
            response.setStatus(500);
            response.getWriter().write(e.getMessage());
        }
    }

}
