package com.EmployeeManagementSystem.Service;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.DTO.EmployeeDTOProjection;

import java.util.List;

public interface FilterEmployeeService {
    List<EmployeeDTO> findEmployeeByDepartmentName(String department_name);
    List<EmployeeDTO> findEmployeeByDepartmentId(Long id);
    List<EmployeeDTOProjection> findEmployeeNameAndDepartment();
    List<EmployeeDTO> showEmployeeDetails();
}
