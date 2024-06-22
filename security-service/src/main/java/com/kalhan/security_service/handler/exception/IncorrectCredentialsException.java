package com.kalhan.security_service.handler.exception;

public class IncorrectCredentialsException extends RuntimeException{
    public IncorrectCredentialsException(String message) {
        super(message);
    }
}
