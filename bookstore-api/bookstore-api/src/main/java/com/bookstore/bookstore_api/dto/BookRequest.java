package com.bookstore.bookstore_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookRequest {

    @NotBlank private String title;
    private String isbn;
    private Long publishedYear;
    private Long authorId;
    private Long genreId;
}
