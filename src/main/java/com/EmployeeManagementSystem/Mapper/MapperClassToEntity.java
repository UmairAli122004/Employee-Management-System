package com.EmployeeManagementSystem.Mapper;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.Entity.Department;
import com.EmployeeManagementSystem.Entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MapperClassToEntity {

    public Employee dtoToEntity(EmployeeDTO dto, Department department){
        Employee employee = new Employee();

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setAddress(dto.getAddress());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setSalary(dto.getSalary());
        employee.setActive(dto.getActive());
        employee.setDepartment(department);

        return employee;
    }
}