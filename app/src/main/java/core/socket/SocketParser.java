package core.socket;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;

import com.hoperlady.FCM.NotificationUtils;
import com.hoperlady.R;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.app.ChatPage;
import com.hoperlady.app.MyJobs;
import com.hoperlady.app.MyTaskDetails;
import com.hoperlady.app.NavigationDrawer;
import com.hoperlady.app.NewLeadsPage;
import com.hoperlady.app.RatingSmilyPage;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;

import core.Xmpp.Xmpp_PushNotificationPage;
import core.service.ServiceConstant;

public class SocketParser implements ServiceConstant {
    private SessionManager sessionManager;
    private Context context;
    private MediaPlayer mediaPlayer;
    private String Job_status_message = "";
    private String status = "";
    NotificationChannel mChannel;
    NotificationManager notifManager;
    private NotificationUtils notificationUtils;

    public SocketParser(Context context, SessionManager sessionManager) {
        this.context = context;
        this.sessionManager = sessionManager;
        mediaPlayer = MediaPlayer.create(context, Uri.parse("android.resource://com.maidacpartner/" + R.raw.notifysnd));
    }

    public String getStringJSON(JSONObject object, String name) {
        try {
            return object.getString(name);
        } catch (Exception e) {
        }
        return "";
    }

    public void parseJSON(JSONObject object) {

        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }

        if (object.has("status")) {
            try {
                status = object.getString("status");
                if (status.equalsIgnoreCase("0") || status.equalsIgnoreCase("1")) {
                    AvailabilityChange(status);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            try {

                JSONObject jobject = object.getJSONObject("message");

                String action = jobject.getString(ACTION_TAG);
                String message = jobject.getString(MESSAGE_TAG);
                String key1 = "";
                if (jobject.has(KEY1_TAG)) {
                    key1 = jobject.getString(KEY1_TAG);
                }
                String job_id = key1;
                Intent resultIntent = null;
                String imageUrl = "";
                String title = "";
                String task_id = "";
                String tasker_id = "";
                String timestamp = "";
                String username = "";

                System.out.println("jobject-----------------" + jobject);
                System.out.println("socketdata-----------------" + object);
                System.out.println("action---------" + action);
                System.out.println("message-------------" + message);
                System.out.println("key1------------------" + key1);
                System.out.println("data-------------" + object);

                if (jobject.has("key0")) {
                    if (jobject.has("message")) {
                        Job_status_message = jobject.getString("message");
                    }

                    if (jobject.has("username"))
                        username = jobject.getString("username");

                    System.out.println("------------Job_Message--------" + Job_status_message);
                    job_id = jobject.getString("key0");
                } else {

                    JSONArray array = object.getJSONArray("messages");
                    JSONObject data = array.getJSONObject(0);

//                    title = context.getResources().getString(R.string.app_splace_name) + context.getResources().getString(R.string.Chat_Notification);
                    if (!username.equalsIgnoreCase("")) {
                        title = context.getResources().getString(R.string.chat_message_service_chat_without_user) + " " + username;
                    } else {
                        title = context.getResources().getString(R.string.chat_message_service_chat_user);
                    }

                    message = data.getString("message");
                    if (data.has("image")) {
                        imageUrl = data.getString("image");
                    }
                    timestamp = data.getString("date");
                    tasker_id = object.getString("tasker");
                    task_id = object.getString("task");

                    resultIntent = new Intent(context, ChatPage.class);
                    resultIntent.putExtra("TaskId", task_id);
                    resultIntent.putExtra("TaskerId", tasker_id);
                }

                String sAction = "";
                if (jobject.has("action")) {

                    Intent refreshIntent = new Intent();
                    refreshIntent.setAction("com.package.refresh.MyJobDetails");
                    context.sendBroadcast(refreshIntent);

                    sAction = jobject.getString("action");
                    if (sAction.equalsIgnoreCase(ServiceConstant.ACTION_TAG_JOB_REQUEST)) {
                        resultIntent = new Intent(context, MyTaskDetails.class);
                        resultIntent.putExtra("JobId", job_id);
                        resultIntent.putExtra("status", "ongoing");
                        //title = context.getResources().getString(R.string.Job_Notification);
                        title = context.getResources().getString(R.string.myfirebasemessagingservice_new_leads_title_txt);
                        message = Job_status_message;

                        Intent homepage = new Intent();
                        homepage.setAction("com.package.refresh.experthomepage");
                        context.sendBroadcast(homepage);

                    } else if (sAction.equalsIgnoreCase(ServiceConstant.ACTION_TAG_PAYMENT_PAID)) {
//                        resultIntent = new Intent(context, ReviwesPage.class);
                        resultIntent = new Intent(context, RatingSmilyPage.class);
                        resultIntent.putExtra("jobId", job_id);
//                            title = context.getResources().getString(R.string.Payment_Completed);
                        title = context.getResources().getString(R.string.myfirebasemessagingservice_payment_completed_title_txt);
                        message = Job_status_message;

                        Intent intent = new Intent(context, RatingSmilyPage.class);
                        intent.putExtra("jobId", job_id);
                        context.startActivity(intent);

                        Intent editpage = new Intent();
                        editpage.setAction("com.package.refresh.paymentfaresummery");
                        context.sendBroadcast(editpage);

                        Intent homepage = new Intent();
                        homepage.setAction("com.package.refresh.experthomepage");
                        context.sendBroadcast(homepage);

                    } else if (sAction.equalsIgnoreCase(ServiceConstant.ACTION_TAG_PARTIALLY_PAID)) {
                        resultIntent = new Intent(context, MyTaskDetails.class);
                        resultIntent.putExtra("JobId", job_id);
//                            title = context.getResources().getString(R.string.Payment_Completed);
                        title = context.getResources().getString(R.string.myfirebasemessagingservice_payment_completed_title_txt);
                        message = Job_status_message;

                        Intent editpage = new Intent();
                        editpage.setAction("com.package.refresh.paymentfaresummery");
                        context.sendBroadcast(editpage);

                        Intent homepage = new Intent();
                        homepage.setAction("com.package.refresh.experthomepage");
                        context.sendBroadcast(homepage);
                    } else if (sAction.equalsIgnoreCase(ServiceConstant.ACTION_TAG_JOB_CANCELLED)) {
//                            title = context.getResources().getString(R.string.Job_Cancelled_Notification);
                        title = context.getResources().getString(R.string.myfirebasemessagingservice_cancelled_job_title_txt) + " (" + job_id + ")";
                        message = Job_status_message;
                        resultIntent = new Intent(context, MyJobs.class);
                        resultIntent.putExtra("status", "cancelled");

                        Intent homepage = new Intent();
                        homepage.setAction("com.package.refresh.experthomepage");
                        context.sendBroadcast(homepage);

                    } else if (sAction.equalsIgnoreCase(ServiceConstant.ACTION_PENDING_TASK)) {
                        title = context.getResources().getString(R.string.Please_Accept_the_Pending_task) + " " + job_id + ")";
                        message = Job_status_message;
                        resultIntent = new Intent(context, NewLeadsPage.class);
                    } else if (sAction.equalsIgnoreCase(ServiceConstant.ACTION_LEFT_JOB)) {
                        title = context.getResources().getString(R.string.Your_Job_has_been_Expired) + " " + job_id + ")";
                        message = Job_status_message;
                        resultIntent = new Intent(context, NavigationDrawer.class);

                        Intent homepage = new Intent();
                        homepage.setAction("com.package.refresh.experthomepage");
                        context.sendBroadcast(homepage);
                    } else if (sAction.equalsIgnoreCase(ServiceConstant.AVAILABILITYSTATUS)) {

                        title = context.getResources().getString(R.string.availchangedff);
                        message = context.getResources().getString(R.string.availchanged);
                        resultIntent = new Intent(context, NavigationDrawer.class);
                    } else if (sAction.equalsIgnoreCase(ServiceConstant.Accout_deleted)) {
                        title = "";
                        logoutNotification(Job_status_message, sAction, job_id);

                    } else if (sAction.equalsIgnoreCase(ServiceConstant.Accout_status)) {
                        title = "";
                        Intent homepage = new Intent();
                        homepage.setAction("com.package.refresh.experthomepage");
                        context.sendBroadcast(homepage);
                    } else {
                        title = context.getResources().getString(R.string.app_splace_name);
                        //title = context.getResources().getString(R.string.Admin_Notification);
                        message = message + "\n" + Job_status_message;
                        resultIntent = new Intent(context, NavigationDrawer.class);
                    }
                }

                System.out.println("---------------------app is close");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationfororeo(context, title, message, timestamp, resultIntent);
                } else {
                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                        showNotificationMessage(context, title, message, timestamp, resultIntent);
                    } else {
                        // image is present, show notification with image
                        showNotificationMessageWithBigImage(context, title, message, timestamp, resultIntent, imageUrl);
                    }
                }
//                }
//                else {
//
//
//                    if (jobject.has("action")) {
//                        //socket code
//                        if (ServiceConstant.ACTION_TAG_JOB_REQUEST.equalsIgnoreCase(action)) {
//                            jobRequest(jobject);
//                        } else if (ServiceConstant.ACTION_TAG_JOB_CANCELLED.equalsIgnoreCase(action)) {
//                            jobCancelled(jobject);
//                        } else if (ServiceConstant.ACTION_TAG_RECEIVE_CASH.equalsIgnoreCase(action)) {
//                            System.out.println("recwivecash1-------");
//                            receiveCash(jobject);
//                        } else if (ServiceConstant.ACTION_TAG_PAYMENT_PAID.equalsIgnoreCase(action)) {
//                            paymentPaid(jobject);
//                        } else if (ServiceConstant.ACTION_REJECT_TASK.equalsIgnoreCase(action)) {
//                            JobRejected(jobject);
//                        } else if (ServiceConstant.Admin_Notification.equalsIgnoreCase(action)) {
//                            JobRejected(jobject);
//                        } else if (ServiceConstant.ACTION_TAG_PARTIALLY_PAID.equalsIgnoreCase(action)) {
//                            PartiallyPaid(jobject);
//                        } else if (ServiceConstant.ACTION_LEFT_JOB.equalsIgnoreCase(action)) {
//                            JobPending(jobject);
//                        } else if (ServiceConstant.ACTION_PENDING_TASK.equalsIgnoreCase(action)) {
//                            JobPending(jobject);
//                        } else if (ServiceConstant.AVAILABILITYSTATUS.equalsIgnoreCase(action)) {
//                            String key1s = jobject.getString(KEY11_TAG);
//                            AvailabilityChange(key1s);
//                        }
//                    } else {
//
//                        JSONArray array = object.getJSONArray("messages");
//                        JSONObject data = array.getJSONObject(0);
//
//                        title = context.getResources().getString(R.string.app_splace_name) + context.getResources().getString(R.string.Chat_Notification);
//                        message = data.getString("message");
//                        if (data.has("image")) {
//                            imageUrl = data.getString("image");
//                        }
//                        timestamp = data.getString("date");
//                        tasker_id = object.getString("tasker");
//                        task_id = object.getString("task");
//
//                        resultIntent = new Intent(context, ChatPage.class);
//                        resultIntent.putExtra("TaskId", task_id);
//                        resultIntent.putExtra("TaskerId", tasker_id);
//
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            notificationfororeo(context, title, message, timestamp, resultIntent);
//                        } else {
//                            // check for image attachment
//                            if (TextUtils.isEmpty(imageUrl)) {
//                                showNotificationMessage(context, title, message, timestamp, resultIntent);
//                            } else {
//                                // image is present, show notification with image
//                                showNotificationMessageWithBigImage(context, title, message, timestamp, resultIntent, imageUrl);
//                            }
//                        }
//
//
//                    }
//
//
//                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    private void jobRequest(JSONObject messageObject) throws Exception {

//        Intent intent = new Intent(context, MyJobs_OnGoingDetailPage.class);
//
//        intent.putExtra("JobId", getStringJSON(messageObject,"key1"));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);


        System.out.println("job request given-------");
        notificationfinish();
        Intent intent = new Intent(context, Xmpp_PushNotificationPage.class);
        intent.putExtra("Message", getStringJSON(messageObject, "message"));
        intent.putExtra("Action", getStringJSON(messageObject, "action"));
        intent.putExtra("JobId", getStringJSON(messageObject, "key0"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private void jobCancelled(JSONObject messageObject) throws Exception {
        notificationfinish();
        Intent intent = new Intent(context, Xmpp_PushNotificationPage.class);
        intent.putExtra("Message", getStringJSON(messageObject, "message"));
        intent.putExtra("Action", getStringJSON(messageObject, "action"));
        intent.putExtra("JobId", getStringJSON(messageObject, "key1"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void receiveCash(JSONObject messageObject) throws Exception {
        System.out.println("recivecash2-------");
        notificationfinish();
        Intent intent = new Intent(context, Xmpp_PushNotificationPage.class);
        intent.putExtra("Message", getStringJSON(messageObject, "message"));
        intent.putExtra("Action", getStringJSON(messageObject, "action"));
        intent.putExtra("amount", getStringJSON(messageObject, "key3"));
        intent.putExtra("JobId", getStringJSON(messageObject, "key1"));
        intent.putExtra("Currencycode", getStringJSON(messageObject, "key4"));
        System.out.println("recwivecash3-------");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }


    private void paymentPaid(JSONObject messageObject) throws Exception {
        notificationfinish();

        Intent broadcastIntentnavigation = new Intent();
        broadcastIntentnavigation.setAction("com.package.finish_LOADINGPAGE");
        context.sendBroadcast(broadcastIntentnavigation);

        Intent broadcastnewleads = new Intent();
        broadcastnewleads.setAction("com.finish.NewLeadsFragmet");
        context.sendBroadcast(broadcastnewleads);


        Intent intent = new Intent(context, Xmpp_PushNotificationPage.class);
        intent.putExtra("Message", getStringJSON(messageObject, "message"));
        intent.putExtra("Action", getStringJSON(messageObject, "action"));
        intent.putExtra("JobId", getStringJSON(messageObject, "key0"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }


    private void notificationfinish() {
        Intent broadcastIntentnavigation = new Intent();
        broadcastIntentnavigation.setAction("com.package.finish_PUSHNOTIFIACTION");
        context.sendBroadcast(broadcastIntentnavigation);

    }

    private void JobRejected(JSONObject messageObject) throws Exception {
        notificationfinish();
        Intent intent = new Intent(context, Xmpp_PushNotificationPage.class);
        intent.putExtra("Message", getStringJSON(messageObject, "message"));
        intent.putExtra("Action", getStringJSON(messageObject, "action"));
        intent.putExtra("JobId", getStringJSON(messageObject, "key0"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void JobPending(JSONObject messageObject) throws Exception {
        notificationfinish();
        Intent intent = new Intent(context, Xmpp_PushNotificationPage.class);
        intent.putExtra("Message", getStringJSON(messageObject, "message"));
        intent.putExtra("Action", getStringJSON(messageObject, "action"));
        intent.putExtra("JobId", getStringJSON(messageObject, "key0"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private void AvailabilityChange(String status) {

        notificationfinish();
        Intent intent = new Intent(context, Xmpp_PushNotificationPage.class);
        intent.putExtra("Action", "Admin Availability Change");
        if (status.equalsIgnoreCase("0")) {
            intent.putExtra("Message", "Availability Off");
        } else {
            intent.putExtra("Message", "Availability On");
        }
        intent.putExtra("JobId", status);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private void PartiallyPaid(JSONObject messageObject) throws Exception {
        notificationfinish();
        Intent intent = new Intent(context, Xmpp_PushNotificationPage.class);
        intent.putExtra("Message", getStringJSON(messageObject, "message"));
        intent.putExtra("Action", getStringJSON(messageObject, "action"));
        intent.putExtra("JobId", getStringJSON(messageObject, "key0"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static class Helper {

        private static boolean isAppIsInBackground(Context context) {
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

    private void logoutNotification(String message, String sAction, String key0) {
        Intent intent = new Intent(context, Xmpp_PushNotificationPage.class);
        intent.putExtra("Message", message);
        intent.putExtra("Action", sAction);
        intent.putExtra("key0", key0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private void notificationfororeo(Context context, String title, String message, String timeStamp, Intent intentz) {
        final int random = new Random().nextInt(61) + 20;
        NotificationCompat.Builder builder;
        intentz.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent;
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifManager = (NotificationManager) context.getSystemService
                    (Context.NOTIFICATION_SERVICE);

            mChannel = new NotificationChannel
                    ("0", title, importance);

            mChannel.setDescription(message);
            mChannel.enableVibration(true);
            notifManager.createNotificationChannel(mChannel);
        }
        builder = new NotificationCompat.Builder(context, "0");

        intentz.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intentz);
        pendingIntent = stackBuilder.getPendingIntent(1251, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle(title)
                .setSmallIcon(R.mipmap.handylogo) // required
                .setContentText(message)  // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource
                        (context.getResources(), R.mipmap.handylogo))
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
}
