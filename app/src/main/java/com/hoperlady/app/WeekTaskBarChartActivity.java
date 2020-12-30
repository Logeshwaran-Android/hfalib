package com.hoperlady.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.hoperlady.Pojo.MonthlyTaskBarChartPojo;
import com.hoperlady.Pojo.WeekTaskBarChartPojo;
import com.hoperlady.Pojo.WeeklyStartEndDatePojo;
import com.hoperlady.Pojo.WeeklyTaskDatesPojo;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.CurrencySymbolConverter;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.adapter.WeekTaskBarChartAdapter;
import com.hoperlady.adapter.WeekTaskBarChartAmountAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;

public class WeekTaskBarChartActivity extends AppCompatActivity {

    private RecyclerView earningsListRV, earningsAmountListRV;
    private CardView cardView4;
    private SessionManager sessionManager;
    private String mProviderId = "", sWeekCount = "", sYear = "";
    private ArrayList<WeekTaskBarChartPojo> sevenDaysListArray;
    private ArrayList<MonthlyTaskBarChartPojo> monthlyListArray;
    private ArrayList<WeeklyTaskDatesPojo> billingDatesListArray;
    private ConnectionDetector cd;
    private RelativeLayout loaderRL;
    private TextView emptyEarningsTV, totalAmountTV, totalTaskTV, hoursTaskTV, weekDateTV, tabbarTV;
    private View bg_view;
    private ImageView backIV, prevArrowIV, nextArrowIV;
    private ArrayList<WeeklyStartEndDatePojo> datePojoArrayList;
    private int arrayCount = 0;
    String currencySymbole = "";

    LottieAnimationView animation_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_task_bar_chart);
        initialition();
        initOnClick();
    }

    private void initialition() {
        earningsListRV = (RecyclerView) findViewById(R.id.earningsListRV);
        earningsAmountListRV = (RecyclerView) findViewById(R.id.earningsAmountListRV);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(WeekTaskBarChartActivity.this, LinearLayoutManager.HORIZONTAL, false);
        earningsListRV.setLayoutManager(mLayoutManager);
        earningsListRV.setHasFixedSize(true);
        earningsListRV.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(WeekTaskBarChartActivity.this, LinearLayoutManager.HORIZONTAL, false);
        earningsAmountListRV.setLayoutManager(mLayoutManager1);
        earningsAmountListRV.setHasFixedSize(true);
        earningsAmountListRV.setItemAnimator(new DefaultItemAnimator());

        loaderRL = (RelativeLayout) findViewById(R.id.loaderRL);
        emptyEarningsTV = (TextView) findViewById(R.id.emptyEarningsTV);
        totalAmountTV = (TextView) findViewById(R.id.totalAmountTV);
        totalTaskTV = (TextView) findViewById(R.id.totalTaskTV);
        hoursTaskTV = (TextView) findViewById(R.id.hoursTaskTV);
        weekDateTV = (TextView) findViewById(R.id.weekDateTV);
        tabbarTV = (TextView) findViewById(R.id.tabbarTV);
        prevArrowIV = (ImageView) findViewById(R.id.prevArrowIV);
        nextArrowIV = (ImageView) findViewById(R.id.nextArrowIV);

        cardView4 = (CardView) findViewById(R.id.cardView4);

        bg_view = (View) findViewById(R.id.bg_view);

        backIV = (ImageView) findViewById(R.id.backIV);

        animation_view = (LottieAnimationView) findViewById(R.id.animation_view);

        animation_view.playAnimation();
        animation_view.setProgress(0.5f);
        animation_view.loop(false);

        sessionManager = new SessionManager(WeekTaskBarChartActivity.this);
        cd = new ConnectionDetector(WeekTaskBarChartActivity.this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        mProviderId = user.get(SessionManager.KEY_PROVIDERID);

        Calendar calender = Calendar.getInstance();
        sWeekCount = calender.get(Calendar.WEEK_OF_YEAR) + "";
        sYear = calender.get(Calendar.YEAR) + "";
        Log.e("days", "Current Week:" + calender.get(Calendar.WEEK_OF_YEAR));

        monthlyListArray = new ArrayList<MonthlyTaskBarChartPojo>();

//        if (cd.isConnectingToInternet()) {
//            postRequestEarningsList(ServiceConstant.EARNINGS_ONE_WEEK_LIST_URL, getStartEndOFWeek(Integer.parseInt(sWeekCount), Integer.parseInt(sYear)).get(0), getStartEndOFWeek(Integer.parseInt(sWeekCount), Integer.parseInt(sYear)).get(1));
//        } else {
//            Toast.makeText(WeekTaskBarChartActivity.this, getResources().getString(R.string.no_inetnet_label), Toast.LENGTH_SHORT).show();
//        }

        if (cd.isConnectingToInternet()) {
            providerEarningsList(ServiceConstant.EARNINGS_ONE_WEEK_LIST_URL, getStartEndOFWeek(Integer.parseInt(sWeekCount), Integer.parseInt(sYear)).get(0), getStartEndOFWeek(Integer.parseInt(sWeekCount), Integer.parseInt(sYear)).get(1), 0);
        } else {
            Toast.makeText(WeekTaskBarChartActivity.this, getResources().getString(R.string.no_inetnet_label), Toast.LENGTH_SHORT).show();
        }

    }

    private void initOnClick() {
        bg_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!totalAmountTV.getText().toString().replace(currencySymbole, "").equalsIgnoreCase("0")) {
                    Intent intent = new Intent(WeekTaskBarChartActivity.this, WeeklyTaskActivity.class);
                    intent.putExtra("startDate", datePojoArrayList.get(arrayCount).getStartWeekDate());
                    intent.putExtra("endDate", datePojoArrayList.get(arrayCount).getEndWeekDate());
                    startActivity(intent);
                    overridePendingTransition(R.anim.sheet_show, R.anim.sheet_hide);
                }
            }
        });
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        prevArrowIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                arrayCount = arrayCount - 1;
//                nextArrowIV.setVisibility(View.VISIBLE);
//                if (arrayCount == 0) {
//                    prevArrowIV.setVisibility(View.INVISIBLE);
//                } else {
//                    prevArrowIV.setVisibility(View.VISIBLE);
//                }
//
//                cardView4.setVisibility(View.VISIBLE);
//                earningsAmountListRV.setVisibility(View.VISIBLE);
//                tabbarTV.setVisibility(View.VISIBLE);
//
//                HashMap<String, String> currencyMap = sessionManager.getWalletDetails();
//                String currecyCode = currencyMap.get(SessionManager.KEY_CURRENCY_CODE);
//                currencySymbole = CurrencySymbolConverter.getCurrencySymbol(currecyCode);
//                totalAmountTV.setText(currencySymbole + monthlyListArray.get(arrayCount).getTotal_earnings());
//                weekDateTV.setText(monthlyListArray.get(arrayCount).getBillingStartDate() + " - " + monthlyListArray.get(arrayCount).getBillingEndDate());
//                totalTaskTV.setText(monthlyListArray.get(arrayCount).getTotal_task());
//
//                StringTokenizer time = new StringTokenizer(monthlyListArray.get(arrayCount).getTotal_worked_hours(), ".");
//                String hours = time.nextToken();
//                String minits = time.nextToken();
//                if (minits.length() > 2) {
//                    minits = minits.substring(0, 1);
//                }
//                hoursTaskTV.setText(hours + "h : " + minits + "m");
//
//                WeekTaskBarChartAdapter adapter = new WeekTaskBarChartAdapter(WeekTaskBarChartActivity.this, monthlyListArray.get(arrayCount).getBarChartPojoArrayList(), monthlyListArray.get(arrayCount).getMaxValues(), new WeekTaskBarChartAdapter.Refresh() {
//                    @Override
//                    public void UpdateSelected() {
//                        WeekTaskBarChartAmountAdapter adapter1 = new WeekTaskBarChartAmountAdapter(WeekTaskBarChartActivity.this, monthlyListArray.get(arrayCount).getBarChartPojoArrayList());
//                        earningsAmountListRV.setAdapter(adapter1);
//                    }
//                });
//                earningsListRV.setAdapter(adapter);
//
//                WeekTaskBarChartAmountAdapter adapter1 = new WeekTaskBarChartAmountAdapter(WeekTaskBarChartActivity.this, monthlyListArray.get(arrayCount).getBarChartPojoArrayList());
//                earningsAmountListRV.setAdapter(adapter1);

                if (cd.isConnectingToInternet()) {
                    prevArrowIV.setEnabled(false);
                    nextArrowIV.setEnabled(false);
                    arrayCount = arrayCount - 1;
                    nextArrowIV.setVisibility(View.VISIBLE);
                    if (arrayCount == 0) {
                        prevArrowIV.setVisibility(View.INVISIBLE);
                    } else {
                        prevArrowIV.setVisibility(View.VISIBLE);
                    }
                    providerEarningsList(ServiceConstant.EARNINGS_ONE_WEEK_LIST_URL, datePojoArrayList.get(arrayCount).getStartWeekDate(), datePojoArrayList.get(arrayCount).getEndWeekDate(), 0);
                } else {
                    Toast.makeText(WeekTaskBarChartActivity.this, getResources().getString(R.string.no_inetnet_label), Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextArrowIV.setVisibility(View.INVISIBLE);
        nextArrowIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                arrayCount = arrayCount + 1;
//                prevArrowIV.setVisibility(View.VISIBLE);
//                if (monthlyListArray.size() - 1 == arrayCount) {
//                    nextArrowIV.setVisibility(View.INVISIBLE);
//                } else {
//                    nextArrowIV.setVisibility(View.VISIBLE);
//                }
//
//                cardView4.setVisibility(View.VISIBLE);
//                earningsAmountListRV.setVisibility(View.VISIBLE);
//                tabbarTV.setVisibility(View.VISIBLE);
//
//                HashMap<String, String> currencyMap = sessionManager.getWalletDetails();
//                String currecyCode = currencyMap.get(SessionManager.KEY_CURRENCY_CODE);
//                currencySymbole = CurrencySymbolConverter.getCurrencySymbol(currecyCode);
//                totalAmountTV.setText(currencySymbole + monthlyListArray.get(arrayCount).getTotal_earnings());
//                weekDateTV.setText(monthlyListArray.get(arrayCount).getBillingStartDate() + " - " + monthlyListArray.get(arrayCount).getBillingEndDate());
//                totalTaskTV.setText(monthlyListArray.get(arrayCount).getTotal_task());
//
//                StringTokenizer time = new StringTokenizer(monthlyListArray.get(arrayCount).getTotal_worked_hours(), ".");
//                String hours = time.nextToken();
//                String minits = time.nextToken();
//                if (minits.length() > 2) {
//                    minits = minits.substring(0, 1);
//                }
//                hoursTaskTV.setText(hours + "h : " + minits + "m");
//
//                WeekTaskBarChartAdapter adapter = new WeekTaskBarChartAdapter(WeekTaskBarChartActivity.this, monthlyListArray.get(arrayCount).getBarChartPojoArrayList(), monthlyListArray.get(arrayCount).getMaxValues(), new WeekTaskBarChartAdapter.Refresh() {
//                    @Override
//                    public void UpdateSelected() {
//                        WeekTaskBarChartAmountAdapter adapter1 = new WeekTaskBarChartAmountAdapter(WeekTaskBarChartActivity.this, monthlyListArray.get(arrayCount).getBarChartPojoArrayList());
//                        earningsAmountListRV.setAdapter(adapter1);
//                    }
//                });
//                earningsListRV.setAdapter(adapter);
//
//                WeekTaskBarChartAmountAdapter adapter1 = new WeekTaskBarChartAmountAdapter(WeekTaskBarChartActivity.this, monthlyListArray.get(arrayCount).getBarChartPojoArrayList());
//                earningsAmountListRV.setAdapter(adapter1);


                if (cd.isConnectingToInternet()) {
                    nextArrowIV.setEnabled(false);
                    prevArrowIV.setEnabled(false);
                    arrayCount = arrayCount + 1;
                    prevArrowIV.setVisibility(View.VISIBLE);
                    if (datePojoArrayList.size() - 1 == arrayCount) {
                        nextArrowIV.setVisibility(View.INVISIBLE);
                    } else {
                        nextArrowIV.setVisibility(View.VISIBLE);
                    }
                    providerEarningsList(ServiceConstant.EARNINGS_ONE_WEEK_LIST_URL, datePojoArrayList.get(arrayCount).getStartWeekDate(), datePojoArrayList.get(arrayCount).getEndWeekDate(), 0);
                } else {
                    Toast.makeText(WeekTaskBarChartActivity.this, getResources().getString(R.string.no_inetnet_label), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void providerEarningsList(String url, final String startWeekDate, final String endWeekDate, final int value) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("provider_id", mProviderId);
        params.put("start_week", startWeekDate);
        params.put("end_week", endWeekDate);
        loaderRL.setVisibility(View.VISIBLE);
        ServiceRequest mRequest = new ServiceRequest(WeekTaskBarChartActivity.this);
        mRequest.makeServiceRequest(url, Request.Method.POST, params, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                prevArrowIV.setEnabled(true);
                nextArrowIV.setEnabled(true);

                String jStatus = "", jSResponce = "";
                int max = -1;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    jStatus = jsonObject.getString("status");
                    if (jStatus.equalsIgnoreCase("1")) {
                        JSONObject jResponse = jsonObject.getJSONObject("response");
                        billingDatesListArray = new ArrayList<>();
                        if (jResponse.has("billing")) {
                            JSONArray jaBilling = jResponse.getJSONArray("billing");
                            if (!billingDatesListArray.contains(startWeekDate)) {
                                WeeklyTaskDatesPojo pojo1 = new WeeklyTaskDatesPojo();
                                pojo1.setStart_date(startWeekDate);
                                pojo1.setEnd_date(endWeekDate);
                                billingDatesListArray.add(pojo1);
                            }
                            if (jaBilling.length() > 0) {
                                for (int b = 0; b < jaBilling.length(); b++) {
                                    JSONObject jBilling = jaBilling.getJSONObject(b);
                                    WeeklyTaskDatesPojo pojo = new WeeklyTaskDatesPojo();
                                    try {
                                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        Date date = inputFormat.parse(jBilling.getString("start_date"));
                                        String formattedDate = outputFormat.format(date);
                                        pojo.setStart_date(formattedDate);

                                        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                        SimpleDateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                                        Date date1 = inputFormat1.parse(jBilling.getString("end_date"));
                                        String formattedDate1 = outputFormat1.format(date1);
                                        pojo.setEnd_date(formattedDate1);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    billingDatesListArray.add(pojo);
                                }
                            }
                        }


                        JSONArray jaWeeklyEarning = jResponse.getJSONArray("weekly_earnings");
                        sevenDaysListArray = new ArrayList<>();
                        if (jaWeeklyEarning.length() > 0) {
                            loaderRL.setVisibility(View.GONE);

                            for (int i = 0; i < jaWeeklyEarning.length(); i++) {
                                JSONObject jWeeklyEarning = jaWeeklyEarning.getJSONObject(i);
                                JSONArray jaEarning = jWeeklyEarning.getJSONArray("earnings");

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

                                String currecyCode = jWeeklyEarning.getString("currency");
                                sessionManager.createWalletSession(currecyCode);
                                currencySymbole = CurrencySymbolConverter.getCurrencySymbol(currecyCode);
                                totalAmountTV.setText(currencySymbole + jWeeklyEarning.getString("total_earnings"));
                                weekDateTV.setText(sevenDaysListArray.get(0).getJob_date() + " - " + sevenDaysListArray.get(sevenDaysListArray.size() - 1).getJob_date());
                                totalTaskTV.setText(jWeeklyEarning.getString("total_task"));
                                StringTokenizer time = new StringTokenizer(jWeeklyEarning.getString("total_worked_hours"), ".");
                                String hours = time.nextToken();
                                String minits = time.nextToken();
                                if (minits.length() > 2) {
                                    minits = minits.substring(0, 1);
                                }
                                hoursTaskTV.setText(hours + "h : " + minits + "m");

                                String joinDate = jResponse.getString("first_job_date");
                                String joinDate1 = joinDate.split("T")[0];
                                int joinDate2 = getWeekDates(Integer.parseInt(joinDate1.split("-")[0]),
                                        Integer.parseInt(joinDate1.split("-")[1]),
                                        Integer.parseInt(joinDate1.split("-")[2]));

                                if (billingDatesListArray.size() == 0) {
                                    if (datePojoArrayList == null) {
                                        getStartEndWeek(joinDate2, Integer.parseInt(joinDate1.split("-")[0]), Integer.parseInt(sWeekCount), Calendar.getInstance().get(Calendar.YEAR));
                                    }
                                } else {
                                    datePojoArrayList = new ArrayList<>();
                                    for (int dp = 0; dp < billingDatesListArray.size(); dp++) {
                                        WeeklyStartEndDatePojo pojo = new WeeklyStartEndDatePojo();
                                        pojo.setStartWeekDate(billingDatesListArray.get(i).getStart_date());
                                        pojo.setEndWeekDate(billingDatesListArray.get(i).getEnd_date());
                                        datePojoArrayList.add(pojo);
                                    }
                                    arrayCount = datePojoArrayList.size() - 1;
                                    if (arrayCount == -1 || arrayCount == 0) {
                                        prevArrowIV.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                        } else {
                            if (value == 0) {
                                loaderRL.setVisibility(View.GONE);
                                elseEarningsValue(jResponse, startWeekDate, endWeekDate);
                            } else {
                                loaderRL.setVisibility(View.VISIBLE);
                            }
                        }

                        cardView4.setVisibility(View.VISIBLE);
                        earningsAmountListRV.setVisibility(View.VISIBLE);
                        tabbarTV.setVisibility(View.VISIBLE);

                        WeekTaskBarChartAdapter adapter = new WeekTaskBarChartAdapter(WeekTaskBarChartActivity.this, sevenDaysListArray, max + "", new WeekTaskBarChartAdapter.Refresh() {
                            @Override
                            public void UpdateSelected() {
                                WeekTaskBarChartAmountAdapter adapter1 = new WeekTaskBarChartAmountAdapter(WeekTaskBarChartActivity.this, sevenDaysListArray);
                                earningsAmountListRV.setAdapter(adapter1);
                            }
                        });
                        earningsListRV.setAdapter(adapter);

                        WeekTaskBarChartAmountAdapter adapter1 = new WeekTaskBarChartAmountAdapter(WeekTaskBarChartActivity.this, sevenDaysListArray);
                        earningsAmountListRV.setAdapter(adapter1);

                    } else if (jStatus.equalsIgnoreCase("2")) {
                        if (value == 0) {
                            JSONObject jResponse = jsonObject.getJSONObject("response");
                            billingDatesListArray = new ArrayList<>();
                            if (jResponse.has("billing")) {
                                JSONArray jaBilling = jResponse.getJSONArray("billing");
                                if (!billingDatesListArray.contains(startWeekDate)) {
                                    WeeklyTaskDatesPojo pojo1 = new WeeklyTaskDatesPojo();
                                    pojo1.setStart_date(startWeekDate);
                                    pojo1.setEnd_date(endWeekDate);
                                    billingDatesListArray.add(pojo1);
                                }
                                if (jaBilling.length() > 0) {
                                    for (int b = 0; b < jaBilling.length(); b++) {
                                        JSONObject jBilling = jaBilling.getJSONObject(b);
                                        WeeklyTaskDatesPojo pojo = new WeeklyTaskDatesPojo();

                                        try {
                                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                            Date date = inputFormat.parse(jBilling.getString("start_date"));
                                            String formattedDate = outputFormat.format(date);
                                            pojo.setStart_date(formattedDate);

                                            SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                            SimpleDateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                                            Date date1 = inputFormat1.parse(jBilling.getString("end_date"));
                                            String formattedDate1 = outputFormat1.format(date1);
                                            pojo.setEnd_date(formattedDate1);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        billingDatesListArray.add(pojo);
                                    }
                                }
                            }
                            loaderRL.setVisibility(View.GONE);
                            elseEarningsValue(jResponse, startWeekDate, endWeekDate);
                        } else {
                            loaderRL.setVisibility(View.VISIBLE);
                            jSResponce = jsonObject.getString("response");
                            emptyEarningsTV.setText(jSResponce);

                        }
                    } else {
                        loaderRL.setVisibility(View.VISIBLE);
                        jSResponce = jsonObject.getString("response");
                        emptyEarningsTV.setText(jSResponce);

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

    private void postRequestEarningsList(String url, String startWeekDate, String endWeekDate) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("provider_id", mProviderId);
        params.put("start_week", startWeekDate);
        params.put("end_week", endWeekDate);
        loaderRL.setVisibility(View.VISIBLE);
        ServiceRequest mRequest = new ServiceRequest(WeekTaskBarChartActivity.this);
        mRequest.makeServiceRequest(url, Request.Method.POST, params, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                loaderRL.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jaBilling = jsonObject.getJSONArray("billing");
                    if (jaBilling.length() > 0) {
                        int j = 0;
                        for (int i = 0; i < jaBilling.length(); i++) {
                            JSONObject jMonthlyData = jaBilling.getJSONObject(i);
                            MonthlyTaskBarChartPojo pojoM = new MonthlyTaskBarChartPojo();
                            pojoM.setBillingStartDate(getFormates(jMonthlyData.getString("start_date")));
                            pojoM.setBillingEndDate(getFormates(jMonthlyData.getString("end_date")));
//                            pojoM.setBillingStartDate(jMonthlyData.getString("start_date"));
//                            pojoM.setBillingEndDate(jMonthlyData.getString("end_date"));
                            JSONObject jResponse = jsonObject.getJSONObject("response");
                            JSONArray jaEarnings = jResponse.getJSONArray("weekly_earnings");
                            int max = 0;
                            sevenDaysListArray = new ArrayList<WeekTaskBarChartPojo>();
                            if (jaEarnings.length() > 0) {
//                                for (j = 0; j < jaEarnings.length(); j++) {
                                j = j + 1;
//                                if (j <= (jaEarnings.length() - 1)) {
                                JSONObject jEarnings = jaEarnings.getJSONObject(j);
                                JSONArray jaWeeklyData = jEarnings.getJSONArray("weekly_data");
                                if (jaWeeklyData.length() > 0) {
                                    for (int k = 0; k < getInBetweenDates(getFormates(jMonthlyData.getString("start_date")), getFormates(jMonthlyData.getString("end_date"))).size(); k++) {
                                        WeekTaskBarChartPojo pojo = new WeekTaskBarChartPojo();
                                        pojo.setJob_date(getInBetweenDates(jMonthlyData.getString("start_date"), jMonthlyData.getString("end_date")).get(k));
                                        pojo.setEarnings("0");
                                        pojo.setTask_count("0");
                                        pojo.setBarLineSelected(false);
                                        sevenDaysListArray.add(pojo);
                                    }

                                    for (int k = 0; k < jaWeeklyData.length(); k++) {
                                        JSONObject temp = jaWeeklyData.getJSONObject(k);
                                        if (temp.getInt("earnings") > max)
                                            max = temp.getInt("earnings");
                                    }

                                    for (int k = 0; k < jaWeeklyData.length(); k++) {
                                        JSONObject jWeeklyData = jaWeeklyData.getJSONObject(k);

                                        for (int l = 0; l < sevenDaysListArray.size(); l++) {
                                            if (getFormates(jWeeklyData.getString("job_date")).equalsIgnoreCase(sevenDaysListArray.get(l).getJob_date())) {
                                                sevenDaysListArray.get(l).setJob_date(getFormates(jWeeklyData.getString("job_date")));
                                                sevenDaysListArray.get(l).setEarnings(jWeeklyData.getString("earnings"));
                                                sevenDaysListArray.get(l).setTask_count(jWeeklyData.getString("task_count"));
                                                sevenDaysListArray.get(l).setBarLineSelected(false);
                                            }
                                        }
                                    }
                                }
                                pojoM.setMaxValues(max + "");
                                pojoM.setTotal_earnings(jEarnings.getString("total_earnings"));
                                pojoM.setTotal_task(jEarnings.getString("total_task"));
                                pojoM.setTotal_worked_hours(jEarnings.getString("total_worked_hours"));
                                pojoM.setBarChartPojoArrayList(sevenDaysListArray);
                                monthlyListArray.add(pojoM);
//                                } else {
//                                    for (int k = 0; k < getInBetweenDates(getFormates(jMonthlyData.getString("start_date")), getFormates(jMonthlyData.getString("end_date"))).size(); k++) {
//                                        WeekTaskBarChartPojo pojo = new WeekTaskBarChartPojo();
//                                        pojo.setJob_date(getInBetweenDates(jMonthlyData.getString("start_date"), jMonthlyData.getString("end_date")).get(k));
//                                        pojo.setEarnings("0");
//                                        pojo.setTask_count("0");
//                                        pojo.setBarLineSelected(false);
//                                        sevenDaysListArray.add(pojo);
//                                    }
//                                    pojoM.setMaxValues("0");
//                                    pojoM.setTotal_earnings("0");
//                                    pojoM.setTotal_task("0");
//                                    pojoM.setTotal_worked_hours("0.0");
//                                    pojoM.setBarChartPojoArrayList(sevenDaysListArray);
//                                    monthlyListArray.add(pojoM);
//                                }
//                                }
                            }
                        }
                        Collections.reverse(monthlyListArray);
                        cardView4.setVisibility(View.VISIBLE);
                        earningsAmountListRV.setVisibility(View.VISIBLE);
                        tabbarTV.setVisibility(View.VISIBLE);

                        HashMap<String, String> currencyMap = sessionManager.getWalletDetails();
                        String currecyCode = currencyMap.get(SessionManager.KEY_CURRENCY_CODE);
                        currencySymbole = CurrencySymbolConverter.getCurrencySymbol(currecyCode);
                        totalAmountTV.setText(currencySymbole + monthlyListArray.get(monthlyListArray.size() - 1).getTotal_earnings());
                        weekDateTV.setText(monthlyListArray.get(monthlyListArray.size() - 1).getBillingStartDate() + " - " + monthlyListArray.get(monthlyListArray.size() - 1).getBillingEndDate());
                        totalTaskTV.setText(monthlyListArray.get(monthlyListArray.size() - 1).getTotal_task());

                        StringTokenizer time = new StringTokenizer(monthlyListArray.get(monthlyListArray.size() - 1).getTotal_worked_hours(), ".");
                        String hours = time.nextToken();
                        String minits = time.nextToken();
                        if (minits.length() > 2) {
                            minits = minits.substring(0, 1);
                        }
                        hoursTaskTV.setText(hours + "h : " + minits + "m");

                        WeekTaskBarChartAdapter adapter = new WeekTaskBarChartAdapter(WeekTaskBarChartActivity.this, monthlyListArray.get(monthlyListArray.size() - 1).getBarChartPojoArrayList(), monthlyListArray.get(monthlyListArray.size() - 1).getMaxValues(), new WeekTaskBarChartAdapter.Refresh() {
                            @Override
                            public void UpdateSelected() {
                                WeekTaskBarChartAmountAdapter adapter1 = new WeekTaskBarChartAmountAdapter(WeekTaskBarChartActivity.this, monthlyListArray.get(monthlyListArray.size() - 1).getBarChartPojoArrayList());
                                earningsAmountListRV.setAdapter(adapter1);
                            }
                        });
                        earningsListRV.setAdapter(adapter);

                        WeekTaskBarChartAmountAdapter adapter1 = new WeekTaskBarChartAmountAdapter(WeekTaskBarChartActivity.this, monthlyListArray.get(monthlyListArray.size() - 1).getBarChartPojoArrayList());
                        earningsAmountListRV.setAdapter(adapter1);

                        arrayCount = monthlyListArray.size() - 1;
                    }
                    Log.e("monthlyListArray", "monthlyListArray" + monthlyListArray.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onErrorListener() {

            }
        });
    }


    private void elseEarningsValue(JSONObject jResponse, String startWeekDate, String endWeekDate) {
        HashMap<String, String> currencyMap = sessionManager.getWalletDetails();
        String currecyCode = currencyMap.get(SessionManager.KEY_CURRENCY_CODE);
        currencySymbole = CurrencySymbolConverter.getCurrencySymbol(currecyCode);
        totalAmountTV.setText(currencySymbole + "0");
        weekDateTV.setText(startWeekDate + " - " + endWeekDate);
        totalTaskTV.setText("0");
        StringTokenizer time = new StringTokenizer("0.0", ".");
        String hours = time.nextToken();
        String minits = time.nextToken();
        if (minits.length() > 2) {
            minits = minits.substring(0, 1);
        }
        hoursTaskTV.setText(hours + "h : " + minits + "m");
        cardView4.setVisibility(View.GONE);
        earningsAmountListRV.setVisibility(View.GONE);
        tabbarTV.setVisibility(View.GONE);

        try {
            String joinDate = jResponse.getString("first_job_date");
            if (!joinDate.isEmpty()) {
                String joinDate1 = joinDate.split("T")[0];
                int joinDate2 = getWeekDates(Integer.parseInt(joinDate1.split("-")[0]),
                        Integer.parseInt(joinDate1.split("-")[1]),
                        Integer.parseInt(joinDate1.split("-")[2]));
                if (billingDatesListArray.size() == 0) {
                    if (datePojoArrayList == null) {
                        getStartEndWeek(joinDate2, Integer.parseInt(joinDate1.split("-")[0]), Integer.parseInt(sWeekCount), Calendar.getInstance().get(Calendar.YEAR));
                    }
                } else {
                    datePojoArrayList = new ArrayList<>();
                    for (int i = 0; i < billingDatesListArray.size(); i++) {
                        WeeklyStartEndDatePojo pojo = new WeeklyStartEndDatePojo();
                        pojo.setStartWeekDate(billingDatesListArray.get(i).getStart_date());
                        pojo.setEndWeekDate(billingDatesListArray.get(i).getEnd_date());
                        datePojoArrayList.add(pojo);
                    }
                    arrayCount = datePojoArrayList.size() - 1;
                    if (arrayCount == -1 || arrayCount == 0) {
                        prevArrowIV.setVisibility(View.INVISIBLE);
                    }
                }
            } else {
                loaderRL.setVisibility(View.VISIBLE);
                String sMessage = jResponse.getString("message");
                emptyEarningsTV.setText(sMessage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> getCurrentWeekDate(int year, int month, int day) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        String[] days = new String[7];
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < 7; i++) {
            arrayList.add(format.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return arrayList;
    }

    public int getWeekDates(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);

        int ordinalDay = cal.get(Calendar.DAY_OF_YEAR);
        int weekDay = cal.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 0
        int numberOfWeeks = (ordinalDay - weekDay + 10) / 7;
        Log.e("days", "numberOfWeeks---" + numberOfWeeks);

        return numberOfWeeks;
    }

    public ArrayList<String> getStartEndOFWeek(int enterWeek, int enterYear) {
        //enterWeek is week number
        //enterYear is year
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, enterWeek);
        calendar.set(Calendar.YEAR, enterYear);

//        SimpleDateFormat formatter = new SimpleDateFormat("ddMMM yyyy"); // PST`
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = calendar.getTime();
        String startDateInStr = formatter.format(startDate);
//        Log.e("days", "days" + startDateInStr);

        calendar.add(Calendar.DATE, 6);
        Date enddate = calendar.getTime();
        String endDaString = formatter.format(enddate);
//        Log.e("days", "days" + endDaString);

        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(startDateInStr);
        arrayList.add(endDaString);

        return arrayList;
    }

    public ArrayList<WeeklyStartEndDatePojo> getStartEndWeek(int startWeekNumber, int startYear, int endWeekNumber, int endYear) {
        datePojoArrayList = new ArrayList<WeeklyStartEndDatePojo>();
        boolean isCorrectDate = true;
        for (int j = startYear; j <= endYear; j++) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, j);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DAY_OF_MONTH, 31);

            int ordinalDay = cal.get(Calendar.DAY_OF_YEAR);
            int weekDay = cal.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 0
            int numberOfWeeks = (ordinalDay - weekDay + 10) / 7;
            Log.e("days", "numberOfWeeks---" + numberOfWeeks);

            if (j == endYear) {
                if (!isCorrectDate) {
                    startWeekNumber = 1;
                }
                for (int i = startWeekNumber; i <= endWeekNumber; i++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.clear();
                    calendar.setFirstDayOfWeek(Calendar.SUNDAY);
                    calendar.set(Calendar.WEEK_OF_YEAR, i);
                    calendar.set(Calendar.YEAR, j);

//              SimpleDateFormat formatter = new SimpleDateFormat("ddMMM yyyy"); // PST
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate = calendar.getTime();
                    String startDateInStr = formatter.format(startDate);
                    Log.e("days", "days---" + startDateInStr);

                    calendar.add(Calendar.DATE, 6);
                    Date enddate = calendar.getTime();
                    String endDaString = formatter.format(enddate);
                    Log.e("days", "days---" + endDaString);
                    WeeklyStartEndDatePojo pojo = new WeeklyStartEndDatePojo();
                    pojo.setStartWeekDate(startDateInStr);
                    pojo.setEndWeekDate(endDaString);
                    datePojoArrayList.add(pojo);
                }
            } else {
                isCorrectDate = false;
                if (startYear != j) {
                    startWeekNumber = 1;
                }
                for (int i = startWeekNumber; i <= numberOfWeeks; i++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.clear();
                    calendar.setFirstDayOfWeek(Calendar.SUNDAY);
                    calendar.set(Calendar.WEEK_OF_YEAR, i);
                    calendar.set(Calendar.YEAR, j);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate = calendar.getTime();
                    String startDateInStr = formatter.format(startDate);
                    Log.e("days", "days1---" + startDateInStr);

                    calendar.add(Calendar.DATE, 6);
                    Date enddate = calendar.getTime();
                    String endDaString = formatter.format(enddate);
                    Log.e("days", "days1---" + endDaString);

                    WeeklyStartEndDatePojo pojo = new WeeklyStartEndDatePojo();
                    pojo.setStartWeekDate(startDateInStr);
                    pojo.setEndWeekDate(endDaString);
                    datePojoArrayList.add(pojo);
                }
            }
        }
        arrayCount = datePojoArrayList.size() - 1;
        if (arrayCount == -1 || arrayCount == 0) {
            prevArrowIV.setVisibility(View.INVISIBLE);
        }
        return datePojoArrayList;
    }

    public String getFormateDate(String strDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
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

    public ArrayList<String> getInBetweenDates(String StrstartDate, String StrendDate) {
        ArrayList<String> dateList = new ArrayList<String>();
        try {
            ArrayList<Date> dates = new ArrayList<Date>();
            DateFormat formatter;
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = (Date) formatter.parse(StrstartDate);
            Date endDate = (Date) formatter.parse(StrendDate);
            long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
            long endTime = endDate.getTime(); // create your endtime here, possibly using Calendar or Date
            long curTime = startDate.getTime();
            while (curTime <= endTime) {
                dates.add(new Date(curTime));
                curTime += interval;
            }
            for (int i = 0; i < dates.size(); i++) {
                Date lDate = (Date) dates.get(i);
                String ds = formatter.format(lDate);
                dateList.add(ds);
                Log.e("DateList", "Date is ..." + ds);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateList;
    }

    public String getFormates(String strDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = originalFormat.parse(strDate);
            System.out.println("Old Format :   " + originalFormat.format(date));
            System.out.println("New Format :   " + targetFormat.format(date));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return targetFormat.format(date);
    }
}
