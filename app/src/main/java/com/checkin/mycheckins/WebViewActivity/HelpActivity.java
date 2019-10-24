package com.checkin.mycheckins.WebViewActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import com.checkin.mycheckins.R;

public class HelpActivity extends AppCompatActivity implements HelpInterface.View {

    private WebView webView;
    private HelpPresenter helpPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeView();

        helpPresenter.requestWebpage();

    }

    private void initializeView() {

        helpPresenter = new HelpPresenter(this);

        webView = findViewById(R.id.helpWebView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void showWebPage(String url) {
        webView.loadUrl(url);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
