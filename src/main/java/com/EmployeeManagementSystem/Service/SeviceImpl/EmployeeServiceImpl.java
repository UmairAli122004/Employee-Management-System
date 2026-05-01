package com.EmployeeManagementSystem.Service.SeviceImpl;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.Entity.Department;
import com.EmployeeManagementSystem.Entity.Employee;
import com.EmployeeManagementSystem.Exceptions.EmployeeAlreadyExistException;
import com.EmployeeManagementSystem.Exceptions.SomeThingWentWrong;
import com.EmployeeManagementSystem.Exceptions.UserNotFoundException;
import com.EmployeeManagementSystem.Entity.User;
import com.EmployeeManagementSystem.Security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import com.EmployeeManagementSystem.Mapper.MapperClassToDTO;
import com.EmployeeManagementSystem.Mapper.MapperClassToEntity;
import com.EmployeeManagementSystem.Repository.DepartmentRepository;
import com.EmployeeManagementSystem.Repository.EmployeeRepository;
import com.EmployeeManagementSystem.Repository.UserRepository;
import com.EmployeeManagementSystem.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "employeeById")
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final MapperClassToEntity mapperClassToEntity;
    private final MapperClassToDTO mapperClassToDTO;

    @Value("${employee.not.exist}")
    private String employeeNotExist;

    @Value("${employee.already.exist.with.email}")
    private String employeeAlreadyExistWithEmail;

    @Value("${employee.check.id:You are not authorized to view employee %d}")
    private String checkEmployeeId;

    @Value("${employee.already.exist.with.contact}")
    private String employeeAlreadyExistWithContact;

    @Caching(evict = {
            @CacheEvict(value = "employeeList", allEntries = true),
            @CacheEvict(value = "employeePage", allEntries = true),
            @CacheEvict(value = "employeeSearchPage", allEntries = true),
            @CacheEvict(value = "employeeSearchList", allEntries = true),
            @CacheEvict(value = "employeeFilter", allEntries = true),
            @CacheEvict(value = "employeeSummary", allEntries = true),
            @CacheEvict(value = "employeeAuth", allEntries = true)
    })
    @Override
    public Employee addEmployee(EmployeeDTO employeeDTO) {
        log.info("Attempting to add employee with phone number: {}", employeeDTO.getPhoneNumber());
        if (employeeRepository.existsByPhoneNumber(employeeDTO.getPhoneNumber())) {
            log.warn("Failed to add employee: Phone number {} already exists", employeeDTO.getPhoneNumber());
            throw new EmployeeAlreadyExistException(
                    String.format(employeeAlreadyExistWithContact, employeeDTO.getPhoneNumber()));
        }
        Department department = departmentRepository.findById(employeeDTO.getDepartmentId())
                .orElseThrow(() -> {
                    log.warn("Failed to add employee: Department ID {} not found", employeeDTO.getDepartmentId());
                    return new RuntimeException("Department not found");
                });
                
        User user = userRepository.findById(employeeDTO.getUserId())
                .orElseThrow(() -> {
                    log.warn("Failed to add employee: User ID {} not found", employeeDTO.getUserId());
                    return new UserNotFoundException("User not found with ID: " + employeeDTO.getUserId());
                });
                
        if (user.getEmployee() != null) {
            log.warn("Failed to add employee: User ID {} is already linked to an employee profile", employeeDTO.getUserId());
            throw new RuntimeException("User is already linked to an employee profile.");
        }
        Employee employee = mapperClassToEntity.dtoToEntity(employeeDTO, department);
        
        employee.setUser(user);

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee successfully added with ID: {}", savedEmployee.getId());
        return savedEmployee;
    }


    @Cacheable(value = "employeeById", key = "#employeeId")
    @Override
    public EmployeeDTO findEmployeeById(Long employeeId) {
        log.debug("Fetching employee with ID: {}", employeeId);
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String email;
        boolean isAdmin;

        if (principal instanceof CustomUserDetails userDetails) {
            email = userDetails.getUsername();
            isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        } else if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oauthUser) {
            email = oauthUser.getAttribute("email");
            // Look up the user from DB to check role
            User dbUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("Authentication failure: OAuth2 user {} not found in database", email);
                        return new UserNotFoundException("User not found: " + email);
                    });
            isAdmin = dbUser.getRole() == com.EmployeeManagementSystem.Enum.Role.ADMIN;
        } else {
            log.warn("Authentication failure: Unknown principal type {}", principal.getClass().getName());
            throw new RuntimeException("Unknown authentication type");
        }

        Employee employee;

        if (isAdmin) {
            log.debug("Admin user {} fetching employee ID {}", email, employeeId);
            employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> {
                        log.warn("Employee ID {} not found for admin {}", employeeId, email);
                        return new SomeThingWentWrong(String.format(checkEmployeeId, employeeId));
                    });

        } else {
            log.debug("Regular user {} fetching employee ID {}", email, employeeId);
            employee = employeeRepository.findByIdAndUserEmail(employeeId, email)
                    .orElseThrow(() -> {
                        log.warn("Employee ID {} not found or access denied for user {}", employeeId, email);
                        return new SomeThingWentWrong(String.format(checkEmployeeId, employeeId));
                    });
        }

        return mapperClassToDTO.entityToDTO(employee);
    }
}