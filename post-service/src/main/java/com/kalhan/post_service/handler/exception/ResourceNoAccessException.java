package com.kalhan.post_service.handler.exception;

public class ResourceNoAccessException extends RuntimeException{
    public ResourceNoAccessException(String message) {
        super(message);
    }
}
