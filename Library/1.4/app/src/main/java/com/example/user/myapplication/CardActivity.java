package com.example.user.myapplication;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class CardActivity extends AppCompatActivity {

    private Card card;
   // private WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        card = getIntent().getParcelableExtra("card");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        buildCard();
//        mWebView = (WebView) findViewById(R.id.webView);
//        // включаем поддержку JavaScript
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.setWebViewClient(new MyWebViewClient());
//        // указываем страницу загрузки
//        //mWebView.loadUrl("http://developer.alexanderklimov.ru/android");
        openBrowser();
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

    void loadActivity() {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("cardLink", card.link);
        startActivity(intent);//mWebView.loadUrl(card.link);
    }

//    private  class MyWebViewClient extends WebViewClient
//    {
//        @SuppressWarnings("deprecation") @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return true;
//        }
//
//        @TargetApi(Build.VERSION_CODES.N) @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            view.loadUrl(request.getUrl().toString());
//            return true;
//        }
//
//        @Override public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//            Toast.makeText(getApplicationContext(), "Страница загружена!", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            super.onPageStarted(view, url, favicon);
//            Toast.makeText(getApplicationContext(), "Начата загрузка страницы", Toast.LENGTH_SHORT)
//                    .show();
//        }
//    };

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
