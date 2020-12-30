package core.Xmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperlady.R;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.Utils.SocketCheckService;
import com.hoperlady.app.NavigationDrawer;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import core.Dialog.LoadingDialog;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;
import core.socket.ChatMessageService;
import core.socket.SocketHandler;

/**
 * Created by user88 on 1/11/2016.
 */
public class Xmpp_PushNotificationPage extends AppCompatActivity {
    private TextView Message_Tv, Textview_Ok, Textview_alert_header;
    private String message = "", action = "", Str_amount = "", currency_code = "", Str_JobId = "", Amount = "";
    private RelativeLayout Rl_layout_alert_ok;
    private ImageView Img_notification;
    private String Page_Status = "";
    NavigationDrawer drawer;
    private String provider_id = "", provider_name = "";
    private SessionManager session;
    private String key0 = "";

    LoadingDialog mLoadingDialog;

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.finish_PUSHNOTIFIACTION")) {
                finish();
            }
        }
    }

    private RefreshReceiver refreshReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.pushnotification);
        session = new SessionManager(this);
        HashMap<String, String> user = session.getUserDetails();
        provider_id = user.get(SessionManager.KEY_PROVIDERID);
        initialize();

        System.out.println("pushnotification--------");

        Rl_layout_alert_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("action-----------" + action);
                if (action.equalsIgnoreCase(ServiceConstant.Accout_deleted)) {
                    postRequest_Logout(ServiceConstant.logout_url);
                }

//                if (action.equalsIgnoreCase("job_request")) {
//                    System.out.println("jobid-------" + Str_JobId);
//                    Intent intent = new Intent(Xmpp_PushNotificationPage.this, MyTaskDetails.class);
//                    intent.putExtra("JobId", Str_JobId);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//
//
//                } else if (action.equalsIgnoreCase("job_cancelled")) {
//
//                    Intent refreshBroadcastIntent = new Intent();
//                    refreshBroadcastIntent.setAction("com.package.finish.jobdetailpage");
//                    sendBroadcast(refreshBroadcastIntent);
//                    finish();
//
//                } else if (action.equalsIgnoreCase("receive_cash")) {
//                    System.out.println("recwivecashcxmpp-------");
//
//                    Intent intent = new Intent(Xmpp_PushNotificationPage.this, ReceiveCashPage.class);
//                    intent.putExtra("Amount", Amount);
//                    intent.putExtra("jobId", Str_JobId);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//
//                } else if (action.equalsIgnoreCase("payment_paid")) {
//                    Intent broadcastIntentnavigation = new Intent();
//                    broadcastIntentnavigation.setAction("com.package.finish_LOADINGPAGE");
//                    sendBroadcast(broadcastIntentnavigation);
//
//                    Intent intent = new Intent(Xmpp_PushNotificationPage.this, ReviwesPage.class);
//                    intent.putExtra("jobId", Str_JobId);
//                    System.out.println("jobId-------" + Str_JobId);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                } else if (action.equalsIgnoreCase("rejecting_task")) {
//
//                    Intent refreshBroadcastIntent = new Intent();
//                    refreshBroadcastIntent.setAction("com.package.finish.jobdetailpage");
//                    sendBroadcast(refreshBroadcastIntent);
//
//                    finish();
//
//                } else if (action.equalsIgnoreCase("admin_notification")) {
//
//                    finish();
//
//                } else if (action.equalsIgnoreCase("Left_job")) {
//
//                    finish();
//                } else if (action.equalsIgnoreCase("Accept_task")) {
//                    Intent pending_task = new Intent(Xmpp_PushNotificationPage.this, NewLeadsPage.class);
//                    startActivity(pending_task);
//                    finish();
//                } else if (action.equalsIgnoreCase("Admin Availability Change")) {
//
//                    Intent s = new Intent();
//                    s.setAction("com.availability.change");
//                    s.putExtra("status", Str_JobId);
//                    sendBroadcast(s);
//                    if (Page_Status.equalsIgnoreCase("open")) {
//                        finish();
//                    } else {
//
//                        Intent i = new Intent(getApplicationContext(), NavigationDrawer.class);
//                        startActivity(i);
//                        finish();
//
//                    }
//
//
//                } else if (action.equalsIgnoreCase("partially_paid")) {
//                    finish();
//                }
            }
        });
    }


    private void initialize() {
        Intent i = getIntent();
        message = i.getStringExtra("Message");
        action = i.getStringExtra("Action");
        Str_JobId = i.getStringExtra("JobId");
        Str_amount = i.getStringExtra("amount");
        currency_code = i.getStringExtra("Currencycode");
        key0 = i.getStringExtra("key0");

        HashMap<String, String> user = session.getUserDetails();

        Page_Status = user.get(SessionManager.NAVIGATION_OPEN);
        // -----code to finish using broadcast receiver-----
        refreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.finish_PUSHNOTIFIACTION");
        registerReceiver(refreshReceiver, intentFilter);

        Textview_Ok = (TextView) findViewById(R.id.pushnotification_alert_ok_textview);
        Message_Tv = (TextView) findViewById(R.id.pushnotification_alert_messge_textview);
        Textview_alert_header = (TextView) findViewById(R.id.pushnotification_alert_messge_label);
        Rl_layout_alert_ok = (RelativeLayout) findViewById(R.id.pushnotification_alert_ok);
        Img_notification = (ImageView) findViewById(R.id.notification_icon);

        Message_Tv.setText(message);

//        if (action.equalsIgnoreCase("job_cancelled")) {
//            Textview_alert_header.setText(getResources().getString(R.string.label_pushnotification_canceled));
//
//        } else if (action.equalsIgnoreCase("receive_cash")) {
//            Textview_alert_header.setText(getResources().getString(R.string.label_pushnotification_cashreceived));
//            Img_notification.setImageResource(R.drawable.oksmily);
//
//        } else if (action.equalsIgnoreCase("payment_paid")) {
//            Textview_alert_header.setText(getResources().getString(R.string.label_pushnotification_ride_completed));
//            Img_notification.setImageResource(R.drawable.oksmily);
//
//        } else if (action.equalsIgnoreCase("job_request")) {
//            Textview_alert_header.setText(getResources().getString(R.string.label_pushnotification_newjob));
//        } else if (action.equalsIgnoreCase("admin_notification")) {
//            Textview_alert_header.setText(getResources().getString(R.string.admin_notification));
//        } else if (action.equalsIgnoreCase("Admin Availability Change")) {
//            Textview_alert_header.setText(getResources().getString(R.string.availability_change));
//        } else if (action.equalsIgnoreCase(ServiceConstant.ACTION_TAG_PARTIALLY_PAID)) {
//            Textview_alert_header.setText(getResources().getString(R.string.partially_change));
//        } else if (action.equalsIgnoreCase(ServiceConstant.ACTION_LEFT_JOB)) {
//            Textview_alert_header.setText(getResources().getString(R.string.admin_notification));
//            Message_Tv.setText(message + " : " + Str_JobId);
//        } else if (action.equalsIgnoreCase(ServiceConstant.ACTION_PENDING_TASK)) {
//            Textview_alert_header.setText(getResources().getString(R.string.admin_notification));
//            Message_Tv.setText(message + " : " + Str_JobId);
//        }
        if (action.equalsIgnoreCase(ServiceConstant.Accout_deleted)) {
            Textview_alert_header.setText(getResources().getString(R.string.admin_notification));
            Message_Tv.setText(message);
        }

        if (currency_code != null) {
            Currency currencycode = Currency.getInstance(getLocale(currency_code));
            Amount = currencycode.getSymbol() + Str_amount;
        }
    }

    private void showDialog(String data) {
        mLoadingDialog = new LoadingDialog(Xmpp_PushNotificationPage.this);
        mLoadingDialog.setLoadingTitle(data);
        mLoadingDialog.show();
    }

    //-----------------------Logout Request-----------------
    private void postRequest_Logout(String Url) {
        showDialog(getResources().getString(R.string.action_logging_out));
        System.out.println("---------------LogOut Url-----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", provider_id);
        jsonParams.put("device_type", "android");
        ServiceRequest mservicerequest = new ServiceRequest(Xmpp_PushNotificationPage.this);
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
                    postSetOnlineStatus(ServiceConstant.AVAILABILITY_URL, Xmpp_PushNotificationPage.this, 0);
                }
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }

    //----------------------------------------Availability Off------------------------------------
    private void postSetOnlineStatus(String url, final Context context, int state) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("tasker", provider_id);
        jsonParams.put("availability", "" + state);
        ServiceRequest mservicerequest = new ServiceRequest(context);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                String status = "", responseString = "";
                System.out.println("Online Status-----------------------" + response);
                try {
                    JSONObject jobject = new JSONObject(response);
                    status = jobject.getString("status");
                    if (status.equalsIgnoreCase("0")
                            || status.equalsIgnoreCase("1")
                            || status.equalsIgnoreCase("2")
                            || status.equalsIgnoreCase("3")) {

                        JSONObject object = jobject.getJSONObject("response");
                        status = object.getString("tasker_status");
                        if (status.equalsIgnoreCase("0")) {
                            session.logoutUser();
                            NavigationDrawer.navigationDrawerClass.finish();
                            SocketHandler.getInstance(context).getSocketManager().disconnect();
                            Intent i = new Intent(context, ChatMessageService.class);
                            context.stopService(i);
                            Intent stop = new Intent(context, SocketCheckService.class);
                            context.stopService(stop);
                        }

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
    public void onBackPressed() {
        super.onBackPressed();
        //...
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    //method to convert currency code to currency symbol
    private static Locale getLocale(String strCode) {

        for (Locale locale : NumberFormat.getAvailableLocales()) {
            String code = NumberFormat.getCurrencyInstance(locale).getCurrency().getCurrencyCode();
            if (strCode != null && strCode.equals(code)) {
                return locale;
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {
        // Unregister the logout receiver
        unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }

}
