package com.example.bankcards.service;

import com.example.bankcards.dto.request.SignInRequest;
import com.example.bankcards.dto.request.SignUpRequest;
import com.example.bankcards.dto.response.JwtAuthenticationResponse;


public interface AuthService {
    public JwtAuthenticationResponse signUp(SignUpRequest request);

    public JwtAuthenticationResponse signIn(SignInRequest request);

}