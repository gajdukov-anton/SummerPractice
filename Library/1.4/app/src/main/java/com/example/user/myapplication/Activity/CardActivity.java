package com.example.user.myapplication.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication.Objects.Booking;
import com.example.user.myapplication.Objects.Cancel;
import com.example.user.myapplication.Objects.Card;
import com.example.user.myapplication.Objects.InfoBook;
import com.example.user.myapplication.Objects.ResponseFromServer;
import com.example.user.myapplication.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.user.myapplication.Activity.MainActivity.serverApi;

public class CardActivity extends AppCompatActivity {

    private Card card;
    private TextView textParam;
    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        card = getIntent().getParcelableExtra("card");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        buildCard();
        openBrowser();
        takeBook();
        cancelBooking();
        getBookInfo();
    }

    void getBookInfo() {
        Button button = (Button) findViewById(R.id.getMoreInformation);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.flicker);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animation);
                getInfoFromServer(card._id);
            }
        });
    }

    void openBrowser() {
        TextView textView = (TextView) findViewById(R.id.link);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadActivity();
            }
        });
    }

    void takeBook() {
        Button button = (Button) findViewById(R.id.takeBook);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.flicker);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animation);
                if (card.available) {
                    LayoutInflater li = LayoutInflater.from(context);
                    View dialogBox = li.inflate(R.layout.dialog_box, null);
                    AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                    mDialogBuilder.setView(dialogBox);
                    final EditText userInput = (EditText) dialogBox.findViewById(R.id.input_text);
                    mDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if (userInput.getText().toString().length() != 0) {
                                                Booking booking = new Booking(card._id, userInput.getText().toString());
                                                postBookingToServer(booking);
                                            } else {
                                                Toast.makeText(CardActivity.this, "Пожалуйста, ввидите имя и фамилию", Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    })
                            .setNegativeButton("Отмена",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    AlertDialog alertDialog = mDialogBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(CardActivity.this, "Книга уже взята", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    void cancelBooking () {
        Button button = (Button) findViewById(R.id.cancelBooking);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.flicker);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animation);
                if (!card.available) {
                    Cancel id = new Cancel(card._id);
                    postCancelBooking(id);
                } else {
                    Toast.makeText(CardActivity.this, "Чтобы отменить заказ, нужно взять книгу", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    void loadActivity() {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("cardLink", card.link);
        startActivity(intent);
    }

    void getInfoFromServer(String id) {
        if (isOnline()) {
            Call<InfoBook> call = serverApi.getInfoBook(id);
            call.enqueue(new Callback<InfoBook>() {
                @Override
                public void onResponse(Call<InfoBook> call, Response<InfoBook> response) {
                    if (response.isSuccessful()) {
                        InfoBook infoBook = response.body();
                        if (infoBook.lastBooking != null) {
                            Toast.makeText(CardActivity.this, infoBook.lastBooking.user + "\n" + infoBook.lastBooking.taken + "\n" + infoBook.lastBooking.returned
                                    , Snackbar.LENGTH_LONG).show();
//                            addToCard(infoBook.lastBooking.user, infoBook.lastBooking.taken, infoBook.lastBooking.returned);
                        } else {
                            Toast.makeText(CardActivity.this, "Отсутсвует дополнительная  информация" + infoBook.book.authors, Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(CardActivity.this, "Impossible to connect to server", Snackbar.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<InfoBook> call, Throwable t) {
                }
            });
        } else {
            Toast.makeText(CardActivity.this, "Отсутствует подключение к интернету", Snackbar.LENGTH_LONG).show();
        }
    }

    void postBookingToServer (Booking booking) {
        if (isOnline()) {
            Call<ResponseFromServer> call = serverApi.postBooking(booking);
            call.enqueue(new Callback<ResponseFromServer>() {
                @Override
                public void onResponse(Call<ResponseFromServer> call, Response<ResponseFromServer> response) {
                    if (response.isSuccessful()) {
                        ResponseFromServer info = response.body();
                        if (info != null) {
                            Toast.makeText(CardActivity.this, info.message, Snackbar.LENGTH_LONG).show();
                            updateState();
                        }
                    } else {
                        Toast.makeText(CardActivity.this, "Impossible to connect to server", Snackbar.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseFromServer> call, Throwable t) {
                }
            });
        } else {
            Toast.makeText(CardActivity.this, "Отсутствует подключение к интернету", Snackbar.LENGTH_LONG).show();
        }
    }

    void postCancelBooking (Cancel id) {
        if (isOnline()) {
            Call<ResponseFromServer> call = serverApi.postCancel(id);
            call.enqueue(new Callback<ResponseFromServer>() {
                @Override
                public void onResponse(Call<ResponseFromServer> call, Response<ResponseFromServer> response) {
                    if (response.isSuccessful()) {
                        ResponseFromServer info = response.body();
                        if (info != null) {
                            Toast.makeText(CardActivity.this, info.message, Snackbar.LENGTH_LONG).show();
                            updateState();
                        }
                    } else {
                        Toast.makeText(CardActivity.this, "Impossible to connect to server", Snackbar.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseFromServer> call, Throwable t) {
                }
            });
        } else {
            Toast.makeText(CardActivity.this, "Отсутствует подключение к интернету", Snackbar.LENGTH_LONG).show();
        }
    }

//    private void addToCard(String user, String taken, String returned) {
//        String text;
//        textParam = findViewById(R.id.user);
//        text = "User: " + user;
//        textParam.setText(text);
//        textParam = findViewById(R.id.taken);
//        text = "Taken: " + taken;
//        textParam.setText(text);
//        textParam = findViewById(R.id.returned);
//        text = "Returned: " + returned;
//        textParam.setText(text);
//    }

    private void buildCard() {

        String text;
        textParam = findViewById(R.id.name);
        text = "Name: " + card.name;
        textParam.setText(text);
        textParam = findViewById(R.id.authors);
        text = "Authors: " + card.authors;
        textParam.setText(text);
        textParam = findViewById(R.id.year);
        text = "Year: " + card.year;
        textParam.setText(text);
        textParam = findViewById(R.id.link);
        text = "Link: " + card.link;
        textParam.setText(text);
        textParam = findViewById(R.id.available);
        text = "Available: " + card.availableToString();
        textParam.setText(text);
        textParam = findViewById(R.id.description);
        text = "Description: " + card.description;
        textParam.setText(text);
        textParam.setMovementMethod(new ScrollingMovementMethod());
    }

    private void updateState() {
        String text;
        if (card.available) {
            card.available = false;
        } else {
            card.available = true;
        }
        textParam = findViewById(R.id.available);
        text = "Available: " + card.availableToString();
        textParam.setText(text);
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

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
