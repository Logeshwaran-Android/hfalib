package com.hoperlady.app.GpsTracking;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.hoperlady.Utils.SessionManager;

import core.socket.SocketManager;


public class locationupdatejobservice extends Service {

    static SocketManager smanager;

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SessionManager session;
        session = new SessionManager(locationupdatejobservice.this);
        if(!session.getTaskerUserId().equals(""))
        {
            TrackLocationGoogleMap locationgooglemap=new TrackLocationGoogleMap(getApplicationContext());
            locationgooglemap.addTo();
        }



        return START_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SessionManager session;
        session = new SessionManager(locationupdatejobservice.this);
        if(!session.getTaskerUserId().equals("")) {
            TrackLocationGoogleMap locationgooglemap = new TrackLocationGoogleMap(getApplicationContext());
            locationgooglemap.removeloc();
        }
    }
}
