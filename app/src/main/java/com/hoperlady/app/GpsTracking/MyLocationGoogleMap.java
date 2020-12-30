package com.hoperlady.app.GpsTracking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hoperlady.app.map.TileSystem;

import org.greenrobot.eventbus.EventBus;

import core.socket.SocketManager;


public class MyLocationGoogleMap {


    private Circle accuracyCircle;
    private Marker locationMarker;
    private MyLocationProvider mLocationProvider;
    private boolean isMyLocationCentered = false;
    private final float accuracyStrokeWidth;
    private Marker bearingMarker;
    private Marker pickupmarker;
    static SocketManager smanager;
    public static final String LOGTAG = "Tracking";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Context mContext;

    public static final String ACCURACY_COLOR = "#1800ce5b";
    public static final String ACCURACY_COLOR_BORDERS = "#8000ce5b";


    public MyLocationGoogleMap(Context context) {
        mContext = context;
        Resources r = context.getResources();
        float density = r.getDisplayMetrics().density;
        int size = (int) (256 * density);
        TileSystem.setTileSize(size);

        accuracyStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                r.getDisplayMetrics()
        );
    }

    @SuppressLint("MissingPermission")
    public void moveToMyLocation(GoogleMap googleMap) {
        if (mLocationProvider != null) {
            Location location = mLocationProvider.getLastKnownLocation();
            if (googleMap != null && location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f);
                googleMap.animateCamera(cameraUpdate, 1000, null);
            }
        }
    }





    public void addTo(final GoogleMap googleMap, Bitmap driver_marker_resized) {
        addTo(googleMap, new GpsMyLocationProvider(mContext), driver_marker_resized);
    }

    public void addTo(final GoogleMap googleMap, MyLocationProvider myCustomLocationProvider, final Bitmap driver_marker_resized) {
        mLocationProvider = myCustomLocationProvider;
        mLocationProvider.startLocationProvider(new MyLocationConsumer() {
            @Override
            public void onLocationChanged(final Location location, MyLocationProvider source) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        EventBus.getDefault().post(location.getLatitude()+","+location.getLongitude());
                        LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
                        final float radius = location.getAccuracy()
                                / (float) TileSystem.GroundResolution(location.getLatitude(),
                                googleMap.getCameraPosition().zoom);
                        if (accuracyCircle == null) {
                            accuracyCircle = googleMap.addCircle(new CircleOptions()
                                    .center(center)
                                    .radius(radius)
                                    .fillColor(Color.parseColor(ACCURACY_COLOR))
                                    .strokeColor(Color.parseColor(ACCURACY_COLOR_BORDERS))
                                    .strokeWidth(accuracyStrokeWidth)
                            );
                        } else {
                            accuracyCircle.setCenter(center);
                            accuracyCircle.setRadius(radius);
                        }
                        if (locationMarker == null) {
                            // changes made by pandiyan
                            locationMarker = googleMap.addMarker(new MarkerOptions()
                                    .position(center)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromBitmap(driver_marker_resized))
                            );
                        } else {
                            locationMarker.setPosition(center);
                        }
                        if (location.getBearing() == 0.0) {
                            if (bearingMarker != null) {
                                bearingMarker.remove();
                                bearingMarker = null;
                            }
                        } else {
                            if (bearingMarker == null) {
                                if (locationMarker != null) {
                                    locationMarker.remove();
                                    locationMarker = null;
                                }
                                bearingMarker = googleMap.addMarker(new MarkerOptions()
                                        .position(center)
                                        .flat(true)
                                        .anchor(0.5f, 0.5f)
                                        .rotation(location.getBearing())
                                        .icon(BitmapDescriptorFactory.fromBitmap(driver_marker_resized))
                                );
                            } else {
                                if (locationMarker != null) {
                                    locationMarker.remove();
                                    locationMarker = null;
                                }
                                bearingMarker.setPosition(center);
                                bearingMarker.setRotation(location.getBearing());
                                googleMap.animateCamera(CameraUpdateFactory.newLatLng(center));
                            }
                        }
                        if (!isMyLocationCentered) {
                            isMyLocationCentered = true;
                            moveToMyLocation(googleMap);
                        }
                    }
                });
            }
        });
    }
    public void removeFrom(GoogleMap googleMap) {
        if (accuracyCircle != null) {
            accuracyCircle.remove();
            accuracyCircle = null;
        }
        if (locationMarker != null) {
            locationMarker.remove();
            locationMarker = null;
        }
        if (bearingMarker != null) {
            bearingMarker.remove();
            bearingMarker = null;
        }
        if (mLocationProvider != null) {
            mLocationProvider.destroy();
            mLocationProvider = null;
        }
        isMyLocationCentered = false;
    }





}
