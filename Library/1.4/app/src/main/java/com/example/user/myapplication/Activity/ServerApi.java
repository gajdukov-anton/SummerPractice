package com.example.user.myapplication.Activity;

import com.example.user.myapplication.Objects.Book;
import com.example.user.myapplication.Objects.Booking;
import com.example.user.myapplication.Objects.Cancel;
import com.example.user.myapplication.Objects.Card;
import com.example.user.myapplication.Objects.FullInformationAboutBook;
import com.example.user.myapplication.Objects.ResponseFromServer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServerApi {
    @GET("/books/showPage/{id}")
    Call<List<Card>> getCards(@Path("id") int numPage);

    @GET("/books/searchBook")
    Call<List<Card>> searchBook(@Query("substring") String substring);

    @GET("/books")
    Call<List<Card>> getAvailable(@Query("available") boolean isAvailable);

    @GET("/books/{id}")
    Call<FullInformationAboutBook> getInfoBook(@Path("id") String idBook);

    @POST("/books")
    Call<ResponseFromServer> postBook(@Body Book book);

    @POST("/booking")
    Call<ResponseFromServer> postBooking(@Body Booking booking);

    @POST("/cancelBooking")
    Call<ResponseFromServer> postCancel(@Body Cancel id);
}
