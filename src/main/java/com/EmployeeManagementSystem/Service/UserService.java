package com.EmployeeManagementSystem.Service;

import com.EmployeeManagementSystem.DTO.UserDTO;
import com.EmployeeManagementSystem.Entity.User;

public interface UserService {
    User registerUser(UserDTO userDTO);
    User findByEmail(String email);
}
