package com.example.notepad.dto;

import com.example.notepad.model.Note;

import java.time.Instant;

public class NoteResponse {
    public Long id;
    public String title;
    public String content;
    public Note.Priority priority;
    public Instant createdAt;

    public static NoteResponse from(Note n) {
        var r = new NoteResponse();
        r.id = n.getId();
        r.title = n.getTitle();
        r.content = n.getContent();
        r.priority = n.getPriority();
        r.createdAt = n.getCreatedAt();
        return r;
    }
}