package com.bookstore.bookstore_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "books") @Data @NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String title;
    private String isbn;
    private Integer publishedYear;
    @ManyToOne @JoinColumn(name = "author_id")
    private Author author;
    @ManyToOne @JoinColumn(name = "genre_id")
    private Genre genre;
}
