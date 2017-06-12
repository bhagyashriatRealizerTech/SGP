/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.realizer.schoolgenie.parent;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.realizer.schoolgenie.parent.Notification.NotificationModel;
import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.generalcommunication.backend.DALGeneralCommunication;
import com.realizer.schoolgenie.parent.generalcommunication.model.ParentGeneralCommunicationListModel;
import com.realizer.schoolgenie.parent.pupil.backend.DALMyPupilInfo;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.viewstar.backend.DALViewStar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    SharedPreferences sharedpreferences;
    static String useridglob="";
    static int numChatMessages = 0;
    static int numStarMessages = 0;
    static int numAttendanceMessages = 0;
    static int numAnnouncementMessages = 0;
    static int notificatinChatID=001;
    static int notificatinViewStarID=002;
    static int notificatinAttendanceID=0;
    static int notificatinAnnouncementID=004;
    final static String GROUP_KEY_PARENT = "SchoolGenieParent";
    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(Config.SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {

        Log.d(TAG, "Device registered: regId = " + registrationId);
        //displayMessage(context, getString(R.string.gcm_registered));
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        useridglob = sharedpreferences.getString("UidName", "");
        String empID =useridglob;
        ServerUtilities.register(context, registrationId, empID);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            if (context==null)
            {
                ServerUtilities.unregister(Singleton.getContext(), registrationId);
            }
            else
            {
                ServerUtilities.unregister(context, registrationId);
            }

            Log.d("GCMUnregister","Done");
        } else {
            Log.d("GCMUnregister","Not Done");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        // Log.i(TAG, "Received message");
        String message = intent.getStringExtra("message");
        // displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        // Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        // displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
        // Log.i(TAG, "Received error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
         Log.i(TAG, "Received recoverable error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_recoverable_error,
        //errorId))
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        DatabaseQueries qr  = new DatabaseQueries(context);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String date = df.format(calendar.getTime());
        int icon = R.mipmap.school_genie_logo;
        String[] msg=message.split("@@@");

        long when = System.currentTimeMillis();
        /*NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        Log.d("Message=",message);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, LoginActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, msg[4], intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);*/
        //splitting of msg to store in sqlite Conversion database
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
       /*
        //int nid = sharedpreferences.getInt("NID",0);
       // nid =nid+1;
        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putInt("NID",notificatinID );
        edit.commit();*/

        Log.d("Feature Type=", msg[0]);


        if(msg[0].equals(context.getString(R.string.GCMConversation))) {

            String uname[]=qr.Getuname(msg[2]).split("@@@");
            String imageurl = null;
            if(uname.length>1)
                imageurl = uname[1];

            SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
            String date1 = df1.format(calendar.getTime());
            Date sendDate = new Date();
            try {
                sendDate = df.parse(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Get an instance of the NotificationManager service
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
           /* NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);*/
            Notification notification ;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            Log.d("Message=", message);
            String msgname[] = msg[4].split(":");
            String title = context.getString(R.string.app_name);
            Intent notificationIntent = new Intent(context, DrawerActivity.class);
            notificationIntent.putExtra("FragName","ConverSation");
            notificationIntent.putExtra("FromId",msg[2]);
            notificationIntent.putExtra("FromName",msgname[0]);
            notificationIntent.putExtra("NID",notificatinChatID);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            SharedPreferences.Editor edit = sharedpreferences.edit();
            edit.putString("FragName", "ConverSation");
            edit.commit();

            // set intent so it does not start a new activity
            /*notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);*/
            PendingIntent intent =
                    PendingIntent.getActivity(context, notificatinChatID, notificationIntent, 0);

            int num=++numChatMessages;
            builder.setAutoCancel(true);
            builder.setContentTitle("Message");
            if (num==1) {
                builder.setContentText(msg[4]);
            }
            else {
                builder.setContentText("You have received " + num + " messages.");
            }
            builder.setSmallIcon(icon);
            builder.setContentIntent(intent);
            builder.setOngoing(false);  //API level 16
            builder.setNumber(num);
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.setGroup(GROUP_KEY_PARENT);
            builder.setGroupSummary(true);
            builder.build();

            notification = builder.getNotification();


            notificationManager.notify(notificatinChatID, builder.build());
            DatabaseQueries qr1 = new DatabaseQueries(context);
            //String msgnew[] = msg[4].split(":");
            long n = qr1.insertQuery("true",msg[2],msg[3],msgname[1],date1
                    ,"true",sendDate);
            sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String std=sharedpreferences.getString("SyncStd", "");
            String div = sharedpreferences.getString("SyncDiv", "");



            if (n >= 0) {

                int unread = qr1.GetUnreadCount(msg[2]);
                n = qr1.updateInitiatechat(std,div,msgname[0],"true",msg[2],unread+1,imageurl);
                if(n>0)
                {
                    NotificationModel obj = qr.GetNotificationByUserId(msg[2]);
                    if(obj.getId() == 0)
                    {
                        n =0;
                        NotificationModel notification1 = new NotificationModel();
                        notification1.setNotificationId(9);
                        notification1.setNotificationDate(date);
                        notification1.setNotificationtype("Message");
                        notification1.setMessage(msg[4]);
                        notification1.setIsRead("false");
                        notification1.setAdditionalData2(msg[2]);
                        notification1.setAdditionalData1(uname[0]+"@@@"+(unread+1)+"@@@"+imageurl);
                        n = qr.InsertNotification(notification1);
                        if(Singleton.getResultReceiver() != null)
                            Singleton.getResultReceiver().send(1,null);
                    }
                    else
                    {
                        n =0;
                        obj.setMessage(msg[4]);
                        obj.setNotificationDate(date);
                        obj.setAdditionalData1(uname[0]+"@@@"+(unread+1)+"@@@"+imageurl);

                        n = qr.UpdateNotification(obj);

                        Bundle b = new Bundle();
                        b.putInt("NotificationId",1);
                        b.putString("NotificationDate", date);
                        b.putString("NotificationType", "Query");
                        b.putString("NotificationMessage", msg[4]);
                        b.putString("IsNotificationread", "false");
                        b.putString("AdditionalData1",uname[0]+"@@@"+(unread+1)+"@@@"+imageurl);
                        b.putString("AdditionalData2",msg[2]);

                        if(Singleton.getResultReceiver() != null)
                            Singleton.getResultReceiver().send(1,b);
                    }
                }
                Log.d("Conversation", " Done!!!");
            } else {
                Log.d("Conversation", " Not Done!!!");
            }

            Singleton obj = Singleton.getInstance();
            if(obj.getResultReceiver() != null)
            {
                obj.getResultReceiver().send(100, null);
            }
        }
        else if(msg[0].equals(context.getString(R.string.GCMViewstar))) {
            //splitting of msg to store in sqlite ViewStar database
           /* NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);*/
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
            Log.d("Message=", message);
            Notification notification ;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            String title = context.getString(R.string.app_name);
            Intent notificationIntent = new Intent(context, DrawerActivity.class);
            notificationIntent.putExtra("FragName","ViewStar");
            // set intent so it does not start a new activity
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SharedPreferences.Editor edit = sharedpreferences.edit();
            edit.putString("FragName", "ViewStar");
            edit.commit();
            /*notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);*/
            PendingIntent intent =
                    PendingIntent.getActivity(context, notificatinViewStarID, notificationIntent, 0);
            int num=++numStarMessages;
            builder.setAutoCancel(true);
            builder.setContentTitle("Star");
            if (num==1) {
                builder.setContentText(msg[6]);
            }
            else {
                builder.setContentText("You have received "+num+" Stars.");
            }

            builder.setSmallIcon(icon);
            builder.setContentIntent(intent);
            builder.setOngoing(false);  //API level 16
            builder.setNumber(num);
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.build();
            notification = builder.getNotification();

            notificationManager.notify(notificatinViewStarID, notification);
            DALViewStar Vqr = new DALViewStar(context);

            long m = Vqr.insertViewStarInfo(msg[1], date, msg[3], msg[4],msg[5], msg[6]);

            if (m >= 0) {

                    m =0;
                    NotificationModel notification1 = new NotificationModel();
                    notification1.setNotificationId(8);
                    notification1.setNotificationDate(date);
                    notification1.setNotificationtype("Star");
                    notification1.setMessage(msg[3]);
                    notification1.setIsRead("false");
                    notification1.setAdditionalData2(msg[1]);
                    notification1.setAdditionalData1(msg[4]+"@@@"+msg[6]);
                    m = qr.InsertNotification(notification1);

                Singleton obj1 = Singleton.getInstance();
                if(obj1.getResultReceiver() != null)
                    obj1.getResultReceiver().send(1,null);

                Log.d("Viewstar", " Done!!!");
            } else {
                Log.d("Viewstar", " Not Done!!!");
            }
        }
        else if(msg[0].equals(context.getString(R.string.GCMAttendance)))
        {

            sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String student=sharedpreferences.getString("FirstNameStudent", "");
            if (msg[1].toLowerCase().contains(student.toLowerCase()))
            {
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(context);
                Notification notification ;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                Log.d("Message=", message);
                String title = context.getString(R.string.app_name);
                Intent notificationIntent = new Intent(context, DrawerActivity.class);
                notificationIntent.putExtra("FragName","Attendance");
                // set intent so it does not start a new activity
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.putString("FragName", "Attendance");
                edit.commit();
                PendingIntent intent =
                        PendingIntent.getActivity(context, notificatinAttendanceID++, notificationIntent, 0);

                builder.setAutoCancel(true);
                builder.setContentTitle("Attendance");
                builder.setContentText(msg[1]);
                builder.setSmallIcon(icon);
                builder.setContentIntent(intent);
                builder.setOngoing(false);  //API level 16
                builder.setNumber(0);
                builder.setDefaults(Notification.DEFAULT_SOUND);
                builder.setDefaults(Notification.DEFAULT_VIBRATE);
                builder.build();
                notification = builder.getNotification();
                notificationManager.notify(notificatinAttendanceID, notification);

                NotificationModel notification1 = new NotificationModel();
                notification1.setNotificationId(7);
                notification1.setNotificationDate(date);
                notification1.setNotificationtype("Attendance");
                notification1.setMessage(msg[1]);
                notification1.setIsRead("false");
                notification1.setAdditionalData2("");
                notification1.setAdditionalData1("");

                qr.InsertNotification(notification1);

                Singleton obj1 = Singleton.getInstance();
                if(obj1.getResultReceiver() != null)
                    obj1.getResultReceiver().send(1,null);
            }
        }
        else if(msg[0].equals(context.getString(R.string.GCMAnnouncement)))
        {
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
            Notification notification ;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            Log.d("Message=", message);
            String title = context.getString(R.string.app_name);
            Intent notificationIntent = new Intent(context, DrawerActivity.class);
            notificationIntent.putExtra("FragName","Announcement");
            // set intent so it does not start a new activity
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SharedPreferences.Editor edit = sharedpreferences.edit();
            edit.putString("FragName", "Announcement");
            edit.commit();
            PendingIntent intent =
                    PendingIntent.getActivity(context, notificatinAnnouncementID, notificationIntent, 0);
            int num=++numAnnouncementMessages;
            builder.setAutoCancel(true);
            builder.setContentTitle("Announcement");
            if (num==1) {
                if(msg[1].equals("CA")) {
                    builder.setContentText("Cultural Activity");
                }
                else if(msg[1].equals("SD")) {
                    builder.setContentText("Sport Day");
                }
                else if(msg[1].equals("FDC")) {
                    builder.setContentText("Fancy Dress Competitions");
                }
                else if(msg[1].equals("CM")) {
                    builder.setContentText("Class Meeting");
                }
                else
                {
                    builder.setContentText("Others");
                }
            }
            else {
                builder.setContentText(num+" Announcement has been done.");
            }

            builder.setSmallIcon(icon);
            builder.setContentIntent(intent);
            builder.setOngoing(false);  //API level 16
            builder.setNumber(num);
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.build();
            notification = builder.getNotification();
            /*notification.setLatestEventInfo(context, title, msg[0], intent);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;*/
            notificationManager.notify(notificatinAnnouncementID, notification);

            NotificationModel notification1 = new NotificationModel();
            notification1.setNotificationId(6);
            notification1.setNotificationDate(date);
            notification1.setNotificationtype("Alerts");
            notification1.setMessage(msg[2]);
            notification1.setIsRead("false");
            notification1.setAdditionalData2(msg[1]);
            notification1.setAdditionalData1(msg[3]);

            qr.InsertNotification(notification1);

            //storing alerts in database
            sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String student=sharedpreferences.getString("UidName", "");
            String UserData[]=new String[10];
            DALMyPupilInfo DAP=new DALMyPupilInfo(context);
            UserData =  DAP.GetSTDDIVData();

            DALGeneralCommunication gc=new DALGeneralCommunication(context);
            ArrayList<ParentGeneralCommunicationListModel> alertList= gc.GetGCTableData(student);
            String announcementId="1";
            if (alertList.size()==0)
            {
                announcementId="1";
            }
            else
            {
                announcementId=String.valueOf(alertList.size()+1);
            }
            gc.insertAnnouncementInfo(UserData[2], announcementId, UserData[0], UserData[1], UserData[3], msg[2], msg[1], msg[4], msg[3]);


            Singleton obj1 = Singleton.getInstance();
            if(obj1.getResultReceiver() != null)
                obj1.getResultReceiver().send(1,null);
            Log.d("Announcement Noti", " Done!!!");
        }

    }

    public void setCountZero(String notifyFragment)
    {
        if (notifyFragment.equals("Announcement"))
        {
            numAnnouncementMessages=0;
        }
        else  if (notifyFragment.equals("ViewStar"))
        {
            numStarMessages=0;
        }
        else  if (notifyFragment.equals("Attendance"))
        {
            numAttendanceMessages=0;
        }
        else  if (notifyFragment.equals("ConverSation"))
        {
            numChatMessages=0;
        }
    }

}
