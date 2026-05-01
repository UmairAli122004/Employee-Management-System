package com.EmployeeManagementSystem.Controller;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.DTO.EmployeeDTOProjection;
import com.EmployeeManagementSystem.Service.EmployeeService;
import com.EmployeeManagementSystem.Service.FilterEmployeeService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class FilterEmployeeController {

    private final FilterEmployeeService filterEmployeeService;
    private final EmployeeService employeeService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/employeeId")
    public ResponseEntity<EmployeeDTO> findEmployeeById(@RequestParam @Min(1) Long employeeId){
        log.info("REST request to find employee by ID: {}", employeeId);
        EmployeeDTO employeeDTO = employeeService.findEmployeeById(employeeId);
        log.info("Successfully found employee by ID: {}", employeeId);
        return ResponseEntity.ok(employeeDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/employeeId/{employeeId}")
    public ResponseEntity<EmployeeDTO> findEmployeeByIdUsingPathVariable(@PathVariable @Min(1) Long employeeId){
        log.info("REST request to find employee by ID (path variable): {}", employeeId);
        EmployeeDTO employeeDTO = employeeService.findEmployeeById(employeeId);
        log.info("Successfully found employee by ID (path variable): {}", employeeId);
        return ResponseEntity.ok(employeeDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/filter")
    public ResponseEntity<List<EmployeeDTO>> filterEmployeeByDepartmentName(
            @RequestParam(value = "department_name", defaultValue = "IT") String department_name) {
        log.info("REST request to filter employees by department name: {}", department_name);
        List<EmployeeDTO> employees = filterEmployeeService.findEmployeeByDepartmentName(department_name);
        log.info("Successfully filtered employees by department name: {}, count: {}", department_name, employees.size());
        return ResponseEntity.ok(employees);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/filterByDepartmentId")
    public ResponseEntity<List<EmployeeDTO>> findEmployeeByDepartmentId(@RequestParam(value="id", defaultValue = "1") @Min(1) Long id){
        log.info("REST request to filter employees by department ID: {}", id);
        List<EmployeeDTO> employees = filterEmployeeService.findEmployeeByDepartmentId(id);
        log.info("Successfully filtered employees by department ID: {}, count: {}", id, employees.size());
        return ResponseEntity.ok(employees);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/filterByEmployeeDTOProjection")
    public ResponseEntity<List<EmployeeDTOProjection>> findEmployeeNameAndDepartment(){
        log.info("REST request to find employee names and departments (projection)");
        List<EmployeeDTOProjection> projections = filterEmployeeService.findEmployeeNameAndDepartment();
        log.info("Successfully retrieved employee name and department projections, count: {}", projections.size());
        return ResponseEntity.ok(projections);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allEmployee")
    public ResponseEntity<List<EmployeeDTO>> showEmployeeDetails(){
        log.info("REST request to show all employee details");
        List<EmployeeDTO> employees = filterEmployeeService.showEmployeeDetails();
        log.info("Successfully retrieved all employee details, count: {}", employees.size());
        return ResponseEntity.ok(employees);
    }
}

