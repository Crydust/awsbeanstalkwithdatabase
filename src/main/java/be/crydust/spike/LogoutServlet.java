package be.crydust.spike;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    private static void writeResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.logout();
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/"));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        writeResponse(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        writeResponse(request, response);
    }
}
