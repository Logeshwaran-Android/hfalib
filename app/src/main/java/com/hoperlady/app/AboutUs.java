package com.hoperlady.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperlady.R;
import com.hoperlady.hockeyapp.ActivityHockeyApp;

import core.service.ServiceConstant;
import core.socket.SocketHandler;


/**
 * Casperon Technology on 10/1-2/2015.
 */
public class AboutUs extends ActivityHockeyApp {
    String web_url = "";
    private RelativeLayout back;
    private TextView Tv_more_info;
    private SocketHandler socketHandler;
    private WebView myWebView;
    private ProgressBar progressBar;
    private TextView aboutus_versioncode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus);
        socketHandler = SocketHandler.getInstance(this);
        back = (RelativeLayout) findViewById(R.id.aboutus_header_back_layout);
        Tv_more_info = (TextView) findViewById(R.id.more_info_baseurl);
        progressBar = (ProgressBar) findViewById(R.id.webView_progressbar);
        aboutus_versioncode = (TextView) findViewById(R.id.aboutus_versioncode);
        myWebView = (WebView) findViewById(R.id.WebView);
        final WebSettings webSettings = myWebView.getSettings();
        Resources res = getResources();
        int fontSize = (int) res.getDimension(R.dimen.txtSize);
        webSettings.setDefaultFontSize((int) fontSize);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            aboutus_versioncode.setText(getResources().getString(R.string.navigation_menu_version_txt) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        web_url = ServiceConstant.Aboutus_Url;
        myWebView.loadUrl(web_url);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                progressBar.setProgress(progress);

                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            onBackPressed();
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);
            return true;
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
