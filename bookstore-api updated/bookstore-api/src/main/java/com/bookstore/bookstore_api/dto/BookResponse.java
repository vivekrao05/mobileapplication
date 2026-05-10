package com.bookstore.bookstore_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String isbn;
    private Integer publishedYear;
    private AuthorDto author;
    private GenreDto genre;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthorDto {
        private Long id;
        private String name;
        private String biography;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GenreDto {
        private Long id;
        private String name;
    }
}