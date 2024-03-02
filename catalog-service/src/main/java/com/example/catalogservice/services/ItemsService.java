package com.example.catalogservice.services;

import com.example.catalogservice.dto.ApiResponse;
import com.example.catalogservice.dto.ItemRequest;
import com.example.catalogservice.dto.ItemResponse;
import com.example.catalogservice.exceptions.ItemAlreadyExistsException;
import com.example.catalogservice.exceptions.ItemNotFoundException;
import com.example.catalogservice.exceptions.RestaurantNotFoundException;
import com.example.catalogservice.models.Item;
import com.example.catalogservice.models.Restaurant;
import com.example.catalogservice.repositories.ItemsRepository;
import com.example.catalogservice.repositories.RestaurantsRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.catalogservice.constants.Constants.FETCHED;
import static com.example.catalogservice.constants.Constants.ITEM_ADDED;

@Service
@RequiredArgsConstructor
public class ItemsService {
    private final ItemsRepository itemsRepository;
    private final RestaurantsRepository restaurantsRepository;

    public ResponseEntity<ApiResponse> add(String restaurantId, ItemRequest request) {
        Restaurant restaurant = restaurantsRepository.findById(restaurantId)
                .orElseThrow(RestaurantNotFoundException::new);

        if (itemsRepository.existsByNameAndRestaurant(request.getName(), restaurant)) {
            throw new ItemAlreadyExistsException();
        }

        Item item = Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .restaurant(restaurant)
                .build();

        itemsRepository.save(item);

        ApiResponse response = ApiResponse.builder()
                .message(ITEM_ADDED)
                .status(HttpStatus.CREATED)
                .data(Map.of("item", new ItemResponse(item)))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public ResponseEntity<ApiResponse> fetchAll(String restaurantId) {
        Restaurant restaurant = restaurantsRepository.findById(restaurantId)
                .orElseThrow(RestaurantNotFoundException::new);

        List<Item> items = itemsRepository.findAllByRestaurant(restaurant);
        List<ItemResponse> responses = new ArrayList<>();

        for (Item item : items) {
            responses.add(new ItemResponse(item));
        }

        ApiResponse response = ApiResponse.builder()
                .message(FETCHED)
                .status(HttpStatus.OK)
                .data(Map.of("items", responses))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public ResponseEntity<ApiResponse> fetchByName(String restaurantId, String itemName) {
        Restaurant restaurant = restaurantsRepository.findById(restaurantId)
                .orElseThrow(RestaurantNotFoundException::new);

        Item item = itemsRepository.findByNameAndRestaurant(itemName, restaurant)
                .orElseThrow(ItemNotFoundException::new);

        ApiResponse response = ApiResponse.builder()
                .message(FETCHED)
                .status(HttpStatus.OK)
                .data(Map.of("items", new ItemResponse(item)))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
