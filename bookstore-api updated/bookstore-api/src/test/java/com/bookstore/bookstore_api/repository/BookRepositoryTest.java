package com.bookstore.bookstore_api.repository;

import com.bookstore.bookstore_api.model.Author;
import com.bookstore.bookstore_api.model.Book;
import com.bookstore.bookstore_api.model.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired private BookRepository   bookRepo;
    @Autowired private AuthorRepository authorRepo;
    @Autowired private GenreRepository  genreRepo;

    @Test
    void shouldSaveAndRetrieveBook() {
        Author author = new Author();
        author.setName("Test Author");
        author = authorRepo.save(author);

        Genre genre = new Genre();
        genre.setName("Fiction");
        genre = genreRepo.save(genre);

        Book book = new Book();
        book.setTitle("Test Book");
        book.setIsbn("978-0000000000");
        book.setPublishedYear(2024);
        book.setAuthor(author);
        book.setGenre(genre);

        Book saved = bookRepo.save(book);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Book");
        assertThat(saved.getAuthor().getName()).isEqualTo("Test Author");
        assertThat(saved.getGenre().getName()).isEqualTo("Fiction");
        assertThat(saved.getIsbn()).isEqualTo("978-0000000000");
    }

    @Test
    void shouldFindBooksByAuthorId() {
        Author author = authorRepo.save(makeAuthor("J.K. Rowling"));
        Genre genre   = genreRepo.save(makeGenre("Fantasy"));
        bookRepo.save(makeBook("HP1", author, genre));
        bookRepo.save(makeBook("HP2", author, genre));

        List<Book> result = bookRepo.findByAuthorId(author.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Book::getTitle).containsExactlyInAnyOrder("HP1", "HP2");
    }

    @Test
    void shouldDetectExistingBookByGenre() {
        Author author = authorRepo.save(makeAuthor("Author"));
        Genre genre   = genreRepo.save(makeGenre("Sci-Fi"));
        bookRepo.save(makeBook("Foundation", author, genre));


        assertThat(bookRepo.existsByGenreId(genre.getId())).isTrue();
        assertThat(bookRepo.existsByGenreId(99999L)).isFalse();
    }

    @Test
    void shouldDeleteBook() {
        Author author = authorRepo.save(makeAuthor("Author"));
        Genre genre   = genreRepo.save(makeGenre("Genre"));
        Book book = bookRepo.save(makeBook("Doomed", author, genre));

        bookRepo.deleteById(book.getId());

        Optional<Book> found = bookRepo.findById(book.getId());
        assertThat(found).isEmpty();
    }

    // Helpers
    private Author makeAuthor(String name) {
        Author a = new Author();
        a.setName(name);
        return a;
    }
    private Genre makeGenre(String name) {
        Genre g = new Genre();
        g.setName(name);
        return g;
    }
    private Book makeBook(String title, Author a, Genre g) {
        Book b = new Book();
        b.setTitle(title);
        b.setAuthor(a);
        b.setGenre(g);
        return b;
    }
}
