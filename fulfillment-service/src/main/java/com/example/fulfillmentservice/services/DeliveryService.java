package com.example.fulfillmentservice.services;

import com.example.fulfillmentservice.dto.Address;
import com.example.fulfillmentservice.dto.ApiResponse;
import com.example.fulfillmentservice.dto.DeliveryRequest;
import com.example.fulfillmentservice.enums.Availability;
import com.example.fulfillmentservice.exceptions.NoExecutiveNearbyException;
import com.example.fulfillmentservice.exceptions.OrderHasAlreadyBeenFacilitatedException;
import com.example.fulfillmentservice.models.Delivery;
import com.example.fulfillmentservice.models.User;
import com.example.fulfillmentservice.repositories.DeliveryRepository;
import com.example.fulfillmentservice.repositories.UserRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static com.example.fulfillmentservice.constants.Constants.DELIVERY_FACILITATED;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public ResponseEntity<ApiResponse> facilitate(DeliveryRequest request) throws JsonProcessingException {
        if (deliveryRepository.existsByOrderId(request.getOrderId())) {
            throw new OrderHasAlreadyBeenFacilitatedException();
        }

        User nearestExecutive = this.getNearestAvailableExecutive(request.getPickupAddress());

        if (nearestExecutive == null) {
            throw new NoExecutiveNearbyException();
        }

        nearestExecutive.setAvailability(Availability.UNAVAILABLE);
        Delivery delivery = Delivery.builder()
                .user(nearestExecutive)
                .restaurantAddress(request.getPickupAddress())
                .customerAddress(request.getDeliveryAddress())
                .orderId(request.getOrderId())
                .build();

        deliveryRepository.save(delivery);
        userRepository.save(nearestExecutive);

        ApiResponse response = ApiResponse.builder()
                .message(DELIVERY_FACILITATED)
                .status(HttpStatus.CREATED)
                .data(Map.of("delivery", delivery))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    private User getNearestAvailableExecutive(Address address) throws JsonProcessingException {
        User user = null;
        List<User> executives = userRepository.findAll();
        ObjectMapper objectMapper = new ObjectMapper();

        String restaurantAddressResponse = this.getDetailedAddress(address);
        JsonNode restaurantAddressJson = objectMapper.readTree(restaurantAddressResponse);
        double restaurantLatitude = restaurantAddressJson.get("lat").asDouble();
        double restaurantLongitude = restaurantAddressJson.get("lon").asDouble();

        double minDistance = Double.MAX_VALUE;
        for (User executive : executives) {
            if (!executive.getAddress().getCity().equals(address.getCity()) || executive.getAvailability().equals(Availability.UNAVAILABLE)) {
                continue;
            }

            String executiveAddressResponse = this.getDetailedAddress(executive.getAddress());
            JsonNode executiveAddressJson = objectMapper.readTree(executiveAddressResponse);
            double executiveLatitude = executiveAddressJson.get("lat").asDouble();
            double executiveLongitude = executiveAddressJson.get("lon").asDouble();

            double distance = this.calculateDistance(
                    restaurantLatitude,
                    restaurantLongitude,
                    executiveLatitude,
                    executiveLongitude
            );

            if (distance < minDistance) {
                minDistance = distance;
                user = executive;
            }
        }

        return user;
    }

    private String getDetailedAddress(Address address) {
        String jsonResponse =  String.valueOf(restTemplate.getForEntity(
                "https://nominatim.openstreetmap.org/search?country=India"
                        + "&postalcode=" + address.getZipcode() + "&format=json",
                String.class
        ));
        int startIndex = jsonResponse.indexOf("[");
        int endIndex = jsonResponse.lastIndexOf("}]");

        return jsonResponse.substring(startIndex + 1, endIndex + 1);
    }

    private double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double phi1 = Math.toRadians(latitude1);
        double phi2 = Math.toRadians(latitude2);
        double deltaPhi = Math.toRadians(Math.abs(latitude2 - latitude1));
        double deltaLambda = Math.toRadians(Math.abs(longitude2 - longitude1));
        final double EARTH_RADIUS = 6371.0;

        // HAVERSINE FORMULA
        double a = Math.pow(Math.sin(deltaPhi / 2.0), 2)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.pow(Math.sin(deltaLambda / 2.0), 2);
        double c = Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}
