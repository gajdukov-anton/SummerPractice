package com.example.user.myapplication;

import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class CardActivity extends AppCompatActivity {

    private Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
       // Bundle arguments = getIntent().getExtras();
        card = getIntent().getParcelableExtra("card");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        buildCard();
    }

    void buildCard() {
        TextView textParam;
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
