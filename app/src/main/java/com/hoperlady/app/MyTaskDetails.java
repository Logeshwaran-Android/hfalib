package com.hoperlady.app;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hoperlady.Pojo.Addmaterialpojo;
import com.hoperlady.Pojo.Materialcostsubmitpojo;
import com.hoperlady.Pojo.MyJobsDetailPojo;
import com.hoperlady.Pojo.PaymentFareSummeryPojo;
import com.hoperlady.Pojo.WorkFlow_Pojo;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.CurrencySymbolConverter;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.adapter.MateriaNewRVlAdapter;
import com.hoperlady.adapter.MaterialAddNewAdapter;
import com.hoperlady.adapter.MyTaskDetailTimeLineAdapter;
import com.hoperlady.adapter.MyTaskDetailsFareSummeryAdapter;
import com.hoperlady.app.map.MapUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import core.Dialog.LoadingDialog;
import core.Dialog.PkDialog;
import core.Map.GPSTracker;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;
import core.socket.ChatMessageService;
import core.socket.TaskerTrackRideServiceClass;

public class MyTaskDetails extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , com.google.android.gms.location.LocationListener {

    private ArrayList<WorkFlow_Pojo> timeLineList;
    private ArrayList<PaymentFareSummeryPojo> farelist;
    private ArrayList<MyJobsDetailPojo> infoList;

    private RecyclerView timeLineRV, paymentInvoiceRV;
    public static String sProviderID = "", sJobID = "", mTaskerID = "", mTaskID = "";
    private String myCurrencySymbol = "";
    private TextView myTaskDetail_headerBar_category_textView, myTaskDetail_headerBar_job_id_textView;
    private TextView myTaskDetailsJobStatusTV, myTaskDetailsDateTimeTV, myTaskDetailsLocationTV, myTaskDetailsCancellaionTV, myTaskDetailsInstructionTV;
    private TextView providerNameTV, providerEmailIdTV;
    private LinearLayout cancel_layout, paymentInvoiceLL;
    private ImageView providerimg, statusIconIV;
    private TextView myTaskDetailsReviewsTV;
    public static Button chatButton, cancelActionTV, submitActionTV;
    private RelativeLayout Rl_back, loaderRL;
    private LinearLayout actionLL, actionmainLL, instructionLL;
    private NestedScrollView nestedSrollView;

    private String User_image = "";
    private String User_name = "";

    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;
    private GPSTracker gps;

    //  public static MaterialAddAdapter aAdapter;
    public static MaterialAddNewAdapter aAdapter;
    private RecyclerView meterialItemsRV;
    int count = 0;
    public static Dialog moreAddressDialog;
    private View moreAddressView;
    ArrayList<String> listItems;
    public static ListView list;
    private ArrayList<Addmaterialpojo> item_add = new ArrayList<Addmaterialpojo>();
    MateriaNewRVlAdapter rVlAdapter;
    public static boolean item_add_bollean = false;
    public static RelativeLayout aCancelLAY;
    public static RelativeLayout aOKLAY;
    public static CheckBox addmaterial;

    private String usercurrentlat = "", usercurrentlong = "";
    private double Slattitude = 0.0, Slongitude = 0.0;
    private double locationlattitude = 0.0, locationlongitude = 0.0;

    private String provider_hourly_rate = "", provider_minimum_rate = "";
    private String Job_Status = "", booking_address = "", cancel_reason = "", sBtn_group = "",
            Str_usermobile = "", share_text = "", Str_Userid = "", destination_lat = "",
            destination_long = "", Str_jobusername = "", Str_userimg = "", Str_user_email = "";
    private ImageView detail_chat_img, detail_phone_img, detail_message_img, detail_email_img, track_location;
    private LinearLayout trackingLL, callLL, chatLL, mailBoxLL, smsLL;
    private ConnectionDetector cd;

    private boolean isReasonAvailable = false;
    private LoadingDialog mLoadingDialog;
    private SessionManager sessionManager;
    private RefreshReceiver finishReceiver;

    public static AppCompatActivity MyTask_page;

    private FloatingActionButton extraCommunicationFAB, phone, phonechat, email, appchat, tracking;
    private LinearLayout overAll;
    private RelativeLayout detail_chats_relativeLayout;
    private View timeLineView;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static Location myLocation;

    private PendingResult<LocationSettingsResult> result;
    private final static int REQUEST_LOCATION = 199;
    final int PERMISSION_REQUEST_LOCATION = 333;

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.refresh.MyJobDetails")) {
                if (cd.isConnectingToInternet()) {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    JobDetailRequest(ServiceConstant.MYJOB_DETAIL_INFORMATION_URL, "0");
                }
            } else if (intent.getAction().equals("com.package.finish.jobdetailpage")) {
                if (MyJobs.Myjobs_Activity != null) {
                    MyJobs.Myjobs_Activity.finish();
                    Intent i = new Intent(MyTaskDetails.this, MyJobs.class);
                    i.putExtra("status", "cancelled");
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Intent i = new Intent(MyTaskDetails.this, MyJobs.class);
                    i.putExtra("status", "cancelled");
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

            }

            if (intent.getAction().equalsIgnoreCase("com.avail.finish")) {
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_task_details);
        initilization();
        initOnClick();
        try {
            setLocationRequest();
            buildGoogleApiClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initOnClick() {

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMessageService.tasker_id = "";
                ChatMessageService.task_id = "";
                Intent intent = new Intent(MyTaskDetails.this, ChatPage.class);
//                intent.putExtra("JobID-Intent", sJobID);
//                intent.putExtra("TaskerId", mTaskerID);
//                intent.putExtra("TaskId", mTaskID);
                //this project
//                intent.putExtra("chatpage", true);
//                intent.putExtra("JOBID", sJobID);
//                intent.putExtra("TaskerId", Str_Userid);
//                intent.putExtra("TaskId", mTaskID);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        cancelActionTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MyTaskDetails.this, Cancel_Job_Reason.class);
                intent.putExtra("JobId", sJobID);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        submitActionTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sBtn_group.equals("1")) {
                    if (cd.isConnectingToInternet()) {
                        if (TaskerTrackRideServiceClass.isStarted()) {
                            Intent taskertrack_service = new Intent(getApplicationContext(), TaskerTrackRideServiceClass.class);
                            stopService(taskertrack_service);
                        }
                        buttonsClickActions(MyTaskDetails.this, ServiceConstant.ACCEPT_JOB_URL, "accept");
                        System.out.println("--------------accept-------------------" + ServiceConstant.ACCEPT_JOB_URL);
                    } else {
                        Toast.makeText(MyTaskDetails.this, getResources().getString(R.string.alert_nointernet), Toast.LENGTH_SHORT).show();
                    }
                } else if (sBtn_group.equals("2")) {
                    if (cd.isConnectingToInternet()) {

                        if (!TaskerTrackRideServiceClass.isStarted()) {
                            Intent taskertrack_service = new Intent(getApplicationContext(), TaskerTrackRideServiceClass.class);
                            startService(taskertrack_service);
                        }
                        TaskerTrackRideServiceClass.TaskDetail(mTaskID, Str_Userid);

                        buttonsClickActions(MyTaskDetails.this, ServiceConstant.STARTOFFJOB_JOB_URL, "startoff");
                        System.out.println("--------------startoff url-------------------" + ServiceConstant.STARTOFFJOB_JOB_URL);
                    } else {
                        Toast.makeText(MyTaskDetails.this, getResources().getString(R.string.alert_nointernet), Toast.LENGTH_SHORT).show();
                    }
                } else if (sBtn_group.equals("3")) {
                    if (cd.isConnectingToInternet()) {

                        if (!TaskerTrackRideServiceClass.isStarted()) {
                            Intent taskertrack_service = new Intent(getApplicationContext(), TaskerTrackRideServiceClass.class);
                            startService(taskertrack_service);
                        }
                        TaskerTrackRideServiceClass.TaskDetail(mTaskID, Str_Userid);

                        buttonsClickActions(MyTaskDetails.this, ServiceConstant.ARRIVED_JOB_URL, "arrived");
                        System.out.println("--------------startoff url-------------------" + ServiceConstant.ARRIVED_JOB_URL);
                    } else {
                        Toast.makeText(MyTaskDetails.this, getResources().getString(R.string.alert_nointernet), Toast.LENGTH_SHORT).show();
                    }

                } else if (sBtn_group.equals("4")) {
                    cancelActionTV.setVisibility(View.GONE);
                    if (cd.isConnectingToInternet()) {
                        if (TaskerTrackRideServiceClass.isStarted()) {
                            Intent taskertrack_service = new Intent(getApplicationContext(), TaskerTrackRideServiceClass.class);
                            stopService(taskertrack_service);
                        }
                        buttonsClickActions(MyTaskDetails.this, ServiceConstant.STARTJOB_URL, "startjob");
                        System.out.println("--------------startoff url-------------------" + ServiceConstant.STARTJOB_URL);
                    } else {
                        Toast.makeText(MyTaskDetails.this, getResources().getString(R.string.alert_nointernet), Toast.LENGTH_SHORT).show();
                    }
                } else if (sBtn_group.equals("5")) {

                    cancelActionTV.setVisibility(View.GONE);
                    Materail_Add_New_Alert();
                   // jobCompletewithoutmaterial(MyTaskDetails.this, ServiceConstant.JOBCOMPLETE_URL, "jobcomplete");

                } else if (sBtn_group.equals("7")) {
//                    Intent rating = new Intent(getApplicationContext(), ReviwesPage.class);
                    Intent rating = new Intent(getApplicationContext(), RatingSmilyPage.class);
                    rating.putExtra("jobId", sJobID);
                    startActivity(rating);

                } else if (sBtn_group.equals("8")) {


                }
            }
        });


        extraCommunicationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (overAll.getVisibility() == View.VISIBLE) {
                    overAll.setVisibility(View.GONE);
                } else {
                    overAll.setVisibility(View.GONE);
                }
            }
        });

        detail_phone_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Str_usermobile != null) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + Str_usermobile));
                    startActivity(callIntent);
                } else {
                    Toast.makeText(MyTaskDetails.this, getResources().getString(R.string.arrived_alert_content1), Toast.LENGTH_SHORT).show();
                }
            }
        });
        detail_message_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    share_text = getResources().getString(R.string.ongoing_detail_shar_text);
                    sms_sendMsg(share_text);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Your call has failed...", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        detail_email_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{Str_user_email});
                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.ongoing_detail_subject));
                i.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.ongoing_detail_text) + " ");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    // Toast.makeText(SettingsPage.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        detail_chat_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMessageService.user_id = "";
                ChatMessageService.task_id = "";
                Intent intent = new Intent(MyTaskDetails.this, ChatPage.class);
                intent.putExtra("chatpage", true);
                intent.putExtra("JOBID", sJobID);
                intent.putExtra("TaskerId", Str_Userid);
                intent.putExtra("TaskId", mTaskID);
                intent.putExtra("btn_group", sBtn_group);
                System.out.println("Str_Userid-----------" + sProviderID);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        trackingLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    CheckPermissions();
                } else {
                    Toast.makeText(MyTaskDetails.this, getResources().getString(R.string.alert_nointernet), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void initilization() {
        MyTask_page = MyTaskDetails.this;
        myTaskDetail_headerBar_category_textView = (TextView) findViewById(R.id.myTaskDetail_headerBar_category_textView);
        myTaskDetail_headerBar_job_id_textView = (TextView) findViewById(R.id.myTaskDetail_headerBar_job_id_textView);
        myTaskDetailsJobStatusTV = (TextView) findViewById(R.id.myTaskDetailsJobStatusTV);
        myTaskDetailsDateTimeTV = (TextView) findViewById(R.id.myTaskDetailsDateTimeTV);
        myTaskDetailsLocationTV = (TextView) findViewById(R.id.myTaskDetailsLocationTV);
        myTaskDetailsCancellaionTV = (TextView) findViewById(R.id.myTaskDetailsCancellaionTV);
        providerNameTV = (TextView) findViewById(R.id.providerNameTV);
        providerEmailIdTV = (TextView) findViewById(R.id.providerEmailIdTV);
        cancel_layout = (LinearLayout) findViewById(R.id.cancel_layout);
        paymentInvoiceLL = (LinearLayout) findViewById(R.id.paymentInvoiceLL);
        providerimg = (ImageView) findViewById(R.id.providerimg);
        statusIconIV = (ImageView) findViewById(R.id.statusIconIV);
        myTaskDetailsReviewsTV = (TextView) findViewById(R.id.myTaskDetailsReviewsTV);
        chatButton = (Button) findViewById(R.id.chatButton);
        cancelActionTV = (Button) findViewById(R.id.cancelActionTV);
        submitActionTV = (Button) findViewById(R.id.submitActionTV);
        Rl_back = (RelativeLayout) findViewById(R.id.myJob_detail_headerBar_left_layout);
        actionLL = (LinearLayout) findViewById(R.id.actionRL);
        actionmainLL = (LinearLayout) findViewById(R.id.actionmainLL);
        loaderRL = (RelativeLayout) findViewById(R.id.loaderRL);
        myTaskDetailsInstructionTV = (TextView) findViewById(R.id.myTaskDetailsInstructionTV);
        instructionLL = (LinearLayout) findViewById(R.id.instructionLL);

        detail_chat_img = (ImageView) findViewById(R.id.detail_chat_img);
        detail_phone_img = (ImageView) findViewById(R.id.detail_phone_img);
        detail_message_img = (ImageView) findViewById(R.id.detail_message_img);
        detail_email_img = (ImageView) findViewById(R.id.detail_email_img);
        track_location = (ImageView) findViewById(R.id.track_location);
        trackingLL = (LinearLayout) findViewById(R.id.trackingLL);
        callLL = (LinearLayout) findViewById(R.id.callLL);
        chatLL = (LinearLayout) findViewById(R.id.chatLL);
        mailBoxLL = (LinearLayout) findViewById(R.id.mailBoxLL);
        smsLL = (LinearLayout) findViewById(R.id.smsLL);

        timeLineRV = (RecyclerView) findViewById(R.id.timeLineRV);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyTaskDetails.this);
        timeLineRV.setLayoutManager(mLayoutManager);
        timeLineRV.setHasFixedSize(true);
        timeLineRV.setItemAnimator(new DefaultItemAnimator());
        timeLineRV.setNestedScrollingEnabled(false);

        paymentInvoiceRV = (RecyclerView) findViewById(R.id.paymentInvoiceRV);
        RecyclerView.LayoutManager mLayoutManagerPayment = new LinearLayoutManager(MyTaskDetails.this);
        paymentInvoiceRV.setLayoutManager(mLayoutManagerPayment);
        paymentInvoiceRV.setHasFixedSize(true);
        paymentInvoiceRV.setItemAnimator(new DefaultItemAnimator());
        paymentInvoiceRV.setNestedScrollingEnabled(false);

        sessionManager = new SessionManager(MyTaskDetails.this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        sProviderID = user.get(SessionManager.KEY_PROVIDERID);

        HashMap<String, String> hAmount = sessionManager.getWalletDetails();
        String aCurrencyCode = hAmount.get(SessionManager.KEY_CURRENCY_CODE);
        myCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(aCurrencyCode);

        gps = new GPSTracker(MyTaskDetails.this);

        infoList = new ArrayList<MyJobsDetailPojo>();
        timeLineList = new ArrayList<WorkFlow_Pojo>();
        farelist = new ArrayList<PaymentFareSummeryPojo>();
        Intent intent = getIntent();
        sJobID = intent.getStringExtra("JobId");

        cd = new ConnectionDetector(MyTaskDetails.this);

        extraCommunicationFAB = (FloatingActionButton) findViewById(R.id.extraCommunicationFAB);
        phone = (FloatingActionButton) findViewById(R.id.phone);
        phonechat = (FloatingActionButton) findViewById(R.id.phonechat);
        email = (FloatingActionButton) findViewById(R.id.email);
        appchat = (FloatingActionButton) findViewById(R.id.appchat);
        tracking = (FloatingActionButton) findViewById(R.id.tracking);
        overAll = (LinearLayout) findViewById(R.id.overAll);
        detail_chats_relativeLayout = (RelativeLayout) findViewById(R.id.detail_chats_relativeLayout);
        timeLineView = (View) findViewById(R.id.timeLineView);
        nestedSrollView = (NestedScrollView) findViewById(R.id.nestedSrollView);
        nestedSrollView.setFocusableInTouchMode(true);
        nestedSrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        if (cd.isConnectingToInternet()) {
            JobDetailRequest(ServiceConstant.MYJOB_DETAIL_INFORMATION_URL, "1");
            System.out.println("mjobdeetail---------" + ServiceConstant.MYJOB_DETAIL_INFORMATION_URL);
        } else {
            Toast.makeText(MyTaskDetails.this, getResources().getString(R.string.action_no_internet_message), Toast.LENGTH_SHORT).show();
        }

        if (gps.canGetLocation()) {
            double Dlatitude = gps.getLatitude();
            double Dlongitude = gps.getLongitude();
            MyCurrent_lat = Dlatitude;
            MyCurrent_long = Dlongitude;

            System.out.println("currntlat----------" + MyCurrent_lat);
            System.out.println("currntlon----------" + MyCurrent_long);

        }

        finishReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.refresh.MyJobDetails");
        intentFilter.addAction("com.package.finish.jobdetailpage");

        registerReceiver(finishReceiver, intentFilter);
    }

    private void JobDetailRequest(String url, String loading) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", sProviderID);
        jsonParams.put("job_id", sJobID);

        System.out.println("JobDetailRequest jsonParams" + jsonParams);

        if (loading.equals("1")) {
            loaderRL.setVisibility(View.VISIBLE);
        }

        ServiceRequest mRequest = new ServiceRequest(MyTaskDetails.this);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("JobDetailRequest response" + response);

                String sStatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");

                    if (sStatus.equalsIgnoreCase("1")) {
                        Object check_response_object = object.get("response");
                        if (check_response_object instanceof JSONObject) {
                            JSONObject response_Object = object.getJSONObject("response");
                            if (response_Object.length() > 0) {
                                Object check_info_object = response_Object.get("job");
                                if (check_info_object instanceof JSONObject) {
                                    infoList.clear();
                                    JSONObject info_Object = response_Object.getJSONObject("job");
                                    if (info_Object.length() > 0) {

                                        MyJobsDetailPojo pojo = new MyJobsDetailPojo();
                                        pojo.setJob_id(info_Object.getString("job_id"));
                                        sJobID = info_Object.getString("job_id");

                                        mTaskID = info_Object.getString("task_id");
                                        pojo.setTaskid(info_Object.getString("task_id"));
//                                        pojo.setLocation(info_Object.getString("location"));
                                        pojo.setLat(info_Object.getString("location_lat"));
                                        pojo.setLng(info_Object.getString("location_lon"));
                                        pojo.setBook_date(info_Object.getString("job_date"));
                                        pojo.setBook_time(info_Object.getString("job_time"));
//                                        pojo.setDate(info_Object.getString("date"));
//                                        pojo.setTime(info_Object.getString("time"));
                                        pojo.setBooking_address(info_Object.getString("service_address"));
                                        booking_address = info_Object.getString("service_address");
                                        pojo.setJob_status(info_Object.getString("status"));
                                        Job_Status = info_Object.getString("status");
//                                        pojo.setService_type(info_Object.getString("service_type"));
                                        pojo.setWork_type(info_Object.getString("job_type"));
                                        pojo.setInstructions(info_Object.getString("instruction"));
//                                        pojo.setDo_cancel(info_Object.getString("do_cancel"));
//                                        pojo.setNeed_payment(info_Object.getString("need_payment"));
                                        pojo.setSubmit_ratings(info_Object.getString("submit_ratings"));

                                        Str_usermobile = info_Object.getString("user_mobile");
                                        Str_Userid = info_Object.getString("user_id");
                                        Str_jobusername = info_Object.getString("user_name");
                                        Str_userimg = info_Object.getString("user_image");
                                        destination_lat = info_Object.getString("location_lat");
                                        destination_long = info_Object.getString("location_lon");
                                        Str_user_email = info_Object.getString("user_email");

                                        sessionManager.setLatitudeUserId(destination_lat);
                                        sessionManager.setLongUserId(destination_long);

                                        sBtn_group = info_Object.getString("btn_group");
                                        JSONArray billingobject = info_Object.getJSONArray("billing");
                                        farelist = new ArrayList<PaymentFareSummeryPojo>();
                                        if (billingobject.length() > 0) {
                                            paymentInvoiceLL.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < billingobject.length(); i++) {
                                                JSONObject elem = billingobject.getJSONObject(i);
                                                PaymentFareSummeryPojo pfPojo = new PaymentFareSummeryPojo();
                                                pfPojo.setPayment_title(elem.getString("title"));
                                                pfPojo.setPayment_amount(elem.getString("amount"));
                                                pfPojo.setDt(elem.getString("dt"));
                                                pfPojo.setCurrencycode(myCurrencySymbol);
                                                farelist.add(pfPojo);
                                            }
                                            MyTaskDetailsFareSummeryAdapter adapter = new MyTaskDetailsFareSummeryAdapter(MyTaskDetails.this, farelist);
                                            paymentInvoiceRV.setAdapter(adapter);
                                        } else {
                                            paymentInvoiceLL.setVisibility(View.GONE);
                                        }
                                        if (info_Object.has("cancelreason")) {
                                            cancel_reason = info_Object.getString("cancelreason");
                                        }
                                        infoList.add(pojo);
                                    }
                                } else {
                                    infoList.clear();
                                }

                                Object check_timeline_object = response_Object.get("timeline");
                                if (check_timeline_object instanceof JSONArray) {
                                    JSONArray timeline_Array = response_Object.getJSONArray("timeline");
                                    if (timeline_Array.length() > 0) {
                                        timeLineList.clear();
                                        for (int i = 0; i < timeline_Array.length(); i++) {
                                            JSONObject timeline_Object = timeline_Array.getJSONObject(i);
                                            if (timeline_Object.length() > 0) {
                                                WorkFlow_Pojo timePojo = new WorkFlow_Pojo();
                                                timePojo.setJob_title(timeline_Object.getString("title"));
                                                timePojo.setJob_date(timeline_Object.getString("date"));
                                                timePojo.setJob_time(timeline_Object.getString("time"));
                                                timePojo.setJobs_check(timeline_Object.getString("check"));
                                                timeLineList.add(timePojo);
                                            }
                                        }
                                    }
                                    final MyTaskDetailTimeLineAdapter adapter = new MyTaskDetailTimeLineAdapter(MyTaskDetails.this, timeLineList);
                                    timeLineRV.setAdapter(adapter);
                                }
                            }
                        }
                    }

                    if (sStatus.equalsIgnoreCase("1")) {
                        if (infoList.size() > 0) {
                            myTaskDetail_headerBar_category_textView.setText(infoList.get(0).getWork_type() + " - ");
                            myTaskDetail_headerBar_job_id_textView.setText(sJobID);
                            myTaskDetailsJobStatusTV.setText(infoList.get(0).getJob_status());
                            myTaskDetailsDateTimeTV.setText(infoList.get(0).getBook_date() + "," + infoList.get(0).getBook_time());
                            myTaskDetailsLocationTV.setText(booking_address);
                            myTaskDetailsInstructionTV.setText(infoList.get(0).getInstructions());
                            if (infoList.get(0).getInstructions().equals("")) {
                                instructionLL.setVisibility(View.GONE);
                            }
                            if (!cancel_reason.equalsIgnoreCase("")) {
                                cancel_layout.setVisibility(View.VISIBLE);
                                myTaskDetailsCancellaionTV.setText(cancel_reason);
                            } else {
                                cancel_layout.setVisibility(View.GONE);
                            }
//                            Picasso.with(MyTaskDetails.this).load(infoList.get(0).getProvider_image()).error(R.drawable.placeholder_icon)
//                                    .placeholder(R.drawable.placeholder_icon).fit().into(providerimg);
//                            myTaskDetailsReviewsTV.setText(infoList.get(0).getProvider_ratings());
                            setBackgroundDrawableAction(sBtn_group);
                        }

                    } else {
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loaderRL.setVisibility(View.GONE);
                }
            }

            @Override
            public void onErrorListener() {
                loaderRL.setVisibility(View.GONE);
            }
        });
    }

    private void startLoading() {
        mLoadingDialog = new LoadingDialog(MyTaskDetails.this);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoadingDialog.show();
    }

    private void stopLoading() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog.dismiss();
            }
        }, 500);
    }

    //------Alert Method-----
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(MyTaskDetails.this);
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

    private void setBackgroundDrawableAction(String sBtn_group) {
//        int[] backgroungInt = {0, R.drawable.mytask_accept_bg, R.drawable.mytask_startoff_bg,
//                R.drawable.mytask_arraived_bg, R.drawable.mytask_payment_bg, R.drawable.mytask_payment_bg,
//                R.drawable.mytask_gray_bg, R.drawable.mytask_gray_bg, R.drawable.mytask_gray_bg};

        int[] backgroungInt = {0, R.drawable.accept_curve, R.drawable.accept_curve,
                R.drawable.accept_curve, R.drawable.accept_curve, R.drawable.accept_curve,
                R.drawable.accept_curve, R.drawable.accept_curve, R.drawable.accept_curve};

        int[] statusBackgroungInt = {0, R.drawable.mytask_new_leads_yellow, R.drawable.mytask_thumb_up_yellow,
                R.drawable.mytask_thumb_up_yellow, R.drawable.mytask_thumb_up_yellow, R.drawable.mytask_thumb_up_green,
                R.drawable.mytask_tick_check_yellow, R.drawable.tick_checked, R.drawable.mytask_cancelled_red};

        String[] showText = new String[]{"", getResources().getString(R.string.ongoing_detail_accept_label),
                getResources().getString(R.string.ongoing_detail_startoff_label), getResources().getString(R.string.ongoing_detail_arrived_label),
                getResources().getString(R.string.ongoing_detail_startjob_label), getResources().getString(R.string.ongoing_detail_jobcompleted_label),
                getResources().getString(R.string.myjobs_ongoing_details_job_completed_txt), getResources().getString(R.string.myjobs_ongoing_details_payment_completed_txt),
                getResources().getString(R.string.ongoing_detail_canceled_job_label)};

        actionmainLL.setVisibility(View.VISIBLE);
//        if (sBtn_group.equalsIgnoreCase("6")) {
//            actionmainLL.setVisibility(View.GONE);
//        }
        callLL.setVisibility(View.VISIBLE);
        chatLL.setVisibility(View.VISIBLE);
        mailBoxLL.setVisibility(View.VISIBLE);
        smsLL.setVisibility(View.VISIBLE);
        actionLL.setVisibility(View.VISIBLE);
        if (sBtn_group.equalsIgnoreCase("8")) {
            actionLL.setVisibility(View.GONE);
            actionmainLL.setVisibility(View.GONE);

        }

        if (sBtn_group.equalsIgnoreCase("1")) {
            cancelActionTV.setBackgroundResource(R.drawable.cancel_curve);
            cancelActionTV.setText(getResources().getString(R.string.ongoing_detail_rejectlabel));
            callLL.setVisibility(View.GONE);
            chatLL.setVisibility(View.GONE);
        } else {
            cancelActionTV.setBackgroundResource(R.drawable.cancel_curve);
            cancelActionTV.setText(getResources().getString(R.string.ongoing_detail_cancelbtn_label));
            if (!sBtn_group.equalsIgnoreCase("0")) {
                callLL.setVisibility(View.VISIBLE);
                chatLL.setVisibility(View.VISIBLE);
            }
        }
        if (sBtn_group.equalsIgnoreCase("6")
                || sBtn_group.equalsIgnoreCase("7")) {
            timeLineView.setVisibility(View.VISIBLE);
        } else {
            timeLineView.setVisibility(View.GONE);
        }

        detail_chats_relativeLayout.setVisibility(View.VISIBLE);
        extraCommunicationFAB.setVisibility(View.GONE);
        if (sBtn_group.equalsIgnoreCase("8")) {
            detail_chats_relativeLayout.setVisibility(View.GONE);
        }

        if (sBtn_group.equalsIgnoreCase("4")
                || sBtn_group.equalsIgnoreCase("5")
                || sBtn_group.equalsIgnoreCase("6")
                || sBtn_group.equalsIgnoreCase("7")
                || sBtn_group.equalsIgnoreCase("8")) {
            cancelActionTV.setVisibility(View.GONE);
        }
        submitActionTV.setVisibility(View.VISIBLE);
        submitActionTV.setBackgroundResource(backgroungInt[Integer.parseInt(sBtn_group)]);
        submitActionTV.setText(showText[Integer.parseInt(sBtn_group)]);
        statusIconIV.setBackgroundResource(statusBackgroungInt[Integer.parseInt(sBtn_group)]);

        if (sBtn_group.equals("7") || sBtn_group.equals("8")) {
            chatButton.setVisibility(View.INVISIBLE);
        }
//        actionmainLL.setVisibility(View.VISIBLE);
////        if (sBtn_group.equalsIgnoreCase("6")) {
////            actionmainLL.setVisibility(View.GONE);
////        }
////        callLL.setVisibility(View.VISIBLE);
////        chatLL.setVisibility(View.VISIBLE);
//        mailBoxLL.setVisibility(View.VISIBLE);
//        smsLL.setVisibility(View.VISIBLE);
//        actionLL.setVisibility(View.VISIBLE);

        if (sBtn_group.equalsIgnoreCase("6") || sBtn_group.equalsIgnoreCase("7")) {
            callLL.setVisibility(View.GONE);
            chatLL.setVisibility(View.VISIBLE);
            mailBoxLL.setVisibility(View.GONE);
            smsLL.setVisibility(View.GONE);
            submitActionTV.setVisibility(View.GONE);
            actionLL.setVisibility(View.GONE);
        }

        if (sBtn_group.equals("7") && infoList.get(0).getSubmit_ratings().equalsIgnoreCase("NO")) {
            actionLL.setVisibility(View.GONE);
        }

        if (sBtn_group.equals("2") || sBtn_group.equals("3")) {
            trackingLL.setVisibility(View.VISIBLE);
        } else {
            trackingLL.setVisibility(View.GONE);
        }

        loaderRL.setVisibility(View.GONE);
        nestedSrollView.scrollTo(0, nestedSrollView.getBottom());

        nestedSrollView.post(new Runnable() {
            @Override
            public void run() {
                nestedSrollView.fling(nestedSrollView.getBottom());
                nestedSrollView.smoothScrollTo(0, nestedSrollView.getBottom());
            }
        });

//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        if (behavior != null) {
//            behavior.onNestedFling(coordinatorLayout, appBarLayout, null, 0, 10000, true);
//        }

    }

    private void Materail_Add_New_Alert() {
        item_add.clear();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen
        moreAddressView = View.inflate(MyTaskDetails.this, R.layout.matrial_add_new_dialog_design, null);
        moreAddressDialog = new Dialog(MyTaskDetails.this);
        moreAddressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moreAddressDialog.setContentView(moreAddressView);
        moreAddressDialog.setCanceledOnTouchOutside(false);
        moreAddressDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        moreAddressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window dialogWindow = moreAddressDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 0;
        lp.x = 0;
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);

        meterialItemsRV = (RecyclerView) moreAddressView.findViewById(R.id.listMaterialRV);
        Button addItemsBTN = (Button) moreAddressView.findViewById(R.id.addItemsBTN);
        final Button doneBTN = (Button) moreAddressView.findViewById(R.id.doneBTN);
        final TextView materialDescriptionTV = (TextView) moreAddressView.findViewById(R.id.materialDescriptionTV);
        RelativeLayout backRL = (RelativeLayout) moreAddressView.findViewById(R.id.register_header_back_layout);

        meterialItemsRV.setHasFixedSize(true);
        meterialItemsRV.setLayoutManager(new LinearLayoutManager(this));
        meterialItemsRV.setNestedScrollingEnabled(false);

        doneBTN.setText(getResources().getString(R.string.material_add_dailog_text_skip));
        materialDescriptionTV.setText(getResources().getString(R.string.add_material_used));

        addItemsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Tname = "";

                boolean isItemEmpty = true;
                if (item_add.size() > 0) {
                    for (int i = 0; i < item_add.size(); i++) {
                        if (item_add.get(i).getToolname().equalsIgnoreCase("")) {
                            Tname = getResources().getString(R.string.ongoing_detail_toolname);
                            isItemEmpty = false;
                            break;
                        }
                        if (item_add.get(i).getToolcost().equalsIgnoreCase("")) {
                            Tname = getResources().getString(R.string.ongoing_detail_toolcost);
                            isItemEmpty = false;
                            break;
                        }
                    }
                }

                if (isItemEmpty) {
                    Addmaterialpojo pojo = new Addmaterialpojo();
                    pojo.setToolname("");
                    pojo.setToolcost("");
                    pojo.setValuesPosition(item_add.size());
                    item_add.add(pojo);
                    rVlAdapter = new MateriaNewRVlAdapter(MyTaskDetails.this, item_add, myCurrencySymbol, new MateriaNewRVlAdapter.RefreshDeleteArray() {
                        @Override
                        public void Update(int position) {
                            item_add.remove(position);
                            rVlAdapter.notifyDataSetChanged();

                            if (item_add.size() > 0) {
                                doneBTN.setText(getResources().getString(R.string.material_add_dailog_text_done));
                                materialDescriptionTV.setText(getResources().getString(R.string.add_material_click));
                            } else {
                                doneBTN.setText(getResources().getString(R.string.material_add_dailog_text_skip));
                                materialDescriptionTV.setText(getResources().getString(R.string.add_material_used));
                            }
                        }

                        @Override
                        public void UpdateEditName(int position, String name, boolean isPerformClicked) {
                            item_add.get(position).setToolname(name);
                            item_add.get(position).setToolcost(item_add.get(position).getToolcost());
                            item_add.get(position).setValuesPosition(position);
                        }

                        @Override
                        public void UpdateEditCost(int position, String cost, boolean isPerformClicked) {
                            item_add.get(position).setToolname(item_add.get(position).getToolname());
                            item_add.get(position).setToolcost(cost);
                            item_add.get(position).setValuesPosition(position);
                        }
                    });
                    meterialItemsRV.setAdapter(rVlAdapter);
                } else {
                    Toast.makeText(MyTaskDetails.this, Tname, Toast.LENGTH_SHORT).show();
                }

                if (item_add.size() > 0) {
                    doneBTN.setText(getResources().getString(R.string.material_add_dailog_text_done));
                    materialDescriptionTV.setText(getResources().getString(R.string.add_material_click));
                } else {
                    doneBTN.setText(getResources().getString(R.string.material_add_dailog_text_skip));
                    materialDescriptionTV.setText(getResources().getString(R.string.add_material_used));
                }
            }
        });

        doneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item_add.size() == 0) {
                    if (cd.isConnectingToInternet()) {
                        moreAddressDialog.dismiss();
                        jobCompletewithoutmaterial(MyTaskDetails.this, ServiceConstant.JOBCOMPLETE_URL, "jobcomplete");
                        System.out.println("--------------completejob url-------------------" + ServiceConstant.JOBCOMPLETE_URL);
                    } else {
                        Toast.makeText(MyTaskDetails.this, getResources().getString(R.string.alert_nointernet), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String Tname = "";

                    boolean isItemEmpty = true;
                    if (item_add.size() > 0) {
                        for (int i = 0; i < item_add.size(); i++) {
                            if (item_add.get(i).getToolname().equalsIgnoreCase("")) {
                                Tname = getResources().getString(R.string.ongoing_detail_toolname);
                                isItemEmpty = false;
                                break;
                            }
                            if (item_add.get(i).getToolcost().equalsIgnoreCase("")) {
                                Tname = getResources().getString(R.string.ongoing_detail_toolcost);
                                isItemEmpty = false;
                                break;
                            }
                        }
                    }
                    if (isItemEmpty) {
                        if (cd.isConnectingToInternet()) {
                            moreAddressDialog.dismiss();
                            jobCompletewithmaterial(MyTaskDetails.this, ServiceConstant.JOBCOMPLETE_URL, "jobcomplete");
                            System.out.println("--------------completejob url-------------------" + ServiceConstant.JOBCOMPLETE_URL);
                        } else {
                            Toast.makeText(MyTaskDetails.this, getResources().getString(R.string.alert_nointernet), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MyTaskDetails.this, Tname, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        backRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreAddressDialog.dismiss();
            }
        });
        moreAddressDialog.show();

    }


    public void Materail_Add_ALert() {

        item_add.clear();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen
        moreAddressView = View.inflate(MyTaskDetails.this, R.layout.material_add_design, null);
        moreAddressDialog = new Dialog(MyTaskDetails.this);
        moreAddressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moreAddressDialog.setContentView(moreAddressView);
        moreAddressDialog.setCanceledOnTouchOutside(false);
        moreAddressDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        moreAddressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addmaterial = (CheckBox) moreAddressView.findViewById(R.id.check_box);
        list = (ListView) moreAddressView.findViewById(R.id.list);
        final RelativeLayout aAddFieldLAY = (RelativeLayout) moreAddressView.findViewById(R.id.add_fields_layout);
        aCancelLAY = (RelativeLayout) moreAddressView.findViewById(R.id.cancel_layout);
        aOKLAY = (RelativeLayout) moreAddressView.findViewById(R.id.add_one_layout);
        Window dialogWindow = moreAddressDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 0;
        lp.x = 0;
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
        listItems = new ArrayList<>();

        addmaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Addmaterialpojo pojo = new Addmaterialpojo();
                    pojo.setToolname("name");
                    pojo.setToolcost("cost");
                    item_add.add(pojo);
                    listItems.add("1");
                    aAdapter = new MaterialAddNewAdapter(MyTaskDetails.this, listItems, list, addmaterial, item_add);
                    list.setAdapter(aAdapter);
                    // setListViewHeightBasedOnChildren(list);
                    aAdapter.notifyDataSetChanged();
                    aAddFieldLAY.setVisibility(View.GONE);
                } else {
                    aAddFieldLAY.setVisibility(View.GONE);
                    listItems.clear();
                    aAdapter.notifyDataSetChanged();
                    aOKLAY.setVisibility(View.VISIBLE);
                    aCancelLAY.setVisibility(View.VISIBLE);
                }
            }
        });

        aOKLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (addmaterial.isChecked()) {

                    if (item_add.size() >= 0) {
                        if (aAdapter != null) {
                            aAdapter.notifyDataSetChanged();

                            ArrayList<EditText> aToolNameET = aAdapter.getToolname();
                            if (aToolNameET.size() == 0) {
                                return;
                            }
                            ArrayList<EditText> aToolCost = aAdapter.getTool_cost();
                            Addmaterialpojo pojo = new Addmaterialpojo();
                            if (aToolNameET.get(0).getText().toString().length() == 0) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.ongoing_detail_toolname), Toast.LENGTH_SHORT).show();
                            } else if (aToolCost.get(0).getText().toString().length() == 0) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.ongoing_detail_toolcost), Toast.LENGTH_SHORT).show();
                                return;
                            } else {

                                pojo.setToolname(aToolNameET.get(0).getText().toString());
                                pojo.setToolcost(aToolCost.get(0).getText().toString());
                                if (!item_add_bollean) {
                                    item_add.add(pojo);
                                }

                                for (int i = 0; i < item_add.size(); i++) {
                                    Log.e("name", item_add.get(i).getToolname());
                                    Log.e("cost", item_add.get(i).getToolcost());
                                }
                                // createArrayFormat();
                                if (cd.isConnectingToInternet()) {
                                    jobCompletewithmaterial(MyTaskDetails.this, ServiceConstant.JOBCOMPLETE_URL, "jobcomplete");
                                    System.out.println("--------------completejob url-------------------" + ServiceConstant.JOBCOMPLETE_URL);
                                } else {
                                    Toast.makeText(MyTaskDetails.this, getResources().getString(R.string.alert_nointernet), Toast.LENGTH_SHORT).show();
                                }

                            }

                        }
                    }

                }
                if (!addmaterial.isChecked()) {
                    if (cd.isConnectingToInternet()) {
                        jobCompletewithoutmaterial(MyTaskDetails.this, ServiceConstant.JOBCOMPLETE_URL, "jobcomplete");
                        System.out.println("--------------completejob url-------------------" + ServiceConstant.JOBCOMPLETE_URL);
                    } else {
                        Toast.makeText(MyTaskDetails.this, getResources().getString(R.string.alert_nointernet), Toast.LENGTH_SHORT).show();
                    }
                }
                moreAddressDialog.dismiss();
            }
        });
        aAddFieldLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                item_add_bollean = false;
                ArrayList<EditText> aToolNameET = aAdapter.getToolname();
                ArrayList<EditText> aToolCost = aAdapter.getTool_cost();
                Addmaterialpojo pojo = new Addmaterialpojo();
                if (aToolNameET.get(0).getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.ongoing_detail_toolname), Toast.LENGTH_SHORT).show();
                } else if (aToolCost.get(0).getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.ongoing_detail_toolcost), Toast.LENGTH_SHORT).show();

                } else {
                    pojo.setToolname(aToolNameET.get(0).getText().toString());
                    pojo.setToolcost(aToolCost.get(0).getText().toString());
                    item_add.add(pojo);

                    if (listItems.size() > 0) {
                        listItems.add("1");
                        setListViewHeightBasedOnChildren(list);
                        aAdapter.notifyDataSetChanged();
                    }
                }


            }
        });
        aCancelLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                moreAddressDialog.dismiss();
            }
        });
        moreAddressDialog.show();
    }

    //------------------------Job Complete------------------
    private void jobCompletewithmaterial(Context mContext, String url, String key) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", sProviderID);
        jsonParams.put("job_id", sJobID);
        jsonParams.put("summary", "");
        for (int i = 0; i < item_add.size(); i++) {
            jsonParams.put("miscellaneous[" + i + "][name]", item_add.get(i).getToolname());
            jsonParams.put("miscellaneous[" + i + "][price]", item_add.get(i).getToolcost());

            System.out.println("miscellaneous[" + i + "][name]" + item_add.get(i).getToolname());
            System.out.println("miscellaneous[" + i + "][price]" + item_add.get(i).getToolcost());
        }

        System.out.println("provider_id-----------" + sProviderID);
        System.out.println("job_id-----------" + sJobID);

        final LoadingDialog dialog = new LoadingDialog(MyTaskDetails.this);
        dialog.setLoadingTitle(getResources().getString(R.string.loading_in));
        dialog.show();

        ServiceRequest mservicerequest = new ServiceRequest(mContext);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("jobcomplete-----------" + response);
                Log.e("jobcomplete", response);

                String Str_status = "", Str_response = "", Str_message = "", Str_btn_group = "";

                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_status = jobject.getString("status");
                    Str_message = jobject.getString("message");
                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject object = jobject.getJSONObject("response");
                       // Str_message = object.getString("message");
                        Str_btn_group = object.getString("btn_group");

                    } else {
                        Str_response = jobject.getString("response");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

                if (Str_status.equalsIgnoreCase("1")) {

                    if (Str_btn_group.equalsIgnoreCase("6")) {
                        cancelActionTV.setVisibility(View.GONE);
                        submitActionTV.setVisibility(View.GONE);
                        submitActionTV.setText(getResources().getString(R.string.myjobs_ongoing_details_job_completed_txt));

                        Intent refreshBroadcastIntent = new Intent();
                        refreshBroadcastIntent.setAction("com.package.refresh.MyJobDetails");
                        sendBroadcast(refreshBroadcastIntent);

//                        Intent intent = new Intent(context, PaymentFareSummery.class);
//                        intent.putExtra("JobId", sJobID);
//                        context.startActivity(intent);
                        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                    Toast.makeText(MyTaskDetails.this, Str_message, Toast.LENGTH_SHORT).show();

                } else {
//                    AlertShow(context.getResources().getString(R.string.alert_label_title), Str_response, context);
                    Toast.makeText(MyTaskDetails.this, Str_response, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onErrorListener() {

                dialog.dismiss();

            }
        });

    }

    //------------------------Job Complete------------------
    private void jobCompletewithoutmaterial(Context mContext, String url, String key) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", sProviderID);
        jsonParams.put("job_id", sJobID);
        jsonParams.put("summary", "");
        //jsonParams.put("miscellaneous", jsonformat);
        //  jsonParams.put("cost",Et_job_cost.getText().toString());

        System.out.println("provider_id-----------" + sProviderID);
        System.out.println("job_id-----------" + sJobID);

        final LoadingDialog dialog = new LoadingDialog(MyTaskDetails.this);
        dialog.setLoadingTitle(getResources().getString(R.string.loading_in));
        dialog.show();

        ServiceRequest mservicerequest = new ServiceRequest(mContext);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("jobcomplete-----------" + response);
                Log.e("jobcomplete", response);

                String Str_status = "", Str_response = "", Str_message = "", Str_btn_group = "";

                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_status = jobject.getString("status");

                    Str_message = jobject.getString("message");
                   // Str_btn_group = jobject.getString("btn_group");

                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject object = jobject.getJSONObject("response");
//                        Str_message = object.getString("message");
//                        Str_btn_group = object.getString("btn_group");

                    } else {
                        Str_response = jobject.getString("response");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

                if (Str_status.equalsIgnoreCase("1")) {

                    if (Str_btn_group.equalsIgnoreCase("6")) {
                        cancelActionTV.setVisibility(View.GONE);
                        submitActionTV.setVisibility(View.GONE);
                        submitActionTV.setText(getResources().getString(R.string.myjobs_ongoing_details_job_completed_txt));

//                        Intent intent = new Intent(MyTaskDetails.this, PaymentFareSummery.class);
//                        intent.putExtra("JobId", sJobID);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        if (cd.isConnectingToInternet()) {
                            JobDetailRequest(ServiceConstant.MYJOB_DETAIL_INFORMATION_URL, "0");
                            System.out.println("mjobdeetail---------" + ServiceConstant.MYJOB_DETAIL_INFORMATION_URL);
                        } else {
                            Toast.makeText(MyTaskDetails.this, getResources().getString(R.string.action_no_internet_message), Toast.LENGTH_SHORT).show();
                        }
                    }

                    Toast.makeText(MyTaskDetails.this, Str_message, Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(MyTaskDetails.this, Str_response, Toast.LENGTH_SHORT).show();
//                    Alert(getResources().getString(R.string.alert_label_title), Str_response);
                }

            }

            @Override
            public void onErrorListener() {

                dialog.dismiss();

            }
        });

    }

    //---------------------------------------------Material Fees Submit Request--------------------------------------------------------

    public static void SubmitMaterialFees(ArrayList<Materialcostsubmitpojo> arrayList,
                                          final Context context) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", sProviderID);
        jsonParams.put("job_id", sJobID);
        jsonParams.put("summary", "");
        for (int i = 0; i < arrayList.size(); i++) {
            jsonParams.put("miscellaneous[" + i + "][name]", arrayList.get(i).getToolname());
            jsonParams.put("miscellaneous[" + i + "][price]", arrayList.get(i).getToolcost());

            System.out.println("miscellaneous[" + i + "][name]" + arrayList.get(i).getToolname());
            System.out.println("miscellaneous[" + i + "][price]" + arrayList.get(i).getToolcost());
        }

        System.out.println("provider_id-----------" + sProviderID);
        System.out.println("job_id-----------" + sJobID);

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.setLoadingTitle(context.getResources().getString(R.string.loading_in));
        dialog.show();

        ServiceRequest mservicerequest = new ServiceRequest(context);
        mservicerequest.makeServiceRequest(ServiceConstant.JOBCOMPLETE_URL, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("jobcomplete-----------" + response);
                Log.e("jobcomplete", response);

                String Str_status = "", Str_response = "", Str_message = "", Str_btn_group = "";

                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_status = jobject.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject object = jobject.getJSONObject("response");
                        Str_message = object.getString("message");
                        Str_btn_group = object.getString("btn_group");

                    } else {
                        Str_response = jobject.getString("response");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

                if (Str_status.equalsIgnoreCase("1")) {

                    if (Str_btn_group.equalsIgnoreCase("6")) {
                        cancelActionTV.setVisibility(View.GONE);
                        submitActionTV.setVisibility(View.GONE);
                        submitActionTV.setText(context.getResources().getString(R.string.ongoing_detail_more_label));

                        Intent refreshBroadcastIntent = new Intent();
                        refreshBroadcastIntent.setAction("com.package.refresh.MyJobDetails");
                        context.sendBroadcast(refreshBroadcastIntent);

//                        Intent intent = new Intent(context, PaymentFareSummery.class);
//                        intent.putExtra("JobId", sJobID);
//                        context.startActivity(intent);
                        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                    Toast.makeText(context, Str_message, Toast.LENGTH_SHORT).show();

                } else {
//                    AlertShow(context.getResources().getString(R.string.alert_label_title), Str_response, context);
                    Toast.makeText(context, Str_response, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onErrorListener() {

                dialog.dismiss();

            }
        });
    }

    //-----------------------code for detail ButtonAction Post request---------------------
    private void buttonsClickActions(Context mContext, String url, final String key) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", sProviderID);
        jsonParams.put("job_id", sJobID);

        if (key.equalsIgnoreCase("accept")) {
            jsonParams.put("provider_lat", String.valueOf(MyCurrent_lat));
            jsonParams.put("provider_lon", String.valueOf(MyCurrent_long));
        }
        if (key.equalsIgnoreCase("startoff")) {
            jsonParams.put("provider_lat", String.valueOf(MyCurrent_lat));
            jsonParams.put("provider_lon", String.valueOf(MyCurrent_long));
        }

        System.out.println("curentlat-----------" + MyCurrent_lat);
        System.out.println("curentlong-----------" + MyCurrent_long);
        System.out.println("provider_id-----------" + sProviderID);
        System.out.println("job_id-----------" + sJobID);

        final LoadingDialog dialog = new LoadingDialog(MyTaskDetails.this);
        dialog.setLoadingTitle(getResources().getString(R.string.loading_in));
        dialog.show();

        ServiceRequest mservicerequest = new ServiceRequest(mContext);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {

                Log.e("clickresponse------", response);

                String Str_status = "", username = "", user_email = "", user_phoneno = "", user_img = "", user_reviwe = "", jobId = "", user_location = "", jobtime = "", job_lat = "", job_long = "",
                        Str_message = "", Str_btngroup = "";

                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_status = jobject.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject object = jobject.getJSONObject("response");
                        Str_message = object.getString("message");
                        Str_btngroup = object.getString("btn_group");

                        Intent refreshIntent = new Intent();
                        refreshIntent.setAction("com.refresh.MyJob_Fragments");
                        sendBroadcast(refreshIntent);

//                        Toast.makeText(getApplicationContext(), Str_message, Toast.LENGTH_SHORT).show();

                        if (key.equalsIgnoreCase("accept")) {
                            Intent homepage = new Intent();
                            homepage.setAction("com.package.refresh.experthomepage");
                            sendBroadcast(homepage);
                        }

                    } else {
                        Str_message = jobject.getString("response");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                if (Str_status.equalsIgnoreCase("1")) {

                    if (cd.isConnectingToInternet()) {
                        JobDetailRequest(ServiceConstant.MYJOB_DETAIL_INFORMATION_URL, "1");
                        System.out.println("mjobdeetail---------" + ServiceConstant.MYJOB_DETAIL_INFORMATION_URL);
                    } else {
                        Toast.makeText(MyTaskDetails.this, getResources().getString(R.string.action_no_internet_message), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MyTaskDetails.this, Str_message, Toast.LENGTH_SHORT).show();
//                    Alert(getResources().getString(R.string.server_lable_header), Str_message);
                }
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();

            }
        });
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

    private void sms_sendMsg(String share_text) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                // Marshmallow+
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.putExtra("address", Str_usermobile);
                intent.putExtra("sms_body", share_text);
                intent.setData(Uri.parse("smsto:" + Str_usermobile));
                startActivity(intent);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", Str_usermobile, null)).putExtra("sms_body", share_text));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CheckPermissions() {
        if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission()) {
            requestPermissionLocation();
        } else {
            enableGpsService();
        }
    }

    private boolean checkAccessFineLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermissionLocation() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_LOCATION);
    }

    private boolean checkAccessCoarseLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void enableGpsService() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final com.google.android.gms.common.api.Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
//                        Intent intent = new Intent(MyTaskDetails.this, TrackYourRide.class);
//                        intent.putExtra("LAT", destination_lat);
//                        intent.putExtra("LONG", destination_long);
//                        intent.putExtra("Userid", Str_Userid);
//                        intent.putExtra("tasker", sProviderID);
//                        intent.putExtra("task", mTaskID);
//                        intent.putExtra("address", booking_address);
//                        intent.putExtra("user_image", Str_userimg);
//                        intent.putExtra("user_name", Str_jobusername);
//                        startActivity(intent);

                        String lat = sessionManager.getLatitudeUserId();
                        String log = sessionManager.getLongUserId();
                        Intent intent = new Intent(MyTaskDetails.this, MapUser.class);
                        intent.putExtra("User_lat", lat);
                        intent.putExtra("user_log", log);
                        intent.putExtra("user_location", myTaskDetailsLocationTV.getText().toString());
//
                        System.out.println("Str_Userid-----------" + sProviderID);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MyTaskDetails.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }

    private void setLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (gps != null && gps.canGetLocation() && gps.isgpsenabled()) {
        }

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            myLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
        //  mHandler.removeCallbacks(mHandlerTask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishReceiver);
    }


}
