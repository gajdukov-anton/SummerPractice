package com.example.user.myapplication.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.user.myapplication.Objects.Card;
import com.example.user.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private List<Card> cards;
    private List<Card> newCards;
    private RecyclerView rv;
    private RecyclerView.LayoutManager lm;
    static private Retrofit retrofit;
    private RVAdapter adapter;
    static public ServerApi serverApi;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mayAddBooks = true;
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
        setSwipeRefreshLayout();
        initializeData();
        showAvailable();
        updateData();
    }

    private void setSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateCards();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.blue_swipe, R.color.green_swipe,
                R.color.orange_swipe, R.color.red_swipe);
    }

    private void updateData () {
        Button button = (Button)findViewById(R.id.update_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCards();
            }
        });
    }

    private void showAvailable() {

        Button button = (Button)findViewById(R.id.show_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCards(cards);
                mayAddBooks = false;
                if (isOnline()) {
                    final Call<List<Card>> newCars = serverApi.getAvailable(true);
                    newCars.enqueue(new Callback<List<Card>>() {
                        @Override
                        public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    cards = response.body();
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
        });
    }

    void addBook() {
        Intent intent = new Intent(this, AddBookActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mayAddBooks = false;
        clearCards(cards);
        if (isOnline()) {
            final Call<List<Card>> newCars = serverApi.searchBook(query);
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
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    void showSnackbar(int position) {
        Snackbar.make(rv, "Карточка с номером " + position + "numPage: " + numPage, Snackbar.LENGTH_LONG).show();
    }

    void openCard(int position) {
        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra("card", cards.get(position));
        startActivity(intent);
    }

    private void initializeData() {
        cards = new ArrayList<>();
        mSwipeRefreshLayout.setRefreshing(true);
        updateCards();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void clearCards(List<Card> list) {
        if (list.size() != 0) {
            list.clear();
        }
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
                if (mayAddBooks) {
                    loadPage();
                }
            }
        });
    }

    private void updateCards() {
        clearCards(cards);
        mayAddBooks = true;
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
                            Toast.makeText(MainActivity.this, response.message(), Snackbar.LENGTH_LONG).show();
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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
