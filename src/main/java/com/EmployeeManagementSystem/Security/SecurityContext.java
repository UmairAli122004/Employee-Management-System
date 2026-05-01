//package com.EmployeeManagementSystem.Security;
//import com.EmployeeManagementSystem.Entity.Employee;
//import com.EmployeeManagementSystem.Repository.EmployeeRepository;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SecurityContext {
//    private final EmployeeRepository employeeRepository;
//    public SecurityContext(EmployeeRepository employeeRepository){
//        this.employeeRepository=employeeRepository;
//    }
//    public Employee getCurrentUser(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication!=null && authentication.isAuthenticated()){
//            String email = authentication.getName();
//            return employeeRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
//        }
//        return null;
//    }
//
//    public Long getCurrentUserId(){
//        Employee user = getCurrentUser();
//        return user!=null? user.getId() : null;
//    }
//}
//
