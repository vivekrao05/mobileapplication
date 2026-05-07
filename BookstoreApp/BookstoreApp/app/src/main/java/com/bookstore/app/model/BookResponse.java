package com.bookstore.app.model;

public class BookResponse {
    private Long id;
    private String title;
    private String isbn;
    private Integer publishedYear;
    private AuthorResponse author;
    private GenreResponse genre;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public AuthorResponse getAuthor() {
        return author;
    }

    public GenreResponse getGenre() {
        return genre;
    }
}