package com.breakingchains.exception;

import com.breakingchains.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {
        log.warn("Business Exception [{} {}] -> Code: {}, Status: {}, Message: {}",
                request.getMethod(), request.getRequestURI(), ex.getCode(), ex.getStatus(), ex.getMessage());

        ApiErrorResponse response = ApiErrorResponse.of(ex.getCode(), ex.getMessage(), ex.getDetails());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("Validation Failure [{} {}] -> Errors: {}", request.getMethod(), request.getRequestURI(), errors);

        ApiErrorResponse response = ApiErrorResponse.of(
                "VALIDATION_ERROR",
                "Validation failed for request body",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String field = violation.getPropertyPath().toString();
            errors.put(field, violation.getMessage());
        }

        log.warn("Constraint Violation [{} {}] -> Errors: {}", request.getMethod(), request.getRequestURI(), errors);

        ApiErrorResponse response = ApiErrorResponse.of(
                "VALIDATION_ERROR",
                "Constraint validation failed",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Malformed JSON Body [{} {}] -> {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        ApiErrorResponse response = ApiErrorResponse.of(
                "MALFORMED_JSON",
                "Required request body is missing or contains malformed JSON data"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format("Parameter '%s' should be of type '%s'", 
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "valid type");
        
        log.warn("Parameter Mismatch [{} {}] -> {}", request.getMethod(), request.getRequestURI(), message);

        ApiErrorResponse response = ApiErrorResponse.of("INVALID_PARAMETER", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("HTTP Method Not Supported [{} {}] -> Allowed methods: {}", 
                request.getMethod(), request.getRequestURI(), ex.getSupportedHttpMethods());

        ApiErrorResponse response = ApiErrorResponse.of(
                "METHOD_NOT_ALLOWED",
                String.format("Method '%s' is not supported for this endpoint", request.getMethod())
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access Denied [{} {}] -> {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        ApiErrorResponse response = ApiErrorResponse.of(
                "FORBIDDEN",
                "You do not have permission to access this resource"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("Resource Not Found [{} {}]", request.getMethod(), request.getRequestURI());

        ApiErrorResponse response = ApiErrorResponse.of(
                "NOT_FOUND",
                String.format("The requested path '%s' does not exist", request.getRequestURI())
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled Exception [{} {}] -> Critical Server Failure", request.getMethod(), request.getRequestURI(), ex);

        ApiErrorResponse response = ApiErrorResponse.of(
                "INTERNAL_SERVER_ERROR",
                "An unexpected internal server error occurred. Please try again later."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
