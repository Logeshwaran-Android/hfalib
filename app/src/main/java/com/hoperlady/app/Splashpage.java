package com.hoperlady.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.Utils.SessionManagerDB;
import com.hoperlady.hockeyapp.ActivityHockeyApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import core.Dialog.LoadingDialog;
import core.Dialog.PkDialog;
import core.Map.GPSTracker;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;
import core.socket.ChatMessageService;
import core.socket.SocketHandler;
import io.fabric.sdk.android.Fabric;

//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;


/**
 * Created by user88 on 11/28/2015.
 */
public class Splashpage extends ActivityHockeyApp implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final static int REQUEST_LOCATION = 199;
    public static String Appmail = "";
    public static String provider_id = "";
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    // final static int REQUEST_LOCATION = 199;
    final int PERMISSION_REQUEST_CODE = 111;
    GPSTracker gps;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    private SessionManager sessionManager;
    private ServiceRequest mRequest;
    private ConnectionDetector cd;
    private LoadingDialog dialog;
    private String Str_Latitude = "", Str_longitude = "";
    private Boolean isInternetPresent = false;
    private Context context;
    private SocketHandler socketHandler;
    private SessionManagerDB sessionManagerDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splashspage);
        sessionManager = new SessionManager(Splashpage.this);
        sessionManagerDB = new SessionManagerDB(Splashpage.this);
        gps = new GPSTracker(Splashpage.this);
        cd = new ConnectionDetector(Splashpage.this);
        context = getApplicationContext();
        socketHandler = SocketHandler.getInstance(Splashpage.this);

        mGoogleApiClient = new GoogleApiClient.Builder(Splashpage.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission() || !checkWriteExternalStoragePermission() || !checkcamerapermission()) {
                        requestPermission();
                    } else {
                        setLocation();
                    }
                } else {
                    setLocation();
                }
            }
        }, SPLASH_TIME_OUT);


    }


    private void setLocation() {
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            Appinfo(Splashpage.this, ServiceConstant.App_Info);
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }


    //--------------Alert Method------------------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(Splashpage.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                setLocation();
            }
        });
        mDialog.show();
    }

    //Enabling Gps Service
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
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(Splashpage.this, REQUEST_LOCATION);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Toast.makeText(Splashpage.this, "Location enabled!", Toast.LENGTH_LONG).show();
                        if (sessionManager.isLoggedIn()) {
                            //starting XMPP service
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    sessionManager = new SessionManager(getApplicationContext());
                                    gps = new GPSTracker(Splashpage.this);

                                    HashMap<String, String> user = sessionManager.getUserDetails();
                                    provider_id = user.get(SessionManager.KEY_PROVIDERID);
                                    Str_Latitude = String.valueOf(gps.getLatitude());
                                    Str_longitude = String.valueOf(gps.getLongitude());

                                    //postRequest_UpdateProviderLocation(ServiceConstant.UPDATE_URL);

                                }
                            }, 2000);

                        } else {
//                            Intent i = new Intent(Splashpage.this, MainPage.class);
                            Intent i = new Intent(Splashpage.this, NewLoginHomePageActivity.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }

                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        finish();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }


    //-----------------------Update current Location for notification  Post Request-----------------
    private void postRequest_UpdateProviderLocation(String Url) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", provider_id);
        jsonParams.put("latitude", Str_Latitude);
        jsonParams.put("longitude", Str_longitude);

        mRequest = new ServiceRequest(Splashpage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                String Str_status = "", Str_Response = "", Str_jobID = "", Str_message = "", Str_availabilty = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    Str_Response = object.getString("response");

                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject jobject = object.getJSONObject("response");
                        // Str_jobID = jobject.getString("job_id");
                        Str_message = jobject.getString("message");
                        //Str_availabilty = jobject.getString("availability");
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), Str_Response);

                    }

                    if (Str_status.equalsIgnoreCase("1")) {

                        Intent i = new Intent(Splashpage.this, NavigationDrawer.class);
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), Str_Response);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onErrorListener() {
                Intent i = new Intent(Splashpage.this, NavigationDrawer.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);

            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {

        if (sessionManager.isLoggedIn()) {
            socketHandler.getSocketManager().connect();

            if (!ChatMessageService.isStarted()) {
                Intent intent = new Intent(Splashpage.this, ChatMessageService.class);
                startService(intent);
            }
        }

        super.onResume();
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
                        Appmail = jobject.getString("email_address");
                        sessionManager.Setemailappinfo(Appmail);

                        JSONObject android_map_api = jobject.getJSONObject("android_map_api");
                        taskerAPI = android_map_api.getString("tasker");
                        sessionManagerDB.setAPIKey(taskerAPI);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (gps.isgpsenabled() && gps.canGetLocation()) {
                    if (sessionManager.isLoggedIn()) {

                        HashMap<String, String> user = sessionManager.getUserDetails();
                        provider_id = user.get(SessionManager.KEY_PROVIDERID);
                        Str_Latitude = String.valueOf(gps.getLatitude());
                        Str_longitude = String.valueOf(gps.getLongitude());
                        Intent i = new Intent(Splashpage.this, NavigationDrawer.class);
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                    } else {
//                    Intent i = new Intent(Splashpage.this, MainPage.class);
                        Intent i = new Intent(Splashpage.this, NewLoginHomePageActivity.class);
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }

                } else {
                    enableGpsService();
                }
            }

            @Override
            public void onErrorListener() {

            }
        });


    }


    private boolean checkAccessFineLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkAccessCoarseLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkWriteExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkcamerapermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocation();
                } else {
                    finish();
                }
                break;
        }
    }
}
