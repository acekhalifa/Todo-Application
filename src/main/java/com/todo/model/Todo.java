package com.todo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        this.status = Status.ACTIVE; // Default status
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

}
