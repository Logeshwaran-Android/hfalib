package com.hoperlady.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.HideSoftKeyboard;
import com.hoperlady.Utils.Pinview;
import com.hoperlady.Utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import core.Dialog.PkDialog;
import core.Volley.ServiceRequest;
import core.gcm.GCMIntializer;
import core.service.ServiceConstant;
import core.socket.ChatMessageService;
import core.socket.SocketHandler;

public class NewLoginOTPActivity extends AppCompatActivity {

    private TextView titleOTPDigitTV, focuse_Tv, resendOTP, editMobileNumberTV;
    private EditText editTextOTP1, editTextOTP2, editTextOTP3, editTextOTP4, editTextOTP5, editTextOTP6;
    private ImageView headerSubmitIv, headerBackIv;
    private ProgressBar progressBar;
    private TextView otpSentSuccessTv;

    String otp = "", otp_status = "", country_code = "", phone_number = "", user_type = "",
            date_format = "", radius = "", sSitemode = "";

    private SessionManager session;
    private SocketHandler socketHandler;
    private String GCM_ID = "";
    ConnectionDetector cd;

    private boolean isBackOnClick = false;

    private Pinview otpViewET;

    private SessionManagerRegistration sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login_otp);
        HideSoftKeyboard.setupUI(
                NewLoginOTPActivity.this.getWindow().getDecorView(),
                NewLoginOTPActivity.this);

        initGetIntent();
        initiallization();
        initiOnKeyEvent();
    }

    private void initGetIntent() {
        Intent gIntent = getIntent();
        otp = gIntent.getStringExtra("otp");
        otp_status = gIntent.getStringExtra("otp_status");
        country_code = gIntent.getStringExtra("country_code");
        phone_number = gIntent.getStringExtra("phone_number");
        user_type = gIntent.getStringExtra("user_type");
        sSitemode = gIntent.getStringExtra("sitemode");
        date_format = gIntent.getStringExtra("date_format");
        radius = gIntent.getStringExtra("radius");
    }

    private void initiallization() {
        titleOTPDigitTV = (TextView) findViewById(R.id.titleOTPDigitTV);
        focuse_Tv = (TextView) findViewById(R.id.focuse_Tv);
        headerSubmitIv = (ImageView) findViewById(R.id.headerSubmitIv);
        headerBackIv = (ImageView) findViewById(R.id.headerBackIv);
        progressBar = (ProgressBar) findViewById(R.id.progressBarRunning);
        resendOTP = (TextView) findViewById(R.id.resendOTP);
        editMobileNumberTV = (TextView) findViewById(R.id.editMobileNumberTV);
        cd = new ConnectionDetector(NewLoginOTPActivity.this);
        session = new SessionManager(NewLoginOTPActivity.this);
        otpSentSuccessTv = (TextView) findViewById(R.id.otpSentSuccessTv);
        sessionManager = new SessionManagerRegistration(NewLoginOTPActivity.this);
        otpViewET = (Pinview) findViewById(R.id.otpViewET);
        socketHandler = SocketHandler.getInstance(NewLoginOTPActivity.this);

        editTextOTP1 = (EditText) findViewById(R.id.editTextOTP1);
        editTextOTP2 = (EditText) findViewById(R.id.editTextOTP2);
        editTextOTP3 = (EditText) findViewById(R.id.editTextOTP3);
        editTextOTP4 = (EditText) findViewById(R.id.editTextOTP4);
        editTextOTP5 = (EditText) findViewById(R.id.editTextOTP5);
        editTextOTP6 = (EditText) findViewById(R.id.editTextOTP6);

        otpViewET.setInputType(Pinview.InputType.TYPE_CLASS_NUMBER);
        otpViewET.setPassword(false);
        otpViewET.setCursorColor(getResources().getColor(R.color.black_color));
        otpViewET.showCursor(true);
        otpViewET.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                String sOTPChecker = pinview.getValue();
                if (sOTPChecker.equals("")) {
                    focuse_Tv.setText(getResources().getString(R.string.newloginotp_activity_otp_empty_alert));
                    focuse_Tv.setVisibility(View.VISIBLE);
                } else if (!sOTPChecker.equals(otp)) {
                   // otpViewET.setValue("");
                    focuse_Tv.setText(getResources().getString(R.string.newloginotp_activity_otp_invalid_alert));
                    focuse_Tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onDataEntering(Pinview pinview, boolean fromUser) {
                focuse_Tv.setVisibility(View.GONE);
            }
        });

        if (otp_status.equals("development")) {
            otpViewET.setValue(otp);
        }

        String sourceString = getResources().getString(R.string.newloginotp_activity_title_txt1) + "<b>"
                + " " + country_code + " " + phone_number + " " + "</b>. ";
        titleOTPDigitTV.setText(Html.fromHtml(sourceString));

        headerSubmitIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sOTPChecker = otpViewET.getValue();

                if (sOTPChecker.equals("")) {
                    focuse_Tv.setText(getResources().getString(R.string.newloginotp_activity_otp_empty_alert));
                    focuse_Tv.setVisibility(View.VISIBLE);
                } else if (!sOTPChecker.equals(otp)) {
                    //otpViewET.setValue("");
                    focuse_Tv.setText(getResources().getString(R.string.newloginotp_activity_otp_invalid_alert));
                    focuse_Tv.setVisibility(View.VISIBLE);
                } else {
                    if (user_type.equals("new")) {
                        sessionManager.clearSessionRegistrationData();
                        Intent intent = new Intent(NewLoginOTPActivity.this, RegisterPageFirst.class);
                        intent.putExtra("country_code", country_code);
                        intent.putExtra("phone_number", phone_number);
                        intent.putExtra("date_format", date_format);
                        intent.putExtra("sitemode", sSitemode);
                        intent.putExtra("radius", radius);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else {

                        //---------Getting GCM Id----------
                        GCMIntializer initializer = new GCMIntializer(NewLoginOTPActivity.this, new GCMIntializer.CallBack() {
                            @Override
                            public void onRegisterComplete(String registrationId) {

                                GCM_ID = registrationId;
                                postRequest_OTP(ServiceConstant.LOGIN_URL);
                            }

                            @Override
                            public void onError(String errorMsg) {
                                GCM_ID="";
                                postRequest_OTP(ServiceConstant.LOGIN_URL);
                            }
                        });
                        initializer.init();

                       /* SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                        GCM_ID = pref.getString("regId", null);
                        postRequest_OTP(ServiceConstant.LOGIN_URL);*/
                    }
                }
            }
        });

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    postRequest_MobileNumber(ServiceConstant.HOME_MOBILE_NUMBER_URL);
                } else {
                    Toast.makeText(NewLoginOTPActivity.this, getResources().getString(R.string.no_inetnet_label), Toast.LENGTH_SHORT).show();
                }
            }
        });
        editMobileNumberTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        headerBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initiOnKeyEvent() {

        editTextOTP1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                isBackOnClick = false;
                if ((keyCode == KeyEvent.KEYCODE_DEL)) {
                    editTextOTP1.setText("");
                    editTextOTP2.setText("");
                    editTextOTP3.setText("");
                    editTextOTP4.setText("");
                    editTextOTP5.setText("");
                    editTextOTP6.setText("");
                    isBackOnClick = false;
                    return true;
                }
                return false;
            }
        });

        editTextOTP2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                isBackOnClick = false;
                if ((keyCode == KeyEvent.KEYCODE_DEL)) {
                    isBackOnClick = true;
                    if (editTextOTP2.getText().toString().equals("")) {
                        editTextOTP1.setText("");
                        editTextOTP1.requestFocus();
                        isBackOnClick = false;
                    }
                    editTextOTP2.setText("");
                    editTextOTP3.setText("");
                    editTextOTP4.setText("");
                    editTextOTP5.setText("");
                    editTextOTP6.setText("");
                    return true;
                }
                return false;
            }
        });

        editTextOTP3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                isBackOnClick = false;
                if ((keyCode == KeyEvent.KEYCODE_DEL)) {
                    isBackOnClick = true;
                    if (editTextOTP3.getText().toString().equals("")) {
                        editTextOTP2.setText("");
                        editTextOTP2.requestFocus();
                        isBackOnClick = false;
                    }
                    editTextOTP3.setText("");
                    editTextOTP4.setText("");
                    editTextOTP5.setText("");
                    editTextOTP6.setText("");
                    return true;
                }
                return false;
            }
        });

        editTextOTP4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                isBackOnClick = false;
                if ((keyCode == KeyEvent.KEYCODE_DEL)) {
                    isBackOnClick = true;
                    if (editTextOTP4.getText().toString().equals("")) {
                        editTextOTP3.setText("");
                        editTextOTP3.requestFocus();
                        isBackOnClick = false;
                    }
                    editTextOTP4.setText("");
                    editTextOTP5.setText("");
                    editTextOTP6.setText("");

                    return true;
                }
                return false;
            }
        });

        editTextOTP5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                isBackOnClick = false;
                if ((keyCode == KeyEvent.KEYCODE_DEL)) {
                    isBackOnClick = true;
                    if (editTextOTP5.getText().toString().equals("")) {
                        editTextOTP4.setText("");
                        editTextOTP4.requestFocus();
                        isBackOnClick = false;
                    }
                    editTextOTP5.setText("");
                    editTextOTP6.setText("");
                    return true;
                }
                return false;
            }
        });

        editTextOTP6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                isBackOnClick = false;
                if ((keyCode == KeyEvent.KEYCODE_DEL)) {
                    isBackOnClick = true;
                    if (editTextOTP6.getText().toString().equals("")) {
                        editTextOTP5.setText("");
                        editTextOTP5.requestFocus();
                        isBackOnClick = false;
                    }

                    editTextOTP6.setText("");
                    return true;
                }
                return false;
            }
        });

    }

    TextWatcher otpTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (editTextOTP1.getText().toString().length() == 0 && editTextOTP2.getText().toString().length() == 0 &&
                    editTextOTP3.getText().toString().length() == 0 && editTextOTP4.getText().toString().length() == 0 &&
                    editTextOTP5.getText().toString().length() == 0 && editTextOTP6.getText().toString().length() == 0) {
                isBackOnClick = false;
            }

            if (!isBackOnClick) {
                focuse_Tv.setVisibility(View.GONE);

                if (editTextOTP1.getText().toString().length() == 1) {
                    editTextOTP2.requestFocus();
//                    editTextOTP2.setBackgroundResource(R.drawable.edittext_otp_highlater_bg);
                }
                if (editTextOTP2.getText().toString().length() == 1) {
                    editTextOTP3.requestFocus();
//                    editTextOTP3.setBackgroundResource(R.drawable.edittext_otp_highlater_bg);
                }
                if (editTextOTP3.getText().toString().length() == 1) {
                    editTextOTP4.requestFocus();
//                    editTextOTP4.setBackgroundResource(R.drawable.edittext_otp_highlater_bg);
                }
                if (editTextOTP4.getText().toString().length() == 1) {
                    editTextOTP5.requestFocus();
//                    editTextOTP5.setBackgroundResource(R.drawable.edittext_otp_highlater_bg);
                }
                if (editTextOTP5.getText().toString().length() == 1) {
                    editTextOTP6.requestFocus();
//                    editTextOTP6.setBackgroundResource(R.drawable.edittext_otp_highlater_bg);
                }
                if (editTextOTP6.getText().toString().length() == 1) {
                    editTextOTP6.requestFocus();
//                    editTextOTP6.setBackgroundResource(R.drawable.edittext_otp_highlater_bg);
                }

                if (editTextOTP1.getText().toString().length() == 1 && editTextOTP2.getText().toString().length() == 1 &&
                        editTextOTP3.getText().toString().length() == 1 && editTextOTP4.getText().toString().length() == 1 &&
                        editTextOTP5.getText().toString().length() == 1 && editTextOTP6.getText().toString().length() == 1) {

                    String sOTPChecker = editTextOTP1.getText().toString() +
                            editTextOTP2.getText().toString() +
                            editTextOTP3.getText().toString() +
                            editTextOTP4.getText().toString() +
                            editTextOTP5.getText().toString() +
                            editTextOTP6.getText().toString();

                    if (sOTPChecker.equals("")) {
                        focuse_Tv.setText(getResources().getString(R.string.newloginotp_activity_otp_empty_alert));
                        focuse_Tv.setVisibility(View.VISIBLE);
                    } else if (!sOTPChecker.equals(otp)) {
                        String[] sSpliteOTP = otp.split("");
                        System.out.println("sSpliteOTP" + sSpliteOTP[1] + "," + sSpliteOTP[2] + "," + sSpliteOTP[3] + "," + sSpliteOTP[4] + "," + sSpliteOTP[5] + "," + sSpliteOTP[6]);
                        editTextOTP1.setText("");
                        editTextOTP2.setText("");
                        editTextOTP3.setText("");
                        editTextOTP4.setText("");
                        editTextOTP5.setText("");
                        editTextOTP6.setText("");
                        editTextOTP1.requestFocus();
                        focuse_Tv.setText(getResources().getString(R.string.newloginotp_activity_otp_invalid_alert));
                        focuse_Tv.setVisibility(View.VISIBLE);
                    } else {
//                    resendOTP.setEnabled(true);
//                    editMobileNumberTV.setEnabled(true);
//                    if (user_type.equals("new")) {
//                        sessionManager.clearSessionRegistrationData();
//                        Intent intent = new Intent(NewLoginOTPActivity.this, RegisterPageFirst.class);
//                        intent.putExtra("country_code", country_code);
//                        intent.putExtra("phone_number", phone_number);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                        finish();
//                    } else {
//                        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
//                        GCM_ID = pref.getString("regId", null);
//                        postRequest_OTP(ServiceConstant.LOGIN_URL);
//                    }
                    }
                }
            }
        }
    };

    private void postRequest_OTP(String url) {

        final Dialog dialog1 = new Dialog(NewLoginOTPActivity.this);
        dialog1.getWindow();
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.custom_loading);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        TextView dialog_title = (TextView) dialog1.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        if(GCM_ID==null){

            GCM_ID="";
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone[code]", country_code);
        params.put("phone[number]", phone_number);
        params.put("country_code", country_code);
        params.put("phone_number", phone_number);
        params.put("deviceToken", "");
        params.put("gcm_id", GCM_ID);

        ServiceRequest mRequest = new ServiceRequest(NewLoginOTPActivity.this);
        mRequest.makeServiceRequest(url, Request.Method.POST, params, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                headerSubmitIv.setEnabled(true);
                progressBar.setVisibility(View.GONE);

                System.out.println("LoginRequest response" + response);
                String Str_status = "", Str_response = "", Str_providerid = "", Str_socky_id = "",
                        Str_provider_name = "", Str_provider_email = "", Str_provider_img = "", Str_key = "";
                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_status = jobject.getString("status");

                    if (Str_status.equalsIgnoreCase("1") || Str_status.equalsIgnoreCase("3")) {
                        JSONObject object = jobject.getJSONObject("response");
                        Str_providerid = object.getString("provider_id");
                        Str_provider_name = object.getString("provider_name");
                        Str_provider_email = object.getString("email");
                        Str_provider_img = object.getString("provider_image");

                        String ScurrencyCode = object.getString("currency");

                        session.createWalletSession(ScurrencyCode);

                    } else {
                        if (jobject.has("response")) {
                            Str_response = jobject.getString("response");
                        }
                        if (jobject.has("message")) {
                            Str_response = jobject.getString("message");
                        }
                        if (jobject.has("error")) {
                            Str_response = jobject.getString("error");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Str_status.equalsIgnoreCase("1")) {
                    session.createLoginSession(Str_provider_email, Str_providerid, Str_provider_name, Str_provider_img, GCM_ID);
                    socketHandler.getSocketManager().connect();
                    if (!ChatMessageService.isStarted()) {
                        Intent intent = new Intent(NewLoginOTPActivity.this, ChatMessageService.class);
                        startService(intent);
                    }
                    Intent intent = new Intent(NewLoginOTPActivity.this, NavigationDrawer.class);
                    startActivity(intent);
                    finish();

                    Intent intent1 = new Intent();
                    intent1.setAction("com.newlogin.finish");
                    sendBroadcast(intent1);


                }
//                else if (Str_status.equalsIgnoreCase("3")) {
//                    login();
//                }
                else {
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(500);
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);

                    otpSentSuccessTv.setVisibility(View.VISIBLE);
                    otpSentSuccessTv.startAnimation(anim);
                    otpSentSuccessTv.setTextColor(Color.parseColor("#e34b30"));
                    otpSentSuccessTv.setText(Str_response);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            otpSentSuccessTv.setVisibility(View.GONE);
                            otpSentSuccessTv.clearAnimation();
                        }
                    }, 2000);
                }

                dialog1.dismiss();
            }

            @Override
            public void onErrorListener() {

                dialog1.dismiss();

            }
        });

    }

    private void postRequest_MobileNumber(String url) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone[code]", country_code);
        params.put("phone[number]", phone_number);
        params.put("country_code", country_code);
        params.put("phone_number", phone_number);

        ServiceRequest mRequest = new ServiceRequest(NewLoginOTPActivity.this);
        mRequest.makeServiceRequest(url, Request.Method.POST, params, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                headerSubmitIv.setEnabled(true);
                progressBar.setVisibility(View.GONE);

                String sStatus = "", sMessage = "", sOTP = "", sOTP_status = "", sUser_type = "";
                try {
                    JSONObject Jresponse = new JSONObject(response);
                    sStatus = Jresponse.getString("status");
                    if (sStatus.equals("1")) {
                        otp = Jresponse.getString("otp");
                        sOTP_status = Jresponse.getString("otp_status");
                        user_type = Jresponse.getString("user_type");

                        if (Jresponse.has("sitemode")) {
                            sSitemode = Jresponse.getString("sitemode");
                        }

                        otpViewET.setValue("");

                        if (sOTP_status.equals("development")) {
                            otpViewET.setValue(otp);
                        }

                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(500);
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);

                        otpSentSuccessTv.setVisibility(View.VISIBLE);
                        otpSentSuccessTv.startAnimation(anim);
                        otpSentSuccessTv.setTextColor(Color.parseColor("#0a9c19"));
                        otpSentSuccessTv.setText(getResources().getString(R.string.newloginotp_activity_resend_alert));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                otpSentSuccessTv.setVisibility(View.GONE);
                                otpSentSuccessTv.clearAnimation();
                            }
                        }, 2000);

                    } else {
                        sMessage = Jresponse.getString("errors");

                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(500);
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);

                        otpSentSuccessTv.setVisibility(View.VISIBLE);
                        otpSentSuccessTv.startAnimation(anim);
                        otpSentSuccessTv.setTextColor(Color.parseColor("#e34b30"));
                        otpSentSuccessTv.setText(sMessage);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                otpSentSuccessTv.setVisibility(View.GONE);
                                otpSentSuccessTv.clearAnimation();
                            }
                        }, 2000);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorListener() {

            }
        });

    }

    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(NewLoginOTPActivity.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
}
