package com.bookstore.bookstore_api.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ToStringRecursionTest {

    @Test
    void testToStringRecursion() {
        Author author = new Author();
        author.setId(1L);
        author.setName("Test Author");
        author.setBooks(new ArrayList<>());

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor(author);

        author.getBooks().add(book);

        // This would throw StackOverflowError before the fix
        assertDoesNotThrow(() -> {
            String authorStr = author.toString();
            String bookStr = book.toString();
            System.out.println("Author toString: " + authorStr);
            System.out.println("Book toString: " + bookStr);
        });
    }

    @Test
    void testEqualsHashCodeRecursion() {
        Author author1 = new Author();
        author1.setId(1L);
        author1.setName("Test Author");
        author1.setBooks(new ArrayList<>());

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Test Book");
        book1.setAuthor(author1);
        author1.getBooks().add(book1);

        Author author2 = new Author();
        author2.setId(1L);
        author2.setName("Test Author");
        author2.setBooks(new ArrayList<>());

        Book book2 = new Book();
        book2.setId(1L);
        book2.setTitle("Test Book");
        book2.setAuthor(author2);
        author2.getBooks().add(book2);

        assertDoesNotThrow(() -> {
            author1.equals(author2);
            author1.hashCode();
            book1.equals(book2);
            book1.hashCode();
        });
    }
}
