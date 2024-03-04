package com.example.fulfillmentservice.repositories;

import com.example.fulfillmentservice.models.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {}
