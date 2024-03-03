package com.example.fulfillmentservice.dto;

import com.example.fulfillmentservice.enums.Availability;
import com.example.fulfillmentservice.models.User;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class UserResponse {
    private final String id;
    private final String name;
    private final String username;
    private final Address address;

    @Enumerated(EnumType.STRING)
    private final Availability availability;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.address = user.getAddress();
        this.availability = user.getAvailability();
    }
}
