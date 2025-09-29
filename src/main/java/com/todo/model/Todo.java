package com.todo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class Todo {
    public enum Status { ACTIVE, COMPLETED }

    private final UUID id;
    private String title;
    private String details;
    private Status status;
    private final LocalDateTime createdAt;

    public Todo(String title, String details) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.details = details;
        this.status = Status.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Todo{" + "id=" + id + ", title='" + title + '\'' +
                ", status=" + status + ", createdAt=" +
                createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return Objects.equals(getId(), todo.getId()) && Objects.equals(getTitle(), todo.getTitle()) && Objects.equals(getDetails(), todo.getDetails()) && getStatus() == todo.getStatus() && Objects.equals(getCreatedAt(), todo.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getDetails(), getStatus(), getCreatedAt());
    }
}
