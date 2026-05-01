package com.EmployeeManagementSystem.Controller.ExceptionAdvice;

import com.EmployeeManagementSystem.DTO.ApiResponse;
import com.EmployeeManagementSystem.DTO.ErrorDTO;
import com.EmployeeManagementSystem.Exceptions.EmployeeAlreadyExistException;
import com.EmployeeManagementSystem.Exceptions.EmployeeNotFoundException;
import com.EmployeeManagementSystem.Exceptions.UserNotFoundException;
import com.EmployeeManagementSystem.Exceptions.SomeThingWentWrong;
import com.EmployeeManagementSystem.Mapper.ResponseUtil;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    /*
           Exception                              Purpose
| -------------------------------------- | ------------------------- |
| ValidationException                    | General/custom validation |
| HttpRequestMethodNotSupportedException | Wrong HTTP method         |
| ConstraintViolationException           | Param validation          |
| MethodArgumentNotValidException        | Request body validation   |
| NoResourceFoundException               | Resource not found        |
| ResponseStatusException                | Custom error response     |
| User NOT logged in                     | `AuthenticationException` |
| User logged in but no permission       | `AccessDeniedException`   |
    */



    /*
    *
    *   Manual validation fails
    *   Business rule validation fails
    *            Example: “Salary cannot be negative”
    * */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDTO> handleValidationException(
            ValidationException exception) {
        log.warn("Validation exception: {}", exception.getMessage());
        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(exception.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT)
                .timeStamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDTO);
    }

    //Thrown when client uses the wrong HTTP method
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDTO> handleMethodNotAllowedException(
            HttpRequestMethodNotSupportedException exception) {
        log.warn("Method not allowed: {}", exception.getMessage());
        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(exception.getMessage())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error(HttpStatus.METHOD_NOT_ALLOWED)
                .timeStamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorDTO);
    }

    //@Valid, @Min, @NotNull (For parameter-level validation (mostly in query params, path variables)).
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ErrorDTO>> handleConstraintViolationException(
            ConstraintViolationException exception) {
        log.warn("Constraint violation: {}", exception.getMessage());
        List<ErrorDTO> errorDTOList = exception.getConstraintViolations()
                .stream()
                .map(error -> ErrorDTO.builder()
                        .message(error.getMessage())
                        .error(HttpStatus.BAD_REQUEST)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .timeStamp(LocalDateTime.now())
                        .build()
                )
                .toList();

        return ResponseEntity.badRequest().body(errorDTOList);
    }


    /*
    *
    *
    * For request body validation failure
    * When: @Valid is used with @RequestBody
    *
    * */
    @ExceptionHandler
    public ResponseEntity<List<ErrorDTO>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.warn("Method argument not valid: {}", exception.getMessage());
        List<ErrorDTO> errorDTOList = exception.getBindingResult().getAllErrors()
                .stream()
                .map(error -> ErrorDTO.builder()
                        .message(error.getDefaultMessage())
                        .error(HttpStatus.BAD_REQUEST)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .timeStamp(LocalDateTime.now())
                        .build()
                )
                .toList();
        return ResponseEntity.status(errorDTOList.get(0).getStatus()).body(errorDTOList);
    }


    @ExceptionHandler(EmployeeAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmployeeAlreadyExist(EmployeeAlreadyExistException exception){
        log.warn("Employee already exists: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseUtil.error("ERROR", exception.getMessage()));
    }


    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(EmployeeNotFoundException exception){
        log.warn("Employee not found: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.error("ERROR", exception.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UserNotFoundException exception){
        log.warn("User not found: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.error("ERROR", exception.getMessage()));
    }

    @ExceptionHandler(SomeThingWentWrong.class)
    public ResponseEntity<ApiResponse<Object>> handleSomeThingWentWrong(SomeThingWentWrong exception){
       log.warn("Application constraint violated: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.error("ERROR", "Employee Not Found"));
    }


    /*
    *
    *
    *  Why used:
                When requested resource doesn’t exist

            When:
                API endpoint not found
                Static resource missing

                ✔ Example:
                        GET /unknown-api → 404
    * */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException exception){
        log.warn("No static resource found: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.error("NOT_FOUND", "Resource not found"));
    }

    /*
    *
    *
    * Why used:
        To manually throw HTTP status with message
         When:
             You want custom error response
    * */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Object>> handleResponseStatusException(ResponseStatusException ex) {
        log.warn("Response status exception: status {}, reason {}", ex.getStatusCode(), ex.getReason());
        return ResponseEntity.status(ex.getStatusCode())
                .body(ResponseUtil.error("ERROR", ex.getReason()));
    }



    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException exception) {
        log.warn("Access denied: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseUtil.error("ERROR", "Access Denied: You do not have permission to access this resource"));
    }

    /**
     * Handles invalid username/password — returns 401 with a clean JSON body.
     * This prevents the browser from showing its native Basic Auth popup.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException exception) {
        log.warn("Bad credentials: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseUtil.error("UNAUTHORIZED", "Invalid credentials. Please register first."));
    }

    /**
     * Handles duplicate email during registration.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException exception) {
        String msg = exception.getMessage();
        if (msg != null && msg.toLowerCase().contains("already exists")) {
            log.warn("Runtime exception (likely duplicate): {}", msg);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseUtil.error("CONFLICT", msg));
        }
        log.error("Unhandled RuntimeException occurred: ", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtil.error("ERROR", "Something went wrong"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception exception){
        log.error("Unhandled Exception occurred: ", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtil.error("ERROR", "Something went wrong"));
    }

}
