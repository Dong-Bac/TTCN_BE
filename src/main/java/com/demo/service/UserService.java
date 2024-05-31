package com.demo.service;



import com.demo.dto.UserDTO;
import com.demo.model.User;

import java.util.List;

public interface UserService {
    UserDTO registerUser(User user);
    List<UserDTO> getAllUsers();
    UserDTO getUserByEmail(String email);
    List<UserDTO> getEmployees();
    List<UserDTO> getUsers();
    void deleteUser(String email);
    UserDTO updateUser(String email, User user);
    UserDTO changePassword(String email, String oldPassword, String newPassword);
}
