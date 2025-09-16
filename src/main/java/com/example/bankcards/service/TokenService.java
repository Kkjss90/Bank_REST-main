package com.example.bankcards.service;

import com.example.bankcards.exception.InvalidTokenException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.function.Function;

/**
 * The interface Token service.
 */
public interface TokenService extends UserDetailsService {

    /**
     * Generate token string.
     *
     * @param userDetails the user details
     * @return the string
     */
    public String generateToken(UserDetails userDetails);

    /**
     * Generate token string.
     *
     * @param userDetails the user details
     * @param expiry      the expiry
     * @return the string
     */
    public String generateToken(UserDetails userDetails, Date expiry);

    /**
     * Gets username from token.
     *
     * @param token the token
     * @return the username from token
     * @throws InvalidTokenException the invalid token exception
     */
    public String getUsernameFromToken(String token) throws InvalidTokenException;

    /**
     * Gets expiration date from token.
     *
     * @param token the token
     * @return the expiration date from token
     * @throws InvalidTokenException the invalid token exception
     */
    public Date getExpirationDateFromToken(String token) throws InvalidTokenException;

    /**
     * Gets claim from token.
     *
     * @param <T>            the type parameter
     * @param token          the token
     * @param claimsResolver the claims resolver
     * @return the claim from token
     * @throws InvalidTokenException the invalid token exception
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver)
            throws InvalidTokenException;

    /**
     * Save token.
     *
     * @param token the token
     * @throws InvalidTokenException the invalid token exception
     */
    public void saveToken(String token) throws InvalidTokenException;

    /**
     * Validate token.
     *
     * @param token the token
     * @throws InvalidTokenException the invalid token exception
     */
    public void validateToken(String token) throws InvalidTokenException;

    /**
     * Invalidate token.
     *
     * @param token the token
     */
    public void invalidateToken(String token);
}