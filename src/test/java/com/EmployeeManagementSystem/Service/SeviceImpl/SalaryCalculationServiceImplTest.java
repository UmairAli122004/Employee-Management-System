package com.EmployeeManagementSystem.Service.SeviceImpl;

import com.EmployeeManagementSystem.DTO.DynamicSalaryResponseDTO;
import com.EmployeeManagementSystem.Entity.Department;
import com.EmployeeManagementSystem.Entity.Employee;
import com.EmployeeManagementSystem.Exceptions.EmployeeNotFoundException;
import com.EmployeeManagementSystem.Repository.EmployeeRepository;
import com.EmployeeManagementSystem.SalaryModule.SalaryCalculatorFactory;
import com.EmployeeManagementSystem.SalaryModule.SalaryModuleImpl.FinanceDepartmentSalaryCalculator;
import com.EmployeeManagementSystem.SalaryModule.SalaryModuleImpl.ITDepartmentSalaryCalculator;
import com.EmployeeManagementSystem.SalaryModule.SalaryModuleImpl.SalesDepartmentSalaryCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaryCalculationServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private SalaryCalculatorFactory salaryCalculatorFactory = new SalaryCalculatorFactory();

    private SalaryCalculationServiceImpl salaryCalculationService;

    private Employee employee;
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setDepartment_name("IT");

        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setSalary(50000.0);
        employee.setDepartment(department);

        salaryCalculationService = new SalaryCalculationServiceImpl(employeeRepository, salaryCalculatorFactory);
    }

    @Test
    void calculateDynamicSalary_Success_ITDepartment() {
        when(employeeRepository.findByIdWithDepartment(1L)).thenReturn(Optional.of(employee));

        DynamicSalaryResponseDTO response = salaryCalculationService.calculateDynamicSalary(1L, 10.0);

        assertNotNull(response);
        assertEquals(1L, response.getEmployeeId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("IT", response.getDepartmentName());
        assertEquals(50000.0, response.getBaseSalary());
        assertEquals(10.0, response.getPercentageApplied());
        // IT department calculator logic: baseSalary + (baseSalary * percentage / 100)
        assertEquals(55000.0, response.getCalculatedSalary());
    }

    @Test
    void calculateDynamicSalary_InvalidPercentage_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            salaryCalculationService.calculateDynamicSalary(1L, -5.0);
        });

        assertEquals("Percentage must be greater than 0", exception.getMessage());
        verify(employeeRepository, never()).findByIdWithDepartment(anyLong());
    }

    @Test
    void calculateDynamicSalary_EmployeeNotFound_ThrowsException() {
        when(employeeRepository.findByIdWithDepartment(99L)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            salaryCalculationService.calculateDynamicSalary(99L, 10.0);
        });

        assertEquals("Employee not found with id: 99", exception.getMessage());
    }
    
    @Test
    void calculateDynamicSalary_EmployeeHasNoDepartment_ThrowsException() {
        employee.setDepartment(null);
        when(employeeRepository.findByIdWithDepartment(1L)).thenReturn(Optional.of(employee));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            salaryCalculationService.calculateDynamicSalary(1L, 10.0);
        });

        assertEquals("Department not assigned to employee", exception.getMessage());
    }
}
