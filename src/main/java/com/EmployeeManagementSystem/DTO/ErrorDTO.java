package com.EmployeeManagementSystem.DTO;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorDTO {
    private String message;
    private int status;
    private HttpStatus error;
    private LocalDateTime timeStamp;
}
