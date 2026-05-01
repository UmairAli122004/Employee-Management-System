package com.EmployeeManagementSystem.DTO;

import lombok.Data;

@Data
public class DynamicSalaryRequestDTO {
    private Long employeeId;
    private double percentage;
}
