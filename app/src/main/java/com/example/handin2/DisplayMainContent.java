package com.example.handin2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.webkit.WebView;
import android.widget.Toast;

public class DisplayMainContent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = getIntent();
        String content = intent.getStringExtra("Content");

        WebView webView = findViewById(R.id.webViewContent);

        webView.loadData(content, "text/html", null);

    }
}
