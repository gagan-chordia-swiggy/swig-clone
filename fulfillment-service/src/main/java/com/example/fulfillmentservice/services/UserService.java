package com.example.fulfillmentservice.services;

import com.example.fulfillmentservice.dto.ApiResponse;
import com.example.fulfillmentservice.dto.RegistrationRequest;
import com.example.fulfillmentservice.dto.UserResponse;
import com.example.fulfillmentservice.exceptions.UserAlreadyExistsException;
import com.example.fulfillmentservice.models.User;
import com.example.fulfillmentservice.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.example.fulfillmentservice.constants.Constants.USER_REGISTERED;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<ApiResponse> register(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
                .name(request.getName())
                .address(request.getAddress())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        ApiResponse response = ApiResponse.builder()
                .message(USER_REGISTERED)
                .status(HttpStatus.CREATED)
                .data(Map.of("user", new UserResponse(user)))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
