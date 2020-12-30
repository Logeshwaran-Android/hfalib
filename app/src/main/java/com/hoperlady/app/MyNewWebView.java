package com.hoperlady.app;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperlady.R;
import com.hoperlady.hockeyapp.ActivityHockeyApp;

/**
 * Created by CAS63 on 7/10/2018.
 */

public class MyNewWebView extends ActivityHockeyApp {

    private WebView webView;
    private WebSettings webSettings;
    private RelativeLayout back;
    private ProgressBar progressBar;

    private TextView titleTV;

    String sTitle = "", sURL = "";

    //String url="http://www.serviceyard.com.au/uploads/images/badges/1527081966528.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_webview);


        sTitle = getIntent().getStringExtra("title");
        sURL = getIntent().getStringExtra("url");

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();

//        String data="<html><head><title>Example</title><meta name=\"viewport\"\"content=\"width="+width+", initial-scale=0.65 \" /></head>";
//        data=data+"<body><center><img width=\""+width+"\" src=\""+url+"\" /></center></body></html>";

        webView = (WebView) findViewById(R.id.activity_my_main_webView);
        back = (RelativeLayout) findViewById(R.id.activity_my_webview_layout_header);
        progressBar = (ProgressBar) findViewById(R.id.activity_my_webView_progressbar);
        titleTV = (TextView) findViewById(R.id.titleTV);

        titleTV.setText(sTitle);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportZoom(true);


        webView.requestFocus(View.FOCUS_DOWN);
        webView.loadUrl(sURL);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                progressBar.setProgress(progress);

                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                    //   finish();
                }
            }
        });


    }

}
