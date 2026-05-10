package com.bookstore.bookstore_api.repository;

import com.bookstore.bookstore_api.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthorId(Long authorId);
    List<Book> findByGenreId(Long genreId);

    boolean existsByGenreId(Long genreId);
    boolean existsByAuthorId(Long authorId);
}
