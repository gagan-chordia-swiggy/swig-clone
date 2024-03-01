package com.example.catalogservice.repositories;

import com.example.catalogservice.models.Restaurants;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantsRepository extends JpaRepository<Restaurants, String> {}
