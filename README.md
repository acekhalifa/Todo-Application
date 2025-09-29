# TODO Application

A simple TODO list application built with Java. This project demonstrates the use of several popular libraries: Guava, Apache Commons Collections, and Jackson.

##  Video Demonstration

[**Link to 5-Minute Explainer Video**](https://your-video-link.com)

## Features

### User Management
-   **Register**: Create a new user account with a unique email and password.
-   **Login**: Authenticate users with their email and password.
-   **Update Password**: Allows a logged-in user to change their password.

### TODO Management
-   **Add**: Create new TODO items with a title and description.
-   **Update**: Modify a TODO's title, description, or status.
-   **Delete**: Remove a TODO item.
-   **View All**: Retrieve a list of all TODOs for a user.
-   **Filter by Status**: View only `ACTIVE` or `COMPLETED` TODOs.
-   **Search**: Find TODOs by matching a query against the title, details, or creation date.

---

## Technical Stack & Library Usage

This project was built to specifically demonstrate the following libraries:

1.  **Google Guava**
    -   `Preconditions`: Used extensively in the `TodoService` class for input validation and enforcing constraints (e.g., non-null emails, non-empty titles, unique user registration). This makes the code cleaner and more robust.
    -   `Maps.newHashMap()`: Used as a convenient factory method for creating maps for data storage and JSON responses.

2.  **Apache Commons Collections**
    -   `BidiMap`: Serves as the primary index for users, mapping a unique `UUID` to a unique `email`. Its bidirectional nature is crucial for enforcing unique email constraints and allows efficient lookups by either ID or email.
    -   `MultiValuedMap`: Used to store the relationship between a user (`UUID`) and their list of `Todo` objects. This is a natural fit for a one-to-many relationship.
    -   `CollectionUtils.select()`: A powerful utility used for filtering TODO lists. It's used to implement `getActiveTodos`, `getCompletedTodos`, and the powerful `searchTodos` functionality with a simple and declarative predicate.

3.  **Jackson**
    -   `ObjectMapper`: The core of Jackson, used to serialize Java objects (`User`, `Todo`, `Map`, `List`) into clean, human-readable JSON strings for all API responses.
    -   `jackson-datatype-jsr310`: A necessary module to correctly handle serialization of Java 8 `LocalDateTime` objects into a standard format (ISO-8601).

---

## 🚀 How to Run the Application

1.  **Prerequisites**:
    -   Java Development Kit (JDK) 11 or higher.
    -   Apache Maven.

2.  **Clone the Repository**:
    ```bash
    git clone [https://github.com/your-username/your-repo-name.git](https://github.com/your-username/your-repo-name.git)
    cd your-repo-name
    ```

3.  **Build and Run**:
    The project is self-contained with a `Main.java` class that simulates API calls. You can run it directly from your IDE or use Maven.

    ```bash
    # Compile the project and install dependencies
    mvn clean install

    # Execute the main class
    mvn exec:java -Dexec.mainClass="Main"
    ```
    The console will display the JSON responses for each simulated operation, demonstrating the application's functionality.

---

## 📝 Assumptions

-   **In-Memory Storage**: Data is stored in-memory and will be lost when the application stops. No database is used.
-   **Plaintext Passwords**: Passwords are stored as plaintext strings for simplicity. In a real-world application, they should be securely hashed and salted.
-   **UUIDs for IDs**: `java.util.UUID` is used to generate unique identifiers for users and TODOs.
