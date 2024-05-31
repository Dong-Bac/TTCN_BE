package com.demo.service;

import com.demo.dto.UserDTO;
import com.demo.exception.ErrorrChangePassword;
import com.demo.exception.UserAlreadyExistsException;
import com.demo.model.Role;
import com.demo.model.User;
import com.demo.repository.RoleRepository;
import com.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override

    public UserDTO registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail() + " already exists");
        }
        user.setEmail(user.getEmail());
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Collections.singletonList(userRole));
        userRepository.save(user);
        return this.modelMapper.map(user,UserDTO.class);
    }
    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users=this.userRepository.findAll();
        List<UserDTO> userDto=users.stream().map(user->this.userToDto(user)).collect(Collectors.toList());
        if(userDto!=null){
            return userDto;
        }else{
            return null;
        }
    }
    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return null; // Or throw an exception if you prefer
        }
        return modelMapper.map(user, UserDTO.class);
    }
    @Override
    public List<UserDTO> getEmployees() {
        List<User> users= userRepository.getUsers();
        List<UserDTO> userDto=users.stream().map(user->this.userToDto(user)).collect(Collectors.toList());
        return userDto;
    }
    @Override
    public List<UserDTO> getUsers() {
        List<User> users= userRepository.getUsers();
        List<UserDTO> userDto=users.stream().map(user->this.userToDto(user)).collect(Collectors.toList());
        return userDto;
    }
    @Override
    public UserDTO updateUser (String email, User updatedUser) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return null; // Or throw an exception if preferred
        }

        User user = optionalUser.get();
        user.setUsername(updatedUser.getUsername());
        user.setAddress(updatedUser.getAddress());
        user.setAge(updatedUser.getAge());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setAvatar(updatedUser.getAvatar());

        // Kiểm tra xem có cần mã hóa lại mật khẩu hay không
        if (!updatedUser.getPassword().equals(user.getPassword())) {
            String hashedPassword = passwordEncoder.encode(updatedUser.getPassword());
            user.setPassword(hashedPassword);
        }
        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return this.modelMapper.map(user, UserDTO.class);
            } else {
                throw new ErrorrChangePassword("Incorrect old password");
            }
        } else {
            throw new ErrorrChangePassword("User not found");
        }
    }


    @Transactional
    @Override
    public void deleteUser(String email) {
        if (userRepository.existsByEmail(email)) {
            userRepository.deleteByEmail(email);
        }
    }
    private User dtoToUser(UserDTO userDto){
        User user=this.modelMapper.map(userDto,User.class);
        return user;

    }
    private UserDTO userToDto(User user){
        UserDTO userDto=this.modelMapper.map(user,UserDTO.class);
        return userDto;
    }
}
