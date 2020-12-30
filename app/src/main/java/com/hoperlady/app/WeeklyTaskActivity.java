package com.hoperlady.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.hoperlady.Pojo.WeekTaskBarChartPojo;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.CurrencySymbolConverter;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.adapter.WeeklyTaskAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;

public class WeeklyTaskActivity extends AppCompatActivity {

    private RecyclerView weekTaskListRV;
    private ImageView backIV;
    private String mProviderId = "";
    private SessionManager sessionManager;
    private ConnectionDetector cd;
    private ArrayList<WeekTaskBarChartPojo> sevenDaysListArray;
    private String currencySymbole = "", startDate = "", endDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_task);
        initialization();
        initOnClick();
    }

    private void initialization() {
        weekTaskListRV = (RecyclerView) findViewById(R.id.weekTaskListRV);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(WeeklyTaskActivity.this);
        weekTaskListRV.setLayoutManager(mLayoutManager);
        weekTaskListRV.setHasFixedSize(true);
        weekTaskListRV.setItemAnimator(new DefaultItemAnimator());

        backIV = (ImageView) findViewById(R.id.backIV);

        Intent intent = getIntent();
        startDate = intent.getStringExtra("startDate");
        endDate = intent.getStringExtra("endDate");

        sessionManager = new SessionManager(WeeklyTaskActivity.this);
        cd = new ConnectionDetector(WeeklyTaskActivity.this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        mProviderId = user.get(SessionManager.KEY_PROVIDERID);

        if (cd.isConnectingToInternet()) {
            providerEarningsList(ServiceConstant.EARNINGS_ONE_WEEK_LIST_URL);
        } else {
            Toast.makeText(WeeklyTaskActivity.this, getResources().getString(R.string.no_inetnet_label), Toast.LENGTH_SHORT).show();
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

    private void providerEarningsList(String url) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("provider_id", mProviderId);
        params.put("start_week", startDate);
        params.put("end_week", endDate);

        ServiceRequest mRequest = new ServiceRequest(WeeklyTaskActivity.this);
        mRequest.makeServiceRequest(url, Request.Method.POST, params, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                String jStatus = "", jSResponce = "";
                int max = -1;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    jStatus = jsonObject.getString("status");
                    if (jStatus.equalsIgnoreCase("1")) {
                        JSONObject jResponse = jsonObject.getJSONObject("response");
                        JSONArray jaWeeklyEarning = jResponse.getJSONArray("weekly_earnings");
                        sevenDaysListArray = new ArrayList<>();
                        if (jaWeeklyEarning.length() > 0) {
//                            loaderRL.setVisibility(View.GONE);

                            for (int i = 0; i < jaWeeklyEarning.length(); i++) {
                                JSONObject jWeeklyEarning = jaWeeklyEarning.getJSONObject(i);
                                JSONArray jaEarning = jWeeklyEarning.getJSONArray("earnings");
                                //to
                                for (int k = 0; k < jaEarning.length(); k++) {
                                    JSONObject temp = jaEarning.getJSONObject(k);
                                    if (temp.getInt("earnings") > max)
                                        max = temp.getInt("earnings");
                                }
                                for (int j = 0; j < jaEarning.length(); j++) {
                                    JSONObject jEarning = jaEarning.getJSONObject(j);
                                    WeekTaskBarChartPojo pojo = new WeekTaskBarChartPojo();
                                    pojo.setJob_date(jEarning.getString("job_date"));
                                    pojo.setEarnings(jEarning.getString("earnings"));
                                    pojo.setTask_count(jEarning.getString("task_count"));
                                    pojo.setBarLineSelected(false);
                                    sevenDaysListArray.add(pojo);
                                }
                                for (int j = 0; j < sevenDaysListArray.size(); j++) {
                                    if (Integer.parseInt(sevenDaysListArray.get(j).getTask_count().trim()) == 0) {
                                        sevenDaysListArray.remove(j);
                                        j = j - 1;
                                    }
                                }

                                String currecyCode = jWeeklyEarning.getString("currency");
                                currencySymbole = CurrencySymbolConverter.getCurrencySymbol(currecyCode);
                            }
                        } else {
//                            loaderRL.setVisibility(View.VISIBLE);
                        }


//                        for (int i = sevenDaysListArray.size() - 1; i >= 0; i--) {
//                            // Do something
//                            if (Integer.parseInt(sevenDaysListArray.get(i).getTask_count().trim()) == 0) {
//                                sevenDaysListArray.remove(i);
//                            }
//                        }

                        WeeklyTaskAdapter adapter = new WeeklyTaskAdapter(WeeklyTaskActivity.this, sevenDaysListArray, currencySymbole);
                        weekTaskListRV.setAdapter(adapter);

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
}
