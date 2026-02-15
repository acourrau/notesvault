package com.alex.bluestaq.notesvault;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.alex.bluestaq.notesvault.dao.Note;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {
    
    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }
    
    public record CreateNoteRequest(String text) {}

    @GetMapping
    public List<Note> getAllNotes() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Note getNoteById(@PathVariable Long id) {
        return service.findById(id);
    }
    
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Note createNote(@RequestBody CreateNoteRequest req) {
        return service.create(req.text());
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(@PathVariable Long id) {
        service.deleteById(id);
    }
}
