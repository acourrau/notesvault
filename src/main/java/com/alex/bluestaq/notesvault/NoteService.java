package com.alex.bluestaq.notesvault;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.alex.bluestaq.notesvault.dao.Note;

import java.util.List;

@Service
public class NoteService {
    private final NoteRepository repo;

    public NoteService(NoteRepository repo) { this.repo = repo; }

    public Note create(String text) {
        if (text.length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Text is too long. Maximum 500 characters.");
        }
        if (text == null || text.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Text cannot be empty.");
        }

        return repo.save(new Note(text));
    }
    
    public List<Note> findAll() {
        return repo.findAll();
    }

    public Note findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found."));
    }

    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found, could not delete.");
        }
        repo.deleteById(id);
    }
}
