package com.hoperlady.app;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.hoperlady.Pojo.CancelReasonPojo;
import com.hoperlady.R;
import com.hoperlady.SubClassBroadCast.SubClassActivity;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.adapter.CancelReasonAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import core.Dialog.LoadingDialog;
import core.Dialog.PkDialog;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;
import core.socket.SocketHandler;

/**
 * Created by user88 on 12/17/2015.
 */
public class Cancel_Job_Reason extends SubClassActivity {

    private ConnectionDetector cd;
    private Context context;
    private SessionManager session;
    private ListView cancel_listview;

    private TextView Tv_Emtytxt;
    private Boolean isInternetPresent = false;
    private boolean show_progress_status = false;

    private String Str_JobId = "";
    private RelativeLayout Rl_layout_cancel_back;

    private ArrayAdapter<String> listAdapter;
    private ArrayList<CancelReasonPojo> Cancelreason_arraylist;
    private CancelReasonAdapter adapter;
    private String provider_id = "";
    private String Job_id = "";
    private LoadingDialog dialog;
    private Handler mHandler;
    private boolean isReasonAvailable = false;
    private String Str_reason = "";
    TextView cancel_job_reason_textView;
    RelativeLayout edit_tv_Rl;
    View otherview;
    Dialog other_dialog;
    private SocketHandler socketHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancelreason_job);
        initialize();

        cancel_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Str_reason = Cancelreason_arraylist.get(position).getReason();
                System.out.println("reasonm-----------" + Cancelreason_arraylist.get(position).getReason());

                cancelJobAlert(Str_reason);
            }
        });


        Rl_layout_cancel_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        cancel_job_reason_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        edit_tv_Rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOtherDialog();
            }
        });

    }

    private void initialize() {
        session = new SessionManager(Cancel_Job_Reason.this);
        cd = new ConnectionDetector(Cancel_Job_Reason.this);
        mHandler = new Handler();
        socketHandler = SocketHandler.getInstance(this);
        edit_tv_Rl = (RelativeLayout) findViewById(R.id.edit_tv_Rl);
        cancel_job_reason_textView = (TextView) findViewById(R.id.cancel_job_reason_textView);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        provider_id = user.get(SessionManager.KEY_PROVIDERID);

        Intent i = getIntent();
        Job_id = i.getStringExtra("JobId");

        Cancelreason_arraylist = new ArrayList<CancelReasonPojo>();
        cancel_listview = (ListView) findViewById(R.id.cancelreason_listView);
        Tv_Emtytxt = (TextView) findViewById(R.id.emtpy_cancelreason);
        Rl_layout_cancel_back = (RelativeLayout) findViewById(R.id.layout_cancel_back);

        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            cancelreason_PostRequest(Cancel_Job_Reason.this, ServiceConstant.JOB_CANCELL_REASON_URL);

            System.out.println("cancel--------------url--" + ServiceConstant.JOB_CANCELL_REASON_URL);

        } else {

            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }

    }

    private void cancelOtherDialog() {

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int screenWidth = (int) (displayMetrics.widthPixels * 0.80);//fill only 80% of the screen
        otherview = View.inflate(this, R.layout.other_reason_layout, null);
        other_dialog = new Dialog(Cancel_Job_Reason.this);
        other_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        other_dialog.setContentView(otherview);
        other_dialog.setCanceledOnTouchOutside(false);
        other_dialog.setCancelable(false);
        other_dialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        other_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final MaterialEditText edit_text = (MaterialEditText) otherview.findViewById(R.id.other_reason_ET);
        RelativeLayout cancel_RL = (RelativeLayout) otherview.findViewById(R.id.cancel_RL);
        RelativeLayout ok_RL = (RelativeLayout) otherview.findViewById(R.id.ok_RL);
        ok_RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(Cancel_Job_Reason.this, getResources().getString(R.string.empty_cancel_reason_toast_txt), Toast.LENGTH_SHORT).show();
                } else {
                    Str_reason = edit_text.getText().toString().trim();
                    cancelJobAlert(Str_reason);
                }
            }

        });

        cancel_RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                other_dialog.dismiss();
            }
        });

        other_dialog.show();


    }

    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(Cancel_Job_Reason.this);
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


    //--------------------code for cancel reason diaolg--------------------
    public void cancelJobAlert(final String reason) {
        ConnectionDetector cd = new ConnectionDetector(Cancel_Job_Reason.this);
        final boolean isInternetPresent = cd.isConnectingToInternet();
        final PkDialog mDialog = new PkDialog(Cancel_Job_Reason.this);
        mDialog.setDialogTitle(getResources().getString(R.string.confirmdelete));
        mDialog.setDialogMessage(getResources().getString(R.string.surewanttodelete));

        mDialog.setPositiveButton(getResources().getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetPresent) {
                    postRequest_Canceljob(Cancel_Job_Reason.this, ServiceConstant.JOB_CANCELLED_URL, reason);
                    System.out.println("cancelled-----------" + ServiceConstant.JOB_CANCELLED_URL);

                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
                mDialog.dismiss();
            }
        });

        mDialog.setNegativeButton(getResources().getString(R.string.no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mDialog.show();


    }

    //---------------------code for cancel job-----------------
    private void cancelreason_PostRequest(Context mContext, String url) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", provider_id);


        dialog = new LoadingDialog(Cancel_Job_Reason.this);
        dialog.setLoadingTitle(getResources().getString(R.string.loading_in));
        dialog.show();


        ServiceRequest mservicerequest = new ServiceRequest(mContext);

        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                String Sstatus = "", Str_response = "", sResponse = "";
                System.out.println("cancelreason--------" + response);

                try {

                    // JSONObject jobject = new JSONObject(response);
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject jsonObject = object.getJSONObject("response");

                        JSONArray response_array = jsonObject.getJSONArray("reason");
                        if (response_array.length() > 0) {
                            Cancelreason_arraylist.clear();

                            for (int i = 0; i < response_array.length(); i++) {
                                JSONObject reason_object = response_array.getJSONObject(i);
                                CancelReasonPojo items = new CancelReasonPojo();
                                items.setReason(reason_object.getString("reason"));
                                items.setCancelreason_id(reason_object.getString("id"));

                                Cancelreason_arraylist.add(items);

                            }

                            isReasonAvailable = true;
                        } else {
                            isReasonAvailable = false;
                        }
                    } else {

                        sResponse = object.getString("response");
                        // alert(getResources().getString(R.string.action_sorry), sResponse);
                    }


                    if (Sstatus.equalsIgnoreCase("1")) {
                        System.out.println("secnd-----------" + Cancelreason_arraylist.get(0).getReason());
                        adapter = new CancelReasonAdapter(Cancel_Job_Reason.this, Cancelreason_arraylist);
                        cancel_listview.setAdapter(adapter);
//
//                        if (show_progress_status) {
//                            Tv_Emtytxt.setVisibility(View.GONE);
//                        } else {
//                            Tv_Emtytxt.setVisibility(View.VISIBLE);
//                            cancel_listview.setEmptyView(Tv_Emtytxt);
//                        }
                    } else {

                        Alert(getResources().getString(R.string.server_lable_header), sResponse);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
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


    //---------------------------Code for cancelled-------------------
    private void postRequest_Canceljob(Context mContext, String url, String reason) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", provider_id);
        jsonParams.put("job_id", Job_id);
        jsonParams.put("reason", reason);

        System.out.println("provider_id-----------" + provider_id);
        System.out.println("job_id-----------" + Job_id);
        System.out.println("reason-----------" + Str_reason);

        dialog = new LoadingDialog(Cancel_Job_Reason.this);
        dialog.setLoadingTitle(getResources().getString(R.string.dialog_cancelling));
        dialog.show();

        ServiceRequest mservicerequest = new ServiceRequest(mContext);

        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                System.out.println("cancelled---------------" + response);

                String Str_status = "", Str_message = "", Str_response = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject jobject = object.getJSONObject("response");
                        Str_message = jobject.getString("message");

                    } else {
                        Str_response = object.getString("response");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (Str_status.equalsIgnoreCase("1")) {
                    Toast.makeText(Cancel_Job_Reason.this, Str_message, Toast.LENGTH_SHORT).show();

                    Intent broadcastIntent_detailpage = new Intent();
                    broadcastIntent_detailpage.setAction("com.finish.DetailsPage");
                    sendBroadcast(broadcastIntent_detailpage);

                    Intent broadcastIntent_newleads = new Intent();
                    broadcastIntent_newleads.setAction("com.finish.NewLeadsPage");
                    sendBroadcast(broadcastIntent_newleads);

                    Intent broadcastIntent_newleadsfragment = new Intent();
                    broadcastIntent_newleadsfragment.setAction("com.finish.NewLeadsFragmet");
                    sendBroadcast(broadcastIntent_newleadsfragment);

                    Intent broadcastIntent_missedleadsleadsfragment = new Intent();
                    broadcastIntent_missedleadsleadsfragment.setAction("com.finish.MissedLeadsFragment");
                    sendBroadcast(broadcastIntent_missedleadsleadsfragment);

                    if (MyJobs.Myjobs_Activity != null) {
                        MyJobs.Myjobs_Activity.finish();
                        Intent i = new Intent(Cancel_Job_Reason.this, MyJobs.class);
                        i.putExtra("status", "cancelled");
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else {
                        Intent i = new Intent(Cancel_Job_Reason.this, MyJobs.class);
                        i.putExtra("status", "cancelled");
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }
                    finish();

                } else {
                    Alert(getResources().getString(R.string.alert_label_title), Str_response);
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
    }


}