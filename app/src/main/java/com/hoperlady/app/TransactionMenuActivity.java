package com.hoperlady.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperlady.Pojo.TransactionPojoInfo;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.adapter.TransactionListAdapter;
import com.hoperlady.hockeyapp.ActivityHockeyApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import core.Dialog.PkDialog;
import core.Dialog.PkLoadingDialog;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;

public class TransactionMenuActivity extends ActivityHockeyApp {
    private TextView myHeaderTitleTXT;
    private ImageView myBackIMG;
    private RelativeLayout myBackLAY;
    private boolean isInternetPresent = false;
    private ConnectionDetector myConnectionDetector;
    private Receiver myReceiver;
    private PkLoadingDialog myLoadingDialog;
    private ServiceRequest myRequest;
    private SessionManager mySession;
    private String myProviderIdStr = "";

    private SwipeRefreshLayout mySwipeLAY;
    private ArrayList<TransactionPojoInfo> myTransactionInfoList;
    private TransactionListAdapter myAdapter;
    private ListView myListview;
    private TextView myEmptyTXT;
    private Context myContext;
    private RelativeLayout myInternalLAY;
    private String Str_Refresh_Name = "normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_menu);
        initializeHeaderBar();
        classAndWidgetInitialize();

    }

    private void classAndWidgetInitialize() {
        myContext = TransactionMenuActivity.this;
        myConnectionDetector = new ConnectionDetector(TransactionMenuActivity.this);
        mySession = new SessionManager(TransactionMenuActivity.this);
        myRequest = new ServiceRequest(TransactionMenuActivity.this);
        myTransactionInfoList = new ArrayList<>();
        isInternetPresent = myConnectionDetector.isConnectingToInternet();
        mySwipeLAY = (SwipeRefreshLayout) findViewById(R.id.screen_getmessage_LAY_swipe);
        myListview = (ListView) findViewById(R.id.screen_transaction_LV);
        myEmptyTXT = (TextView) findViewById(R.id.screen_transaction_TXT_empty);
        myInternalLAY = (RelativeLayout) findViewById(R.id.transaction_noInternet_layout);
        mySwipeLAY.setColorSchemeColors(Color.GREEN, Color.RED, Color.BLUE);
        mySwipeLAY.setEnabled(true);


        HashMap<String, String> provider = mySession.getUserDetails();
        myProviderIdStr = provider.get(SessionManager.KEY_PROVIDERID);
        getTransactionData();
        swipeListener();

        myReceiver = new Receiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("com.refresh.message");
        registerReceiver(myReceiver, intentfilter);
    }

    private void swipeListener() {
        mySwipeLAY.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isInternetPresent) {
                    //        Rl_Main.setVisibility(View.VISIBLE);
                    myInternalLAY.setVisibility(View.GONE);
                    Str_Refresh_Name = "swipe";
                    getData();
                } else {
                    mySwipeLAY.setEnabled(true);
                    mySwipeLAY.setRefreshing(false);
                    // Rl_Main.setVisibility(View.GONE);
                    myInternalLAY.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_layout);
        myBackLAY = (RelativeLayout) headerBar.findViewById(R.id.headerBar_left_layout);
        myBackIMG = (ImageView) headerBar.findViewById(R.id.headerBar_imageView);
        myHeaderTitleTXT = (TextView) headerBar.findViewById(R.id.headerBar_title_textView);
        myHeaderTitleTXT.setText(getResources().getString(R.string.navigation_label_transactions));
//        myBackIMG.setImageResource(R.drawable.back_arrow_black);
        myBackLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void getTransactionData() {
        if (isInternetPresent) {
            myInternalLAY.setVisibility(View.GONE);
            getData();
        } else {
            mySwipeLAY.setEnabled(true);
            myInternalLAY.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(myReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getData() {
        startLoading();
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", myProviderIdStr);

        myRequest.makeServiceRequest(ServiceConstant.TRANSACTION_URL, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

                    @Override
                    public void onCompleteListener(String response) {
                        Log.e("response", response);
                        String sStatus = "";
                        try {
                            JSONObject aObject = new JSONObject(response);
                            sStatus = aObject.getString("status");
                            if (sStatus.equalsIgnoreCase("1")) {
                                JSONObject response_Object = aObject.getJSONObject("response");
                                if (response_Object.length() > 0) {
                                    JSONArray aJobsArray = response_Object.getJSONArray("jobs");
                                    if (aJobsArray.length() > 0) {
                                        myTransactionInfoList.clear();
                                        for (int i = 0; i < aJobsArray.length(); i++) {
                                            JSONObject aJsonObject = aJobsArray.getJSONObject(i);
                                            TransactionPojoInfo aTransactionInfo = new TransactionPojoInfo();
                                            aTransactionInfo.setTransactionJobId(aJsonObject.getString("job_id"));
                                            aTransactionInfo.setTransactionCategoryName(aJsonObject.getString("category_name"));
                                            aTransactionInfo.setTransactionTotalAmount(aJsonObject.getString("total_amount"));
                                            aTransactionInfo.setDate(aJsonObject.getString("job_date"));
                                            aTransactionInfo.setTime(aJsonObject.getString("job_time"));
                                            myTransactionInfoList.add(aTransactionInfo);
                                        }
                                    }
                                }
                            } else {
                                String sResponse = aObject.getString("response");
                                alert(getResources().getString(R.string.my_rides_rating_header_sorry_textview), sResponse);
                            }

                            loadInfoData(myTransactionInfoList);
                            stopLoading();
                        } catch (JSONException e) {
                            stopLoading();
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onErrorListener() {
                        stopLoading();
                    }
                }

        );
    }

    private void loadInfoData(final ArrayList<TransactionPojoInfo> aTransactionInfoList) {
        if (aTransactionInfoList.size() > 0) {
            myEmptyTXT.setVisibility(View.GONE);
            myAdapter = new TransactionListAdapter(myContext, aTransactionInfoList);
            myListview.setAdapter(myAdapter);
            myListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(myContext, TransactionDetailActivity.class);
                    intent.putExtra("ProviderId", myProviderIdStr);
                    intent.putExtra("BookingId", aTransactionInfoList.get(position).getTransactionJobId());
                    myContext.startActivity(intent);
                }
            });
        } else {
            myEmptyTXT.setVisibility(View.VISIBLE);
        }
    }

    private void startLoading() {
        if (Str_Refresh_Name.equalsIgnoreCase("normal")) {
            myLoadingDialog = new PkLoadingDialog(TransactionMenuActivity.this);
            myLoadingDialog.show();
        } else {
            mySwipeLAY.setRefreshing(true);
        }
    }

    private void stopLoading() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Str_Refresh_Name.equalsIgnoreCase("normal")) {
                    myLoadingDialog.dismiss();
                } else {
                    mySwipeLAY.setRefreshing(false);
                }
            }
        }, 250);
    }

    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(TransactionMenuActivity.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    //------Alert Method-----

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            onBackPressed();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("com.refresh.message")) {
                if (isInternetPresent) {
                    getData();
                }
            }
        }
    }
}
