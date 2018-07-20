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
    private InfoBook infoBook;
    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        card = getIntent().getParcelableExtra("card");

        setBackToMainActivityButton();
        setMainDataIntoCard();
        activateTheIntegratedBrowser();
        makeOrderForTheBook();
        cancelOrderForTheBook();
        getAdditionalDataAboutBook();
    }

    private void setBackToMainActivityButton() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    void getAdditionalDataAboutBook() {
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

    void activateTheIntegratedBrowser() {
        TextView textView = (TextView) findViewById(R.id.link);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadActivity();
            }
        });
    }

    void makeOrderForTheBook() {
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

    void cancelOrderForTheBook() {
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
                        infoBook = response.body();
                        if (infoBook.lastBooking._id != null) {
                            setAdditionalDataIntoCard();
                        } else {
                            Toast.makeText(CardActivity.this, "Отсутсвует дополнительная  информация", Snackbar.LENGTH_LONG).show();
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

    void postBookingToServer(Booking booking) {
        if (isOnline()) {
            Call<ResponseFromServer> call = serverApi.postBooking(booking);
            call.enqueue(new Callback<ResponseFromServer>() {
                @Override
                public void onResponse(Call<ResponseFromServer> call, Response<ResponseFromServer> response) {
                    if (response.isSuccessful()) {
                        ResponseFromServer info = response.body();
                        if (info != null) {
                            Toast.makeText(CardActivity.this, info.message, Snackbar.LENGTH_LONG).show();
                            updateAvailableState();
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

    void postCancelBooking(Cancel id) {
        if (isOnline()) {
            Call<ResponseFromServer> call = serverApi.postCancel(id);
            call.enqueue(new Callback<ResponseFromServer>() {
                @Override
                public void onResponse(Call<ResponseFromServer> call, Response<ResponseFromServer> response) {
                    if (response.isSuccessful()) {
                        ResponseFromServer info = response.body();
                        if (info != null) {
                            Toast.makeText(CardActivity.this, info.message, Snackbar.LENGTH_LONG).show();
                            updateAvailableState();
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


    private void setAdditionalDataIntoCard() {
        setValueToField("User: ",R.id.user , infoBook.lastBooking.user);
        setVisibleBorder(R.id.userBorder);
        setValueToField("Taken: ",R.id.taken , infoBook.lastBooking.taken);
        setVisibleBorder(R.id.takenBorder);
        setValueToField("Returned: ",R.id.returned , infoBook.lastBooking.returned);
        setVisibleBorder(R.id.returnedBorder);
    }

    private void setVisibleBorder(int borderId) {
        View border = (View) findViewById(borderId);
        border.setVisibility(View.VISIBLE);
    }

    private void setMainDataIntoCard() {
        setValueToField("Name: ", R.id.name, card.name);
        setValueToField("Authors: ", R.id.authors, card.authors);
        setValueToField("Year: ", R.id.year, card.year);
        setValueToField("Link: ", R.id.link, card.link);
        setValueToField("Available: ", R.id.available, card.availableToString());
        setValueToField("Description: ", R.id.description, card.description);
    }

    private void setValueToField(String paramName, int paramId, String paramValue) {
        TextView field;
        String value;
        field = findViewById(paramId);

        if (field.getVisibility() != View.VISIBLE)
            field.setVisibility(View.VISIBLE);

        value = paramName + paramValue;
        field.setText(value);
    }

    private void updateAvailableState() {
        String value;
        TextView field;
        if (card.available)
            card.available = false;
        else
            card.available = true;
        field = findViewById(R.id.available);
        value = "Available: " + card.availableToString();
        field.setText(value);
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
