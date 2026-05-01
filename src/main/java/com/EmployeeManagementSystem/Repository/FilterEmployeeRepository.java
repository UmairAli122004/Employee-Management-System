package com.EmployeeManagementSystem.Repository;

import com.EmployeeManagementSystem.Entity.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilterEmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e JOIN FETCH e.department d WHERE d.department_name = :department_name")
    List<Employee> findEmployeeByDepartmentName(@Param("department_name") String department_name);


//    @Query(nativeQuery = true,
//            value = "SELECT e.* FROM employee e JOIN department d ON e.department_id = d.id WHERE d.id = :id")
//    List<Employee> findEmployeeByDepartmentId(@Param("id") Long id);
    @EntityGraph(attributePaths = {"department"})
    @Query("SELECT e FROM Employee e WHERE e.department.id = :id")
    List<Employee> findEmployeeByDepartmentId(@Param("id") Long id);

    @Query("SELECT e FROM Employee e JOIN FETCH e.department")
    List<Employee> findAllEmployee();
}
