package com.example.bankcards.controller;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.request.UserUpdateRequest;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * The type User controller.
 */
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "Управление пользователями", description = "Административные функции управления пользователями")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserController {
    private final UserService userService;

    /**
     * Delete user.
     *
     * @param userId the user id
     * @return the response entity
     */
    @DeleteMapping("/delete/{userId}")
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя пользователя")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        if (userService.getUserById(userId).isPresent()) {
            userService.deleteUser(userId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Create user.
     *
     * @param userRequest the user request
     * @return the response entity
     */
    @PutMapping("/create")
    @Operation (summary = "Создать пользователя", description = "Создание пользователя")
    public ResponseEntity<Void> create(@Valid @RequestBody UserRequest userRequest) {
        userService.createUser(userRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * Update role.
     *
     * @param user_id     the user id
     * @param userDetails the user details
     * @return the response entity
     */
    @PatchMapping("/role-update/{user_id}")
    @Operation (summary = "Изменение пользователя", description = "Изменение имени, фамилии, почты и роли пользователя")
    public ResponseEntity<Void> updateRole(@PathVariable Long user_id, @Valid @RequestBody UserUpdateRequest userDetails) {
        userService.updateUser(user_id, userDetails);
        return ResponseEntity.ok().build();
    }

}
