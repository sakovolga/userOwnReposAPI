package com.example.userOwnReposAPI.exception.handler;

import com.example.userOwnReposAPI.dto.ErrorResponse;
import com.example.userOwnReposAPI.exception.UserNotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        log.warn("User not found: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(404, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleFeignNotFound(FeignException.NotFound e) {
        log.warn("GitHub user not found");
        ErrorResponse errorResponse = new ErrorResponse(404, "GitHub user not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
