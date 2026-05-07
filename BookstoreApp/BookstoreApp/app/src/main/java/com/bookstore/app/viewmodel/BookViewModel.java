package com.bookstore.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bookstore.app.model.BookRequest;
import com.bookstore.app.model.BookResponse;
import com.bookstore.app.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookViewModel extends ViewModel {

    private final MutableLiveData<List<BookResponse>> booksLive = new MutableLiveData<>();
    private final MutableLiveData<String> errorLive = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLive = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> successLive = new MutableLiveData<>();

    public LiveData<List<BookResponse>> getBooks() {
        return booksLive;
    }

    public LiveData<String> getError() {
        return errorLive;
    }

    public LiveData<Boolean> getLoading() {
        return loadingLive;
    }

    public LiveData<Boolean> getSuccess() {
        return successLive;
    }

    public void fetchBooks(ApiService api) {
        loadingLive.setValue(true);
        api.getBooks().enqueue(new Callback<List<BookResponse>>() {
            @Override
            public void onResponse(Call<List<BookResponse>> call,
                                   Response<List<BookResponse>> response) {
                loadingLive.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    booksLive.setValue(response.body());
                } else {
                    errorLive.setValue("Failed to load books");
                }
            }

            @Override
            public void onFailure(Call<List<BookResponse>> call, Throwable t) {
                loadingLive.setValue(false);
                errorLive.setValue("Connection error: " + t.getMessage());
            }
        });
    }

    public void createBook(ApiService api, BookRequest request) {
        api.createBook(request).enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call,
                                   Response<BookResponse> response) {
                if (response.isSuccessful()) {
                    successLive.setValue(true);
                    fetchBooks(api);
                } else {
                    errorLive.setValue("Failed to create book");
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                errorLive.setValue("Connection error: " + t.getMessage());
            }
        });
    }

    public void updateBook(ApiService api, Long id, BookRequest request) {
        api.updateBook(id, request).enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call,
                                   Response<BookResponse> response) {
                if (response.isSuccessful()) {
                    successLive.setValue(true);
                    fetchBooks(api);
                } else {
                    errorLive.setValue("Failed to update book");
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                errorLive.setValue("Connection error: " + t.getMessage());
            }
        });
    }

    public void deleteBook(ApiService api, Long id) {
        api.deleteBook(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successLive.setValue(true);
                    fetchBooks(api);
                } else {
                    errorLive.setValue("Failed to delete book");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorLive.setValue("Connection error: " + t.getMessage());
            }
        });
    }
}