package com.EmployeeManagementSystem.Service;

import com.EmployeeManagementSystem.DTO.SearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeSpecificationService<T> {
    Page<T> search(List<SearchRequestDTO> requests, String condition, Pageable pageable);

    List<T> search(List<SearchRequestDTO> requests, String condition);
}