package com.example.fulfillmentservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias(value = "BuildingNumber")
    private int buildingNumber;

    @JsonAlias(value = "Street")
    private String street;

    @JsonAlias(value = "Locality")
    private String locality;

    @JsonAlias(value = "City")
    private String city;

    @JsonAlias(value = "State")
    private String state;

    @JsonAlias(value = "Country")
    private String country;

    @Pattern(regexp = "^[1-9]\\d{5}$")
    @JsonAlias(value = "Zipcode")
    private String zipcode;
}
