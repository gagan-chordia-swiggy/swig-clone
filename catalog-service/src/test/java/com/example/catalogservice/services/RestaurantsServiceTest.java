package com.example.catalogservice.services;

import com.example.catalogservice.dto.Address;
import com.example.catalogservice.dto.ApiResponse;
import com.example.catalogservice.dto.RestaurantRequest;
import com.example.catalogservice.exceptions.RestaurantAlreadyExistsException;
import com.example.catalogservice.exceptions.RestaurantNotFoundException;
import com.example.catalogservice.models.Restaurant;
import com.example.catalogservice.repositories.RestaurantsRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.catalogservice.constants.Constants.FETCHED;
import static com.example.catalogservice.constants.Constants.RESTAURANT_CREATED;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class RestaurantsServiceTest {
    @Mock
    private RestaurantsRepository restaurantsRepository;

    @InjectMocks
    private RestaurantsService restaurantsService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void test_RestaurantIsCreated_successfully() {
        RestaurantRequest request = RestaurantRequest.builder()
                .name("restaurant")
                .address(mock(Address.class))
                .build();
        Restaurant restaurant = mock(Restaurant.class);

        when(restaurantsRepository.existsByNameAndAddress(request.getName(), request.getAddress())).thenReturn(false);
        when(restaurantsRepository.save(any(Restaurant.class))).thenReturn(restaurant);
        ResponseEntity<ApiResponse> response = restaurantsService.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(RESTAURANT_CREATED, Objects.requireNonNull(response.getBody()).getMessage());
        verify(restaurantsRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void test_restaurantWithSameNameAlreadyExistsInSameAddress_throwsException() {
        RestaurantRequest request = RestaurantRequest.builder()
                .name("restaurant")
                .address(mock(Address.class))
                .build();

        when(restaurantsRepository.existsByNameAndAddress(request.getName(), request.getAddress())).thenReturn(true);

        assertThrows(RestaurantAlreadyExistsException.class, () -> restaurantsService.create(request));
        verify(restaurantsRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void test_fetchAllRestaurants() {
        Restaurant firstRestaurant = mock(Restaurant.class);
        Restaurant secondRestaurant = mock(Restaurant.class);
        Restaurant thirdRestaurant = mock(Restaurant.class);
        List<Restaurant> restaurants = List.of(firstRestaurant, secondRestaurant, thirdRestaurant);

        when(restaurantsRepository.findAll()).thenReturn(restaurants);
        ResponseEntity<ApiResponse> response = restaurantsService.fetchAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FETCHED, Objects.requireNonNull(response.getBody()).getMessage());
        verify(restaurantsRepository, times(1)).findAll();
    }

    @Test
    void test_fetchRestaurantById_successfully() {
        Restaurant restaurant = mock(Restaurant.class);
        String restaurantId = "id";

        when(restaurantsRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        ResponseEntity<ApiResponse> response = restaurantsService.fetchById(restaurantId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FETCHED, Objects.requireNonNull(response.getBody()).getMessage());
        verify(restaurantsRepository, times(1)).findById(restaurantId);
    }

    @Test
    void test_restaurantNotFoundWhileFetchingById_throwsException() {
        String restaurantId = "id";

        when(restaurantsRepository.findById(restaurantId)).thenThrow(new RestaurantNotFoundException());

        assertThrows(RestaurantNotFoundException.class, () -> restaurantsService.fetchById(restaurantId));
        verify(restaurantsRepository, times(1)).findById(restaurantId);
    }
}