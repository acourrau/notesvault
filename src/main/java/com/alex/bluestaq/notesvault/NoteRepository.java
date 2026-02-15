package com.alex.bluestaq.notesvault;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alex.bluestaq.notesvault.dao.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {}
