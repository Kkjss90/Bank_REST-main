package com.example.bankcards.service;

import com.example.bankcards.dto.request.SignInRequest;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.JwtAuthenticationResponse;
import com.example.bankcards.exception.InvalidTokenException;


public interface AuthService {
    public JwtAuthenticationResponse signUp(UserRequest userRequest);

    public JwtAuthenticationResponse signIn(SignInRequest request) throws InvalidTokenException;

}