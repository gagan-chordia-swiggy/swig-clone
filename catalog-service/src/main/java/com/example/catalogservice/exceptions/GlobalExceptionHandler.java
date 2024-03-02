package com.example.catalogservice.exceptions;

import com.example.catalogservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.catalogservice.constants.Constants.ITEM_ALREADY_EXISTS;
import static com.example.catalogservice.constants.Constants.ITEM_NOT_FOUND;
import static com.example.catalogservice.constants.Constants.RESTAURANT_ALREADY_EXISTS;
import static com.example.catalogservice.constants.Constants.RESTAURANT_NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Restaurants
    @ExceptionHandler(value = RestaurantAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleRestaurantAlreadyExistsException() {
        ApiResponse response = ApiResponse.builder()
                .message(RESTAURANT_ALREADY_EXISTS)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = RestaurantNotFoundException.class)
    public ResponseEntity<ApiResponse> handleRestaurantNotFoundException() {
        ApiResponse response = ApiResponse.builder()
                .message(RESTAURANT_NOT_FOUND)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Items
    @ExceptionHandler(value = ItemAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleItemAlreadyExistsException() {
        ApiResponse response = ApiResponse.builder()
                .message(ITEM_ALREADY_EXISTS)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = ItemNotFoundException.class)
    public ResponseEntity<ApiResponse> handleItemNotFoundException() {
        ApiResponse response = ApiResponse.builder()
                .message(ITEM_NOT_FOUND)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // General
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ApiResponse response = ApiResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(MethodArgumentNotValidException e) {
        ApiResponse response = ApiResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
