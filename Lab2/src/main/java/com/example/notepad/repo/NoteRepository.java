package com.example.notepad.repo;

import com.example.notepad.model.Note;
import java.util.List;
import java.util.Optional;

public interface NoteRepository {
    List<Note> findAll();
    Optional<Note> findById(Long id);
    Note save(Note note);
    void deleteById(Long id);
    Optional<Note> findByShareKey(String key);
}