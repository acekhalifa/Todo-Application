package com.todo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Map;

/**
 * Utility class for serializing Java objects into JSON strings and
 * generating standardized error responses.
 */
public class JsonResponse {

    private final ObjectMapper objectMapper;

    public JsonResponse() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // For LocalDateTime serialization
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String getJsonResponse(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            return "{\"status\":\"error\", \"message\":\"Failed to serialize response.\"}";
        }
    }

    public String errorResponse(String message) {
        Map<String, String> error = Map.of("status", "error", "message", message);
        return getJsonResponse(error);
    }
}
