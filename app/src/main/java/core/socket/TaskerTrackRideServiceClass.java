package core.socket;


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.hoperlady.Utils.LatLngInterpolator;
import com.hoperlady.Utils.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by user145 on 12/22/2017.
 */
public class TaskerTrackRideServiceClass extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    static SocketManager manager;
    static public TaskerTrackRideServiceClass service;
    private ActiveSocketDispatcher dispatcher;
    public static Context context;
    public static String tasker_id="";
    private String mCurrentUserId;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    public static Location myLocation;
    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;
    LatLng pickup_latlong = null, drop_latlong = null, cur_latlong = null, return_latLong = null;
    private LatLng newLatLng, oldLatLng;
    private LatLngInterpolator mLatLngInterpolator;
    private Location oldLocation;
    private double myMovingDistance = 0.0;
    private SessionManager session;
    private String provider_id="";
    public static String task_id="";
    public static String user_id="";

    public static boolean isStarted() {
        if (service == null)
            return false;
        return true;
    }

    public static void TaskDetail(String taskid,String userid){
        user_id=userid;
        task_id=taskid;

    }


    @Override
    public void onCreate() {
        super.onCreate();
        session=new SessionManager(this);
        HashMap<String, String> user = session.getUserDetails();
        provider_id = user.get(SessionManager.KEY_PROVIDERID);
        try {
            setLocationRequest();
            buildGoogleApiClient();
            startLocationUpdates();
        } catch (Exception e) {
        }

        if (manager == null) {
            manager=new SocketManager(this);

        }
        service = this;
        context = this;
        dispatcher = new ActiveSocketDispatcher();
        SessionManager session = new SessionManager(TaskerTrackRideServiceClass.this);
        HashMap<String, String> userDetails = session.getUserDetails();
        mCurrentUserId = userDetails.get(session.KEY_PROVIDERID);

    }

    private void setLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }



    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);

}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.myLocation = location;
        if (myLocation != null) {

            try {
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                cur_latlong = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                MyCurrent_lat = myLocation.getLatitude();
                MyCurrent_long = myLocation.getLongitude();
                if (oldLatLng == null) {
                    oldLatLng = latLng;
                }
                newLatLng = latLng;

                if (mLatLngInterpolator == null) {
                    mLatLngInterpolator = new LatLngInterpolator.Linear();
                }

                oldLocation = new Location("");
                oldLocation.setLatitude(oldLatLng.latitude);
                oldLocation.setLongitude(oldLatLng.longitude);
                float bearingValue = oldLocation.bearingTo(location);
                myMovingDistance = oldLocation.distanceTo(location);
                System.out.println("moving distance------------" + myMovingDistance);

                // if (myMovingDistance > 1) {

                if (!String.valueOf(bearingValue).equalsIgnoreCase("NaN")) {
                    // if (location.getAccuracy() < 100.0 && location.getSpeed() < 6.95) {
                    String latitude= String.valueOf(myLocation.getLatitude());
                    String longitude= String.valueOf(myLocation.getLongitude());
                    String bearing= String.valueOf(bearingValue);
                    Intent drivermarkerupdate=new Intent();
                    drivermarkerupdate.setAction("com.taskermarker.update");
                    drivermarkerupdate.putExtra("lat",latitude);
                    drivermarkerupdate.putExtra("lng",longitude);
                    drivermarkerupdate.putExtra("bearing",bearing);
                    sendBroadcast(drivermarkerupdate);
                    sendtoserver(location,bearingValue);


                    //  }
                }

                //  }

                oldLatLng = newLatLng;
            } catch (Exception e) {
            }

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void sendtoserver(Location location, float bearingValue){
        JSONObject job = new JSONObject();
        String sendlat = Double.valueOf(location.getLatitude()).toString();
        String sendlng = Double.valueOf(location.getLongitude()).toString();
        if (job == null) {
            job = new JSONObject();
        }

        try {
            job.put("user", user_id);
            job.put("tasker", provider_id);
            job.put("task", task_id);
            job.put("bearing", String.valueOf(bearingValue));
            job.put("lat", sendlat);
            job.put("lng", sendlng);

           manager.sendlocation(job);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
