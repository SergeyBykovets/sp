package com.example.notepad.web;

import com.example.notepad.model.Note;
import com.example.notepad.service.NoteService;
import com.example.notepad.model.Note.Priority;
import com.example.notepad.util.Slugifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class NoteController {

    private final NoteService noteService;

    private Slugifier slugifier;

    @Autowired
    private Environment env;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @Autowired
    public void setSlugifier(Slugifier slugifier) {
        this.slugifier = slugifier;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("notes", noteService.list());
        model.addAttribute("appProfile",
                env.getProperty("spring.profiles.active", "default")); 
        return "index";
    }

    @PostMapping("/notes")
    public String create(@RequestParam String title,
                         @RequestParam String content,
                         @RequestParam(name = "priority", defaultValue = "MEDIUM") Note.Priority priority) {
        String safeTitle = title == null || title.isBlank()
                ? "(без назви)" : title;

        slugifier.slug(safeTitle);

        noteService.create(safeTitle, content, priority);
        return "redirect:/";
    }

    @GetMapping("/notes/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Note note = noteService.get(id)
                .orElseThrow(() -> new IllegalArgumentException("Нотатку не знайдено: " + id));
        model.addAttribute("note", note);
        return "edit";
    }

    @PostMapping("/notes/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String content,
                         @RequestParam(name = "priority", defaultValue = "MEDIUM") Note.Priority priority) {
        noteService.update(id, title, content, priority);
        return "redirect:/";
    }

    @PostMapping("/notes/{id}/delete")
    public String delete(@PathVariable Long id) {
        noteService.delete(id);
        return "redirect:/";
    }
    
    @GetMapping("/public/{key}")
    public String viewShared(@PathVariable String key, Model model) {
        Note note = noteService.findByShareKey(key)
                .orElseThrow(() -> new IllegalArgumentException("Нотатку не знайдено або посилання недійсне"));
        model.addAttribute("note", note);
        return "view"; 
    }
}