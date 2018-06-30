package com.example.user.myapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ServerApi {
    @GET("/books/showPage/1")
    Call<List<Card>> getCards();
}
