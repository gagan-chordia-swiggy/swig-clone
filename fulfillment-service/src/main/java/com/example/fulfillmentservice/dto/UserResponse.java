package com.example.fulfillmentservice.dto;

import com.example.fulfillmentservice.enums.Availability;
import com.example.fulfillmentservice.models.User;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private String username;
    private Address address;

    @Enumerated(EnumType.STRING)
    private Availability availability;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.address = user.getAddress();
        this.availability = user.getAvailability();
    }
}
