package com.bookstore.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "bookstore_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER = "jwt_user";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public void saveUser(String username) {
        prefs.edit().putString(KEY_USER, username).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getUser() {
        return prefs.getString(KEY_USER, "");
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
