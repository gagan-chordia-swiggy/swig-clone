package com.example.catalogservice.services;

import com.example.catalogservice.dto.ApiResponse;
import com.example.catalogservice.dto.ItemRequest;
import com.example.catalogservice.exceptions.ItemAlreadyExistsException;
import com.example.catalogservice.exceptions.ItemNotFoundException;
import com.example.catalogservice.exceptions.RestaurantNotFoundException;
import com.example.catalogservice.models.Item;
import com.example.catalogservice.models.Restaurant;
import com.example.catalogservice.repositories.ItemsRepository;
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
import static com.example.catalogservice.constants.Constants.ITEM_ADDED;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class ItemsServiceTest {
    @Mock
    private ItemsRepository itemsRepository;

    @Mock
    private RestaurantsRepository restaurantsRepository;

    @InjectMocks
    private ItemsService itemsService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void test_addItemsToRestaurant_successfully() {
        ItemRequest request = ItemRequest.builder()
                .name("item")
                .price(200.0)
                .build();
        String restaurantId = "abc";
        Restaurant restaurant = mock(Restaurant.class);

        when(restaurantsRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(itemsRepository.existsByNameAndRestaurant("item", restaurant)).thenReturn(false);
        ResponseEntity<ApiResponse> response = itemsService.add(restaurantId, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(ITEM_ADDED, Objects.requireNonNull(response.getBody()).getMessage());
        verify(restaurantsRepository, times(1)).findById(restaurantId);
        verify(itemsRepository, times(1)).existsByNameAndRestaurant("item", restaurant);
        verify(itemsRepository, times(1)).save(any(Item.class));
    }

    @Test
    void test_restaurantNotFoundWhileAddingTheItem_throwsException() {
        ItemRequest request = ItemRequest.builder()
                .name("item")
                .price(200.0)
                .build();
        String restaurantId = "abc";
        Restaurant restaurant = mock(Restaurant.class);

        when(restaurantsRepository.findById(restaurantId)).thenThrow(new RestaurantNotFoundException());

        assertThrows(RestaurantNotFoundException.class, () -> itemsService.add(restaurantId, request));
        verify(restaurantsRepository, times(1)).findById(restaurantId);
        verify(itemsRepository, never()).existsByNameAndRestaurant("item", restaurant);
        verify(itemsRepository, never()).save(any(Item.class));
    }

    @Test
    void test_itemAlreadyPresentInRestaurant_throwsException() {
        ItemRequest request = ItemRequest.builder()
                .name("item")
                .price(200.0)
                .build();
        String restaurantId = "abc";
        Restaurant restaurant = mock(Restaurant.class);

        when(restaurantsRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(itemsRepository.existsByNameAndRestaurant("item", restaurant)).thenReturn(true);

        assertThrows(ItemAlreadyExistsException.class, () -> itemsService.add(restaurantId, request));
        verify(restaurantsRepository, times(1)).findById(restaurantId);
        verify(itemsRepository, times(1)).existsByNameAndRestaurant("item", restaurant);
        verify(itemsRepository, never()).save(any(Item.class));
    }

    @Test
    void test_fetchAllItemsByRestaurant_successfully() {
        String restaurantId = "abc";
        Restaurant restaurant = mock(Restaurant.class);
        Item firstItem = mock(Item.class);
        Item secondItem = mock(Item.class);
        List<Item> items = List.of(firstItem, secondItem);

        when(restaurantsRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(itemsRepository.findAllByRestaurant(restaurant)).thenReturn(items);
        when(firstItem.getRestaurant()).thenReturn(restaurant);
        when(secondItem.getRestaurant()).thenReturn(restaurant);
        ResponseEntity<ApiResponse> response = itemsService.fetchAll(restaurantId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FETCHED, Objects.requireNonNull(response.getBody()).getMessage());
        verify(restaurantsRepository, times(1)).findById(restaurantId);
        verify(itemsRepository, times(1)).findAllByRestaurant(restaurant);
    }

    @Test
    void test_restaurantNotFoundWhileFetchingAllItems_throwsException() {
        String restaurantId = "abc";

        when(restaurantsRepository.findById(restaurantId)).thenThrow(new RestaurantNotFoundException());

        assertThrows(RestaurantNotFoundException.class, () -> itemsService.fetchAll(restaurantId));
        verify(restaurantsRepository, times(1)).findById(restaurantId);
        verify(itemsRepository, never()).findAllByRestaurant(any(Restaurant.class));
    }

    @Test
    void test_fetchItemByIdFromARestaurant_successfully() {
        String restaurantId = "abc";
        String itemName = "def";
        Restaurant restaurant = mock(Restaurant.class);
        Item item = mock(Item.class);

        when(restaurantsRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(itemsRepository.findByNameAndRestaurant(itemName, restaurant)).thenReturn(Optional.of(item));
        when(item.getRestaurant()).thenReturn(restaurant);
        ResponseEntity<ApiResponse> response = itemsService.fetchByName(restaurantId, itemName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FETCHED, Objects.requireNonNull(response.getBody()).getMessage());
        verify(restaurantsRepository, times(1)).findById(restaurantId);
        verify(itemsRepository, times(1)).findByNameAndRestaurant(itemName, restaurant);
    }

    @Test
    void test_cannotFindRestaurantWhileFetchingItem_throwsError() {
        String restaurantId = "abc";
        String itemName = "def";
        Restaurant restaurant = mock(Restaurant.class);

        when(restaurantsRepository.findById(restaurantId)).thenThrow(new RestaurantNotFoundException());

        assertThrows(RestaurantNotFoundException.class, () -> itemsService.fetchByName(restaurantId, itemName));
        verify(restaurantsRepository, times(1)).findById(restaurantId);
        verify(itemsRepository, never()).findByNameAndRestaurant(itemName, restaurant);
    }

    @Test
    void test_cannotFindItemInRestaurant_throwsError() {
        String restaurantId = "abc";
        String itemName = "def";
        Restaurant restaurant = mock(Restaurant.class);

        when(restaurantsRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(itemsRepository.findByNameAndRestaurant(itemName, restaurant)).thenThrow(new ItemNotFoundException());

        assertThrows(ItemNotFoundException.class, () -> itemsService.fetchByName(restaurantId, itemName));
        verify(restaurantsRepository, times(1)).findById(restaurantId);
        verify(itemsRepository, times(1)).findByNameAndRestaurant(itemName, restaurant);
    }
}