package com.kalhan.security_service.handler;

import com.kalhan.security_service.handler.exception.InactiveAccountException;
import com.kalhan.security_service.handler.exception.InvalidTokenException;
import com.kalhan.security_service.handler.exception.TokenExpiredException;
import com.kalhan.security_service.handler.exception.UserAlreadyExistException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.kalhan.security_service.handler.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_LOCKED.getCode())
                                .businessErrorDescription(ACCOUNT_LOCKED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_DISABLED.getCode())
                                .businessErrorDescription(ACCOUNT_DISABLED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException() {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BAD_CREDENTIALS.getCode())
                                .businessErrorDescription(BAD_CREDENTIALS.getDescription())
                                .error("email and / or Password is incorrect")
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    //var fieldName = ((FieldError) error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidTokenException(InvalidTokenException exp) {
        return ResponseEntity
                .status(INVALID_TOKEN.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(INVALID_TOKEN.getCode())
                                .businessErrorDescription(INVALID_TOKEN.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ExceptionResponse> handleTokenExpiredException(TokenExpiredException exp) {
        return ResponseEntity
                .status(TOKEN_EXPIRED.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(TOKEN_EXPIRED.getCode())
                                .businessErrorDescription(TOKEN_EXPIRED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ExceptionResponse> handleTokenExpiredException(UserAlreadyExistException exp) {
        return ResponseEntity
                .status(USER_ALREADY_EXIST.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(USER_ALREADY_EXIST.getCode())
                                .businessErrorDescription(USER_ALREADY_EXIST.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<ExceptionResponse> handleTokenExpiredException(InactiveAccountException exp) {
        return ResponseEntity
                .status(INACTIVE_ACCOUNT.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(INACTIVE_ACCOUNT.getCode())
                                .businessErrorDescription(INACTIVE_ACCOUNT.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }
}
