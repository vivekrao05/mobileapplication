package com.bookstore.bookstore_api.service;

import com.bookstore.bookstore_api.dto.BookRequest;
import com.bookstore.bookstore_api.dto.BookResponse;
import com.bookstore.bookstore_api.model.Book;
import com.bookstore.bookstore_api.repository.AuthorRepository;
import com.bookstore.bookstore_api.repository.BookRepository;
import com.bookstore.bookstore_api.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;
    private final GenreRepository genreRepo;

    // ── READ ALL ───────────────────────────────────────────
    public List<BookResponse> getAllBooks() {
        return bookRepo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── READ ONE ───────────────────────────────────────────
    public BookResponse getBookById(Long id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
        return toResponse(book);
    }

    // ── CREATE ─────────────────────────────────────────────
    public BookResponse createBook(BookRequest req) {
        Book book = new Book();
        book.setTitle(req.getTitle());
        book.setIsbn(req.getIsbn());
        book.setPublishedYear(Math.toIntExact(req.getPublishedYear()));
        book.setAuthor(authorRepo.findById(req.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found: " + req.getAuthorId())));
        book.setGenre(genreRepo.findById(req.getGenreId())
                .orElseThrow(() -> new RuntimeException("Genre not found: " + req.getGenreId())));
        return toResponse(bookRepo.save(book));
    }

    // ── UPDATE ─────────────────────────────────────────────
    public BookResponse updateBook(Long id, BookRequest req) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
        book.setTitle(req.getTitle());
        book.setIsbn(req.getIsbn());
        book.setPublishedYear(Math.toIntExact(req.getPublishedYear()));
        book.setAuthor(authorRepo.findById(req.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found: " + req.getAuthorId())));
        book.setGenre(genreRepo.findById(req.getGenreId())
                .orElseThrow(() -> new RuntimeException("Genre not found: " + req.getGenreId())));
        return toResponse(bookRepo.save(book));
    }

    // ── DELETE ─────────────────────────────────────────────
    public void deleteBook(Long id) {
        if (!bookRepo.existsById(id)) {
            throw new RuntimeException("Book not found: " + id);
        }
        bookRepo.deleteById(id);
    }

    // ── MAPPER ─────────────────────────────────────────────
    private BookResponse toResponse(Book book) {

        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setIsbn(book.getIsbn());
        response.setPublishedYear(book.getPublishedYear());


        if (book.getAuthor() != null) {
            BookResponse.AuthorDto authorDto = new BookResponse.AuthorDto();
            authorDto.setId(book.getAuthor().getId());
            authorDto.setName(book.getAuthor().getName());
            authorDto.setBiography(book.getAuthor().getBiography());
            response.setAuthor(authorDto);

        }


        if (book.getGenre() != null) {
            BookResponse.GenreDto genreDto = new BookResponse.GenreDto();
            genreDto.setId(book.getGenre().getId());
            genreDto.setName(book.getGenre().getName());
            response.setGenre(genreDto);
        }

        return response;
    }
}