package com.EmployeeManagementSystem.Controller;

import com.EmployeeManagementSystem.DTO.DynamicSalaryRequestDTO;
import com.EmployeeManagementSystem.DTO.DynamicSalaryResponseDTO;
import com.EmployeeManagementSystem.Service.SalaryCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/employee")
public class SalaryCalculationController {

    @Autowired
    private SalaryCalculationService salaryCalculationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/dynamic-salary")
    public ResponseEntity<DynamicSalaryResponseDTO> getDynamicSalary(
            @RequestBody DynamicSalaryRequestDTO request) {
        log.info("REST request to calculate dynamic salary for employee ID: {}", request.getEmployeeId());
        DynamicSalaryResponseDTO response = salaryCalculationService.calculateDynamicSalary(
                request.getEmployeeId(),
                request.getPercentage()
        );
        log.info("Successfully calculated dynamic salary for employee ID: {}", request.getEmployeeId());
        return ResponseEntity.ok(response);
    }
}

