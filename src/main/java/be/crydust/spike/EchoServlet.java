package be.crydust.spike;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@WebServlet(name = "EchoServlet", urlPatterns = {"/EchoServlet"})
public class EchoServlet extends HttpServlet {

    private static void writeResponse(HttpServletRequest request, HttpServletResponse response, String name) throws IOException, ServletException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setAttribute("name", name);
        request.getRequestDispatcher("/WEB-INF/jsp/echo.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final String name = Collections.list(request.getParameterNames()).contains("name")
                ? request.getParameter("name")
                : "";
        writeResponse(request, response, name);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        writeResponse(request, response, "");
    }
}
