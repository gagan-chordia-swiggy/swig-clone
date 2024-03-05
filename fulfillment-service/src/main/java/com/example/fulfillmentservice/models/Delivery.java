package com.example.fulfillmentservice.models;

import com.example.fulfillmentservice.dto.Address;
import com.example.fulfillmentservice.enums.DeliveryStatus;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private long orderId;

    @ManyToOne
    @JoinColumn(name = "executive_id")
    private User user;

    @AttributeOverrides(value = {
            @AttributeOverride(name = "buildingNumber", column = @Column(name = "r_buildingNumber")),
            @AttributeOverride(name = "street", column = @Column(name = "r_street")),
            @AttributeOverride(name = "locality", column = @Column(name = "r_locality")),
            @AttributeOverride(name = "city", column = @Column(name = "r_city")),
            @AttributeOverride(name = "state", column = @Column(name = "r_state")),
            @AttributeOverride(name = "country", column = @Column(name = "r_country")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "r_zipcode")),
    })
    private Address restaurantAddress;

    @AttributeOverrides(value = {
            @AttributeOverride(name = "buildingNumber", column = @Column(name = "c_buildingNumber")),
            @AttributeOverride(name = "street", column = @Column(name = "c_street")),
            @AttributeOverride(name = "locality", column = @Column(name = "c_locality")),
            @AttributeOverride(name = "city", column = @Column(name = "c_city")),
            @AttributeOverride(name = "state", column = @Column(name = "c_state")),
            @AttributeOverride(name = "country", column = @Column(name = "c_country")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "c_zipcode")),
    })
    private Address customerAddress;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status = DeliveryStatus.PACKED;
}
