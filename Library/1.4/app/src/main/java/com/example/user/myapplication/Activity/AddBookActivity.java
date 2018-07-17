package com.example.user.myapplication.Activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.myapplication.Objects.Book;
import com.example.user.myapplication.Objects.Card;
import com.example.user.myapplication.Objects.ResponseFromServer;
import com.example.user.myapplication.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.user.myapplication.Activity.MainActivity.serverApi;

public class AddBookActivity extends AppCompatActivity {

    public Card book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        addBook();
    }

    void addBook() {
        Button button = (Button) findViewById(R.id.postBook);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.flicker);

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.startAnimation(animation);
                        postBookToServer();
                        finish();
                    }
                }
        );
    }

    void postBookToServer() {
        EditText text;
        Book book = new Book();
        text = (EditText) findViewById(R.id.nameNewBook);
        book.name = text.getText().toString();
        text = (EditText) findViewById(R.id.authorsNewBook);
        book.authors = text.getText().toString();
        book.available = true;
        text = (EditText) findViewById(R.id.yearNewBook);
        book.year = text.getText().toString();
        text = (EditText) findViewById(R.id.linkNewBook);
        book.link = text.getText().toString();
        text = (EditText) findViewById(R.id.descriptionNewBook);
        book.description = text.getText().toString();

        if (isOnline()) {
            Call<ResponseFromServer> call = serverApi.postBook(book);
            call.enqueue(new Callback<ResponseFromServer>() {
                @Override
                public void onResponse(Call<ResponseFromServer> call, Response<ResponseFromServer> response) {
                    if (response.isSuccessful()) {
                        ResponseFromServer info = response.body();
                        if (info != null) {
                            Toast.makeText(AddBookActivity.this, info.message, Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AddBookActivity.this, "Impossible to connect to server", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseFromServer> call, Throwable t) {
                }
            });
        } else {
            Toast.makeText(AddBookActivity.this, "Отсутствует подключение к интернету", Snackbar.LENGTH_LONG).show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}


