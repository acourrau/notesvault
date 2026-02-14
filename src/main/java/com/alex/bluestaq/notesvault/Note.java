package com.alex.bluestaq.notesvault;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 500)
    private String text;

    @Column(nullable = false)
    private Instant createdDt = Instant.now();

    public Note() {}

    public Note(String text) {
        this.text = text;
    }

    public long getId() { return id; }
 
    public String getText() { return text; }
    public void setText(String newText) { this.text = newText; }
 
    public Instant getCreatedDt() { return createdDt; }
}
