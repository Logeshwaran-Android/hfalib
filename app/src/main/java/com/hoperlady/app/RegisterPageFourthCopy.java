package com.hoperlady.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hoperlady.Pojo.Availability_selecting_pojo;
import com.hoperlady.Pojo.Category_Pojo;
import com.hoperlady.Pojo.ExperianceCategoryPojo;
import com.hoperlady.Pojo.Experiancepojo;
import com.hoperlady.Pojo.SavedCategoryPojo;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.adapter.Category_Adapter;
import com.hoperlady.adapter.SavedCategoryAdapter;
import com.hoperlady.hockeyapp.ActivityHockeyApp;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import core.Dialog.PkDialog;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;

public class RegisterPageFourthCopy extends ActivityHockeyApp implements Comparator<Category_Pojo> {
    public static DrawerLayout drawerLayout;
    public static EditText EditQuick_pitch_ET, edithourly_ET;
    public static ListView saved_listview;
    //    RelativeLayout register_drawer;
    public static RelativeLayout register_drawer, edit_drawer_RL;
    public static LinearLayout category_select_LL;
    public static ArrayList<SavedCategoryPojo> SavedCategory_ArraryList;
    public static int Edit_Position;
    public static TextView Edit_SubCategory_TV, Edit_Category_TV, edithourly_missing_TV;
    public static SavedCategoryAdapter savedCategoryAdapter;
    public static MaterialSpinner levelofexp_spinner_edit;
    public static ArrayList<String> exp_name_list;
    public static ArrayList<String> exp_id_list;
    public static String Exp_Name = "", edit_MinimumHourlyRate = "";
    public static RelativeLayout save_RL;
    public LinearLayout category_LL, edit_hourly_LL, EditQuickPitch_LL, SubCategory_LL, main_subCategory_LL, sub_Category_other_LL, QuickPitch_LL, hourly_LL;
    public EditText category_ET, Quick_pitch_ET, hourly_ET;
    public ListView category_listview;
    public TextView hourly_TV;
    public ListView sub_category_listview;
    public RelativeLayout quickpitch_missing_RL, editquickpitch_missing_RL, edithourly_missing_RL;
    public RelativeLayout hourly_missing_RL;
    public ConnectionDetector cd;
    public Dialog dialog;
    public ArrayList<Category_Pojo> Category_ArrayList;
    public ArrayList<Category_Pojo> Selected_Category_Arraylist;
    public Category_Adapter CategoryAdapter;
    public TextView no_result_found_TV, Category_TV, SubCategory_TV, hourly_missing_TV;
    public String Minimum_Hourly_Rate = "";
    public String Category = "main category", main_category_id = "", sub_category_id = "", main_category_name = "", sub_category_name = "";
    public Button save_BT, Editsave_BT, cancel_BT;
    public boolean main_category_refresh = false;
    public RelativeLayout register_header_back_layout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public boolean Drawer_Opened = false;
    public MaterialSpinner levelofexp_spinner;
    public ArrayList<ExperianceCategoryPojo> arrayListLevelofExp;
    public String Exp_id = "";
    public SessionManager sessionManager;
    public List<Availability_selecting_pojo> available_days_list;
    public List<Experiancepojo> experianceList;
    private TextView categoryHintTv;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

          /*  if (!category_ET.getText().toString().trim().equalsIgnoreCase("")) {
                if (Category_ArrayList.size() != 0) {
                    SearchCategory(s.toString());
                }
            } else {
                no_result_found_TV.setVisibility(View.GONE);
                category_listview.setVisibility(View.VISIBLE);
                sub_category_listview.setVisibility(View.GONE);

                Selected_Category_Arraylist.clear();
                Selected_Category_Arraylist.addAll(Category_ArrayList);
                CategoryAdapter.notifyDataSetChanged();
            }*/


            if (category_ET.getHint().toString().equals(getString(R.string.headerBar_name_category_label))) {
                if (category_ET.getText().toString().trim().length() != 0) {
                    if (Category_ArrayList.size() != 0) {
                        SearchCategory(s.toString(), getString(R.string.headerBar_name_category_label));
                    }
                    categoryHintTv.setVisibility(View.VISIBLE);
                } else {
                    no_result_found_TV.setVisibility(View.GONE);
                    category_listview.setVisibility(View.VISIBLE);
                    sub_category_listview.setVisibility(View.GONE);
                    categoryHintTv.setVisibility(View.INVISIBLE);

                    Selected_Category_Arraylist.clear();
                    Selected_Category_Arraylist.addAll(Category_ArrayList);
                    CategoryAdapter.notifyDataSetChanged();
                }
            } else if (category_ET.getHint().toString().equals(getString(R.string.sub_category))) {
                if (category_ET.getText().toString().trim().length() != 0) {
                    if (Category_ArrayList.size() != 0) {
                        SearchCategory(s.toString(), getString(R.string.sub_category));
                    }
                    categoryHintTv.setVisibility(View.VISIBLE);
                } else {
                    no_result_found_TV.setVisibility(View.GONE);
                    category_listview.setVisibility(View.GONE);
                    sub_category_listview.setVisibility(View.VISIBLE);
                    categoryHintTv.setVisibility(View.INVISIBLE);

                    Selected_Category_Arraylist.clear();
                    Selected_Category_Arraylist.addAll(Category_ArrayList);
                    CategoryAdapter.notifyDataSetChanged();
                }
            }


            if (!Quick_pitch_ET.getText().toString().trim().equalsIgnoreCase("")) {
                QuickPitch_LL.setBackground(getResources().getDrawable(R.drawable.register_linearbg));
                quickpitch_missing_RL.setVisibility(View.GONE);
            }

            if (!hourly_ET.getText().toString().trim().equalsIgnoreCase("")) {
                hourly_LL.setBackground(getResources().getDrawable(R.drawable.register_linearbg));
                hourly_missing_TV.setText("Hourly Rate should be more than " + Minimum_Hourly_Rate);
                hourly_missing_TV.setTextColor(getResources().getColor(R.color.gray));
            }

            if (!EditQuick_pitch_ET.getText().toString().trim().equalsIgnoreCase("")) {
                editquickpitch_missing_RL.setVisibility(View.GONE);
                EditQuickPitch_LL.setBackground(getResources().getDrawable(R.drawable.register_linearbg));
            }

            if (!edithourly_ET.getText().toString().trim().equalsIgnoreCase("")) {
                edit_hourly_LL.setBackground(getResources().getDrawable(R.drawable.register_linearbg));
                edithourly_missing_TV.setTextColor(getResources().getColor(R.color.gray));
            }
        }
    };

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void UpdateListview(final int position, Context context) {

        final PkDialog mDialog = new PkDialog(context);
        mDialog.setDialogTitle(context.getResources().getString(R.string.alert));
        mDialog.setDialogMessage(context.getResources().getString(R.string.delete_category));
        mDialog.setPositiveButton(context.getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedCategory_ArraryList.remove(position);
                setListViewHeightBasedOnChildren(saved_listview);
                savedCategoryAdapter.notifyDataSetChanged();
                mDialog.dismiss();

            }
        });
        mDialog.setNegativeButton(context.getResources().getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public static void EditCategory(int position) {
        save_RL.setVisibility(View.GONE);
        edit_drawer_RL.setVisibility(View.VISIBLE);
        category_select_LL.setVisibility(View.GONE);
        drawerLayout.openDrawer(register_drawer);
        Edit_Position = position;
        if (SavedCategory_ArraryList.size() > 0) {
            Edit_SubCategory_TV.setText(SavedCategory_ArraryList.get(position).getCategoryName());
            Edit_Category_TV.setText(SavedCategory_ArraryList.get(position).getSubCategoryName());
            EditQuick_pitch_ET.setText(SavedCategory_ArraryList.get(position).getQiuckPitch());
            edithourly_ET.setText(SavedCategory_ArraryList.get(position).getHourlyRate());
            edithourly_missing_TV.setText(SavedCategory_ArraryList.get(position).getMinimumHourlyRate());
            edit_MinimumHourlyRate = SavedCategory_ArraryList.get(position).getMinimumHourlyRate();
            edithourly_ET.setSelection(edithourly_ET.getText().length());
            levelofexp_spinner_edit.setItems(exp_name_list);
            String exp_name = SavedCategory_ArraryList.get(position).getExperianceNmae();
            if (!exp_name.equalsIgnoreCase("")) {
                for (int j = 0; j < exp_name_list.size(); j++) {
                    if (exp_name.equalsIgnoreCase(exp_name_list.get(j))) {
                        levelofexp_spinner_edit.setSelectedIndex(j);
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerpage_fourth_layout);
        initialize();
        clickListners();
    }

    private void initialize() {
        available_days_list = new ArrayList<Availability_selecting_pojo>();
        experianceList = new ArrayList<Experiancepojo>();
        cd = new ConnectionDetector(RegisterPageFourthCopy.this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        category_LL = (LinearLayout) findViewById(R.id.category_LL);
        SubCategory_LL = (LinearLayout) findViewById(R.id.SubCategory_LL);
        edit_hourly_LL = (LinearLayout) findViewById(R.id.edit_hourly_LL);
        EditQuickPitch_LL = (LinearLayout) findViewById(R.id.EditQuickPitch_LL);
        main_subCategory_LL = (LinearLayout) findViewById(R.id.main_subCategory_LL);
        sub_Category_other_LL = (LinearLayout) findViewById(R.id.sub_Category_other_LL);
        QuickPitch_LL = (LinearLayout) findViewById(R.id.QuickPitch_LL);
        hourly_LL = (LinearLayout) findViewById(R.id.hourly_LL);
        category_select_LL = (LinearLayout) findViewById(R.id.category_select_LL);
        category_ET = (EditText) findViewById(R.id.category_ET);
        Quick_pitch_ET = (EditText) findViewById(R.id.Quick_pitch_ET);
        hourly_TV = (TextView) findViewById(R.id.hourly_TV);
        hourly_ET = (EditText) findViewById(R.id.hourly_ET);
        edithourly_ET = (EditText) findViewById(R.id.edithourly_ET);
        EditQuick_pitch_ET = (EditText) findViewById(R.id.EditQuick_pitch_ET);
        edit_drawer_RL = (RelativeLayout) findViewById(R.id.edit_drawer_RL);
        register_drawer = (RelativeLayout) findViewById(R.id.register_drawer);
        quickpitch_missing_RL = (RelativeLayout) findViewById(R.id.quickpitch_missing_RL);
        save_RL = (RelativeLayout) findViewById(R.id.save_RL);
        editquickpitch_missing_RL = (RelativeLayout) findViewById(R.id.editquickpitch_missing_RL);
        edithourly_missing_RL = (RelativeLayout) findViewById(R.id.edithourly_missing_RL);
        hourly_missing_RL = (RelativeLayout) findViewById(R.id.hourly_missing_RL);
        category_listview = (ListView) findViewById(R.id.category_listview);
        sub_category_listview = (ListView) findViewById(R.id.sub_category_listview);
        saved_listview = (ListView) findViewById(R.id.saved_listview);
        register_header_back_layout = (RelativeLayout) findViewById(R.id.register_header_back_layout);
        saved_listview.setVisibility(View.GONE);
        SavedCategory_ArraryList = new ArrayList<SavedCategoryPojo>();
        no_result_found_TV = (TextView) findViewById(R.id.no_result_found_TV);
        Category_TV = (TextView) findViewById(R.id.Category_TV);
        SubCategory_TV = (TextView) findViewById(R.id.SubCategory_TV);
        hourly_missing_TV = (TextView) findViewById(R.id.hourly_missing_TV);
        edithourly_missing_TV = (TextView) findViewById(R.id.edithourly_missing_TV);
        Edit_SubCategory_TV = (TextView) findViewById(R.id.Edit_SubCategory_TV);
        Edit_Category_TV = (TextView) findViewById(R.id.Edit_Category_TV);
        save_BT = (Button) findViewById(R.id.save_BT);
        cancel_BT = (Button) findViewById(R.id.cancel_BT);
        Editsave_BT = (Button) findViewById(R.id.Editsave_BT);
        categoryHintTv = findViewById(R.id.category_hint_TV);
        category_ET.addTextChangedListener(textWatcher);
        Quick_pitch_ET.addTextChangedListener(textWatcher);
        EditQuick_pitch_ET.addTextChangedListener(textWatcher);
        hourly_ET.addTextChangedListener(textWatcher);
        edithourly_ET.addTextChangedListener(textWatcher);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        levelofexp_spinner = (MaterialSpinner) findViewById(R.id.levelofexp_spinner);
        levelofexp_spinner_edit = (MaterialSpinner) findViewById(R.id.levelofexp_spinner_edit);
        sessionManager = new SessionManager(RegisterPageFourthCopy.this);
        available_days_list = sessionManager.getAvailDays();
        experianceList = sessionManager.getExperianceList();

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Drawer_Opened = true;
                save_RL.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (SavedCategory_ArraryList.size() > 0 && sub_Category_other_LL.getVisibility() == View.GONE) {
                    save_RL.setVisibility(View.VISIBLE);
                }

                Drawer_Opened = false;
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (cd.isConnectingToInternet()) {
//            LevelOfExperienceRequest(ServiceConstant.REGISTER_EXPERIANCE_LEVEL_URL);
            LevelOfExperienceRequest(ServiceConstant.GET_MAIN_CATEGORY);
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }

    private void LevelOfExperienceRequest(String url) {

        HashMap<String, String> user = sessionManager.getUserDetails();
        String provider_id = user.get(SessionManager.KEY_PROVIDERID);

        final HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", provider_id);

        ServiceRequest mservicerequest = new ServiceRequest(RegisterPageFourthCopy.this);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                String Status = "";

                arrayListLevelofExp = new ArrayList<ExperianceCategoryPojo>();
                exp_name_list = new ArrayList<String>();
                exp_id_list = new ArrayList<String>();

                try {
                    JSONObject jobject = new JSONObject(response);
                    Status = jobject.getString("status");
                    if (Status.equalsIgnoreCase("1")) {
                        arrayListLevelofExp.clear();

                        JSONArray experience = jobject.getJSONArray("experiencelist");

                        for (int i = 0; i < experience.length(); i++) {

                            JSONObject object2 = experience.getJSONObject(i);
                            exp_name_list.add(object2.getString("name"));
                            exp_id_list.add(object2.getString("id"));

                            Exp_Name = object2.getString("name");
                            Exp_id = object2.getString("id");

                            if (i == 0) {
                                Exp_id = object2.getString("id");
                                Exp_Name = object2.getString("name");
                            }

                            ExperianceCategoryPojo levelofexp = new ExperianceCategoryPojo();
                            levelofexp.setName(Exp_Name);
                            levelofexp.setId(Exp_id);
                            arrayListLevelofExp.add(levelofexp);
                        }

                        ArrayList<String> exp_lst = new ArrayList<String>();
                        exp_lst.clear();
                        for (int k = 0; k < arrayListLevelofExp.size(); k++) {
                            exp_lst.add(arrayListLevelofExp.get(k).getName());
                        }
                        levelofexp_spinner.setItems(exp_lst);

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


/*
    private void LevelOfExperienceRequest(String url) {

        final HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("tasker", "category");

        ServiceRequest mservicerequest = new ServiceRequest(RegisterPageFourth.this);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                experianceCategoryPojoArrayList = new ArrayList<ExperianceCategoryPojo>();
                exp_name_list = new ArrayList<String>();
                exp_id_list = new ArrayList<String>();
                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONArray category = jobject.getJSONArray("response");

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject listObject = jsonArray.getJSONObject(i);
                            exp_name_list.add(listObject.getString("name"));
                            exp_id_list.add(listObject.getString("_id"));
                            if (i == 0) {
                                Exp_id = listObject.getString("_id");
                                Exp_Name = listObject.getString("name");
                            }
                        }
                        levelofexp_spinner.setItems(exp_name_list);
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
*/


    private void Alert(String title, String alert) {
        final PkDialog mDialog = new PkDialog(RegisterPageFourthCopy.this);
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

    private void Alertsuccess(String title, String alert) {
        final PkDialog mDialog = new PkDialog(RegisterPageFourthCopy.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(
                getResources().getString(R.string.action_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                    }
                }
        );
        mDialog.show();
    }

    private void clickListners() {
        register_header_back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                finish();
            }
        });

        category_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_RL.setVisibility(View.GONE);
                edit_drawer_RL.setVisibility(View.GONE);
                category_select_LL.setVisibility(View.VISIBLE);

                drawerLayout.openDrawer(register_drawer);

                category_ET.setHint(getResources().getString(R.string.headerBar_name_category_label));
                categoryHintTv.setText(getString(R.string.headerBar_name_category_label));
                category_listview.setVisibility(View.GONE);
                sub_category_listview.setVisibility(View.GONE);
                String category_id = "";

                if (cd.isConnectingToInternet()) {
                    GetCategories(RegisterPageFourthCopy.this, ServiceConstant.REGISTER_CATEGORY_URL, category_id);
                }

            }
        });

        SubCategory_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_RL.setVisibility(View.GONE);
                edit_drawer_RL.setVisibility(View.GONE);
                category_select_LL.setVisibility(View.VISIBLE);
                drawerLayout.openDrawer(register_drawer);
                category_ET.setHint(getResources().getString(R.string.sub_category));
                categoryHintTv.setText(getString(R.string.sub_category));
                category_listview.setVisibility(View.GONE);
                sub_category_listview.setVisibility(View.GONE);
                categoryHintTv.setVisibility(View.INVISIBLE);
                if (cd.isConnectingToInternet()) {
                    GetCategories(RegisterPageFourthCopy.this, ServiceConstant.REGISTER_SUB_CATEGORY_URL, main_category_id);
                }

            }
        });

        category_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (cd.isConnectingToInternet()) {
                    main_subCategory_LL.setVisibility(View.VISIBLE);
                    //   sub_Category_other_LL.setVisibility(View.GONE);
                    category_ET.setHint(getResources().getString(R.string.sub_category));
                    categoryHintTv.setText(getString(R.string.sub_category));
                    main_category_id = Selected_Category_Arraylist.get(position).getCategory_id();
                    main_category_name = Selected_Category_Arraylist.get(position).getCategoryName();
                    Category_TV.setText(Selected_Category_Arraylist.get(position).getCategoryName());

                    //    drawerLayout.closeDrawer(register_drawer);
                    sub_Category_other_LL.setVisibility(View.GONE);
                    categoryHintTv.setVisibility(View.INVISIBLE);
                    GetCategories(RegisterPageFourthCopy.this, ServiceConstant.REGISTER_SUB_CATEGORY_URL, main_category_id);


                }
            }
        });

        sub_category_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!main_category_id.equalsIgnoreCase("")) {
                    sub_category_id = Selected_Category_Arraylist.get(position).getCategory_id();
                    sub_category_name = Selected_Category_Arraylist.get(position).getCategoryName();
                    SubCategory_TV.setText(Selected_Category_Arraylist.get(position).getCategoryName());

                    Minimum_Hourly_Rate = Selected_Category_Arraylist.get(position).getHourly_Rate();
                    hourly_missing_TV.setText(getResources().getString(R.string.hint_hour) + "" + Selected_Category_Arraylist.get(position).getHourly_Rate());
                    sub_Category_other_LL.setVisibility(View.VISIBLE);
                    save_RL.setVisibility(View.GONE);
                    drawerLayout.closeDrawer(register_drawer);

                }
            }
        });
        levelofexp_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                Exp_id = exp_id_list.get(position);
                Exp_Name = exp_name_list.get(position);
            }
        });


        levelofexp_spinner_edit.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                Exp_id = exp_id_list.get(position);
                Exp_Name = exp_name_list.get(position);
            }
        });

        save_RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    postFinalRequest(ServiceConstant.REGISTER_FINAL_LEVEL_URL);
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });
        cancel_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SavedCategory_ArraryList.size() > 0 && sub_Category_other_LL.getVisibility() == View.VISIBLE) {

                    Category_TV.setText(getResources().getString(R.string.main_category));
                    hourly_ET.setText("");
                    Quick_pitch_ET.setText("");

                    save_RL.setVisibility(View.VISIBLE);
                    main_subCategory_LL.setVisibility(View.GONE);
                    sub_Category_other_LL.setVisibility(View.GONE);
                    sub_Category_other_LL.setVisibility(View.GONE);
                } else {
                    Category_TV.setText(getResources().getString(R.string.main_category));
                    hourly_ET.setText("");
                    Quick_pitch_ET.setText("");
                    save_RL.setVisibility(View.GONE);
                    main_subCategory_LL.setVisibility(View.GONE);
                    sub_Category_other_LL.setVisibility(View.GONE);
                    sub_Category_other_LL.setVisibility(View.GONE);
                }
            }
        });
        save_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int min_hourlyrate = 0;
                if (hourly_ET.getText().toString().equals("") || hourly_ET.getText().toString().equals(" ")) {

                } else {
                    min_hourlyrate = Integer.parseInt(hourly_ET.getText().toString());
                }

                if (hourly_ET.getText().toString().trim().equalsIgnoreCase("")) {
                    hourly_LL.setBackground(getResources().getDrawable(R.drawable.register_empty_linearbg));
                    hourly_missing_TV.setText(getResources().getString(R.string.hourly_cost));
                    hourly_missing_TV.setTextColor(getResources().getColor(R.color.app_color));
                } else if (min_hourlyrate < Integer.parseInt(Minimum_Hourly_Rate)) {
                    Alert(getResources().getString(R.string.sorry), getResources().getString(R.string.enter_minimum_hourly_rate));
                } else {
                    boolean category_id_present = false;
                    if (SavedCategory_ArraryList.size() != 0) {
                        for (int i = 0; i < SavedCategory_ArraryList.size(); i++) {
                            if (SavedCategory_ArraryList.get(i).getSubCategoryID().equalsIgnoreCase(sub_category_id)) {
                                category_id_present = true;
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.category_already_added), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    if (!category_id_present) {
                        SavedCategoryPojo pojo = new SavedCategoryPojo();
                        pojo.setCategoryID(main_category_id);
                        pojo.setSubCategoryID(sub_category_id);
                        pojo.setCategoryName(main_category_name);
                        pojo.setSubCategoryName(sub_category_name);
                        pojo.setHourlyRate(hourly_ET.getText().toString().trim());
                        pojo.setQiuckPitch(Quick_pitch_ET.getText().toString().trim());
                        pojo.setExperianceID(Exp_id);
                        pojo.setExperianceNmae(Exp_Name);
                        pojo.setMinimumHourlyRate(Minimum_Hourly_Rate);
                        SavedCategory_ArraryList.add(pojo);
                        if (SavedCategory_ArraryList.size() > 0) {
                            Category_TV.setText(getResources().getString(R.string.main_category));
                            hourly_ET.setText("");
                            Quick_pitch_ET.setText("");
                            save_RL.setVisibility(View.VISIBLE);

                            if (SavedCategory_ArraryList.size() == 1) {
                                savedCategoryAdapter = new SavedCategoryAdapter(RegisterPageFourthCopy.this, SavedCategory_ArraryList);
                                saved_listview.setAdapter(savedCategoryAdapter);
                                saved_listview.setVisibility(View.VISIBLE);
                                main_subCategory_LL.setVisibility(View.GONE);
                                sub_Category_other_LL.setVisibility(View.GONE);
                            } else {
                                saved_listview.setVisibility(View.VISIBLE);
                                if (savedCategoryAdapter != null) {
                                    saved_listview.setVisibility(View.VISIBLE);
                                    main_subCategory_LL.setVisibility(View.GONE);
                                    sub_Category_other_LL.setVisibility(View.GONE);
                                    setListViewHeightBasedOnChildren(saved_listview);
                                    savedCategoryAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

            }
        });
        Editsave_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int edit_hourlyvalue = Integer.parseInt(edithourly_ET.getText().toString());
                if (edithourly_ET.getText().toString().trim().equalsIgnoreCase("")) {
                    edit_hourly_LL.setBackground(getResources().getDrawable(R.drawable.register_empty_linearbg));
                    edithourly_missing_TV.setText(getResources().getString(R.string.enter_hourly_rate));
                    hourly_missing_TV.setTextColor(getResources().getColor(R.color.app_color));
                }
                // edithourly_ET.getText().toString();

                else if (edit_hourlyvalue < Integer.parseInt(edit_MinimumHourlyRate)) {
                    Alert(getResources().getString(R.string.sorry), getResources().getString(R.string.enter_minimum_hourly_rate));
                } else {
                    if (SavedCategory_ArraryList.size() > 0) {
                        SavedCategory_ArraryList.get(Edit_Position).setQiuckPitch(EditQuick_pitch_ET.getText().toString().trim());
                        SavedCategory_ArraryList.get(Edit_Position).setHourlyRate(edithourly_ET.getText().toString().trim());
                        SavedCategory_ArraryList.get(Edit_Position).setExperianceID(Exp_id);
                        SavedCategory_ArraryList.get(Edit_Position).setExperianceNmae(Exp_Name);
                        drawerLayout.closeDrawer(register_drawer);
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }

                }
            }
        });
    }

    private void postFinalRequest(String registerFinalLevelUrl) {

        dialog = new Dialog(RegisterPageFourthCopy.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonParams = new JSONObject();

        try {
            jsonParams.put("username", sessionManager.getTaskerDetails().getTaskerName());
            jsonParams.put("email", sessionManager.getTaskerDetails().getTaskerEmail());
            jsonParams.put("password", sessionManager.getTaskerDetails().getTaskerPswrd());
            jsonParams.put("password_confirmation", sessionManager.getTaskerDetails().getTaskerPswrd());
            jsonParams.put("phone_code", sessionManager.getTaskerDetails().getTaskerCountryCde());
            jsonParams.put("phone_number", sessionManager.getTaskerDetails().getTaskerMblNumbr());
            jsonParams.put("gender", sessionManager.getTaskerDetails().getTaskerGender());
            jsonParams.put("birthdate", sessionManager.getTaskerDetails().getTaskerDOB());
            jsonParams.put("lat", sessionManager.getTaskerDetails().getTaskerAddressLat());
            jsonParams.put("availability_address", sessionManager.getTaskerDetails().getTaskerWrkLocation());
            jsonParams.put("location_lat", sessionManager.getTaskerDetails().getTaskerWork_lat());
            jsonParams.put("location_lng", sessionManager.getTaskerDetails().getTaskerWork_long());
            jsonParams.put("radius", sessionManager.getTaskerDetails().getTaskerRadius());
            jsonParams.put("long", sessionManager.getTaskerDetails().getTaskerAddressLng());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject object = new JSONObject();
            object.put("city", sessionManager.getTaskerDetails().getTaskerTown());
            object.put("state", sessionManager.getTaskerDetails().getTaskerState());
            object.put("country", sessionManager.getTaskerDetails().getTaskerCountry());
            object.put("line1", sessionManager.getTaskerDetails().getTaskerAddress());
            object.put("line2", sessionManager.getTaskerDetails().getTaskerAddress());
            jsonParams.put("address", object.toString());
            //  jsonObject.put("address",object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("postFinalRequest jsonParams" + jsonParams);


        if (RegisterPageFirst.Image_Uploaded) {
            System.out.println("RegisterPageSecond.Image_Uploaded" + RegisterPageFirst.Image_Uploaded);
            try {
                jsonParams.put("image", sessionManager.RegisterImageurl());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("RegisterPageSecond.Image_Uploaded" + RegisterPageFirst.Image_Uploaded);
        }

        JSONArray workingarray = new JSONArray();

        for (int i = 0; i < available_days_list.size(); i++) {
            try {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("day", available_days_list.get(i).getDay_name());
                JSONObject user = new JSONObject();
                if (available_days_list.get(i).isMorning() == true) {
                    user.put("morning", "1");
                } else {
                    user.put("morning", "0");
                }
                if (available_days_list.get(i).isAfternoon() == true) {
                    user.put("afternoon", "1");
                } else {
                    user.put("afternoon", "0");
                }
                if (available_days_list.get(i).isEvening() == true) {
                    user.put("evening", "1");
                } else {
                    user.put("evening", "0");
                }
                jsonObject1.put("hour", user);
                workingarray.put(jsonObject1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            jsonParams.put("working_days", workingarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray1 = new JSONArray();
        for (int i = 0; i <= experianceList.size(); i++) {
            try {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("question", experianceList.get(i).getId());
                jsonObject1.put("answer", experianceList.get(i).getAnswer());
                jsonArray1.put(jsonObject1);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            jsonParams.put("profile_details", jsonArray1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < SavedCategory_ArraryList.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("categoryid", SavedCategory_ArraryList.get(i).getCategoryID());
                jsonObject.put("childid", SavedCategory_ArraryList.get(i).getSubCategoryID());
                jsonObject.put("hour_rate", SavedCategory_ArraryList.get(i).getHourlyRate());
                jsonObject.put("experience", SavedCategory_ArraryList.get(i).getExperianceID());
                jsonArray.put(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            jsonParams.put("taskerskills", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, registerFinalLevelUrl, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String str_Status, str_Message, str_Response;

                            str_Status = jsonObject.getString("status");

                            if (str_Status.equalsIgnoreCase("1")) {
                                str_Message = jsonObject.getString("response");
                                Alertsuccess(getResources().getString(R.string.action_loading_sucess), str_Message);
                                //    FinalAlert(getResources().getString(R.string.action_loading_sucess), response);

                            } else {
                                str_Response = str_Message = jsonObject.getString("response");
                                Alert(getResources().getString(R.string.alert_label_title), str_Response);

                            }
                            dialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                        }

                        String Str_Status = "";
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);

    }

    private void SearchCategory(String s, String categoryType) {
        Selected_Category_Arraylist.clear();

        for (Category_Pojo category : Category_ArrayList) {
            if (category.getCategoryName().toLowerCase(Locale.ENGLISH).contains(s.toLowerCase())) {
                Selected_Category_Arraylist.add(category);
            }
        }

        if (Selected_Category_Arraylist.size() > 0) {
            no_result_found_TV.setVisibility(View.GONE);

            if (categoryType.equals(getString(R.string.headerBar_name_category_label))) {
                category_listview.setVisibility(View.VISIBLE);
                sub_category_listview.setVisibility(View.GONE);
            } else {
                category_listview.setVisibility(View.GONE);
                sub_category_listview.setVisibility(View.VISIBLE);
            }
        } else {
            no_result_found_TV.setVisibility(View.VISIBLE);
            category_listview.setVisibility(View.GONE);
            sub_category_listview.setVisibility(View.GONE);
        }

        CategoryAdapter.notifyDataSetChanged();
    }

    private void GetCategories(Context context, String app_categories_uRl, final String Category_id) {

        dialog = new Dialog(RegisterPageFourthCopy.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        if (!Category_id.equalsIgnoreCase("")) {
            jsonParams.put("category", Category_id);
        }

        System.out.print("GetCategories jsonParams" + jsonParams);

        final TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));
        ServiceRequest serviceRequest = new ServiceRequest(context);
        serviceRequest.makeServiceRequest(app_categories_uRl, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.print("GetCategories response" + response);
                String Status = "", Response = "";
                try {
                    //JSONObject jsonObject = new JSONObject(response);

                    Category_ArrayList = new ArrayList<Category_Pojo>();
                    Category_ArrayList.clear();
                    // JSONObject response_object = jsonObject.getJSONObject("response");

                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Category_Pojo pojo = new Category_Pojo();
                            pojo.setCategory_id(object.getString("_id"));
                            pojo.setCategoryName(object.getString("name"));
                            pojo.setCategory_Image("");
                            if (object.has("minimum_hourly_rate")) {
                                pojo.setHourly_Rate(object.getString("minimum_hourly_rate"));
                            }
                            Category_ArrayList.add(pojo);
                        }
                    }
                    dialog.dismiss();
                    if (Category_ArrayList.size() > 0) {
                        Collections.sort(Category_ArrayList, RegisterPageFourthCopy.this);
                        Selected_Category_Arraylist = new ArrayList<Category_Pojo>();
                        Selected_Category_Arraylist.clear();
                        Selected_Category_Arraylist.addAll(Category_ArrayList);
                        CategoryAdapter = new Category_Adapter(RegisterPageFourthCopy.this, Selected_Category_Arraylist);
                        if (!Category_id.equalsIgnoreCase("")) {
                            sub_category_listview.setAdapter(CategoryAdapter);
                            sub_category_listview.setVisibility(View.VISIBLE);
                            category_listview.setVisibility(View.GONE);
                        } else {
                            sub_category_listview.setVisibility(View.GONE);
                            category_listview.setAdapter(CategoryAdapter);
                            category_listview.setVisibility(View.VISIBLE);
                            SubCategory_TV.setText(getResources().getString(R.string.sub_category));
                        }
                        category_ET.setText(null);
                    }

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
//          alertDelete("Alert","Are you sure, you want to delete",position);

    @Override
    public int compare(Category_Pojo lhs, Category_Pojo rhs) {
        return lhs.getCategoryName().compareTo(rhs.getCategoryName());
    }

    @Override
    public void onBackPressed() {

        if (Drawer_Opened) {
            drawerLayout.closeDrawer(register_drawer);
        } else {
            super.onBackPressed();
        }
    }

//    private static void alertDelete(String title, String alert, int position) {
//        final PkDialog mDialog = new PkDialog(RegisterPageFourth.this);
//        mDialog.setDialogTitle(title);
//        mDialog.setDialogMessage(alert);
//        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        mDialog.setNegativeButton(getResources().getString(R.string.cancel), new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDialog.dismiss();
//            }
//        });
//        mDialog.show();
//    }

}
