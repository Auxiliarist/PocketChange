package com.simplebytes.pocketchange.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.simplebytes.pocketchange.R;

public class PrivacyActivity extends BaseActivity {

    public WebView webview;
    private String url = "https://docs.google.com/document/d/11EsAGauFzR7YB0G2cpSz5F0eDJUVB9Mik1hvIwDJO0c/edit?usp=sharing";

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_privacy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Privacy Policy");

        webview = (WebView) findViewById(R.id.privacyWebView);
        webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        //webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webview.setWebViewClient(new WebViewHelper());

        webview.loadUrl(url);

    }

    private class WebViewHelper extends WebViewClient
    {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
