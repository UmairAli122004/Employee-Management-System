package com.EmployeeManagementSystem.Controller;

import com.EmployeeManagementSystem.DTO.UpdateEmployeeDTO;
import com.EmployeeManagementSystem.Service.UpdateEmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class UpdateEmployeeController {
    private final UpdateEmployeeService updateEmployeeService;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<Void> updateEmployee(@Valid @RequestBody UpdateEmployeeDTO updateEmployeeDTO) {
        log.info("REST request to update employee ID: {}", updateEmployeeDTO.getEmployeeId());
        updateEmployeeService.UpdateEmployee(updateEmployeeDTO);
        log.info("Successfully updated employee ID: {}", updateEmployeeDTO.getEmployeeId());
        return ResponseEntity.noContent().build();
    }
}

