package com.example.fulfillmentservice.dto;

import com.example.fulfillmentservice.enums.DeliveryStatus;

import com.example.fulfillmentservice.models.Delivery;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeliveryResponse {
    private String id;
    private long orderId;
    private String executiveId;
    private DeliveryStatus status;

    public DeliveryResponse(Delivery delivery) {
        this.id = delivery.getId();
        this.orderId = delivery.getOrderId();
        this.executiveId = delivery.getUser().getId();
        this.status = delivery.getStatus();
    }
}
