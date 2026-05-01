package com.EmployeeManagementSystem.Security;
import com.EmployeeManagementSystem.Service.SeviceImpl.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomEmployeeDetailsService userDetailsService;
        private final CustomOAuth2UserService customOAuth2UserService;
        private final CustomOAuth2SuccessHandler successHandler;
        private final CustomOAuth2FailureHandler failureHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                        .csrf(csrf -> csrf.disable())
                        .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                        "/user/register", "/user/login", "/", "/*.html", "/css/**", "/js/**", "/images/**", "/favicon.ico",
                                        "/oauth2/**",
                                        "/login/**",
                                        "/login"
                                ).permitAll()
                                .requestMatchers("/employee/dynamic-salary").hasRole("ADMIN")
                                .anyRequest().authenticated())

                        .exceptionHandling(ex -> ex
                                .authenticationEntryPoint(jsonAuthEntryPoint()))

                        .httpBasic(basic -> basic.disable())

                        .oauth2Login(oauth -> oauth
                                .loginPage("/login")
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService)
                                )
                                .successHandler(successHandler)
                                .failureHandler(failureHandler)
                        )
                        .logout(logout -> logout
                                .logoutSuccessUrl("/login.html")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .deleteCookies("JSESSIONID")
                        )
                        .build();
        }

        @Bean
        public AuthenticationEntryPoint jsonAuthEntryPoint() {
                return (request, response, authException) -> {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.getWriter().write(
                                "{\"status\":false,\"message\":\"Invalid credentials. Please register first.\",\"data\":null}"
                        );
                };
        }


        @Bean
        public AuthenticationManager authenticationManager() {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
                provider.setUserDetailsService(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder());
                return new ProviderManager(provider);
        }


        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
