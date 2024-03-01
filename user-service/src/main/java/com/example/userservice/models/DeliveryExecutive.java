package com.example.userservice.models;

import com.example.userservice.enums.Availability;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DeliveryExecutive extends User {
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column
    private Availability availability = Availability.AVAILABLE;
}
