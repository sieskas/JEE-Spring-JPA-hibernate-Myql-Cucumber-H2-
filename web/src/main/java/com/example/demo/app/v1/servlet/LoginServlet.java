package com.example.demo.app.v1.servlet;

import com.example.demo.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
    private final Logger logger = LogManager.getLogger(LoginServlet.class);

    @Inject
    private UserService userService;

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

}
