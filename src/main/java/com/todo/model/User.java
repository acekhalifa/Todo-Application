package com.todo.model;

import java.util.Objects;
import java.util.UUID;

public class User {
    private final UUID id;
    private final String email;
    private String password;

    public User(String email, String password) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.password = password;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", email='" + email + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password);
    }
}
