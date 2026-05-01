package com.EmployeeManagementSystem.Mapper;

import com.EmployeeManagementSystem.DTO.ApiResponse;
import org.springframework.stereotype.Component;

@Component
public class ResponseUtil {

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }


    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
