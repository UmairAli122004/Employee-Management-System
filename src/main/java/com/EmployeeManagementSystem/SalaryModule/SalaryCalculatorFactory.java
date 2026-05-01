package com.EmployeeManagementSystem.SalaryModule;

import com.EmployeeManagementSystem.SalaryModule.SalaryModuleImpl.FinanceDepartmentSalaryCalculator;
import com.EmployeeManagementSystem.SalaryModule.SalaryModuleImpl.HRDepartmentSalaryCalculator;
import com.EmployeeManagementSystem.SalaryModule.SalaryModuleImpl.ITDepartmentSalaryCalculator;
import com.EmployeeManagementSystem.SalaryModule.SalaryModuleImpl.MarketingDepartmentSalaryCalculator;
import com.EmployeeManagementSystem.SalaryModule.SalaryModuleImpl.SalesDepartmentSalaryCalculator;
import org.springframework.stereotype.Component;

@Component
public class SalaryCalculatorFactory {

    public SalaryCalculator getSalaryCalculator(String departmentName) {
        if (departmentName == null) {
            throw new IllegalArgumentException("Department name cannot be null");
        }
        switch (departmentName.toLowerCase()) {
            case "it":
                return new ITDepartmentSalaryCalculator();
            case "sales":
                return new SalesDepartmentSalaryCalculator();
            case "finance":
                return new FinanceDepartmentSalaryCalculator();
            case "hr":
                return new HRDepartmentSalaryCalculator();
            case "marketing":
                return new MarketingDepartmentSalaryCalculator();
            default:
                throw new IllegalArgumentException("No salary calculator found for department: " + departmentName);
        }
    }
}
