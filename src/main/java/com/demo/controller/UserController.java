package com.demo.controller;

import com.demo.dto.UserDTO;
import com.demo.exception.ErrorrChangePassword;
import com.demo.model.User;
import com.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/")
@CrossOrigin(origins = "http://localhost:5500/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable("email") String email) {
        try {
            UserDTO userDto = userService.getUserByEmail(email);
            if (userDto == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(userDto);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/employees")
    public ResponseEntity<List<UserDTO>> getEmployees() {
        List<UserDTO> employees = userService.getEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/customer")
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<UserDTO> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update/{userName}/{email}/{phoneNumber}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("userName") String userName,@PathVariable("email") String email,@PathVariable("phoneNumber") String phoneNumber ) {
        UserDTO userDto = userService.updateUser(userName,email,phoneNumber);
        if (userDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable("email") String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/changePassword/{email}/{oldPassword}/{newPassword}")
    public ResponseEntity<UserDTO> changePassword(
            @PathVariable("email") String email,
            @PathVariable("oldPassword") String oldPassword,
            @PathVariable("newPassword") String newPassword) {

        try {
            UserDTO userDTO = userService.changePassword(email, oldPassword, newPassword);
            return ResponseEntity.ok(userDTO);
        } catch (ErrorrChangePassword e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}