package com.example.catalogservice.services;

import com.example.catalogservice.dto.ApiResponse;
import com.example.catalogservice.dto.RestaurantRequest;
import com.example.catalogservice.dto.RestaurantResponse;
import com.example.catalogservice.exceptions.RestaurantAlreadyExistsException;
import com.example.catalogservice.exceptions.RestaurantNotFoundException;
import com.example.catalogservice.models.Restaurants;
import com.example.catalogservice.repositories.RestaurantsRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.catalogservice.constants.Constants.FETCHED;
import static com.example.catalogservice.constants.Constants.RESTAURANT_CREATED;

@Service
@RequiredArgsConstructor
public class RestaurantsService {
    private final RestaurantsRepository restaurantsRepository;

    public ResponseEntity<ApiResponse> create(RestaurantRequest request) {
        if (restaurantsRepository.existsByNameAndAddress(request.getName(), request.getAddress())) {
            throw new RestaurantAlreadyExistsException();
        }

        Restaurants restaurant = Restaurants.builder()
                .name(request.getName())
                .address(request.getAddress())
                .build();

        restaurant = restaurantsRepository.save(restaurant);

        RestaurantResponse restaurantResponse = new RestaurantResponse(restaurant);

        ApiResponse response = ApiResponse.builder()
                .message(RESTAURANT_CREATED)
                .status(HttpStatus.CREATED)
                .data(Map.of("restaurant", restaurantResponse))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public ResponseEntity<ApiResponse> fetchAll() {
        List<Restaurants> restaurants = restaurantsRepository.findAll();
        List<RestaurantResponse> responses = new ArrayList<>();

        for (Restaurants restaurant: restaurants) {
            responses.add(new RestaurantResponse(restaurant));
        }

        ApiResponse response = ApiResponse.builder()
                .message(FETCHED)
                .status(HttpStatus.OK)
                .data(Map.of("restaurants", responses))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public ResponseEntity<ApiResponse> fetchById(String id) {
        Restaurants restaurant = restaurantsRepository.findById(id)
                .orElseThrow(RestaurantNotFoundException::new);

        ApiResponse response = ApiResponse.builder()
                .message(FETCHED)
                .status(HttpStatus.OK)
                .data(Map.of("restaurant", new RestaurantResponse(restaurant)))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
