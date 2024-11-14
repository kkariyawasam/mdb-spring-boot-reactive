package com.example.mdbspringbootreactive.exception;

public class AccountNotFoundException extends RuntimeException {

    // Constructor with custom message
    public AccountNotFoundException(String message) {
        super(message);  // Use the provided message
    }

    // Default constructor with a default message
    public AccountNotFoundException() {
        super("Account Not Found");  // Provide a default message if none is specified
    }
}
