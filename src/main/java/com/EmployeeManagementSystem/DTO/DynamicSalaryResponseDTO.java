package com.EmployeeManagementSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DynamicSalaryResponseDTO {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String departmentName;
    private Double baseSalary;
    private Double percentageApplied;
    private Double calculatedSalary;
}
