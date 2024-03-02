package com.example.catalogservice.dto;

import com.example.catalogservice.models.Items;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponse {
    private String id;

    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    private String restaurantId;

    private double price;

    public ItemResponse(Items item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.restaurantId = item.getRestaurant().getId();
    }
}
