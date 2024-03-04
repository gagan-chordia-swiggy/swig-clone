package com.example.fulfillmentservice.services;

import com.example.fulfillmentservice.dto.Address;
import com.example.fulfillmentservice.dto.ApiResponse;
import com.example.fulfillmentservice.dto.DeliveryRequest;
import com.example.fulfillmentservice.repositories.DeliveryRepository;
import com.example.fulfillmentservice.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class DeliveryServiceTest {
    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DeliveryService deliveryService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    // TODO: jsonNode as null --> to find out
//    @Test
//    void test_deliveryIsFacilitated_successfully() throws JsonProcessingException {
//        Address address = Address.builder()
//                .buildingNumber(1)
//                .street("street")
//                .locality("locality")
//                .city("city")
//                .state("state")
//                .country("country")
//                .zipcode("600001")
//                .build();
//        long orderId = 1L;
//        DeliveryRequest request = DeliveryRequest.builder()
//                .orderId(orderId)
//                .pickup(address)
//                .build();
//        String jsonResponse = "{\"lat\":\"13.08114703448276\",\"lon\":\"80.26748411034482\"}";
//        ObjectMapper objectMapper = mock(ObjectMapper.class);
//        JsonNode firstNode = mock(JsonNode.class);
//        JsonNode secondNode = mock(JsonNode.class);
//
//        when(deliveryRepository.existsByOrderId(orderId)).thenReturn(false);
//        when(userRepository.findAll()).thenReturn(anyList());
//        when(restTemplate.getForEntity(anyString(), String.class)).thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));
//        when(objectMapper.readTree(anyString())).thenReturn(firstNode);
//        when(firstNode.get("lat")).thenReturn(firstNode);
//        when(firstNode.get("lon")).thenReturn(firstNode);
//        when(firstNode.asDouble()).thenReturn(1.305);
//        ResponseEntity<ApiResponse> response = deliveryService.facilitate(request);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//    }
}