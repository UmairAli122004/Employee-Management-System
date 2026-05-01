package com.EmployeeManagementSystem.DTO;

import lombok.Data;
import java.util.List;

@Data
public class EmployeeSpecificationDTO {

    private List<SearchRequestDTO> conditions;  // all filters
    private String logicalOperator;             // AND / OR
}