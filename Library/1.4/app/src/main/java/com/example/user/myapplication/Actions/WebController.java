package com.example.user.myapplication.Actions;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;

import com.example.user.myapplication.Activity.ServerApi;
import com.example.user.myapplication.Objects.Card;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebController {

    static public ServerApi serverApi;

    public WebController() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://libraryomega.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serverApi = retrofit.create(ServerApi.class);
    }

}
