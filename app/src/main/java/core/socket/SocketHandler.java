package core.socket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Messenger;
import android.util.Log;

import com.hoperlady.Utils.SessionManager;
import com.hoperlady.app.notificationdb;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 */
public class SocketHandler {
    private static final String TAG = "HII";
    private Context context;
    private SocketManager manager;
    private SocketParser parser;
    private SessionManager sessionManager;
    public static SocketHandler instance;
    private String status = "";
    private SQLiteDatabase dataBase;
    notificationdb mHelper;
    private ArrayList<Messenger> listenerMessenger;

    private SocketHandler(Context context) {
        this.context = context;
        this.manager = new SocketManager(context, callBack);
        this.sessionManager = new SessionManager(context);
        this.parser = new SocketParser(context, sessionManager);
        this.listenerMessenger = new ArrayList<Messenger>();
    }

    public static SocketHandler getInstance(Context context) {
        if (instance == null) {
            instance = new SocketHandler(context);
        }
        return instance;
    }

    public void addChatListener(Messenger messenger) {
        listenerMessenger.add(messenger);
    }

    public SocketManager getSocketManager() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String provider_id = user.get(SessionManager.KEY_PROVIDERID);
        System.out.println("handler provider_id-------------" + provider_id);
        manager.setTaskId(provider_id);
        return manager;
    }

    public SocketManager.SocketCallBack callBack = new SocketManager.SocketCallBack() {
        boolean isChat = false;

        @Override
        public void onSuccessListener(Object response) {
            if (response instanceof JSONObject) {
                if (!sessionManager.isLoggedIn())
                    return;
                JSONObject jsonObject = (JSONObject) response;

                System.out.println("socktresponse--------" + jsonObject);


                try {

                    String username = jsonObject.getString("username");
                    String message = jsonObject.getString("message");
                    android.os.Message chatMessage = android.os.Message.obtain();
                    chatMessage.obj = jsonObject.toString();
                    isChat = true;
                    for (Messenger m :
                            listenerMessenger) {
                        if (m != null)
                            m.send(chatMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isChat = false;
                }
                if (isChat) {


                    sessionManager = new SessionManager(context);
                    HashMap<String, String> tasker_availaibilty_status = sessionManager.getUserDetails();
                    String tasker_availability_status = tasker_availaibilty_status.get(SessionManager.KEY_STATUS);
                    Log.e(TAG, "tasker_availability_status: " + tasker_availability_status);
                    if (tasker_availability_status.equals("1")) {
                        if (sessionManager.isLoggedIn()) {
                            try {
                                JSONObject jobject = jsonObject.getJSONObject("message");

                                if (jobject.has("unique_id")) {
                                    int okl = 0;
                                    String uniqueId = jobject.getString("unique_id");
                                    System.out.println("Sara response--->socket-unique id" + uniqueId);
                                    mHelper = new notificationdb(context);
                                    dataBase = mHelper.getWritableDatabase();
                                    Cursor mCursor = dataBase.rawQuery("SELECT * FROM " + mHelper.TABLE_NAME + " WHERE uniqueid ='" + uniqueId + "'", null);

                                    if (mCursor.moveToFirst()) {
                                        do {
                                            okl++;
                                            System.out.println("Sara response--->socket-okl" + okl);
                                        } while (mCursor.moveToNext());
                                    }
                                    if (okl == 0) {
                                        System.out.println("Sara response--->socket-record not exists" + mHelper.getProfilesCount());
                                        mHelper = new notificationdb(context);
                                        dataBase = mHelper.getWritableDatabase();
                                        ContentValues values = new ContentValues();
                                        values.put(notificationdb.KEY_FNAME, jsonObject.toString());
                                        values.put(notificationdb.KEY_LNAME, uniqueId);
                                        dataBase.insert(notificationdb.TABLE_NAME, null, values);
                                        sessionManager = new SessionManager(context);
                                        if (sessionManager.isLoggedIn()) {
                                            parser.parseJSON(jsonObject);
                                        } else {

                                        }
                                    } else {

                                    }

                                } else {
                                    System.out.println("Sara response--->socket");
                                    sessionManager = new SessionManager(context);
                                    if (sessionManager.isLoggedIn()) {
                                        parser.parseJSON(jsonObject);
                                    } else {
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {

                        }
                    }


                }
                if (jsonObject.has("status")) {
                    sessionManager = new SessionManager(context);
                    HashMap<String, String> tasker_availaibilty_status = sessionManager.getUserDetails();
                    String tasker_availability_status = tasker_availaibilty_status.get(SessionManager.KEY_STATUS);
                    Log.e(TAG, "tasker_availability_status: " + tasker_availability_status);
                    if (tasker_availability_status.equals("1")) {
                        if (sessionManager.isLoggedIn()) {
                            try {
                                JSONObject jobject = jsonObject.getJSONObject("message");
                                if (jobject.has("unique_id")) {
                                    int okl = 0;
                                    String uniqueId = jobject.getString("unique_id");
                                    System.out.println("Sara response--->socket-unique id" + uniqueId);
                                    mHelper = new notificationdb(context);
                                    dataBase = mHelper.getWritableDatabase();
                                    Cursor mCursor = dataBase.rawQuery("SELECT * FROM " + mHelper.TABLE_NAME + " WHERE uniqueid ='" + uniqueId + "'", null);

                                    if (mCursor.moveToFirst()) {
                                        do {
                                            okl++;
                                            System.out.println("Sara response--->socket-okl" + okl);
                                        } while (mCursor.moveToNext());
                                    }
                                    if (okl == 0) {
                                        System.out.println("Sara response--->socket-record not exists" + mHelper.getProfilesCount());
                                        mHelper = new notificationdb(context);
                                        dataBase = mHelper.getWritableDatabase();
                                        ContentValues values = new ContentValues();
                                        values.put(notificationdb.KEY_FNAME, jsonObject.toString());
                                        values.put(notificationdb.KEY_LNAME, uniqueId);
                                        dataBase.insert(notificationdb.TABLE_NAME, null, values);
                                        sessionManager = new SessionManager(context);
                                        if (sessionManager.isLoggedIn()) {
                                            parser.parseJSON(jsonObject);
                                        }
                                    }
                                } else {
                                    System.out.println("Sara response--->socket");
                                    sessionManager = new SessionManager(context);
                                    if (sessionManager.isLoggedIn()) {
                                        parser.parseJSON(jsonObject);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                }

            }
        }
    };
}
