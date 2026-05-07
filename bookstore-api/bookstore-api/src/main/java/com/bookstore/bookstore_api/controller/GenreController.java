package com.bookstore.bookstore_api.controller;

import com.bookstore.bookstore_api.dto.GenreRequest;
import com.bookstore.bookstore_api.dto.GenreResponse;
import com.bookstore.bookstore_api.model.Genre;
import com.bookstore.bookstore_api.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

//    GET ALL GENRES
    @GetMapping
    public ResponseEntity<List<GenreResponse>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

//    CREATE GENRE
    @PostMapping
    public ResponseEntity<GenreResponse> createGenre(@RequestBody Genre genre) {
        return ResponseEntity.status(201).body(genreService.createGenre(genre));
    }

//    UPDATE GENRE
    @PutMapping("/{id}")
    public ResponseEntity<GenreResponse> update(@PathVariable Long id,
                                                @RequestBody GenreRequest request) {
        return ResponseEntity.ok(genreService.updateGenre(id, request));
    }

//    Delete GENRE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
