package com.hoperlady.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hoperlady.Pojo.Availability_selecting_pojo;
import com.hoperlady.Pojo.EditAvailabileArrayPojo;
import com.hoperlady.Pojo.Experiancepojo;
import com.hoperlady.Pojo.TaskerInfoPojo;
import com.hoperlady.app.NewLoginHomePageActivity;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user88 on 12/9/2015.
 */
public class SessionManager {


    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "PlumbalPartner";


    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public static final String KEY_PROVIDERID = "providerid";

    public static final String KEY_GCM_ID = "gcmId";

    public static final String KEY_PROVIDERNAME = "providerrname";

    public static final String KEY_USERIMAGE = "userimage";

    public static final String KEY = "key";

    public static final String KEY_SOCKEYID = "socky_id";

    public static final String KEY_STATUS = "status";

    public static final String KEY_Appinfo_email = "Appinfo_email";

    public static final String KEY_Chat_userid = "chatuserid";
    public static final String KEY_CURRENCY_CODE = "currencyCode";
    public static final String KEY_Task_id = "taskid";
    public static final String NAVIGATION_OPEN = "open";
    public static final String TASKERDETAILS = "TaskerDetail";
    public static final String TaskerRegisterImage = "image";
    public static final String TaskerVerification = "TaskerVerification";

    public static final String KEY_AVAILABIL_DAYS = "availabledays";

    public static final String KEY_AVAILABIL_Rating = "avg_review";

    public static final String KEY_PROVIDER_DOCUMENT = "provider_document";

    public static final String KEY_user_id = "user_id";
    public static final String KEY_user_log = "logtitude";
    public static final String KEY_user_lat = "latitude";


    public static final String KEY_user_currentlong = "logtitude";
    public static final String KEY_user_currentlat = "latitude";


    private static final Type LIST_TYPE = new TypeToken<List<Availability_selecting_pojo>>() {
    }.getType();
    private static final Type Exp_List_Type = new TypeToken<List<Experiancepojo>>() {

    }.getType();

    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String email, String providerid, String providername, String userimage, String gcmId) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PROVIDERID, providerid);
        editor.putString(KEY_PROVIDERNAME, providername);
        editor.putString(KEY_USERIMAGE, userimage);
        editor.putString(KEY_GCM_ID, gcmId);

        // commit changes
        editor.commit();
    }

    public void createWalletSession(String currencyCode) {
        editor.putString(KEY_CURRENCY_CODE, currencyCode);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, ""));
        user.put(KEY_PROVIDERID, pref.getString(KEY_PROVIDERID, ""));
        user.put(KEY_PROVIDERNAME, pref.getString(KEY_PROVIDERNAME, ""));
        user.put(KEY_USERIMAGE, pref.getString(KEY_USERIMAGE, ""));
        user.put(KEY_SOCKEYID, pref.getString(KEY_SOCKEYID, ""));
        user.put(KEY, pref.getString(KEY, ""));
        user.put(KEY_GCM_ID, pref.getString(KEY_GCM_ID, ""));
        user.put(KEY_STATUS, pref.getString(KEY_STATUS, ""));
        user.put(KEY_Appinfo_email, pref.getString(KEY_Appinfo_email, ""));
        user.put(KEY_Chat_userid, pref.getString(KEY_Chat_userid, ""));
        user.put(KEY_Task_id, pref.getString(KEY_Task_id, ""));
        user.put(NAVIGATION_OPEN, pref.getString(NAVIGATION_OPEN, ""));
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, NewLoginHomePageActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);

    }

    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


    //------UserImage update code-----
    public void setUserImageUpdate(String image) {
        editor.putString(KEY_USERIMAGE, image);
        editor.commit();
    }

    public void Taskerstatus(String status) {
        editor.putString(KEY_STATUS, status);
        editor.commit();


    }

    public void Setemailappinfo(String email) {
        editor.putString(KEY_Appinfo_email, email);
        editor.commit();


    }

    public void setchatuserid(String id) {
        editor.putString(KEY_Chat_userid, id);
        editor.commit();


    }
    public void setrating(String rating) {
        editor.putString(KEY_AVAILABIL_Rating, rating);
        editor.commit();
    }

    public String getRating() {
        return pref.getString(KEY_AVAILABIL_Rating, "");
    }

    public HashMap<String, String> getWalletDetails() {
        HashMap<String, String> wallet = new HashMap<String, String>();
        wallet.put(KEY_CURRENCY_CODE, pref.getString(KEY_CURRENCY_CODE, ""));
        return wallet;
    }

    public void settaskid(String id) {
        editor.putString(KEY_Task_id, id);
        editor.commit();

    }

    public void pageopen(String status) {
        editor.putString(NAVIGATION_OPEN, status);
        editor.commit();
    }


    public void putTaskerDetails(TaskerInfoPojo aTaskerInfo) {
        String aUserDetailJson = null;

        Gson aGson = new Gson();

        aUserDetailJson = aGson.toJson(aTaskerInfo);

        editor.putString(TASKERDETAILS, aUserDetailJson);

        editor.commit();

    }

    /**
     * Get Taskerdetails
     *
     * @return
     */


    public TaskerInfoPojo getTaskerDetails() {

        TaskerInfoPojo aTaskerInfo = null;

        String aUserInfoJSON = pref.getString(TASKERDETAILS, null);

        if (aUserInfoJSON != null) {

            Gson aGson = new Gson();

            aTaskerInfo = aGson.fromJson(aUserInfoJSON, TaskerInfoPojo.class);

        }
        return aTaskerInfo;
    }


    public void setAvailList(List<Availability_selecting_pojo> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        editor.putString("AvailList", json);
        editor.commit();
    }

    public List<Availability_selecting_pojo> getAvailList() {
        Gson gson = new Gson();
        List<Availability_selecting_pojo> companyList;

        String string = pref.getString("AvailList", null);
        Type type = new TypeToken<List<Availability_selecting_pojo>>() {
        }.getType();
        companyList = gson.fromJson(string, type);
        return companyList;
    }

    public void putAvailDays(List<Availability_selecting_pojo> data) {
        Gson gson = new Gson();
        String strObject = gson.toJson(data);
        editor.putString("AvailList", strObject);
        editor.commit();
    }

    public List<Availability_selecting_pojo> getAvailDays() {

        //Gson gson = new Gson();
        List<Availability_selecting_pojo> availList;
        availList = new Gson().fromJson(pref.getString("AvailList", null), LIST_TYPE);
        return availList;


    }

    public void putExperianceList(List<Experiancepojo> experianceList) {
        Gson gson = new Gson();
        String object = gson.toJson(experianceList);
        editor.putString("explist", object);
        editor.commit();
    }

    public String getEditAvailablityDays() {
        return pref.getString(KEY_AVAILABIL_DAYS, "");
    }

    public void setEditAvailablityDays(EditAvailabileArrayPojo availabileArrayPojo) {
        Gson gson = new Gson();
        String json = gson.toJson(availabileArrayPojo);
        editor.putString(KEY_AVAILABIL_DAYS, json);
        editor.commit();
    }

    public List<Experiancepojo> getExperianceList() {
        List<Experiancepojo> experiancepojos;
        experiancepojos = new Gson().fromJson(pref.getString("explist", null), Exp_List_Type);
        return experiancepojos;
    }

    public void setRegisterTaskerImage(String TaskerRegisterImageurl) {
        editor.putString(TaskerRegisterImage, TaskerRegisterImageurl);
        editor.commit();

    }

    public String RegisterImageurl() {
        return pref.getString(TaskerRegisterImage, null);
    }

    public String getTaskerVerification() {
        return pref.getString(TaskerVerification, "");
    }

    public void setTaskerVerification(String TaskerVerification1) {
        editor.putString(TaskerVerification, TaskerVerification1);
        editor.commit();

    }

    public void setProviderName(String providername) {
        editor.putString(KEY_PROVIDERNAME, providername);
        editor.commit();
    }

    public String getProviderDocumentStatus(){
        return pref.getString(KEY_PROVIDER_DOCUMENT,"");
    }

    public void setProviderDocumentStatus(String providername) {
        editor.putString(KEY_PROVIDER_DOCUMENT, providername);
        editor.commit();
    }


    public void setLatitudecuurent(String latitude) {
        editor.putString(KEY_user_currentlat, latitude);
        editor.commit();
    }

    public String getLatitudecuurent() {
        return pref.getString(KEY_user_currentlat, "");
    }

    public void setLongcurrent(String log) {
        editor.putString(KEY_user_currentlong, log);
        editor.commit();

    }

    public String getLongcurrent (){
        return pref.getString(KEY_user_currentlong, "");
    }


    public void setLatitudeUserId(String latitude) {
        editor.putString(KEY_user_lat, latitude);
        editor.commit();
    }

    public String getLatitudeUserId() {
        return pref.getString(KEY_user_lat, "");
    }

    public void setLongUserId(String log) {
        editor.putString(KEY_user_log, log);
        editor.commit();

    }

    public String getLongUserId() {
        return pref.getString(KEY_user_log, "");
    }

    public void setTaskerUserId(String status) {
        editor.putString(KEY_user_id, status);
        editor.commit();

    }

    public String getTaskerUserId() {
        return pref.getString(KEY_user_id, "");
    }


}
