package com.hoperlady.app;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.countrycodepicker.Country;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hoperlady.FCM.Config;
import com.hoperlady.Pojo.DocumentUploadPojo;
import com.hoperlady.Pojo.Experiancepojo;
import com.hoperlady.Pojo.RegisterLicenceUploadPojo;
import com.hoperlady.Pojo.TaskerInfoPojo;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.adapter.ExperianceAdapter;
import com.hoperlady.adapter.GenderAdapter;
import com.hoperlady.adapter.RegisterListAdapter;
import com.hoperlady.hockeyapp.ActivityHockeyApp;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import core.Dialog.PkDialog;
import core.Dialog.PkLoadingDialog;
import core.Map.GPSTracker;
import core.Volley.AppController;
import core.Volley.ServiceRequest;
import core.Volley.VolleyMultipartRequest;
import core.service.ServiceConstant;
import me.piruin.quickaction.ActionItem;
import me.piruin.quickaction.QuickAction;


public class RegisterPageFirst extends ActivityHockeyApp implements Comparator<Country> {
    private LinearLayout email_LL, continue_button_LL, countrycode_LL, dob_LL, address_LL;
    private EditText email_ET, mobile_ET,
            firstname_ET, lastname_ET, dob_ET, address_ET, radius_ET, gender_ET;
    private RelativeLayout dob_missing_RL, email_missing_RL,
            gender_missing_RL, male_RL, female_RL, enable_eye_RL, register_drawer, register_header_back_layout, rl_terms,
            firstname_missing_RL, lastname_missing_RL, address_missing_RL, radius_missing_RL;
    private TextView firstname_missing_TV;

    private TextView dob_invalid_TV, gender_invalid_TV, address_invalid_TV, radius_invalid_TV;
    private LinearLayout firstname_LL, lastname_LL;
    private DatePickerDialog datePickerDialog;
    private RelativeLayout rl_terms_conditions;
    private SimpleDateFormat simpleDateFormat;
    private ImageView enable_eye_IV;
    private String str_text = "Hide", mySelectedSpnrValueSTR = "", Selected_Gender = "", Phone_CountryCode = "";
    private DrawerLayout register_drawer_layout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private int mySpnrcheck = 0, Gender_Position = 0;
    private boolean Drawer_Opened = false;
    private CheckBox cb_terms_conditions;
    private TextView TvLogin;
    private TextView dob_TV, country_code_TV, email_invalid_TV, invalid_password_TV, Terms_TV, no_result_found_TV, myTermsCondTXT, txt_terms_conditions, myPrivacyPolicyTXT;
    private List<Country> allCountriesList;
    private List<Country> selectedCountriesList;
    private RegisterListAdapter CountryCodeAdapter;
    private ListView register_phone_listview;
    private GPSTracker gpsTracker;
    private GenderAdapter myAdapter;
    private ServiceRequest myRequest;
    private PkLoadingDialog myLoadingDialog;
    private boolean isSpnrItemSlctd = false;
    private SessionManager sessionManager;
    private SessionManagerRegistration sessionManagerRegistration;
    private StringBuffer strBuf;
    private ConnectionDetector cd;

    private String dobDate = "", formateDate = "";

    private ExperianceAdapter expAdapter;
    private ListView experiance_listview;

    private ArrayList<String> myGenderArr;

    private ArrayList<Experiancepojo> ExperianceList;
    private ArrayList<String> getExperianceArrayList;
    private int placeSearch_request_code = 200;
    private String SselectedLatitude = "", SselectedLongitude = "", Selected_Location = "", selected_zipcode = "",
            selected_country = "", selected_state = "", selected_city = "", selected_line2 = "";

    TextView titleTV, subTitleTV;


    MaterialSpinner myGenderSpnr;
    String str_gender = "";

    CircularImageView round_IV;
    Dialog photo_dialog;
    private static int CAMERA_REQUEST_2 = 1;
    private static int PICK_IMAGE = 2;
    File captured_image;
    File imageRoot;
    String appDirectoryName;
    final int PERMISSION_REQUEST_CODE = 111;
    private Uri mImageCaptureUri;
    String imagePath = "";
    boolean rotate = false;
    Uri outputUri = null;
    Uri selectedImage;
    private byte[] byteArray;
    Bitmap finalPic = null;
    String encode = "";
    Bitmap bitMapThumbnail;
    public static boolean Image_Uploaded = false;

    String gcmID = "";

    ImageView add_icon_IM;

    String country_code = "", phone_number = "", date_format = "", radius = "", sSitemode = "";

    private ImageView maleGenderIv, femaleGenderIv, otherGenderIv;
    private String strGender = "";

    private TextView countinueTV;

    private String blockCharacterSet = "~#^|$%&*!";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_first_layout);
        initialize();
        clickListners();
    }

    private void SearchCountryCode(String s) {
        selectedCountriesList.clear();

        for (Country country : allCountriesList) {
            if (country.getName().toLowerCase(Locale.ENGLISH)
                    .contains(s.toLowerCase())) {
                selectedCountriesList.add(country);
            }
        }

        if (selectedCountriesList.size() > 0) {
            no_result_found_TV.setVisibility(View.GONE);
            register_phone_listview.setVisibility(View.VISIBLE);
        } else {
            no_result_found_TV.setVisibility(View.VISIBLE);
            register_phone_listview.setVisibility(View.GONE);
        }

        CountryCodeAdapter.notifyDataSetChanged();

    }

    private void initialize() {

        Intent intent = getIntent();
        country_code = intent.getStringExtra("country_code");
        phone_number = intent.getStringExtra("phone_number");
        date_format = intent.getStringExtra("date_format");
        radius = intent.getStringExtra("radius");
        sSitemode = intent.getStringExtra("sitemode");

        sessionManager = new SessionManager(RegisterPageFirst.this);
        sessionManagerRegistration = new SessionManagerRegistration(RegisterPageFirst.this);
        cd = new ConnectionDetector(RegisterPageFirst.this);

        HashMap<String, String> details = sessionManager.getUserDetails();
        gcmID = details.get(SessionManager.KEY_GCM_ID);

        email_missing_RL = (RelativeLayout) findViewById(R.id.email_missing_RL);
        rl_terms_conditions = (RelativeLayout) findViewById(R.id.rl_terms_conditions);
        rl_terms = (RelativeLayout) findViewById(R.id.rl_terms);
        firstname_missing_RL = (RelativeLayout) findViewById(R.id.firstname_missing_RL);
        lastname_missing_RL = (RelativeLayout) findViewById(R.id.lastname_missing_RL);

        firstname_missing_TV = (TextView) findViewById(R.id.firstname_missing_TV);

        experiance_listview = (ListView) findViewById(R.id.experiance_listview);

        gender_missing_RL = (RelativeLayout) findViewById(R.id.gender_missing_RL);
        dob_missing_RL = (RelativeLayout) findViewById(R.id.dob_missing_RL);
        address_missing_RL = (RelativeLayout) findViewById(R.id.address_missing_RL);
        radius_missing_RL = (RelativeLayout) findViewById(R.id.radius_missing_RL);
        register_header_back_layout = (RelativeLayout) findViewById(R.id.register_header_back_layout);
//        enable_eye_RL = (RelativeLayout) findViewById(R.id.enable_eye_RL);
        TvLogin = (TextView) findViewById(R.id.TvLogin);
        email_ET = (EditText) findViewById(R.id.email_ET);
        mobile_ET = (EditText) findViewById(R.id.mobile_ET);
//        register_phoneNumber_edittext = (EditText) findViewById(R.id.register_phoneNumber_edittext);
        continue_button_LL = (LinearLayout) findViewById(R.id.continue_button_LL);
        countrycode_LL = (LinearLayout) findViewById(R.id.countrycode_LL);

        maleGenderIv = (ImageView) findViewById(R.id.maleGenderIv);
        femaleGenderIv = (ImageView) findViewById(R.id.femaleGenderIv);
        otherGenderIv = (ImageView) findViewById(R.id.otherGenderIv);

        countinueTV = (TextView) findViewById(R.id.countinueTV);

        if (sSitemode.equalsIgnoreCase("Demo")) {
            countinueTV.setText(getResources().getString(R.string.mainpage_signup_textview_lable));
        }

        titleTV = (TextView) findViewById(R.id.titleTV);
        subTitleTV = (TextView) findViewById(R.id.subTitleTV);

        String sourceString = "<b>" + getResources().getString(R.string.registerpagefirst_activity_title_txt)
                + " <font color=" + getResources().getColor(R.color.appmain_color) + ">" + getResources().getString(R.string.app_name_shotform) + "</font>" + "</b> ";
        titleTV.setText(Html.fromHtml(sourceString));

//        dob_LL = (LinearLayout) findViewById(R.id.dob_LL);
//        name_LL = (LinearLayout) findViewById(R.id.name_LL);
        email_LL = (LinearLayout) findViewById(R.id.email_LL);
        dob_LL = (LinearLayout) findViewById(R.id.dob_LL);
        address_LL = (LinearLayout) findViewById(R.id.address_LL);

        dob_ET = (EditText) findViewById(R.id.dob_ET);
        address_ET = (EditText) findViewById(R.id.address_ET);
        radius_ET = (EditText) findViewById(R.id.radius_ET);
        radius_invalid_TV = (TextView) findViewById(R.id.radius_invalid_TV);
        myGenderSpnr = (MaterialSpinner) findViewById(R.id.myGenderSpnr);
        myGenderSpnr.setBackgroundResource(R.drawable.spinner_background_gray);
        gender_ET = (EditText) findViewById(R.id.gender_ET);

        dob_TV = (TextView) findViewById(R.id.dob_TV);
        country_code_TV = (TextView) findViewById(R.id.country_code_TV);
        email_invalid_TV = (TextView) findViewById(R.id.email_invalid_TV);
        invalid_password_TV = (TextView) findViewById(R.id.invalid_password_TV);
        no_result_found_TV = (TextView) findViewById(R.id.no_result_found_TV);
        Terms_TV = (TextView) findViewById(R.id.Terms_TV);
        enable_eye_IV = (ImageView) findViewById(R.id.enable_eye_IV);
        myTermsCondTXT = (TextView) findViewById(R.id.registerpage_first_layout_terms_cond_TXT);
        myPrivacyPolicyTXT = (TextView) findViewById(R.id.registerpage_first_layout_policy_TXT);
        txt_terms_conditions = (TextView) findViewById(R.id.txt_terms_conditions);
        firstname_ET = (EditText) findViewById(R.id.firstname_ET);
        lastname_ET = (EditText) findViewById(R.id.lastname_ET);
        firstname_LL = (LinearLayout) findViewById(R.id.firstname_LL);
        lastname_LL = (LinearLayout) findViewById(R.id.lastname_LL);

        dob_invalid_TV = (TextView) findViewById(R.id.dob_invalid_TV);
        gender_invalid_TV = (TextView) findViewById(R.id.gender_invalid_TV);
        address_invalid_TV = (TextView) findViewById(R.id.address_invalid_TV);

        add_icon_IM = (ImageView) findViewById(R.id.add_icon_IM);

        round_IV = (CircularImageView) findViewById(R.id.round_IV);
        firstname_ET.setFilters(new InputFilter[] { filter });
        lastname_ET.setFilters(new InputFilter[] { filter });
        firstname_ET.addTextChangedListener(EditTextWatcher);
        lastname_ET.addTextChangedListener(EditTextWatcher);
        email_ET.addTextChangedListener(EditTextWatcher);
        dob_ET.addTextChangedListener(EditTextWatcher);
        address_ET.addTextChangedListener(EditTextWatcher);
        radius_ET.addTextChangedListener(EditTextWatcher);

//        register_phoneNumber_edittext.addTextChangedListener(EditTextWatcher);
        register_phone_listview = (ListView) findViewById(R.id.register_phone_listview);
        cb_terms_conditions = (CheckBox) findViewById(R.id.cb_terms_conditions);

        register_drawer = (RelativeLayout) findViewById(R.id.register_drawer);
        register_drawer_layout = (DrawerLayout) findViewById(R.id.register_drawer_layout);
        register_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, register_drawer_layout, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Drawer_Opened = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Drawer_Opened = false;
            }
        };

        register_drawer_layout.setDrawerListener(actionBarDrawerToggle);
        register_drawer_layout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        cb_terms_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CompoundButton) view).isChecked()) {
                    cb_terms_conditions.setChecked(true);
                    rl_terms_conditions.setVisibility(View.GONE);
                } else {
                    cb_terms_conditions.setChecked(false);
                    rl_terms_conditions.setVisibility(View.VISIBLE);
                }
            }
        });
        appDirectoryName = getString(R.string.app_name);
        imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), appDirectoryName);
        if (!imageRoot.exists()) {
            imageRoot.mkdir();
        } else if (!imageRoot.isDirectory()) {
            imageRoot.delete();
            imageRoot.mkdir();
        }
        String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        captured_image = new File(imageRoot,
                name + ".jpg");

        gender_ET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QuickAction quickAction = new QuickAction(RegisterPageFirst.this, QuickAction.VERTICAL);

//                ActionItem selectGenderAI = new ActionItem(2, "Select Gender");
                ActionItem maleAL = new ActionItem(2, "Male");
                ActionItem femaleAI = new ActionItem(2, "Female");
                ActionItem ratherNotSayAI = new ActionItem(2, "Rather not say");

//                quickAction.addActionItem(selectGenderAI);
                quickAction.addActionItem(maleAL);
                quickAction.addActionItem(femaleAI);
                quickAction.addActionItem(ratherNotSayAI);
                quickAction.show(v);

                quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(ActionItem item) {
                        gender_ET.setText(item.getTitle());
                    }
                });
            }
        });

        maleGenderIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strGender = getResources().getString(R.string.male_txt);
                maleGenderIv.setBackgroundResource(R.drawable.circle_radio_icon);
                femaleGenderIv.setBackgroundResource(R.drawable.circle_outline_icon);
                otherGenderIv.setBackgroundResource(R.drawable.circle_outline_icon);
                gender_missing_RL.setVisibility(View.INVISIBLE);
            }
        });
        femaleGenderIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strGender = getResources().getString(R.string.female_txt);
                maleGenderIv.setBackgroundResource(R.drawable.circle_outline_icon);
                otherGenderIv.setBackgroundResource(R.drawable.circle_outline_icon);
                femaleGenderIv.setBackgroundResource(R.drawable.circle_radio_icon);
                gender_missing_RL.setVisibility(View.INVISIBLE);
            }
        });
        otherGenderIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strGender = getResources().getString(R.string.rather_not_say);
                otherGenderIv.setBackgroundResource(R.drawable.circle_radio_icon);
                maleGenderIv.setBackgroundResource(R.drawable.circle_outline_icon);
                femaleGenderIv.setBackgroundResource(R.drawable.circle_outline_icon);
                gender_missing_RL.setVisibility(View.INVISIBLE);
            }
        });

        address_ET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search_intent = new Intent(RegisterPageFirst.this, Map_Location_Search.class);
                startActivityForResult(search_intent, placeSearch_request_code);
            }
        });

        round_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission() || !checkWriteExternalStoragePermission()) {
                        requestPermission();
                    } else {
                        chooseimage();
                    }
                } else {
                    chooseimage();
                }
            }
        });

        myGenderArr = new ArrayList<String>();

        myGenderArr.add("Select Gender");
        myGenderArr.add("Male");
        myGenderArr.add("Female");
        myGenderArr.add("Rather not say");
        myGenderSpnr.setItems(myGenderArr);
        Selected_Gender = myGenderArr.get(0);
        str_gender = String.valueOf(myGenderArr);
        myGenderSpnr.setSelectedIndex(0);

        myGenderSpnr.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

                Selected_Gender = myGenderArr.get(position);
                if (Selected_Gender.equalsIgnoreCase("Select Gender")) {
                    gender_missing_RL.setVisibility(View.VISIBLE);
                } else {
                    gender_missing_RL.setVisibility(View.GONE);
                }

            }
        });

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);


        if (cd.isConnectingToInternet()) {
            QuestionAnswerRequest(ServiceConstant.QUESTION_ANSWER_URL);
        }

    }

    private void clickListners() {
//        male_radio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                male_RL.setBackground(getResources().getDrawable(R.drawable.register_linearselected_bg));
//                female_RL.setBackground(getResources().getDrawable(R.drawable.register_linearbg));
//                female_radio.setChecked(false);
//                male_radio.setChecked(true);
//                gender_missing_RL.setVisibility(View.GONE);
//            }
//        });
//        female_radio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                female_RL.setBackground(getResources().getDrawable(R.drawable.register_linearselected_bg));
//                male_RL.setBackground(getResources().getDrawable(R.drawable.register_linearbg));
//                male_radio.setChecked(false);
//                female_radio.setChecked(true);
//                gender_missing_RL.setVisibility(View.GONE);
//            }
//        });

//        enable_eye_RL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (str_text.equals("Show")) {
//                    // hide password
//                    Password_ET.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    Password_ET.setSelection(Password_ET.getText().length());
//                    enable_eye_IV.setBackground(getResources().getDrawable(R.drawable.password_disable_eye));
//                    str_text = "Hide";
//                } else if (str_text.equals("Hide")) {
//                    // show password
//                    Password_ET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    Password_ET.setSelection(Password_ET.getText().length());
//                    enable_eye_IV.setBackground(getResources().getDrawable(R.drawable.register_eye_enable));
//                    str_text = "Show";
//                }
//            }
//        });

        continue_button_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                String email = email_ET.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (firstname_ET.getText().toString().trim().equalsIgnoreCase("")) {
                    firstname_missing_RL.setVisibility(View.VISIBLE);
                    firstname_missing_TV.setText(getResources().getString(R.string.registerpagefirst_activity_alert_first_name));
                    firstname_ET.requestFocus();
                }
//                else if (firstname_ET.getText().toString().length() < 4) {
//                    firstname_missing_RL.setVisibility(View.VISIBLE);
//                    firstname_missing_TV.setText(getResources().getString(R.string.editbiovalidate_firstname_label_alert));
//                    firstname_ET.requestFocus();
//                }
                else if (lastname_ET.getText().toString().trim().equalsIgnoreCase("")) {
                    lastname_missing_RL.setVisibility(View.VISIBLE);
                    lastname_ET.requestFocus();
                } else if (email_ET.getText().toString().trim().equalsIgnoreCase("")) {
                    email_invalid_TV.setText(getResources().getString(R.string.enter_emailid_txt));
                    email_missing_RL.setVisibility(View.VISIBLE);
                    mgr.hideSoftInputFromWindow(email_ET.getWindowToken(), 0);
                    email_ET.requestFocus();
                } else if (!email.matches(emailPattern)) {
                    email_missing_RL.setVisibility(View.VISIBLE);
                    email_invalid_TV.setText(getResources().getString(R.string.enter_valid_mail));
                    email_ET.requestFocus();
                    mgr.hideSoftInputFromWindow(email_ET.getWindowToken(), 0);
                } else if (dob_ET.getText().toString().length() == 0) {
                    dob_invalid_TV.setText(getResources().getString(R.string.select_dob_txtt));
                    dob_missing_RL.setVisibility(View.VISIBLE);
                    dob_ET.requestFocus();
                } else if (strGender.equalsIgnoreCase("")) {
//                    gender_invalid_TV.setText(getResources().getString(R.string.registerpagefirst_activity_alert_plz_select_gender));
                    gender_missing_RL.setVisibility(View.VISIBLE);
//                    gender_ET.requestFocus();
                }
//                else if (gender_ET.getText().toString().equalsIgnoreCase("Select Gender")) {
////                    gender_invalid_TV.setText(getResources().getString(R.string.registerpagefirst_activity_alert_plz_select_gender));
//                    gender_missing_RL.setVisibility(View.VISIBLE);
//                }
                else if (address_ET.getText().toString().length() == 0) {
                    address_invalid_TV.setText(getResources().getString(R.string.work_location_empty_txt));
                    address_missing_RL.setVisibility(View.VISIBLE);
                    address_ET.requestFocus();
                }
//                else if (radius_ET.getText().toString().length() == 0) {
//                    radius_invalid_TV.setText(getResources().getString(R.string.registerpagefirst_activity_alert_radius_empty));
//                    radius_missing_RL.setVisibility(View.VISIBLE);
//                    radius_ET.requestFocus();
//                } else if (Integer.parseInt(radius_ET.getText().toString()) == 0) {
//                    radius_invalid_TV.setText(getResources().getString(R.string.radius_validation));
//                    radius_missing_RL.setVisibility(View.VISIBLE);
//                    radius_ET.requestFocus();
//                }
                else if (!cb_terms_conditions.isChecked()) {
                    mgr.hideSoftInputFromWindow(cb_terms_conditions.getWindowToken(), 0);
                    rl_terms_conditions.setVisibility(View.VISIBLE);
                    txt_terms_conditions.setText(getResources().getString(R.string.agree_terms_conditions));
                } else {
                    if (cd.isConnectingToInternet()) {
                        checkTaskerRequest();
                    } else {
                        Alert(getResources().getString(R.string.sorry), getResources().getString(R.string.no_inetnet_label));
                    }
                }
            }
        });

//        dob_LL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDatePickerDialog();
//            }
//        });

        countrycode_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_drawer_layout.openDrawer(register_drawer);
                getAllCountries();
                CountryCodeAdapter = new RegisterListAdapter(RegisterPageFirst.this, selectedCountriesList);
                register_phone_listview.setAdapter(CountryCodeAdapter);
                CountryCodeAdapter.notifyDataSetChanged();
                //  setListViewHeightBasedOnChildren(register_phone_listview);
            }
        });
        dob_ET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        register_header_back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        myTermsCondTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aIntent = new Intent(RegisterPageFirst.this, MyNewWebView.class);
                aIntent.putExtra("title", getResources().getString(R.string.terms_cond_title));
                aIntent.putExtra("url", ServiceConstant.Terms_and_conditions);
                startActivity(aIntent);
            }
        });

        myPrivacyPolicyTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aIntent = new Intent(RegisterPageFirst.this, MyNewWebView.class);
                aIntent.putExtra("title", getResources().getString(R.string.privacy_policy_ttile_txt));
                aIntent.putExtra("url", ServiceConstant.Privacy_Polocy);
                startActivity(aIntent);
            }
        });

        TvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aIntent = new Intent(RegisterPageFirst.this, LoginPage.class);
                startActivity(aIntent);
            }
        });

    }

    private void mobileValidation() {

        String countryCode = country_code_TV.getText().toString().trim();
        String phoneNumber = mobile_ET.getText().toString().trim();
        if (countryCode.length() > 0 && phoneNumber.length() > 0) {
            if (isValidPhoneNumber(phoneNumber)) {
                boolean status = validateUsing_libphonenumber(countryCode, phoneNumber);
                if (status) {
//                    Toast.makeText(this, "Valid Phone Number (libphonenumber)", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(this, "Valid Phone Number (libphonenumber)", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Country Code and Phone Number is required", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPassword(String pass) {
        if (!pass.matches("(.*[a-z].*)")) {
            return false;
        } else if (!pass.matches("(.*[A-Z].*)")) {
            return false;
        } else if (!pass.matches("(.*[0-9].*)")) {
            return false;
        } else {
            return true;
        }

    }

    private void checkTaskerRequest() {

        myLoadingDialog = new PkLoadingDialog(RegisterPageFirst.this);
        myLoadingDialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("firstname", firstname_ET.getText().toString().trim());
        jsonParams.put("lastname", lastname_ET.getText().toString().trim());
        jsonParams.put("email", email_ET.getText().toString().trim());
        jsonParams.put("dob", dobDate);
        jsonParams.put("gender", strGender);
//        jsonParams.put("address[formatted_address]", address_ET.getText().toString());
        jsonParams.put("phone[code]", country_code);
        jsonParams.put("phone[number]", phone_number);
//        jsonParams.put("address[line1]", address_ET.getText().toString());
//        jsonParams.put("address[line2]", address_ET.getText().toString());
//        jsonParams.put("address[country]", selected_country);
//        jsonParams.put("address[zipcode]", selected_zipcode);
//        jsonParams.put("address[state]", selected_state);
//        jsonParams.put("address[city]", address_ET.getText().toString());
//        jsonParams.put("lat", SselectedLatitude);
//        jsonParams.put("lng", SselectedLongitude);
        jsonParams.put("availability_address", address_ET.getText().toString());
        jsonParams.put("location_lat", SselectedLatitude);
        jsonParams.put("location_lng", SselectedLongitude);
        jsonParams.put("radius", radius);

        getExperianceArrayList = new ArrayList<String>();

        if(expAdapter!=null){

            getExperianceArrayList = expAdapter.getAnswer();
            if (getExperianceArrayList.size() > 0) {
                for (int i = 0; i < getExperianceArrayList.size(); i++) {
                    jsonParams.put("profile_details[" + i + "][question]", ExperianceList.get(i).getId());
                    jsonParams.put("profile_details[" + i + "][answer]", getExperianceArrayList.get(i));
                }
            }

        }

        System.out.println("checkTaskerRequest jsonParams" + jsonParams);

        myRequest = new ServiceRequest(RegisterPageFirst.this);
        myRequest.makeServiceRequest(ServiceConstant.Register_Form1, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                myLoadingDialog.dismiss();
                System.out.println("checkTaskerRequest response" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                        TaskerInfoPojo aTaskerInfo = new TaskerInfoPojo();
                        aTaskerInfo.setTaskerEmail(jsonObject1.getString("email"));
                        aTaskerInfo.setTaskerGender(jsonObject1.getString("gender"));
                        aTaskerInfo.setTaskerDOB(jsonObject1.getString("dob"));
                        sessionManager.putTaskerDetails(aTaskerInfo);

                        sessionManagerRegistration.setPersonalDetails(response);
                        if (jsonObject.has("document_upload_status")) {
                            sessionManagerRegistration.setDocumentStatus(jsonObject.getString("document_upload_status"));
                        }

                        if (sSitemode.equalsIgnoreCase("Demo")) {
                            RegistrationRequest(ServiceConstant.REGISTER_FINAL_LEVEL_URL);
                        } else {
                            Intent RegisterPageFirst = new Intent(RegisterPageFirst.this, RegiterPageAvailablilityActivity.class);
                            RegisterPageFirst.putExtra("response", response);
                            startActivity(RegisterPageFirst);
                        }


                    } else {
                        Alert(getResources().getString(R.string.sorry), jsonObject.getString("response"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    myLoadingDialog.dismiss();
                }
            }

            @Override
            public void onErrorListener() {
                myLoadingDialog.dismiss();
            }
        });
    }

    private void RegistrationRequest(String registerFinalLevelUrl) {

        final Dialog dialog = new Dialog(RegisterPageFirst.this);
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
            JSONObject personalDetailsJSON = new JSONObject(sessionManagerRegistration.getPersonalDetails());
            personalDetailsJSON.remove("working_days");
            personalDetailsJSON.remove("status");
            JSONObject response = personalDetailsJSON.getJSONObject("response");
            response.remove("message");

            Log.d("personalDetailsJSON", "response----------" + response);
            jsonParams = response;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray jsonArrayDays = new JSONArray();

//            jsonParams.put("working_days", jsonArrayDays);

            Gson gson2 = new Gson();
            String documentStringPojo = sessionManagerRegistration.getDocumentDays();
            if (documentStringPojo.length() > 0) {
                DocumentUploadPojo mPojo = gson2.fromJson(documentStringPojo, DocumentUploadPojo.class);
                System.out.println("available----------" + gson2.fromJson(documentStringPojo, DocumentUploadPojo.class));
                ArrayList<RegisterLicenceUploadPojo> pojoArrayList = mPojo.getPojoArrayList();
                JSONArray jsonArrayDocument = new JSONArray();
                for (int i = 0; i < pojoArrayList.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", pojoArrayList.get(i).getName());
                    jsonObject.put("file_type", pojoArrayList.get(i).getFile_typle());
                    jsonObject.put("path", pojoArrayList.get(i).getImageWebUrl());
                    jsonArrayDocument.put(jsonObject);
                }
                jsonParams.put("documents", jsonArrayDocument);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        try {
//            jsonParams.put("taskerskills", "");
            jsonParams.put("deviceToken", "");
            SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
            gcmID = pref.getString("regId", null);
            jsonParams.put("gcm_id", gcmID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("RegistrationRequest jsonParams" + jsonParams);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, registerFinalLevelUrl, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("RegistrationRequest response" + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String str_Status, str_Message, str_Response;

                            str_Status = jsonObject.getString("status");

                            if (str_Status.equalsIgnoreCase("1")) {

                                str_Message = jsonObject.getString("response");

                                Intent intent = new Intent(getApplicationContext(), RegistrationSuccessPage.class);
                                intent.putExtra("response", response.toString());
                                startActivity(intent);

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

    //to get date of birth
    private String getAge(int year, int month, int day) {

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        int todaymonth = today.get(java.util.Calendar.MONTH);
        int todayyear = today.get(java.util.Calendar.YEAR);
        int today_date = today.get(java.util.Calendar.DAY_OF_MONTH);
        today.set(todayyear, todaymonth + 1, today_date + 1);
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        System.out.println("DateFormat1 " + today.get(Calendar.YEAR) + "---" + dob.get(Calendar.YEAR));
        System.out.println("DateFormat2 " + today.get(Calendar.DAY_OF_YEAR) + "---" + dob.get(Calendar.DAY_OF_YEAR));

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();
        return ageS;
    }

    private List<Country> getAllCountries() {
        if (allCountriesList == null) {
            try {
                allCountriesList = new ArrayList<Country>();
                String allCountriesCode = readEncodedJsonString(RegisterPageFirst.this);
                JSONArray countrArray = new JSONArray(allCountriesCode);
                for (int i = 0; i < countrArray.length(); i++) {
                    JSONObject jsonObject = countrArray.getJSONObject(i);
                    String countryName = jsonObject.getString("name");
                    String countryDialCode = jsonObject.getString("dial_code");
                    String countryCode = jsonObject.getString("code");
                    Country country = new Country();
                    country.setCode(countryCode);
                    country.setName(countryName);
                    country.setDialCode(countryDialCode);
                    allCountriesList.add(country);
                }

                Collections.sort(allCountriesList, RegisterPageFirst.this);
                selectedCountriesList = new ArrayList<Country>();
                selectedCountriesList.addAll(allCountriesList);
                // Return
                return allCountriesList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public int compare(Country lhs, Country rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }

    @Override
    public void onBackPressed() {

        if (Drawer_Opened) {
            register_drawer_layout.closeDrawer(register_drawer);
        } else {
            super.onBackPressed();
        }
    }

    private void Alert(String Title, String response) {
        final PkDialog mdialog = new PkDialog(RegisterPageFirst.this);
        mdialog.setDialogTitle(Title);
        mdialog.setDialogMessage(response);
        mdialog.setCancelOnTouchOutside(false);
        mdialog.setPositiveButton(
                getResources().getString(R.string.server_ok_lable_header), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        myLoadingDialog.dismiss();
                        mdialog.dismiss();
                    }
                }
        );
        mdialog.show();

    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    private boolean validateUsing_libphonenumber(String countryCode, String phNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
//            phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
            phoneNumber = phoneNumberUtil.parse(mobile_ET.getText().toString(), isoCode);
//            phoneNumber = phoneNumberUtil.parse(mobile_ET.getText().toString(), country_code_TV.getText().toString());
        } catch (NumberParseException e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        if (isValid) {
            String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            Toast.makeText(this, "Phone Number is Valid " + internationalFormat, Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(this, "Phone Number is Invalid " + phoneNumber, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void showDatePickerDialog() {
        // Get open DatePickerDialog button.
        // Create a new OnDateSetListener instance. This listener will be invoked when user click ok button in DatePickerDialog.
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                strBuf = new StringBuffer();
                // strBuf.append("You select date is ");


                if ((month + 1) < 10) {
                    strBuf.append("0" + (month + 1));
                } else {
                    strBuf.append(month + 1);
                }
                strBuf.append("-");

                if (dayOfMonth < 10) {
                    strBuf.append("0" + dayOfMonth);
                } else {
                    strBuf.append(dayOfMonth);
                }
                strBuf.append("-");

                strBuf.append(year);

                String ages = getAge(year, month + 1, dayOfMonth);

//                dob_LL.setBackground(getResources().getDrawable(R.drawable.register_linearbg));
                dobDate = strBuf.toString();

                try {
                    DateFormat inputFormat = new SimpleDateFormat("MM-dd-yyyy");
                    DateFormat outputFormat = new SimpleDateFormat(date_format);
                    String inputDateStr = strBuf.toString();
                    Date date = inputFormat.parse(inputDateStr);
                    formateDate = outputFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                dob_ET.setText(ages + " " + getResources().getString(R.string.years_txt) + " / " + formateDate);
            }
        };

        // Get current year, month and day.
        Calendar now = Calendar.getInstance();
        int year = now.get(java.util.Calendar.YEAR);
        int month = now.get(java.util.Calendar.MONTH);
        int day = now.get(java.util.Calendar.DAY_OF_MONTH);
        // Create the new DatePickerDialog instance.
        DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterPageFirst.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);
        //Theme_Holo_Light_Dialog
        // Set dialog icon and title.
        now.add(Calendar.YEAR, -18);

        datePickerDialog.getDatePicker().setMaxDate(now.getTimeInMillis());
//        datePickerDialog.getDatePicker().setMaxDate(now);
        // Set the Calendar new date as minimum date of date picker
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.setTitle(getResources().getString(R.string.calender_select_date_txt));
        datePickerDialog.show();

    }

    private void QuestionAnswerRequest(String questionAnswerUrl) {
        myLoadingDialog = new PkLoadingDialog(RegisterPageFirst.this);
        myLoadingDialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("tasker", "question");

        System.out.println("QuestionAnswerRequest jsonParams" + jsonParams);

        ExperianceList = new ArrayList<Experiancepojo>();
        ServiceRequest mservicerequest = new ServiceRequest(RegisterPageFirst.this);
        mservicerequest.makeServiceRequest(questionAnswerUrl, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("QuestionAnswerRequest response" + response);

                String Str_Status = "", Str_distanceby = "";
                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_Status = jobject.getString("status");
                    Str_distanceby = jobject.getString("distanceby");
                    radius_ET.setHint(getResources().getString(R.string.radius_txt) + "(" + Str_distanceby + ")");
                    if (Str_Status.equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jobject.getJSONArray("response");
                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject listObject = jsonArray.getJSONObject(i);
                                Experiancepojo pojo = new Experiancepojo();
                                pojo.setQuestion(listObject.getString("question"));
                                pojo.setId(listObject.getString("_id"));
                                ExperianceList.add(pojo);
                            }
                        } else {
                            ExperianceList.clear();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    myLoadingDialog.dismiss();
                }
                if (Str_Status.equalsIgnoreCase("1")) {
                    expAdapter = new ExperianceAdapter(RegisterPageFirst.this, ExperianceList, experiance_listview);
                    experiance_listview.setAdapter(expAdapter);
                    if (ExperianceList.size() > 0) {
                        setListViewHeightBasedOnChildren(experiance_listview);
                        expAdapter.notifyDataSetChanged();
                    }
                }
                myLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                myLoadingDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == placeSearch_request_code && data != null)) {
            SselectedLatitude = data.getStringExtra("Selected_Latitude");
            SselectedLongitude = data.getStringExtra("Selected_Longitude");
            Selected_Location = data.getStringExtra("Selected_Location");


//            selected_zipcode = data.getStringExtra("ZipCode");
//            selected_country = data.getStringExtra("country");
//            selected_city = data.getStringExtra("City");
//            selected_line2 = data.getStringExtra("Location");
//            selected_state = data.getStringExtra("State");

//            address_TV.setText(Selected_Location);
//            street_ET.setText(selected_line2);
//            town_ET.setText(selected_line2);
//            state_ET.setText(selected_state);
//            country_ET.setText(selected_country);
//            zipcode_ET.setText(selected_zipcode);

//            address_LL.setBackground(getResources().getDrawable(R.drawable.register_linearbg));
//            if (!Selected_Location.equals("")) {
            address_ET.setText(Selected_Location);
//            } else {
//                address_ET.setText(Selected_Location);
//            }

        } else if (requestCode == CAMERA_REQUEST_2) {
            try {

                rotate = true;
                imagePath = captured_image.getAbsolutePath();
                mImageCaptureUri = Uri.fromFile(new File(imagePath));
                outputUri = mImageCaptureUri;
                System.out.println("Image Captured Uri = " + mImageCaptureUri);

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                System.out.println("Image Captured Uri bitmap = " + bitmap.toString());

                UCrop.Options options = new UCrop.Options();
                options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                options.setMaxBitmapSize(800);

                UCrop.of(mImageCaptureUri, outputUri)
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(8000, 8000)
                        .withOptions(options)
                        .start(RegisterPageFirst.this);

//                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE) {
            rotate = false;
            if (data != null) {
                selectedImage = data.getData();
            }

            try {

                if (selectedImage != null) {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    int wid = bitmap.getWidth();
                    int height = bitmap.getHeight();

                    if (!imageRoot.exists()) {
                        imageRoot.mkdir();
                    } else if (!imageRoot.isDirectory()) {
                        imageRoot.delete();
                        imageRoot.mkdir();
                    }


                    final File image = new File(imageRoot, System.currentTimeMillis() + ".jpg");
                    outputUri = Uri.fromFile(image);

                    UCrop.Options options = new UCrop.Options();
                    options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                    options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                    options.setMaxBitmapSize(800);

                    UCrop.of(selectedImage, outputUri)
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(8000, 8000)
                            .withOptions(options)
                            .start(RegisterPageFirst.this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == UCrop.REQUEST_CROP && data != null) {

            final Uri resultUri = UCrop.getOutput(data);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                if (bitmap.getWidth() == 500 && bitmap.getHeight() == 500) {
                    finalPic = bitmap;
                } else {

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();

                    float bitmapRatio = (float) width / (float) height;
                    if (bitmapRatio > 0) {
                        width = 500;
                        height = (int) (width / bitmapRatio);

                    } else {
                        height = 500;
                        width = (int) (height * bitmapRatio);
                    }

                    finalPic = Bitmap.createScaledBitmap(bitmap, width, height, true);
                    encode = encodeToBase64(finalPic, Bitmap.CompressFormat.JPEG, 100);
                }

                if (finalPic == null) {
                    Log.d("Bitmap", "null");
                } else {
                    Log.d("Bitmap", "not null");

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    finalPic.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();

                    cd = new ConnectionDetector(RegisterPageFirst.this);
                    if (cd.isConnectingToInternet()) {
                        bitMapThumbnail = finalPic;
                        uploadTaskerImage(ServiceConstant.REGISTER_IMAGE_UPLOAD_URL);
                    } else {
                        Alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void chooseimage() {
        photo_dialog = new Dialog(RegisterPageFirst.this);
        photo_dialog.getWindow();
        photo_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        photo_dialog.setContentView(R.layout.register_image_upload_dialog);
        photo_dialog.setCanceledOnTouchOutside(true);
        photo_dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_photo_Picker;
        photo_dialog.show();
        photo_dialog.getWindow().setGravity(Gravity.CENTER);

        ImageView img_popup_close = (ImageView) photo_dialog.findViewById(R.id.img_popup_close);
        TextView camera = (TextView) photo_dialog.findViewById(R.id.take_photo);
        TextView gallery = (TextView) photo_dialog.findViewById(R.id.pick_gallery);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
                photo_dialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                photo_dialog.dismiss();
            }
        });

        img_popup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo_dialog.dismiss();
            }
        });
    }

    private void takePicture() {
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", captured_image);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
        startActivityForResult(intent, CAMERA_REQUEST_2);
    }

    private void openGallery() {
        Intent ia = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(ia, PICK_IMAGE);
    }

    private boolean checkAccessFineLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkAccessCoarseLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkWriteExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseimage();
                } else {
                    finish();
                }
                break;
        }
    }

    private void uploadTaskerImage(String editProfileimageUrl) {

        myLoadingDialog = new PkLoadingDialog(RegisterPageFirst.this);
        myLoadingDialog.show();


        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, editProfileimageUrl, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {


                String resultResponse = new String(response.data);
                String sStatus = "", sResponse = "", SUser_image = "", Smsg = "";

                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);
                    sStatus = jsonObject.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {
                        JSONObject responseObject = jsonObject.getJSONObject("response");
                        SUser_image = responseObject.getString("image");
                        Smsg = responseObject.getString("message");

                        sessionManager.setRegisterTaskerImage(SUser_image);
                        Image_Uploaded = true;
                        add_icon_IM.setVisibility(View.VISIBLE);

                        round_IV.setImageBitmap(bitMapThumbnail);
                        sessionManager.setUserImageUpdate(SUser_image);

                        Alert(getResources().getString(R.string.action_loadings_sucess), getResources().getString(R.string.edit_profile_success_label));
                    } else {
                        sResponse = jsonObject.getString("response");
                        Alert(getResources().getString(R.string.server_lable_header), sResponse);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                myLoadingDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                myLoadingDialog.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type", "tasker");
                //params.put("user", provider_id);
                params.put("device", gcmID);
                params.put("devicetype", "android");
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // params.put("provider_id", provider_id);
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("image", new DataPart(getResources().getString(R.string.app_name) + ".jpg", byteArray));
                return params;
            }
        };

        //to avoid repeat request Multiple Time
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        multipartRequest.setRetryPolicy(retryPolicy);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        multipartRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(multipartRequest);

    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    private TextWatcher EditTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (!firstname_ET.getText().toString().trim().equalsIgnoreCase("")) {
                firstname_missing_RL.setVisibility(View.INVISIBLE);
            }

            if (!lastname_ET.getText().toString().trim().equalsIgnoreCase("")) {
                lastname_missing_RL.setVisibility(View.INVISIBLE);
            }

            if (!email_ET.getText().toString().trim().equalsIgnoreCase("")) {
                email_missing_RL.setVisibility(View.INVISIBLE);
            }

            if (isValidEmail(email_ET.getText().toString())) {
                email_missing_RL.setVisibility(View.INVISIBLE);
            }

            if (dob_ET.getText().toString().length() != 0) {
                dob_missing_RL.setVisibility(View.INVISIBLE);
            }

            if (gender_ET.getText().toString().length() != 0) {
                gender_missing_RL.setVisibility(View.INVISIBLE);
            }

            if (address_ET.getText().toString().length() != 0) {
                address_missing_RL.setVisibility(View.INVISIBLE);
            }

            if (!radius_ET.getText().toString().trim().equalsIgnoreCase("")) {
                radius_missing_RL.setVisibility(View.INVISIBLE);
                if (Integer.parseInt(radius_ET.getText().toString()) != 0) {
                    radius_missing_RL.setVisibility(View.INVISIBLE);
                }
            }


        }
    };

    private static String readEncodedJsonString(Context context) throws java.io.IOException {
        String base64 = context.getResources().getString(com.countrycodepicker.R.string.countries_code);
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return new String(data, "UTF-8");
    }

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

    // --------Code to Check Email Validation--------
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


}