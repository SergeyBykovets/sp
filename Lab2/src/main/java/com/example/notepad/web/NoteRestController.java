package com.example.notepad.web;

import com.example.notepad.dto.NoteRequest;
import com.example.notepad.dto.NoteResponse;
import com.example.notepad.model.Note;
import com.example.notepad.service.NoteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notes")
@Tag(name = "Notes API", description = "CRUD, фільтрація, пагінація, часткове оновлення")
public class NoteRestController {

    private final NoteService service;
    private final ObjectMapper mapper;

    public NoteRestController(NoteService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Створити нотатку")
    @PostMapping
    public ResponseEntity<NoteResponse> create(@RequestBody NoteRequest req) {
        Note n = service.create(
                req.title == null ? null : req.title.trim(),
                req.content,
                req.priority
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(NoteResponse.from(n));
    }

    @Operation(summary = "Отримати нотатку за id")
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> get(@PathVariable Long id) {
        return service.get(id)
                .map(NoteResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Список нотаток з фільтрами та пагінацією")
    @GetMapping
    public ResponseEntity<List<NoteResponse>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String priorityStr,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Note.Priority priority = null;
        if (priorityStr != null && !priorityStr.isBlank()) {
            try {
                priority = Note.Priority.valueOf(priorityStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        // ВИПРАВЛЕНО: використовуємо findInefficient
        var result = service.findInefficient(q, priority, page, size);
        var body = result.items().stream()
                .map(NoteResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.total()))
                .header("X-Page", String.valueOf(page))
                .header("X-Size", String.valueOf(size))
                .body(body);
    }

    @Operation(summary = "Оновити нотатку (повністю)")
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> update(@PathVariable Long id, @RequestBody NoteRequest req) {
        try {
            var saved = service.update(
                    id,
                    req.title == null ? null : req.title.trim(),
                    req.content,
                    req.priority
            );
            return ResponseEntity.ok(NoteResponse.from(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Часткове оновлення (JSON Patch, RFC 6902)")
    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<NoteResponse> patch(@PathVariable Long id, @RequestBody JsonPatch patch) {
        return service.get(id).<ResponseEntity<NoteResponse>>map(existing -> {
            try {
                // Використовуємо NoteRequest як DTO для патчу
                JsonNode dtoNode = mapper.valueToTree(new PatchDTO(
                        existing.getTitle(),
                        existing.getContent(),
                        existing.getPriority()
                ));

                JsonNode patchedNode = patch.apply(dtoNode);
                PatchDTO patchDTO = mapper.treeToValue(patchedNode, PatchDTO.class);

                var saved = service.update(
                        id,
                        patchDTO.title(),
                        patchDTO.content(),
                        patchDTO.priority()
                );
                return ResponseEntity.ok(NoteResponse.from(saved));

            } catch (JsonPatchException | JsonProcessingException e) {
                return ResponseEntity.badRequest().build();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Видалити нотатку")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.get(id).<ResponseEntity<Void>>map(note -> {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- DTO для JSON Patch (щоб уникнути id/createdAt) ---
    private record PatchDTO(String title, String content, Note.Priority priority) {}
}