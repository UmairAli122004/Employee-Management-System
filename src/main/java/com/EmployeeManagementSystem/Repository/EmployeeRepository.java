package com.EmployeeManagementSystem.Repository;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.Entity.Employee;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    boolean existsByPhoneNumber(String phoneNumber);

    //“Fetch department along with employee in the same query” ("Avoid  N+1 Problem")
    @EntityGraph(attributePaths = {"department"})
    Page<Employee> findAll(Specification<Employee> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"department"})
    List<Employee> findAll(Specification<Employee> specification);

    @EntityGraph(attributePaths = {"department"})
    Optional<Employee> findById(Long employeeId);

    @EntityGraph(attributePaths = {"department", "user"})
    Optional<Employee> findByIdAndUserEmail(Long id, String email);

    @Query("SELECT e FROM Employee e WHERE e.phoneNumber = :phoneNumber")
    Optional<Employee> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.id = :id")
    Optional<Employee> findByIdWithDepartment(@Param("id") Long id);
}
