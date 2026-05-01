package com.EmployeeManagementSystem.Service.SeviceImpl;

import com.EmployeeManagementSystem.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

        OAuth2User oauthUser = super.loadUser(request);

        String email = oauthUser.getAttribute("email");

        //DO NOT auto-register — only allow pre-registered users
        userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new OAuth2AuthenticationException(
                        "User not registered. Please register first before using Google login."
                    )
                );

        return oauthUser;
    }
}