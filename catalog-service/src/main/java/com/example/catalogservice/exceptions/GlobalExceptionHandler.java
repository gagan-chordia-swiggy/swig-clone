package com.example.catalogservice.exceptions;

import com.example.catalogservice.dto.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .status(HttpStatus.BAD_REQUEST)
                .data(Map.of("error", RESTAURANT_ALREADY_EXISTS))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = RestaurantNotFoundException.class)
    public ResponseEntity<ApiResponse> handleRestaurantNotFoundException() {
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .data(Map.of("error", RESTAURANT_NOT_FOUND))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Items
    @ExceptionHandler(value = ItemAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleItemAlreadyExistsException() {
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .data(Map.of("error", ITEM_ALREADY_EXISTS))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = ItemNotFoundException.class)
    public ResponseEntity<ApiResponse> handleItemNotFoundException() {
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .data(Map.of("error", ITEM_NOT_FOUND))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // General
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .data(Map.of("error", e.getMostSpecificCause().getMessage()))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).toList();

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .data(this.getErrorsMap(errors))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
}
