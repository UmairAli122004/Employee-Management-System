package com.EmployeeManagementSystem.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // 1. Log the actual error
        //System.out.println("OAuth2 Login Failed: " + exception.getMessage());

        // 2. Store error in session
        //request.getSession().setAttribute("error_message", exception.getMessage());

        response.sendRedirect("/login.html?error=true");
    }
}