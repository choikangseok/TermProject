package com.example.termproject;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.VideoView;

public class Movie extends AppCompatActivity {
    WebView wevview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        wevview = (WebView)findViewById(R.id.web);

        wevview.setWebViewClient(new WebViewClient());
        WebSettings set = wevview.getSettings();
        set.setJavaScriptEnabled(true);

        wevview.loadUrl("http://serviceapi.nmv.naver.com/flash/convertIframeTag.nhn?vid=C3B788B64CB12908F19A79F3FF3AE01A02EC&outKey=V121018c6b92388b85ccdded0b06bbc892e14ded5c115bbf617b7ded0b06bbc892e14&width=544&height=306");

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
