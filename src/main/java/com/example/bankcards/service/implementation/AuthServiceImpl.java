package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.request.SignInRequest;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.JwtAuthenticationResponse;
import com.example.bankcards.entity.Token;
import com.example.bankcards.exception.InvalidTokenException;
import com.example.bankcards.repository.TokenRepository;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.TokenService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.entity.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Autowired
    private final UserService userService;
    @Autowired
    private final TokenService jwtService;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final TokenRepository tokenRepository;

    @SneakyThrows
    public JwtAuthenticationResponse signUp(UserRequest userRequest) {

        userService.createUser(userRequest);

        var jwt = jwtService.generateToken(userService.getUserByUsername(userRequest.getUsername()));

        jwtService.saveToken(jwt);
        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

//        var user = userService
//                .userDetailsService()
//                .loadUserByUsername(request.getUsername());
        User user = userService.getUserByUsername(request.getUsername());

        //var jwt = jwtService.generateToken(user);
        Token jwt = tokenRepository.findByUser(user);
        try {
            jwtService.validateToken(jwt.getToken());
        }
        catch (Exception e) {
            String newToken = jwtService.generateToken(user);

            try {
                jwtService.saveToken(newToken);
            } catch (InvalidTokenException ex) {
                throw new RuntimeException(ex);
            }
            return new JwtAuthenticationResponse(newToken);
        }

        return new JwtAuthenticationResponse(jwt.getToken());
    }
}