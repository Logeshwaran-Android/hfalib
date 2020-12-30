package com.hoperlady.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.hoperlady.Pojo.ParentCategorypojo;
import com.hoperlady.Pojo.UpdateCategorydatapojo;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.CurrencySymbolConverter;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.adapter.ChildCategoryAdapter;
import com.hoperlady.adapter.LevelOfExpCategoryAdapter;
import com.hoperlady.adapter.ParentCategoryAdapter;
import com.hoperlady.hockeyapp.ActionBarActivityHockeyApp;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import core.Dialog.LoadingDialog;
import core.Dialog.PkDialog;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;

public class EditCategoryActivity extends ActionBarActivityHockeyApp implements View.OnClickListener {

    private static LoadingDialog dialog;
    SessionManager session;
    String provider_id = "", mainCategoryID = "", subCategoryID = "", minRate = "", selectExp = "select_experience";
    ArrayList<ParentCategorypojo> arrayListParentcategory;
    ArrayList<ParentCategorypojo> arrayListSubcategory;
    ArrayList<ParentCategorypojo> arrayListLevelofExp;
    //    Spinner /*parentSpinner,*/ /*childSpinner*/ levelOfExpSpinner;
    MaterialSpinner parentSpinner, childSpinner, levelOfExpSpinner;
    ParentCategoryAdapter parentCategoryAdapter;
    ChildCategoryAdapter childCategoryAdapter;
    LevelOfExpCategoryAdapter levelOfExpCategoryAdapter;
    Integer childPos, Parentpost, expPos;
    EditText edittext_hourlyrate, edittext_quickPinch;
    TextView parentCategory_name, childCategory_name, levelOfExp_name;
    Button UpdateCategorydatabtn, UpdateCategoryDataCancel;
    RelativeLayout layout_editcategory_back;
    UpdateCategorydatapojo categorydatapojo;
    String from, ParentCategory_name;
    TextView txt_header;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private String parent_id = "", child_id = "", quick_pitch = "", hour_rate = "", experience_name = "", parent_name = "", price_type = "", child_name = "", min_hourly_rate = "", ratetypestatus = "";
    private TextView hour_rate_text;
    private TextView currency_symbol, txt_hourly_rate;
    private String myCurrencySymbol = "", Price_Type = "";
    private RelativeLayout childCategory_RelativeLayout, hourlyRate_RelativeLayout, levelOfExp_RelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);
        from = getIntent().getStringExtra("from");
        initWidgets();
        initData();

        parentSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (isInternetPresent) {
                    Parentpost = position;
                    mainCategoryID = arrayListParentcategory.get(position).getParentCategoryID();
                    if (!mainCategoryID.equals("select_category")) {
                        //childCategory_RelativeLayout.setVisibility(View.VISIBLE);
                        hourlyRate_RelativeLayout.setVisibility(View.GONE);
                        levelOfExp_RelativeLayout.setVisibility(View.GONE);
                        //GetSubCategories(EditCategoryActivity.this, mainCategoryID, ServiceConstant.GET_SUB_CATEGORY);
                        hourlyRate_RelativeLayout.setVisibility(View.VISIBLE);
                        levelOfExp_RelativeLayout.setVisibility(View.VISIBLE);
                       // GetSubCategoriesDetails(EditCategoryActivity.this, subCategoryID, ServiceConstant.GET_SUB_CATEGORY_DETAILS);



                    } else {
                        childCategory_RelativeLayout.setVisibility(View.GONE);
                        hourlyRate_RelativeLayout.setVisibility(View.GONE);
                        levelOfExp_RelativeLayout.setVisibility(View.GONE);
                    }
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });

        childSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (isInternetPresent) {
                    childPos = position;
                    subCategoryID = arrayListSubcategory.get(position).getParentCategoryID();
                    if (!subCategoryID.equals("select_subcategory")) {
                        hourlyRate_RelativeLayout.setVisibility(View.VISIBLE);
                        levelOfExp_RelativeLayout.setVisibility(View.VISIBLE);
                        GetSubCategoriesDetails(EditCategoryActivity.this, subCategoryID, ServiceConstant.GET_SUB_CATEGORY_DETAILS);
                    } else {
                        hourlyRate_RelativeLayout.setVisibility(View.GONE);
                        levelOfExp_RelativeLayout.setVisibility(View.GONE);
                    }
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });


        levelOfExpSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (isInternetPresent) {
                    expPos = position;
                    selectExp = arrayListLevelofExp.get(position).getParentCategoryID();
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });

    }


    private void initWidgets() {
        cd = new ConnectionDetector(EditCategoryActivity.this);
        arrayListParentcategory = new ArrayList<>();
        arrayListSubcategory = new ArrayList<>();
        arrayListLevelofExp = new ArrayList<>();
        session = new SessionManager(EditCategoryActivity.this);

        parentSpinner = (MaterialSpinner) findViewById(R.id.parentCategory_spinner);
        childSpinner = (MaterialSpinner) findViewById(R.id.childCategory_spinner);
        levelOfExpSpinner = (MaterialSpinner) findViewById(R.id.levelofexp_spinner);

        parentSpinner.setBackgroundResource(R.drawable.spinner_background);
        childSpinner.setBackgroundResource(R.drawable.spinner_background);
        levelOfExpSpinner.setBackgroundResource(R.drawable.spinner_background);

        edittext_hourlyrate = (EditText) findViewById(R.id.edittext_hourlyrate);
        edittext_quickPinch = (EditText) findViewById(R.id.edittext_quickPinch);
        UpdateCategorydatabtn = (Button) findViewById(R.id.btn_positive_categorydata);
        UpdateCategoryDataCancel = (Button) findViewById(R.id.btn_negative_categorydata);
        layout_editcategory_back = (RelativeLayout) findViewById(R.id.layout_editcategory_back);
        UpdateCategorydatabtn.setOnClickListener(EditCategoryActivity.this);
        UpdateCategoryDataCancel.setOnClickListener(EditCategoryActivity.this);
        layout_editcategory_back.setOnClickListener(EditCategoryActivity.this);
        parentCategory_name = (TextView) findViewById(R.id.parentCategory_name);
        childCategory_name = (TextView) findViewById(R.id.childCategory_name);
        levelOfExp_name = (TextView) findViewById(R.id.levelOfExp_name);
        hour_rate_text = (TextView) findViewById(R.id.hour_rate_text);
        currency_symbol = (TextView) findViewById(R.id.currency_symbol);
        txt_hourly_rate = (TextView) findViewById(R.id.categorydialog_set_hourlyrate_lbl);
        txt_header = (TextView) findViewById(R.id.category_dialog_labelname);
        childCategory_RelativeLayout = (RelativeLayout) findViewById(R.id.childCategory_RelativeLayout);
        hourlyRate_RelativeLayout = (RelativeLayout) findViewById(R.id.hourlyRate_RelativeLayout);
        levelOfExp_RelativeLayout = (RelativeLayout) findViewById(R.id.levelOfExp_RelativeLayout);
    }

    private void initData() {
        HashMap<String, String> user = session.getUserDetails();
        provider_id = user.get(SessionManager.KEY_PROVIDERID);
        HashMap<String, String> aAmountMap = session.getWalletDetails();
        String aCurrencyCode = aAmountMap.get(SessionManager.KEY_CURRENCY_CODE);
        myCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(aCurrencyCode);
        currency_symbol.setText(myCurrencySymbol);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            saveCategoryDatas(EditCategoryActivity.this, ServiceConstant.GET_MAIN_CATEGORY);
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }

        if (from.equalsIgnoreCase("edit")) {
            parentSpinner.setVisibility(View.GONE);
            childSpinner.setVisibility(View.GONE);
            parent_id = getIntent().getExtras().getString("parent_id");
            child_id = getIntent().getExtras().getString("child_id");
            quick_pitch = getIntent().getExtras().getString("quick_pitch");
            hour_rate = getIntent().getExtras().getString("hour_rate");
            experience_name = getIntent().getExtras().getString("experience_name");
            parent_name = getIntent().getExtras().getString("parent_name");
            child_name = getIntent().getExtras().getString("child_name");
            min_hourly_rate = getIntent().getExtras().getString("min_hourly_rate");
            ratetypestatus = getIntent().getExtras().getString("ratetypestatus");
            price_type = getIntent().getExtras().getString("price_type");

            selectExp = experience_name;

            childCategory_RelativeLayout.setVisibility(View.VISIBLE);
            hourlyRate_RelativeLayout.setVisibility(View.VISIBLE);
            levelOfExp_RelativeLayout.setVisibility(View.VISIBLE);

            categorydatapojo = new UpdateCategorydatapojo();
            categorydatapojo.setParentID(parent_id);
            categorydatapojo.setChildID(child_id);
            categorydatapojo.setQuickpinch(quick_pitch);
            categorydatapojo.setHourlyRate(hour_rate);
            categorydatapojo.setlevelOfexp(experience_name);
            categorydatapojo.setParentcategory(parent_name);
            categorydatapojo.setChildCategory(child_name);
            categorydatapojo.setMinHourlyRate(min_hourly_rate);

            edittext_quickPinch.setText(categorydatapojo.getQuickpinch());
            edittext_hourlyrate.setText(categorydatapojo.getHourlyRate());
            parentCategory_name.setVisibility(View.VISIBLE);
            parentCategory_name.setText(categorydatapojo.getParentcategory());
            childCategory_name.setVisibility(View.VISIBLE);
            childCategory_name.setText(categorydatapojo.getChildCategory());
//            hour_rate_text.setText(getResources().getString(R.string.setMinrate) + ":" + myCurrencySymbol + categorydatapojo.getMinHourlyRate());
            //edittext_hourlyrate.setHint(getResources().getString(R.string.setMinrate) + ":" + categorydatapojo.getMinHourlyRate());
            currency_symbol.setText(myCurrencySymbol);
            edittext_hourlyrate.setFocusableInTouchMode(true);
            hour_rate_text.setVisibility(View.VISIBLE);

            if (price_type.equalsIgnoreCase("Flat")) {
                hour_rate_text.setVisibility(View.VISIBLE);
//                edittext_hourlyrate.setEnabled(true);
                edittext_hourlyrate.setEnabled(false);
                edittext_hourlyrate.setText(categorydatapojo.getMinHourlyRate());
                txt_hourly_rate.setText(getResources().getString(R.string.editcategory_flate_txt_title));
                hour_rate_text.setText(getResources().getString(R.string.hint_admin_flat));
            } else {
                hour_rate_text.setVisibility(View.VISIBLE);
//                edittext_hourlyrate.setEnabled(true);
                edittext_hourlyrate.setEnabled(false);
                edittext_hourlyrate.setText(categorydatapojo.getMinHourlyRate());
                txt_hourly_rate.setText(getResources().getString(R.string.hour_cost));
//                txt_hourly_rate.setText(getResources().getString(R.string.set_hourlyrate));
                hour_rate_text.setText(getResources().getString(R.string.hint_admin_hourly));
            }
            txt_header.setText(getResources().getString(R.string.edit_category_text));
        }
    }

    private void SetMaterialSpinner(ArrayList<ParentCategorypojo> categorypojoArrayList, MaterialSpinner spinner) {
        if (spinner.getId() == R.id.parentCategory_spinner) {
            parentCategoryAdapter = new ParentCategoryAdapter(EditCategoryActivity.this, categorypojoArrayList);
            spinner.setAdapter(parentCategoryAdapter);

        } else if (spinner.getId() == R.id.childCategory_spinner) {
            childCategoryAdapter = new ChildCategoryAdapter(EditCategoryActivity.this, categorypojoArrayList);
            spinner.setAdapter(childCategoryAdapter);


        }
    }

    private void GetSubCategories(Context context, String mainCategoryID, String url) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("category_id", mainCategoryID);

        System.out.println("GetSubCategories jsonParams" + jsonParams);

        ServiceRequest mservicerequest = new ServiceRequest(context);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("GetSubCategories response" + response);

                String Status = "", category_name = "", category_ID = "";
                try {
                    JSONObject jobject = new JSONObject(response);
                    Status = jobject.getString("status");
                    JSONArray category = jobject.getJSONArray("response");
                    if (Status.equalsIgnoreCase("1")) {
                        arrayListSubcategory.clear();
                        if (category.length() > 0) {
                            ParentCategorypojo parentpojo1 = new ParentCategorypojo();
                            parentpojo1.setParentCategory_name("Select Subcategory");
                            parentpojo1.setParentCategoryID("select_subcategory");
                            arrayListSubcategory.add(parentpojo1);
                            for (int k = 0; k < category.length(); k++) {
                                JSONObject object2 = category.getJSONObject(k);
                                category_name = object2.getString("name");
                                category_ID = object2.getString("id");

                                ParentCategorypojo parentpojo = new ParentCategorypojo();
                                parentpojo.setParentCategory_name(category_name);
                                parentpojo.setParentCategoryID(category_ID);
                                arrayListSubcategory.add(parentpojo);
                            }
//                            setAdapterforParent(arrayListSubcategory, childSpinner);
//                            SetMaterialSpinner(arrayListSubcategory, childSpinner);

                            ArrayList<String> lst = new ArrayList<String>();
                            lst.clear();
                            for (int k = 0; k < arrayListSubcategory.size(); k++) {
                                lst.add(arrayListSubcategory.get(k).getParentCategory_name());
                            }
                            childSpinner.setItems(lst);

//                            if (!from.equalsIgnoreCase("edit")) {
//                                subCategoryID = arrayListSubcategory.get(0).getParentCategoryID();
//                                GetSubCategoriesDetails(EditCategoryActivity.this, subCategoryID, ServiceConstant.GET_SUB_CATEGORY_DETAILS);
//                            }

                        } else {
                            AlertEditCategory(getResources().getString(R.string.sorry), getResources().getString(R.string.alert_no_category));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorListener() {

            }
        });
    }


    private void saveCategoryDatas(Context context, String url) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", provider_id);
        System.out.println("saveCategoryDatas jsonParams" + jsonParams);

        dialog = new LoadingDialog(EditCategoryActivity.this);
        dialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        dialog.show();

        ServiceRequest mservicerequest = new ServiceRequest(context);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("saveCategoryDatas response" + response);

                String Status = "", category_name = "", category_ID = "", level_of_exp_name = "", level_of_exp_ID = "";
                try {
                    JSONObject jobject = new JSONObject(response);
                    Status = jobject.getString("status");
                    JSONArray category = jobject.getJSONArray("response");
                    if (Status.equalsIgnoreCase("1")) {
                        arrayListParentcategory.clear();
                        arrayListLevelofExp.clear();
                        ParentCategorypojo categorypojo1 = new ParentCategorypojo();
                        categorypojo1.setParentCategory_name("Select Category");
                        categorypojo1.setParentCategoryID("select_category");
                        arrayListParentcategory.add(categorypojo1);
                        for (int j = 0; j < category.length(); j++) {
                            JSONObject object2 = category.getJSONObject(j);
                            category_name = object2.getString("name");
                            category_ID = object2.getString("id");

                            ParentCategorypojo categorypojo = new ParentCategorypojo();
                            categorypojo.setParentCategory_name(category_name);
                            categorypojo.setParentCategoryID(category_ID);
                            arrayListParentcategory.add(categorypojo);
                        }

//                        setAdapterforParent(arrayListParentcategory, parentSpinner);
//                        SetMaterialSpinner(arrayListParentcategory, parentSpinner);

                        ArrayList<String> lst = new ArrayList<String>();
                        lst.clear();
                        for (int k = 0; k < arrayListParentcategory.size(); k++) {
                            lst.add(arrayListParentcategory.get(k).getParentCategory_name());
                        }
                        parentSpinner.setItems(lst);

                        JSONArray experience = jobject.getJSONArray("experiencelist");
                        ParentCategorypojo levelofexp1 = new ParentCategorypojo();
                        levelofexp1.setParentCategory_name("Select Experience");
                        levelofexp1.setParentCategoryID("select_experience");
                        arrayListLevelofExp.add(levelofexp1);
                        for (int index = 0; index < experience.length(); index++) {

                            JSONObject object2 = experience.getJSONObject(index);
                            level_of_exp_name = object2.getString("name");
                            level_of_exp_ID = object2.getString("id");

                            ParentCategorypojo levelofexp = new ParentCategorypojo();
                            levelofexp.setParentCategory_name(level_of_exp_name);
                            levelofexp.setParentCategoryID(level_of_exp_ID);
                            arrayListLevelofExp.add(levelofexp);

                        }

                        ArrayList<String> exp_lst = new ArrayList<String>();
                        exp_lst.clear();
                        for (int k = 0; k < arrayListLevelofExp.size(); k++) {
                            exp_lst.add(arrayListLevelofExp.get(k).getParentCategory_name());
                        }
                        levelOfExpSpinner.setItems(exp_lst);


//                        setAdapterforParent(arrayListLevelofExp, levelOfExpSpinner);
                        setLevelofExp();
                        mainCategoryID = arrayListParentcategory.get(0).getParentCategoryID();
                        GetSubCategories(EditCategoryActivity.this, mainCategoryID, ServiceConstant.GET_SUB_CATEGORY);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {

            }
        });
    }

    private void setLevelofExp() {

        if (from.equalsIgnoreCase("edit")) {

            if (!experience_name.equalsIgnoreCase("")) {
                for (int j = 0; j < arrayListLevelofExp.size(); j++) {
                    if (experience_name.equalsIgnoreCase(arrayListLevelofExp.get(j).getParentCategory_name())) {
                        levelOfExpSpinner.setSelectedIndex(j);
                    }
                }
            }

//            int position = levelOfExpCategoryAdapter.getPositionForItem(categorydatapojo.getlevelOfexp());
//            levelOfExpSpinner.setSelectedIndex(position);
        }

    }


    private void GetSubCategoriesDetails(Context context, String subCategoryID, String url) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("subcategory_id", subCategoryID);

        ServiceRequest mservicerequest = new ServiceRequest(context);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                Log.e("getcategorydata", response);
                String status = "";
                try {
                    JSONObject jobject = new JSONObject(response);
                    status = jobject.getString("status");
                    JSONArray category = jobject.getJSONArray("response");
                    if (status.equalsIgnoreCase("1")) {
                        for (int k = 0; k < category.length(); k++) {
                            JSONObject object2 = category.getJSONObject(k);
                            minRate = object2.getString("minrate");
                            Price_Type = object2.getString("ratetype");
                            hour_rate_text.setVisibility(View.VISIBLE);
                            edittext_hourlyrate.setFocusableInTouchMode(true);
                            edittext_hourlyrate.setText("");
//                            hour_rate_text.setText(getResources().getString(R.string.setMinrate) + ":" + myCurrencySymbol + minRate);
                            if (Price_Type.equalsIgnoreCase("Flat")) {
//                                hour_rate_text.setVisibility(View.GONE);
                                hour_rate_text.setVisibility(View.VISIBLE);
                                edittext_hourlyrate.setEnabled(false);
                                edittext_hourlyrate.setText(minRate);
                                txt_hourly_rate.setText(getResources().getString(R.string.editcategory_flate_txt_title));
                                hour_rate_text.setText(getResources().getString(R.string.hint_admin_flat));
                            } else {
                                hour_rate_text.setVisibility(View.VISIBLE);
                                edittext_hourlyrate.setEnabled(false);
                                edittext_hourlyrate.setText(minRate);
//                                edittext_hourlyrate.setEnabled(true);
                                txt_hourly_rate.setText(getResources().getString(R.string.hour_cost));
//                                txt_hourly_rate.setText(getResources().getString(R.string.set_hourlyrate));
                                hour_rate_text.setText(getResources().getString(R.string.hint_admin_hourly));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {

            }
        });
    }


    //--------------Alert Method-----------
    private void Alert(String title, String alert) {
        final PkDialog mDialog = new PkDialog(EditCategoryActivity.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(
                getResources().getString(R.string.action_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                }
        );

        mDialog.show();
    }


    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(EditCategoryActivity.this,
                R.anim.shake);
        editname.startAnimation(shake);

        ForegroundColorSpan fgcspan = new ForegroundColorSpan(
                Color.parseColor("#cc0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_positive_categorydata: {
                if (edittext_hourlyrate.getText().toString().isEmpty() && edittext_hourlyrate.getText().toString().length() == 0) {
                    edittext_hourlyrate.requestFocus();
                    erroredit(edittext_hourlyrate, getResources().getString(R.string.edittext_hourly_rate_validation));
                } else if (from.equalsIgnoreCase("add")) {
                    if (selectExp.equals("select_experience")) {
                        Toast.makeText(EditCategoryActivity.this, getResources().getString(R.string.editcategoryactivity_select_experience_alert), Toast.LENGTH_SHORT).show();
                    } else {
                        Parentpost = parentSpinner.getSelectedIndex();
                        childPos = childSpinner.getSelectedIndex();
                        expPos = levelOfExpSpinner.getSelectedIndex();
                        updateEditedCategoryData(EditCategoryActivity.this, ServiceConstant.ADD_CATEGORY_DATA, childPos, Parentpost, expPos);
                    }
                } else if (from.equalsIgnoreCase("edit")) {
                    if (Float.parseFloat(categorydatapojo.getMinHourlyRate()) > Float.parseFloat(edittext_hourlyrate.getText().toString())) {
                        edittext_hourlyrate.requestFocus();
                        erroredit(edittext_hourlyrate, getResources().getString(R.string.hourly_rate_alert));
                    } else if (selectExp.equals("select_experience")) {
                        Toast.makeText(EditCategoryActivity.this, getResources().getString(R.string.editcategoryactivity_select_experience_alert), Toast.LENGTH_SHORT).show();
                    } else {
                        expPos = levelOfExpSpinner.getSelectedIndex();
                        updateEditedCategoryData(EditCategoryActivity.this, ServiceConstant.UPDATE_CATEGORY, categorydatapojo.getParentID(), categorydatapojo.getChildID(), expPos);
                    }
                }
                break;
            }
            case R.id.layout_editcategory_back:
                onBackPressed();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.btn_negative_categorydata:
                onBackPressed();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;


        }

    }

    private void updateEditedCategoryData(Context context, String url, int childPos, int parentPos, int levelOfExpPos) {

        dialog = new LoadingDialog(EditCategoryActivity.this);
        dialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        dialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("tasker", provider_id);
//        jsonParams.put("quickpitch", edittext_quickPinch.getText().toString());
        jsonParams.put("childid", arrayListParentcategory.get(parentPos).getParentCategoryID());
        jsonParams.put("parentcategory", arrayListParentcategory.get(parentPos).getParentCategoryID());
        jsonParams.put("hourrate", edittext_hourlyrate.getText().toString());
        jsonParams.put("experience", arrayListLevelofExp.get(levelOfExpPos).getParentCategoryID());

        ServiceRequest mservicerequest = new ServiceRequest(context);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                Log.e("getcategorydata", response);
                String message = "", status = "";
                try {
                    JSONObject aObject = new JSONObject(response);
                    message = aObject.getString("response");
                    status = aObject.getString("status");
//                    AlertEditCategory(getResources().getString(R.string.action_loading_sucess), message);
                    if (status.equals("1")) {
                        Intent i = new Intent();
                        i.setAction("com.refresh.editprofilepage");
                        sendBroadcast(i);
                        finish();
                    }
                    dialog.dismiss();
                    Toast.makeText(EditCategoryActivity.this, message, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }

            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }

        });
    }


    private void updateEditedCategoryData(Context context, String url, String parent, String child, int levelOfExpPos) {

        dialog = new LoadingDialog(EditCategoryActivity.this);
        dialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        dialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("tasker", provider_id);
//        jsonParams.put("quickpitch", edittext_quickPinch.getText().toString());
        jsonParams.put("childid", child);
        jsonParams.put("parentcategory", parent);
        jsonParams.put("hourrate", edittext_hourlyrate.getText().toString());
        jsonParams.put("experience", arrayListLevelofExp.get(levelOfExpPos).getParentCategoryID());

        ServiceRequest mservicerequest = new ServiceRequest(context);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                Log.e("getcategorydata", response);
                try {
                    JSONObject aObject = new JSONObject(response);
                    String message = aObject.getString("response");
                    dialog.dismiss();
//                    AlertEditCategory(getResources().getString(R.string.action_loading_sucess), message);

                    Toast.makeText(EditCategoryActivity.this, message, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent();
                    i.setAction("com.refresh.editprofilepage");
                    sendBroadcast(i);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();

            }

        });
    }

    private void AlertEditCategory(String title, String alert) {
        final PkDialog mDialog = new PkDialog(EditCategoryActivity.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setPositiveButton(
                getResources().getString(R.string.action_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent();
                        i.setAction("com.refresh.editprofilepage");
                        sendBroadcast(i);
                        finish();
                    }
                }
        );

        mDialog.show();
    }

}