package com.example.demo.app.v1.servlet;

import com.example.demo.app.v1.mapper.UserMapper;
import com.example.demo.app.v1.resources.UserResource;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "DeleteUserServlet", value = "/delete-user")
public class UserDeleteServlet extends SpringInjectedHttpServlet {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    private final Logger logger = LogManager.getLogger(UserDeleteServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Get the name and email from the form data
        try {
            String email = request.getParameter("email");

            userService.deleteUserByEmail(email);
            //session.setAttribute("successMessage", "User deleted successfully.");
            response.sendRedirect(request.getContextPath() + "/user-list");
        } catch (IOException e) {
            logger.info(e);
        }
    }
}
