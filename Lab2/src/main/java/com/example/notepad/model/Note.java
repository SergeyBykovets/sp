package com.example.notepad.model;

import java.time.Instant;
import java.util.UUID;

public class Note {
    private Long id;
    private String title;
    private String content;
    private Instant createdAt;
    private String shareKey;
    
    public Note() {}

    public Note(Long id, String title, String content, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.shareKey = UUID.randomUUID().toString(); 
    }
    public enum Priority { LOW, MEDIUM, HIGH }
    
    private Priority priority = Priority.MEDIUM;

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getShareKey() { return shareKey; }
    public void setShareKey(String shareKey) { this.shareKey = shareKey; }
}