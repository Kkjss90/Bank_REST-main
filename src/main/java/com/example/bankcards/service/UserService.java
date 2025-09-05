package com.example.bankcards.service;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
//    User createUser(User user)
    UserResponse createUser(UserRequest userRequest);
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    public UserDetailsService userDetailsService();
}