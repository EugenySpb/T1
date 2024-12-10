package ru.novikov.T1.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, Object>> controllerResourceNotFoundException(TaskNotFoundException e) {
        logger.error("Resource not found: {}", e.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> controllerValidationExceptions(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        logger.error("Validation error: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed: " + message);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> controllerAuthenticationException(AuthenticationException e) {
        logger.error("Unauthorized error: {}", e.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized: " + e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> controllerAccessDeniedException(AccessDeniedException e) {
        logger.error("Access denied: {}", e.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> controllerMainException(Exception e) {
        logger.error("Unexpected error: {}", e.getMessage(), e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
