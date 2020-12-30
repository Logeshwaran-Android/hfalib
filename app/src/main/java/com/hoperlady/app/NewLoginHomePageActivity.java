package com.hoperlady.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperlady.R;
import com.hoperlady.Utils.CountryDialCode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import core.Map.GPSTracker;

public class NewLoginHomePageActivity extends AppCompatActivity {

    RelativeLayout mobileNumberIntent;
    private GPSTracker gpsTracker;
    private ImageView flag_image_IV;
    private TextView country_code_TV;

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
        setContentView(R.layout.activity_new_login_home_page);

        mobileNumberIntent = (RelativeLayout) findViewById(R.id.mobile_number_indiviale_RL);
        flag_image_IV = (ImageView) findViewById(R.id.flag_image_IV);
        country_code_TV = (TextView) findViewById(R.id.country_code_TV);

        mobileNumberIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewLoginHomePageActivity.this, NewLoginHomePageSecondActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_slide_enter, R.anim.open_slide_exit);
            }
        });


        gpsTracker = new GPSTracker(NewLoginHomePageActivity.this);
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
                        flag_image_IV.setImageResource(getResId(drawableName));
                        country_code_TV.setText(Str_countyCode);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(hindenReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
