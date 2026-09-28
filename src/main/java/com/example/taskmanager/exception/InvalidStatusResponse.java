package com.example.taskmanager.exception;

import java.util.List;

public class InvalidStatusResponse {

    private String message;
    private List<String> allowedValues;

    public InvalidStatusResponse(String message, List<String> allowedValues) {
        this.message = message;
        this.allowedValues = allowedValues;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }
}