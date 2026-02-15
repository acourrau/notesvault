package com.alex.bluestaq.notesvault;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NotesApiTest {

    private static final String testDb = "target/test.db";

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void sqliteProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:sqlite:"+testDb);
        registry.add("spring.datasource.driver-class-name", () -> "org.sqlite.JDBC");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeAll
    static void deleteTestDb() throws Exception {
        Files.deleteIfExists(Path.of(testDb));
    }

    private long createNote(String text) throws Exception {
        String json = objectMapper.writeValueAsString(Map.of("text", text));

        String response = mvc.perform(post("/notes")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.createdDt", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void create_note_success() throws Exception {
        long id = createNote("test case 1");

        mvc.perform(get("/notes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) id))
                .andExpect(jsonPath("$.text").value("test case 1"));
    }

    @Test
    void get_notes_list_all() throws Exception {
        long id = createNote("Test note 1");
        long id2 = createNote("Test note 2");

        mvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$[*].id", hasItem((int) id)))
                .andExpect(jsonPath("$[*].id", hasItem((int) id2)));
    }

    @Test
    void get_note_by_id_found() throws Exception {
        long id = createNote("I expect to be found");

        mvc.perform(get("/notes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) id))
                .andExpect(jsonPath("$.text").value("I expect to be found"))
                .andExpect(jsonPath("$.createdDt", notNullValue()));
    }

    @Test
    void get_note_by_id_not_found() throws Exception {
        long id = 999999;

        mvc.perform(get("/notes/{id}", id).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(containsString("Note not found.")));
    }

    @Test
    void delete_note_existing() throws Exception {
        long id = createNote("I will be deleted");

        mvc.perform(delete("/notes/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_note_not_found() throws Exception {
        long id = 999999;

        mvc.perform(delete("/notes/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_note_then_confirm() throws Exception {
        long id = createNote("bye");

        mvc.perform(delete("/notes/{id}", id))
                .andExpect(status().isNoContent());

        mvc.perform(get("/notes/{id}", id))
                .andExpect(status().isNotFound());
    }
}
