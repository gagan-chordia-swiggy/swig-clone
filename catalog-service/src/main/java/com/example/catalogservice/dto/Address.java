package com.example.catalogservice.dto;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import lombok.*;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Validated
public class Address {
    @Min(value = 1)
    @NonNull
    private Integer buildingNumber;

    @NonNull
    private String street;

    @NonNull
    private String locality;

    @NonNull
    private String city;

    @NonNull
    private String state;

    @NonNull
    private String country;

    @Pattern(regexp = "^[1-9]\\d{5}$")
    @NonNull
    private Integer zipcode;
}
