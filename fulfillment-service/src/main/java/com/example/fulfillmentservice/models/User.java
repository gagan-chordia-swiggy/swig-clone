package com.example.fulfillmentservice.models;

import com.example.fulfillmentservice.dto.Address;
import com.example.fulfillmentservice.enums.Availability;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Address address;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Availability availability = Availability.AVAILABLE;
}
