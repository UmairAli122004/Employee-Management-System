package com.EmployeeManagementSystem.Controller;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.DTO.SearchRequestDTO;
import com.EmployeeManagementSystem.Service.EmployeeSpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeSpecificationController {
    private final EmployeeSpecificationService<EmployeeDTO> employeeSpecificationService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/search")
    public ResponseEntity<Page<EmployeeDTO>> search(
            @RequestBody List<SearchRequestDTO> requests,
            @RequestParam(defaultValue = "AND") String condition,
            Pageable pageable) {
        return ResponseEntity.ok(
                employeeSpecificationService.search(requests, condition, pageable)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/searchAsList")
    public ResponseEntity<List<EmployeeDTO>> search(
            @RequestBody List<SearchRequestDTO> requests,
            @RequestParam(defaultValue = "AND") String condition) {
        return ResponseEntity.ok(
                employeeSpecificationService.search(requests, condition)
        );
    }
}
