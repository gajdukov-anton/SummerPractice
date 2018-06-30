package com.example.user.myapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServerApi {
    @GET("/books/showPage/{id}")
    Call<List<Card>> getCards(@Path("id") int numPage);
}
