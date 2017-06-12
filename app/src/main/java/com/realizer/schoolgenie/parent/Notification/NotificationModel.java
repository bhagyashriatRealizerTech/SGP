package com.realizer.schoolgenie.parent.Notification;

/**
 * Created by Bhagyashri on 9/14/2016.
 */
public class NotificationModel {

    int id;
    int notificationId;
    String notificationDate;
    String notificationtype;
    String message;
    String isRead;
    String additionalData1;
    String additionalData2;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getNotificationtype() {
        return notificationtype;
    }

    public void setNotificationtype(String notificationtype) {
        this.notificationtype = notificationtype;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String isRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getAdditionalData1() {
        return additionalData1;
    }

    public void setAdditionalData1(String additionalData1) {
        this.additionalData1 = additionalData1;
    }

    public String getAdditionalData2() {
        return additionalData2;
    }

    public void setAdditionalData2(String additionalData2) {
        this.additionalData2 = additionalData2;
    }
}
