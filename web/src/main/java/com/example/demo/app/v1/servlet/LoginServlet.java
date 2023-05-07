package com.example.demo.app.v1.servlet;

import com.example.demo.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends SpringInjectedHttpServlet {
    private final Logger logger = LogManager.getLogger(LoginServlet.class);

    @Autowired
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            boolean isAuthenticated = userService.authenticateUser(email, password);

            if (isAuthenticated) {
                HttpSession session = request.getSession();
                session.setAttribute("email", email);

                // Redirigez l'utilisateur vers une page protégée ou la page d'accueil de votre application.
                response.sendRedirect(request.getContextPath() + "/user-list");
            } else {
                // Redirigez l'utilisateur vers la page de connexion avec un message d'erreur.
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=1");
            }
        } catch (IOException e) {
            logger.info(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.getWriter().println("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Login</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Login</h1>\n" +
                "<form method=\"post\" action=\"" + request.getContextPath() + "/login\">\n" +
                "    <label for=\"email\">Email:</label>\n" +
                "    <input type=\"email\" id=\"email\" name=\"email\" required><br>\n" +
                "    <label for=\"password\">Password:</label>\n" +
                "    <input type=\"password\" id=\"password\" name=\"password\" required><br>\n" +
                "    <input type=\"submit\" value=\"Login\">\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>");
    }


}
