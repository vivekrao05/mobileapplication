package com.bookstore.app.network;

import com.bookstore.app.util.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = tokenManager.getToken();
        Request original = chain.request();

        if (token != null && !token.isEmpty()) {
            Request request = original.newBuilder().header("Authorization", "Bearer " + token).build();
            return chain.proceed(request);
        }
        return chain.proceed(original);
    }
}

