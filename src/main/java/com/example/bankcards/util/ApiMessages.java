package com.example.bankcards.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApiMessages {
    TOKEN_ALREADY_EXISTS_ERROR("Token already exists"),
    TOKEN_EMPTY_ERROR("Token is empty"),
    TOKEN_EXPIRED_ERROR("Token has expired"),
    TOKEN_ISSUED_SUCCESS("{ \"token\": \"%s\" }"),
    TOKEN_MALFORMED_ERROR("Token is malformed"),
    TOKEN_NOT_FOUND_ERROR("Token not found"),
    TOKEN_SIGNATURE_INVALID_ERROR("Token signature is invalid"),
    TOKEN_UNSUPPORTED_ERROR("Token is not supported"),
    USER_NOT_FOUND_BY_ACCOUNT("User not found for the given account number: %s");

    @Getter
    private final String message;

}