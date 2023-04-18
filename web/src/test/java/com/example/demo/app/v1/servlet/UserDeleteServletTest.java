package com.example.demo.app.v1.servlet;

import com.example.demo.app.v1.mapper.UserMapper;
import com.example.demo.app.v1.resources.UserResource;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDeleteServletTest {

    @InjectMocks
    private UserDeleteServlet userDeleteServlet;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private String email;
    private List<UserResource> userResourceList;
    @BeforeEach
    void init() {
        email = "test@example.com";
        User user = User.builder().email(email).build();
        UserResource userResource = UserResource.builder().email("test@example.com").build();

        userResourceList = new ArrayList<>();
        userResourceList.add(userResource);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userList")).thenReturn(userResourceList);
        when(request.getParameter("email")).thenReturn(email);
        when(userService.deleteUserByEmail(email)).thenReturn(user);
        when(userMapper.domainToResource(user)).thenReturn(userResource);
        when(request.getContextPath()).thenReturn("");
    }

    @Test
    void doPost() throws Exception {
        userDeleteServlet.doPost(request, response);

        verify(userService).deleteUserByEmail(email);
        verify(session).setAttribute("userList", userResourceList);
        verify(session).setAttribute("successMessage", "User deleted successfully.");
        verify(response).sendRedirect("/user-list");
    }

    @Test
    void doPost_catch() throws Exception {
        doThrow(IOException.class).when(response).sendRedirect("/user-list");
        assertDoesNotThrow(() -> userDeleteServlet.doPost(request, response));
    }


}