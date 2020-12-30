package com.hoperlady.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.countrycodepicker.CountryPicker;
import com.countrycodepicker.CountryPickerListener;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.CountryDialCode;
import com.hoperlady.Utils.HideSoftKeyboard;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import core.Dialog.PkDialog;
import core.Map.GPSTracker;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;

public class NewLoginHomePageSecondActivity extends AppCompatActivity {

    ImageView flagImage_IV;
    TextView countryCodeTV;
    EditText mobileNumberET;
    TextView alertTV;
    private GPSTracker gpsTracker;

    CountryPicker picker;

    ImageView headerSubmitIv, headerBackIv;
    ProgressBar progressBar;
    ConnectionDetector cd;

    public class HindenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.newlogin.finish")) {
                finish();
            }
        }
    }

    HindenReceiver hindenReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login_home_page_second);
        HideSoftKeyboard.setupUI(
                NewLoginHomePageSecondActivity.this.getWindow().getDecorView(),
                NewLoginHomePageSecondActivity.this);

        initialization();
        initOnClick();

    }

    private void initialization() {
        flagImage_IV = (ImageView) findViewById(R.id.flagImage_IV);
        countryCodeTV = (TextView) findViewById(R.id.countryCodeTV);
        mobileNumberET = (EditText) findViewById(R.id.mobileNumberET);
        headerSubmitIv = (ImageView) findViewById(R.id.headerSubmitIv);
        progressBar = (ProgressBar) findViewById(R.id.progressBarRunning);
        headerBackIv = (ImageView) findViewById(R.id.headerBackIv);
        alertTV = (TextView) findViewById(R.id.alertTV);

        cd = new ConnectionDetector(NewLoginHomePageSecondActivity.this);

        countryCodeTV.addTextChangedListener(mobileNumberTextWatcher);
        mobileNumberET.addTextChangedListener(mobileNumberTextWatcher);

        picker = CountryPicker.newInstance("Select Country");

        gpsTracker = new GPSTracker(NewLoginHomePageSecondActivity.this);
        if (gpsTracker.canGetLocation() && gpsTracker.isgpsenabled()) {

            double MyCurrent_lat = gpsTracker.getLatitude();
            double MyCurrent_long = gpsTracker.getLongitude();

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(MyCurrent_lat, MyCurrent_long, 1);
                if (addresses != null && !addresses.isEmpty()) {

                    String Str_getCountryCode = addresses.get(0).getCountryCode();
                    if (Str_getCountryCode.length() > 0 && !Str_getCountryCode.equals(null) && !Str_getCountryCode.equals("null")) {
                        String Str_countyCode = CountryDialCode.getCountryCode(Str_getCountryCode);

                        String drawableName = "flag_" + Str_getCountryCode.toLowerCase();
                        flagImage_IV.setImageResource(getResId(drawableName));
                        countryCodeTV.setText(Str_countyCode);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        hindenReceiver = new HindenReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.newlogin.finish");
        registerReceiver(hindenReceiver, intentFilter);
    }


    private void initOnClick() {
        flagImage_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!picker.isAdded()) {
                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                }
            }
        });

        countryCodeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!picker.isAdded()) {
                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                }
            }
        });

        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode) {
                picker.dismiss();
                countryCodeTV.setText(dialCode);
                String drawableName = "flag_" + code.toLowerCase(Locale.ENGLISH);
                flagImage_IV.setImageResource(getResId(drawableName));
            }
        });

        headerSubmitIv.setEnabled(true);
        headerSubmitIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (countryCodeTV.getText().toString().length() == 0) {
                    alertTV.setText(getResources().getString(R.string.newloginpagesecond_activity_country_code_empty_txt));
                    alertTV.setVisibility(View.VISIBLE);
                } else if (mobileNumberET.getText().toString().length() == 0) {
                    alertTV.setText(getResources().getString(R.string.newloginpagesecond_activity_mobile_no_empty_txt));
                    alertTV.setVisibility(View.VISIBLE);
                } else if (!isValidPhoneNumber(mobileNumberET.getText().toString())) {
                    alertTV.setText(getResources().getString(R.string.newloginpagesecond_activity_invalide_mobile_number_txt));
                    alertTV.setVisibility(View.VISIBLE);
                } else {
                    if (cd.isConnectingToInternet()) {
                        headerSubmitIv.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        postRequest_MobileNumber(ServiceConstant.HOME_MOBILE_NUMBER_URL);
                    } else {
                        Toast.makeText(NewLoginHomePageSecondActivity.this, getResources().getString(R.string.no_inetnet_label), Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });

        headerBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void postRequest_MobileNumber(String url) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone[code]", countryCodeTV.getText().toString());
        params.put("phone[number]", mobileNumberET.getText().toString());
        params.put("country_code", countryCodeTV.getText().toString());
        params.put("phone_number", mobileNumberET.getText().toString());

        ServiceRequest mRequest = new ServiceRequest(NewLoginHomePageSecondActivity.this);
        mRequest.makeServiceRequest(url, Request.Method.POST, params, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                headerSubmitIv.setEnabled(true);
                progressBar.setVisibility(View.GONE);

                String sStatus = "", sMessage = "", sOTP = "", sOTP_status = "", sUser_type = "",
                        sDate_format = "", sRadius = "", sSitemode = "";

                try {

                    JSONObject Jresponse = new JSONObject(response);
                    sStatus = Jresponse.getString("status");
                    if (sStatus.equals("1")) {
                        sOTP = Jresponse.getString("otp");
                        sOTP_status = Jresponse.getString("otp_status");
                        sUser_type = Jresponse.getString("user_type");
                        if (Jresponse.has("radius")) {
                            sRadius = Jresponse.getString("radius");
                        }
                        if (Jresponse.has("date_format")) {
                            sDate_format = Jresponse.getString("date_format");
                        }

                        if (Jresponse.has("sitemode")) {

                            sSitemode = Jresponse.getString("sitemode");
                        }

                        Intent intent = new Intent(NewLoginHomePageSecondActivity.this, NewLoginOTPActivity.class);
                        intent.putExtra("otp", sOTP);
                        intent.putExtra("otp_status", sOTP_status);
                        intent.putExtra("country_code", countryCodeTV.getText().toString());
                        intent.putExtra("phone_number", mobileNumberET.getText().toString());
                        intent.putExtra("user_type", sUser_type);
                        intent.putExtra("radius", sRadius);
                        intent.putExtra("sitemode", sSitemode);
                        intent.putExtra("date_format", sDate_format.replace("MMMM Do, YYYY", "MM-dd-yyyy").replace("Y", "y").replace("D", "d").replace("/", "-"));
                        startActivity(intent);
                        overridePendingTransition(0, 0);

                    } else {
                        if (Jresponse.has("errors")) {
                            sMessage = Jresponse.getString("errors");
                            Alert(getResources().getString(R.string.sorry), sMessage);
                        }
                        if (Jresponse.has("msg")) {
                            sMessage = Jresponse.getString("msg");
                            Alert(getResources().getString(R.string.sorry), sMessage);
                        }
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

    private int getResId(String drawableName) {

        try {
            Class<com.countrycodepicker.R.drawable> res = com.countrycodepicker.R.drawable.class;
            Field field = res.getField(drawableName);
            int drawableId = field.getInt(null);
            return drawableId;
        } catch (Exception e) {
            Log.e("CountryCodePicker", "Failure to get drawable id.", e);
        }
        return -1;
    }

    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(NewLoginHomePageSecondActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(hindenReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    TextWatcher mobileNumberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            alertTV.setVisibility(View.GONE);
        }
    };

    // --------validating Phone Number--------
    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target) || target.length() <= 6) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

}
