package com.hoperlady.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hoperlady.Pojo.AvailabileArrayPojo;
import com.hoperlady.Pojo.OldAvailabileArrayPojo;
import com.hoperlady.Pojo.RegistrastionAvailabilityChildPojo;
import com.hoperlady.Pojo.RegistrastionAvailabilityPojo;
import com.hoperlady.R;
import com.hoperlady.adapter.AvailabilityNewCustomPageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RegiterPageAvailablilityActivity extends AppCompatActivity {

    private EditText worklocation_ET;
    private Button continueAvailableBTN;
    private TextView wholeDayBTN;
    private CardView wholeDayLL;
    private ImageView wholeDayCB;
    private RecyclerView availabilityDaysList;
    private AvailabilityNewCustomPageAdapter customAdapter;
    private String sJSONresponse = "";
    boolean isWholeDay = false;
    boolean isWholeDay1 = false;
    private ArrayList<RegistrastionAvailabilityPojo> daysArrayList = new ArrayList<RegistrastionAvailabilityPojo>();
    private ArrayList<RegistrastionAvailabilityChildPojo> slotArrayList = new ArrayList<RegistrastionAvailabilityChildPojo>();

    private int placeSearch_request_code = 200;
    private String SselectedLatitude = "", SselectedLongitude = "", Selected_Location = "", selected_zipcode = "",
            selected_country = "", selected_state = "", selected_city = "", selected_line2 = "";

    RelativeLayout backRL;
    private SessionManagerRegistration sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiter_page_availablility);
        initialization();
    }

    private void initialization() {
        backRL = (RelativeLayout) findViewById(R.id.register_header_back_layout);
        worklocation_ET = (EditText) findViewById(R.id.worklocation_ET);
        continueAvailableBTN = (Button) findViewById(R.id.continueAvailableBTN);
        wholeDayBTN = (TextView) findViewById(R.id.wholeDayBTN);
        wholeDayLL = (CardView) findViewById(R.id.wholeDayLL);
        wholeDayCB = (ImageView) findViewById(R.id.wholeDayCB);
        availabilityDaysList = (RecyclerView) findViewById(R.id.availabilityDaysList);
        availabilityDaysList.setHasFixedSize(true);
        availabilityDaysList.setLayoutManager(new LinearLayoutManager(this));

        sessionManager = new SessionManagerRegistration(RegiterPageAvailablilityActivity.this);

        Intent intent = getIntent();
        sJSONresponse = intent.getStringExtra("response");

        worklocation_ET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search_intent = new Intent(RegiterPageAvailablilityActivity.this, LocationSearch.class);
                startActivityForResult(search_intent, placeSearch_request_code);
            }
        });

//        wholeDayBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (int i = 0; i < daysArrayList.size(); i++) {
//                    daysArrayList.get(i).setWholeDayChoose(true);
//                }
//                customAdapter.notifyDataSetChanged();
//            }
//        });

        wholeDayLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isWholeDay1) {
                    for (int i = 0; i < daysArrayList.size(); i++) {
                        daysArrayList.get(i).setWholeDayChoose(false);
                    }
                    OldAvailabileArrayPojo pojo = new OldAvailabileArrayPojo();
                    pojo.setPojoArrayList(daysArrayList);
                    sessionManager.setOldAvailablityDays(pojo);

                    isWholeDay1 = false;
                    wholeDayCB.setImageDrawable(getResources().getDrawable(R.drawable.wholday_uncheck));
                } else {
                    for (int i = 0; i < daysArrayList.size(); i++) {
                        daysArrayList.get(i).setWholeDayChoose(true);
                    }
                    OldAvailabileArrayPojo pojo = new OldAvailabileArrayPojo();
                    pojo.setPojoArrayList(daysArrayList);
                    sessionManager.setOldAvailablityDays(pojo);
                    isWholeDay1 = true;
                    wholeDayCB.setImageDrawable(getResources().getDrawable(R.drawable.merge_tick_and_circle));
                }
                customAdapter.notifyDataSetChanged();
            }
        });

//        try {
//            JSONObject jResponse = new JSONObject(sJSONresponse);
//            if (jResponse.has("availability_address")) {
//                String availability_address = jResponse.getString("availability_address");
//                String location_lat = jResponse.getString("location_lat");
//                String location_lng = jResponse.getString("location_lng");
//                worklocation_ET.setText(availability_address);
//                SselectedLatitude = location_lat;
//                SselectedLongitude = location_lng;
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        continueAvailableBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OldAvailabileArrayPojo pojo = new OldAvailabileArrayPojo();
                pojo.setPojoArrayList(daysArrayList);
                sessionManager.setOldAvailablityDays(pojo);

                if (sessionManager.getDocumentStatus().equalsIgnoreCase("1")) {
                    Intent intent = new Intent(RegiterPageAvailablilityActivity.this, RegisterLicenceUploadActivity.class);
                    intent.putExtra("response", sJSONresponse);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(RegiterPageAvailablilityActivity.this, RegisterPageFourth.class);
                    intent.putExtra("response", sJSONresponse);
                    startActivity(intent);
                }
            }
        });

        backRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {
            Gson gson = new Gson();
            String availabileStringPojo = sessionManager.getOldAvailablityDays();
            if (!availabileStringPojo.equals("")) {
                OldAvailabileArrayPojo mPojo = gson.fromJson(availabileStringPojo, OldAvailabileArrayPojo.class);
                System.out.println("available----------" + gson.fromJson(availabileStringPojo, AvailabileArrayPojo.class));
                daysArrayList = mPojo.getPojoArrayList();
                for (int i = 0; i < daysArrayList.size(); i++) {
                    if (!daysArrayList.get(i).isWholeDayChoose()) {
                        isWholeDay = true;
                    }
                }
            } else {

                try {
                    JSONObject jsonObject = new JSONObject(sJSONresponse);
                    JSONArray jsonWorkingDays = jsonObject.getJSONArray("working_days");
                    daysArrayList = new ArrayList<>();
                    for (int i = 0; i < jsonWorkingDays.length(); i++) {
                        JSONObject jsonWD1 = jsonWorkingDays.getJSONObject(i);
                        RegistrastionAvailabilityPojo availabilityPojo1 = new RegistrastionAvailabilityPojo();
                        availabilityPojo1.setDayNames(jsonWD1.getString("day"));

                        JSONArray jsonSlot = jsonWD1.getJSONArray("slot");
                        slotArrayList = new ArrayList<>();
                        for (int j = 0; j < jsonSlot.length(); j++) {
                            JSONObject object = jsonSlot.getJSONObject(j);
                            RegistrastionAvailabilityChildPojo childPojo = new RegistrastionAvailabilityChildPojo();
                            childPojo.setSlot(object.getString("slot"));
                            childPojo.setTime(object.getString("time"));
                            childPojo.setSelected(object.getBoolean("selected"));
                            slotArrayList.add(childPojo);
                        }

                        availabilityPojo1.setPojoArrayList(slotArrayList);
                        availabilityPojo1.setSelectedDay(jsonWD1.getBoolean("selected"));
                        availabilityPojo1.setWholeDayChoose(jsonWD1.getBoolean("wholeday"));
                        if (!jsonWD1.getBoolean("wholeday")) {
                            isWholeDay = true;
                        }
                        daysArrayList.add(availabilityPojo1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isWholeDay) {
            isWholeDay1 = true;
            wholeDayCB.setImageDrawable(getResources().getDrawable(R.drawable.wholday_uncheck));
        } else {
            isWholeDay1 = false;
            wholeDayCB.setImageDrawable(getResources().getDrawable(R.drawable.merge_tick_and_circle));
        }

        customAdapter = new AvailabilityNewCustomPageAdapter(RegiterPageAvailablilityActivity.this, daysArrayList, new AvailabilityNewCustomPageAdapter.Refresh() {
            @Override
            public void Update(ArrayList<RegistrastionAvailabilityPojo> aAvailabilityList) {
                daysArrayList = aAvailabilityList;
                OldAvailabileArrayPojo pojo = new OldAvailabileArrayPojo();
                pojo.setPojoArrayList(daysArrayList);
                sessionManager.setOldAvailablityDays(pojo);
                customAdapter.notifyDataSetChanged();

                for (int i = 0; i < daysArrayList.size(); i++) {
                    if (!daysArrayList.get(i).isWholeDayChoose()) {
                        isWholeDay = true;
                    }
                }

                if (isWholeDay) {
                    wholeDayCB.setImageDrawable(getResources().getDrawable(R.drawable.wholday_uncheck));
                } else {
                    wholeDayCB.setImageDrawable(getResources().getDrawable(R.drawable.merge_tick_and_circle));
                }
            }
        });
        availabilityDaysList.setAdapter(customAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == placeSearch_request_code && data != null)) {
            SselectedLatitude = data.getStringExtra("Selected_Latitude");
            SselectedLongitude = data.getStringExtra("Selected_Longitude");
            Selected_Location = data.getStringExtra("Selected_Location");

            selected_zipcode = data.getStringExtra("ZipCode");
            selected_country = data.getStringExtra("country");
            selected_city = data.getStringExtra("City");
            selected_line2 = data.getStringExtra("Location");
            selected_state = data.getStringExtra("State");

            worklocation_ET.setText(Selected_Location);

            try {
                JSONObject jResponse = new JSONObject(sJSONresponse);
                jResponse.put("availability_address", worklocation_ET.getText().toString());
                jResponse.put("location_lat", SselectedLatitude);
                jResponse.put("location_lng", SselectedLongitude);
                sessionManager.setPersonalDetails(String.valueOf(jResponse));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}
