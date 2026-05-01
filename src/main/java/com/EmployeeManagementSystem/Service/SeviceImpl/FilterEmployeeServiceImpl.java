package com.EmployeeManagementSystem.Service.SeviceImpl;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.DTO.EmployeeDTOProjection;
import com.EmployeeManagementSystem.Mapper.MapperClassToDTO;
import com.EmployeeManagementSystem.Repository.EmployeeDTOProjectionRepository;
import com.EmployeeManagementSystem.Repository.FilterEmployeeRepository;
import com.EmployeeManagementSystem.Service.FilterEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class FilterEmployeeServiceImpl implements FilterEmployeeService {

    private final FilterEmployeeRepository filterEmployeeRepository;
    private final EmployeeDTOProjectionRepository employeeSummaryRepository;
    private final MapperClassToDTO mapperClassToDTO;

    private final String CACHE_FILTER = "employeeFilter";
    private final String CACHE_SUMMARY = "employeeSummary";


    @Cacheable(value = CACHE_FILTER, key = "#department_name")
    @Override
    public List<EmployeeDTO> findEmployeeByDepartmentName(String department_name) {
        return filterEmployeeRepository.findEmployeeByDepartmentName(department_name)
                .stream()
                .map(mapperClassToDTO::entityToDTO)
                .toList();
    }


    @Cacheable(value = CACHE_FILTER, key = "#id")
    @Override
    public List<EmployeeDTO> findEmployeeByDepartmentId(Long id) {
        return filterEmployeeRepository.findEmployeeByDepartmentId(id)
                .stream()
                .map(mapperClassToDTO::entityToDTO)
                .toList();
    }


    @Cacheable(value = CACHE_SUMMARY)
    @Override
    public List<EmployeeDTOProjection> findEmployeeNameAndDepartment() {
        return employeeSummaryRepository.findEmployeeNameAndDepartment();
    }


    @Cacheable(value = "employeeList", key = "'allEmployees'")
    @Override
    public List<EmployeeDTO> showEmployeeDetails() {
        return filterEmployeeRepository.findAllEmployee()
                .stream()
                .map(mapperClassToDTO::entityToDTO)
                .toList();
    }
}