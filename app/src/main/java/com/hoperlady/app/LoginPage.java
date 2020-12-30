package com.hoperlady.app;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.hoperlady.FCM.Config;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.CurrencySymbolConverter;
import com.hoperlady.Utils.HideSoftKeyboard;
import com.hoperlady.Utils.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;

import core.Dialog.PkDialog;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;
import core.socket.ChatMessageService;
import core.socket.SocketHandler;

/**
 * Created by user88 on 11/28/2015.
 */
public class LoginPage extends BaseActivity {

    public static String Appmail = "";
    RelativeLayout Rl_layout_login_back_img, Rl_layout_no_account_signup, Rl_layout_forgotpassword;
    EditText Et_login_email, Et_login_password;
    Button Bt_login;
    StringRequest postrequest;
    TextView signup;
    String TAG = "LoginPage";
    private String evalue;
    private Dialog dialog;
    private SocketHandler socketHandler;
    private String sCurrencySymbol = "";
    private String Gcm_Id;
    private SessionManager session;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private Receiver receive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);
        initilize();
        //Hide the keyboard of click the outside
        HideSoftKeyboard.setupUI(
                LoginPage.this.getWindow().getDecorView(),
                LoginPage.this);
        Rl_layout_login_back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        Bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Et_login_email.getText().toString().length() == 0) {
                    erroredit(Et_login_email, getResources().getString(R.string.mainpage_logineithemail_validate_lable));
                } else if (Et_login_password.getText().toString().length() == 0) {
                    erroredit(Et_login_password, getResources().getString(R.string.mainpage_login_password_validate_lable));
                } else {
                    login();
                }
            }
        });

        Rl_layout_forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, ForgotPassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, RegisterPageFirst.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


    }

    public void initilize() {
        cd = new ConnectionDetector(LoginPage.this);
        session = new SessionManager(LoginPage.this);
        socketHandler = SocketHandler.getInstance(LoginPage.this);

        Rl_layout_login_back_img = (RelativeLayout) findViewById(R.id.logipage_backimg_label);
        Rl_layout_no_account_signup = (RelativeLayout) findViewById(R.id.loginpage_no_account_label);
        Et_login_email = (EditText) findViewById(R.id.loginpage_email_edittext_label);
        Et_login_password = (EditText) findViewById(R.id.loginpage_password_edittext_label);
        Bt_login = (Button) findViewById(R.id.logipage_login_label);
        Rl_layout_forgotpassword = (RelativeLayout) findViewById(R.id.loginpage_forgt_password);

        signup = (TextView) findViewById(R.id.sign);

        Et_login_email.setImeOptions(EditorInfo.IME_ACTION_DONE);
        Et_login_password.setImeOptions(EditorInfo.IME_ACTION_DONE);


        if (isInternetPresent) {
            Appinfo(LoginPage.this, ServiceConstant.App_Info);
        }


        receive = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.app.device.back.button.pressed");
        registerReceiver(receive, intentFilter);
    }

    public void login() {

        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

            SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
            Gcm_Id = pref.getString("regId", null);
            LoginRequest(LoginPage.this, LOGIN_URL);

        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {
        final PkDialog mDialog = new PkDialog(LoginPage.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(
                getResources().getString(R.string.action_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                }
        );

        mDialog.show();
    }

    // --------------------Code to set error for EditText-----------------------
    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(LoginPage.this,
                R.anim.shake);
        editname.startAnimation(shake);

        ForegroundColorSpan fgcspan = new ForegroundColorSpan(
                Color.parseColor("#cc0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }

    private void LoginRequest(Context mContext, String url) {

        dialog = new Dialog(LoginPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.logging_in));


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email", Et_login_email.getText().toString());
        jsonParams.put("password", Et_login_password.getText().toString());
        jsonParams.put("gcm_id", Gcm_Id);

        System.out.println("LoginRequest jsonParams" + jsonParams);

        ServiceRequest mservicerequest = new ServiceRequest(mContext);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {

                System.out.println("LoginRequest response" + response);

                String Str_status = "", Str_response = "", Str_providerid = "", Str_socky_id = "",
                        Str_provider_name = "", Str_provider_email = "", Str_provider_img = "", Str_key = "",
                        verified_status = "";


                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_status = jobject.getString("status");

                    verified_status = jobject.getString("verified_status");
                    session.setTaskerVerification(verified_status);

                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject object = jobject.getJSONObject("response");
                        Str_providerid = object.getString("provider_id");
                        //Str_socky_id = object.getString("soc_key");
                        Str_provider_name = object.getString("provider_name");
                        Str_provider_email = object.getString("email");
                        Str_provider_img = object.getString("provider_image");
                        // Str_key = object.getString("key");


                        String ScurrencyCode = object.getString("currency");

                        sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);
                        session.createWalletSession(ScurrencyCode);

                    } else {
                        Str_response = jobject.getString("response");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

                if (Str_status.equalsIgnoreCase("1")) {
                    session.createLoginSession(Str_provider_email, Str_providerid, Str_provider_name, Str_provider_img, Gcm_Id);
                    socketHandler.getSocketManager().connect();
                    if (!ChatMessageService.isStarted()) {
                        Intent intent = new Intent(LoginPage.this, ChatMessageService.class);
                        startService(intent);
                    }
                    Intent intent = new Intent(LoginPage.this, NavigationDrawer.class);
                    startActivity(intent);
                    finish();
                } else if (Str_status.equalsIgnoreCase("3")) {
                    // dialog.dismiss();
                    login();
                } else {
                    final PkDialog mdialog = new PkDialog(LoginPage.this);
                    mdialog.setDialogTitle(getResources().getString(R.string.server_lable_header));
                    mdialog.setDialogMessage(Str_response);
                    mdialog.setCancelOnTouchOutside(false);
                    mdialog.setPositiveButton(
                            getResources().getString(R.string.server_ok_lable_header), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mdialog.dismiss();
                                }
                            }
                    );
                    mdialog.show();
                }


            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });


    }

    private void Appinfo(Context mContext, String url) {


        ServiceRequest mservicerequest = new ServiceRequest(mContext);

        mservicerequest.makeServiceRequest(url, Request.Method.POST, null, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                Log.e("loginresponse", response);

                System.out.println("response---------" + response);
                String Str_status = "", Str_response = "", Str_providerid = "", Str_socky_id = "", Str_provider_name = "", Str_provider_email = "", Str_provider_img = "", Str_key = "";

                String email = "", admincomsn = "", miniamt = "", servicetax = "";

                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_status = jobject.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {

                        Appmail = jobject.getString("email_address");

                        admincomsn = jobject.getString("admin_commission");
                        miniamt = jobject.getString("minimum_amount");
                        servicetax = jobject.getString("service_tax");
                        session.Setemailappinfo(Appmail);

                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (Str_status.equalsIgnoreCase("1")) {

                }

            }

            @Override
            public void onErrorListener() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (receive != null) {
            unregisterReceiver(receive);
        }
        super.onDestroy();
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("com.app.device.back.button.pressed")) {
                finish();
            }
        }
    }
}