package com.realizer.schoolgenie.parent.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.realizer.schoolgenie.parent.Notification.NotificationModel;
import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.chat.backend.DALQueris;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionAsyncTaskPost;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionModel;
import com.realizer.schoolgenie.parent.funcenter.asynctask.ParentFunCenterAsynckPost;
import com.realizer.schoolgenie.parent.funcenter.asynctask.ParentFunCenterImageAsynckPost;
import com.realizer.schoolgenie.parent.funcenter.backend.DLAFuncenter;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterGalleryModel;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterModel;
import com.realizer.schoolgenie.parent.funcenter.model.QueueListModel;
import com.realizer.schoolgenie.parent.generalcommunication.asynctask.ParentGeneralCommunicationAsyncTaskGet;
import com.realizer.schoolgenie.parent.generalcommunication.backend.DALGeneralCommunication;
import com.realizer.schoolgenie.parent.generalcommunication.model.ParentGeneralCommunicationListModel;
import com.realizer.schoolgenie.parent.homework.asynctask.ClassworkAsyncTaskPost;
import com.realizer.schoolgenie.parent.homework.asynctask.HomeworkAsyncTaskPost;
import com.realizer.schoolgenie.parent.homework.backend.DALHomework;
import com.realizer.schoolgenie.parent.homework.model.ParentHomeworkListModel;
import com.realizer.schoolgenie.parent.pupil.backend.DALMyPupilInfo;
import com.realizer.schoolgenie.parent.timetable.asynctask.ParentTimeTableAsyncTask;
import com.realizer.schoolgenie.parent.timetable.model.ParentTimeTableExamListModel;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.utils.StoreBitmapImages;
import com.realizer.schoolgenie.parent.utils.StoreBitmapImages1;
import com.realizer.schoolgenie.parent.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by shree on 11/20/2015.
 */
public class ManualSyncService extends Service implements OnTaskCompleted {

    DatabaseQueries qr;
    DALMyPupilInfo DAP;
    DALQueris qrt;
    DLAFuncenter dla;
    DALGeneralCommunication dlag;
    String type;
    int id,LastId;
    Handler handler;
    ProgressDialog dialog;
    AlertDialog.Builder adbdialog;
    ArrayList<QueueListModel> quelist;
    Context mContext;
    String student;
    boolean SyncTimeTable=true;
    boolean SyncHomework=false;
    boolean SyncClasswork=false;
    boolean SyncFuncenterEventGallery=false;
    boolean SyncFuncenterImageGallery=false;
    boolean SyncAlertNdAttendance=false;
    boolean SyncException=false;
    String UserData[]=new String[10];
    String currentDate="";
    ArrayList<String> subjects;
    static int counter=0;
    SharedPreferences preferences;
    static int eventimagesCount=0;
    String globID="";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ManualSyncService", "Stop");
       // Toast.makeText(this, "Service Destroy", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //Toast.makeText(this, "Service LowMemory", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ManualSyncService", "Start");
        qr = new DatabaseQueries(this);
        DAP=new DALMyPupilInfo(this);
        qrt = new DALQueris(this);
        dla= new DLAFuncenter(this);
        dlag = new DALGeneralCommunication(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        subjects = qrt.GetAllSub();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        currentDate = df.format(calendar.getTime());
        UserData =  DAP.GetSTDDIVData();
        SyncTimeTable=true;
        handler = new Handler();
        BackgroundThread background = new BackgroundThread();
        background.start();

        return START_NOT_STICKY;
    }

   private class BackgroundThread extends Thread {
       @Override
       public void run() {
           super.run();
           if(Config.isConnectingToInternet(ManualSyncService.this))
           {
               Syncdata();
           }
       }
   }


    @Override
    public void onTaskCompleted(String s) {
        Log.d("String", s);

       final String[] onTaskString=s.split("@@@");

        if (onTaskString[1].equalsIgnoreCase("TimeTable"))
        {
            Log.d("OnTaskCompleted","TimeTable");
            DatabaseQueries dla = new DatabaseQueries(this);
            JSONObject rootObj = null;
            try {
                rootObj = new JSONObject(onTaskString[0]);

                JSONArray jsonArray = rootObj.getJSONArray("TTLst");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String academicyear = jsonObject.getString("AcademicYr");
                    String description = jsonObject.getString("Description");
                    String Std = jsonObject.getString("Std");
                    String timeTableImage = jsonObject.getString("TimeTableImage");
                    String timetabletext = jsonObject.getString("TimeTableText");
                    String uploaddate = jsonObject.getString("UploadDate");
                    String uploadedBy = jsonObject.getString("UploadedBy");
                    String division = jsonObject.getString("division");
                    String fileName = jsonObject.getString("fileName");
                    String schoolCode = jsonObject.getString("schoolCode");

                    Calendar calendar1 = Calendar.getInstance();
                    SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                    String date1 = df1.format(calendar1.getTime());

                    ArrayList<ParentTimeTableExamListModel> results =dla.getTimeTableData();
                    boolean isPresent=false;
                    int eventId=0;
                    for (int j=0;j<results.size();j++)
                    {
                        if (results.get(j).getDescription().equalsIgnoreCase(description) &&
                                results.get(j).getTitle().equalsIgnoreCase(timetabletext))
                        {
                            isPresent=true;
                            eventId=results.get(j).getTtid();
                            break;
                        }
                    }
                    if (isPresent)
                    {
                        dla.updateTimeTable(eventId,academicyear, description, Std, timeTableImage, timetabletext,
                                date1, uploadedBy, division, fileName, schoolCode);
                        //Toast.makeText(this, "Updated Time table", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        long n=0;
                        n=dla.insertTimeTable(academicyear, description, Std, timeTableImage, timetabletext,
                                date1, uploadedBy, division, fileName, schoolCode);
                       /* String newPath = new Utility().getURLImage(timeTableImage);
                        if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                            new StoreBitmapImages(newPath,newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                        }*/
                       // Toast.makeText(this, "TimeTable Downloaded Successfully...", Toast.LENGTH_SHORT).show();
                        if (n>0)
                        {
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                            String date = df.format(calendar.getTime());
                            NotificationModel notification1 = new NotificationModel();
                            notification1.setNotificationId(1);
                            notification1.setNotificationDate(date);
                            notification1.setNotificationtype("TimeTable");
                            notification1.setMessage(timetabletext);
                            notification1.setIsRead("false");
                            notification1.setAdditionalData1(uploadedBy);
                            qr.InsertNotification(notification1);
                            if(Singleton.getResultReceiver() != null)
                                Singleton.getResultReceiver().send(1,null);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (onTaskString[1].equalsIgnoreCase("Homework"))
        {
            Log.d("OnTaskCompleted","Homework");
            DALHomework dla = new DALHomework(this);
            JSONObject rootObj = null;
            try {
                rootObj = new JSONObject(onTaskString[0]);
                JSONObject obj=rootObj.getJSONObject("fetchHomeWorkResult");
                String schoolCode= obj.getString("SchoolCode");
                String std= obj.getString("Std");
                String division= obj.getString("div");
                String givenby= obj.getString("givenBy");
                String hwdate= obj.getString("hwDate");
                JSONArray img  = obj.getJSONArray("hwImage64Lst");
                JSONArray text  = obj.getJSONArray("hwTxtLst");
                String subject= obj.getString("subject");

                if (!std.equalsIgnoreCase("null") && !givenby.equalsIgnoreCase("null"))
                {
                    String[] IMG=new String[img.length()];
                    String[] TEXT=new String[text.length()];
                    for (int i = 0; i < img.length(); i++) {
                        IMG[i] = img.getString(i);
                    }
                    for (int i = 0; i < text.length(); i++) {
                        TEXT[i] = text.getString(i);
                    }

                    DALHomework dh=new DALHomework(this);
                    ArrayList<ParentHomeworkListModel> results=dh.GetHomeworkInfoData(hwdate, onTaskString[1]);
                    boolean isPresent=false;
                    for (int j=0;j<results.size();j++)
                    {
                        if (results.get(j).getSubject().equalsIgnoreCase(subject) && results.get(j).getHwdate().equalsIgnoreCase(hwdate))
                        {
                            isPresent=true;
                            break;
                        }
                    }

                    if (!isPresent)
                    {
                        String hwUUID= String.valueOf(UUID.randomUUID());
                        long n=0;
                        for (int i = 0; i < IMG.length; i++) {
                            n=dla.insertHomeworkInfo(schoolCode, std, division, givenby, hwdate, IMG[i].toString(), TEXT[TEXT.length-1].toString(), subject,onTaskString[1],hwUUID);
                        }
                        if (n>0)
                        {
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                            String date = df.format(calendar.getTime());

                            NotificationModel notification1 = new NotificationModel();
                            notification1.setNotificationId(2);
                            notification1.setNotificationDate(date);
                            notification1.setNotificationtype("Homework");
                            notification1.setMessage(subject);
                            notification1.setIsRead("false");
                            notification1.setAdditionalData1(givenby);
                            qr.InsertNotification(notification1);
                            if(Singleton.getResultReceiver() != null)
                                Singleton.getResultReceiver().send(1,null);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (onTaskString[1].equalsIgnoreCase("Classwork"))
        {
            Log.d("OnTaskCompleted","Classwork");
            DALHomework dla = new DALHomework(this);
            JSONObject rootObj = null;
            try {
                rootObj = new JSONObject(onTaskString[0]);
                JSONObject obj = rootObj.getJSONObject("fetchClassWorkResult");
                String schoolCode = obj.getString("SchoolCode");
                String std = obj.getString("Std");
                String division = obj.getString("div");
                String givenby = obj.getString("givenBy");
                String hwdate = obj.getString("cwDate");
                JSONArray img = obj.getJSONArray("cwImage64Lst");
                JSONArray text = obj.getJSONArray("CwTxtLst");
                String subject = obj.getString("subject");

                if (!std.equalsIgnoreCase("null") && !givenby.equalsIgnoreCase("null")) {
                    String[] IMG = new String[img.length()];
                    String[] TEXT=new String[text.length()];
                    for (int i = 0; i < img.length(); i++) {
                        IMG[i] = img.getString(i);
                    }
                    for (int i = 0; i < text.length(); i++) {
                        TEXT[i] = text.getString(i);
                    }

                    DALHomework dh=new DALHomework(this);
                    ArrayList<ParentHomeworkListModel> results=dh.GetHomeworkInfoData(hwdate, onTaskString[1]);
                    boolean isPresent=false;
                    for (int j=0;j<results.size();j++)
                    {
                        if (results.get(j).getSubject().equalsIgnoreCase(subject) && results.get(j).getHwdate().equalsIgnoreCase(hwdate))
                        {
                            isPresent=true;
                            break;
                        }
                    }

                    if (!isPresent)
                    {
                        String hwUUID= String.valueOf(UUID.randomUUID());
                        long n=0;
                        for (int i = 0; i < IMG.length; i++) {
                            n=dla.insertHomeworkInfo(schoolCode, std, division, givenby, hwdate, IMG[i].toString(), TEXT[TEXT.length-1].toString(), subject,onTaskString[1],hwUUID);
                        }
                        if (n>0)
                        {
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                            String date = df.format(calendar.getTime());

                            NotificationModel notification1 = new NotificationModel();
                            notification1.setNotificationId(2);
                            notification1.setNotificationDate(date);
                            notification1.setNotificationtype("Classwork");
                            notification1.setMessage(subject);
                            notification1.setIsRead("false");
                            notification1.setAdditionalData1(givenby);
                            qr.InsertNotification(notification1);
                            if(Singleton.getResultReceiver() != null)
                                Singleton.getResultReceiver().send(1,null);
                        }
                    }
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else if (onTaskString[1].equalsIgnoreCase("FuncenterEvents"))
        {
            Log.d("OnTaskCompleted","FuncenterEvents");
            if (onTaskString[0].equalsIgnoreCase("{\"eventMDLst\":[]}"))
            {

            }
            else {
                JSONObject rootObj = null;
                try {
                    rootObj = new JSONObject(onTaskString[0]);
                    JSONArray jsonArray = rootObj.getJSONArray("eventMDLst");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String createTS = obj.getString("CreateTS");
                        String evntname = obj.getString("Event");
                        String eventDate = obj.getString("EventDate");
                        String evntuuid = obj.getString("EventId");
                        String thmbnail = obj.getString("ThumbNailImage");
                        String thumbNailPath = obj.getString("ThumbNailPath");

                        ArrayList<ParentFunCenterModel> getEventData = dla.GetEventInfoData();
                        boolean isPresent = false;
                        int eventId = 0;
                        for (int j = 0; j < getEventData.size(); j++) {
                            if (getEventData.get(j).getEventUUID().equalsIgnoreCase(evntuuid)) {
                                isPresent = true;
                                //eventId = getEventData.get(j).getEventid();
                                break;
                            }
                        }
                        if (isPresent) {
                            // dla.updateEventSyncFlag(eventId, createTS, evntname, eventDate, evntuuid, thmbnail, thumbNailPath);
                            // Toast.makeText(this,"Updated Funcenter events",Toast.LENGTH_LONG).show();
                        } else {
                            String newPath = new Utility().getURLImage(thumbNailPath);
                          /*  if (!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                                new StoreBitmapImages(newPath, newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                            }*/
                            long n=0;
                            n=dla.insertEventInfo(createTS, evntname, eventDate, evntuuid, thmbnail, thumbNailPath);
                           // Toast.makeText(this, "Event Downloaded Successfully...", Toast.LENGTH_SHORT).show();
                            if (n>0)
                            {
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                                String date = df.format(calendar.getTime());

                                NotificationModel notification1 = new NotificationModel();
                                notification1.setNotificationId(4);
                                notification1.setNotificationDate(date);
                                notification1.setNotificationtype("Fun Center");
                                notification1.setMessage(evntname);
                                notification1.setIsRead("false");
                                notification1.setAdditionalData1("Events");
                                qr.InsertNotification(notification1);
                                if(Singleton.getResultReceiver() != null)
                                    Singleton.getResultReceiver().send(1,null);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (onTaskString[1].equalsIgnoreCase("FuncenterEventsImages"))
        {
            Log.d("OnTaskCompleted","FuncenterEventsImages");
            JSONObject rootObj = null;
            try {
                rootObj = new JSONObject(onTaskString[0]);
                JSONArray jsonArray = rootObj.getJSONArray("Images");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String createTS  = obj.getString("CreateTime");
                    String evntid= obj.getString("EventId");
                    String imgcaption  = obj.getString("ImageCaption");
                    String imgid= obj.getString("ImageId");
                    String srno= obj.getString("SrNo");
                    String thumbNailPath= obj.getString("fileName");
                    String uploadDate= obj.getString("uploadDate");

                    ArrayList<ParentFunCenterGalleryModel> allData =dla.GetImage(evntid);
                    boolean isPresent=false;
                    int imgId=0;
                    for (int j=0;j<allData.size();j++)
                    {
                        if (imgid.equalsIgnoreCase(allData.get(j).getImguuid()))
                        {
                            isPresent=true;
                            //imgId= Integer.valueOf(allData.get(j).getImageid());
                            break;
                        }
                    }

                    if (isPresent)
                    {
                        //dla.updateImageSyncFlag(imgId,createTS, evntid, imgcaption, imgid, srno, thumbNailPath, uploadDate);
                        //Toast.makeText(this,"Updated Funcenter events images",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        ParentFunCenterModel getEventName=dla.GetEventByID(evntid);
                        eventimagesCount++;
                        long n=0;
                        n=dla.InsertImage(createTS, evntid, imgcaption, imgid, srno, thumbNailPath, uploadDate);

                        //String newPath = new Utility().getURLImage(thumbNailPath);
                       /* if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                            new StoreBitmapImages1(newPath,newPath.split("/")[newPath.split("/").length - 1],"EventImage@@@"+getEventName.getText()+"@@@"+evntid,imgid,ManualSyncService.this).execute(newPath);
                        }*/
                        //Toast.makeText(this, "Event Image Downloaded Successfully...", Toast.LENGTH_SHORT).show();
                        if (n>0)
                        {
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                            String date = df.format(calendar.getTime());
                            //qr.deleteNotificationRow(5);
                            NotificationModel notification1 = new NotificationModel();
                            notification1.setNotificationId(5);
                            notification1.setNotificationDate(date);
                            notification1.setNotificationtype("Fun Center");
                            notification1.setMessage(getEventName.getText() + "@@" + eventimagesCount);
                            notification1.setIsRead("false");
                            notification1.setAdditionalData1("Images");

                            if(eventimagesCount>1)
                            {
                                String eventname=qr.GetNotificationEvntMsg(LastId);
                                if (getEventName.getText().toString().equals(eventname))
                                {
                                    qr.UpdateNotificationCount(notification1, LastId);
                                }
                                else {
                                    eventimagesCount=1;
                                    qr.InsertNotification(notification1);
                                    LastId=Integer.valueOf(qr.GetNotificationEvntID());
                                }
                            }
                            else {
                                qr.InsertNotification(notification1);
                                LastId=Integer.valueOf(qr.GetNotificationEvntID());
                            }

                            if(Singleton.getResultReceiver() != null)
                                Singleton.getResultReceiver().send(1,null);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (onTaskString[1].equalsIgnoreCase("AlertNdAttendance"))
        {
            Log.d("OnTaskCompleted","AlertNdAttendance");
            JSONObject rootObj = null;
            try {
                rootObj = new JSONObject(onTaskString[0]);

                JSONArray sdlist = rootObj.getJSONArray("annLst");
                for(int i =0;i<sdlist.length();i++)
                {
                    JSONObject obj = sdlist.getJSONObject(i);
                    String schoolCode= obj.getString("SchoolCode");
                    String announcementId= obj.getString("AnnouncementId");
                    String std= obj.getString("Std");
                    String division= obj.getString("division");
                    String academicYr= obj.getString("AcademicYr");
                    String announcementText= obj.getString("AnnouncementText");
                    String category= obj.getString("Category");
                    String sentBy= obj.getString("sentBy");
                    String createTS= obj.getString("createTS");
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString("LastsyncDate", createTS);
                    ArrayList<ParentGeneralCommunicationListModel> msg= dlag.GetGCTableData(student);
                    boolean isPresnet=false;
                    for (int k=0;k<msg.size();k++)
                    {
                        if (announcementText.equals(msg.get(k).getAnnouncementText()) && sentBy.equals(msg.get(k).getsentBy()))
                        {
                            isPresnet=true;
                            break;
                            //Toast.makeText(this,"Updated Alerts",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            isPresnet=false;
                        }
                    }
                    if(!isPresnet)
                    {
                        dlag.insertAnnouncementInfo(schoolCode, announcementId, std, division, academicYr, announcementText, category, sentBy, createTS);
                        //Toast.makeText(this, "Alerts Downloaded Successfully...", Toast.LENGTH_SHORT).show();
                    }
                }

                JSONArray attendList = rootObj.getJSONArray("attLst");
                String attendanceDate="",isPresent="";
                int i=attendList.length();
                for(int j=0;j<i;j++)
                {
                    JSONObject obj = attendList.getJSONObject(j);
                    attendanceDate = obj.getString("attDate");
                    isPresent=obj.getString("isPresent");
                    String timestamp="";
                    if (attendanceDate.contains("Date")) {
                        timestamp = attendanceDate.split("\\(")[1].split("\\-")[0];
                    }
                    else
                    {
                        timestamp=attendanceDate.split("-")[0];
                    }
                    if(dlag.GetDate(timestamp).equals("false"))
                    {
                        dlag.insertAttendInfo(attendanceDate.split("\\(")[1].split("\\-")[0], isPresent);
                        //Toast.makeText(this, "Attendance Downloaded Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        dlag.updateAttendanceData(attendanceDate.split("\\(")[1].split("\\-")[0],isPresent);
                        //Toast.makeText(this, "Attendance Updated Successfully...", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSON", e.toString());
            }
        }
        else  if(onTaskString[1].equalsIgnoreCase("Exception"))
        {
            Log.d("OnTaskCompleted","Exception");
            if (onTaskString[0].equalsIgnoreCase("true")) {
                long n = qr.deleteQueueRow(Integer.valueOf(onTaskString[2]), "Exception");
                if (n >= 0) {
                    ExceptionModel obj = qr.GetException(Integer.valueOf(onTaskString[2]));
                    n = qr.updateException(obj);
                }
            }
        }
        else if(onTaskString[0].equalsIgnoreCase("EventImage"))
        {
            Log.d("OnTaskCompleted","EventImage");
            if(globID.length()<=0)
            {
                globID = onTaskString[2];
            }
           // if(onTaskString[2].equalsIgnoreCase(globID)) {
            if(LastId == 0)
            {
                eventimagesCount++;
            }
            else {
                NotificationModel notificationModel = qr.GetNotificationByeventname(onTaskString[2]);
                eventimagesCount = Integer.valueOf(notificationModel.getMessage().split("@@")[1]);

                if(eventimagesCount<0)
                    eventimagesCount=0;

                eventimagesCount = eventimagesCount+1;
            }

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                String date = df.format(calendar.getTime());
                //qr.deleteNotificationRow(5);
                NotificationModel notification1 = new NotificationModel();
                notification1.setNotificationId(5);
                notification1.setNotificationDate(date);
                notification1.setNotificationtype("Fun Center");
                notification1.setMessage(onTaskString[1] + "@@" + eventimagesCount);
                notification1.setIsRead("false");
                notification1.setAdditionalData1("Images");
                notification1.setAdditionalData2(onTaskString[2]);

                if (eventimagesCount > 1) {
                    String eventname = qr.GetNotificationEvntMsg(LastId);
                    if (onTaskString[1].equals(eventname)) {
                        qr.UpdateNotificationCount(notification1, LastId);
                    } else {
                        eventimagesCount = 1;
                        qr.InsertNotification(notification1);
                        LastId = Integer.valueOf(qr.GetNotificationEvntID());
                    }
                } else {
                    qr.InsertNotification(notification1);
                    LastId = Integer.valueOf(qr.GetNotificationEvntID());
                }
                if (Singleton.getResultReceiver() != null)
                    Singleton.getResultReceiver().send(1, null);
           // }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (onTaskString[1].equalsIgnoreCase("AlertNdAttendance")) {
                    Config.alertDialog(Singleton.getContext(), "Manual Sync", "Sync Completed Successfully");
                }
            }
        });

    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void Syncdata()
    {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adbdialog = new AlertDialog.Builder(Singleton.getContext());
                adbdialog.setTitle("Manual Sync");
                adbdialog.setMessage("Sync will be Performed in Background, you will be Notified once sync is Completed.");
                adbdialog.setIcon(android.R.drawable.ic_dialog_info);
                adbdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        for (int i=1;i<=7;i++)
                        {
                            if (SyncTimeTable)
                            {
                                //time table
                                ParentTimeTableExamListModel home = new ParentTimeTableExamListModel();
                                home.setSchoolcode(UserData[2]);
                                home.setStandard(UserData[0]);
                                home.setDivision(UserData[1]);
                                home.setAcademicyear(UserData[3]);
                                home.setDate(currentDate);
                                ParentTimeTableAsyncTask obj = new ParentTimeTableAsyncTask(home,ManualSyncService.this, ManualSyncService.this);
                                obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                                SyncTimeTable=false;
                                SyncHomework=true;
                            }
                            else if (SyncHomework)
                            {
                                //homework
                                for (int k = 0; k < subjects.size(); k++) {

                                    ParentHomeworkListModel home = new ParentHomeworkListModel();
                                    home.setschoolcode(UserData[2]);
                                    home.setstandard(UserData[0]);
                                    home.setdivision(UserData[1]);
                                    /*String datear[]=currentDate.split("/");
                                    home.setHwdate(datear[1]+"/"+datear[0]+"/"+datear[2]);*/
                                    home.setHwdate(currentDate);
                                    home.setSubject(subjects.get(k));
                                    HomeworkAsyncTaskPost obj = new HomeworkAsyncTaskPost(home, ManualSyncService.this, ManualSyncService.this);
                                    obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                }

                                SyncHomework=false;
                                SyncClasswork=true;
                            }
                            else if (SyncClasswork)
                            {
                                //classwork
                                for (int k = 0; k < subjects.size(); k++) {

                                    ParentHomeworkListModel home = new ParentHomeworkListModel();
                                    home.setschoolcode(UserData[2]);
                                    home.setstandard(UserData[0]);
                                    home.setdivision(UserData[1]);
                                    /*String datear[]=currentDate.split("/");
                                    home.setHwdate(datear[1]+"/"+datear[0]+"/"+datear[2]);*/
                                    home.setHwdate(currentDate);
                                    home.setSubject(subjects.get(k));
                                    ClassworkAsyncTaskPost obj = new ClassworkAsyncTaskPost(home, ManualSyncService.this, ManualSyncService.this);
                                    obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                }

                                SyncClasswork=false;
                                SyncFuncenterEventGallery=true;
                            }
                            else if (SyncFuncenterEventGallery)
                            {
                                //Funcenter events
                                ParentFunCenterModel home = new ParentFunCenterModel();
                                home.setSchoolCode(UserData[2]);
                                home.setStd(UserData[0]);
                                home.setDiv(UserData[1]);
                                home.setAcademicYear(UserData[3]);

                                //getting current utc date
                               /*
                               //this below 2 lines for curent local time as per india
                                Calendar calendar=Calendar.getInstance();
                                String gmtTime = df1.format(calendar.getTime());*/
                                SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                                df1.setTimeZone(TimeZone.getTimeZone("utc"));
                                String gmtTime = df1.format(new Date());
                                String utcDate = gmtTime.split(" ")[0];
                                home.setEventDate(utcDate);

                                ParentFunCenterAsynckPost obj = new ParentFunCenterAsynckPost(home, ManualSyncService.this, ManualSyncService.this);
                                obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                                SyncFuncenterEventGallery=false;
                                SyncFuncenterImageGallery=true;
                            }
                            else if (SyncFuncenterImageGallery)
                            {
                                //Funcenter events images
                                ParentFunCenterModel home = new ParentFunCenterModel();
                                home.setSchoolCode(UserData[2]);
                                home.setStd(UserData[0]);
                                home.setDiv(UserData[1]);
                                home.setAcademicYear(UserData[3]);
                                //getting current utc date
                                SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                                df1.setTimeZone(TimeZone.getTimeZone("utc"));
                                String gmtTime = df1.format(new Date());
                                String utcDate = gmtTime.split(" ")[0];
                                home.setEventDate(utcDate);

                                ParentFunCenterImageAsynckPost obj = new ParentFunCenterImageAsynckPost(home,ManualSyncService.this, ManualSyncService.this);
                                obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                                SyncFuncenterImageGallery=false;
                                SyncAlertNdAttendance=true;
                            }
                            else if (SyncAlertNdAttendance) {
                                //For Alerts and attendance

                                student = preferences.getString("UidName", "");
                                String prevSyncYr = preferences.getString("SyncCurYear", "");
                                String prevSyncMt = preferences.getString("SyncCurMonth", "");
                                String prevSyncDy = preferences.getString("SyncCurDay", "");
                                String prevSyncHr = preferences.getString("SyncCurHour", "");
                                String prevSyncMn = preferences.getString("SyncCurMin", "");
                                String prevSyncSc = preferences.getString("SyncCurSecond", "");

                                //for getting current utc time
                                //DateFormat df1 = DateFormat.getTimeInstance();
                               /* Calendar calendar = Calendar.getInstance();//new line 8th nov 16
                                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");*/
                                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                df1.setTimeZone(TimeZone.getTimeZone("utc"));
                                String gmtTime = df1.format(new Date());
                                //df1.setTimeZone(TimeZone.getTimeZone("utc"));
                                //String gmtTime = df1.format(calendar.getTime());
                                String dateArr[] = gmtTime.split(" ")[0].split("-");
                                String timeArr[] = gmtTime.split(" ")[1].split(":");
                                String CurYear = dateArr[0];
                                String CurMonth = dateArr[1];
                                String curDay = dateArr[2];
                                int curHr=Integer.valueOf(timeArr[0]);
                                int curMn=Integer.valueOf(timeArr[1]);
                                int curSec=Integer.valueOf(timeArr[2]);

                                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(ManualSyncService.this);
                                String lastSyncTime = sharedpreferences.getString("LastsyncDate", "");
                                String curHour="0";
                                String curMin="0";
                                String curSecond="0";
                                if (lastSyncTime.equals("")) {
                                    curMin=String.valueOf(curMn);
                                    curSecond=String.valueOf(curSec);
                                }
                                else
                                {
                                    String ArrayOfDateTime[] = lastSyncTime.split(" ");
                                    String ArrayOfTime[] = ArrayOfDateTime[1].split(":");
                                    curMin = String.valueOf(ArrayOfTime[1]);
                                    curSecond = String.valueOf(ArrayOfTime[2]);
                                }

                                curHour = String.valueOf(curHr);

                                //System.out.println("Curent hour="+ String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))+" Current Min="+ String.valueOf(calendar.get(Calendar.MINUTE)));
                                System.out.println("Convert hour="+curHour+" Convert Min="+curMin);
                                if (prevSyncYr.equals("") && prevSyncHr.equals(""))
                                {
                                    SharedPreferences.Editor edit = preferences.edit();
                                    edit.putString("SyncCurYear", CurYear);
                                    edit.putString("SyncCurMonth", CurMonth);
                                    edit.putString("SyncCurDay", curDay);
                                    edit.putString("SyncCurHour", curHour);
                                    edit.putString("SyncCurMin", curMin);
                                    edit.putString("SyncCurSecond", curSecond);
                                    edit.commit();

                                    ParentGeneralCommunicationAsyncTaskGet obj = new
                                            ParentGeneralCommunicationAsyncTaskGet(
                                            student, UserData[2], UserData[0], UserData[1], UserData[3], ManualSyncService.this, ManualSyncService.this,
                                            CurYear,CurMonth,curDay,curHour,curMin,curSecond);
                                    try {
                                        obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        // Toast.makeText(this, getString(R.string.ManualSynckup), Toast.LENGTH_LONG).show();
                                    }
                                }
                                else
                                {
                                    ParentGeneralCommunicationAsyncTaskGet obj = new
                                            ParentGeneralCommunicationAsyncTaskGet(
                                            student, UserData[2], UserData[0], UserData[1], UserData[3], ManualSyncService.this, ManualSyncService.this,
                                            prevSyncYr,prevSyncMt,prevSyncDy,prevSyncHr,prevSyncMn,prevSyncSc);
                                    try {
                                        obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        // Toast.makeText(this, getString(R.string.ManualSynckup), Toast.LENGTH_LONG).show();
                                    }


                                    SharedPreferences.Editor edit = preferences.edit();
                                    edit.putString("SyncCurYear", CurYear);
                                    edit.putString("SyncCurMonth", CurMonth);
                                    edit.putString("SyncCurDay", curDay);
                                    edit.putString("SyncCurHour", curHour);
                                    edit.putString("SyncCurMin", curMin);
                                    edit.putString("SyncCurSecond", curSecond);
                                    edit.commit();
                                }
                                SyncAlertNdAttendance=false;
                                SyncException=true;
                            }
                            else if (SyncException)
                            {
                                final ArrayList<QueueListModel> lst = qr.GetQueueData("Exception");
                                for(int j=0;j<lst.size();j++)
                                {
                                    if(lst.get(j).getType().equals("Exception"))
                                    {
                                        ExceptionModel o = qr.GetException(lst.get(j).getId());
                                        ExceptionAsyncTaskPost obj = new ExceptionAsyncTaskPost(o, ManualSyncService.this, ManualSyncService.this);
                                        obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                    }
                                }
                                SyncException=false;
                                SyncTimeTable=true;
                            }
                        }

                    } });

                adbdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        stopService(Singleton.getManualserviceIntent());
                    } });
                adbdialog.show();

            }
        });
    }

    /*
 * Converts a specified time to different time zones
 */
    public String convert(Date dt) {
        // This prints: Date with default formatter: 2013-03-14 22:00:12 PDT
        // As my machine is in PDT time zone
        System.out.println("Date with default formatter: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(dt));

        // This prints: Date with IST time zone formatter: 2013-03-15 10:30:12 GMT+05:30
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        TimeZone tz = TimeZone.getTimeZone("UTC+0530");
        sdf.setTimeZone(tz);
        String dateIST = sdf.format(dt);
        System.out.println("Date with IST time zone formatter: " + dateIST);

        // This prints: Date CST time zone formatter: 2013-03-15 00:00:12 CDT
        tz = TimeZone.getTimeZone("UTC-0500");
        sdf.setTimeZone(tz);
        System.out.println("Date CDT time zone formatter: " + sdf.format(dt));
        return sdf.format(dt);
    }
}
