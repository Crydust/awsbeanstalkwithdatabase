package be.crydust.spike;

import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;

@WebServlet(name = "UsersServlet", urlPatterns = {"/UsersServlet"})
@ServletSecurity(@HttpConstraint(rolesAllowed={"admin"}))
public class UsersServlet extends HttpServlet {

    private static void writeResponse(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.getRequestDispatcher("/WEB-INF/jsp/users.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(SC_METHOD_NOT_ALLOWED);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        writeResponse(request, response);
    }
}
