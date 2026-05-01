package com.EmployeeManagementSystem.Service.SeviceImpl;

import com.EmployeeManagementSystem.DTO.UserDTO;
import com.EmployeeManagementSystem.Entity.User;
import com.EmployeeManagementSystem.Exceptions.UserNotFoundException;
import com.EmployeeManagementSystem.Repository.UserRepository;
import com.EmployeeManagementSystem.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @CacheEvict(value = "employeeAuth", key = "#userDTO.email")
    public User registerUser(UserDTO userDTO) {
        log.info("Processing registration for email: {}", userDTO.getEmail());
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            log.warn("Registration failed: Email {} already exists", userDTO.getEmail());
            throw new RuntimeException("User with this email already exists!");
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEnabled(userDTO.isEnabled());
        user.setRole(userDTO.getRole());
        
        User savedUser = userRepository.save(user);
        log.info("User successfully registered with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    @Cacheable(value = "employeeAuth", key = "#email")
    public User findByEmail(String email) {
        log.debug("Fetching user details for email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });
    }
}

