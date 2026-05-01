package com.EmployeeManagementSystem.Service.SeviceImpl;

import com.EmployeeManagementSystem.Repository.EmployeeRepository;
import com.EmployeeManagementSystem.Service.DeleteEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteEmployeeServiceImpl implements DeleteEmployeeService {
    private final EmployeeRepository employeeRepository;
    
    @Override
    @Caching(evict = {
            @CacheEvict(value = "employeeById", key = "#employeeId"),
            @CacheEvict(value = "employeeList", allEntries = true),
            @CacheEvict(value = "employeePage", allEntries = true),
            @CacheEvict(value = "employeeSearchPage", allEntries = true),
            @CacheEvict(value = "employeeSearchList", allEntries = true),
            @CacheEvict(value = "employeeFilter", allEntries = true),
            @CacheEvict(value = "employeeSummary", allEntries = true),
            @CacheEvict(value = "employeeAuth", allEntries = true)
    })
    public void deleteEmployee(Long employeeId) {
        log.info("Attempting to delete employee ID: {}", employeeId);

        if (!employeeRepository.existsById(employeeId)) {
            log.warn("Delete failed: Employee ID {} not found", employeeId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
        }

        employeeRepository.deleteById(employeeId);
        log.info("Employee ID {} successfully deleted", employeeId);
    }
}

