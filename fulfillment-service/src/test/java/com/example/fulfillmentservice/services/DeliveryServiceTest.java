package com.example.fulfillmentservice.services;

import com.example.fulfillmentservice.dto.Address;
import com.example.fulfillmentservice.dto.ApiResponse;
import com.example.fulfillmentservice.dto.DeliveryRequest;
import com.example.fulfillmentservice.enums.Availability;
import com.example.fulfillmentservice.enums.DeliveryStatus;
import com.example.fulfillmentservice.exceptions.*;
import com.example.fulfillmentservice.models.Delivery;
import com.example.fulfillmentservice.models.User;
import com.example.fulfillmentservice.repositories.DeliveryRepository;
import com.example.fulfillmentservice.repositories.UserRepository;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.fulfillmentservice.constants.Constants.DELIVERY_FACILITATED;
import static com.example.fulfillmentservice.constants.Constants.STATUS_UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.*;
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

    @Test
    void test_deliveryIsFacilitated_successfully() throws JsonProcessingException {
        Address address = spy(Address.builder()
                .buildingNumber(1)
                .street("street")
                .locality("locality")
                .city("city")
                .state("state")
                .country("country")
                .zipcode("600001")
                .build());
        long orderId = 1L;
        User executive = mock(User.class);
        DeliveryRequest request = DeliveryRequest.builder()
                .orderId(orderId)
                .pickupAddress(address)
                .build();
        String jsonResponse = "<200 OK OK,[{\"place_id\":332908868,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright\",\"lat\":\"13.08114703448276\",\"lon\":\"80.26748411034482\",\"category\":\"place\",\"type\":\"postcode\",\"place_rank\":21,\"importance\":0.12000999999999995,\"addresstype\":\"postcode\",\"name\":\"600001\",\"display_name\":\"600001, Zone 5 Royapuram, Chennai, Chennai District, Tamil Nadu, India\",\"boundingbox\":[\"12.9211470\",\"13.2411470\",\"80.1074841\",\"80.4274841\"]}],[Server:\"nginx\", Date:\"Mon, 04 Mar 2024 16:29:08 GMT\", Content-Type:\"application/json; charset=utf-8\", Content-Length:\"442\", Connection:\"keep-alive\", Keep-Alive:\"timeout=20\"]>";
        String apiString = "https://nominatim.openstreetmap.org/search?country=India&postalcode=" + address.getZipcode() + "&format=json";

        when(deliveryRepository.existsByOrderId(orderId)).thenReturn(false);
        when(userRepository.findAll()).thenReturn(List.of(executive));
        when(restTemplate.getForEntity(apiString, String.class))
                .thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));
        when(executive.getAddress()).thenReturn(address);
        when(executive.getAvailability()).thenReturn(Availability.AVAILABLE);
        when(address.getCity()).thenReturn("city");
        when(address.getZipcode()).thenReturn("600001");
        ResponseEntity<ApiResponse> response = deliveryService.facilitate(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(DELIVERY_FACILITATED, Objects.requireNonNull(response.getBody()).getMessage());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void test_deliveryAlreadyFacilitatedForGivenOrderId_throwsException() {
        Address address = Address.builder()
                .buildingNumber(1)
                .street("street")
                .locality("locality")
                .city("city")
                .state("state")
                .country("country")
                .zipcode("600001")
                .build();
        long orderId = 1L;
        DeliveryRequest request = DeliveryRequest.builder()
                .orderId(orderId)
                .pickupAddress(address)
                .build();

        when(deliveryRepository.existsByOrderId(orderId)).thenReturn(true);

        assertThrows(OrderHasAlreadyBeenFacilitatedException.class, () -> deliveryService.facilitate(request));
        verify(userRepository, never()).findAll();
    }

    @Test
    void test_deliveryExecutiveNotFoundInGivenCity_throwsException() {
        Address restaurantAddress = spy(Address.builder()
                .buildingNumber(1)
                .street("street")
                .locality("locality")
                .city("city")
                .state("state")
                .country("country")
                .zipcode("600001")
                .build());
        Address executiveAddress = spy(Address.builder()
                .buildingNumber(1)
                .street("street")
                .locality("locality")
                .city("city1")
                .state("state")
                .country("country")
                .zipcode("600001")
                .build());
        User executive = mock(User.class);
        long orderId = 1L;
        DeliveryRequest request = DeliveryRequest.builder()
                .orderId(orderId)
                .pickupAddress(restaurantAddress)
                .build();
        String jsonResponse = "<200 OK OK,[{\"place_id\":332908868,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright\",\"lat\":\"13.08114703448276\",\"lon\":\"80.26748411034482\",\"category\":\"place\",\"type\":\"postcode\",\"place_rank\":21,\"importance\":0.12000999999999995,\"addresstype\":\"postcode\",\"name\":\"600001\",\"display_name\":\"600001, Zone 5 Royapuram, Chennai, Chennai District, Tamil Nadu, India\",\"boundingbox\":[\"12.9211470\",\"13.2411470\",\"80.1074841\",\"80.4274841\"]}],[Server:\"nginx\", Date:\"Mon, 04 Mar 2024 16:29:08 GMT\", Content-Type:\"application/json; charset=utf-8\", Content-Length:\"442\", Connection:\"keep-alive\", Keep-Alive:\"timeout=20\"]>";
        String apiString = "https://nominatim.openstreetmap.org/search?country=India&postalcode=" + restaurantAddress.getZipcode() + "&format=json";

        when(deliveryRepository.existsByOrderId(orderId)).thenReturn(false);
        when(userRepository.findAll()).thenReturn(List.of(executive));when(restTemplate.getForEntity(apiString, String.class)).thenReturn(new ResponseEntity<>(jsonResponse,HttpStatus.OK));
        when(restTemplate.getForEntity(apiString, String.class)).thenReturn(new ResponseEntity<>(jsonResponse,HttpStatus.OK));
        when(executive.getAddress()).thenReturn(executiveAddress);
        when(executiveAddress.getCity()).thenReturn("city1");
        when(restaurantAddress.getCity()).thenReturn("city");
        when(restaurantAddress.getZipcode()).thenReturn("600001");

        assertThrows(NoExecutiveNearbyException.class, () -> deliveryService.facilitate(request));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void test_whenNoExecutiveIsAvailableToFacilitateDelivery_throwException() {
        Address address = spy(Address.builder()
                .buildingNumber(1)
                .street("street")
                .locality("locality")
                .city("city")
                .state("state")
                .country("country")
                .zipcode("600001")
                .build());
        long orderId = 1L;
        User executive = mock(User.class);
        DeliveryRequest request = DeliveryRequest.builder()
                .orderId(orderId)
                .pickupAddress(address)
                .build();
        String jsonResponse = "<200 OK OK,[{\"place_id\":332908868,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright\",\"lat\":\"13.08114703448276\",\"lon\":\"80.26748411034482\",\"category\":\"place\",\"type\":\"postcode\",\"place_rank\":21,\"importance\":0.12000999999999995,\"addresstype\":\"postcode\",\"name\":\"600001\",\"display_name\":\"600001, Zone 5 Royapuram, Chennai, Chennai District, Tamil Nadu, India\",\"boundingbox\":[\"12.9211470\",\"13.2411470\",\"80.1074841\",\"80.4274841\"]}],[Server:\"nginx\", Date:\"Mon, 04 Mar 2024 16:29:08 GMT\", Content-Type:\"application/json; charset=utf-8\", Content-Length:\"442\", Connection:\"keep-alive\", Keep-Alive:\"timeout=20\"]>";
        String apiString = "https://nominatim.openstreetmap.org/search?country=India&postalcode=" + address.getZipcode() + "&format=json";

        when(deliveryRepository.existsByOrderId(orderId)).thenReturn(false);
        when(userRepository.findAll()).thenReturn(List.of(executive));
        when(restTemplate.getForEntity(apiString, String.class))
                .thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));
        when(executive.getAddress()).thenReturn(address);
        when(executive.getAvailability()).thenReturn(Availability.UNAVAILABLE);
        when(address.getCity()).thenReturn("city");
        when(address.getZipcode()).thenReturn("600001");

        assertThrows(NoExecutiveNearbyException.class, () -> deliveryService.facilitate(request));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void test_whileUpdatingDeliveryStatusDeliveryNotFoundById_throwsException() {
        String deliveryId = "id";

        when(deliveryRepository.findById(deliveryId)).thenThrow(new DeliveryNotFoundException());

        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.updateStatus(deliveryId));
        verify(deliveryRepository, times(1)).findById(deliveryId);
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void test_userNotFoundWhileUpdatingStatus_throwsException() {
        String deliveryId = "id";
        Delivery delivery = mock(Delivery.class);
        String username = "user";
        SecurityContext context = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(context);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> deliveryService.updateStatus(deliveryId));
        verify(deliveryRepository, times(1)).findById(deliveryId);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void test_authenticatedUserIsNotSameAsExecutiveForDelivery_throwsException() {
        String deliveryId = "id";
        Delivery delivery = mock(Delivery.class);
        User user = mock(User.class);
        User anotherUser = mock(User.class);
        String username = "user";
        SecurityContext context = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(context);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(delivery.getUser()).thenReturn(anotherUser);

        assertThrows(UnauthorizedStatusUpdateException.class, () -> deliveryService.updateStatus(deliveryId));
        verify(deliveryRepository, times(1)).findById(deliveryId);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void test_completedDeliveryStatusCannotBeUpdated_throwsException() {
        String deliveryId = "id";
        Delivery delivery = mock(Delivery.class);
        User user = mock(User.class);
        String username = "user";
        SecurityContext context = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(context);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(delivery.getUser()).thenReturn(user);
        when(delivery.getStatus()).thenReturn(DeliveryStatus.DELIVERED);

        assertThrows(OrderHasBeenAlreadyDeliveredException.class, () -> deliveryService.updateStatus(deliveryId));
        verify(deliveryRepository, times(1)).findById(deliveryId);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void test_packedDeliveryStatusIsUpdatedToPickedUp() {
        String deliveryId = "id";
        Delivery delivery = spy(Delivery.builder()
                .status(DeliveryStatus.PACKED)
                .build());
        User user = mock(User.class);
        String username = "user";
        SecurityContext context = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(context);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(delivery.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(anyString());
        ResponseEntity<ApiResponse> response = deliveryService.updateStatus(deliveryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(STATUS_UPDATED + DeliveryStatus.PICKED_UP, Objects.requireNonNull(response.getBody()).getMessage());
        verify(deliveryRepository, times(1)).findById(deliveryId);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void test_pickedUpDeliveryStatusIsUpdatedToDelivered() {
        String deliveryId = "id";
        Delivery delivery = spy(Delivery.builder()
                .status(DeliveryStatus.PICKED_UP)
                .build());
        User user = mock(User.class);
        String username = "user";
        SecurityContext context = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(context);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(delivery.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(anyString());
        ResponseEntity<ApiResponse> response = deliveryService.updateStatus(deliveryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(STATUS_UPDATED + DeliveryStatus.DELIVERED, Objects.requireNonNull(response.getBody()).getMessage());
        verify(deliveryRepository, times(1)).findById(deliveryId);
        verify(userRepository, times(1)).findByUsername(username);
    }
}