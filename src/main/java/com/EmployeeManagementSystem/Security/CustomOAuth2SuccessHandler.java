package com.EmployeeManagementSystem.Security;

import com.EmployeeManagementSystem.Entity.User;
import com.EmployeeManagementSystem.Enum.Role;
import com.EmployeeManagementSystem.Repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        User dbUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not registered"));

        if (dbUser.getRole().equals(Role.ADMIN)) {
            response.sendRedirect("/admin.html");
        } else {
            response.sendRedirect("/employee.html");
        }
    }
}
