package com.EmployeeManagementSystem.Service.SeviceImpl;

import com.EmployeeManagementSystem.DTO.DynamicSalaryResponseDTO;
import com.EmployeeManagementSystem.Entity.Employee;
import com.EmployeeManagementSystem.Exceptions.EmployeeNotFoundException;
import com.EmployeeManagementSystem.Repository.EmployeeRepository;
import com.EmployeeManagementSystem.SalaryModule.SalaryCalculator;
import com.EmployeeManagementSystem.SalaryModule.SalaryCalculatorFactory;
import com.EmployeeManagementSystem.Service.SalaryCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalaryCalculationServiceImpl implements SalaryCalculationService {

    private final EmployeeRepository employeeRepository;
    private final SalaryCalculatorFactory salaryCalculatorFactory;

    @Transactional(readOnly = true) //keeps session open & safe for reads
    @Override
    public DynamicSalaryResponseDTO calculateDynamicSalary(Long employeeId, double percentage) {

        if (percentage <= 0) {
            throw new IllegalArgumentException("Percentage must be greater than 0");
        }

        //Use the fetch-join method
        Employee employee = employeeRepository.findByIdWithDepartment(employeeId)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        if (employee.getDepartment() == null) {
            throw new IllegalStateException("Department not assigned to employee");
        }

        String departmentName = employee.getDepartment().getDepartment_name();

        SalaryCalculator calculator =
                salaryCalculatorFactory.getSalaryCalculator(departmentName);

        double calculatedSalary = calculator.calculateSalary(employee, percentage);

        return new DynamicSalaryResponseDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                departmentName,
                employee.getSalary(),
                percentage,
                calculatedSalary
        );
    }
}
