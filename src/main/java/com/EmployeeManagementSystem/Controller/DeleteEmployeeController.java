package com.EmployeeManagementSystem.Controller;

import com.EmployeeManagementSystem.Service.DeleteEmployeeService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class DeleteEmployeeController {
    private final DeleteEmployeeService deleteEmployeeService;

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable @Min(1) Long employeeId) {
        log.info("REST request to delete employee ID: {}", employeeId);
        deleteEmployeeService.deleteEmployee(employeeId);
        log.info("Successfully deleted employee ID: {}", employeeId);
        return ResponseEntity.noContent().build();
    }
}

