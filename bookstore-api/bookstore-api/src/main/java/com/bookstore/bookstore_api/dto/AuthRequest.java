package com.bookstore.bookstore_api.dto;

import lombok.Data;

@Data
public class AuthRequest {

    private String username;
    private String password;
}
