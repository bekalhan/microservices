package com.kalhan.user_service.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCodes {
    //NO_CODE(0,HttpStatus.NOT_IMPLEMENTED,"No code"),
    //INCORRECT_CURRENT_PASSWORD(300,HttpStatus.BAD_REQUEST,"Current password is incorrect"),
    //NEW_PASSWORD_DOES_NOT_MATCH(301,HttpStatus.BAD_REQUEST,"The new password does not match"),
    INCORRECT_CREDENTIALS(309,HttpStatus.BAD_REQUEST,"Incorrect credentials"),
    ACCOUNT_DISABLED(303,HttpStatus.FORBIDDEN,"User account is disabled"),
    BAD_CREDENTIALS(304,HttpStatus.FORBIDDEN,"email and / or password is incorrect"),
    ACCOUNT_LOCKED(302,HttpStatus.FORBIDDEN,"User account is locked"),
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