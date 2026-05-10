package com.bookstore.bookstore_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {

    @NotBlank private String title;
    private String isbn;
    private Integer publishedYear;
    private Long authorId;
    private Long genreId;
}
