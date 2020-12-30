package com.hoperlady.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.hoperlady.Pojo.Reviwes_Pojo;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import core.Dialog.LoadingDialog;
import core.Dialog.PkDialog;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;

public class RatingSmilyPage extends AppCompatActivity implements View.OnClickListener {

    private ImageView smileyTerrible, smileyBad, smileyOkey, smileyGood, smileyGreate;
    private ImageView closeIV, smileyFullIV;
    private EditText commentsET;
    private TextView ratingNameTV, titltDescriptionTV;
    private Button submitBTN;
    private View view;

    private String sRatingValue = "5";
    private ConnectionDetector cd;
    private ArrayList<Reviwes_Pojo> reviweslist;


    private String Userimage = "", Usersname = "";
    private boolean show_progress_status = false;
    private String provider_id = "", JobId = "";
    private SessionManager session;
    private String id = "", title = "", count = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_smily_page);
        initialition();
        initOnClick();
    }

    private void initialition() {
        smileyTerrible = (ImageView) findViewById(R.id.smileyTerrible);
        smileyBad = (ImageView) findViewById(R.id.smileyBad);
        smileyOkey = (ImageView) findViewById(R.id.smileyOkey);
        smileyGood = (ImageView) findViewById(R.id.smileyGood);
        smileyGreate = (ImageView) findViewById(R.id.smileyGreate);
        closeIV = (ImageView) findViewById(R.id.closeIV);
        commentsET = (EditText) findViewById(R.id.commentsET);
        smileyFullIV = (ImageView) findViewById(R.id.smileyFullIV);
        ratingNameTV = (TextView) findViewById(R.id.ratingNameTV);
        titltDescriptionTV = (TextView) findViewById(R.id.titltDescriptionTV);
        submitBTN = (Button) findViewById(R.id.submitBTN);
        view = (View) findViewById(R.id.view);

        cd = new ConnectionDetector(RatingSmilyPage.this);
        session = new SessionManager(RatingSmilyPage.this);

        HashMap<String, String> user = session.getUserDetails();
        provider_id = user.get(SessionManager.KEY_PROVIDERID);

        reviweslist = new ArrayList<Reviwes_Pojo>();

        Intent i = getIntent();
        JobId = i.getStringExtra("jobId");

        if (cd.isConnectingToInternet()) {
            RatingsRequest(RatingSmilyPage.this, ServiceConstant.REVIWES_GET_URL);
            System.out.println("reviwes-get url-----------" + ServiceConstant.REVIWES_GET_URL);

        } else {
            Toast.makeText(this, getResources().getString(R.string.alert_nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void initOnClick() {
        smileyTerrible.setOnClickListener(this);
        smileyBad.setOnClickListener(this);
        smileyOkey.setOnClickListener(this);
        smileyGood.setOnClickListener(this);
        smileyGreate.setOnClickListener(this);
        closeIV.setOnClickListener(this);
        submitBTN.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.smileyTerrible:
                smileyTerrible.setBackgroundResource(R.drawable.smiley_terrible_yellow);
                smileyFullIV.setBackgroundResource(R.drawable.smiley_terrible_yellow);

                smileyBad.setBackgroundResource(R.drawable.smiley_bad_gray);
                smileyOkey.setBackgroundResource(R.drawable.smiley_okey_gray);
                smileyGood.setBackgroundResource(R.drawable.smiley_good_gray);
                smileyGreate.setBackgroundResource(R.drawable.smiley_greate_gray);
                sRatingValue = "1";
                ratingNameTV.setText(getResources().getString(R.string.ratingsmileypage_terrible));
                ratingNameTV.setVisibility(View.VISIBLE);
                smileyFullIV.setVisibility(View.VISIBLE);
                titltDescriptionTV.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
//                commentsET.setVisibility(View.VISIBLE);
                submitBTN.setVisibility(View.VISIBLE);
                break;
            case R.id.smileyBad:
                smileyTerrible.setBackgroundResource(R.drawable.smiley_terrible_yellow);
                smileyBad.setBackgroundResource(R.drawable.smiley_bad_yellow);
                smileyFullIV.setBackgroundResource(R.drawable.smiley_bad_yellow);

                smileyOkey.setBackgroundResource(R.drawable.smiley_okey_gray);
                smileyGood.setBackgroundResource(R.drawable.smiley_good_gray);
                smileyGreate.setBackgroundResource(R.drawable.smiley_greate_gray);
                sRatingValue = "2";
                ratingNameTV.setText(getResources().getString(R.string.ratingsmileypage_bad));
                ratingNameTV.setVisibility(View.VISIBLE);
                smileyFullIV.setVisibility(View.VISIBLE);
                titltDescriptionTV.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
//                commentsET.setVisibility(View.VISIBLE);
                submitBTN.setVisibility(View.VISIBLE);
                break;
            case R.id.smileyOkey:
                smileyTerrible.setBackgroundResource(R.drawable.smiley_terrible_yellow);
                smileyBad.setBackgroundResource(R.drawable.smiley_bad_yellow);
                smileyOkey.setBackgroundResource(R.drawable.smiley_okey_yellow);
                smileyFullIV.setBackgroundResource(R.drawable.smiley_okey_yellow);

                smileyGood.setBackgroundResource(R.drawable.smiley_good_gray);
                smileyGreate.setBackgroundResource(R.drawable.smiley_greate_gray);
                sRatingValue = "3";
                ratingNameTV.setText(getResources().getString(R.string.ratingsmileypage_okay));
                ratingNameTV.setVisibility(View.VISIBLE);
                smileyFullIV.setVisibility(View.VISIBLE);
                titltDescriptionTV.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
//                commentsET.setVisibility(View.VISIBLE);
                submitBTN.setVisibility(View.VISIBLE);
                break;
            case R.id.smileyGood:
                smileyTerrible.setBackgroundResource(R.drawable.smiley_terrible_yellow);
                smileyBad.setBackgroundResource(R.drawable.smiley_bad_yellow);
                smileyOkey.setBackgroundResource(R.drawable.smiley_okey_yellow);
                smileyGood.setBackgroundResource(R.drawable.smiley_good_yellow);
                smileyFullIV.setBackgroundResource(R.drawable.smiley_good_yellow);

                smileyGreate.setBackgroundResource(R.drawable.smiley_greate_gray);
                sRatingValue = "4";
                ratingNameTV.setText(getResources().getString(R.string.ratingsmileypage_good));
                ratingNameTV.setVisibility(View.VISIBLE);
                smileyFullIV.setVisibility(View.VISIBLE);
                titltDescriptionTV.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
//                commentsET.setVisibility(View.VISIBLE);
                submitBTN.setVisibility(View.VISIBLE);
                break;
            case R.id.smileyGreate:
                smileyTerrible.setBackgroundResource(R.drawable.smiley_terrible_yellow);
                smileyBad.setBackgroundResource(R.drawable.smiley_bad_yellow);
                smileyOkey.setBackgroundResource(R.drawable.smiley_okey_yellow);
                smileyGood.setBackgroundResource(R.drawable.smiley_good_yellow);
                smileyGreate.setBackgroundResource(R.drawable.smiley_greate_yellow);
                smileyFullIV.setBackgroundResource(R.drawable.smiley_greate_yellow);

                sRatingValue = "5";
                ratingNameTV.setText(getResources().getString(R.string.ratingsmileypage_great));
                ratingNameTV.setVisibility(View.VISIBLE);
                smileyFullIV.setVisibility(View.VISIBLE);
                titltDescriptionTV.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
//                commentsET.setVisibility(View.VISIBLE);
                submitBTN.setVisibility(View.VISIBLE);
                break;
            case R.id.closeIV:
                finish();
                break;
            case R.id.submitBTN:
                if (sRatingValue.equals("0")) {
                    Toast.makeText(this, getResources().getString(R.string.my_rides_rating_header_select), Toast.LENGTH_SHORT).show();
                }
//                else if (commentsET.getText().toString().length() == 0 && commentsET.getVisibility() == View.VISIBLE) {
//                    Toast.makeText(this, getResources().getString(R.string.my_rides_rating_header_comment_textview), Toast.LENGTH_SHORT).show();
//                }
                else {
                    HashMap<String, String> jsonParams = new HashMap<String, String>();
                    jsonParams.put("comments", commentsET.getText().toString());
                    jsonParams.put("ratingsFor", "tasker");
                    jsonParams.put("job_id", JobId);
                    for (int i = 0; i < reviweslist.size(); i++) {
                        id = reviweslist.get(i).getOptions_id();
                        title = reviweslist.get(i).getOptions_title();
                        count = reviweslist.get(i).getRatings_count();

                        jsonParams.put("ratings[" + i + "][option_id]", reviweslist.get(i).getOptions_id());
                        jsonParams.put("ratings[" + i + "][option_title]", reviweslist.get(i).getOptions_title());
                        jsonParams.put("ratings[" + i + "][rating]", sRatingValue);
                    }
                    SubmitRatingsRequest(ServiceConstant.REVIWES_SUBMIT_URL, jsonParams);
                }
                break;
        }
    }

    private void RatingsRequest(Context mContext, String url) {

//        dialog = new LoadingDialog(ReviwesPage.this);
//        dialog.setLoadingTitle(getResources().getString(R.string.loading_in));
//        dialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("holder_type", "provider");
        jsonParams.put("user", provider_id);
        jsonParams.put("job_id", JobId);

        ServiceRequest mservicerequest = new ServiceRequest(mContext);

        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                String Str_status = "", Str_total = "", Str_Rating = "";

                try {

                    JSONObject jobject = new JSONObject(response);
                    Log.e("reviews_url", jobject.toString(1));
                    Str_status = jobject.getString("status");
                    Str_total = jobject.getString("total");

                    if (Str_status.equalsIgnoreCase("1")) {

                        JSONArray jarry = jobject.getJSONArray("review_options");

                        if (jarry.length() > 0) {
                            for (int i = 0; i < jarry.length(); i++) {
                                JSONObject object = jarry.getJSONObject(i);
                                Reviwes_Pojo pojo = new Reviwes_Pojo();

                                pojo.setOptions_title(object.getString("option_name"));
                                pojo.setOptions_id(object.getString("option_id"));
                                pojo.setRatings_count("5");
                                Userimage = object.getString("image");
                                Usersname = object.getString("username");
                                System.out.println("-------username------------" + Usersname);
                                reviweslist.add(pojo);
                            }
                        }
                        show_progress_status = true;
//                        Picasso.with(getApplicationContext()).load(String.valueOf(Userimage)).placeholder(R.drawable.nouserimg).into(Img_upload_ratingimg);

                    } else {
                        show_progress_status = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

//                dialog.dismiss();

                if (Str_status.equalsIgnoreCase("1")) {
//                    username.setText(Usersname);
//                    adapter = new Reviwes_Adapter(RatingSmilyPage.this, reviweslist);
//                    listview.setAdapter(adapter);
//                    listview.setExpanded(true);
//                    dialog.dismiss();

                } else {
                    final PkDialog mdialog = new PkDialog(RatingSmilyPage.this);
                    mdialog.setDialogTitle(getResources().getString(R.string.server_lable_header));
                    mdialog.setDialogMessage(getResources().getString(R.string.alert_servererror));
                    mdialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mdialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onErrorListener() {

//                dialog.dismiss();

            }
        });


    }

    private void SubmitRatingsRequest(String url, final Map<String, String> jsonParams) {

        final LoadingDialog dialog = new LoadingDialog(RatingSmilyPage.this);
        dialog.setLoadingTitle(getResources().getString(R.string.dialog_rating));
        dialog.show();

        ServiceRequest mservicerequest = new ServiceRequest(RatingSmilyPage.this);

        mservicerequest.makeServiceRequest(url, Request.Method.POST, (HashMap<String, String>) jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {

                String status = "", Str_response = "";
                String sStatus = "", SUser_image = "", Smsg = "";
                try {
                    JSONObject object = new JSONObject(response);
                    status = object.getString("status");
                    Str_response = object.getString("response");

                    if (status.equalsIgnoreCase("1")) {
                        JSONObject responseObject = object.getJSONObject("response");
                        SUser_image = responseObject.getString("image");
                        Smsg = responseObject.getString("msg");
                        session.setUserImageUpdate(SUser_image);
//                        Img_upload_ratingimg.setImageBitmap(bitMapThumbnail);
                        session.setUserImageUpdate(SUser_image);

                        Intent intent = new Intent(getApplicationContext(), NavigationDrawer.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        Toast.makeText(RatingSmilyPage.this, Smsg, Toast.LENGTH_SHORT).show();

                    } else {
//                        Alert(getResources().getString(R.string.server_lable_header), Str_response);
                        Toast.makeText(RatingSmilyPage.this, Str_response, Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

}
