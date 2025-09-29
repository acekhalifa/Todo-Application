package com.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.model.Todo;
import com.todo.service.TodoService;

import java.util.Map;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 TODO Application Simulation 🚀\n");
        TodoService service = new TodoService();

        // --- User Management Demo ---
        System.out.println("--- 1. User Registration ---");
        String user1Reg = service.registerUser("alice@example.com", "password123");
        System.out.println(user1Reg);
        String user2Reg = service.registerUser("bob@example.com", "bob-secret");
        System.out.println(user2Reg);
        System.out.println("\n--- 2. Registering with a duplicate email (expect error) ---");
        System.out.println(service.registerUser("alice@example.com", "anotherpass"));

        // For the demo, we manually extract the UUID. In a real app, you'd parse the JSON response.
        UUID aliceId; // Replace with actual UUID from output
        try { 
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(user1Reg, Map.class);
            aliceId = UUID.fromString((String) map.get("userId"));
        } catch (Exception e) {
            System.out.println("Could not parse user ID for demo. Exiting.");
            return;
        }


        System.out.println("\n--- 3. User Login ---");
        System.out.println(service.loginUser("alice@example.com", "password123"));
        System.out.println("\n--- 4. Login with wrong password (expect error) ---");
        System.out.println(service.loginUser("alice@example.com", "wrongpass"));


        // --- TODO Management Demo for Alice ---
        System.out.println("\n--- 5. Adding TODOs for Alice ---");
        String todo1Response = service.addTodo(aliceId, "Buy Groceries", "Milk, Bread, Eggs");
        System.out.println(todo1Response);
        String todo2Response = service.addTodo(aliceId, "Finish Project Report", "Complete the final section and proofread.");
        System.out.println(todo2Response);
        service.addTodo(aliceId, "Schedule Dentist Appointment", "Call Dr. Smith's office.");

        UUID todo1Id = getTodoIdFromJson(todo1Response);

        System.out.println("\n--- 6. Get All of Alice's TODOs ---");
        System.out.println(service.getAllTodos(aliceId));

        System.out.println("\n--- 7. Update a TODO (Buy Groceries -> Buy Organic Groceries) ---");
        System.out.println(service.updateTodo(aliceId, todo1Id, "Buy Organic Groceries", null, null));

        System.out.println("\n--- 8. Mark a TODO as Completed (Project Report) ---");
        UUID todo2Id = getTodoIdFromJson(todo2Response);
        System.out.println(service.updateTodo(aliceId, todo2Id, null, null, Todo.Status.COMPLETED));

        System.out.println("\n--- 9. Get Only Active TODOs ---");
        System.out.println(service.getActiveTodos(aliceId));

        System.out.println("\n--- 10. Get Only Completed TODOs ---");
        System.out.println(service.getCompletedTodos(aliceId));

        System.out.println("\n--- 11. Search for TODOs containing 'dentist' ---");
        System.out.println(service.searchTodos(aliceId, "dentist"));

        System.out.println("\n--- 12. Delete a TODO ---");
        System.out.println(service.deleteTodo(aliceId, todo1Id));

        System.out.println("\n--- 13. Get All TODOs again to see the deletion ---");
        System.out.println(service.getAllTodos(aliceId));
    }

    // Helper to extract Todo ID from JSON response for the demo
    private static UUID getTodoIdFromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.readValue(json, Map.class);
            Map<String, Object> todoMap = (Map<String, Object>) responseMap.get("todo");
            return UUID.fromString((String) todoMap.get("id"));
        } catch (Exception e) {
            return null;
        }
    }
}
