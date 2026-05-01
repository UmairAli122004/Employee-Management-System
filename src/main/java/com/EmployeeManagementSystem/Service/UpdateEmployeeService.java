package com.EmployeeManagementSystem.Service;

import com.EmployeeManagementSystem.DTO.UpdateEmployeeDTO;
import com.EmployeeManagementSystem.Entity.Employee;

public interface UpdateEmployeeService {
    Employee UpdateEmployee(UpdateEmployeeDTO employeeDTO);
}
