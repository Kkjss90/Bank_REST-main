package com.example.bankcards.controller;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user")
@Tag(name = "Управление пользователями", description = "Административные функции управления пользователями")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserController {
    @Autowired
    private UserService userService;

    @DeleteMapping("/delete/{userId}")
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя пользователя")
    public ResponseEntity<?> delete(@PathVariable Long userId) {
        if (userService.getUserById(userId).isPresent()) {
            userService.deleteUser(userId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/create")
    @Operation (summary = "Создать пользователя", description = "Создание пользователя")
    public ResponseEntity<?> create(@Valid @RequestBody UserRequest userRequest) {
        userService.createUser(userRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/role-update/{user_id}")
    @Operation (summary = "Изменение пользователя", description = "Изменение имени, фамилии, почты и роли пользователя")
    public ResponseEntity<?> updateRole(@PathVariable Long user_id, @Valid @RequestBody User userDetails) {
        userService.updateUser(user_id, userDetails);
        return ResponseEntity.ok().build();
    }

}
