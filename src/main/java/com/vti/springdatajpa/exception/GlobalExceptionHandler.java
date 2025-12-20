package com.vti.springdatajpa.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRegisterError(RuntimeException ex) {

        switch (ex.getMessage()) {
            case "EMAIL_EXISTS":
                return ResponseEntity.status(409).body("Email already exists");

            case "USERNAME_EXISTS":
                return ResponseEntity.status(409).body("Username already exists");

            case "PHONE_EXISTS":
                return ResponseEntity.status(409).body("Phone already exists");

            default:
                return ResponseEntity.badRequest().body("Invalid request");
        }
    }
}
