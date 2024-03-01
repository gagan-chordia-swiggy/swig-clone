package com.example.catalogservice.repositories;

import com.example.catalogservice.dto.Address;
import com.example.catalogservice.models.Restaurants;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantsRepository extends JpaRepository<Restaurants, String> {
    boolean existsByNameAndAddress(String name, Address address);
}
