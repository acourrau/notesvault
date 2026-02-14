package com.alex.bluestaq.notesvault;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/notes")
public class NoteController {
    
    private final NoteRepository repo;

    record CreateNoteRequest(String text) {}

    public NoteController(NoteRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Note> getAllNotes() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Note getNoteById(@PathVariable Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    }
    
    @PostMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Note createNote(@RequestBody CreateNoteRequest req) {
        return repo.save(new Note(req.text));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
        repo.deleteById(id);
    }
}
