package be.crydust.spike;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;

@WebServlet(name = "WhoamiServlet", urlPatterns = {"/WhoamiServlet"})
public class WhoamiServlet extends HttpServlet {

    private static void writeResponse(HttpServletRequest request, HttpServletResponse response, String name, List<String> roles) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setAttribute("login", name);
        request.setAttribute("roles", roles);
        request.getRequestDispatcher("/WEB-INF/jsp/whoami.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final Principal principal = request.getUserPrincipal();
        final String name = principal == null ? "anonymous" : principal.getName();
        final List<String> roles = new ArrayList<>();
        if (request.isUserInRole("admin")) {
            roles.add("admin");
        }
        if (request.isUserInRole("monitoring")) {
            roles.add("monitoring");
        }

        writeResponse(request, response, name, roles);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(SC_METHOD_NOT_ALLOWED);
    }
}
