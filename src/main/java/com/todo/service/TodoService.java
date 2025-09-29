package com.todo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.todo.model.Todo;
import com.todo.model.User;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class TodoService {

    private final BidiMap<UUID, String> userEmailBiMap = new DualHashBidiMap<>();

    // MultiValuedMap maps a user's UUID to their collection of Todos.
    private final MultiValuedMap<UUID, Todo> userTodosMap = new ArrayListValuedHashMap<>();

    // Simple map for storing the full User object by UUID.
    private final Map<UUID, User> userStore = Maps.newHashMap(); // Guava's factory method

    // --- JSON Utility using Jackson ---
    private final ObjectMapper objectMapper;

    public TodoService() {
        // Configure Jackson ObjectMapper
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // For LocalDateTime serialization
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // For pretty JSON
    }

    // ========== USER MANAGEMENT ==========

    public String registerUser(String email, String password) {
        try {
            // --- Guava Preconditions for Input Validation ---
            Preconditions.checkNotNull(email, "Email cannot be null.");
            Preconditions.checkArgument(!email.trim().isEmpty(), "Email cannot be empty.");
            Preconditions.checkNotNull(password, "Password cannot be null.");
            Preconditions.checkArgument(password.length() >= 6, "Password must be at least 6 characters long.");
            Preconditions.checkState(!userEmailBiMap.containsValue(email), "Email '%s' is already registered.", email);

            User newUser = new User(email, password);
            userStore.put(newUser.getId(), newUser);
            userEmailBiMap.put(newUser.getId(), newUser.getEmail());

            Map<String, Object> response = Maps.newHashMap();
            response.put("status", "success");
            response.put("message", "User registered successfully.");
            response.put("userId", newUser.getId());
            return jsonResponse(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    public String loginUser(String email, String password) {
        try {
            Preconditions.checkNotNull(email, "Email cannot be null.");
            Preconditions.checkNotNull(password, "Password cannot be null.");

            // Use the BidiMap to find the user ID by email
            UUID userId = userEmailBiMap.getKey(email);
            Preconditions.checkArgument(userId != null, "User with email '%s' not found.", email);

            User user = userStore.get(userId);
            Preconditions.checkArgument(user.getPassword().equals(password), "Invalid password.");

            Map<String, Object> response = Maps.newHashMap();
            response.put("status", "success");
            response.put("message", "Login successful.");
            response.put("userId", user.getId()); // This ID acts as a session token
            return jsonResponse(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    public String updatePassword(UUID userId, String oldPassword, String newPassword) {
        try {
            Preconditions.checkNotNull(userId, "User ID cannot be null.");
            User user = findUserOrThrow(userId);

            Preconditions.checkArgument(user.getPassword().equals(oldPassword), "Old password does not match.");
            Preconditions.checkNotNull(newPassword, "New password cannot be null.");
            Preconditions.checkArgument(newPassword.length() >= 6, "New password must be at least 6 characters long.");

            user.setPassword(newPassword);
            userStore.put(userId, user); // Update the user in the store

            return jsonResponse(Map.of("status", "success", "message", "Password updated successfully."));
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    // ========== TODO MANAGEMENT ==========

    public String addTodo(UUID userId, String title, String details) {
        try {
            findUserOrThrow(userId); // Validates user existence
            Preconditions.checkNotNull(title, "Todo title cannot be null.");
            Preconditions.checkArgument(!title.trim().isEmpty(), "Todo title cannot be empty.");

            Todo newTodo = new Todo(title, details);
            userTodosMap.put(userId, newTodo);

            Map<String, Object> response = Maps.newHashMap();
            response.put("status", "success");
            response.put("message", "Todo added successfully.");
            response.put("todo", newTodo);
            return jsonResponse(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    public String updateTodo(UUID userId, UUID todoId, String newTitle, String newDetails, Todo.Status newStatus) {
        try {
            findUserOrThrow(userId);
            Todo todoToUpdate = findTodoOrThrow(userId, todoId);

            if (newTitle != null) todoToUpdate.setTitle(newTitle);
            if (newDetails != null) todoToUpdate.setDetails(newDetails);
            if (newStatus != null) todoToUpdate.setStatus(newStatus);

            return jsonResponse(Map.of("status", "success", "updatedTodo", todoToUpdate));
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    public String deleteTodo(UUID userId, UUID todoId) {
        try {
            findUserOrThrow(userId);
            Collection<Todo> userTodos = userTodosMap.get(userId);

            boolean removed = userTodos.removeIf(todo -> todo.getId().equals(todoId));
            Preconditions.checkState(removed, "Todo with ID '%s' not found for this user.", todoId);

            return jsonResponse(Map.of("status", "success", "message", "Todo deleted successfully."));
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    public String getAllTodos(UUID userId) {
        try {
            findUserOrThrow(userId);
            return jsonResponse(userTodosMap.get(userId));
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    public String getActiveTodos(UUID userId) {
        try {
            findUserOrThrow(userId);
            Collection<Todo> allTodos = userTodosMap.get(userId);

            // --- Apache Commons CollectionUtils for Filtering ---
            Collection<Todo> activeTodos = CollectionUtils.select(allTodos, todo -> todo.getStatus() == Todo.Status.ACTIVE);

            return jsonResponse(activeTodos);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    public String getCompletedTodos(UUID userId) {
        try {
            findUserOrThrow(userId);
            Collection<Todo> allTodos = userTodosMap.get(userId);

            // --- Apache Commons CollectionUtils for Filtering ---
            Collection<Todo> completedTodos = CollectionUtils.select(allTodos, todo -> todo.getStatus() == Todo.Status.COMPLETED);

            return jsonResponse(completedTodos);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    public String searchTodos(UUID userId, String query) {
        try {
            findUserOrThrow(userId);
            Preconditions.checkNotNull(query, "Search query cannot be null.");
            String lowerCaseQuery = query.toLowerCase();

            Collection<Todo> allTodos = userTodosMap.get(userId);

            // --- Apache Commons CollectionUtils for Complex Filtering (Searching) ---
            Collection<Todo> matchingTodos = CollectionUtils.select(allTodos, todo ->
                    todo.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                            (todo.getDetails() != null && todo.getDetails().toLowerCase().contains(lowerCaseQuery)) ||
                            todo.getCreatedAt().toString().contains(lowerCaseQuery)
            );

            return jsonResponse(matchingTodos);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    // ========== HELPER METHODS ==========

    private User findUserOrThrow(UUID userId) {
        User user = userStore.get(userId);
        Preconditions.checkState(user != null, "User with ID '%s' not found.", userId);
        return user;
    }

    private Todo findTodoOrThrow(UUID userId, UUID todoId) {
        return userTodosMap.get(userId).stream()
                .filter(t -> t.getId().equals(todoId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Todo with ID '%s' not found.", todoId)));
    }

    private String jsonResponse(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            return "{\"status\":\"error\", \"message\":\"Failed to serialize response.\"}";
        }
    }

    private String errorResponse(String message) {
        Map<String, String> error = Map.of("status", "error", "message", message);
        return jsonResponse(error);
    }
}
