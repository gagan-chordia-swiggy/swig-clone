package com.example.catalogservice.repositories;

import com.example.catalogservice.models.Item;
import com.example.catalogservice.models.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemsRepository extends JpaRepository<Item, String> {
    boolean existsByNameAndRestaurant(String name, Restaurant restaurant);
    List<Item> findAllByRestaurant(Restaurant restaurant);
    Optional<Item> findByNameAndRestaurant(String name, Restaurant restaurant);
}
