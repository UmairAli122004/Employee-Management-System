package com.EmployeeManagementSystem.DTO;

import com.EmployeeManagementSystem.Enum.EmployeeStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmployeeDTO {

    private Long id;

    @NotNull(message = "User ID is required to link employee to a user")
    private Long userId;

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(\\+91[-\\s]?)?[6-9]\\d{9}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be greater than 0")
    private Double salary;

    private LocalDateTime joiningDateTime;

    @NotNull(message = "Active status is required")
    private EmployeeStatus active;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private String departmentName;
}