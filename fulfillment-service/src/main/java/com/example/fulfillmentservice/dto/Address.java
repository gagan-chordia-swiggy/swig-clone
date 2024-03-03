package com.example.fulfillmentservice.dto;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Address {
    @Min(value = 1)
    private int buildingNumber;

    private String street;
    private String locality;
    private String city;
    private String state;
    private String country;

    @Pattern(regexp = "^[1-9]\\d{5}$")
    private String zipcode;
}
