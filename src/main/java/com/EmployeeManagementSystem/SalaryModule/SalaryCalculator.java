package com.EmployeeManagementSystem.SalaryModule;

import com.EmployeeManagementSystem.Entity.Employee;

public interface SalaryCalculator {
    double calculateSalary(Employee employee, double percentage);
}
