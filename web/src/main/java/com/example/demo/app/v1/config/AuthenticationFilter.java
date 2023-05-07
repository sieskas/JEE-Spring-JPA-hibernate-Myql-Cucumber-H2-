package com.example.demo.app.v1.config;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
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
