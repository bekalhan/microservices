package com.kalhan.security_service.handler.exception;

public class UserAlreadyExistException extends RuntimeException{
    private String email;

    public UserAlreadyExistException(String message) {
        super(message);
        this.email = email;
    }
}
