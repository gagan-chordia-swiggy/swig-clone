package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    @Builder.Default
    private LocalDate timestamp = LocalDate.now();
    private String message;
    private HttpStatus status;
    private Map<?, ?> data;
}
