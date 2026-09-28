package com.example.taskmanager.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

class GlobalExceptionHandlerTest {

    @Test
    void testTaskNotFoundException() {

        GlobalExceptionHandler handler =
                new GlobalExceptionHandler();

        TaskNotFoundException exception =
                new TaskNotFoundException(
                        "Task not found with id: 999"
                );

        ErrorResponse response =
                handler.handleTaskNotFound(exception);

        assertEquals(
                "Task not found with id: 999",
                response.getMessage()
        );
    }
    @Test
    void testValidationError() {

        GlobalExceptionHandler handler =
                new GlobalExceptionHandler();

        Map<String, String> errors = new HashMap<>();

        errors.put("title", "Title is required");

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        "Validation failed",
                        errors
                );

        assertEquals(
                "Validation failed",
                response.getMessage()
        );

        assertEquals(
                "Title is required",
                response.getErrors().get("title")
        );

    }
    @Test
    void testInvalidStatus() {

        GlobalExceptionHandler handler =
                new GlobalExceptionHandler();

        InvalidStatusResponse response =
                handler.handleInvalidStatus(null);

        assertEquals(
                "Invalid task status",
                response.getMessage()
        );

        assertEquals(
                List.of("PENDING", "IN_PROGRESS", "COMPLETED"),
                response.getAllowedValues()
        );
    }
}


