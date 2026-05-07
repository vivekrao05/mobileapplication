package com.bookstore.app.model;

public class AuthorRequest {
    private String name;
    private String biography;

    public AuthorRequest(String name, String biography) {
        this.name = name;
        this.biography = biography;
    }

    public String getName() {
        return name;
    }

    public String getBiography() {
        return biography;
    }
}