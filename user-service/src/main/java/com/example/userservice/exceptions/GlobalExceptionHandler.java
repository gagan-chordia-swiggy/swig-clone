package com.example.userservice.exceptions;

import com.example.userservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException() {
        ApiResponse response = ApiResponse
                .builder()
                .message("User not found with the given username")
                .status(HttpStatus.NOT_FOUND)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
