package com.bookstore.bookstore_api.service;

import com.bookstore.bookstore_api.dto.AuthorRequest;
import com.bookstore.bookstore_api.dto.AuthorResponse;
import com.bookstore.bookstore_api.model.Author;
import com.bookstore.bookstore_api.model.Book;
import com.bookstore.bookstore_api.repository.AuthorRepository;
import com.bookstore.bookstore_api.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepo;

    //    GET all Authors
    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    //    GET Author by ID
    public Author getAuthorById(Long id) {
        return authorRepository.findById(id).orElseThrow(() -> new RuntimeException("Author with ID " + id + " not found"));
    }

    //    CREATE Author
    public AuthorResponse createAuthor(AuthorRequest request) {
        Author newAuthor = new Author();
        newAuthor.setName(request.getName());
        newAuthor.setBiography(request.getBiography());
        return toResponse(authorRepository.save(newAuthor));
    }

    //    UPDATE Author
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found: " + id));
        author.setName(request.getName());
        author.setBiography(request.getBiography());
        return toResponse(authorRepository.save(author));
    }

    //    DELETE Author by ID
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found: " + id));
        List<Book> books = bookRepo.findByAuthorId(id);
        books.forEach(book -> book.setAuthor(null));
        bookRepo.saveAll(books);
        authorRepository.deleteById(id);


    }

    // ── MAPPER ─────────────────────────────────────────────
    private AuthorResponse toResponse(Author author) {
        AuthorResponse response = new AuthorResponse();
        response.setId(author.getId());
        response.setName(author.getName());
        response.setBiography(author.getBiography());
        return response;
    }


}
