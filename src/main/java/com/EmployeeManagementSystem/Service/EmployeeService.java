package com.EmployeeManagementSystem.Service;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.Entity.Employee;

public interface EmployeeService {
    Employee addEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO findEmployeeById(Long employeeId);

}
