package com.example.catalogservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    @NonNull
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @Min(value = 100)
    private double price;
}
