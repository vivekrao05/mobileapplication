package com.bookstore.bookstore_api.service;

import com.bookstore.bookstore_api.dto.BookRequest;
import com.bookstore.bookstore_api.dto.BookResponse;
import com.bookstore.bookstore_api.model.Author;
import com.bookstore.bookstore_api.model.Book;
import com.bookstore.bookstore_api.model.Genre;
import com.bookstore.bookstore_api.repository.AuthorRepository;
import com.bookstore.bookstore_api.repository.BookRepository;
import com.bookstore.bookstore_api.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock private BookRepository   bookRepo;
    @Mock private AuthorRepository authorRepo;
    @Mock private GenreRepository  genreRepo;

    @InjectMocks private BookService bookService;

    private Author author;
    private Genre  genre;
    private Book   sampleBook;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setName("George Orwell");

        genre = new Genre();
        genre.setId(1L);
        genre.setName("Dystopian");

        sampleBook = new Book();
        sampleBook.setId(1L);
        sampleBook.setTitle("1984");
        sampleBook.setIsbn("978-0451524935");
        sampleBook.setPublishedYear(1949);
        sampleBook.setAuthor(author);
        sampleBook.setGenre(genre);
    }

    @Test
    void getAllBooks_returnsListOfResponses() {
        when(bookRepo.findAll()).thenReturn(List.of(sampleBook));

        List<BookResponse> result = bookService.getAllBooks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("1984");
        assertThat(result.get(0).getAuthor().getName()).isEqualTo("George Orwell");
    }

    @Test
    void getBookById_whenFound_returnsResponse() {
        when(bookRepo.findById(1L)).thenReturn(Optional.of(sampleBook));

        BookResponse result = bookService.getBookById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("1984");
    }

    @Test
    void getBookById_whenNotFound_throws() {
        when(bookRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Book not found");
    }

    @Test
    void createBook_savesAndReturns() {
        BookRequest req = new BookRequest("New Book", null, null, 1L, 1L);

        when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
        when(genreRepo.findById(1L)).thenReturn(Optional.of(genre));
        when(bookRepo.save(any(Book.class))).thenAnswer(inv -> {
            Book b = inv.getArgument(0);
            b.setId(42L);
            return b;
        });

        BookResponse result = bookService.createBook(req);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getTitle()).isEqualTo("New Book");
        verify(bookRepo).save(any(Book.class));
    }

    @Test
    void deleteBook_callsRepository() {
        when(bookRepo.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepo).deleteById(1L);
    }

    @Test
    void deleteBook_whenNotFound_throws() {
        when(bookRepo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> bookService.deleteBook(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Book not found");
    }
}