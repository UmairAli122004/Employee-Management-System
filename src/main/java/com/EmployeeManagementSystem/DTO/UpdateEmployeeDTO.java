package com.EmployeeManagementSystem.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateEmployeeDTO {

    @NotNull(message = "Employee Id is required")
    private Long employeeId;

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(\\+91[-\\s]?)?[6-9]\\d{9}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;

}