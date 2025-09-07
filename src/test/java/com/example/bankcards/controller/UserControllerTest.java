package com.example.bankcards.controller;

import com.example.bankcards.config.CorsConfig;
import com.example.bankcards.config.WebSecurityConfig;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.request.UserUpdateRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtAuthenticationEntryPoint;
import com.example.bankcards.service.TokenService;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.example.bankcards.entity.enums.RoleEnum.ROLE_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({WebSecurityConfig.class, CorsConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequest userRequest;
    private UserUpdateRequest userUpdateRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setUsername("newuser");
        userRequest.setPassword("password");
        userRequest.setEmail("new@example.com");
        userRequest.setFirstName("New");
        userRequest.setLastName("User");

        userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setUsername("newuser1");
        userUpdateRequest.setFirstName("Updated11");
        userUpdateRequest.setLastName("User11");
        userUpdateRequest.setPassword("password11");
        userUpdateRequest.setEmail("updated11@example.com");
        userUpdateRequest.setRole(ROLE_USER);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAsAdmin_ShouldUpdateUser() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(patch("/api/admin/user/role-update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isOk());

        Mockito.verify(userService).updateUser(eq(1L), any(UserUpdateRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_AsAdmin_ShouldDeleteUser() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(delete("/api/admin/user/delete/1")
                .with(csrf()))
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_WithNonExistingUser_ShouldReturnNotFound() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/admin/user/delete/1")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteUser_AsUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/admin/user/delete/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_AsAdmin_ShouldCreateUser() throws Exception {
        mockMvc.perform(put("/api/admin/user/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());

        Mockito.verify(userService).createUser(any(UserRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        UserRequest invalidRequest = new UserRequest();
        invalidRequest.setUsername("");

        mockMvc.perform(put("/api/admin/user/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

}