package com.bookstore.bookstore_api.service;

import com.bookstore.bookstore_api.dto.GenreRequest;
import com.bookstore.bookstore_api.dto.GenreResponse;
import com.bookstore.bookstore_api.model.Book;
import com.bookstore.bookstore_api.model.Genre;
import com.bookstore.bookstore_api.repository.BookRepository;
import com.bookstore.bookstore_api.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final BookRepository bookRepo;


    //    GET ALL Genres
    public List<GenreResponse> getAllGenres() {
        return genreRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    //    CREATE Genre
    public GenreResponse createGenre(Genre request) {
        Genre genre = new Genre();
        genre.setName(request.getName());
        return toResponse(genreRepository.save(genre));
    }

//    UPDATE Genre
    public GenreResponse updateGenre(Long id, GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found: " + id));
        genre.setName(request.getName());
        return toResponse(genreRepository.save(genre));
    }

//    DELETE Genre
public void deleteGenre(Long id) {
    Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Genre not found: " + id));
    // Set genre to null on all books that reference it
    List<Book> books = bookRepo.findByGenreId(id);
    books.forEach(book -> book.setGenre(null));
    bookRepo.saveAll(books);
    genreRepository.deleteById(id);
}


    // ── MAPPER ─────────────────────────────────────────────
    private GenreResponse toResponse(Genre genre) {
        GenreResponse response = new GenreResponse();
        response.setId(genre.getId());
        response.setName(genre.getName());
        return response;
    }
}
