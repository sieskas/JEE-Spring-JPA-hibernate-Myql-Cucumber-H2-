package com.example.demo.app.v1.servlet;


import com.example.demo.app.v1.mapper.UserMapper;
import com.example.demo.app.v1.resources.UserResource;
import com.example.demo.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "UserCreateServlet", value = "/create-user")
public class UserCreateServlet extends HttpServlet {

    @Inject
    private UserService userService;
    @Inject
    private UserMapper userMapper;

    private final Logger logger = LogManager.getLogger(UserCreateServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Get the name and email from the form data
            List<UserResource> userResourceList = (List<UserResource>) request.getSession().getAttribute("userList");

            String name = request.getParameter("name");
            String email = request.getParameter("email");

            // Create a new user with the name and email
            UserResource userResource = new UserResource(name, email);
            userService.saveUser(userMapper.resourceToDomain(userResource));

            // Add the user to the list of users
            userResourceList.add(userResource);

            // Redirect to the user list page with a success message
            HttpSession session = request.getSession();
            request.getSession().setAttribute("userList", userResourceList);
            session.setAttribute("successMessage", "User created successfully.");
            response.sendRedirect(request.getContextPath() + "/user-list");
        } catch (IOException e) {
            logger.info(e);
        }
    }
}