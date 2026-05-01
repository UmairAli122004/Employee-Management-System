package com.EmployeeManagementSystem.DTO;

import com.EmployeeManagementSystem.Enum.SearchOperation;
import lombok.Data;
import java.util.List;

@Data
public class SearchRequestDTO {

    private String column;
    private SearchOperation operation;
    private Object value;
    private Object valueTo;
    private List<Object> values;
}