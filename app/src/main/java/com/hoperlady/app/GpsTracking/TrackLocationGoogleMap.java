package com.hoperlady.app.GpsTracking;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;
import com.hoperlady.Utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import core.socket.SocketManager;


public class TrackLocationGoogleMap {


    private Circle accuracyCircle;
    private Marker locationMarker;
    private MyLocationProvider mLocationProvider;
    private boolean isMyLocationCentered = false;

    private Marker bearingMarker;
    private Marker pickupmarker;
    static SocketManager smanager;
    public static final String LOGTAG = "Tracking";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Context mContext;
    SessionManager sessionManager;
    public static final String ACCURACY_COLOR = "#1800ce5b";
    public static final String ACCURACY_COLOR_BORDERS = "#8000ce5b";


    public TrackLocationGoogleMap(Context context) {
        sessionManager = new SessionManager(context);
        mContext = context;
    }

    public void addTo()
    {

        if (smanager == null) {
            smanager = new SocketManager(mContext, new SocketManager.SocketCallBack() {

                @Override
                public void onSuccessListener(Object response) {
                }
            });

        }
        addTo(new GpsMyLocationProvider(mContext));
    }



    public void addTo( MyLocationProvider myCustomLocationProvider) {
        mLocationProvider = myCustomLocationProvider;
        mLocationProvider.startLocationProvider(new MyLocationConsumer() {
            @Override
            public void onLocationChanged(final Location location, MyLocationProvider source) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject job = new JSONObject();
                        System.out.println("Testing lat--->"+location.getLatitude());
                        if (job == null) {
                            job = new JSONObject();
                        }

                        try {
                            SessionManager session;
                            session = new SessionManager(mContext);
                            if(!session.getTaskerUserId().equals(""))
                            {
                                if (!smanager.isConnected) {
                                    smanager.connect();
                                }
                                job.put("user", session.getTaskerUserId());
                                job.put("location_lat", ""+location.getLatitude());
                                job.put("location_lon", ""+location.getLongitude());
                                job.put("bearing", String.valueOf(location.getBearing()));
                                job.put("lat", ""+location.getLatitude());
                                job.put("lng", ""+location.getLongitude());
                                smanager.sendlocation(job);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    public void removeloc()
    {
        if (mLocationProvider == null) {

        } mLocationProvider = new GpsMyLocationProvider(mContext);
        if (mLocationProvider != null)
        {
            mLocationProvider.destroy();
            mLocationProvider = null;
        }

    }





}
