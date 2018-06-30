package com.example.user.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScrollingActivity extends AppCompatActivity {
    private List<Card> cards;
    private RecyclerView rv;
    private Retrofit retrofit;
    private  ServerApi serverApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //      .setAction("Action", null).show();
                 readJson();
            }
        });

        rv = (RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        connectToServer();
        initializeData();
        initializeAdapter();
    }

    void showSnackbar(int position) {
        Snackbar.make(rv, "Карточка с номером " + position, Snackbar.LENGTH_LONG).show();
    }

    void openCard(int position) {
        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra("card", cards.get(position));
        startActivity(intent);
    }

    private void initializeData(){
        cards = new ArrayList<>();
        cards.add(new Card("Harry Potter", "Part 1", R.drawable.harry));
        cards.add(new Card("Harry Potter", "Part 2", R.drawable.harry));
        cards.add(new Card("Живопись", "Часть 1", R.drawable.zivopis));
        cards.add(new Card("Живопись", "Часть 2", R.drawable.zivopis));
        cards.add(new Card("Портрет", "Часть 1", R.drawable.emma));
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(cards);
        rv.setAdapter(adapter);
    }

    public void readJson()
    {
        try {
            //Card card = JsonParser.readCompanyJSONFile(this);

//            Toast.makeText(this, "Данные восстановлены" + card.year, Toast.LENGTH_LONG).show();
//            cards.add(card);
//            initializeAdapter();


//            Response<List<Card>> response = serverApi.getCards().execute();
//            Snackbar.make(rv, response.code(), Snackbar.LENGTH_LONG).show();
//            newCards = response.body();

            final Call<List<Card>> newCars = serverApi.getCards();
            newCars.enqueue(new Callback<List<Card>>() {
                @Override
                public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                    Snackbar.make(rv, "Successful", Snackbar.LENGTH_LONG).show();
                    cards = response.body();
                    initializeAdapter();
                }

                @Override
                public void onFailure(Call<List<Card>> call, Throwable t) {
                    Snackbar.make(rv, "Failed " + t, Snackbar.LENGTH_LONG).show();
                }
            });
            Snackbar.make(rv, "Successful", Snackbar.LENGTH_LONG).show();
        } catch(java.lang.Throwable e)  {
            Toast.makeText(this, "Не удалось открыть данные", Toast.LENGTH_LONG).show();
        }

    }

    void connectToServer() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://shrouded-garden-88616.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serverApi = retrofit.create(ServerApi.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
