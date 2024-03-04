package com.example.fulfillmentservice.controllers;

import com.example.fulfillmentservice.dto.ApiResponse;
import com.example.fulfillmentservice.dto.RegistrationRequest;
import com.example.fulfillmentservice.services.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegistrationRequest request) {
        return this.userService.register(request);
    }
}
