package com.bookstore.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bookstore.app.model.GenreRequest;
import com.bookstore.app.model.GenreResponse;
import com.bookstore.app.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreViewModel extends ViewModel {

    private final MutableLiveData<List<GenreResponse>> genresLive  = new MutableLiveData<>();
    private final MutableLiveData<String>              errorLive   = new MutableLiveData<>();
    private final MutableLiveData<Boolean>             loadingLive = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean>             successLive = new MutableLiveData<>();

    public LiveData<List<GenreResponse>> getGenres()   { return genresLive; }
    public LiveData<String>              getError()    { return errorLive; }
    public LiveData<Boolean>             getLoading()  { return loadingLive; }
    public LiveData<Boolean>             getSuccess()  { return successLive; }

    public void fetchGenres(ApiService api) {
        loadingLive.setValue(true);
        api.getGenres().enqueue(new Callback<List<GenreResponse>>() {
            @Override
            public void onResponse(Call<List<GenreResponse>> call,
                                   Response<List<GenreResponse>> response) {
                loadingLive.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    genresLive.setValue(response.body());
                } else {
                    errorLive.setValue("Failed to load genres");
                }
            }
            @Override
            public void onFailure(Call<List<GenreResponse>> call, Throwable t) {
                loadingLive.setValue(false);
                errorLive.setValue("Connection error: " + t.getMessage());
            }
        });
    }

    public void createGenre(ApiService api, GenreRequest request) {
        api.createGenre(request).enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call,
                                   Response<GenreResponse> response) {
                if (response.isSuccessful()) {
                    successLive.setValue(true);
                    fetchGenres(api);
                } else {
                    errorLive.setValue("Failed to create genre");
                }
            }
            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                errorLive.setValue("Connection error: " + t.getMessage());
            }
        });
    }

    public void updateGenre(ApiService api, Long id, GenreRequest request) {
        api.updateGenre(id, request).enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call,
                                   Response<GenreResponse> response) {
                if (response.isSuccessful()) {
                    successLive.setValue(true);
                    fetchGenres(api);
                } else {
                    errorLive.setValue("Failed to update genre");
                }
            }
            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                errorLive.setValue("Connection error: " + t.getMessage());
            }
        });
    }

    public void deleteGenre(ApiService api, Long id) {
        api.deleteGenre(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successLive.setValue(true);
                    fetchGenres(api);
                } else {
                    errorLive.setValue("Failed to delete genre");
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorLive.setValue("Connection error: " + t.getMessage());
            }
        });
    }
}