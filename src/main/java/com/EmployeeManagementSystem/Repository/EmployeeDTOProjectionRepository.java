package com.EmployeeManagementSystem.Repository;

import com.EmployeeManagementSystem.DTO.EmployeeDTOProjection;
import com.EmployeeManagementSystem.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeDTOProjectionRepository extends JpaRepository<Employee, Long> {
    //Use Entity field names
    //DTO Projection is use to optimize the API Response (API Response Optimization)
    @Query("""
       SELECT new com.EmployeeManagementSystem.DTO.EmployeeDTOProjection(
           e.firstName,
           e.lastName,
           d.department_name
       )
       FROM Employee e
       JOIN e.department d
       """)
    List<EmployeeDTOProjection> findEmployeeNameAndDepartment();
}
