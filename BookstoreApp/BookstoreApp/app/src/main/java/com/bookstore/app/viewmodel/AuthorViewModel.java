package com.bookstore.app.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bookstore.app.model.AuthorRequest;
import com.bookstore.app.model.AuthorResponse;
import com.bookstore.app.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorViewModel extends ViewModel {

    private final MutableLiveData<List<AuthorResponse>> authorsLive = new MutableLiveData<>();
    private final MutableLiveData<String> errorLive = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLive = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> successLive = new MutableLiveData<>();

    public LiveData<List<AuthorResponse>> getAuthors() {
        return authorsLive;
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

    public void fetchAuthors(ApiService api) {
        loadingLive.setValue(true);
        api.getAuthors().enqueue(new Callback<List<AuthorResponse>>() {
            @Override
            public void onResponse(Call<List<AuthorResponse>> call,
                                   Response<List<AuthorResponse>> response) {
                loadingLive.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    authorsLive.setValue(response.body());
                } else {
                    errorLive.setValue("Failed to load authors");
                }
            }

            @Override
            public void onFailure(Call<List<AuthorResponse>> call, Throwable t) {
                loadingLive.setValue(false);
                errorLive.setValue("Connection error: " + t.getMessage());
            }
        });
    }

    public void createAuthor(ApiService api, AuthorRequest request) {
        api.createAuthor(request).enqueue(new Callback<AuthorResponse>() {
            @Override
            public void onResponse(Call<AuthorResponse> call,
                                   Response<AuthorResponse> response) {
                if (response.isSuccessful()) {
                    successLive.setValue(true);
                    fetchAuthors(api);
                } else {
                    errorLive.setValue("Failed to create author");
                }
            }

            @Override
            public void onFailure(Call<AuthorResponse> call, Throwable t) {
                errorLive.setValue("Connection error: " + t.getMessage());
            }
        });
    }

    public void updateAuthor(ApiService api, Long id, AuthorRequest request) {
        api.updateAuthor(id, request).enqueue(new Callback<AuthorResponse>() {
            @Override
            public void onResponse(Call<AuthorResponse> call,
                                   Response<AuthorResponse> response) {
                if (response.isSuccessful()) {
                    successLive.setValue(true);
                    fetchAuthors(api);
                } else {
                    errorLive.setValue("Failed to update author");
                }
            }

            @Override
            public void onFailure(Call<AuthorResponse> call, Throwable t) {
                errorLive.setValue("Connection error: " + t.getMessage());
            }
        });
    }

    public void deleteAuthor(ApiService api, Long id) {
        api.deleteAuthor(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successLive.setValue(true);
                    fetchAuthors(api);
                } else {
                    errorLive.setValue("Failed to delete author");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorLive.setValue("Connection error: " + t.getMessage());
            }
        });
    }
}
