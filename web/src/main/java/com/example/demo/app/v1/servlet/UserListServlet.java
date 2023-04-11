package com.example.demo.app.v1.servlet;

import com.example.demo.app.v1.mapper.UserMapper;
import com.example.demo.app.v1.resources.UserResource;
import com.example.demo.service.UserService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "user-list", value = "/user-list")
public class UserListServlet extends HttpServlet {
    @Inject
    private UserService userService;
    @Inject
    private UserMapper userMapper;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        List<UserResource> userResourceList = (List<UserResource>) session.getAttribute("userList");

        if (userResourceList == null) {
            userResourceList = userMapper.domainToResource(userService.getUsers());
            session.setAttribute("userList", userResourceList);
        }
        // Set the user list as an attribute of the request
        request.setAttribute("userList", userResourceList);

        // Forward to the JSP page
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}