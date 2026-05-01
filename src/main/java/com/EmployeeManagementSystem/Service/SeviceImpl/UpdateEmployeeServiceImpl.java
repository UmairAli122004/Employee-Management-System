package com.EmployeeManagementSystem.Service.SeviceImpl;

import com.EmployeeManagementSystem.DTO.UpdateEmployeeDTO;
import com.EmployeeManagementSystem.Entity.Employee;
import com.EmployeeManagementSystem.Repository.EmployeeRepository;

import com.EmployeeManagementSystem.Service.UpdateEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UpdateEmployeeServiceImpl implements UpdateEmployeeService {
    private final EmployeeRepository employeeRepository;

    @Transactional
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "employeeById", key = "#dto.employeeId"),
                    @CacheEvict(value = "employeeList", allEntries = true),
                    @CacheEvict(value = "employeePage", allEntries = true),
                    @CacheEvict(value = "employeeSearchPage", allEntries = true),
                    @CacheEvict(value = "employeeSearchList", allEntries = true),
                    @CacheEvict(value = "employeeFilter", allEntries = true),
                    @CacheEvict(value = "employeeSummary", allEntries = true),
                    @CacheEvict(value = "employeeAuth", allEntries = true)
            }
    )
    public Employee UpdateEmployee(UpdateEmployeeDTO dto) {
        log.info("Attempting to update employee ID: {}", dto.getEmployeeId());
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> {
                    log.warn("Update failed: Employee ID {} not found", dto.getEmployeeId());
                    return new RuntimeException("Employee not found");
                });

        Optional<Employee> existing = employeeRepository.findByPhoneNumber(dto.getPhoneNumber());

        if (existing.isPresent() && !existing.get().getId().equals(dto.getEmployeeId())) {
            log.warn("Update failed: Phone number {} already exists for another employee", dto.getPhoneNumber());
            throw new RuntimeException("Phone number already exists");
        }

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setAddress(dto.getAddress());
        employee.setPhoneNumber(dto.getPhoneNumber());

        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee ID {} successfully updated", updatedEmployee.getId());
        return updatedEmployee;
    }
}

