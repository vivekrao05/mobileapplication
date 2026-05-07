package com.bookstore.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bookstore.app.model.LoginRequest;
import com.bookstore.app.model.LoginResponse;
import com.bookstore.app.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {
    private final MutableLiveData<String> tokenLive = new MutableLiveData<>();
    private final MutableLiveData<String> errorLive = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLive = new MutableLiveData<>(false);

    public LiveData<String> getToken() {
        return tokenLive;
    }

    public LiveData<String> getError() {
        return errorLive;
    }

    public LiveData<Boolean> getLoading() {
        return loadingLive;
    }

    public void login(ApiService api, String username, String password) {
        loadingLive.setValue(true);
        api.login(new LoginRequest(username, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {
                        loadingLive.setValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            tokenLive.setValue(response.body().getToken());
                        } else {
                            errorLive.setValue("Invalid credentials");
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        loadingLive.setValue(false);
                        errorLive.setValue("Connection failed: " + t.getMessage());
                    }
                });
    }

}
