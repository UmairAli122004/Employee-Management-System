package com.EmployeeManagementSystem.Mapper;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.Entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class MapperClassToDTO {
    public EmployeeDTO entityToDTO(Employee employee){
        EmployeeDTO employeeDTO = new EmployeeDTO();

        employeeDTO.setId(employee.getId());
        employeeDTO.setFirstName(employee.getFirstName());
        employeeDTO.setLastName(employee.getLastName());
        employeeDTO.setAddress(employee.getAddress());
        employeeDTO.setPhoneNumber(employee.getPhoneNumber());
        employeeDTO.setSalary(employee.getSalary());
        employeeDTO.setJoiningDateTime(employee.getJoiningDateTime());
        employeeDTO.setActive(employee.getActive());
        employeeDTO.setDepartmentId(employee.getDepartment().getId());
        employeeDTO.setDepartmentName(employee.getDepartment().getDepartment_name());

        return employeeDTO;
    }

}
