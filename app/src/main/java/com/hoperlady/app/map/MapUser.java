package com.hoperlady.app.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hoperlady.R;
import com.hoperlady.SubClassBroadCast.SubClassActivity;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.Utils.SessionManagerDB;
import com.hoperlady.app.GpsTracking.MyLocationGoogleMap;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import core.Map.GPSTracker;
import core.Widgets.CircularImageView;

public class MapUser extends SubClassActivity {
    private GoogleMap googleMap;
    MapFragment mapFragment;
    private GPSTracker gps;
    private SessionManagerDB sessionManagerDB;
    private Double MyCurrent_lat = 0.0;
    private Double MyCurrent_long = 0.0;
    BitmapDescriptor markerIcon;
    LatLngBounds bounds;
    String lat = "", log = "",user_location,deiverlat = "",driverlong = "";
    TextView userbookinglocation;
    private ArrayList<School_lat_longPajo> School_lat_longPajoList = new ArrayList<>();
    private ArrayList<LatLng> wayPointList;
    private LatLngBounds.Builder wayPointBuilder;
    private List<Polyline> polyLines;
    ArrayList<School_lat_longPajo> multiple_latlon_list = new ArrayList<School_lat_longPajo>();
    private RelativeLayout rentals_header_back_layout;
    int jk=0;

    String deslat="";
    String deslong="";

    SessionManager session;


    CircularImageView track_ride;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_map);
        sessionManagerDB = new SessionManagerDB(MapUser.this);

        session = new SessionManager(this);

        Map_Initilaize();

        track_ride = (CircularImageView) findViewById(R.id.track_ride);



        Intent intent = getIntent();
        lat = intent.getStringExtra("User_lat");
        log = intent.getStringExtra("user_log");



        user_location = intent.getStringExtra("user_location");

        userbookinglocation.setText(user_location);





    }




    private void Map_Initilaize() {
        wayPointList = new ArrayList<LatLng>();
        polyLines = new ArrayList<Polyline>();
        if (googleMap == null) {
            mapFragment = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.school_rute_map));
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap arg) {
                    LoadMap(arg);
                }
            });
        }
        rentals_header_back_layout = findViewById(R.id.rentals_header_back_layout);
        userbookinglocation= (TextView) findViewById(R.id.userbookinglocation);

        rentals_header_back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void LoadMap(GoogleMap arg) {
        gps = new GPSTracker(MapUser.this);
        googleMap = arg;
//
//        GroundOverlayOptions homeOverlay = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.applogo)).position(sd,100);
//        googleMap.addGroundOverlay(homeOverlay);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);


//        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);




        MyCurrent_lat = Double.parseDouble(lat);
        MyCurrent_long = Double.parseDouble(log);



        MyLocationGoogleMap mylocationicon = new MyLocationGoogleMap(getApplicationContext());
        mylocationicon.addTo(googleMap, drawableToBitmap());


        LatLng toPosition = new LatLng(MyCurrent_lat, MyCurrent_long);


        // markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_booking_auto_map_topview);
        googleMap.addMarker(new MarkerOptions()
                //.icon(BitmapDescriptorFactory.fromBitmap(bitmapIcon))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                // .title(user_nme)
                //./snippet(pickup_location)
                .infoWindowAnchor(0.5f, 0.5f)
                .draggable(true)
                .position(toPosition));

        track_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                DecimalFormat dFormat = new DecimalFormat("#.######");
//                Double   userlat= Double.valueOf(dFormat .format(MyCurrent_lat));
//                Double   userlong= Double.valueOf(dFormat .format(MyCurrent_long));
//
//                Double deslat = Double.valueOf(dFormat .format(session.getLatitudeUserId()));
//                Double deslong = Double.valueOf(dFormat .format(session.getLongUserId()));

                String voice_curent_lat_long = MyCurrent_lat + "," + MyCurrent_long;

                String voice_destination_lat_long = session.getLatitudeUserId() + "," + session.getLongUserId();
                System.out.println("----------fromPosition---------------" + voice_curent_lat_long);
                System.out.println("----------toPosition---------------" + voice_destination_lat_long);
                String locationUrl = "http://maps.google.com/maps?saddr=" + voice_curent_lat_long + "&daddr=" + voice_destination_lat_long;
                System.out.println("----------locationUrl---------------" + locationUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl));
                startActivity(intent);

            }
        });http://maps.google.com/maps?saddr=13.057446989167802,80.2531947568059&daddr=13.1067,80.0970

        try {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(MyCurrent_lat, MyCurrent_long)).zoom(15f).build();



            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } catch (Exception e) {
            e.printStackTrace();
        }







    }


    public class GetRouteTask extends AsyncTask<String, Void, String> {
        String response = "";
        private ArrayList<LatLng> wayLatLng=new ArrayList<LatLng>();
        private String dLat, dLong;
        private ArrayList<Schol_TripPojo> multipleDropList;
        public GetRouteTask(ArrayList<School_lat_longPajo> multiple_latlon_list, String lat, String lon) {
            this.multipleDropList = multipleDropList;
            dLat = lat;
            dLong = lon;

            wayLatLng = addWayPointPoint(multiple_latlon_list, dLat, dLong);

        }
        @Override
        protected void onPreExecute() {
        }
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... urls) {
            try {
                if (wayLatLng.size() >= 2) {
                    Routing routing = new Routing.Builder()
                            .travelMode(AbstractRouting.TravelMode.DRIVING)
                            .withListener(listner)
                            .alternativeRoutes(true)
                            .key("AIzaSyBjOJ79xpH-SVeN167QxJY4dJREIqzGQlU")
                            .waypoints(wayLatLng)
                            .build();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        routing.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    else
                        routing.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            response = "Success";
            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("Success")) {
            }
        }
    }
    RoutingListener listner = new RoutingListener() {
        @Override
        public void onRoutingFailure(RouteException e) {

           System.out.println("RouteException--"+e.getMessage());

        }


        @Override
        public void onRoutingStart() {
        }



        @Override
        public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
            if (polyLines.size() > 0) {
                for (Polyline poly : polyLines) {
                    poly.remove();
                }
            }
            polyLines = new ArrayList<>();
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.pattern(DashedPolyLine.PATTERN_POLYGON_ALPHA);
            polyOptions.color(getResources().getColor(R.color.black_color));
            polyOptions.width(14);
            polyOptions.addAll(arrayList.get(0).getPoints());
            Polyline polyline = googleMap.addPolyline(polyOptions);
            polyLines.add(polyline);
            try {
                if (wayPointBuilder != null) {
                    int padding, paddingH, paddingW;
                    bounds = wayPointBuilder.build();
                    final View mapview = mapFragment.getView();
                    float maxX = mapview.getMeasuredWidth();
                    float maxY = mapview.getMeasuredHeight();
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    int width = displayMetrics.widthPixels;
                    float percentageH = 55.0f;
                    float percentageW = 80.0f;
                    paddingH = (int) (maxY * (percentageH / 100.0f));
                    paddingW = (int) (maxX * (percentageW / 100.0f));
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, paddingW, paddingH, 100);
                    googleMap.animateCamera(cu);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onRoutingCancelled() {
        }
    };
    private ArrayList<LatLng> addWayPointPoint(ArrayList<School_lat_longPajo> mMultipleDropLatLng, String lat, String lon) {
        try {
            if (googleMap != null) {
                wayPointList.clear();
                wayPointBuilder = new LatLngBounds.Builder();
                if (mMultipleDropLatLng != null) {
                    for (int i = 0; i < mMultipleDropLatLng.size(); i++) {
                        Double sLat = mMultipleDropLatLng.get(i).getMap_lat();
                        Double sLng = mMultipleDropLatLng.get(i).getMap_long();
                        String sTxt = mMultipleDropLatLng.get(i).getMap_txt();
                        double Dlatitude = sLat;
                        double Dlongitude = sLng;
                        wayPointList.add(new LatLng(Dlatitude, Dlongitude));
                        wayPointBuilder.include(new LatLng(Dlatitude, Dlongitude));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wayPointList;
    }

    public Bitmap drawableToBitmap() {
        Bitmap bitmap = null;
        Drawable drawable = getResources().getDrawable(R.drawable.ic_booking_sedan_map_topview);
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String latlong) {

        if(jk==0)
        {
            jk++;
            String splitlatlong = latlong;
            String[] splitarray = splitlatlong.split(",");
            School_lat_longPajo school_lat_longPajo = new School_lat_longPajo();
            school_lat_longPajo.setMap_lat(Double.parseDouble(splitarray[0]));
            school_lat_longPajo.setMap_long(Double.parseDouble(splitarray[1]));
            School_lat_longPajoList.add(school_lat_longPajo);

            School_lat_longPajo school_lat_longPajos = new School_lat_longPajo();
            school_lat_longPajos.setMap_lat(MyCurrent_lat);
            school_lat_longPajos.setMap_long(MyCurrent_long);
            School_lat_longPajoList.add(school_lat_longPajos);

            GetRouteTask getRoute = new GetRouteTask(School_lat_longPajoList, String.valueOf(MyCurrent_lat), String.valueOf(MyCurrent_long));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                getRoute.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                getRoute.execute();
        }

    }
}
