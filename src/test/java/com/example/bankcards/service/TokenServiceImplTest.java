package com.example.bankcards.service;

import com.example.bankcards.entity.Token;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidTokenException;
import com.example.bankcards.repository.TokenRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.implementation.TokenServiceImpl;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.bankcards.entity.enums.RoleEnum.ROLE_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenServiceImpl tokenService;

    private User user;
    private UserDetails userDetails;
    private Token token;
    private String validToken;
    private String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private long expiration = 3600000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "secret", secretKey);
        ReflectionTestUtils.setField(tokenService, "expiration", expiration);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(ROLE_USER);

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        token = new Token();
        token.setId(1L);
        token.setToken("valid.jwt.token");
        token.setUser(user);

        validToken = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secretKey);
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String result = tokenService.generateToken(userDetails);

        assertNotNull(result);
        assertTrue(result.split("\\.").length == 3); // JWT имеет 3 части
    }

    @Test
    void generateToken_WithCustomExpiry_ShouldReturnTokenWithCustomExpiry() {
        Date customExpiry = new Date(System.currentTimeMillis() + 1800000);

        String result = tokenService.generateToken(userDetails, customExpiry);

        assertNotNull(result);
    }

    @Test
    void getUsernameFromToken_WithValidToken_ShouldReturnUsername() throws InvalidTokenException {
        String username = tokenService.getUsernameFromToken(validToken);

        assertEquals("testuser", username);
    }

    @Test
    void getUsernameFromToken_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.token.here";

        assertThrows(InvalidTokenException.class, () -> 
            tokenService.getUsernameFromToken(invalidToken));
    }

    @Test
    void getExpirationDateFromToken_WithValidToken_ShouldReturnExpirationDate() throws InvalidTokenException {
        Date expirationDate = tokenService.getExpirationDateFromToken(validToken);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void getClaimFromToken_WithValidToken_ShouldReturnClaim() throws InvalidTokenException {
        String subject = tokenService.getClaimFromToken(validToken, Claims::getSubject);

        assertEquals("testuser", subject);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails result = tokenService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> 
            tokenService.loadUserByUsername("unknown"));
        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    void saveToken_WithValidToken_ShouldSaveToken() throws InvalidTokenException {
        when(tokenRepository.findByToken(validToken)).thenReturn(null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(Token.class))).thenReturn(token);

        tokenService.saveToken(validToken);

        verify(tokenRepository, times(1)).findByToken(validToken);
        verify(userRepository, atLeastOnce()).findByUsername("testuser");
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void saveToken_WhenTokenAlreadyExists_ShouldThrowException() {
        when(tokenRepository.findByToken(validToken)).thenReturn(token);

        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () -> 
            tokenService.saveToken(validToken));
        
        assertTrue(exception.getMessage().contains("already exists"));
        verify(tokenRepository, times(1)).findByToken(validToken);
        verify(userRepository, never()).findByUsername(any());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void saveToken_WhenUserNotFound_ShouldNotSaveToken() throws InvalidTokenException {
        when(tokenRepository.findByToken(validToken)).thenReturn(null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        tokenService.saveToken(validToken);

        verify(tokenRepository, times(1)).findByToken(validToken);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void validateToken_WithValidToken_ShouldNotThrowException() throws InvalidTokenException {
        when(tokenRepository.findByToken(validToken)).thenReturn(token);

        assertDoesNotThrow(() -> tokenService.validateToken(validToken));
        verify(tokenRepository, times(1)).findByToken(validToken);
    }

    @Test
    void validateToken_WhenTokenNotFound_ShouldThrowException() {
        when(tokenRepository.findByToken(validToken)).thenReturn(null);

        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () -> 
            tokenService.validateToken(validToken));
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(tokenRepository, times(1)).findByToken(validToken);
    }

    @Test
    void invalidateToken_WhenTokenExists_ShouldDeleteToken() {
        when(tokenRepository.findByToken(validToken)).thenReturn(token);
        doNothing().when(tokenRepository).deleteByToken(validToken);

        tokenService.invalidateToken(validToken);

        verify(tokenRepository, times(1)).findByToken(validToken);
        verify(tokenRepository, times(1)).deleteByToken(validToken);
    }

    @Test
    void invalidateToken_WhenTokenNotExists_ShouldDoNothing() {
        when(tokenRepository.findByToken(validToken)).thenReturn(null);

        tokenService.invalidateToken(validToken);

        verify(tokenRepository, times(1)).findByToken(validToken);
        verify(tokenRepository, never()).deleteByToken(any());
    }

    @Test
    void getAllClaimsFromToken_WithExpiredToken_ShouldThrowExceptionAndInvalidate() {
        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000000))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

        when(tokenRepository.findByToken(expiredToken)).thenReturn(token);
        doNothing().when(tokenRepository).deleteByToken(expiredToken);

        assertThrows(InvalidTokenException.class, () -> 
            tokenService.getClaimFromToken(expiredToken, Claims::getSubject));
        
        verify(tokenRepository, times(1)).deleteByToken(expiredToken);
    }

    @Test
    void getAllClaimsFromToken_WithMalformedToken_ShouldThrowException() {
        String malformedToken = "malformed.token.here";

        assertThrows(InvalidTokenException.class, () -> 
            tokenService.getClaimFromToken(malformedToken, Claims::getSubject));
    }

    @Test
    void getAllClaimsFromToken_WithEmptyToken_ShouldThrowException() {
        assertThrows(InvalidTokenException.class, () -> 
            tokenService.getClaimFromToken("", Claims::getSubject));
    }

    @Test
    void getAllClaimsFromToken_WithNullToken_ShouldThrowException() {
        assertThrows(InvalidTokenException.class, () -> 
            tokenService.getClaimFromToken(null, Claims::getSubject));
    }

    @Test
    void key_ShouldReturnValidKey() {
        Key key = (Key) ReflectionTestUtils.invokeMethod(tokenService, "key");

        assertNotNull(key);
    }

    @Test
    void doGenerateToken_ShouldIncludeAuthorities() {
        String token = (String) ReflectionTestUtils.invokeMethod(tokenService, "doGenerateToken",
                userDetails, new Date(System.currentTimeMillis() + expiration));

        assertNotNull(token);

        Key signingKey = (Key) ReflectionTestUtils.invokeMethod(tokenService, "key");

        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("testuser", claims.getSubject());
        assertTrue(claims.containsKey("authorities"));
        List<String> authorities = (List<String>) claims.get("authorities");
        assertTrue(authorities.contains("ROLE_USER"));
    }


}