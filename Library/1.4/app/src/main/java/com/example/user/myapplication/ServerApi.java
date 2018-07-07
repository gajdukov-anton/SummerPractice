package com.example.user.myapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServerApi {
    @GET("/books/showPage/{id}")
    Call<List<Card>> getCards(@Path("id") int numPage);

    @POST("/books")
    Call<ResponseFromServer> postBook(@Body Book book);

    @POST("/booking")
    Call<ResponseFromServer> postBooking(@Body Booking booking);

    @POST("/cancelBooking")
    Call<ResponseFromServer> postCancel(@Body Cancel id);
}
