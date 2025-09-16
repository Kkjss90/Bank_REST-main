package com.example.bankcards.service;

import com.example.bankcards.dto.request.SignInRequest;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.JwtAuthenticationResponse;
import com.example.bankcards.exception.InvalidTokenException;


/**
 * The interface Auth service.
 */
public interface AuthService {
    /**
     * Sign up jwt authentication response.
     *
     * @param userRequest the user request
     * @return the jwt authentication response
     */
    public JwtAuthenticationResponse signUp(UserRequest userRequest);

    /**
     * Sign in jwt authentication response.
     *
     * @param request the request
     * @return the jwt authentication response
     * @throws InvalidTokenException the invalid token exception
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) throws InvalidTokenException;

}