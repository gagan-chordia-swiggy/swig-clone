package com.example.fulfillmentservice.services;

import com.example.fulfillmentservice.dto.Address;
import com.example.fulfillmentservice.dto.ApiResponse;
import com.example.fulfillmentservice.dto.RegistrationRequest;
import com.example.fulfillmentservice.exceptions.UserAlreadyExistsException;
import com.example.fulfillmentservice.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

import static com.example.fulfillmentservice.constants.Constants.USER_REGISTERED;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void test_userRegistered_successfully() {
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

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(anyString());
        ResponseEntity<ApiResponse> response = userService.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(USER_REGISTERED, Objects.requireNonNull(response.getBody()).getMessage());
        verify(userRepository, times(1)).existsByUsername(request.getUsername());
        verify(passwordEncoder, times(1)).encode(request.getPassword());
    }

    @Test
    void test_cannotSaveUserWithRegisteredUsername() {
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

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(request));
        verify(userRepository, times(1)).existsByUsername(request.getUsername());
        verify(passwordEncoder, never()).encode(request.getPassword());
    }
}