package com.example.demo.app.v1.config;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    public static final String TEST_AUTHENTICATION_HEADER = "X-Test-Authentication-Token";
    public static final String TEST_AUTHENTICATION_TOKEN = "asdsad5asdw5yuukmy[p[;l;l;[dawwadcada4d1ad1ad3a85w2@e2dqqd@@#$%$%@#$!sda6da1dw1222d2";

    @Override
    public void init(FilterConfig filterConfig) {
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String testAuthenticationToken = httpRequest.getHeader(TEST_AUTHENTICATION_HEADER);

        if (TEST_AUTHENTICATION_TOKEN.equals(testAuthenticationToken)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String indexURI = httpRequest.getContextPath() + "/index.jsp";
        String loginURI = httpRequest.getContextPath() + "/login";
        String rootURI = httpRequest.getContextPath() + "/";

        boolean loggedIn = session != null && session.getAttribute("email") != null;
        boolean indexRequest = httpRequest.getRequestURI().equals(indexURI);
        boolean loginRequest = httpRequest.getRequestURI().equals(loginURI);
        boolean rootRequest = httpRequest.getRequestURI().equals(rootURI);

        if (loggedIn || indexRequest || loginRequest || rootRequest) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(indexURI);
        }
    }

    @Override
    public void destroy() {
        //Filter.super.destroy();
    }
}
