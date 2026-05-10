package com.bookstore.bookstore_api;

import com.bookstore.bookstore_api.model.Author;
import com.bookstore.bookstore_api.model.Book;
import com.bookstore.bookstore_api.model.Genre;
import com.bookstore.bookstore_api.model.User;
import com.bookstore.bookstore_api.repository.AuthorRepository;
import com.bookstore.bookstore_api.repository.BookRepository;
import com.bookstore.bookstore_api.repository.GenreRepository;
import com.bookstore.bookstore_api.repository.UserRepository;
import com.bookstore.bookstore_api.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("vivek2026"));
            userRepository.save(admin);

        }

        if (authorRepository.count() == 0) {
            Author a1 = new Author();
            a1.setName("Donald Knuth");
            Author a2 = new Author();
            a2.setName("Alan Turing");
            Author a3 = new Author();
            a3.setName("Grace Hopper");
            Author a4 = new Author();
            a4.setName("Isaac Asimov");
            Author a5 = new Author();
            a5.setName("Martin Fowler");
            authorRepository.saveAll(List.of(a1, a2, a3, a4, a5));


            Genre g1 = new Genre();
            g1.setName("Engineering");
            Genre g2 = new Genre();
            g2.setName("Networks");
            Genre g3 = new Genre();
            g3.setName("Science");
            genreRepository.saveAll(List.of(g1, g2, g3));

            Book b1 = new Book();
            b1.setTitle("1984");
            b1.setIsbn("978-0451524935");
            b1.setPublishedYear(1949);
            b1.setAuthor(a1);
            b1.setGenre(g1);

            Book b2 = new Book();
            b2.setTitle("Software Engineering");
            b2.setIsbn("978-0439708180");
            b2.setPublishedYear(1997);
            b2.setAuthor(a2);
            b2.setGenre(g2);

            Book b3 = new Book();
            b3.setTitle("Clean Code");
            b3.setIsbn("978-0132350884");
            b3.setPublishedYear(2008);
            b3.setAuthor(a5);
            b3.setGenre(g1);

            Book b4 = new Book();
            b4.setTitle("The C Programming Language");
            b4.setIsbn("978-0131103627");
            b4.setPublishedYear(1978);
            b4.setAuthor(a4);
            b4.setGenre(g3);

            Book b5 = new Book();
            b5.setTitle("Artificial Intelligence: A Modern Approach");
            b5.setIsbn("978-0136042594");
            b5.setPublishedYear(2009);
            b5.setAuthor(a3);
            b5.setGenre(g2);

            bookRepository.saveAll(List.of(b1, b2, b3, b4, b5));


        }

    }
}
