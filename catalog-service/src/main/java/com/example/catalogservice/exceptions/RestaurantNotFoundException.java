package com.example.catalogservice.exceptions;

import java.util.NoSuchElementException;

public class RestaurantNotFoundException extends NoSuchElementException {
    public RestaurantNotFoundException() {
        super();
    }
}
