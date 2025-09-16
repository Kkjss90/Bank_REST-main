package com.example.bankcards.util;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * The type Api error.
 */
@Getter
@Setter
public class ApiError {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, Object> details;

    /**
     * Instantiates a new Api error.
     *
     * @param status  the status
     * @param error   the error
     * @param message the message
     * @param path    the path
     */
    public ApiError(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    /**
     * Instantiates a new Api error.
     *
     * @param status  the status
     * @param error   the error
     * @param message the message
     * @param path    the path
     * @param details the details
     */
    public ApiError(int status, String error, String message, String path, Map<String, Object> details) {
        this(status, error, message, path);
        this.details = details;
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiError apiError = (ApiError) o;
        return status == apiError.status &&
                Objects.equals(error, apiError.error) &&
                Objects.equals(message, apiError.message) &&
                Objects.equals(timestamp, apiError.timestamp) &&
                Objects.equals(path, apiError.path) &&
                Objects.equals(details, apiError.details);
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(status, error, message, timestamp, path, details);
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "ApiError{" +
                "status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", path='" + path + '\'' +
                ", details=" + details +
                '}';
    }
}