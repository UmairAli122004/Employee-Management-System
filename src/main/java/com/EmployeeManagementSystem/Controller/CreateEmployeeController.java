package com.EmployeeManagementSystem.Controller;

import com.EmployeeManagementSystem.DTO.ApiResponse;
import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.Entity.Employee;
import com.EmployeeManagementSystem.Mapper.MapperClassToDTO;
import com.EmployeeManagementSystem.Mapper.ResponseUtil;
import com.EmployeeManagementSystem.Service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class CreateEmployeeController {
    private final EmployeeService employeeService;
    private final MapperClassToDTO mapperClassToDTO;

    @Value("${operation.successful}")
    private String EMPLOYEE_CREATED;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addEmployee")
    public ResponseEntity<ApiResponse<EmployeeDTO>> addEmployee(@Valid @RequestBody EmployeeDTO employeeDTO){
        log.info("REST request to add employee with phone: {}", employeeDTO.getPhoneNumber());
        Employee savedEmployee = employeeService.addEmployee(employeeDTO);
        EmployeeDTO responseDTO = mapperClassToDTO.entityToDTO(savedEmployee);
        log.info("Successfully created employee with ID: {}", savedEmployee.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(EMPLOYEE_CREATED, responseDTO));
    }
}

