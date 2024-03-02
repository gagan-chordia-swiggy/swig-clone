package com.example.catalogservice.exceptions;

import com.example.catalogservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.catalogservice.constants.Constants.RESTAURANT_ALREADY_EXISTS;
import static com.example.catalogservice.constants.Constants.RESTAURANT_NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RestaurantAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleRestaurantAlreadyExistsException() {
        ApiResponse response = ApiResponse.builder()
                .message(RESTAURANT_ALREADY_EXISTS)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = RestaurantNotFoundException.class)
    public ResponseEntity<ApiResponse> handleRRestaurantNotFoundException() {
        ApiResponse response = ApiResponse.builder()
                .message(RESTAURANT_NOT_FOUND)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
