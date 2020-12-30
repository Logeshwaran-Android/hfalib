package com.hoperlady.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hoperlady.Pojo.AvailabileArrayPojo;
import com.hoperlady.Pojo.RegistrastionAvailabilityChildPojo;
import com.hoperlady.Pojo.RegistrastionAvailabilityPojo;
import com.hoperlady.R;
import com.hoperlady.adapter.RegistrationAvailabilityAdapter;
import com.hoperlady.hockeyapp.ActivityHockeyApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class RegisterPageSecond extends ActivityHockeyApp {

    private String sJSONresponse = "";
    private RecyclerView availabilityDaysList;
    private ExpandableListView availabilityDaysExpandList;
    private EditText worklocation_ET;
    private Button continueAvailableBTN;
    private RegistrationAvailabilityAdapter aAdapter;

    private ArrayList<RegistrastionAvailabilityPojo> daysName = new ArrayList<RegistrastionAvailabilityPojo>();
    private ArrayList<RegistrastionAvailabilityChildPojo> slotArrayList = new ArrayList<RegistrastionAvailabilityChildPojo>();
    private String[] daysNameList;
    private int ParentIndexValue = 0;
    SessionManagerRegistration sessionManagerRegistration;

    private int placeSearch_request_code = 200;
    private String SselectedLatitude = "", SselectedLongitude = "", Selected_Location = "", selected_zipcode = "",
            selected_country = "", selected_state = "", selected_city = "", selected_line2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_second_layout);

        Intent intent = getIntent();
        sJSONresponse = intent.getStringExtra("response");

        initialization();

    }

    private void initialization() {
        daysNameList = new String[]{getResources().getString(R.string.sunday),
                getResources().getString(R.string.monday),
                getResources().getString(R.string.tuesday),
                getResources().getString(R.string.wednesday),
                getResources().getString(R.string.thursday),
                getResources().getString(R.string.friday),
                getResources().getString(R.string.saturday)};

        availabilityDaysList = (RecyclerView) findViewById(R.id.availabilityDaysList);
        availabilityDaysExpandList = (ExpandableListView) findViewById(R.id.availabilityDaysExpandList);
        continueAvailableBTN = (Button) findViewById(R.id.continueAvailableBTN);
        worklocation_ET = (EditText) findViewById(R.id.worklocation_ET);

        sessionManagerRegistration = new SessionManagerRegistration(this);

        try {
            JSONObject jsonObject = new JSONObject(sJSONresponse);
            JSONArray jsonWorkingDays = jsonObject.getJSONArray("working_days");
            JSONObject jsonWD = jsonWorkingDays.getJSONObject(0);
            JSONArray jsonSlot = jsonWD.getJSONArray("slot");
            slotArrayList.clear();
            slotArrayList = new ArrayList<>();
            ParentIndexValue = 0;
            for (String aDaysNameList : daysNameList) {
                for (int j = 0; j < jsonSlot.length(); j++) {
                    JSONObject object = jsonSlot.getJSONObject(j);
                    RegistrastionAvailabilityChildPojo childPojo = new RegistrastionAvailabilityChildPojo();
                    childPojo.setSlot(object.getString("slot"));
                    childPojo.setTime(object.getString("time"));
                    childPojo.setSelected(object.getBoolean("selected"));
                    childPojo.setDay(aDaysNameList);
                    childPojo.setParentIndex(ParentIndexValue);
                    slotArrayList.add(childPojo);
                    ParentIndexValue++;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        daysName = new ArrayList<RegistrastionAvailabilityPojo>();
        for (int i = 0; i < daysNameList.length; i++) {
            RegistrastionAvailabilityPojo pojo = new RegistrastionAvailabilityPojo();
            pojo.setDayNames(daysNameList[i]);
            pojo.setPojoArrayList(slotArrayList);
            daysName.add(i, pojo);
        }

        AvailabileArrayPojo mPojo = new AvailabileArrayPojo();
        mPojo.setPojoArrayList(slotArrayList);
        sessionManagerRegistration.setAvailablityDays(mPojo);

        System.out.println("sJSONresponse-----------------" + sJSONresponse);

        aAdapter = new RegistrationAvailabilityAdapter(RegisterPageSecond.this, daysNameList, slotArrayList, new RegistrationAvailabilityAdapter.Refresh() {
            @Override
            public void Update(int Position, RegistrastionAvailabilityChildPojo pojo) {
                slotArrayList.set(Position, pojo);
                AvailabileArrayPojo mPojo = new AvailabileArrayPojo();
                mPojo.setPojoArrayList(slotArrayList);
                sessionManagerRegistration.setAvailablityDays(mPojo);
            }
        });
        availabilityDaysExpandList.setAdapter(aAdapter);
        setListViewHeight(availabilityDaysExpandList, slotArrayList.size());

        availabilityDaysExpandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    availabilityDaysExpandList.collapseGroup(previousGroup);
                previousGroup = groupPosition;

                setListViewHeight(availabilityDaysExpandList, groupPosition);
            }
        });

        worklocation_ET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search_intent = new Intent(RegisterPageSecond.this, LocationSearch.class);
                startActivityForResult(search_intent, placeSearch_request_code);
            }
        });

        continueAvailableBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String availabileStringPojo = sessionManagerRegistration.getAvailablityDays();
                AvailabileArrayPojo mPojo = gson.fromJson(availabileStringPojo, AvailabileArrayPojo.class);
                System.out.println("available----------" + gson.fromJson(availabileStringPojo, AvailabileArrayPojo.class));
                ArrayList<RegistrastionAvailabilityChildPojo> pojoArrayList = mPojo.getPojoArrayList();

                System.out.println("available----------" + pojoArrayList);

                if (worklocation_ET.getText().toString().length() == 0) {
                    Toast.makeText(RegisterPageSecond.this, getResources().getString(R.string.work_location_empty_txt), Toast.LENGTH_SHORT).show();
                } else {
                    Intent RegisterPageFirst = new Intent(RegisterPageSecond.this, RegisterPageSecond.class);
                    RegisterPageFirst.putExtra("response", sJSONresponse);
                    startActivity(RegisterPageFirst);
                }
            }
        });


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

        }
    }

    public static void setExpandableListViewHeightBasedOnChildren(ExpandableListView listView,
                                                                  int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();
            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    private void setListViewHeight(ExpandableListView listView, int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
                //Add Divider Height
                totalHeight += listView.getDividerHeight() * (listAdapter.getChildrenCount(i) - 1);
            }
        }
        //Add Divider Height
        totalHeight += listView.getDividerHeight() * (listAdapter.getGroupCount() - 1);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
