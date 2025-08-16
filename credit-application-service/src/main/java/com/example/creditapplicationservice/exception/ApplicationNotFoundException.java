package com.example.creditapplicationservice.exception;

import java.util.UUID;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(UUID id) {
        super("Credit application not found with ID: " + id);
    }
}
