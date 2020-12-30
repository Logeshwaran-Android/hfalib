package com.hoperlady.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.Utils.SocketCheckService;
import com.hoperlady.app.NavigationDrawer;
import com.hoperlady.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import core.Dialog.LoadingDialog;
import core.Dialog.PkDialog;
import core.Volley.ServiceRequest;
import core.Widgets.CircularImageView;
import core.service.ServiceConstant;
import core.socket.ChatMessageService;
import core.socket.SocketHandler;


/**
 * Created by user88 on 12/9/2015.
 */
public class NavigationMenuAdapter extends BaseAdapter {
    Context context;
    String[] mTitle;
    int[] mIcon;
    LayoutInflater inflater;
    View itemView;
    public SessionManager session;
    LoadingDialog mLoadingDialog;
    private String provider_id = "";

    private TextView username, user_email;

    private String profile_username = "", profile_user_email;


    private String provider_name, provide_img;

    public NavigationMenuAdapter(Context context, String[] title, int[] icon) {
        this.context = context;
        this.mTitle = title;
        this.mIcon = icon;
        session = new SessionManager(context);
        HashMap<String, String> user = session.getUserDetails();
        provider_id = user.get(SessionManager.KEY_PROVIDERID);
    }

    @Override
    public int getCount() {
        return mTitle.length;
    }

    @Override
    public Object getItem(int position) {
        return mTitle[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView txtTitle, profile_name, profile_mobile, profile_alert_title;
        CircularImageView profile_icon;
        ImageView imgIcon;
        RelativeLayout profile_layout, listitem_layout;
        LinearLayout backLL;

        View drawer_view;
        TextView logoutTV;
        ImageView backIV;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = inflater.inflate(R.layout.drawer_list_item, parent, false);

        txtTitle = (TextView) itemView.findViewById(R.id.title);
        imgIcon = (ImageView) itemView.findViewById(R.id.icon);
        profile_icon = (CircularImageView) itemView.findViewById(R.id.profile_icon);
        drawer_view = (View) itemView.findViewById(R.id.drawer_list_view1);
        profile_layout = (RelativeLayout) itemView.findViewById(R.id.drawer_list_item_profile_layout);
        listitem_layout = (RelativeLayout) itemView.findViewById(R.id.drawer_list_item_layout);
        username = (TextView) itemView.findViewById(R.id.profile_user_name);
        username.setSelected(true);
        user_email = (TextView) itemView.findViewById(R.id.profile_email_title);
        profile_alert_title = (TextView) itemView.findViewById(R.id.profile_alert_title);
        logoutTV = (TextView) itemView.findViewById(R.id.logoutTV);
        backIV = (ImageView) itemView.findViewById(R.id.backIV);
        backLL = (LinearLayout) itemView.findViewById(R.id.backLL);

        logoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NavigationDrawer.navigationDrawerClass != null) {
                    NavigationDrawer.drawerLayout.closeDrawer(NavigationDrawer.mDrawer);
                }
                ConnectionDetector cd = new ConnectionDetector(context);

                if (cd.isConnectingToInternet()) {
                    final PkDialog mDialog = new PkDialog(context);
                    mDialog.setDialogTitle(context.getResources().getString(R.string.profile_lable_logout_title));
                    mDialog.setDialogMessage(context.getResources().getString(R.string.profile_lable_logout_message));
                    mDialog.setPositiveButton(context.getResources().getString(R.string.profile_lable_logout_yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            postRequest_Logout(ServiceConstant.logout_url);
                            mDialog.dismiss();
                        }
                    });
                    mDialog.setNegativeButton(context.getResources().getString(R.string.profile_lable_logout_no), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                    mDialog.show();

                } else {
                    alert(context.getResources().getString(R.string.alert_label_title), context.getResources().getString(R.string.alert_nointernet));
                }
            }
        });

        backLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NavigationDrawer.navigationDrawerClass != null) {
                    NavigationDrawer.drawerLayout.closeDrawer(NavigationDrawer.mDrawer);
                }
            }
        });

        if (session.isLoggedIn()) {
            if (position == 0) {
                HashMap<String, String> user = session.getUserDetails();
                provider_name = user.get(SessionManager.KEY_PROVIDERNAME);
                provide_img = user.get(SessionManager.KEY_USERIMAGE);
                profile_user_email = user.get(SessionManager.KEY_EMAIL);

                profile_layout.setVisibility(View.VISIBLE);
                listitem_layout.setVisibility(View.GONE);
                drawer_view.setVisibility(View.VISIBLE);
                if (session.getTaskerVerification().equals("3")
                        || session.getTaskerVerification().equals("0")
                        || session.getTaskerVerification().equals("")) {
                    profile_alert_title.setVisibility(View.VISIBLE);
                } else {
                    profile_alert_title.setVisibility(View.INVISIBLE);
                }

                System.out.println("name------------" + provider_name);

                username.setText(provider_name);
                username.setSelected(true);
                user_email.setText(profile_user_email);
                try {
                    Picasso.with(context).load(String.valueOf(provide_img)).placeholder(R.drawable.nouserimg).into(profile_icon);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {

                drawer_view.setVisibility(View.GONE);

                profile_layout.setVisibility(View.GONE);
                listitem_layout.setVisibility(View.VISIBLE);

                if (session.getProviderDocumentStatus().equalsIgnoreCase("0")) {
                    if (position == 4) {
                        listitem_layout.setVisibility(View.GONE);
                    }
                }

                imgIcon.setImageResource(mIcon[position]);
                txtTitle.setText(mTitle[position]);

            }

        } else {
            if (position == 0) {
                profile_layout.setVisibility(View.VISIBLE);
                listitem_layout.setVisibility(View.GONE);
                drawer_view.setVisibility(View.GONE);

                username.setText(context.getResources().getString(R.string.mainpage_login_signin_lable));
                user_email.setVisibility(View.GONE);
            } else {
                if (position == 2) {
                    drawer_view.setVisibility(View.GONE);
                } else {
                    drawer_view.setVisibility(View.GONE);
                }
                profile_layout.setVisibility(View.GONE);
                listitem_layout.setVisibility(View.VISIBLE);

                imgIcon.setImageResource(mIcon[position]);
                txtTitle.setText(mTitle[position]);
            }


        }


        return itemView;
    }

    //-----------------------Logout Request-----------------
    private void postRequest_Logout(String Url) {
        showDialog(context.getResources().getString(R.string.action_logging_out));
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
                if (sStatus.equalsIgnoreCase("1")
                        || sStatus.equalsIgnoreCase("2")
                        || sStatus.equalsIgnoreCase("3")) {
                    postSetOnlineStatus(ServiceConstant.AVAILABILITY_URL, context, 0);
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
                    if (status.equalsIgnoreCase("1")
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
                        } else {
                            String message = object.getString("message");
//                            alert(context.getResources().getString(R.string.availability_title), context.getResources().getString(R.string.availability_message_on));
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


    //--------------Alert Method-----------
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(context);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(context.getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void showDialog(String data) {
        mLoadingDialog = new LoadingDialog(context);
        mLoadingDialog.setLoadingTitle(data);
        mLoadingDialog.show();
    }
}

