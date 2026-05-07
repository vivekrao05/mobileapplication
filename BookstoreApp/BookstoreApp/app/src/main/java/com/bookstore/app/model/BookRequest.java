package com.bookstore.app.model;

public class BookRequest {
    private String title;
    private String isbn;
    private Integer publishedYear;
    private Long authorId;
    private Long genreId;

    public BookRequest(String title, String isbn, Integer publishedYear,
                       Long authorId, Long genreId) {
        this.title = title;
        this.isbn = isbn;
        this.publishedYear = publishedYear;
        this.authorId = authorId;
        this.genreId = genreId;
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

    public Long getAuthorId() {
        return authorId;
    }

    public Long getGenreId() {
        return genreId;
    }
}