package com.hoperlady.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.andexert.library.RippleView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hoperlady.R;
import com.hoperlady.adapter.CustomPagerAdapter;
import com.hoperlady.hockeyapp.ActivityHockeyApp;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import core.Dialog.PkDialog;
import core.socket.SocketHandler;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by user88 on 11/28/2015.
 */
public class MainPage extends ActivityHockeyApp implements RippleView.OnRippleCompleteListener {

    private RippleView mySignRPLVW, myRegisterRPLVW;
    private SocketHandler socketHandler;
    private AutoScrollViewPager myViewPager;
    private CustomPagerAdapter myAdapter;
    int[] myImageInt = {
            R.drawable.plumber1,
            R.drawable.plumber2,
            R.drawable.plumber3,
            R.drawable.plumber4,
    };
    String[] myText = {"Our rich & responsive interface will give you the best user experience.",
            "Customers are looking to hire experienced professionals just like you !!",
            "Connect with customers to get hired and earn more money !!!",
            "Carry your ToolKit and help the people around the world !!!"};
    private CircleIndicator myViewPageIndicator;

    private GoogleApiClient client;

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MainPage Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    private class Receiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equalsIgnoreCase("com.app.device.back.button.pressed"))
            {
                finish();
            }
        }
    }
private Receiver receive;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initilize();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }


    public void initilize() {
        socketHandler = SocketHandler.getInstance(this);
        mySignRPLVW = (RippleView) findViewById(R.id.main_RPL_signin);
        myRegisterRPLVW = (RippleView) findViewById(R.id.main_RPL_register);
        //myViewPager = (AutoScrollViewPager) findViewById(R.id.main_page_VWPGR);
        myViewPageIndicator = (CircleIndicator) findViewById(R.id.main_page_VWPGR_indicator);
        loadData();
        clickListener();
        receive = new Receiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.app.device.back.button.pressed");
        registerReceiver(receive,intentFilter);
    }

    private void clickListener() {
        mySignRPLVW.setOnRippleCompleteListener(this);
        myRegisterRPLVW.setOnRippleCompleteListener(this);
    }

    @Override
    public void onComplete(RippleView aRippleView) {
        switch (aRippleView.getId()) {
            case R.id.main_RPL_signin:
                Intent aSignInIntent = new Intent(MainPage.this, LoginPage.class);
                startActivity(aSignInIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.main_RPL_register:
                Intent aIntent = new Intent(MainPage.this, RegisterPageFirst.class);
                startActivity(aIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }

    }

    private void loadData() {
        try {
//            myAdapter = new CustomPagerAdapter(getApplicationContext(), myImageInt, myText);
//            myViewPager.setAdapter(myAdapter);
//            myViewPageIndicator.setViewPager(myViewPager);
            myViewPager.startAutoScroll();
            myViewPager.setInterval(2900);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(MainPage.this);
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
    protected void onResume() {
        super.onResume();
        /*if (!socketHandler.getSocketManager().isConnected){
            socketHandler.getSocketManager().connect();
        }*/
    }


}
