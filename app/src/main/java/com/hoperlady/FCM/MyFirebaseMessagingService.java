package com.hoperlady.FCM;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hoperlady.R;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.app.ChatPage;
import com.hoperlady.app.MyJobs;
import com.hoperlady.app.MyTaskDetails;
import com.hoperlady.app.NavigationDrawer;
import com.hoperlady.app.NewLeadsPage;

import com.hoperlady.app.RatingSmilyPage;

import com.hoperlady.app.notificationdb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import core.Xmpp.Xmpp_PushNotificationPage;
import core.service.ServiceConstant;

/**
 * Created by user145 on 8/4/2017.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    NotificationChannel mChannel;
    NotificationManager notifManager;
    private NotificationUtils notificationUtils;
    private String Job_status_message = "";
    private notificationdb mHelper;
    private SessionManager sessionManager;
    private SQLiteDatabase dataBase;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        System.out.println("--------FCM Received-------" + remoteMessage);
        if (remoteMessage == null)
            return;

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                sessionManager = new SessionManager(getApplicationContext());
                if (!sessionManager.isLoggedIn())
                    return;
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                JSONObject object1 = json.getJSONObject("data");
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            sessionManager = new SessionManager(getApplicationContext());
            if (!sessionManager.isLoggedIn())
                return;
            HashMap<String, String> tasker_availaibilty_status = sessionManager.getUserDetails();
            String tasker_availability_status = tasker_availaibilty_status.get(SessionManager.KEY_STATUS);
            Log.e(TAG, "tasker_availability_status: " + tasker_availability_status);
            if (tasker_availability_status.equals("1")) {
                if (sessionManager.isLoggedIn()) {
                    handleNotification(remoteMessage.getNotification().getBody());
                    // Check if message contains a data payload.
                    if (remoteMessage.getData().size() > 0) {
                        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
                        try {
                            JSONObject json = new JSONObject(remoteMessage.getData().toString());
                            handleDataMessage(json);
                        } catch (Exception e) {
                            Log.e(TAG, "Exception: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private void handleNotification(String message) {
        Job_status_message = message;
    }

    public static class Helper {

        public static boolean isAppIsInBackground(Context context) {
            boolean isInBackground = true;
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                }
            }

            return isInBackground;
        }
    }


    private void handleDataMessage(JSONObject json) {


        Log.e(TAG, "push json: " + json.toString());
        String imageUrl = "";
        String message = "";
        String title = "";
        String task_id = "";
        String tasker_id = "";
        String timestamp = "";
        String job_id = "";
        Intent resultIntent = null;
        JSONObject object = null;
        String key0 = "";
        String username = "";

        try {
            JSONObject object1 = json.getJSONObject("data");

            if (object1.has("unique_id")) {
                int okl = 0;
                String uniqueId = object1.getString("unique_id");
                if (object1.has("username"))
                    username = object1.getString("username");
                System.out.println("Sara response--->fcm-unique id" + uniqueId);
                mHelper = new notificationdb(this);
                dataBase = mHelper.getWritableDatabase();
                Cursor mCursor = dataBase.rawQuery("SELECT * FROM " + mHelper.TABLE_NAME + " WHERE uniqueid ='" + uniqueId + "'", null);

                if (mCursor.moveToFirst()) {
                    do {
                        okl++;
                        System.out.println("Sara response--->fcm-okl" + okl);
                    } while (mCursor.moveToNext());
                }

                if (okl == 0) {
                    System.out.println("Sara response--->fcm-record not exists" + mHelper.getProfilesCount());
                    mHelper = new notificationdb(this);
                    dataBase = mHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(notificationdb.KEY_FNAME, json.toString());
                    values.put(notificationdb.KEY_LNAME, uniqueId);
                    dataBase.insert(notificationdb.TABLE_NAME, null, values);
                    Log.e("FCM Notification", object1.toString(1));
                    if (object1.has("key0")) {
                        key0 = object1.getString("key0");
                        Job_status_message = object1.getString("title");
                        System.out.println("------------Job_Message--------" + Job_status_message);
                        job_id = object1.getString("key0");
                    } else {
                        object = json.getJSONObject("data");
                        if (object.has("messages")) {
                            JSONArray array = object.getJSONArray("messages");

                            JSONObject data = array.getJSONObject(0);

//                            title = getResources().getString(R.string.chat_message_service_chat_user);// + getResources().getString(R.string.Chat_Notification);
                            if (!username.equalsIgnoreCase("")) {
                                title = getResources().getString(R.string.chat_message_service_chat_without_user) + " " + username;
                            } else {
                                title = getResources().getString(R.string.chat_message_service_chat_user);
                            }
                            message = data.getString("message");
                            if (data.has("image")) {
                                imageUrl = data.getString("image");
                            }
                            timestamp = data.getString("date");
                            tasker_id = object.getString("tasker");
                            task_id = object.getString("task");

                            Log.e(TAG, "title: " + title);
                            Log.e(TAG, "message: " + message);
                            Log.e(TAG, "tasker_id: " + tasker_id);
                            Log.e(TAG, "task_id: " + task_id);

                            resultIntent = new Intent(getApplicationContext(), ChatPage.class);
                            resultIntent.putExtra("TaskId", task_id);
                            resultIntent.putExtra("TaskerId", tasker_id);
                        }
                    }

                    String sAction = "";
                    if (object1.has("action")) {

                        Intent refreshIntent = new Intent();
                        refreshIntent.setAction("com.package.refresh.MyJobDetails");
                        sendBroadcast(refreshIntent);

                        sAction = object1.getString("action");
                        if (sAction.equalsIgnoreCase(ServiceConstant.ACTION_TAG_JOB_REQUEST)) {
                            resultIntent = new Intent(getApplicationContext(), MyTaskDetails.class);
                            resultIntent.putExtra("JobId", job_id);
                            resultIntent.putExtra("status", "ongoing");
                            //title = getResources().getString(R.string.Job_Notification);
                            title = getResources().getString(R.string.myfirebasemessagingservice_new_leads_title_txt);
                            message = Job_status_message;
                            Intent homepage = new Intent();
                            homepage.setAction("com.package.refresh.experthomepage");
                            sendBroadcast(homepage);
                        } else if (sAction.equalsIgnoreCase(ServiceConstant.ACTION_TAG_PAYMENT_PAID)) {
//                            resultIntent = new Intent(getApplicationContext(), ReviwesPage.class);
                            resultIntent = new Intent(getApplicationContext(), RatingSmilyPage.class);
                            resultIntent.putExtra("jobId", job_id);
                            //title = getResources().getString(R.string.Payment_Completed);
                            title = getResources().getString(R.string.myfirebasemessagingservice_payment_completed_title_txt);
                            message = Job_status_message;

                            Intent intent = new Intent(getApplicationContext(), RatingSmilyPage.class);
                            intent.putExtra("jobId", job_id);
                            startActivity(intent);

                            Intent editpage = new Intent();
                            editpage.setAction("com.package.refresh.paymentfaresummery");
                            sendBroadcast(editpage);

                            Intent refreshBroadcastIntent = new Intent();
                            refreshBroadcastIntent.setAction("com.package.refresh.MyJobDetails");
                            sendBroadcast(refreshBroadcastIntent);
                            Intent homepage = new Intent();
                            homepage.setAction("com.package.refresh.experthomepage");
                            sendBroadcast(homepage);
                        } else if (sAction.equalsIgnoreCase(ServiceConstant.ACTION_TAG_JOB_CANCELLED)) {
//                            title = getResources().getString(R.string.Job_Cancelled_Notification);
                            title = getResources().getString(R.string.myfirebasemessagingservice_cancelled_job_title_txt) + " (" + job_id + ")";
                            message = Job_status_message;
                            resultIntent = new Intent(getApplicationContext(), MyJobs.class);
                            resultIntent.putExtra("status", "cancelled");

                            Intent refreshBroadcastIntent = new Intent();
                            refreshBroadcastIntent.setAction("com.package.refresh.MyJobDetails");
                            sendBroadcast(refreshBroadcastIntent);

                            Intent homepage = new Intent();
                            homepage.setAction("com.package.refresh.experthomepage");
                            sendBroadcast(homepage);

                        } else if (sAction.equalsIgnoreCase(ServiceConstant.ACTION_PENDING_TASK)) {
                            title = getResources().getString(R.string.Please_Accept_the_Pending_task) + " " + job_id + ")";
                            message = Job_status_message;
                            resultIntent = new Intent(getApplicationContext(), NewLeadsPage.class);
                            Intent homepage = new Intent();
                            homepage.setAction("com.package.refresh.experthomepage");
                            sendBroadcast(homepage);
                        } else if (sAction.equalsIgnoreCase(ServiceConstant.ACTION_LEFT_JOB)) {
                            title = getResources().getString(R.string.Your_Job_has_been_Expired) + " " + job_id;
                            message = Job_status_message;
                            resultIntent = new Intent(getApplicationContext(), NavigationDrawer.class);
                            Intent homepage = new Intent();
                            homepage.setAction("com.package.refresh.experthomepage");
                            sendBroadcast(homepage);
                        } else if (sAction.equalsIgnoreCase(ServiceConstant.AVAILABILITYSTATUS)) {
                            title = getResources().getString(R.string.availchangedff);
                            message = getResources().getString(R.string.availchanged);
                            resultIntent = new Intent(getApplicationContext(), NavigationDrawer.class);
                        } else if (object != null) {
                            if (object.has("user")) {
                                resultIntent = new Intent(getApplicationContext(), ChatPage.class);
                                resultIntent.putExtra("TaskId", task_id);
                                resultIntent.putExtra("TaskerId", tasker_id);
                            }
                        } else if (sAction.equalsIgnoreCase(ServiceConstant.Accout_deleted)) {
                            title = "";
                            logoutNotification(Job_status_message, sAction, key0);

                        } else if (sAction.equalsIgnoreCase(ServiceConstant.Accout_status)) {
                            title = "";
                            Intent homepage = new Intent();
                            homepage.setAction("com.package.refresh.experthomepage");
                            sendBroadcast(homepage);
                        } else if (sAction.equalsIgnoreCase(ServiceConstant.ACTION_TAG_PARTIALLY_PAID)) {
                            resultIntent = new Intent(getApplicationContext(), MyTaskDetails.class);
                            resultIntent.putExtra("JobId", job_id);
                            //title = getResources().getString(R.string.Payment_Completed);
                            title = getResources().getString(R.string.myfirebasemessagingservice_payment_completed_title_txt);
                            message = Job_status_message;
                            Intent editpage = new Intent();
                            editpage.setAction("com.package.refresh.paymentfaresummery");
                            sendBroadcast(editpage);

                            Intent refreshBroadcastIntent = new Intent();
                            refreshBroadcastIntent.setAction("com.package.refresh.MyJobDetails");
                            sendBroadcast(refreshBroadcastIntent);

                            Intent homepage = new Intent();
                            homepage.setAction("com.package.refresh.experthomepage");
                            sendBroadcast(homepage);
                        } else {
                            title = getResources().getString(R.string.Admin_Notification);
//                            title = getResources().getString(R.string.app_splace_name);
                            message = message + "\n" + Job_status_message;
                            resultIntent = new Intent(getApplicationContext(), NavigationDrawer.class);
                        }
                    }
//                    if (Helper.isAppIsInBackground(getApplicationContext()) == true) {
                    System.out.println("---------------------app is close");
                    if (!title.equalsIgnoreCase("")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notificationfororeo(getApplicationContext(), title, message, timestamp, resultIntent);
                        } else {
                            // check for image attachment
                            if (TextUtils.isEmpty(imageUrl)) {
                                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                            } else {
                                // image is present, show notification with image
                                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                            }
                        }
                    }
//                    } else {
//
//
//                        System.out.println("action---------" + sAction);
//                        System.out.println("message-------------" + message);
//                        System.out.println("JobId-------------" + job_id);
//
//                        if (ServiceConstant.ACTION_TAG_JOB_REQUEST.equalsIgnoreCase(sAction)) {
//                            System.out.println("job request given-------");
//                            notificationfinish();
//                            Intent intent = new Intent(getApplicationContext(), Xmpp_PushNotificationPage.class);
//                            intent.putExtra("Message", message);
//                            intent.putExtra("Action", sAction);
//                            intent.putExtra("JobId", job_id);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//
//                        } else if (ServiceConstant.ACTION_TAG_JOB_CANCELLED.equalsIgnoreCase(sAction)) {
//                            notificationfinish();
//                            Intent intent = new Intent(getApplicationContext(), Xmpp_PushNotificationPage.class);
//                            intent.putExtra("Message", message);
//                            intent.putExtra("Action", sAction);
//                            intent.putExtra("JobId", job_id);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        } else if (ServiceConstant.ACTION_TAG_RECEIVE_CASH.equalsIgnoreCase(sAction)) {
//                            System.out.println("recwivecash1-------");
//                            System.out.println("recivecash2-------");
//                            notificationfinish();
//                            Intent intent = new Intent(getApplicationContext(), Xmpp_PushNotificationPage.class);
//                            intent.putExtra("Message", message);
//                            intent.putExtra("Action", sAction);
//                            intent.putExtra("amount", "");
//                            intent.putExtra("JobId", job_id);
//                            intent.putExtra("Currencycode", sessionManager.getWalletDetails());
//                            System.out.println("recwivecash3-------");
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//
//                        } else if (ServiceConstant.ACTION_TAG_PAYMENT_PAID.equalsIgnoreCase(sAction)) {
//                            notificationfinish();
//
//                            Intent broadcastIntentnavigation = new Intent();
//                            broadcastIntentnavigation.setAction("com.package.finish_LOADINGPAGE");
//                            sendBroadcast(broadcastIntentnavigation);
//
//                            Intent broadcastnewleads = new Intent();
//                            broadcastnewleads.setAction("com.finish.NewLeadsFragmet");
//                            sendBroadcast(broadcastnewleads);
//
//
//                            Intent intent = new Intent(getApplicationContext(), Xmpp_PushNotificationPage.class);
//                            intent.putExtra("Message", message);
//                            intent.putExtra("Action", sAction);
//                            intent.putExtra("JobId", job_id);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        } else if (ServiceConstant.ACTION_REJECT_TASK.equalsIgnoreCase(sAction)) {
//                            notificationfinish();
//                            Intent intent = new Intent(getApplicationContext(), Xmpp_PushNotificationPage.class);
//                            intent.putExtra("Message", message);
//                            intent.putExtra("Action", sAction);
//                            intent.putExtra("JobId", job_id);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        } else if (ServiceConstant.Admin_Notification.equalsIgnoreCase(sAction)) {
//                            notificationfinish();
//                            Intent intent = new Intent(getApplicationContext(), Xmpp_PushNotificationPage.class);
//                            intent.putExtra("Message", message);
//                            intent.putExtra("Action", sAction);
//                            intent.putExtra("JobId", job_id);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        } else if (ServiceConstant.ACTION_TAG_PARTIALLY_PAID.equalsIgnoreCase(sAction)) {
//                            notificationfinish();
//                            Intent intent = new Intent(getApplicationContext(), Xmpp_PushNotificationPage.class);
//                            intent.putExtra("Message", message);
//                            intent.putExtra("Action", sAction);
//                            intent.putExtra("JobId", job_id);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        } else if (ServiceConstant.ACTION_LEFT_JOB.equalsIgnoreCase(sAction)) {
//                            notificationfinish();
//                            Intent intent = new Intent(getApplicationContext(), Xmpp_PushNotificationPage.class);
//                            intent.putExtra("Message", message);
//                            intent.putExtra("Action", sAction);
//                            intent.putExtra("JobId", job_id);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        } else if (ServiceConstant.ACTION_PENDING_TASK.equalsIgnoreCase(sAction)) {
//                            notificationfinish();
//                            Intent intent = new Intent(getApplicationContext(), Xmpp_PushNotificationPage.class);
//                            intent.putExtra("Message", message);
//                            intent.putExtra("Action", sAction);
//                            intent.putExtra("JobId", job_id);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        } else if (ServiceConstant.AVAILABILITYSTATUS.equalsIgnoreCase(sAction)) {
//                            notificationfinish();
//                            Intent intent = new Intent(getApplicationContext(), Xmpp_PushNotificationPage.class);
//                            intent.putExtra("Action", "Admin Availability Change");
//                            if (job_id.equalsIgnoreCase("0")) {
//                                intent.putExtra("Message", "Availability Off");
//                            } else {
//                                intent.putExtra("Message", "Availability On");
//                            }
//                            intent.putExtra("JobId", job_id);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        }
//
//                        //pending for socket write
//                    }
                }
            } else {
                object = json.getJSONObject("data");
                JSONArray array = object.getJSONArray("messages");
                JSONObject data = array.getJSONObject(0);

                title = getResources().getString(R.string.app_splace_name) + getResources().getString(R.string.Chat_Notification);
                message = data.getString("message");
                if (data.has("image")) {
                    imageUrl = data.getString("image");
                }
                timestamp = data.getString("date");
                tasker_id = object.getString("tasker");
                task_id = object.getString("task");


                resultIntent = new Intent(getApplicationContext(), ChatPage.class);
                resultIntent.putExtra("TaskId", task_id);
                resultIntent.putExtra("TaskerId", tasker_id);

                Log.e(TAG, "title: " + title);
                Log.e(TAG, "message: " + message);
                Log.e(TAG, "tasker_id: " + tasker_id);
                Log.e(TAG, "task_id: " + task_id);

                System.out.println("---------------------app is close");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationfororeo(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                    } else {
                        // image is present, show notification with image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    }
                }
            }


        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void notificationfororeo(Context context, String title, String message, String timeStamp, Intent intentz) {
        final int random = new Random().nextInt(61) + 20;
        NotificationCompat.Builder builder;
        intentz.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent;
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifManager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);

            mChannel = new NotificationChannel
                    ("0", title, importance);

            mChannel.setDescription(message);
            mChannel.enableVibration(true);
            notifManager.createNotificationChannel(mChannel);
        }
        builder = new NotificationCompat.Builder(this, "0");

        intentz.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intentz);
        pendingIntent = stackBuilder.getPendingIntent(1251, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentTitle(title)
                .setSmallIcon(R.mipmap.handylogo) // required
                .setContentText(message)  // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource
                        (getResources(), R.mipmap.handylogo))
                .setBadgeIconType(R.mipmap.handylogo)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri
                        (RingtoneManager.TYPE_NOTIFICATION));
        Notification notification = builder.build();
        notifManager.notify(random, notification);
    }


    //fcm compare

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    private void notificationfinish() {
        Intent broadcastIntentnavigation = new Intent();
        broadcastIntentnavigation.setAction("com.package.finish_PUSHNOTIFIACTION");
        sendBroadcast(broadcastIntentnavigation);

    }

    private void logoutNotification(String message, String sAction, String key0) {
        Intent intent = new Intent(getApplicationContext(), Xmpp_PushNotificationPage.class);
        intent.putExtra("Message", message);
        intent.putExtra("Action", sAction);
        intent.putExtra("key0", key0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}