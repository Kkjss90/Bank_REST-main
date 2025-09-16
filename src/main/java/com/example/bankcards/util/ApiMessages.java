package com.example.bankcards.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The enum Api messages.
 */
@RequiredArgsConstructor
public enum ApiMessages {
    /**
     * The Token already exists error.
     */
    TOKEN_ALREADY_EXISTS_ERROR("Token already exists"),
    /**
     * The Token empty error.
     */
    TOKEN_EMPTY_ERROR("Token is empty"),
    /**
     * The Token expired error.
     */
    TOKEN_EXPIRED_ERROR("Token has expired"),
    /**
     * Token issued success api messages.
     */
    TOKEN_ISSUED_SUCCESS("{ \"token\": \"%s\" }"),
    /**
     * The Token malformed error.
     */
    TOKEN_MALFORMED_ERROR("Token is malformed"),
    /**
     * The Token not found error.
     */
    TOKEN_NOT_FOUND_ERROR("Token not found"),
    /**
     * The Token signature invalid error.
     */
    TOKEN_SIGNATURE_INVALID_ERROR("Token signature is invalid"),
    /**
     * The Token unsupported error.
     */
    TOKEN_UNSUPPORTED_ERROR("Token is not supported"),
    /**
     * The User not found by account.
     */
    USER_NOT_FOUND_BY_ACCOUNT("User not found for the given account number: %s"),
    /**
     * The User not found.
     */
    USER_NOT_FOUND("User not found"),
    /**
     * The Source account not found.
     */
    SOURCE_ACCOUNT_NOT_FOUND("Source account not found"),
    /**
     * The Destination account not found.
     */
    DESTINATION_ACCOUNT_NOT_FOUND("Destination account not found"),
    /**
     * The Transaction not found.
     */
    TRANSACTION_NOT_FOUND("Transaction not found"),
    /**
     * The Transaction failed.
     */
    TRANSACTION_FAILED("Transfer failed"),
    /**
     * The Card not found.
     */
    CARD_NOT_FOUND("Card not found");


    @Getter
    private final String message;

}