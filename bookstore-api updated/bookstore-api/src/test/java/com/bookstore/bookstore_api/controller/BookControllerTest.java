package com.bookstore.bookstore_api.controller;

import com.bookstore.bookstore_api.dto.BookRequest;
import com.bookstore.bookstore_api.model.Author;
import com.bookstore.bookstore_api.model.Genre;
import com.bookstore.bookstore_api.repository.AuthorRepository;
import com.bookstore.bookstore_api.repository.GenreRepository;
import com.bookstore.bookstore_api.repository.UserRepository;
import com.bookstore.bookstore_api.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepo;
    @Autowired private AuthorRepository authorRepo;
    @Autowired private GenreRepository genreRepo;
    @Autowired private PasswordEncoder encoder;

    private String token;
    private Long authorId;
    private Long genreId;

    @BeforeEach
    void setUp() throws Exception {
        // Seed test user
        userRepo.deleteAll();
        User u = new User();
        u.setUsername("testadmin");
        u.setPassword(encoder.encode("testpass"));
        userRepo.save(u);

        // Seed an author and genre for book tests
        Author a = new Author();
        a.setName("Test Author");
        authorId = authorRepo.save(a).getId();

        Genre g = new Genre();
        g.setName("Test Genre");
        genreId = genreRepo.save(g).getId();

        // Login to obtain JWT
        String body = objectMapper.writeValueAsString(
                Map.of("username", "testadmin", "password", "testpass"));

        MvcResult res = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        token = objectMapper.readTree(res.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Test
    void getAllBooks_withValidToken_returns200() throws Exception {
        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllBooks_withoutToken_returns401or403() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createBook_withValidPayload_returns201() throws Exception {
        BookRequest req = new BookRequest();
        req.setTitle("Integration Test Book");
        req.setIsbn("978-1111111111");
        req.setPublishedYear(2024);
        req.setAuthorId(authorId);
        req.setGenreId(genreId);

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Book"))
                .andExpect(jsonPath("$.author.id").value(authorId));
    }

    @Test
    void createBook_withMissingTitle_returns400() throws Exception {
        BookRequest req = new BookRequest();
        req.setAuthorId(authorId);
        req.setGenreId(genreId);
        // No title set — should fail validation

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withWrongCredentials_returns401() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("username", "testadmin", "password", "WRONG"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is4xxClientError());
    }
}