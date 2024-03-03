package com.example.fulfillmentservice.exceptions;

import jakarta.persistence.EntityExistsException;

public class UserAlreadyExistsException extends EntityExistsException {
    public UserAlreadyExistsException() {
        super();
    }
}
