package com.bookstore.app.network;

import com.bookstore.app.model.AuthorRequest;
import com.bookstore.app.model.AuthorResponse;
import com.bookstore.app.model.BookRequest;
import com.bookstore.app.model.BookResponse;
import com.bookstore.app.model.GenreRequest;
import com.bookstore.app.model.GenreResponse;
import com.bookstore.app.model.LoginRequest;
import com.bookstore.app.model.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // ── AUTH ──────────────────────────────────────────
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // ── BOOKS ─────────────────────────────────────────
    @GET("api/books")
    Call<List<BookResponse>> getBooks();

    @GET("api/books/{id}")
    Call<BookResponse> getBook(@Path("id") Long id);

    @POST("api/books")
    Call<BookResponse> createBook(@Body BookRequest request);

    @PUT("api/books/{id}")
    Call<BookResponse> updateBook(@Path("id") Long id, @Body BookRequest request);

    @DELETE("api/books/{id}")
    Call<Void> deleteBook(@Path("id") Long id);

    // ── AUTHORS ───────────────────────────────────────
    @GET("api/authors")
    Call<List<AuthorResponse>> getAuthors();

    @POST("api/authors")
    Call<AuthorResponse> createAuthor(@Body AuthorRequest request);

    @PUT("api/authors/{id}")
    Call<AuthorResponse> updateAuthor(@Path("id") Long id, @Body AuthorRequest request);

    @DELETE("api/authors/{id}")
    Call<Void> deleteAuthor(@Path("id") Long id);

    // ── GENRES ────────────────────────────────────────
    @GET("api/genres")
    Call<List<GenreResponse>> getGenres();

    @POST("api/genres")
    Call<GenreResponse> createGenre(@Body GenreRequest request);

    @PUT("api/genres/{id}")
    Call<GenreResponse> updateGenre(@Path("id") Long id, @Body GenreRequest request);

    @DELETE("api/genres/{id}")
    Call<Void> deleteGenre(@Path("id") Long id);
}