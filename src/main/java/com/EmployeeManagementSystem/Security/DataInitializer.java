package com.EmployeeManagementSystem.Security;

import com.EmployeeManagementSystem.Entity.User;
import com.EmployeeManagementSystem.Enum.Role;
import com.EmployeeManagementSystem.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer seeds a default ADMIN user on every application startup
 * if no admin user already exists in the database.
 *
 * Configure the admin credentials in application.properties:
 *   app.admin.email=admin@company.com
 *   app.admin.password=Admin@123
 *
 * This approach ensures:
 *  - The registration form never exposes a "role" field to end-users.
 *  - Admin creation is controlled at the infrastructure/config level.
 *  - Production admins can be rotated by changing the property and restarting.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@ems.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@1234}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            log.info("Default ADMIN user created: {}", adminEmail);
        } else {
            //Always sync the password from config so it stays current
            User existingAdmin = userRepository.findByEmail(adminEmail).orElse(null);
            if (existingAdmin != null) {
                existingAdmin.setPassword(passwordEncoder.encode(adminPassword));
                existingAdmin.setRole(Role.ADMIN);
                existingAdmin.setEnabled(true);
                userRepository.save(existingAdmin);
                log.info("ℹ️  Admin user password synced: {}", adminEmail);
            }
        }
    }
}
