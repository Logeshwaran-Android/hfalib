package com.hoperlady.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.app.MyJobs;
import com.hoperlady.app.MyProfileActivity;
import com.hoperlady.app.NavigationDrawer;
import com.hoperlady.app.NewLeadsPage;
import com.hoperlady.app.WeekTaskBarChartActivity;
import com.hoperlady.hockeyapp.FragmentHockeyApp;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

import core.Dialog.LoadingDialog;
import core.Dialog.PkDialog;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;

public class ExpertHomePageFragment extends FragmentHockeyApp {

    private View myView;
    private CardView newLeadsCV, myJobsHomeCV, lastTaskCV, totalEarningsCV;

    ImageView profileIV;
    TextView ratingTV, providerNameTV, newLeadsCountTV, categoriesListTV, moreCategoriesTV,
            lastTaskCurrencyTV, lastTaskNumberTV, lastTaskDecimalTV,
            totalTaskCurrencyTV, totalTaskNumberTV, totalTaskDecimalTV,
            taskStartTimeTV, taskDurationTimeTV,
            totalStartTimeTV, totalDurationTimeTV,
            workLoacationTV, radiusTV, completedJobCoutTV;
    private String myProviderIdStr;
    private SessionManager mySession;
    private RelativeLayout myDrawerLAY;
    private SwipeRefreshLayout homePagwSwipereFresh;
    ConnectionDetector cd;
    private Receiver refreshReceiver;
    private TextView adminVerifyAlertTitleTv;
    private LoadingDialog mLoadingDialog;
    ToggleButton simpleToggleButton;
    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("com.package.refresh.experthomepage")) {
                if (cd.isConnectingToInternet()) {
                    postSetOnlineStatus(ServiceConstant.AVAILABILITY_URL, getContext(), 1);
                }
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.expert_home_page_layout, container, false);
        initializeFragment(myView);
        initOnClick();

        return myView;
    }

    private void initializeFragment(View myView) {
        newLeadsCV = (CardView) myView.findViewById(R.id.newLeadsCV);
        myJobsHomeCV = (CardView) myView.findViewById(R.id.myJobsHomeCV);
        lastTaskCV = (CardView) myView.findViewById(R.id.lastTaskCV);
        totalEarningsCV = (CardView) myView.findViewById(R.id.totalEarningsCV);
        simpleToggleButton = (ToggleButton)myView. findViewById(R.id.simpleToggleButton);
        profileIV = (ImageView) myView.findViewById(R.id.profileIV);
        ratingTV = (TextView) myView.findViewById(R.id.ratingTV);
        newLeadsCountTV = (TextView) myView.findViewById(R.id.newLeadsCountTV);
        providerNameTV = (TextView) myView.findViewById(R.id.providerNameTV);
        categoriesListTV = (TextView) myView.findViewById(R.id.categoriesListTV);
        moreCategoriesTV = (TextView) myView.findViewById(R.id.moreCategoriesTV);
        lastTaskCurrencyTV = (TextView) myView.findViewById(R.id.lastTaskCurrencyTV);
        lastTaskNumberTV = (TextView) myView.findViewById(R.id.lastTaskNumberTV);
        lastTaskDecimalTV = (TextView) myView.findViewById(R.id.lastTaskDecimalTV);
        totalTaskCurrencyTV = (TextView) myView.findViewById(R.id.totalTaskCurrencyTV);
        totalTaskNumberTV = (TextView) myView.findViewById(R.id.totalTaskNumberTV);
        totalTaskDecimalTV = (TextView) myView.findViewById(R.id.totalTaskDecimalTV);
        taskStartTimeTV = (TextView) myView.findViewById(R.id.taskStartTimeTV);
        taskDurationTimeTV = (TextView) myView.findViewById(R.id.taskDurationTimeTV);
        totalStartTimeTV = (TextView) myView.findViewById(R.id.totalStartTimeTV);
        totalDurationTimeTV = (TextView) myView.findViewById(R.id.totalDurationTimeTV);
        workLoacationTV = (TextView) myView.findViewById(R.id.workLoacationTV);
        radiusTV = (TextView) myView.findViewById(R.id.radiusTV);
        completedJobCoutTV = (TextView) myView.findViewById(R.id.completedJobCoutTV);
        adminVerifyAlertTitleTv = (TextView) myView.findViewById(R.id.adminVerifyAlertTitleTv);
        myDrawerLAY = (RelativeLayout) myView.findViewById(R.id.home_navigation_layout_icon);
        homePagwSwipereFresh = (SwipeRefreshLayout) myView.findViewById(R.id.homePagwSwipereFresh);
        mySession = new SessionManager(getActivity());
        HashMap<String, String> user = mySession.getUserDetails();
        myProviderIdStr = user.get(SessionManager.KEY_PROVIDERID);

        refreshReceiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.refresh.experthomepage");
        getActivity().registerReceiver(refreshReceiver, intentFilter);
        simpleToggleButton.setChecked(true);
        simpleToggleButton.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                if(isChecked)
                {
                    cd = new ConnectionDetector(getActivity());
                    if (cd.isConnectingToInternet()) {
                        postSetOnlineStatus(ServiceConstant.AVAILABILITY_URL, getContext(), 1);
                    }
                }
                else
                {
                    cd = new ConnectionDetector(getActivity());
                    if (cd.isConnectingToInternet()) {
                        postSetOnlineStatus(ServiceConstant.AVAILABILITY_URL, getContext(), 0);
                    }
                }
            }
        }) ;
    }

    private void initOnClick() {

        newLeadsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aNewLeadsIntent = new Intent(getActivity(), NewLeadsPage.class);
                startActivity(aNewLeadsIntent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        myJobsHomeCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aJobsIntent = new Intent(getActivity(), MyJobs.class);
                aJobsIntent.putExtra("status", "open");
                startActivity(aJobsIntent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        lastTaskCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        totalEarningsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aEarningsIntent = new Intent(getActivity(), WeekTaskBarChartActivity.class);
                startActivity(aEarningsIntent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        moreCategoriesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        cd = new ConnectionDetector(getActivity());
        if (cd.isConnectingToInternet()) {
            postSetOnlineStatus(ServiceConstant.AVAILABILITY_URL, getContext(), 1);
        }
        myDrawerLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDrawer.openDrawer();
            }
        });
        homePagwSwipereFresh.setColorSchemeColors(getResources().getColor(R.color.appmain_color), Color.RED, Color.BLUE);
        homePagwSwipereFresh.setEnabled(true);
        homePagwSwipereFresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (cd.isConnectingToInternet()) {
                    homePagwSwipereFresh.setEnabled(true);
                    homePagwSwipereFresh.setRefreshing(true);
                    postSetOnlineStatus(ServiceConstant.AVAILABILITY_URL, getContext(), 1);
                }
            }
        });

    }

    private void postSetOnlineStatus(String url, Context context, final int state) {
        final LoadingDialog myDialog = new LoadingDialog(getContext());
        myDialog.setLoadingTitle(getResources().getString(R.string.loading_in));
        myDialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("tasker", myProviderIdStr);
        jsonParams.put("availability", "" + state);
        ServiceRequest mservicerequest = new ServiceRequest(context);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                myDialog.dismiss();
                String status = "", responseString = "";
                System.out.println("Online Status-----------------------" + response);
                    postHomePageRequest(ServiceConstant.HOME_PAGE_PROVIDER_INFO_URL);
            }

            @Override
            public void onErrorListener() {
                myDialog.dismiss();
            }
        });
    }

    private void postHomePageRequest(String url) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", myProviderIdStr);
        jsonParams.put("task_date", getCurrentDate());
        ServiceRequest mservicerequest = new ServiceRequest(getActivity());
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                homePagwSwipereFresh.setRefreshing(false);
                String status = "", responseString = "",
                        avg_review = "", image = "", username = "", Working_location = "",
                        radius = "", distanceby = "", currency_symbol = "", currency_code = "",
                        last_task_starttime = "", last_task_hours = "", last_task_earning = "",
                        task_count = "", task_hours = "", task_earnings = "", completed_count = "",
                        new_leads_count = "", categoryname = "";
                System.out.println("Online Status-----------------------" + response);
                try {
                    JSONObject jobject = new JSONObject(response);
                    status = jobject.getString("status");
                    mySession.setTaskerVerification(status);
                    if (status.equalsIgnoreCase("1")
                            || status.equalsIgnoreCase("2")
                            || status.equalsIgnoreCase("3")) {

                        JSONObject jResponse = jobject.getJSONObject("response");
                        avg_review = jResponse.getString("avg_review");

                        mySession.setrating(avg_review);

                        image = jResponse.getString("image");
                        username = jResponse.getString("username");
                        if (jResponse.has("Working_location")) {
                            Working_location = jResponse.getString("Working_location");

                        }
                        if (jResponse.has("radius")) {
                            radius = jResponse.getString("radius");
                        }
                        if (jResponse.has("distanceby")) {
                            distanceby = jResponse.getString("distanceby");
                        }
                        if (jResponse.has("newleads")) {
                            new_leads_count = jResponse.getString("newleads");
                        }

                        if (jResponse.has("document_upload_status")) {
                            mySession.setProviderDocumentStatus(jResponse.getString("document_upload_status"));
                        }

                        JSONObject jCurrency = jResponse.getJSONObject("currency");
                        currency_symbol = jCurrency.getString("symbol");
                        currency_code = jCurrency.getString("code");
                        mySession.createWalletSession(currency_code);

                        ArrayList<String> categoryArrayList = new ArrayList<>();
                        JSONArray jsonArrayCat = jResponse.getJSONArray("category_Details");
                        if (jResponse.has("category_Details")) {
                            for (int i = 0; i < jsonArrayCat.length(); i++) {
                                JSONObject jCategoryname = jsonArrayCat.getJSONObject(i);
                                if (!categoryArrayList.contains(jCategoryname.getString("categoryname"))) {
                                    categoryArrayList.add(jCategoryname.getString("categoryname"));
                                }
                            }
                        }

                        if (jResponse.has("taskdetails") && jResponse.getJSONObject("taskdetails").length() > 0) {
                            JSONObject jTaskdetails = jResponse.getJSONObject("taskdetails");
                            last_task_starttime = jTaskdetails.getString("last_task_starttime");
                            last_task_hours = jTaskdetails.getString("last_task_hours");
                            last_task_earning = jTaskdetails.getString("last_task_earning");
                            task_count = jTaskdetails.getString("task_count");
                            task_hours = jTaskdetails.getString("task_hours");
                            task_earnings = jTaskdetails.getString("task_earnings");
                            if (jTaskdetails.has("completed_count")) {
                                completed_count = jTaskdetails.getString("completed_count");
                            }
                        }

                        if (jResponse.has("completed_count")) {
                            completed_count = jResponse.getString("completed_count");
                        }

                        Picasso.with(getActivity()).load(image).placeholder(R.drawable.nouserimg).into(profileIV);
                        mySession.setUserImageUpdate(image);
                        if (avg_review.equals("0")) {
                            ratingTV.setText(avg_review);
                        } else {
                            ratingTV.setText(Double.parseDouble(avg_review) + "");
                        }
                        newLeadsCountTV.setText(new_leads_count);
                        providerNameTV.setText(username);
                        workLoacationTV.setText(Working_location);
                        moreCategoriesTV.setText("");
                        categoriesListTV.setText("");
                        if (categoryArrayList.size() <= 2) {
                            for (int j = 0; j < categoryArrayList.size(); j++) {
                                categoriesListTV.append(categoryArrayList.get(j));
                                if (categoryArrayList.size() != j + 1) {
                                    categoriesListTV.append(" / ");
                                }
                            }
                        }
                        if (categoryArrayList.size() > 2) {
                            for (int j = 0; j < categoryArrayList.size(); j++) {
                                categoriesListTV.setText(categoryArrayList.get(0) + " / " + categoryArrayList.get(1));
                            }
                            moreCategoriesTV.setText("+" + String.valueOf(categoryArrayList.size() - 2) + " " + getResources().getString(R.string.expert_homepage_more_txt));
                        }

                        if (jResponse.has("taskdetails") && jResponse.getJSONObject("taskdetails").length() > 0) {
                            lastTaskCurrencyTV.setText(currency_symbol);
                            StringTokenizer tokenslast_task_earning = new StringTokenizer(last_task_earning, ".");
                            String firste = tokenslast_task_earning.nextToken();
                            String seconde = "";
                            if (last_task_earning.contains(".")) {
                                seconde = tokenslast_task_earning.nextToken();
                            }
                            lastTaskNumberTV.setText(firste + ".");
                            if (!seconde.equals("")) {
                                lastTaskDecimalTV.setText(seconde);
                            } else {
                                lastTaskDecimalTV.setText("00");
                            }

                            taskStartTimeTV.setText(last_task_starttime);
                            taskDurationTimeTV.setText(last_task_hours);

                            totalTaskCurrencyTV.setText(currency_symbol);
                            StringTokenizer tokens = new StringTokenizer(task_earnings, ".");
                            String first = tokens.nextToken();
                            String seconde1 = "";
                            if (task_earnings.contains(".")) {
                                seconde1 = tokens.nextToken();
                            }
                            totalTaskNumberTV.setText(first + ".");
                            totalTaskDecimalTV.setText(seconde1);

                            if (!seconde1.equals("")) {
                                totalTaskDecimalTV.setText(seconde1);
                            } else {
                                totalTaskDecimalTV.setText("00");
                            }
                            if (Integer.parseInt(task_count) <= 1) {
                                totalStartTimeTV.setText(task_count + " " + getResources().getString(R.string.weektaskbaractivity_task_txt));
                            } else {
                                totalStartTimeTV.setText(task_count + " " + getResources().getString(R.string.weektaskbaractivity_tasks_txt));
                            }
                            StringTokenizer time = new StringTokenizer(task_hours, ".");
                            String hours = time.nextToken();
                            String minits = time.nextToken();
                            if (minits.length() > 2) {
                                minits = minits.substring(0, 2);
                            }
                            totalDurationTimeTV.setText(hours + "h " + minits + "m");

                            completedJobCoutTV.setText(completed_count);

                        } else {
                            lastTaskCurrencyTV.setText(currency_symbol);
                            lastTaskNumberTV.setText("0.");
                            lastTaskDecimalTV.setText("00");

                            taskStartTimeTV.setText("--:--");
                            taskDurationTimeTV.setText("--:--");

                            totalTaskCurrencyTV.setText(currency_symbol);
                            totalTaskNumberTV.setText("0.");
                            totalTaskDecimalTV.setText("00");

                            totalStartTimeTV.setText("0 " + getResources().getString(R.string.weektaskbaractivity_task_txt));
                            totalDurationTimeTV.setText("--:--");
                            completedJobCoutTV.setText("");
                        }


                    } else if (status.equalsIgnoreCase("3") || status.equalsIgnoreCase("2")) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.navigation_menu_adapter_verify_your_account), Toast.LENGTH_SHORT).show();
                    }

                    mySession.Taskerstatus(status);

                    Intent s = new Intent();
                    s.setAction("com.admin.verification");
                    getActivity().sendBroadcast(s);

                } catch (Exception e) {
                    e.printStackTrace();
                    homePagwSwipereFresh.setEnabled(false);
                    homePagwSwipereFresh.setRefreshing(false);
                }
            }

            @Override
            public void onErrorListener() {
            }
        });
    }

    private void showDialog(String data) {
        mLoadingDialog = new LoadingDialog(getActivity());
        mLoadingDialog.setLoadingTitle(data);
        mLoadingDialog.show();
    }

    //-----------------------Logout Request-----------------
    private void postRequest_Logout(String Url) {
        showDialog(getResources().getString(R.string.action_logging_out));
        System.out.println("---------------LogOut Url-----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", myProviderIdStr);
        jsonParams.put("device_type", "android");
        ServiceRequest mservicerequest = new ServiceRequest(getActivity());
        mservicerequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------------LogOut Response-----------------" + response);
                String sStatus = "", sResponse = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    sResponse = object.getString("response");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mLoadingDialog.dismiss();
                if (sStatus.equalsIgnoreCase("1")
                        || sStatus.equalsIgnoreCase("2")
                        || sStatus.equalsIgnoreCase("3")) {
                    postSetOnlineStatus(ServiceConstant.AVAILABILITY_URL, getContext(), 0);
                }
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }

    private void Alert(String title, String alert) {
        final PkDialog mDialog = new PkDialog(getActivity());
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(
                getResources().getString(R.string.action_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                }
        );

        mDialog.show();
    }

    private String getCurrentDate() {
        String aCurrentDateStr = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            aCurrentDateStr = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aCurrentDateStr;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(refreshReceiver);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
