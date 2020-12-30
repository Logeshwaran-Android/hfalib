package com.hoperlady.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by user88 on 12/9/2015.
 */
public class SessionManagerDB {


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
    private static final String API_KEY = "api_key";


    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManagerDB(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setAPIKey(String TaskerVerification1) {
        editor.putString(API_KEY, TaskerVerification1);
        editor.commit();

    }

    public String getAPIKey() {
        return pref.getString(API_KEY, "");
    }

}
