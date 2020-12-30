package com.hoperlady.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperlady.Pojo.MyProfileAvailabilityPojo;
import com.hoperlady.Pojo.MyProfileAvailabilityShowPojo;
import com.hoperlady.Pojo.NewAvailabilityPojo;
import com.hoperlady.Pojo.ProviderCategory;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.adapter.AvailabilityNewCustomShowAdapter;
import com.hoperlady.adapter.NewAvailabilityAdapter;
import com.hoperlady.adapter.ProviderCategoryAdapter;
import com.hoperlady.app.EditProfilePage;

import com.hoperlady.hockeyapp.FragmentHockeyApp;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import core.Dialog.LoadingDialog;
import core.Dialog.PkDialog;
import core.Volley.ServiceRequest;
import core.Widgets.CircularImageView;
import core.service.ServiceConstant;
import core.socket.SocketHandler;

/**
 * Created by user88 on 1/6/2016.
 */
public class MyProfileFragment extends FragmentHockeyApp implements ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupCollapseListener {

    private static CircularImageView profile_img;
    private static Context context;
    private static String profile_pic = "";
    ListView list;
    NewAvailabilityAdapter availadapter;
    private Boolean isInternetPresent = false;
    private boolean show_progress_status = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private TextView Tv_profile_name, Tv_profile_email, Tv_profile_desigaination, Tv_profile_mobile_no, Tv_profile_bio, Tv_profile_address, Tv_profile_category;
    private RatingBar profile_rating;
    private RelativeLayout Rl_layout_edit_profile, Rl_layout_main, Rl_layout_profile_nointernet, Rl_layout_profile_bio, Rl_layout_profile_address, Rl_layout_category;
    private String provider_id = "";
    private LoadingDialog dialog;
    private String Str_provider_image = "";
    private ArrayList<NewAvailabilityPojo> availlist;
    private SocketHandler socketHandler;
    private RelativeLayout myExperienceLAY, myRadiusLAY, myWorkLocationLAY;
    private TextView myExperienceTXT, myWorkLocationTXT, myRadiusTXT;

    private TextView rating;

    private ListView cat_list;
    private ArrayList<String> listItems;
    private ArrayList<ProviderCategory> cat_list_item = new ArrayList<ProviderCategory>();
    private ProviderCategoryAdapter provider_adapter;
    private ScrollView main_scroll_layout;
    private RefreshReceiver finishReceiver;

    private ArrayList<MyProfileAvailabilityPojo> daysArrayList = new ArrayList<MyProfileAvailabilityPojo>();
    private ArrayList<MyProfileAvailabilityShowPojo> slotArrayList = new ArrayList<MyProfileAvailabilityShowPojo>();

    ExpandableListView availabity_EXPLV;
    RecyclerView myProfileAvailabilityRV;

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.load.editpage")) {
                ProfileRequest(getActivity(), ServiceConstant.PROFILEINFO_URL);

            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.new_tasker_profile, container, false);

        init(rootview);

        Rl_layout_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfilePage.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        return rootview;
    }

    private void init(View rootview) {
        cd = new ConnectionDetector(getActivity());
        session = new SessionManager(getActivity());
        context = getActivity();
        socketHandler = SocketHandler.getInstance(getActivity());
        availlist = new ArrayList<NewAvailabilityPojo>();
        HashMap<String, String> user = session.getUserDetails();
        provider_id = user.get(SessionManager.KEY_PROVIDERID);
        profile_pic = user.get(SessionManager.KEY_USERIMAGE);

        list = (ListView) rootview.findViewById(R.id.list);
        profile_img = (CircularImageView) rootview.findViewById(R.id.profile_user_img);
        Tv_profile_name = (TextView) rootview.findViewById(R.id.profile_username_Tv);
        // Tv_profile_desigaination = (TextView)rootview.findViewById(R.id.profile_desigination_Tv);
        Tv_profile_email = (TextView) rootview.findViewById(R.id.profile_email_Tv);
        Tv_profile_mobile_no = (TextView) rootview.findViewById(R.id.profile_mobile_Tv);
        Tv_profile_bio = (TextView) rootview.findViewById(R.id.profile_bio_Tv);
        Tv_profile_address = (TextView) rootview.findViewById(R.id.profile_address_Tv);
        Tv_profile_category = (TextView) rootview.findViewById(R.id.profile_category_Tv);
        profile_rating = (RatingBar) rootview.findViewById(R.id.user_ratting);
        Rl_layout_edit_profile = (RelativeLayout) rootview.findViewById(R.id.layout_edit_profile);
        Rl_layout_profile_nointernet = (RelativeLayout) rootview.findViewById(R.id.layout_profile_noInternet);
        Rl_layout_main = (RelativeLayout) rootview.findViewById(R.id.layout_profile_main);
        main_scroll_layout = (ScrollView) rootview.findViewById(R.id.main_scroll_layout);
        Rl_layout_profile_bio = (RelativeLayout) rootview.findViewById(R.id.profile_bio_layout);
        Rl_layout_profile_address = (RelativeLayout) rootview.findViewById(R.id.profile_address_layout);
        Rl_layout_category = (RelativeLayout) rootview.findViewById(R.id.profile_category_layout);
        myExperienceLAY = (RelativeLayout) rootview.findViewById(R.id.experience_layout);
        myWorkLocationLAY = (RelativeLayout) rootview.findViewById(R.id.profile_worklocation_layout);
        myRadiusLAY = (RelativeLayout) rootview.findViewById(R.id.profile_radius_layout);

        myExperienceTXT = (TextView) rootview.findViewById(R.id.profile_experience_Tv);
        myWorkLocationTXT = (TextView) rootview.findViewById(R.id.profile_worklocation_Tv);
        myRadiusTXT = (TextView) rootview.findViewById(R.id.profile_radius_Tv);
        rating = (TextView) rootview.findViewById(R.id.rating);
        cat_list = (ListView) rootview.findViewById(R.id.cat_list);
        listItems = new ArrayList<>();

        availabity_EXPLV = (ExpandableListView) rootview.findViewById(R.id.availabity_EXPLV);

        myProfileAvailabilityRV = (RecyclerView) rootview.findViewById(R.id.myProfileAvailabilityRV);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        myProfileAvailabilityRV.setLayoutManager(mLayoutManager);
        myProfileAvailabilityRV.setHasFixedSize(true);
        myProfileAvailabilityRV.setItemAnimator(new DefaultItemAnimator());

        Picasso.with(getActivity()).load(String.valueOf(profile_pic)).placeholder(R.drawable.nouserimg).into(profile_img);


        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

            Rl_layout_main.setVisibility(View.VISIBLE);
            Rl_layout_profile_nointernet.setVisibility(View.GONE);
            ProfileRequest(getActivity(), ServiceConstant.PROFILEINFO_URL);

        } else {
            Rl_layout_main.setVisibility(View.GONE);
            Rl_layout_profile_nointernet.setVisibility(View.VISIBLE);

        }

        finishReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.load.editpage");
        context.registerReceiver(finishReceiver, intentFilter);

    }


    public static void profileimgNotifyChange() {

        Picasso.with(context).load(String.valueOf(profile_pic)).placeholder(R.drawable.nouserimg).into(profile_img);
    }

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
        listView.requestLayout();
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) availabity_EXPLV.getLayoutParams();
        param.height = (availabity_EXPLV.getHeight());
        availabity_EXPLV.setLayoutParams(param);
        availabity_EXPLV.refreshDrawableState();
        main_scroll_layout.refreshDrawableState();
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) availabity_EXPLV.getLayoutParams();
        param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        availabity_EXPLV.setLayoutParams(param);
        availabity_EXPLV.refreshDrawableState();
        main_scroll_layout.refreshDrawableState();
    }

    private void ProfileRequest(Context mContext, String url) {

        dialog = new LoadingDialog(getActivity());
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
                        Str_addrress = "", Str_category = "", aExperienceStr = "", aWorkLocationStr = "", aRadiusStr = "", aDialcode = "";

                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_Status = jobject.getString("status");

                    if (Str_Status.equalsIgnoreCase("1")
                            || Str_Status.equalsIgnoreCase("2")
                            || Str_Status.equalsIgnoreCase("3")) {

                        listItems.clear();
                        cat_list_item.clear();

                        session.setTaskerVerification(Str_Status);

                        if (Str_Status.equalsIgnoreCase("3")) {
                            Intent s = new Intent();
                            s.setAction("com.admin.verification");
                            getActivity().sendBroadcast(s);
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

                        AvailabilityNewCustomShowAdapter customAdapter = new AvailabilityNewCustomShowAdapter(getActivity(), daysArrayList);
                        myProfileAvailabilityRV.setAdapter(customAdapter);


                        if (object.has("category_Details")) {

                            JSONArray cat_array = object.getJSONArray("category_Details");
                            if (cat_array.length() > 0) {

                                for (int i = 0; i < cat_array.length(); i++) {

                                    JSONObject obs = cat_array.getJSONObject(i);
                                    ProviderCategory pojo = new ProviderCategory();
                                    String cat_name = obs.getString("categoryname");
                                    String hour_amt = "";
                                    if (obs.getString("ratetypestatus").equals(1)) {
                                        hour_amt = obs.getString("hourlyrate");
                                    } else {
                                        hour_amt = obs.getString("hourlyrate") + "/hr";
                                    }

                                    pojo.setCategory_name(cat_name);
                                    pojo.setHourly_rate(hour_amt);
                                    cat_list_item.add(pojo);
                                }

                            }

                            for (int j = 0; j < cat_list_item.size(); j++) {

                                listItems.add(String.valueOf(j));
                            }

                            provider_adapter = new ProviderCategoryAdapter(getActivity(), listItems, cat_list_item);
                            cat_list.setAdapter(provider_adapter);


                            setListViewHeightBasedOnChildren(cat_list);

                        }

                    } else {
                        Str_response = jobject.getString("response");
                    }
                    if (Str_Status.equalsIgnoreCase("1") || Str_Status.equalsIgnoreCase("3")) {

                        Tv_profile_name.setText(Str_name);
                        rating.setText(Str_rating);
                        Tv_profile_mobile_no.setText(aDialcode + " " + Str_mobileno);
                        Tv_profile_category.setText(Html.fromHtml(Str_category + "<br/>"));
                        Tv_profile_email.setText(Str_email);
                        Tv_profile_address.setText(Str_addrress);
                        Picasso.with(getActivity()).load(String.valueOf(Str_provider_image)).placeholder(R.drawable.nouserimg).into(profile_img);
                        session.setUserImageUpdate(Str_provider_image);
                        session.setProviderName(Str_name);

//                        if (aRadiusStr.length() > 0) {
//                            myRadiusTXT.setText(aRadiusStr);
//                        }

                        if (aWorkLocationStr.length() > 0) {
                            myWorkLocationTXT.setText(aWorkLocationStr);
                        }
                        main_scroll_layout.setVisibility(View.VISIBLE);

                        Intent s = new Intent();
                        s.setAction("com.admin.verification");
                        getActivity().sendBroadcast(s);


                    } else {
                        Alert(getResources().getString(R.string.server_lable_header), Str_response);
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


    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(getActivity());
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.server_ok_lable_header), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(finishReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
