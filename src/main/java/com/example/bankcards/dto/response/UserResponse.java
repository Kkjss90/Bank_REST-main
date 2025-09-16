package com.example.bankcards.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * The type User response.
 */
@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private LocalDateTime createdAt;

    /**
     * Instantiates a new User response.
     *
     * @param id        the id
     * @param username  the username
     * @param email     the email
     * @param firstName the first name
     * @param lastName  the last name
     * @param role      the role
     * @param createdAt the created at
     */
    public UserResponse(Long id, String username, String email, String firstName,
                       String lastName, String role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.createdAt = createdAt;
    }
}