package com.EmployeeManagementSystem.SalaryModule.SalaryModuleImpl;

import com.EmployeeManagementSystem.Entity.Employee;
import com.EmployeeManagementSystem.SalaryModule.SalaryCalculator;

public class ITDepartmentSalaryCalculator implements SalaryCalculator {
    @Override
    public double calculateSalary(Employee employee, double percentage) {
        return employee.getSalary() + (employee.getSalary() * (percentage / 100.0));
    }
}
