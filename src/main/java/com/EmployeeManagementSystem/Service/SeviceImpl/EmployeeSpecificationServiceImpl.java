package com.EmployeeManagementSystem.Service.SeviceImpl;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.DTO.SearchRequestDTO;
import com.EmployeeManagementSystem.Entity.Employee;
import com.EmployeeManagementSystem.Mapper.MapperClassToDTO;
import com.EmployeeManagementSystem.Repository.EmployeeRepository;
import com.EmployeeManagementSystem.Service.EmployeeSpecificationService;
import com.EmployeeManagementSystem.SpecificationClass.EmployeeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeSpecificationServiceImpl implements EmployeeSpecificationService<EmployeeDTO> {

    private final EmployeeRepository employeeRepository;
    private final MapperClassToDTO mapperClassToDTO;
    private final EmployeeSpecification<Employee> employeeSpecification;

    @Cacheable(value = "employeeSearchPage", keyGenerator = "customKeyGenerator")
    @Override
    public Page<EmployeeDTO> search(List<SearchRequestDTO> requests, String condition, Pageable pageable) {
        Specification<Employee> spec = employeeSpecification.filter(requests, condition);
        return employeeRepository.findAll(spec, pageable)
                .map(mapperClassToDTO::entityToDTO);
    }

    @Cacheable(value = "employeeSearchList", keyGenerator = "customKeyGenerator")
    @Override
    public List<EmployeeDTO> search(List<SearchRequestDTO> requests, String condition) {
        Specification<Employee> spec = employeeSpecification.filter(requests, condition);
        return employeeRepository.findAll(spec)
                .stream()
                .map(mapperClassToDTO::entityToDTO)
                .toList();
    }
}