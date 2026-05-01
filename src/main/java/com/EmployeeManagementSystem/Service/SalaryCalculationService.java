package com.EmployeeManagementSystem.Service;

import com.EmployeeManagementSystem.DTO.DynamicSalaryResponseDTO;

public interface SalaryCalculationService {
    DynamicSalaryResponseDTO calculateDynamicSalary(Long employeeId, double percentage);
}
