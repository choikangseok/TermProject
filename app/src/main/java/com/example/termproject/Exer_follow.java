package com.example.termproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Exer_follow extends AppCompatActivity {
    WebView wevview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exer_follow);

        wevview = (WebView)findViewById(R.id.web);

        wevview.setWebViewClient(new WebViewClient());
        WebSettings set = wevview.getSettings();
        set.setJavaScriptEnabled(true);

        wevview.loadUrl("http://serviceapi.nmv.naver.com/flash/convertIframeTag.nhn?vid=CD2685BC868FCD64C4961F95F1DECF913074&outKey=V12610b0005a8739599107e788e22a210d8b9f6e9aa9b3eec31a37e788e22a210d8b9&width=544&height=306");

    }

    @Override
    protected void onStop() {
        super.onStop();
        wevview.destroy();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wevview.destroy();
        finish();
    }
}
