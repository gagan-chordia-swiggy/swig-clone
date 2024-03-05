package com.example.fulfillmentservice.exceptions;

import com.example.fulfillmentservice.dto.ApiResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import static com.example.fulfillmentservice.constants.Constants.DELIVERY_NOT_FOUND;
import static com.example.fulfillmentservice.constants.Constants.NO_EXECUTIVE_NEARBY;
import static com.example.fulfillmentservice.constants.Constants.ORDER_ALREADY_DELIVERED;
import static com.example.fulfillmentservice.constants.Constants.ORDER_ALREADY_FACILITATED;
import static com.example.fulfillmentservice.constants.Constants.UNAUTHORIZED_STATUS_UPDATE;
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

    // Delivery
    @ExceptionHandler(value = DeliveryNotFoundException.class)
    public ResponseEntity<ApiResponse> handleDeliveryNotFoundException() {
        ApiResponse response = ApiResponse.builder()
                .message(DELIVERY_NOT_FOUND)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = NoExecutiveNearbyException.class)
    public ResponseEntity<ApiResponse> handleNoExecutiveNearbyException() {
        ApiResponse response = ApiResponse.builder()
                .message(NO_EXECUTIVE_NEARBY)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = OrderHasAlreadyBeenFacilitatedException.class)
    public ResponseEntity<ApiResponse> handleOrderHasAlreadyBeenFacilitatedException() {
        ApiResponse response = ApiResponse.builder()
                .message(ORDER_ALREADY_FACILITATED)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = OrderHasBeenAlreadyDeliveredException.class)
    public ResponseEntity<ApiResponse> handleOrderHasBeenAlreadyDeliveredException() {
        ApiResponse response = ApiResponse.builder()
                .message(ORDER_ALREADY_DELIVERED)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = UnauthorizedStatusUpdateException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedStatusUpdateException() {
        ApiResponse response = ApiResponse.builder()
                .message(UNAUTHORIZED_STATUS_UPDATE)
                .status(HttpStatus.UNAUTHORIZED)
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

    @ExceptionHandler(value = JsonProcessingException.class)
    public ResponseEntity<ApiResponse> handleJsonProcessingException(JsonProcessingException e) {
        ApiResponse response = ApiResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
}
