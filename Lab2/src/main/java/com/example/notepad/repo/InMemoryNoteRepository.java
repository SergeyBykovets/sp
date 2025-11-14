package com.example.notepad.repo;

import com.example.notepad.model.Note;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository  
public class InMemoryNoteRepository implements NoteRepository {

    private final Map<Long, Note> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public List<Note> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Note::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public Optional<Note> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Note save(Note note) {
        if (note.getId() == null) note.setId(seq.incrementAndGet());
        storage.put(note.getId(), note);
        return note;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
    
    @Override
    public Optional<Note> findByShareKey(String key) {
        return storage.values().stream()
                .filter(n -> n.getShareKey().equals(key))
                .findFirst();
    }
}