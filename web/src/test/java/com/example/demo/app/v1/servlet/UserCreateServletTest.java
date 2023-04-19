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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCreateServletTest {

    @InjectMocks
    private UserCreateServlet userCreateServlet;

    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private User user;
    private final String PASSWORD = "password";

    @BeforeEach
    void setUp() {
        String name = "Test User";
        String email = "test@example.com";

        user = User.builder()
                .username(name)
                .email(email)
                .build();

        when(request.getParameter(any())).thenReturn(name);
        when(request.getParameter(any())).thenReturn(email);
        when(request.getParameter(any())).thenReturn(PASSWORD);

        when(userMapper.resourceToDomain(any())).thenReturn(user);
        when(request.getContextPath()).thenReturn("");
    }

    @Test
    void doPost() throws Exception {
        userCreateServlet.doPost(request, response);

        verify(userService).saveUser(user, PASSWORD);
        verify(userMapper).resourceToDomain(any());
        verify(response).sendRedirect("/user-list");
    }

    @Test
    void doPost_catch() throws Exception {
        doThrow(IOException.class).when(response).sendRedirect("/user-list");
        assertDoesNotThrow(() -> userCreateServlet.doPost(request, response));
    }

}