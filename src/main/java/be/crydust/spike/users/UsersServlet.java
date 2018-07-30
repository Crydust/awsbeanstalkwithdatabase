package be.crydust.spike.users;

import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
            final InputAndViolations<DeleteUserRoleInputModel> inputAndViolations = filteredRequest.read(new DeleteUserRoleInputModel());
            LOGGER.info("inputAndViolations = " + inputAndViolations);
        } else if (button.startsWith("addRoleToUser:")) {
            final InputAndViolations<AddRoleToUserInputModel> inputAndViolations = filteredRequest.read(new AddRoleToUserInputModel());
            LOGGER.info("inputAndViolations = " + inputAndViolations);
        } else if (button.startsWith("removeUser:")) {
            final InputAndViolations<RemoveUserInputModel> inputAndViolations = filteredRequest.read(new RemoveUserInputModel());
            LOGGER.info("inputAndViolations = " + inputAndViolations);
        } else if (button.startsWith("createUser:")) {
            final InputAndViolations<CreateUserInputModel> inputAndViolations = filteredRequest.read(new CreateUserInputModel());
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
            final DataSource ds = RepositoryCommons.lookupDataSource();
            final UserRepository repository = new UserRepository(ds);
            final List<User> users = repository.findAll();
            writeResponse(request, response, UsersViewModel.createSuccess(users));
        } catch (RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Could not load users.", e);
            writeResponse(request, response, UsersViewModel.createError("Could not load users."));
        }

    }

    public static class DeleteUserRoleInputModel {
        @NotBlank
        @Size(min = 1, max = 64)
        private String name;
        @NotBlank
        @Size(min = 1, max = 64)
        private String role;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        @Override
        public String toString() {
            return "DeleteUserRoleInputModel{" +
                    "name='" + name + '\'' +
                    ", role='" + role + '\'' +
                    '}';
        }
    }

    public static class AddRoleToUserInputModel {
        @NotBlank
        @Size(min = 1, max = 64)
        private String name;
        @NotBlank
        @Size(min = 1, max = 64)
        private String role;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class RemoveUserInputModel {
        @NotBlank
        @Size(min = 1, max = 64)
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class CreateUserInputModel {
        @NotBlank
        @Size(min = 1, max = 64)
        private String name;
        @NotBlank
        private String pasword;
        @NotBlank
        @Size(min = 1, max = 64)
        private String role;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPasword() {
            return pasword;
        }

        public void setPasword(String pasword) {
            this.pasword = pasword;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
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

//        String getParameter(String name) {
//            final String[] values = parameterMap.get(name);
//            if (values == null || values.length == 0) {
//                return null;
//            }
//            return values[0];
//        }
//
//        String[] getParameterValues(String name) {
//            return parameterMap.get(name);
//        }
//
//        Map<String, String[]> getParameterMap() {
//            return parameterMap;
//        }
//
//        Enumeration<String> getParameterNames() {
//            return Collections.enumeration(parameterMap.keySet());
//        }

        <T> InputAndViolations<T> read(T input) {
            try {
                BeanUtils.populate(input, parameterMap);
                final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                final Validator validator = factory.getValidator();
                Set<ConstraintViolation<T>> violations = validator.validate(input);
                return new InputAndViolations<>(input, violations);
            } catch (IllegalAccessException | InvocationTargetException | ValidationException | IllegalArgumentException e) {
                e.printStackTrace();
            }
            return new InputAndViolations<>(input, Collections.emptySet());
        }
    }

    private static class InputAndViolations<T> {
        private final T input;
        private final Set<ConstraintViolation<T>> violations;

        private InputAndViolations(T input, Set<ConstraintViolation<T>> violations) {
            this.input = input;
            this.violations = violations;
        }

        public T getInput() {
            return input;
        }

        public Set<ConstraintViolation<T>> getViolations() {
            return violations;
        }

        @Override
        public String toString() {
            return "InputAndViolations{" +
                    "input=" + input +
                    ", violations=" + violations +
                    '}';
        }
    }

}
