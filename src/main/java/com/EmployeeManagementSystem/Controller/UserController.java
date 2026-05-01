package com.EmployeeManagementSystem.Controller;

import com.EmployeeManagementSystem.DTO.ApiResponse;
import com.EmployeeManagementSystem.DTO.LoginDTO;
import com.EmployeeManagementSystem.DTO.UserDTO;
import com.EmployeeManagementSystem.Entity.User;
import com.EmployeeManagementSystem.Mapper.ResponseUtil;
import com.EmployeeManagementSystem.Security.CustomUserDetails;
import com.EmployeeManagementSystem.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;  //AuthenticationManager is the main component responsible for authentication in Spring Security.
                                                                //AuthenticationManager - Security Guard

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("Attempting to register a new user with email: {}", userDTO.getEmail());
        
        User registeredUser = userService.registerUser(userDTO);

        log.info("User successfully registered with ID: {}", registeredUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success("User registered successfully", registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginUser(
            @Valid @RequestBody LoginDTO loginDTO,
            HttpServletRequest request) {
        log.info("REST request to login user: {}", loginDTO.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication); //Store authenticated user inside context
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        User user = userService.findByEmail(loginDTO.getEmail());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("email", user.getEmail());
        responseData.put("role", "ROLE_" + user.getRole().name());
        responseData.put("id", user.getId());

        // Include linked employee ID if one exists
        if (user.getEmployee() != null) {
            responseData.put("employeeId", user.getEmployee().getId());
        }

        log.info("User {} successfully logged in", loginDTO.getEmail());
        return ResponseEntity.ok(ResponseUtil.success("User logged in successfully", responseData));
    }

    /**
     * Returns the currently authenticated user's info.
     * Used by the frontend to verify auth status and get role/employeeId.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        log.debug("REST request to get current authenticated user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            log.debug("User is not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseUtil.error("UNAUTHORIZED", "Not authenticated"));
        }

        Map<String, Object> data = new HashMap<>();
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            User user = userDetails.getUser();
            data.put("email", user.getEmail());
            data.put("role", "ROLE_" + user.getRole().name());
            data.put("id", user.getId());
            if (user.getEmployee() != null) {
                data.put("employeeId", user.getEmployee().getId());
            }
            log.debug("Found custom user details for: {}", user.getEmail());
        } else if (principal instanceof OAuth2User oauthUser) {

            String email = oauthUser.getAttribute("email");

            User user = userService.findByEmail(email);

            data.put("email", user.getEmail());
            data.put("role", "ROLE_" + user.getRole().name());
            data.put("id", user.getId());
            if (user.getEmployee() != null) {
                data.put("employeeId", user.getEmployee().getId());
            }
            log.debug("Found OAuth2 user details for: {}", email);
        }

        return ResponseEntity.ok(ResponseUtil.success("Authenticated", data));
    }

}
