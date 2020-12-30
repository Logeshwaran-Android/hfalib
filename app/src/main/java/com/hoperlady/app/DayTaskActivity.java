package com.hoperlady.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.hoperlady.Pojo.DayTaskPojo;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.CurrencySymbolConverter;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.adapter.DayTaskAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;

public class DayTaskActivity extends AppCompatActivity {
    private RecyclerView todayTaskListRV;
    private ArrayList<DayTaskPojo> sevenDaysListArray;
    private String date = "", task_count = "", total_earnings = "";
    private String mProviderId = "", myCurrencySymbol = "";
    private SessionManager sessionManager;
    private ConnectionDetector cd;
    private TextView todayEarningsTV, todayEarningsDateTV, todayTaskTV, todayTaskDateTV;
    private ImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_task);
        initialization();
        initOnClick();
    }

    private void initialization() {
        todayTaskListRV = (RecyclerView) findViewById(R.id.todayTaskListRV);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(DayTaskActivity.this);
        todayTaskListRV.setLayoutManager(mLayoutManager);
        todayTaskListRV.setHasFixedSize(true);
        todayTaskListRV.setItemAnimator(new DefaultItemAnimator());

        todayEarningsTV = (TextView) findViewById(R.id.todayEarningsTV);
        todayEarningsDateTV = (TextView) findViewById(R.id.todayEarningsDateTV);
        todayTaskTV = (TextView) findViewById(R.id.todayTaskTV);
        todayTaskDateTV = (TextView) findViewById(R.id.todayTaskDateTV);

        backIV = (ImageView) findViewById(R.id.backIV);

        sessionManager = new SessionManager(DayTaskActivity.this);

        Intent dIntent = getIntent();
        date = dIntent.getStringExtra("date");
        task_count = dIntent.getStringExtra("task_count");
        total_earnings = dIntent.getStringExtra("total_earnings");

        HashMap<String, String> aAmountMap = sessionManager.getWalletDetails();
        String aCurrencyCode = aAmountMap.get(SessionManager.KEY_CURRENCY_CODE);
        myCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(aCurrencyCode);

        cd = new ConnectionDetector(DayTaskActivity.this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        mProviderId = user.get(SessionManager.KEY_PROVIDERID);

        todayEarningsTV.setText(myCurrencySymbol + total_earnings);
        todayEarningsDateTV.setText(date);
        todayTaskTV.setText(task_count);
        todayTaskDateTV.setText(date);

        if (cd.isConnectingToInternet()) {
            providerDayByDayEarningsList(ServiceConstant.EARNINGS_ONE_DAY_LIST_URL);
        } else {
            Toast.makeText(DayTaskActivity.this, getResources().getString(R.string.no_inetnet_label), Toast.LENGTH_SHORT).show();
        }
    }

    private void initOnClick() {
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void providerDayByDayEarningsList(String url) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("provider_id", mProviderId);
        params.put("task_date", date);

        ServiceRequest mRequest = new ServiceRequest(DayTaskActivity.this);
        mRequest.makeServiceRequest(url, Request.Method.POST, params, new ServiceRequest.ServiceListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCompleteListener(String response) {

                String jStatus = "", jSResponce = "";
                int max = -1;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    jStatus = jsonObject.getString("status");
                    if (jStatus.equalsIgnoreCase("1")) {
                        JSONObject jResponse = jsonObject.getJSONObject("response");
                        JSONArray jaEarning = jResponse.getJSONArray("earnings");
                        sevenDaysListArray = new ArrayList<>();
                        if (jaEarning.length() > 0) {
//                            loaderRL.setVisibility(View.GONE);`
                            for (int j = 0; j < jaEarning.length(); j++) {
                                JSONObject jEarning = jaEarning.getJSONObject(j);
                                DayTaskPojo pojo = new DayTaskPojo();
                                pojo.setBooking_id(jEarning.getString("booking_id"));
                                pojo.setCategory_name(jEarning.getString("category_name"));
                                pojo.setDate(jEarning.getString("date"));
                                pojo.setTime(jEarning.getString("time"));
                                pojo.setAddress(jEarning.getString("address"));
                                pojo.setAmount(jEarning.getString("amount"));
                                sevenDaysListArray.add(pojo);
                            }

                        } else {
//                            loaderRL.setVisibility(View.VISIBLE);
                        }

                        DayTaskAdapter adapter = new DayTaskAdapter(DayTaskActivity.this, sevenDaysListArray, myCurrencySymbol);
                        todayTaskListRV.setAdapter(adapter);

                    } else {
//                        loaderRL.setVisibility(View.VISIBLE);
                        jSResponce = jsonObject.getString("response");
//                        emptyEarningsTV.setText(jSResponce);
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

    public String getFormateDate(String strDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;
        try {
            date = originalFormat.parse(strDate);
            System.out.println("Old Format :   " + originalFormat.format(date));
            System.out.println("New Format :   " + targetFormat.format(date));
        } catch (ParseException ex) {
            // Handle Exception.
        }
        return targetFormat.format(date);
    }
}
