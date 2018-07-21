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

    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        setBackToMainActivityButton();
        addBook();
    }

    private void setBackToMainActivityButton() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void addBook() {
        Button button = (Button) findViewById(R.id.postBook);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.flicker);

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.startAnimation(animation);
                        getInformationAboutBookFromUser();
                        if (book.checkForAllFieldsAreFilled()) {
                            postBookToServer();
                            finish();
                        } else {
                            Toast.makeText(AddBookActivity.this, "Пожалуйста заполните все поля", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void getInformationAboutBookFromUser() {
        book = new Book();
        book.name = getInformationFromField(R.id.nameNewBook);
        book.authors = getInformationFromField(R.id.authorsNewBook);
        book.year = getInformationFromField(R.id.yearNewBook);
        book.link = getInformationFromField(R.id.linkNewBook);
        book.description = getInformationFromField(R.id.descriptionNewBook);
    }

    private String getInformationFromField(int idField) {
        EditText field;
        field = (EditText) findViewById(idField);
        return field.getText().toString();
    }

    void postBookToServer() {
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