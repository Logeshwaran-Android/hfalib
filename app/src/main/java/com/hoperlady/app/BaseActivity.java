package com.hoperlady.app;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

import com.hoperlady.R;
import com.hoperlady.hockeyapp.ActionBarActivityHockeyApp;

import androidx.annotation.Nullable;
import core.service.ServiceConstant;


/**
 */
public class BaseActivity extends ActionBarActivityHockeyApp implements ServiceConstant {
    private Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showDialog(String message) {
        dialog = new Dialog(this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void dismissDialog() {
        try {
            if (dialog != null)
                dialog.dismiss();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

    }
}
