package com.EmployeeManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="department_name", nullable = false, unique = true)
    private String department_name;
}
