package com.example.fulfillmentservice.exceptions;

import jakarta.persistence.EntityExistsException;

public class OrderHasAlreadyBeenFacilitatedException extends EntityExistsException {
    public OrderHasAlreadyBeenFacilitatedException() {
        super();
    }
}
