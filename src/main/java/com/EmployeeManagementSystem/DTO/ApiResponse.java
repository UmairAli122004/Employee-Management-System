package com.EmployeeManagementSystem.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ApiResponse<T> {

    private boolean status;
    private String message;
    private T data;

    private String code;

}