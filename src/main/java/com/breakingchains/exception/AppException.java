package com.breakingchains.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final Object details;

    public AppException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
        this.details = null;
    }

    public AppException(HttpStatus status, String code, String message, Object details) {
        super(message);
        this.status = status;
        this.code = code;
        this.details = details;
    }

    public static AppException validationError(String message) {
        return new AppException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    public static AppException validationError(String message, Object details) {
        return new AppException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, details);
    }

    public static AppException invalidCredentials(String message) {
        return new AppException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", message);
    }

    public static AppException unauthorized(String message) {
        return new AppException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message);
    }

    public static AppException tokenExpired(String message) {
        return new AppException(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", message);
    }

    public static AppException invalidRefreshToken(String message) {
        return new AppException(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", message);
    }

    public static AppException userExists(String message) {
        return new AppException(HttpStatus.CONFLICT, "USER_EXISTS", message);
    }

    public static AppException notFound(String message) {
        return new AppException(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }
}
