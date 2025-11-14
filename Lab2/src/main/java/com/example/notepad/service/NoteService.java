package com.example.notepad.service;

import com.example.notepad.model.Note;
import com.example.notepad.repo.NoteRepository;
import java.util.Objects;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    private final NoteRepository repo;
    private final Clock clock;
    private final SanitizerService sanitizer;

    public NoteService(NoteRepository repo, SanitizerService sanitizer, Clock clock) {
        this.repo = repo;
        this.sanitizer = sanitizer;
        this.clock = clock;
    }

    public List<Note> list() {
        return repo.findAll();
    }

    public Optional<Note> get(Long id) {
        return repo.findById(id);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Optional<Note> findByShareKey(String key) {
        return repo.findByShareKey(key);
    }

    // Пагінація
    public record PageResult<T>(List<T> items, long total) {}

    // --- НЕЕФЕКТИВНИЙ варіант (якщо не можна змінити репозиторій) ---
    public PageResult<Note> findInefficient(String q, Note.Priority prio, int page, int size) {
        List<Note> all = repo.findAll();

        String query = (q != null && !q.trim().isEmpty()) ? q.trim().toLowerCase() : null;

        List<Note> filtered = all.stream()
                .filter(n -> query == null || 
                            (n.getTitle() != null && n.getTitle().toLowerCase().contains(query)))
                .filter(n -> prio == null || Objects.equals(prio, n.getPriority())) // ВИПРАВЛЕНО
                .toList();

        int from = Math.max(0, page * size);
        int to = Math.min(filtered.size(), from + size);
        List<Note> slice = (from >= filtered.size()) ? List.of() : filtered.subList(from, to);

        return new PageResult<>(slice, filtered.size());
    }
    private boolean queryIsEmptyOrMatches(String q, Note n) {
        if (q == null || q.isBlank()) return true;
        if (n.getTitle() == null) return false;
        return n.getTitle().toLowerCase().contains(q.toLowerCase());
    }
    // ---------------------------------------------------------------

    public Note create(String title, String content, Note.Priority priority) {
        title = title == null ? "" : title.trim();
        content = sanitizer.clean(content);
        Note n = new Note(null, title, content, clock.instant()); // ВИПРАВЛЕНО
        n.setPriority(priority);
        return repo.save(n);
    }

    public Note update(Long id, String title, String content, Note.Priority priority) {
        Note n = repo.findById(id).orElseThrow();
        n.setTitle(title == null ? "" : title.trim());
        n.setContent(sanitizer.clean(content));
        n.setPriority(priority);
        return repo.save(n);
    }
}