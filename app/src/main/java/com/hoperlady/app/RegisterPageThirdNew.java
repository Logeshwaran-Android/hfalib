package com.hoperlady.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.hoperlady.Pojo.AvailDayPojo;
import com.hoperlady.Pojo.Availability_selecting_pojo;
import com.hoperlady.Pojo.Experiancepojo;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.adapter.ExperianceAdapter;
import com.hoperlady.adapter.RegisterAvailabilty_Adapter;
import com.hoperlady.hockeyapp.ActivityHockeyApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import core.Dialog.PkLoadingDialog;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;


public class RegisterPageThirdNew extends ActivityHockeyApp implements View.OnClickListener {

    RelativeLayout register_avail_drawer, main_RL;
    LinearLayout register_header_back_layout;
    ListView gridView;
    DrawerLayout drawer_layout;
    TextView day_txt_TV;
    SwitchCompat evening_switch, morning_switch, afternoon_switch;
    LinearLayout save_LL, continue_button_LL;
    int Position;
    ActionBarDrawerToggle actionBarDrawerToggle;
    int mHour = 0, mMinute = 0;
    ArrayList<Availability_selecting_pojo> avail_pojoArraylist;
    Availability_selecting_pojo avail_pojo;
    RegisterAvailabilty_Adapter adapter;
    ExperianceAdapter expAdapter;
    ListView experiance_listview;
    ConnectionDetector cd;
    ArrayList<Experiancepojo> ExperianceList;
    ArrayList<Experiancepojo> sessionExperianceList;
    PkLoadingDialog myLoadingDialog;
    ArrayList<String> questions_list;
    SessionManager sessionManager;
    LinearLayout ll_sunday, ll_monday, ll_tuesday, ll_wednesday, ll_thursday, ll_friday, ll_saturday;
    HashMap<String, String> hashMapSunday, hashMapMonday, hashMapTuesday, hashMapWednessday, hashMapThursday, hashMapFriday, hashMapSaturday;
    private SessionManagerRegistration sessionManagerRegistrationAvailability, sessionManagerRegistration;
    private ArrayList<AvailDayPojo> myDayArrList;
    private TextView btn_from, btn_to, btn_submit;
    private CheckBox chkSunday, chkMonday, chkTuesday, chkWednesday, chkThursday, chkFriday, chkSaturday;
    private CheckBox chkWholeSunday, chkWholeMonday, chkWholeTuesday, chkWholeWednesday, chkWholeThursday, chkWholeFriday, chkWholeSaturday;
    private TextView TvSunday, TvMonday, TvTuesday, TvWednesday, TvThursday, TvFriday, TvSaturday;
    private TextView TvSundayTime, TvMondayTime, TvTuesdayTime, TvWednesdayTime, TvThursdayTime, TvFridayTime, TvSaturdayTime;
    private LinearLayout ll_btn_cancel;
    private JSONArray jsonArrayWholeJsonArray;

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.setDivider(null);
        listView.requestLayout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_third_layout);
        initialize();
        initObects();
        initSessionVariables();
    }

    private void initialize() {
        register_header_back_layout = (LinearLayout) findViewById(R.id.register_header_back_layout);
        register_avail_drawer = (RelativeLayout) findViewById(R.id.register_avail_drawer);
        main_RL = (RelativeLayout) findViewById(R.id.main_RL);
        cd = new ConnectionDetector(RegisterPageThirdNew.this);
        day_txt_TV = (TextView) findViewById(R.id.day_txt_TV);
//        gridView = (ListView) findViewById(R.id.gridView);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer_layout.requestDisallowInterceptTouchEvent(true);
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        morning_switch = (SwitchCompat) findViewById(R.id.morning_switch);
        afternoon_switch = (SwitchCompat) findViewById(R.id.afternoon_switch);
        evening_switch = (SwitchCompat) findViewById(R.id.evening_switch);
        save_LL = (LinearLayout) findViewById(R.id.save_LL);
        continue_button_LL = (LinearLayout) findViewById(R.id.continue_button_LL);

        ll_sunday = (LinearLayout) findViewById(R.id.ll_sunday);
        ll_monday = (LinearLayout) findViewById(R.id.ll_monday);
        ll_tuesday = (LinearLayout) findViewById(R.id.ll_tuesday);
        ll_wednesday = (LinearLayout) findViewById(R.id.ll_wednesday);
        ll_thursday = (LinearLayout) findViewById(R.id.ll_thursday);
        ll_friday = (LinearLayout) findViewById(R.id.ll_friday);
        ll_saturday = (LinearLayout) findViewById(R.id.ll_saturday);

        chkSunday = (CheckBox) findViewById(R.id.chkSunday);
        chkMonday = (CheckBox) findViewById(R.id.chkMonday);
        chkTuesday = (CheckBox) findViewById(R.id.chkTuesday);
        chkWednesday = (CheckBox) findViewById(R.id.chkWednesday);
        chkThursday = (CheckBox) findViewById(R.id.chkThursday);
        chkFriday = (CheckBox) findViewById(R.id.chkFriday);
        chkSaturday = (CheckBox) findViewById(R.id.chkSaturday);

        chkWholeSunday = (CheckBox) findViewById(R.id.chkWholeSunday);
        chkWholeMonday = (CheckBox) findViewById(R.id.chkWholeMonday);
        chkWholeTuesday = (CheckBox) findViewById(R.id.chkWholeTuesday);
        chkWholeWednesday = (CheckBox) findViewById(R.id.chkWholeWednesday);
        chkWholeThursday = (CheckBox) findViewById(R.id.chkWholeThursday);
        chkWholeFriday = (CheckBox) findViewById(R.id.chkWholeFriday);
        chkWholeSaturday = (CheckBox) findViewById(R.id.chkWholeSaturday);

        TvSunday = (TextView) findViewById(R.id.TvSunday);
        TvSundayTime = (TextView) findViewById(R.id.TvSundayTime);

        TvMonday = (TextView) findViewById(R.id.TvMonday);
        TvMondayTime = (TextView) findViewById(R.id.TvMondayTime);

        TvTuesday = (TextView) findViewById(R.id.TvTuesday);
        TvTuesdayTime = (TextView) findViewById(R.id.TvTuesdayTime);

        TvWednesday = (TextView) findViewById(R.id.TvWednesday);
        TvWednesdayTime = (TextView) findViewById(R.id.TvWednesdayTime);

        TvThursday = (TextView) findViewById(R.id.TvThursday);
        TvThursdayTime = (TextView) findViewById(R.id.TvThursdayTime);

        TvFriday = (TextView) findViewById(R.id.TvFriday);
        TvFridayTime = (TextView) findViewById(R.id.TvFridayTime);

        TvSaturday = (TextView) findViewById(R.id.TvSaturday);
        TvSaturdayTime = (TextView) findViewById(R.id.TvSaturdayTime);

        avail_pojo = new Availability_selecting_pojo();
        avail_pojoArraylist = new ArrayList<>();
        myDayArrList = new ArrayList<>();
        //click listners
        register_header_back_layout.setOnClickListener(this);
        save_LL.setOnClickListener(this);
        continue_button_LL.setOnClickListener(this);
//        loadDayItems();
//        adapter = new RegisterAvailabilty_Adapter(this, myDayArrList);
//        gridView.setAdapter(adapter);
        experiance_listview = (ListView) findViewById(R.id.experiance_listview);
        questions_list = new ArrayList<String>();
        sessionManager = new SessionManager(RegisterPageThirdNew.this);
        if (cd.isConnectingToInternet()) {
            QuestionAnswerRequest(ServiceConstant.QUESTION_ANSWER_URL);
        }
//        defaultDaysFalse();
//        if (myDayArrList.size() > 0) {
//            setListViewHeightBasedOnChildren(gridView);
//            adapter.notifyDataSetChanged();
//        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                gridView.setEnabled(true);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                gridView.setEnabled(false);
            }
        };

        drawer_layout.setDrawerListener(actionBarDrawerToggle);
        drawer_layout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        chkSunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CompoundButton) view).isChecked()) {
                    chkSunday.setChecked(true);
                    chkWholeSunday.setChecked(false);
                    DialogTimepicker(TvSundayTime, TvSunday, "Sunday");
                } else {
                    TvSundayTime.setVisibility(View.GONE);
                    chkSunday.setChecked(false);
                }
            }
        });

        chkMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CompoundButton) view).isChecked()) {
                    chkMonday.setChecked(true);
                    chkWholeMonday.setChecked(false);
                    DialogTimepicker(TvMondayTime, TvMonday, "Monday");
                } else {
                    TvMondayTime.setVisibility(View.GONE);
                    chkMonday.setChecked(false);
                }
            }
        });

        chkTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CompoundButton) view).isChecked()) {
                    chkTuesday.setChecked(true);
                    chkWholeTuesday.setChecked(false);
                    DialogTimepicker(TvTuesdayTime, TvTuesday, "Tuesday");
                } else {
                    TvTuesdayTime.setVisibility(View.GONE);
                    chkTuesday.setChecked(false);
                }
            }
        });

        chkWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CompoundButton) view).isChecked()) {
                    chkWednesday.setChecked(true);
                    chkWholeWednesday.setChecked(false);
                    DialogTimepicker(TvWednesdayTime, TvWednesday, "Wednesday");
                } else {
                    TvWednesdayTime.setVisibility(View.GONE);
                    chkWednesday.setChecked(false);
                }
            }
        });

        chkThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CompoundButton) view).isChecked()) {
                    chkThursday.setChecked(true);
                    chkWholeThursday.setChecked(false);
                    DialogTimepicker(TvThursdayTime, TvThursday, "Thursday");
                } else {
                    TvThursdayTime.setVisibility(View.GONE);
                    chkThursday.setChecked(false);
                }
            }
        });

        chkFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CompoundButton) view).isChecked()) {
                    chkFriday.setChecked(true);
                    chkWholeFriday.setChecked(false);
                    DialogTimepicker(TvFridayTime, TvFriday, "Friday");
                } else {
                    TvFridayTime.setVisibility(View.GONE);
                    chkFriday.setChecked(false);
                }
            }
        });

        chkSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CompoundButton) view).isChecked()) {
                    chkSaturday.setChecked(true);
                    chkWholeSaturday.setChecked(false);
                    DialogTimepicker(TvSaturdayTime, TvSaturday, "Saturday");
                } else {
                    TvSaturdayTime.setVisibility(View.GONE);
                    chkSaturday.setChecked(false);
                }
            }
        });


// ================================Availability Whole Day Selection=============================================================

        chkWholeSunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CompoundButton) view).isChecked()) {
                    TvSundayTime.setVisibility(View.GONE);
                    chkWholeSunday.setChecked(true);
                    chkSunday.setChecked(false);
                } else {
                    chkWholeSunday.setChecked(false);
                }
            }
        });

        chkWholeMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CompoundButton) view).isChecked()) {
                    TvMondayTime.setVisibility(View.GONE);
                    chkWholeMonday.setChecked(true);
                    chkMonday.setChecked(false);
                } else {
                    chkWholeMonday.setChecked(false);
                }
            }
        });

        chkWholeTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CompoundButton) view).isChecked()) {
                    chkWholeTuesday.setChecked(true);
                } else {
                    TvTuesdayTime.setVisibility(View.GONE);
                    chkWholeTuesday.setChecked(false);
                }
            }
        });

        chkWholeWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CompoundButton) view).isChecked()) {
                    chkWholeWednesday.setChecked(true);
                } else {
                    TvWednesdayTime.setVisibility(View.GONE);
                    chkWholeWednesday.setChecked(false);
                }
            }
        });

        chkWholeThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CompoundButton) view).isChecked()) {
                    chkWholeThursday.setChecked(true);
                } else {
                    TvThursdayTime.setVisibility(View.GONE);
                    chkWholeThursday.setChecked(false);
                }
            }
        });

        chkWholeFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CompoundButton) view).isChecked()) {
                    chkWholeFriday.setChecked(true);
                } else {
                    TvFridayTime.setVisibility(View.GONE);
                    chkWholeFriday.setChecked(false);
                }
            }
        });

        chkWholeSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CompoundButton) view).isChecked()) {
                    chkWholeSaturday.setChecked(true);
                } else {
                    TvSaturdayTime.setVisibility(View.GONE);
                    chkWholeSaturday.setChecked(false);
                }
            }
        });
    }

    public void initObects() {
        hashMapSunday = new HashMap<>();
        hashMapMonday = new HashMap<>();
        hashMapTuesday = new HashMap<>();
        hashMapWednessday = new HashMap<>();
        hashMapThursday = new HashMap<>();
        hashMapFriday = new HashMap<>();
        hashMapSaturday = new HashMap<>();

    }

    public void initSessionVariables() {
        sessionManagerRegistration = new SessionManagerRegistration(RegisterPageThirdNew.this);
        sessionManagerRegistrationAvailability = new SessionManagerRegistration(RegisterPageThirdNew.this, "availabilitySession");
        hashMapSunday = sessionManagerRegistrationAvailability.getDetailsSunDay();
        hashMapMonday = sessionManagerRegistrationAvailability.getDetailsMonDay();
        hashMapTuesday = sessionManagerRegistrationAvailability.getDetailsTueDay();
        hashMapWednessday = sessionManagerRegistrationAvailability.getDetailsWedDay();
        hashMapThursday = sessionManagerRegistrationAvailability.getDetailsThurDay();
        hashMapFriday = sessionManagerRegistrationAvailability.getDetailsFriDay();
        hashMapSaturday = sessionManagerRegistrationAvailability.getDetailsSatDay();
    }

    public String validateHashMapDays(String stringDays) {
        String stringResult = "";
        if (stringDays != null && !stringDays.equals("")) {
            stringResult = "Success";
        } else {

        }
        return stringResult;
    }

/*
    public JSONObject getJsonObjectResult(String stringDays, String stringFromTime, String stringToTime) throws JSONException {

        JSONObject jsonObjectTimes = new JSONObject();
        jsonObjectTimes.put("from", stringFromTime);
        jsonObjectTimes.put("to", stringToTime);

        JSONObject jsonObjectDays = new JSONObject();
        jsonObjectDays.put("day", stringDays);
        jsonObjectDays.put("hour", jsonObjectTimes);

        return jsonObjectDays;
    }
*/

    public JSONObject getJsonObjectResult(String stringDays, String stringFromTime, String stringToTime) throws JSONException {

        JSONObject jsonObjectDays = new JSONObject();
        jsonObjectDays.put("day", stringDays);
        jsonObjectDays.put("from", stringFromTime);
        jsonObjectDays.put("to", stringToTime);
        jsonObjectDays.put("startTime", stringFromTime);
        jsonObjectDays.put("endTime", stringToTime);

        if (chkMonday.isChecked()) {
            jsonObjectDays.put("selected", 1);
        } else {
            jsonObjectDays.put("selected", 0);
        }

        if (chkWholeMonday.isChecked()) {
            jsonObjectDays.put("wholeday", 1);
        } else {
            jsonObjectDays.put("wholeday", 0);
        }

        return jsonObjectDays;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_header_back_layout:
                onBackPressed();
                finish();
                break;
            case R.id.save_LL:
                drawer_layout.closeDrawer(register_avail_drawer);
                break;
            case R.id.main_RL:
                main_RL.setEnabled(false);
                break;

            case R.id.continue_button_LL: {
                initSessionVariables();
                jsonArrayWholeJsonArray = new JSONArray();

                if (validateHashMapDays(hashMapSunday.get(sessionManagerRegistrationAvailability.KEY_SUN_DAY)).equals("Success")) {
                    try {
                        jsonArrayWholeJsonArray.put(getJsonObjectResult(hashMapSunday.get(sessionManagerRegistrationAvailability.KEY_SUN_DAY), hashMapSunday.get(sessionManagerRegistrationAvailability.KEY_SUN_FROM_TIME), hashMapSunday.get(sessionManagerRegistrationAvailability.KEY_SUN_TO_TIME)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (validateHashMapDays(hashMapMonday.get(sessionManagerRegistrationAvailability.KEY_MON_DAY)).equals("Success")) {
                    try {
                        jsonArrayWholeJsonArray.put(getJsonObjectResult(hashMapMonday.get(sessionManagerRegistrationAvailability.KEY_MON_DAY), hashMapMonday.get(sessionManagerRegistrationAvailability.KEY_MON_FROM_TIME), hashMapMonday.get(sessionManagerRegistrationAvailability.KEY_MON_TO_TIME)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (validateHashMapDays(hashMapTuesday.get(sessionManagerRegistrationAvailability.KEY_TUE_DAY)).equals("Success")) {
                    try {
                        jsonArrayWholeJsonArray.put(getJsonObjectResult(hashMapTuesday.get(sessionManagerRegistrationAvailability.KEY_TUE_DAY), hashMapTuesday.get(sessionManagerRegistrationAvailability.KEY_TUE_FROM_TIME), hashMapTuesday.get(sessionManagerRegistrationAvailability.KEY_TUE_TO_TIME)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (validateHashMapDays(hashMapWednessday.get(sessionManagerRegistrationAvailability.KEY_WED_DAY)).equals("Success")) {
                    try {
                        jsonArrayWholeJsonArray.put(getJsonObjectResult(hashMapWednessday.get(sessionManagerRegistrationAvailability.KEY_WED_DAY), hashMapWednessday.get(sessionManagerRegistrationAvailability.KEY_WED_FROM_TIME), hashMapWednessday.get(sessionManagerRegistrationAvailability.KEY_WED_TO_TIME)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (validateHashMapDays(hashMapThursday.get(sessionManagerRegistrationAvailability.KEY_THUR_DAY)).equals("Success")) {
                    try {
                        jsonArrayWholeJsonArray.put(getJsonObjectResult(hashMapThursday.get(sessionManagerRegistrationAvailability.KEY_THUR_DAY), hashMapThursday.get(sessionManagerRegistrationAvailability.KEY_THUR_FROM_TIME), hashMapThursday.get(sessionManagerRegistrationAvailability.KEY_THUR_TO_TIME)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (validateHashMapDays(hashMapFriday.get(sessionManagerRegistrationAvailability.KEY_FRI_DAY)).equals("Success")) {
                    try {
                        jsonArrayWholeJsonArray.put(getJsonObjectResult(hashMapFriday.get(sessionManagerRegistrationAvailability.KEY_FRI_DAY), hashMapFriday.get(sessionManagerRegistrationAvailability.KEY_FRI_FROM_TIME), hashMapFriday.get(sessionManagerRegistrationAvailability.KEY_FRI_TO_TIME)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (validateHashMapDays(hashMapSaturday.get(sessionManagerRegistrationAvailability.KEY_SAT_DAY)).equals("Success")) {
                    try {
                        jsonArrayWholeJsonArray.put(getJsonObjectResult(hashMapSaturday.get(sessionManagerRegistrationAvailability.KEY_SAT_DAY), hashMapSaturday.get(sessionManagerRegistrationAvailability.KEY_SAT_FROM_TIME), hashMapSaturday.get(sessionManagerRegistrationAvailability.KEY_SAT_TO_TIME)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (jsonArrayWholeJsonArray.length() > 0) {
                    sessionManagerRegistrationAvailability.setAvailablityJsonArray(jsonArrayWholeJsonArray);
                    ContinueRegistrationRequest();
                    System.out.println("jsonArrayWholeJsonArray" + jsonArrayWholeJsonArray);
                } else {
                    Toast.makeText(getApplicationContext(), "Please Select Availability days", Toast.LENGTH_LONG).show();
                }

                break;
            }
        }
    }

    private void QuestionAnswerRequest(String questionAnswerUrl) {
        myLoadingDialog = new PkLoadingDialog(RegisterPageThirdNew.this);
        myLoadingDialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("tasker", "question");

        System.out.println("QuestionAnswerRequest jsonParams" + jsonParams);

        ExperianceList = new ArrayList<Experiancepojo>();
        ServiceRequest mservicerequest = new ServiceRequest(RegisterPageThirdNew.this);
        mservicerequest.makeServiceRequest(questionAnswerUrl, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("QuestionAnswerRequest response" + response);

                String Str_Status = "";
                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_Status = jobject.getString("status");
                    if (Str_Status.equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jobject.getJSONArray("response");
                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject listObject = jsonArray.getJSONObject(i);
                                Experiancepojo pojo = new Experiancepojo();
                                pojo.setQuestion(listObject.getString("question"));
                                pojo.setId(listObject.getString("_id"));
                                ExperianceList.add(pojo);
                            }
                        } else {
                            ExperianceList.clear();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    myLoadingDialog.dismiss();
                }
                if (Str_Status.equalsIgnoreCase("1")) {
                    expAdapter = new ExperianceAdapter(RegisterPageThirdNew.this, ExperianceList, experiance_listview);
                    experiance_listview.setAdapter(expAdapter);
                    if (ExperianceList.size() > 0) {
                        setListViewHeightBasedOnChildren(experiance_listview);
                        expAdapter.notifyDataSetChanged();
                    }
                }
                myLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                myLoadingDialog.dismiss();
            }
        });
    }

    private void ContinueRegistrationRequest() {

        myLoadingDialog = new PkLoadingDialog(RegisterPageThirdNew.this);
        myLoadingDialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();

 /*       for (int i = 0; i < avail_pojoArraylist.size(); i++) {
            jsonParams.put("working_days[" + i + "][day]", avail_pojoArraylist.get(i).getDay_name());
            jsonParams.put("working_days[" + i + "][hour][morning]", "" + avail_pojoArraylist.get(i).isMorning());
            jsonParams.put("working_days[" + i + "][hour][afternoon]", "" + avail_pojoArraylist.get(i).isAfternoon());
            jsonParams.put("working_days[" + i + "][hour][evening]", "" + avail_pojoArraylist.get(i).isEvening());
        }*/

        // jsonParams.put("working_days", String.valueOf(sessionManagerRegistrationAvailability.getAvailabilityJsonArray()));
        jsonParams.put("working_days", String.valueOf(jsonArrayWholeJsonArray));

        expAdapter = new ExperianceAdapter(RegisterPageThirdNew.this, ExperianceList, experiance_listview);
        questions_list = expAdapter.getAnswer();

        JSONArray jsonArray1 = new JSONArray();
        ArrayList<String> finalArayAnserlist = new ArrayList<String>();
        sessionExperianceList = new ArrayList<Experiancepojo>();
        for (int i = 0; i <= ExperianceList.size(); i++) {
            try {
                Experiancepojo experiancepojo = new Experiancepojo();
                experiancepojo.setId(ExperianceList.get(i).getId());
                experiancepojo.setAnswer(questions_list.get(i));
                sessionExperianceList.add(experiancepojo);

                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("question", ExperianceList.get(i).getId());
                jsonObject1.put("answer", questions_list.get(i));
                jsonArray1.put(jsonObject1);

            } catch (IndexOutOfBoundsException ie) {
                ie.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        jsonParams.put("profile_details", jsonArray1.toString());

        System.out.println("ContinueRegistrationRequest jsonParams" + jsonParams);

        ServiceRequest myRequest = new ServiceRequest(RegisterPageThirdNew.this);
        myRequest.makeServiceRequest(ServiceConstant.Register_Form3, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {

                System.out.println("ContinueRegistrationRequest response" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        sessionManager.putAvailDays(avail_pojoArraylist);
                        if (sessionExperianceList.size() > 0) {
                            sessionManager.putExperianceList(sessionExperianceList);
                        }

                        Intent intent = new Intent(RegisterPageThirdNew.this, RegisterPageFourth.class);
                        startActivity(intent);
                        System.out.println(sessionManager.getAvailDays().toString() + " session getAvailDays string");
                        System.out.println(sessionManager.getExperianceList().toString() + " session experiance string");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                myLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                myLoadingDialog.dismiss();
            }
        });
    }

/*
    private void TimePicker(final TextView btn_from_to, final TextView textView) {

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog timePickerDialog, int hourOfDay, int minute, int i2) {
                        boolean isPM = (hourOfDay >= 12);
                        btn_from_to.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                    }
                }, mHour, mMinute, false);
        tpd.setMinTime(06, 0, 0);
        tpd.setMaxTime(22, 0, 0);
        tpd.show(getFragmentManager(), "TimePickerDialog");

    }
*/

    private void TimePicker(final TextView btn_from_to, final TextView textView) {

//        final Calendar c = Calendar.getInstance();
//        mHour = c.get(Calendar.HOUR_OF_DAY);
//        mMinute = c.get(Calendar.MINUTE);
//        TimePickerDialog tpd = TimePickerDialog.newInstance(
//                new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePickerDialog timePickerDialog, int hourOfDay, int minute, int i2) {
//                        boolean isPM = (hourOfDay >= 12);
//                        btn_from_to.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
//                    }
//                }, mHour, mMinute, false);
//        tpd.setMinTime(06, 0, 0);
//        tpd.setMaxTime(22, 0, 0);
//        tpd.show(getSupportFragmentManager(), "TimePickerDialog");

    }


    public void DialogTimepicker(final TextView textView, final TextView btn_days, final String str_day_title) {

        View view = View.inflate(RegisterPageThirdNew.this, R.layout.custom_timepicker, null);
        final Dialog dialog = new Dialog(RegisterPageThirdNew.this);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btn_from = (TextView) dialog.findViewById(R.id.handy_register_customlayout_timepicker_edttxt_from);
        btn_to = (TextView) dialog.findViewById(R.id.handy_register_customlayout_timepicker_edttxt_to);
        btn_submit = (TextView) dialog.findViewById(R.id.handy_register_service_availability_button_continue);
        ll_btn_cancel = (LinearLayout) dialog.findViewById(R.id.ll_btn_cancel);

        final TextView txtview_title_day = (TextView) dialog.findViewById(R.id.handy_register_customlayout_timepickertitlebar_txtview_hint_register);
        txtview_title_day.setText(str_day_title);

        ll_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stringDays = "";
                stringDays = txtview_title_day.getText().toString();

                if (stringDays.equals("Sunday")) {
                    dialog.dismiss();
                    chkSunday.setChecked(false);
                }

                if (stringDays.equals("Monday")) {
                    dialog.dismiss();
                    chkMonday.setChecked(false);
                }

                if (stringDays.equals("Tuesday")) {
                    dialog.dismiss();
                    chkTuesday.setChecked(false);
                }

                if (stringDays.equals("Wednesday")) {
                    dialog.dismiss();
                    chkWednesday.setChecked(false);
                }

                if (stringDays.equals("Thursday")) {
                    dialog.dismiss();
                    chkThursday.setChecked(false);
                }

                if (stringDays.equals("Friday")) {
                    dialog.dismiss();
                    chkFriday.setChecked(false);
                }

                if (stringDays.equals("Saturday")) {
                    dialog.dismiss();
                    chkSaturday.setChecked(false);
                }
            }
        });

        btn_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePicker(btn_from, textView);

            }
        });

        btn_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePicker(btn_to, textView);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (doValidation()) {

                    if (!btn_from.getText().toString().equals("") && !btn_to.getText().toString().equals("")) {
                        textView.setText(btn_from.getText().toString() + " - " + btn_to.getText().toString());
                        dialog.dismiss();

                        String stringDays = "";
                        stringDays = txtview_title_day.getText().toString();
                        if (stringDays.equals("Sunday")) {
                            TvSundayTime.setVisibility(View.VISIBLE);
                            sessionManagerRegistrationAvailability.setDetailsSunDay(stringDays, btn_from.getText().toString(), btn_to.getText().toString());
                        } else if (stringDays.equals("Monday")) {
                            TvMondayTime.setVisibility(View.VISIBLE);
                            sessionManagerRegistrationAvailability.setDetailsMonDay(stringDays, btn_from.getText().toString(), btn_to.getText().toString());
                        } else if (stringDays.equals("Tuesday")) {
                            TvTuesdayTime.setVisibility(View.VISIBLE);
                            sessionManagerRegistrationAvailability.setDetailsTueDay(stringDays, btn_from.getText().toString(), btn_to.getText().toString());
                        } else if (stringDays.equals("Wednesday")) {
                            TvWednesdayTime.setVisibility(View.VISIBLE);
                            sessionManagerRegistrationAvailability.setDetailsWedDay(stringDays, btn_from.getText().toString(), btn_to.getText().toString());
                        } else if (stringDays.equals("Thursday")) {
                            TvThursdayTime.setVisibility(View.VISIBLE);
                            sessionManagerRegistrationAvailability.setDetailsThurDay(stringDays, btn_from.getText().toString(), btn_to.getText().toString());
                        } else if (stringDays.equals("Friday")) {
                            TvFridayTime.setVisibility(View.VISIBLE);
                            sessionManagerRegistrationAvailability.setDetailsFriDay(stringDays, btn_from.getText().toString(), btn_to.getText().toString());
                        } else if (stringDays.equals("Saturday")) {
                            TvSaturdayTime.setVisibility(View.VISIBLE);
                            sessionManagerRegistrationAvailability.setDetailsSatDay(stringDays, btn_from.getText().toString(), btn_to.getText().toString());
                        }
                    }
                } else {
                    btn_from.setError("Please Select From Date");
                    btn_to.setError("Please Select TO Date");
                }
            }
        });

        dialog.show();
    }

    public boolean doValidation() {

        if ((btn_from.getText().toString().isEmpty()) && (btn_to.getText().toString().isEmpty())) {
            Toast.makeText(RegisterPageThirdNew.this, "Both fields are empty... Please choose different times", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((btn_from.getText().toString()).equals(btn_to.getText().toString())) {
            btn_to.setError("Both times are equal... Please choose different times");
            Toast.makeText(RegisterPageThirdNew.this, "Both times are equal... Please choose different times", Toast.LENGTH_SHORT).show();
            btn_to.setText("");
            return false;
        }

        return true;
    }

}
