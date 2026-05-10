package com.bookstore.bookstore_api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException ex) {

        Map<String, Object> error = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    ex.getStatusCode().value(),
                "error",     ex.getStatusCode().toString(),
                "message",   ex.getReason() != null ? ex.getReason() : ex.getMessage()
        );

        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex) {

        Map<String, Object> error = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    HttpStatus.UNAUTHORIZED.value(),
                "error",     "Unauthorized",
                "message",   "Invalid username or password"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex) {

        Map<String, Object> error = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error",     "Internal Server Error",
                "message",   ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}