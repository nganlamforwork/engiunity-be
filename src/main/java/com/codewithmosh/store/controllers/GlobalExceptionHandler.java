/**
 * Author: lamlevungan
 * Date: 21/04/2025
 **/
package com.codewithmosh.store.controllers;

import com.codewithmosh.store.exceptions.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    // Validation of Hibernate
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException exception) {
        var errors = new HashMap<String, String>();
        exception.getConstraintViolations().forEach(violation -> {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Validation @Valid DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException exception){
        var errors = new HashMap<String, String>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(),error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);

    }

    // User not found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException exception) {
        var error = new HashMap<String, String>();
        error.put("error", "User not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Exercise not found
    @ExceptionHandler(ExerciseNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleExerciseNotFoundException(ExerciseNotFoundException exception) {
        var error = new HashMap<String, String>();
        error.put("error", "Exercise not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Exercise not found
    @ExceptionHandler(ResponseNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResponseNotFoundException(ResponseNotFoundException exception) {
        var error = new HashMap<String, String>();
        error.put("error", "Response not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Speaking sesion not found
    @ExceptionHandler(SpeakingSessionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleSpeakingSessionNotFoundException(SpeakingSessionNotFoundException exception) {
        var error = new HashMap<String, String>();
        error.put("error", "Speaking session not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Json parsing from form data
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Map<String, String>> handleJsonProcessingException(JsonProcessingException exception) {
        var error = new HashMap<String, String>();
        error.put("error", "Invalid JSON format: " + exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Save file
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Map<String, String>> handleFileStorageException(FileStorageException exception) {
        var errors = new HashMap<String, String>();
        errors.put("error", exception.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }

    // Create directory
    @ExceptionHandler(DirectoryCreationException.class)
    public ResponseEntity<Map<String, String>> handleDirectoryCreationException(DirectoryCreationException exception) {
        var errors = new HashMap<String, String>();
        errors.put("error", exception.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }


    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime exception occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An error occurred while processing your request: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        logger.error("Exception occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred: " + ex.getMessage()));
    }
    // Unauthorized access (unauthenticated)
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationCredentialsNotFoundException exception) {
        var error = new HashMap<String, String>();
        error.put("error", "Unauthorized access: Authentication credentials not found.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // Forbidden access (authenticated but not enough permissions)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException exception) {
        var error = new HashMap<String, String>();
        error.put("error", "Access denied: You do not have permission to perform this action.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
