package com.example.bankcards.security;

import com.example.bankcards.exception.InvalidTokenException;
import com.example.bankcards.service.TokenService;
import com.example.bankcards.util.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * The type Jwt authentication filter.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Instantiates a new Jwt authentication filter.
     *
     * @param userDetailsService the user details service
     * @param tokenService       the token service
     */
    @Autowired
    public JwtAuthenticationFilter(UserDetailsService userDetailsService, TokenService tokenService) {
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
    }

    /**
     * Do filter internal.
     *
     * @param request     the request
     * @param response    the response
     * @param filterChain the filter chain
     * @throws IOException the io exception
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws IOException {

        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            val requestTokenHeader = request.getHeader("Authorization");

            if (requestTokenHeader == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (!requestTokenHeader.startsWith("Bearer ")) {
                sendErrorResponse(response, HttpStatus.BAD_REQUEST,
                        "Invalid token format. Token must start with 'Bearer '", request);
                return;
            }

            val token = requestTokenHeader.substring(7);

            tokenService.validateToken(token);
            String username = tokenService.getUsernameFromToken(token);

            val userDetails = userDetailsService.loadUserByUsername(username);
            val authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } catch (InvalidTokenException e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage(), request);
        } catch (Exception e) {
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal authentication error", request);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status,
                                   String message, HttpServletRequest request) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiError apiError = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getWriter(), apiError);
    }
}