package com.bookstore.bookstore_api.controller;

import com.bookstore.bookstore_api.dto.BookRequest;
import com.bookstore.bookstore_api.dto.BookResponse;
import com.bookstore.bookstore_api.model.Book;
import com.bookstore.bookstore_api.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAll() {
        List<BookResponse> books = bookService.getAllBooks();
        log.info("Retrieving all {} books", books.size());
        log.info("Book {} : genre=> {}, author=> {}, title=> {}, isbn=> {}, year=> {}",
                books.get(0).getId(),
                books.get(0).getGenre().getName(),
                books.get(0).getAuthor().getName(),
                books.get(0).getTitle(),
                books.get(0).getIsbn(),
                books.get(0).getPublishedYear());
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getOne(@PathVariable Long id) {
        BookResponse book = bookService.getBookById(id);
        log.info("Retrieving book with id {}", id);
        log.info("Book {} : genre=> {}, author=> {}, title=> {}, isbn=> {}, year=> {}",
                book.getId(),
                book.getGenre().getName(),
                book.getAuthor().getName(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublishedYear());

        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest req) {
        log.info("Creating book with request: {}", req);
        return ResponseEntity.status(201).body(bookService.createBook(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable Long id,
                                       @Valid @RequestBody BookRequest req) {
        return ResponseEntity.ok(bookService.updateBook(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build(); //204 No Content
    }
}
