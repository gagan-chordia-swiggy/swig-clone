package com.example.fulfillmentservice.controllers;

import com.example.fulfillmentservice.dto.ApiResponse;
import com.example.fulfillmentservice.dto.DeliveryRequest;
import com.example.fulfillmentservice.services.DeliveryService;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<ApiResponse> facilitate(@RequestBody DeliveryRequest request) throws JsonProcessingException {
        return this.deliveryService.facilitate(request);
    }

    @PutMapping("/{deliveryId}")
    public ResponseEntity<ApiResponse> updateStatus(@PathVariable(value = "deliveryId") String id) {
        return this.deliveryService.updateStatus(id);
    }
}
