package com.hoperlady.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.hoperlady.Pojo.MyProfileAvailabilityPojo;
import com.hoperlady.Pojo.MyProfileAvailabilityShowPojo;
import com.hoperlady.Pojo.ProviderCategory;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.Utils.SessionManagerDB;
import com.hoperlady.adapter.AvailabilityNewCustomShowAdapter;
import com.hoperlady.adapter.MyCategoryAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import core.Dialog.LoadingDialog;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;

public class MyProfileActivity extends AppCompatActivity {

    private RecyclerView categoriesListRV, myProfileAvailabilityRV;
    private TextView taskerNameTV, taskerNameTitleTV, taskerLocationTV, ratingTV, taskerDescriptionAboutTitleTV,
            taskerDescriptionAboutTV, editTV, taskerCategiresTV;
    private CircularImageView innerCircleIv;
    private ImageView backImageV;

    private SessionManager session;
    private SessionManagerDB sessionManagerDB;
    private ArrayList<ProviderCategory> categoryList;
    private ArrayList<MyProfileAvailabilityPojo> daysArrayList = new ArrayList<MyProfileAvailabilityPojo>();
    private ArrayList<MyProfileAvailabilityShowPojo> slotArrayList = new ArrayList<MyProfileAvailabilityShowPojo>();
    private String provider_id = "";
    private ConnectionDetector cd;
    private RefreshReceiver receiver;

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.load.editpage")) {
                ProfileRequest(MyProfileActivity.this, ServiceConstant.PROFILEINFO_URL);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        initialization();
        initOnClick();
    }

    private void initialization() {
        categoriesListRV = (RecyclerView) findViewById(R.id.categoriesListRV);
        myProfileAvailabilityRV = (RecyclerView) findViewById(R.id.availabilityListRV);
        categoriesListRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoriesListRV.setHasFixedSize(false);
        categoriesListRV.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyProfileActivity.this);
        myProfileAvailabilityRV.setLayoutManager(mLayoutManager);
        myProfileAvailabilityRV.setHasFixedSize(false);
        myProfileAvailabilityRV.setItemAnimator(new DefaultItemAnimator());

        session = new SessionManager(MyProfileActivity.this);
        sessionManagerDB = new SessionManagerDB(MyProfileActivity.this);

        taskerNameTV = (TextView) findViewById(R.id.taskerNameTV);
        taskerNameTitleTV = (TextView) findViewById(R.id.taskerNameTitleTV);
        editTV = (TextView) findViewById(R.id.editTV);
        taskerLocationTV = (TextView) findViewById(R.id.taskerLocationTV);
        ratingTV = (TextView) findViewById(R.id.ratingTV);
        taskerDescriptionAboutTitleTV = findViewById(R.id.taskerDescriptionAboutTitleTV);
        taskerDescriptionAboutTV = findViewById(R.id.taskerDescriptionAboutTV);
        taskerCategiresTV = findViewById(R.id.taskerCategiresTV);
        innerCircleIv = (CircularImageView) findViewById(R.id.innerCircleIv);
        backImageV = (ImageView) findViewById(R.id.backImageV);
        cd = new ConnectionDetector(MyProfileActivity.this);

        HashMap<String, String> user = session.getUserDetails();
        provider_id = user.get(SessionManager.KEY_PROVIDERID);

        if (cd.isConnectingToInternet()) {
            ProfileRequest(MyProfileActivity.this, ServiceConstant.PROFILEINFO_URL);
        } else {
            Toast.makeText(MyProfileActivity.this, R.string.action_no_internet_message, Toast.LENGTH_SHORT).show();
        }

        if (cd.isConnectingToInternet()) {
            Appinfo(MyProfileActivity.this, ServiceConstant.App_Info);
        } else {
            Toast.makeText(MyProfileActivity.this, R.string.action_no_internet_message, Toast.LENGTH_SHORT).show();
        }

        receiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.load.editpage");
        registerReceiver(receiver, intentFilter);
    }

    private void initOnClick() {
        backImageV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        innerCircleIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        editTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, EditProfilePage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void ProfileRequest(Context mContext, String url) {

        final LoadingDialog dialog = new LoadingDialog(MyProfileActivity.this);
        dialog.setLoadingTitle(getResources().getString(R.string.action_gettinginfo));
        dialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", provider_id);
        System.out.println("ProfileRequest jsonParams" + jsonParams);

        ServiceRequest mservicerequest = new ServiceRequest(mContext);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                System.out.println("ProfileRequest response" + response);

                String Str_Status = "", Str_response = "", Str_name = "", Str_designation = "", Str_rating = "", Str_email = "", Str_mobileno = "", Str_bio = "",
                        Str_addrress = "", Str_category = "", aExperienceStr = "", aWorkLocationStr = "", aRadiusStr = "", aDialcode = "", Str_provider_image = "";

                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_Status = jobject.getString("status");

                    if (Str_Status.equalsIgnoreCase("1")
                            || Str_Status.equalsIgnoreCase("2")
                            || Str_Status.equalsIgnoreCase("3")) {

                        categoryList = new ArrayList<ProviderCategory>();

                        session.setTaskerVerification(Str_Status);

                        if (Str_Status.equalsIgnoreCase("3")) {
                            Intent s = new Intent();
                            s.setAction("com.admin.verification");
                            sendBroadcast(s);
                        }

                        JSONObject object = jobject.getJSONObject("response");

                        if (object.has("provider_name")) {
                            Str_name = object.getString("provider_name");
                        }
                        if (object.has("username")) {
                            Str_name = object.getString("provider_name");
                        }
                        if (object.has("designation")) {
                            Str_designation = object.getString("designation");
                        }
                        if (object.has("avg_review")) {
                            Str_rating = object.getString("avg_review");
                        }
                        if (object.has("email")) {
                            Str_email = object.getString("email");
                        }
                        if (object.has("mobile_number")) {
                            Str_mobileno = object.getString("mobile_number");
                        }
                        if (object.has("dial_code")) {
                            aDialcode = object.getString("dial_code");
                        }

                        boolean anwser = false;
                        if (object.has("about")) {
                            JSONArray about = object.getJSONArray("about");
                            StringBuilder desc = new StringBuilder(100);
                            if (about.length() > 0) {
                                for (int i = 0; i < about.length(); i++) {
                                    JSONObject aboutJsonObject = about.getJSONObject(i);
                                    if (aboutJsonObject.has("answer")) {
                                        desc.append(aboutJsonObject.getString("answer"));
                                        if (!aboutJsonObject.getString("answer").equals("")) {
                                            anwser = true;
                                            desc.append(". ");
                                        }
                                        taskerDescriptionAboutTV.setText(desc);
                                    }
                                }
                            }
                            if (!anwser) {
                                taskerDescriptionAboutTV.setVisibility(View.GONE);
                                taskerDescriptionAboutTitleTV.setVisibility(View.GONE);
                            } else {
                                taskerDescriptionAboutTV.setVisibility(View.VISIBLE);
                                taskerDescriptionAboutTitleTV.setVisibility(View.VISIBLE);
                            }
                        } else {
                            taskerDescriptionAboutTV.setVisibility(View.GONE);
                            taskerDescriptionAboutTitleTV.setVisibility(View.GONE);
                        }


                        if (object.has("bio")) {
                            Str_bio = object.getString("bio");
                        }
                        if (object.has("category")) {
                            Str_category = object.getString("category").replace("\\n", "<br/>");
                        }
                        if (object.has("image")) {
                            Str_provider_image = object.getString("image");
                        }

                        if (object.has("experience")) {
                            aExperienceStr = object.getString("experience");
                        }
                        if (object.has("Working_location")) {
                            aWorkLocationStr = object.getString("Working_location");
                        }
                        if (object.has("radius")) {
                            aRadiusStr = object.getString("radius");
                        }
                        if (object.has("address_str")) {
                            Str_addrress = object.getString("address_str");
                        }

                        daysArrayList = new ArrayList<MyProfileAvailabilityPojo>();
                        slotArrayList = new ArrayList<MyProfileAvailabilityShowPojo>();

                        JSONArray array1 = object.getJSONArray("availability_days");

                        for (int i = 0; i < array1.length(); i++) {
                            JSONObject jsonWD1 = array1.getJSONObject(i);
                            MyProfileAvailabilityPojo availabilityPojo1 = new MyProfileAvailabilityPojo();
                            availabilityPojo1.setDayNames(jsonWD1.getString("day"));

                            JSONArray jsonSlot = jsonWD1.getJSONArray("slot");
                            slotArrayList = new ArrayList<>();
                            if (jsonSlot.length() > 0) {
                                for (int j = 0; j < jsonSlot.length(); j++) {
                                    JSONObject objectSlot = jsonSlot.getJSONObject(j);
                                    MyProfileAvailabilityShowPojo childPojo = new MyProfileAvailabilityShowPojo();
                                    childPojo.setSlot(objectSlot.getString("slot"));
                                    childPojo.setTime(objectSlot.getString("time"));
                                    childPojo.setSelected(objectSlot.getString("selected"));
                                    slotArrayList.add(childPojo);
                                }
                            }
                            availabilityPojo1.setPojoArrayList(slotArrayList);
                            availabilityPojo1.setSelectedDay(jsonWD1.getString("selected"));
                            availabilityPojo1.setWholeDayChoose(jsonWD1.getString("wholeday"));
                            daysArrayList.add(availabilityPojo1);
                        }

                        AvailabilityNewCustomShowAdapter customAdapter = new AvailabilityNewCustomShowAdapter(MyProfileActivity.this, daysArrayList);
                        myProfileAvailabilityRV.setAdapter(customAdapter);


                        if (object.has("category_Details")) {
                            JSONArray cat_array = object.getJSONArray("category_Details");
                            if (cat_array.length() > 0) {
                                taskerCategiresTV.setVisibility(View.VISIBLE);
                                for (int i = 0; i < cat_array.length(); i++) {
                                    JSONObject obs = cat_array.getJSONObject(i);
                                    ProviderCategory pojo = new ProviderCategory();
                                    pojo.setCategory_name(obs.getString("categoryname"));
                                    pojo.setHourly_rate(obs.getString("hourlyrate"));
                                    pojo.setCategoryType(obs.getString("ratetypestatus"));
                                    pojo.setCategoryIcon(ServiceConstant.REgBASE_URL + obs.getString("icon"));
                                    categoryList.add(pojo);
                                }
                            } else {
                                taskerCategiresTV.setVisibility(View.GONE);
                            }
                            MyCategoryAdapter provider_adapter = new MyCategoryAdapter(MyProfileActivity.this, categoryList);
                            categoriesListRV.setAdapter(provider_adapter);
                        }

                    } else {
                        Str_response = jobject.getString("response");
                    }
                    if (Str_Status.equalsIgnoreCase("1")
                            || Str_Status.equalsIgnoreCase("2")
                            || Str_Status.equalsIgnoreCase("3")) {

                        taskerNameTV.setText(Str_name);
//                        taskerNameTitleTV.setText(Str_name);
                        if (Str_rating.equals("0")) {
                            ratingTV.setText(Str_rating);
                        } else {
                            ratingTV.setText(Double.parseDouble(Str_rating) + "");
                        }
//                        Tv_profile_mobile_no.setText(aDialcode + " " + Str_mobileno);
//                        Tv_profile_category.setText(Html.fromHtml(Str_category + "<br/>"));
//                        Tv_profile_email.setText(Str_email);
//                        Tv_profile_address.setText(Str_addrress);
                        Picasso.with(MyProfileActivity.this).load(String.valueOf(Str_provider_image)).placeholder(R.drawable.nouserimg).into(innerCircleIv);
                        session.setUserImageUpdate(Str_provider_image);
                        session.setProviderName(Str_name);

                        if (aWorkLocationStr.length() > 0) {
                            taskerLocationTV.setText(aWorkLocationStr);
                        }
                        Intent s = new Intent();
                        s.setAction("com.admin.verification");
                        sendBroadcast(s);
                    } else {
                        Toast.makeText(MyProfileActivity.this, Str_response, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    //----------------------------------------------------------------------App Info Url------------------------------------------------------
    private void Appinfo(Context mContext, String url) {
        ServiceRequest mservicerequest = new ServiceRequest(mContext);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, null, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                Log.e("loginresponse", response);

                System.out.println("response---------" + response);
                String Str_status = "", taskerAPI = "";

                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_status = jobject.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {
                        String Appmail = jobject.getString("email_address");
                        session.Setemailappinfo(Appmail);

                        JSONObject android_map_api = jobject.getJSONObject("android_map_api");
                        taskerAPI = android_map_api.getString("tasker");
                        sessionManagerDB.setAPIKey(taskerAPI);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onErrorListener() {

            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
