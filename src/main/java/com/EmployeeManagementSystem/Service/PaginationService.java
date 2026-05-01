package com.EmployeeManagementSystem.Service;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface PaginationService {
    Page<EmployeeDTO> findByPageNumber(Pageable pageable);
    List<EmployeeDTO> findEmployeeByPageNumber(Pageable pageable);
}
