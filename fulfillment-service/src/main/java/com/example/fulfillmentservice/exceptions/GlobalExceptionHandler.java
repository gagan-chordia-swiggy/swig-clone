package com.example.fulfillmentservice.exceptions;

import com.example.fulfillmentservice.dto.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.fulfillmentservice.constants.Constants.USER_ALREADY_EXISTS;
import static com.example.fulfillmentservice.constants.Constants.USER_NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // User
    @ExceptionHandler(value = UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleUserAlreadyExistsException() {
        ApiResponse response = ApiResponse.builder()
                .message(USER_ALREADY_EXISTS)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException() {
        ApiResponse response = ApiResponse.builder()
                .message(USER_NOT_FOUND)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
