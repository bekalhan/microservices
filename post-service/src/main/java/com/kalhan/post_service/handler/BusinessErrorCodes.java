package com.kalhan.post_service.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCodes {
    RESOURCE_NOT_FOUND(300,HttpStatus.BAD_REQUEST,"Resource not found"),
    RESOURCE_NO_ACCESS(300,HttpStatus.BAD_REQUEST,"You have no access this resource"),
    ;
    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code,HttpStatus httpStatus,String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;
    }
}