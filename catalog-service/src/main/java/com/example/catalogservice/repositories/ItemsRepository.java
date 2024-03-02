package com.example.catalogservice.repositories;

import com.example.catalogservice.models.Items;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemsRepository extends JpaRepository<Items, String> {}
