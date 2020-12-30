package com.hoperlady.Pojo;

import java.util.ArrayList;

/**
 * Created by user145 on 2/14/2017.
 */
public class NotificationPojoInfo {

    private String NotificationTaskId = "";
    private String NotificationBookingId = "";
    private String NotificationCategory = "";
    private ArrayList<NotificationMessageInfo> NotificationMessageInfo = null;

    public ArrayList<com.hoperlady.Pojo.NotificationMessageInfo> getNotificationMessageInfo() {
        return NotificationMessageInfo;
    }

    public void setNotificationMessageInfo(ArrayList<com.hoperlady.Pojo.NotificationMessageInfo> notificationMessageInfo) {
        NotificationMessageInfo = notificationMessageInfo;
    }

    public String getNotificationTaskId() {
        return NotificationTaskId;
    }

    public void setNotificationTaskId(String notificationTaskId) {
        NotificationTaskId = notificationTaskId;
    }

    public String getNotificationCategory() {
        return NotificationCategory;
    }

    public void setNotificationCategory(String notificationCategory) {
        NotificationCategory = notificationCategory;
    }

    public String getNotificationBookingId() {
        return NotificationBookingId;
    }

    public void setNotificationBookingId(String notificationBookingId) {
        NotificationBookingId = notificationBookingId;
    }
}
