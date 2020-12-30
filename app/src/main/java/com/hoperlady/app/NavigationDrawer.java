package com.hoperlady.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.hoperlady.Fragment.ExpertHomePageFragment;
import com.hoperlady.Fragment.HomePageFragment;
import com.hoperlady.R;
import com.hoperlady.Utils.AndroidServiceStartOnBoot;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.Utils.SessionManagerDB;
import com.hoperlady.Utils.SocketCheckService;
import com.hoperlady.adapter.NavigationMenuAdapter;
import com.hoperlady.hockeyapp.ActionBarActivityHockeyApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import core.Dialog.LoadingDialog;
import core.Dialog.PkDialog;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;
import core.socket.ChatMessageService;
import core.socket.SocketHandler;
import io.socket.client.Socket;

/**
 * Created by user88 on 12/11/2015.
 */
public class NavigationDrawer extends ActionBarActivityHockeyApp {
    public static DrawerLayout drawerLayout;
    public static RelativeLayout mDrawer;
    private static NavigationMenuAdapter mMenuAdapter;
    ActionBarDrawerToggle actionBarDrawerToggle;
    LoadingDialog mLoadingDialog;
    private Context context;
    private ListView mDrawerList;
    public static SessionManager session;
    public SessionManagerDB sessionManagerDB;
    private String provider_id = "", provider_name = "";
    private String Page_Open_Status = "";
    private String Appinfoemail = "", myAppInfoMailStr = "";
    private Socket mSocket;
    private String[] title;
    private int[] icon;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SocketHandler socketHandler;
    private Receiver refreshReceiver;
    public static NavigationDrawer navigationDrawerClass;

    private TextView versionTV;
    private RelativeLayout versionRL;

    public static void navigationNotifyChange() {

        mMenuAdapter.notifyDataSetChanged();
    }

    public static void disableSwipeDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public static void enableSwipeDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public static void openDrawer() {
        drawerLayout.openDrawer(mDrawer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        socketHandler = SocketHandler.getInstance(this);
        context = getApplicationContext();
        session = new SessionManager(NavigationDrawer.this);
        sessionManagerDB = new SessionManagerDB(NavigationDrawer.this);
        navigationDrawerClass = NavigationDrawer.this;
        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mDrawer = (RelativeLayout) findViewById(R.id.drawer);
        mDrawerList = (ListView) findViewById(R.id.drawer_listview);
        versionTV = (TextView) findViewById(R.id.versionTV);
        versionRL = (RelativeLayout) findViewById(R.id.versionRL);

        HashMap<String, String> user = session.getUserDetails();
        provider_id = user.get(SessionManager.KEY_PROVIDERID);
        provider_name = user.get(SessionManager.KEY_PROVIDERNAME);
        Page_Open_Status = user.get(SessionManager.NAVIGATION_OPEN);

        HashMap<String, String> infomail = session.getUserDetails();
        Appinfoemail = infomail.get(SessionManager.KEY_Appinfo_email);
        myAppInfoMailStr = infomail.get(SessionManager.KEY_Appinfo_email);

        refreshReceiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.availability.change");
        intentFilter.addAction("com.admin.verification");
        registerReceiver(refreshReceiver, intentFilter);

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionTV.setText(getResources().getString(R.string.navigation_menu_version_txt) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        if (savedInstanceState == null) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.content_frame, new ExpertHomePageFragment());
            tx.commit();
        }

        if (!ChatMessageService.isStarted()) {
            Intent intent = new Intent(NavigationDrawer.this, ChatMessageService.class);
            startService(intent);
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigarionListMethod();


        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                cd = new ConnectionDetector(NavigationDrawer.this);
                isInternetPresent = cd.isConnectingToInternet();
                FragmentTransaction tx = getSupportFragmentManager().beginTransaction();

                switch (position) {

                    case 0:
                        Intent intent = new Intent(NavigationDrawer.this, MyProfileActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case 1:
                        if (cd.isConnectingToInternet()) {
                            tx.replace(R.id.content_frame, new ExpertHomePageFragment());
                            tx.commit();
                        } else {
                            Toast.makeText(NavigationDrawer.this, getResources().getString(R.string.service_request_no_internet), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case 2:
                        Intent intent_myjobs = new Intent(NavigationDrawer.this, MyJobs.class);
                        intent_myjobs.putExtra("status", "open");
                        startActivity(intent_myjobs);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case 3:
                        Intent aEarningsIntent = new Intent(NavigationDrawer.this, WeekTaskBarChartActivity.class);
                        startActivity(aEarningsIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

//                    case 4:
//                        Intent aMessageIntent = new Intent(NavigationDrawer.this, GetMessageChatActivity.class);
//                        startActivity(aMessageIntent);
//                        overridePendingTransition(R.anim.enter, R.anim.exit);
//                        break;

//                    case 4:
//                        Intent aNotificationIntent = new Intent(NavigationDrawer.this, NotificationMenuActivity.class);
//                        startActivity(aNotificationIntent);
//                        overridePendingTransition(R.anim.enter, R.anim.exit);
//                        break;

//                    case 5:
//                        Intent aTransactionIntent = new Intent(NavigationDrawer.this, TransactionMenuActivity.class);
//                        startActivity(aTransactionIntent);
//                        overridePendingTransition(R.anim.enter, R.anim.exit);
//                        break;

                    case 4:
                        Intent intent_doc = new Intent(NavigationDrawer.this, GetDocuments.class);
                        startActivity(intent_doc);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case 5:
                        Intent intent_bank = new Intent(NavigationDrawer.this, BankDetails.class);
                        startActivity(intent_bank);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

//                    case 7:
//                        Intent aReviewIntent = new Intent(NavigationDrawer.this, ReviewMenuActivity.class);
//                        startActivity(aReviewIntent);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                        break;

//                    case 5:
//                        Intent intent_changepwd = new Intent(NavigationDrawer.this, ChangePassword.class);
//                        startActivity(intent_changepwd);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                        break;

                    case 6:
//                        Intent intent_about = new Intent(NavigationDrawer.this, AboutUs.class);
//                        startActivity(intent_about);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        if (!Appinfoemail.trim().equalsIgnoreCase("")) {
                        shareToGMail("quickrabbit@gmail.com", "", "");
                    } else {
                        if (cd.isConnectingToInternet()) {
                            Appinfo(NavigationDrawer.this, ServiceConstant.App_Info);
                        } else {
                            Toast.makeText(NavigationDrawer.this, R.string.action_no_internet_message, Toast.LENGTH_SHORT).show();
                        }
                    }

                        break;
//                    case 7:
//                        logout();

                }
                mDrawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(mDrawer);
            }
        });
    }

    private void NavigarionListMethod() {
        title = new String[]{getResources().getString(R.string.drawer_list_profile_label), getResources().getString(R.string.drawer_list_home_label),
                getResources().getString(R.string.drawer_list_myjobs_label),
                getResources().getString(R.string.weektaskbarchartactivity_earnings_title_txt),
//                getResources().getString(R.string.drawer_list_chat_label),
//                getResources().getString(R.string.navigation_label_notification),
//                getResources().getString(R.string.navigation_label_transactions),
                "My Document",
                getResources().getString(R.string.drawer_list_banking_label),
//                getResources().getString(R.string.navigation_label_review),
//                getResources().getString(R.string.drawer_list_changepassword_label),
                getResources().getString(R.string.plumbal_support_label),
//                getResources().getString(R.string.drawer_list_signout_label),
        };

        icon = new int[]{R.drawable.nouserimg,
                R.drawable.menu_home_icon,
                R.drawable.menu_jobs_icon,
                R.drawable.menu_earnings_icon,
//                R.drawable.menu_chat_icon,
//                R.drawable.icon_menu_notification,
//                R.drawable.icon_menu_transaction,
                R.drawable.documents_symbol,
                R.drawable.menu_bank_icon,
//                R.drawable.icon_menu_review,
//                R.drawable.changepassword,
                R.drawable.menu_support_icon,
//                R.drawable.logouticon
        };

        mMenuAdapter = new NavigationMenuAdapter(NavigationDrawer.this, title, icon);
        mDrawerList.setAdapter(mMenuAdapter);
        mMenuAdapter.notifyDataSetChanged();
    }

    private void logout() {

        cd = new ConnectionDetector(NavigationDrawer.this);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            final PkDialog mDialog = new PkDialog(NavigationDrawer.this);
            mDialog.setDialogTitle(getResources().getString(R.string.profile_lable_logout_title));
            mDialog.setDialogMessage(getResources().getString(R.string.profile_lable_logout_message));
            mDialog.setPositiveButton(getResources().getString(R.string.profile_lable_logout_yes), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postRequest_Logout(ServiceConstant.logout_url);
                    mDialog.dismiss();
                }
            });
            mDialog.setNegativeButton(getResources().getString(R.string.profile_lable_logout_no), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
            mDialog.show();

        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }

    }

    //--------------Alert Method------------------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(NavigationDrawer.this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (session.isLoggedIn()) {
                Intent i = new Intent(context, AndroidServiceStartOnBoot.class);
                startService(i);

                if (!ChatMessageService.isStarted()) {
                    Intent intent = new Intent(NavigationDrawer.this, ChatMessageService.class);
                    startService(intent);
                }

                Intent start = new Intent(context, SocketCheckService.class);
                startService(start);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        session.pageopen("open");
    }

    @Override
    protected void onStop() {
        super.onStop();
        session.pageopen("close");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // session.pageopen("close");

    }

    private void showDialog(String data) {
        mLoadingDialog = new LoadingDialog(NavigationDrawer.this);
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
        ServiceRequest mservicerequest = new ServiceRequest(context);
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
                if (sStatus.equalsIgnoreCase("1") || sStatus.equalsIgnoreCase("2") || sStatus.equalsIgnoreCase("3")) {
                    postSetOnlineStatus(ServiceConstant.AVAILABILITY_URL, NavigationDrawer.this, 0);
                } else {

                }
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }

    //----------------------------------------Availability Off------------------------------------
    public void postSetOnlineStatus(String url, Context context, int state) {

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
                    if (status.equalsIgnoreCase("1") || status.equalsIgnoreCase("2") || status.equalsIgnoreCase("3")) {

                        JSONObject object = jobject.getJSONObject("response");
                        status = object.getString("tasker_status");
                        if (status.equalsIgnoreCase("0")) {
                            session.logoutUser();
                            finish();
                            SocketHandler.getInstance(NavigationDrawer.this).getSocketManager().disconnect();
                            Intent i = new Intent(NavigationDrawer.this, ChatMessageService.class);
                            stopService(i);
                            Intent stop = new Intent(NavigationDrawer.this, SocketCheckService.class);
                            stopService(stop);
                        } else {
                            String message = object.getString("message");
                            Alert(getResources().getString(R.string.availability_title), getResources().getString(R.string.availability_message_on));
                        }

                    } else {

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

    public void CloseActivity() {
        finish();

    }

    public void shareToGMail(String email, String subject, String content) {


        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //  emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Appinfoemail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        startActivity(emailIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            Intent back_pressed_intent = new Intent();
            back_pressed_intent.setAction("com.app.device.back.button.pressed");
            sendBroadcast(back_pressed_intent);
            finish();
            return true;
        }
        return false;
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
                        Appinfoemail = jobject.getString("email_address");
                        session.Setemailappinfo(Appinfoemail);

                        JSONObject android_map_api = jobject.getJSONObject("android_map_api");
                        taskerAPI = android_map_api.getString("tasker");
                        sessionManagerDB.setAPIKey(taskerAPI);

                        shareToGMail("quickrabbit@gmail.com", "", "");
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(refreshReceiver);
    }


    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("com.availability.change")) {
                String status = intent.getExtras().getString("status");
                HomePageFragment.admin_avail_status = true;
                if (status.equalsIgnoreCase("0")) {
                    HomePageFragment.myStatusTGB.setChecked(false);
                } else {
                    HomePageFragment.myStatusTGB.setChecked(true);
                }

            }

            if (intent.getAction().equalsIgnoreCase("com.admin.verification")) {
                NavigarionListMethod();
            }
        }
    }
}
