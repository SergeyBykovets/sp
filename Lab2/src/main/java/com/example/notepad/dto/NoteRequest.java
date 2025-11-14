// NoteRequest.java
package com.example.notepad.dto;

import com.example.notepad.model.Note;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteRequest {
    public String title;
    public String content;
    public Note.Priority priority = Note.Priority.MEDIUM;

    public NoteRequest() {}

    // Додати геттери
    public String title() { return title; }
    public String content() { return content; }
    public Note.Priority priority() { return priority; }
}