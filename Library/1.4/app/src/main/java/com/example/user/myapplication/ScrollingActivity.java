package com.example.user.myapplication;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScrollingActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private List<Card> cards;
    private List<Card> newCards;
    private RecyclerView rv;
    private RecyclerView.LayoutManager lm;
    static private Retrofit retrofit;
    private RVAdapter adapter;
    static public ServerApi serverApi;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int numPage = 2;

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
                addBook();
            }
        });

        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        connectToServer();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.blue_swipe, R.color.green_swipe,
                R.color.orange_swipe, R.color.red_swipe);

        initializeData();
        // update();
    }

    void addBook() {
        Intent intent = new Intent(this, AddBookActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateCards();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    void showSnackbar(int position) {
        Snackbar.make(rv, "Карточка с номером " + position, Snackbar.LENGTH_LONG).show();
    }

    void showToast() {
        Toast.makeText(ScrollingActivity.this, "Книга уже взята", Snackbar.LENGTH_LONG).show();
    }

    void openCard(int position) {
        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra("card", cards.get(position));
        startActivity(intent);
    }

    private void initializeData() {
        cards = new ArrayList<>();
        mSwipeRefreshLayout.setRefreshing(true);
//        loadBooks(1);
//        initializeAdapter();

        updateCards();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void initializeAdapter() {
        adapter = new RVAdapter(cards);
        rv.setAdapter(adapter);
        update();
    }

    private void update() {
        adapter.setOnLoadMoreListener(new RVAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
//                List<Book> newBooks = //допустим книги и допустим вы их подгрузили
//                        cards.addAll(newBooks); //totalBooks это коллекция книг которая содержит все данные для списка

                // adapter.notifyDataSetChanged();
//                cards.add(new Card("Book", "book", R.drawable.emma));
//                adapter.addCards(cards);
//                rv.setAdapter(adapter);
//                adapter.endLoading(); //когда загрузка завершена
//                adapter.setNoMore(true); //если подгружать больше нечего
                //Toast.makeText(ScrollingActivity.this, "Книга уже взята", Snackbar.LENGTH_LONG).show();
                //adapter.endLoading(); //когда загрузка завершена
                //adapter.setNoMore(true); //если подгружать больше нечего
                loadPage();
              //  adapter.endLoading();
              //  adapter.setNoMore(true);
            }
        });
       // adapter.setNoMore(false);

    }

    private void updateCards() {

        if (isOnline()) {
            final Call<List<Card>> newCars = serverApi.getCards(1);
            newCars.enqueue(new Callback<List<Card>>() {

                @Override
                public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            cards = response.body();
                            numPage = 2;
                            initializeAdapter();
                        }
                    } else {
                        Snackbar.make(rv, "Impossible to connect to server", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Card>> call, Throwable t) {
                    Snackbar.make(rv, "Failed " + t, Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            Snackbar.make(rv, "Отсутсвует подключение к интернету", Snackbar.LENGTH_LONG).show();
        }
    }

    public void loadPage() {

        if (isOnline()) {
            Call<List<Card>> newCars = serverApi.getCards(numPage);
            newCars.enqueue(new Callback<List<Card>>() {
                @Override
                public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                    if (response.isSuccessful()) {
                        if (response.body().size() != 0) {
                            int pos = cards.size();
                            cards.addAll(response.body());
                            adapter.addCards(cards);
                            rv.setAdapter(adapter);
                            rv.scrollToPosition(pos - 4);
                            Toast.makeText(ScrollingActivity.this, response.message(), Snackbar.LENGTH_LONG).show();
                            numPage++;
                        }

                    } else {
                        Snackbar.make(rv, "Impossible to connect to server", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Card>> call, Throwable t) {
                    Snackbar.make(rv, "Failed " + t, Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            Snackbar.make(rv, "Отсутсвует подключение к интернету", Snackbar.LENGTH_LONG).show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    static void connectToServer() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://libraryomega.herokuapp.com")
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
