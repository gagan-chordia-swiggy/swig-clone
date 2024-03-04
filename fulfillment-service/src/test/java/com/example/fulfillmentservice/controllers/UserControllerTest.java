package com.example.fulfillmentservice.controllers;

import com.example.fulfillmentservice.dto.Address;
import com.example.fulfillmentservice.dto.RegistrationRequest;
import com.example.fulfillmentservice.exceptions.UserAlreadyExistsException;
import com.example.fulfillmentservice.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        reset(userService);
    }

    @Test
    void test_userRegistered_created() throws Exception {
        Address address = Address.builder()
                .buildingNumber(1)
                .street("street")
                .locality("locality")
                .city("city")
                .state("state")
                .country("country")
                .zipcode("600001")
                .build();
        RegistrationRequest request = RegistrationRequest.builder()
                .name("name")
                .username("username")
                .password("password")
                .address(address)
                .build();
        String req = objectMapper.writeValueAsString(request);

        when(userService.register(request)).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        mvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
        ).andExpect(status().isCreated());
        verify(userService, times(1)).register(request);
    }

    @Test
    void test_cannotSaveUserWithRegisteredUsername_badRequest() throws Exception {
        Address address = Address.builder()
                .buildingNumber(1)
                .street("street")
                .locality("locality")
                .city("city")
                .state("state")
                .country("country")
                .zipcode("600001")
                .build();
        RegistrationRequest request = RegistrationRequest.builder()
                .name("name")
                .username("username")
                .password("password")
                .address(address)
                .build();
        String req = objectMapper.writeValueAsString(request);

        when(userService.register(request)).thenThrow(new UserAlreadyExistsException());

        mvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
        ).andExpect(status().isBadRequest());
        verify(userService, times(1)).register(request);
    }
}