package com.EmployeeManagementSystem.Controller;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.Service.PaginationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class PaginationController {

    private final PaginationService paginationService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/page")
    public ResponseEntity<Page<EmployeeDTO>> findByPageNumber(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(defaultValue = "id,ASC") String[] sort
    ) {
        log.info("REST request to find employees by page: page={}, size={}, sort={}", page, size, sort);
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sort[1]), sort[0])
        );
        Page<EmployeeDTO> result = paginationService.findByPageNumber(pageable);
        log.info("Successfully retrieved page {} of employees", page);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/employeePage")
    public List<EmployeeDTO> findEmployeeByPageNumber(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                      @RequestParam(required = false, defaultValue = "7") int pageSize,
                                                      @RequestParam(required = false, defaultValue = "rollNo") String sortBy,
                                                      @RequestParam(required = false, defaultValue = "ASC") String sortDir){
        log.info("REST request to find employees by page number: pageNo={}, pageSize={}, sortBy={}, sortDir={}", pageNo, pageSize, sortBy, sortDir);
        Sort sort = null;
        if(sortDir.equalsIgnoreCase("ASC")){
            sort = Sort.by(sortBy).ascending();
        }else{
            sort = Sort.by(sortBy).descending();
        }
        List<EmployeeDTO> employees = paginationService.findEmployeeByPageNumber(
                PageRequest.of(pageNo-1, pageSize, sort)
        );
        log.info("Successfully retrieved {} employees for page {}", employees.size(), pageNo);
        return employees;
    }
}

