package com.example.bankcards.service;

import com.example.bankcards.exception.InvalidTokenException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.function.Function;

public interface TokenService extends UserDetailsService {

    public String generateToken(UserDetails userDetails);

    public String generateToken(UserDetails userDetails, Date expiry);

    public String getUsernameFromToken(String token) throws InvalidTokenException;

    public Date getExpirationDateFromToken(String token) throws InvalidTokenException;

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver)
            throws InvalidTokenException;

    public void saveToken(String token) throws InvalidTokenException;

    public void validateToken(String token) throws InvalidTokenException;

    public void invalidateToken(String token);
}