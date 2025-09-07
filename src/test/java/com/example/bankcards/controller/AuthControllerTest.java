package com.example.bankcards.controller;

import com.example.bankcards.config.CorsConfig;
import com.example.bankcards.config.WebSecurityConfig;
import com.example.bankcards.dto.request.SignInRequest;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.JwtAuthenticationResponse;
import com.example.bankcards.security.JwtAuthenticationEntryPoint;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({WebSecurityConfig.class, CorsConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequest userRequest;
    private SignInRequest signInRequest;
    private JwtAuthenticationResponse authResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setUsername("testuser");
        userRequest.setPassword("password");
        userRequest.setEmail("test@example.com");
        userRequest.setFirstName("Test");
        userRequest.setLastName("User");

        signInRequest = new SignInRequest();
        signInRequest.setUsername("testuser");
        signInRequest.setPassword("password");

        authResponse = new JwtAuthenticationResponse("test-token");
    }

    @Test
    void signUp_ShouldReturnJwtResponse() throws Exception {
        Mockito.when(authService.signUp(any(UserRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"));
        Mockito.verify(authService).signUp(any(UserRequest.class));
    }

    @Test
    void signUp_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        UserRequest invalidRequest = new UserRequest();
        invalidRequest.setUsername("");

        mockMvc.perform(post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signIn_ShouldReturnJwtResponse() throws Exception {
        Mockito.when(authService.signIn(any(SignInRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"));
        Mockito.verify(authService).signIn(any(SignInRequest.class));
    }

    @Test
    void signIn_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        SignInRequest invalidRequest = new SignInRequest();
        invalidRequest.setUsername("");

        mockMvc.perform(post("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}