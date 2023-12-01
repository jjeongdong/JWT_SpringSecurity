package com.example.jwt_springsecurity.exception;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> dtoValidation(final MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler({CustomException.class,
            NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            AuthenticationException.class})
    public ResponseEntity<Object> handleException(Exception e) {

        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = determineHttpStatus(e);

        Map<String, Object> errors = new HashMap<>();
        errors.put("Status", getStatus(e));
        errors.put("ErrorMessage", getErrorMessage(e));
        errors.put("Date", String.valueOf(new Date()));

        return new ResponseEntity<>(errors, headers, status);
    }

    private String getStatus(Exception e) {
        if (e instanceof CustomException) {
            return ((CustomException) e).getErrorCode().getStatus();
        } else if (e instanceof NoHandlerFoundException) {
            return HttpStatus.NOT_FOUND.name();
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return HttpStatus.METHOD_NOT_ALLOWED.name();
        } else if (e instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED.name();
        } else if (e instanceof ChangeSetPersister.NotFoundException) {
            return HttpStatus.NOT_FOUND.name();
        }
        return null;
    }

    private HttpStatus determineHttpStatus(Exception e) {
        if (e instanceof CustomException) {
            return HttpStatus.BAD_REQUEST;
        } else if (e instanceof NoHandlerFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return HttpStatus.METHOD_NOT_ALLOWED;
        } else if (e instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (e instanceof ChangeSetPersister.NotFoundException) {
            return HttpStatus.NOT_FOUND;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String getErrorMessage(Exception e) {
        if (e instanceof CustomException) {
            return ((CustomException) e).getErrorCode().getMessage();
        } else if (e instanceof NoHandlerFoundException) {
            return "해당 요청을 찾을 수 없습니다.";
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return "지원되지 않는 HTTP 메서드입니다.";
        } else if (e instanceof AuthenticationException) {
            return "아이디 또는 비밀번호가 올바르지 않습니다.";
        } else if (e instanceof ChangeSetPersister.NotFoundException) {
            return "주소가 올바르지 않습니다";
        }

        return "Internal Server Error";
    }

}