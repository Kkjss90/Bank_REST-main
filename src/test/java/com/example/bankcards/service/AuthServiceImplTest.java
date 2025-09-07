package com.example.bankcards.service;

import com.example.bankcards.dto.request.SignInRequest;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.JwtAuthenticationResponse;
import com.example.bankcards.entity.Token;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidTokenException;
import com.example.bankcards.repository.TokenRepository;
import com.example.bankcards.service.implementation.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.bankcards.entity.enums.RoleEnum.ROLE_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserRequest userRequest;
    private SignInRequest signInRequest;
    private User user;
    private Token token;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setUsername("testuser");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password");
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");

        signInRequest = new SignInRequest();
        signInRequest.setUsername("testuser");
        signInRequest.setPassword("password");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(ROLE_USER);

        token = new Token();
        token.setId(1L);
        token.setToken("valid.jwt.token");
        token.setUser(user);
    }

    @Test
    void signUp_ShouldCreateUserAndReturnJwtResponse() throws InvalidTokenException {
        String jwtToken = "generated.jwt.token";

        when(userService.createUser(userRequest)).thenReturn(null);
        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn(jwtToken);
        doNothing().when(tokenService).saveToken(jwtToken);

        JwtAuthenticationResponse result = authService.signUp(userRequest);

        assertNotNull(result);
        assertEquals(jwtToken, result.getToken());

        verify(userService, times(1)).createUser(userRequest);
        verify(userService, times(1)).getUserByUsername("testuser");
        verify(tokenService, times(1)).generateToken(user);
        verify(tokenService, times(1)).saveToken(jwtToken);
    }

    @Test
    void signIn_WithValidToken_ShouldReturnExistingToken() throws InvalidTokenException {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(tokenRepository.findByUser(user)).thenReturn(token);
        doNothing().when(tokenService).validateToken("valid.jwt.token");

        JwtAuthenticationResponse result = authService.signIn(signInRequest);

        assertNotNull(result);
        assertEquals("valid.jwt.token", result.getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1)).getUserByUsername("testuser");
        verify(tokenRepository, times(1)).findByUser(user);
        verify(tokenService, times(1)).validateToken("valid.jwt.token");
        verify(tokenService, never()).generateToken(any());
        verify(tokenService, never()).saveToken(any());
    }

    @Test
    void signIn_WithInvalidToken_ShouldGenerateNewToken() throws InvalidTokenException {
        String newToken = "new.jwt.token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(tokenRepository.findByUser(user)).thenReturn(token);
        doThrow(new InvalidTokenException("Token expired"))
                .when(tokenService).validateToken("valid.jwt.token");
        when(tokenService.generateToken(user)).thenReturn(newToken);
        doNothing().when(tokenService).saveToken(newToken);

        JwtAuthenticationResponse result = authService.signIn(signInRequest);

        assertNotNull(result);
        assertEquals(newToken, result.getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1)).getUserByUsername("testuser");
        verify(tokenRepository, times(1)).findByUser(user);
        verify(tokenService, times(1)).validateToken("valid.jwt.token");
        verify(tokenService, times(1)).generateToken(user);
        verify(tokenService, times(1)).saveToken(newToken);
    }

    @Test
    void signIn_WithNoExistingToken_ShouldGenerateNewToken() throws InvalidTokenException {
        String newToken = "new.jwt.token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(tokenRepository.findByUser(user)).thenReturn(null);
        when(tokenService.generateToken(user)).thenReturn(newToken);
        doNothing().when(tokenService).saveToken(newToken);

        JwtAuthenticationResponse result = authService.signIn(signInRequest);

        assertNotNull(result);
        assertEquals(newToken, result.getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1)).getUserByUsername("testuser");
        verify(tokenRepository, times(1)).findByUser(user);
        verify(tokenService, never()).validateToken(any());
        verify(tokenService, times(1)).generateToken(user);
        verify(tokenService, times(1)).saveToken(newToken);
    }

    @Test
    void signIn_WhenAuthenticationFails_ShouldThrowException() throws InvalidTokenException {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () ->
                authService.signIn(signInRequest));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, never()).getUserByUsername(any());
        verify(tokenRepository, never()).findByUser(any());
        verify(tokenService, never()).validateToken(any());
        verify(tokenService, never()).generateToken(any());
        verify(tokenService, never()).saveToken(any());
    }

    @Test
    void signIn_WhenTokenSaveFails_ShouldThrowRuntimeException() throws InvalidTokenException {
        String newToken = "new.jwt.token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(tokenRepository.findByUser(user)).thenReturn(token);
        doThrow(new InvalidTokenException("Token expired"))
                .when(tokenService).validateToken("valid.jwt.token");
        when(tokenService.generateToken(user)).thenReturn(newToken);
        doThrow(new InvalidTokenException("Save failed"))
                .when(tokenService).saveToken(newToken);

        assertThrows(InvalidTokenException.class, () ->
                authService.signIn(signInRequest));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1)).getUserByUsername("testuser");
        verify(tokenRepository, times(1)).findByUser(user);
        verify(tokenService, times(1)).validateToken("valid.jwt.token");
        verify(tokenService, times(1)).generateToken(user);
        verify(tokenService, times(1)).saveToken(newToken);
    }

    @Test
    void signUp_ShouldHandleUserCreationAndTokenGeneration() throws InvalidTokenException {
        String jwtToken = "generated.jwt.token";

        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn(jwtToken);
        doNothing().when(tokenService).saveToken(jwtToken);

        JwtAuthenticationResponse result = authService.signUp(userRequest);

        assertNotNull(result);
        assertEquals(jwtToken, result.getToken());

        verify(userService, times(1)).createUser(userRequest);
        verify(userService, times(1)).getUserByUsername("testuser");
        verify(tokenService, times(1)).generateToken(user);
        verify(tokenService, times(1)).saveToken(jwtToken);
    }
}