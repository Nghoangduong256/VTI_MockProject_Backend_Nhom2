package com.vti.springdatajpa.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}