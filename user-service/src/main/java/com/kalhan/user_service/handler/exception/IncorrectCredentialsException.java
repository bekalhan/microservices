package com.kalhan.user_service.handler.exception;

public class IncorrectCredentialsException extends RuntimeException{
    public IncorrectCredentialsException(String message) {
        super(message);
    }
}

