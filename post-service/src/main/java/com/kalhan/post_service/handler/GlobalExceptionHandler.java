package com.kalhan.post_service.handler;

import com.kalhan.post_service.handler.exception.ResourceNoAccessException;
import com.kalhan.post_service.handler.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.kalhan.post_service.handler.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleTokenExpiredException(ResourceNotFoundException exp) {
        return ResponseEntity
                .status(RESOURCE_NOT_FOUND.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(RESOURCE_NOT_FOUND.getCode())
                                .businessErrorDescription(RESOURCE_NOT_FOUND.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(ResourceNoAccessException.class)
    public ResponseEntity<ExceptionResponse> handleTokenExpiredException(ResourceNoAccessException exp) {
        return ResponseEntity
                .status(RESOURCE_NO_ACCESS.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(RESOURCE_NO_ACCESS.getCode())
                                .businessErrorDescription(RESOURCE_NO_ACCESS.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

}
