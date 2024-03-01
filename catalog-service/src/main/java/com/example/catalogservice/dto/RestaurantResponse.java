package com.example.catalogservice.dto;

import com.example.catalogservice.models.Restaurants;

public class RestaurantResponse {
    private String id;
    private String name;
    private Address address;

    public RestaurantResponse(Restaurants restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
    }
}
