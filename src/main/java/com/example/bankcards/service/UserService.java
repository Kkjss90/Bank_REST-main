package com.example.bankcards.service;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.request.UserUpdateRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

/**
 * The interface User service.
 */
public interface UserService {
    /**
     * Gets all users.
     *
     * @return the all users
     */
    List<User> getAllUsers();

    /**
     * Gets user by id.
     *
     * @param id the id
     * @return the user by id
     */
    Optional<User> getUserById(Long id);

    /**
     * Gets user by username.
     *
     * @param username the username
     * @return the user by username
     */
    User getUserByUsername(String username);

    /**
     * Gets user by email.
     *
     * @param email the email
     * @return the user by email
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Create user user response.
     *
     * @param userRequest the user request
     * @return the user response
     */
    UserResponse createUser(UserRequest userRequest);

    /**
     * Update user user.
     *
     * @param id          the id
     * @param userDetails the user details
     * @return the user
     */
    User updateUser(Long id, UserUpdateRequest userDetails);

    /**
     * Delete user.
     *
     * @param id the id
     */
    void deleteUser(Long id);

    /**
     * Exists by username boolean.
     *
     * @param username the username
     * @return the boolean
     */
    boolean existsByUsername(String username);

    /**
     * Exists by email boolean.
     *
     * @param email the email
     * @return the boolean
     */
    boolean existsByEmail(String email);

    /**
     * User details service user details service.
     *
     * @return the user details service
     */
    public UserDetailsService userDetailsService();
}