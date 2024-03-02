package com.example.catalogservice.controllers;

import com.example.catalogservice.dto.Address;
import com.example.catalogservice.dto.RestaurantRequest;
import com.example.catalogservice.exceptions.RestaurantAlreadyExistsException;
import com.example.catalogservice.exceptions.RestaurantNotFoundException;
import com.example.catalogservice.services.RestaurantsService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RestaurantsControllerTest {
    @MockBean
    private RestaurantsService restaurantsService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void test_RestaurantIsCreated() throws Exception {
        Address address = Address.builder()
                .buildingNumber(2)
                .city("abc")
                .state("def")
                .country("ssw")
                .locality("sdw")
                .street("we")
                .zipcode("600001")
                .build();
        RestaurantRequest request = RestaurantRequest.builder()
                .name("name")
                .address(address)
                .build();
        String req = objectMapper.writeValueAsString(request);

        when(restaurantsService.create(request)).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        mvc.perform(post("/api/v1/restaurants")
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
        verify(restaurantsService, times(1)).create(request);
    }

    @Test
    void test_anyUserCannotCreateARestaurant_unauthorized() throws Exception {
        Address address = Address.builder()
                .buildingNumber(2)
                .city("abc")
                .state("def")
                .country("ssw")
                .locality("sdw")
                .street("we")
                .zipcode("600001")
                .build();
        RestaurantRequest request = RestaurantRequest.builder()
                .name("name")
                .address(address)
                .build();
        String req = objectMapper.writeValueAsString(request);

        when(restaurantsService.create(request)).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        mvc.perform(post("/api/v1/restaurants")
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
        verify(restaurantsService, never()).create(request);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void test_restaurantWithSameNameAlreadyExistsInSameAddress_badRequest() throws Exception {
        Address address = Address.builder()
                .buildingNumber(2)
                .city("abc")
                .state("def")
                .country("ssw")
                .locality("sdw")
                .street("we")
                .zipcode("600001")
                .build();
        RestaurantRequest request = RestaurantRequest.builder()
                .name("name")
                .address(address)
                .build();
        String req = objectMapper.writeValueAsString(request);

        when(restaurantsService.create(request)).thenThrow(new RestaurantAlreadyExistsException());

        mvc.perform(post("/api/v1/restaurants")
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
        verify(restaurantsService, times(1)).create(request);
    }

    @Test
    void test_fetchAllRestaurants() throws Exception {
        when(restaurantsService.fetchAll()).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/api/v1/restaurants")).andExpect(status().isOk());
        verify(restaurantsService, times(1)).fetchAll();
    }

    @Test
    void test_fetchRestaurantById_ok() throws Exception {
        String restaurantId = "abc";

        when(restaurantsService.fetchById(restaurantId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/api/v1/restaurants/" + restaurantId)).andExpect(status().isOk());
        verify(restaurantsService, times(1)).fetchById(restaurantId);
    }

    @Test
    void test_restaurantNotFoundWhileFetchingById_badRequest() throws Exception {
        String restaurantId = "abc";

        when(restaurantsService.fetchById(restaurantId)).thenThrow(new RestaurantNotFoundException());

        mvc.perform(get("/api/v1/restaurants/" + restaurantId)).andExpect(status().isBadRequest());
        verify(restaurantsService, times(1)).fetchById(restaurantId);
    }
}