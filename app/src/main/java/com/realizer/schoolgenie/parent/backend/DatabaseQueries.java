package com.realizer.schoolgenie.parent.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.realizer.schoolgenie.parent.Notification.NotificationModel;
import com.realizer.schoolgenie.parent.chat.model.ParentQueriesTeacherNameListModel;
import com.realizer.schoolgenie.parent.chat.model.TeacherQuery1model;
import com.realizer.schoolgenie.parent.chat.model.TeacherQuerySendModel;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionModel;
import com.realizer.schoolgenie.parent.funcenter.model.QueueListModel;
import com.realizer.schoolgenie.parent.pupil.backend.DALMyPupilInfo;
import com.realizer.schoolgenie.parent.timetable.model.ParentTimeTableExamListModel;
import com.realizer.schoolgenie.parent.utils.Singleton;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Win on 12/21/2015.
 */
public class DatabaseQueries
{
    SQLiteDatabase db;
    Context context;
    String UserD[];
    String scode;

    public DatabaseQueries(Context context) {
        this.context = context;
        SQLiteOpenHelper myHelper =  SqliteHelper.getInstance(context);
        //this.db = myHelper.getWritableDatabase();
        this.db = Singleton.getDb();

        if(this.db == null)
        {
            this.db = myHelper.getWritableDatabase();
            Singleton.setDb(this.db);
        }
        DALMyPupilInfo dal = new DALMyPupilInfo(context);
        UserD= dal.GetSTDDIVData();
        this.scode=UserD[2];
    }

    //Insert Query Information
    public long insertQuery(String fromTeacher,String from,String to,String text,String dtime,String flag,Date sentDate)
    {
        ContentValues conV = new ContentValues();
        conV.put("fromTeacher", fromTeacher);
        conV.put("sendfrom", from);
        conV.put("sendto", to);
        conV.put("msg", text);
        conV.put("sentTime", dtime);
        conV.put("HasSyncedUp", flag);
        conV.put("sentDate" , sentDate.getTime());
        long newRowInserted = db.insert("Query", null, conV);
        return newRowInserted;
    }
    //select Query
    public TeacherQuerySendModel GetQuery(int id) {
        Cursor c = db.rawQuery("SELECT * FROM Query WHERE QueryId="+id, null);
        TeacherQuerySendModel o = new TeacherQuerySendModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setConversationId(c.getInt(c.getColumnIndex("QueryId")));
                    o.setFromTeacher(c.getString(c.getColumnIndex("fromTeacher")));
                    o.setSentTime(c.getString(c.getColumnIndex("sentTime")));
                    o.setFrom(c.getString(c.getColumnIndex("sendfrom")));
                    o.setTo(c.getString(c.getColumnIndex("sendto")));
                    o.setText(c.getString(c.getColumnIndex("msg")));
                    o.setHassync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setSchoolCode(UserD[0]);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return o;
    }

    public ArrayList<ParentQueriesTeacherNameListModel> GetQueryTableData() {
        Cursor c = db.rawQuery("SELECT * FROM InitiatedChat ", null);
        ArrayList<ParentQueriesTeacherNameListModel> result = new ArrayList<>();
        Log.d("LENGTH CURSOR", "" + c.getCount());
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    String teacher = c.getString(c.getColumnIndex("Useranme"));
                    String teacherid = c.getString(c.getColumnIndex("Uid"));
                    ParentQueriesTeacherNameListModel o = new ParentQueriesTeacherNameListModel();
                    o.setName(teacher);
                    o.setTeacherid(teacherid);
                    o.setThumbnail(c.getString(c.getColumnIndex("ThumbnailURL")));
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return result;
    }


    //select Query
    public TeacherQuerySendModel GetTeacherQuery(String id) {
        Cursor c = db.rawQuery("SELECT * FROM Query q INNER JOIN InitiatedChat c ON q.fromTeacher=c.Uid WHERE Uid="+id, null);
        TeacherQuerySendModel o = new TeacherQuerySendModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setConversationId(c.getInt(c.getColumnIndex("QueryId")));
                    o.setFromTeacher(c.getString(c.getColumnIndex("fromTeacher")));
                    o.setSentTime(c.getString(c.getColumnIndex("sentTime")));
                    o.setFrom(c.getString(c.getColumnIndex("sendfrom")));
                    o.setTo(c.getString(c.getColumnIndex("sendto")));
                    o.setText(c.getString(c.getColumnIndex("msg")));
                    o.setHassync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setSchoolCode(UserD[0]);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return o;
    }

    // Select queue Information
    public ArrayList<TeacherQuerySendModel> GetQueuryData(String uid) {
        Cursor c = db.rawQuery("SELECT * FROM Query WHERE sendfrom='"+uid+"' OR sendto='"+uid+"' ORDER BY sentTime ASC", null);
        ArrayList<TeacherQuerySendModel> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    TeacherQuerySendModel o = new TeacherQuerySendModel();
                    o.setConversationId(c.getInt(c.getColumnIndex("QueryId")));
                    o.setFromTeacher(c.getString(c.getColumnIndex("fromTeacher")));
                    o.setSentTime(c.getString(c.getColumnIndex("sentTime")));
                    o.setFrom(c.getString(c.getColumnIndex("sendfrom")));
                    o.setTo(c.getString(c.getColumnIndex("sendto")));
                    o.setText(c.getString(c.getColumnIndex("msg")));
                    o.setHassync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setSchoolCode(UserD[0]);
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return result;
    }

    // get ID
    public int getQueryId() {
        Cursor c = db.rawQuery("SELECT QueryId FROM Query ", null);
        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return att;
    }

    //Update Attendance Syncup Flag
    public long updateQurySyncFlag(TeacherQuerySendModel o) {
        ContentValues conV = new ContentValues();
        conV.put("fromTeacher", o.getFromTeacher());
        conV.put("sendfrom",o.getFrom() );
        conV.put("sendto", o.getTo());
        conV.put("msg", o.getText());
        conV.put("sentTime", o.getSentTime());
        conV.put("HasSyncedUp", "true");
        long newRowUpdate = db.update("Query",conV,"QueryId="+o.getConversationId(),null);
        return newRowUpdate;
    }

    //Insert Queue Infromation
    public long insertQueue(int id,String type,String priority,String time) {
        ContentValues conV = new ContentValues();
        conV.put("Id", id);
        conV.put("Type", type);
        conV.put("SyncPriority", priority);
        conV.put("Time", time);
        long newRowInserted = db.insert("SyncUPQueue", null, conV);
        return newRowInserted;
    }

    // Select queue Information
    public ArrayList<QueueListModel> GetQueueData(String type) {
        Cursor c = db.rawQuery("SELECT * FROM SyncUPQueue WHERE Type='"+type+"' ", null);
        ArrayList<QueueListModel> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    QueueListModel o = new QueueListModel();
                    o.setId(c.getInt(c.getColumnIndex("Id")));
                    o.setType(c.getString(c.getColumnIndex("Type")));
                    o.setPriority(c.getString(c.getColumnIndex("SyncPriority")));
                    o.setTime(c.getString(c.getColumnIndex("Time")));
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return result;
    }


    //Insert Homework data
    public long insertInitiatechat(String uname,String initiated,String uid,int unreadcount,String url)
    {

        ContentValues conV = new ContentValues();
        conV.put("Useranme", uname);
        conV.put("Initiated", initiated);
        conV.put("Uid",uid);
        conV.put("UnreadCount", unreadcount);
        conV.put("ThumbnailURL", url);
        long newRowInserted = db.insert("InitiatedChat", null, conV);

        return newRowInserted;
    }

    public long updateInitiatechat(String std,String div,String uname,String initiate,String uid,int unreadcount,String url) {
        ContentValues conV = new ContentValues();
        conV.put("Std", std);
        conV.put("Div", div);
        conV.put("Useranme", uname);
        conV.put("Initiated", initiate);
        conV.put("Uid",uid);
        conV.put("UnreadCount", unreadcount);
        conV.put("ThumbnailURL", url);
        long newRowUpdate = db.update("InitiatedChat", conV, "Uid='" + uid + "'", null);


        return newRowUpdate;
    }

    public ArrayList<TeacherQuery1model> GetInitiatedChat(String ini) {

        Cursor c = db.rawQuery("SELECT * FROM InitiatedChat WHERE Initiated='"+ini+"' ", null);
        ArrayList<TeacherQuery1model> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    TeacherQuery1model obj = new TeacherQuery1model();
                    obj.setUname(c.getString(c.getColumnIndex("Useranme")));
                    obj.setUid(c.getString(c.getColumnIndex("Uid")));
                    obj.setUnreadCount(c.getInt(c.getColumnIndex("UnreadCount")));
                    obj.setProfileImg(c.getString(c.getColumnIndex("ThumbnailURL")));
                    result.add(obj);

                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    // Select queue Information
    public TeacherQuerySendModel GetLastMessageData(String uid) {
        int quid = GetLAstMessageId(uid);
        Cursor c = db.rawQuery("SELECT * FROM Query WHERE QueryId='"+quid+"'", null);
        TeacherQuerySendModel o = new TeacherQuerySendModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setConversationId(c.getInt(c.getColumnIndex("QueryId")));
                    o.setFromTeacher(c.getString(c.getColumnIndex("fromTeacher")));
                    o.setSentTime(c.getString(c.getColumnIndex("sentTime")));
                    o.setFrom(c.getString(c.getColumnIndex("sendfrom")));
                    o.setTo(c.getString(c.getColumnIndex("sendto")));
                    o.setText(c.getString(c.getColumnIndex("msg")));
                    o.setHassync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setSentDate(c.getInt(c.getColumnIndex("sentDate")));
                    o.setSchoolCode(scode);

                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }

    // Select queue Information
    public int  GetLAstMessageId(String uid) {
        Cursor c = db.rawQuery("SELECT QueryId FROM Query WHERE sendfrom='"+uid+"' OR sendto='"+uid+"' ORDER BY sentDate " +
                " Desc", null);
        ArrayList<TeacherQuerySendModel> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    TeacherQuerySendModel o = new TeacherQuerySendModel();
                    o.setConversationId(c.getInt(c.getColumnIndex("QueryId")));
                    result.add(o);
                    break;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);

        return result.get(0).getConversationId();
    }

    /* //Insert Event Info
    public long InsertEvent(String clas,String div,String event_id,String event,String date,String thumbnail,String create_date)
    {
        //delete();
        ContentValues conV = new ContentValues();

        conV.put("Class", clas);
        conV.put("Div",div);
        conV.put("Event_Id",event_id);
        conV.put("Event",event);
        conV.put("Date",date);
        conV.put("Thumbnail",thumbnail);
        conV.put("Create_Date", create_date);
        long newRow = db.insert("EventMaster", null, conV);

        return newRow;
    }

    //Insert Event Image info
    public long InsertImage(String img_id,String event_id,String image,String upload_date,String is_upload)
    {
        //  delete();
        ContentValues conV = new ContentValues();

        conV.put("Image_id", img_id);
        conV.put("Eventid", event_id);
        conV.put("Image",image);
        conV.put("Upload_Date", upload_date);
        conV.put("Is_Uploaded", is_upload);

        long newRow1 = db.insert("EventImages", null, conV);

        return newRow1;
    }



    //Get Event Name
    public ArrayList<ParentFunCenterModel> GetEvent()
    {
        //delete();

        Cursor c = db.rawQuery("SELECT Event,Thumbnail,Event_Id FROM EventMaster ", null);

        ArrayList<ParentFunCenterModel> event = new ArrayList<ParentFunCenterModel>();
        event.clear();
        int cnt = 1;
        if (c != null && c.getCount() >0 ) {

            if ( c.moveToFirst()) {

                do {
                    ParentFunCenterModel o = new ParentFunCenterModel();
                    String eventname = c.getString(c.getColumnIndex("Event"));
                    String thumb = c.getString(c.getColumnIndex("Thumbnail"));
                    String evnt = c.getString(c.getColumnIndex("Event_Id"));


                    o.setText(eventname);
                    o.setImage(thumb);
                    o.setEventid(evnt);

                    event.add(o);

                    cnt=cnt+1;

                }
                while (c.moveToNext());
            }

        } else
        {
            // mToast("Table Has No contain");
        }
        c.close();
        //db.close();
        return event;

    }



    //Get Images
    public ArrayList<ParentFunCenterGalleryModel> GetImage(String evntid)
    {
        // delete();

        Cursor c = db.rawQuery("SELECT Image FROM EventImages WHERE Eventid = '"+evntid+"'",null);

        ArrayList<ParentFunCenterGalleryModel> images= new ArrayList<ParentFunCenterGalleryModel>();
        images.clear();
        if (c != null && c.getCount() >0)
        {

            if ( c.moveToFirst())
            {
                do {

                    ParentFunCenterGalleryModel o=new ParentFunCenterGalleryModel();

                    String image = c.getString(c.getColumnIndex("Image"));

                    o.setImage(image);

                    images.add(o);

                }
                while (c.moveToNext());
            }

        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        // db.close();
        return images;
    }
*/
    public int GetUnreadCount(String Uid) {

        Cursor c = db.rawQuery("SELECT * FROM InitiatedChat WHERE Uid='"+Uid+"' ", null);
        int result = 0;

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    result = c.getInt(c.getColumnIndex("UnreadCount"));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public ArrayList<TeacherQuery1model> GetstudList( String std,String div) {

        Cursor c = db.rawQuery("SELECT * FROM InitiatedChat WHERE Std='"+std+"' AND Div='"+div+"'", null);
        ArrayList<TeacherQuery1model> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    TeacherQuery1model obj = new TeacherQuery1model();
                    obj.setUname(c.getString(c.getColumnIndex("Useranme")));
                    obj.setUid(c.getString(c.getColumnIndex("Uid")));

                    result.add(obj);

                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }


    public String Getuname( String uid) {

        Cursor c = db.rawQuery("SELECT * FROM InitiatedChat WHERE Uid='"+uid+"' ", null);
        int cnt = 1;
        String temp ="";
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    temp = c.getString(c.getColumnIndex("Useranme"));
                    temp = temp+"@@@"+c.getString(c.getColumnIndex("ThumbnailURL"));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return temp;
    }


    //Delete Row from Queue
    public long deleteQueueRow(int id,String type)
    {
        long deleterow = db.delete("SyncUPQueue", "Id=" + id + " and Type='" + type + "'", null);
        return deleterow;
    }

    public void deleteTable()
    {
        db.delete("QueryInfo", null, null);
        //db.delete("Query",null,null);
        db.delete("SyncUPQueue",null,null);
        db.delete("InitiatedChat",null,null);
    }

    //Insert Homework data
    public long insertRememberMe(String uname,String pwd,String flag)
    {

        ContentValues conV = new ContentValues();
        conV.put("UName", uname);
        conV.put("Pwd", pwd);
        conV.put("Flag", flag);

        long newRowInserted = db.insert("RememberME", null, conV);

        return newRowInserted;
    }

    public long updateRememberMe(String uname,String pwd,String flag) {
        ContentValues conV = new ContentValues();
        conV.put("UName", uname);
        conV.put("Pwd", pwd);
        conV.put("Flag", flag);

        long newRowUpdate = db.update("RememberME", conV, null, null);

        return newRowUpdate;
    }

    public int GetRememberMeCount() {

        Cursor c = db.rawQuery("SELECT * FROM RememberME ", null);
        int cnt = 0;

        if (c != null) {
            if (c.moveToFirst()) {

                cnt = cnt+1;
            }
            while (c.moveToNext());
        }
        else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return cnt;
    }



    public String[] GetRememberMe() {

        Cursor c = db.rawQuery("SELECT * FROM RememberME ", null);
        int cnt = 1;
        String temp[] =new String[3];
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    temp[0] = c.getString(c.getColumnIndex("UName"));
                    temp[1] = c.getString(c.getColumnIndex("Pwd"));
                    temp[2] = c.getString(c.getColumnIndex("Flag"));

                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return temp;
    }

    //Insert Timetable data
    public long insertTimeTable(String academicyear,String description,String Std,String timeTableImage,String timetabletext,
                                String uploaddate,String uploadedBy,String division,String fileName,String schoolCode)
    {

        ContentValues conV = new ContentValues();
        conV.put("AcademicYear", academicyear);
        conV.put("Description", description);
        conV.put("Std", Std);
        conV.put("TimeTableImage", timeTableImage);
        conV.put("TimeTableText", timetabletext);
        conV.put("UploadDate", uploaddate);
        conV.put("UploadedBy", uploadedBy);
        conV.put("division", division);
        conV.put("fileName", fileName);
        conV.put("schoolCode", schoolCode);

        long newRowInserted = db.insert("Timetable", null, conV);
        if(newRowInserted >= 0)
        {
            Log.d("Homework ", "Insert");
        }
        else
        {
            Log.d("Homework ", "Not Insert");
        }
        return newRowInserted;
    }

    public ArrayList<ParentTimeTableExamListModel> getTimeTableData() {

        Cursor c = db.rawQuery("SELECT DISTINCT * FROM Timetable  ORDER BY UploadDate DESC", null);
        ArrayList<ParentTimeTableExamListModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    ParentTimeTableExamListModel obj = new ParentTimeTableExamListModel();
                    obj.setTtid(c.getInt(c.getColumnIndex("TTId")));
                    obj.setAcademicyear(c.getString(c.getColumnIndex("AcademicYear")));
                    obj.setDescription(c.getString(c.getColumnIndex("Description")));
                    obj.setStandard(c.getString(c.getColumnIndex("Std")));
                    obj.setImage(c.getString(c.getColumnIndex("TimeTableImage")));
                    obj.setTitle(c.getString(c.getColumnIndex("TimeTableText")));
                    obj.setDate(c.getString(c.getColumnIndex("UploadDate")));
                    obj.setTeacher(c.getString(c.getColumnIndex("UploadedBy")));
                    obj.setDivision(c.getString(c.getColumnIndex("division")));
                    obj.setFilename(c.getString(c.getColumnIndex("fileName")));
                    obj.setSchoolcode(c.getString(c.getColumnIndex("schoolCode")));
                    result.add(obj);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    //Update Event
    public long updateTimeTable(int ttid,String academicyear,String description,String Std,String timeTableImage,String timetabletext,
                                    String uploaddate,String uploadedBy,String division,String fileName,String schoolCode)
    {
        ContentValues conV = new ContentValues();
        conV.put("TTId", ttid);
        conV.put("AcademicYear", academicyear);
        conV.put("Description", description);
        conV.put("Std", Std);
        conV.put("TimeTableImage", timeTableImage);
        conV.put("TimeTableText", timetabletext);
        conV.put("UploadDate", uploaddate);
        conV.put("UploadedBy", uploadedBy);
        conV.put("division", division);
        conV.put("fileName", fileName);
        conV.put("schoolCode", schoolCode);

        long newRowUpdate = db.update("Timetable", conV, "TTId=" + ttid, null);
        return newRowUpdate;
    }

    public void deleteTimeTable()
    {
        db.delete("Timetable", null, null);

    }

    //==================================================  Notification ======================================================== //

    //Insert Event Image info
    public long InsertNotification(NotificationModel obj)
    {
        //  delete();
        ContentValues conV = new ContentValues();

        conV.put("NotificationId", obj.getNotificationId());
        conV.put("Type", obj.getNotificationtype());
        conV.put("Message",obj.getMessage());
        conV.put("Date", obj.getNotificationDate());
        conV.put("AdditionalData1", obj.getAdditionalData1());
        conV.put("AdditionalData2",obj.getAdditionalData2());
        conV.put("IsRead", obj.isRead());
        long newRow1 = db.insert("Notification", null, conV);

        return newRow1;
    }

    //Update Images
    public long UpdateNotification(NotificationModel obj)
    {
        ContentValues conV = new ContentValues();
        conV.put("ID", obj.getId());
        conV.put("NotificationId", obj.getNotificationId());
        conV.put("Type", obj.getNotificationtype());
        conV.put("Message",obj.getMessage());
        conV.put("Date", obj.getNotificationDate());
        conV.put("AdditionalData1", obj.getAdditionalData1());
        conV.put("AdditionalData2",obj.getAdditionalData2());
        conV.put("IsRead", obj.isRead());

        long newRowUpdate = db.update("Notification", conV, "ID=" + obj.getId(), null);
        return newRowUpdate;
    }

    //Delete Row from Queue

    public long deleteNotificationRow(int id)
    {
        long deleterow = db.delete("Notification", "ID=" + id, null);
        return deleterow;
    }

    // Select queue Information
    public ArrayList<NotificationModel> GetNotificationsData() {
        Cursor c = db.rawQuery("SELECT * FROM Notification ORDER BY ID DESC ", null);
        ArrayList<NotificationModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    NotificationModel o = new NotificationModel();
                    o.setId(c.getInt(c.getColumnIndex("ID")));
                    o.setNotificationId(c.getInt(c.getColumnIndex("NotificationId")));
                    o.setNotificationDate(c.getString(c.getColumnIndex("Date")));
                    o.setNotificationtype(c.getString(c.getColumnIndex("Type")));
                    o.setMessage(c.getString(c.getColumnIndex("Message")));
                    o.setIsRead(c.getString(c.getColumnIndex("IsRead")));
                    o.setAdditionalData1(c.getString(c.getColumnIndex("AdditionalData1")));
                    o.setAdditionalData2(c.getString(c.getColumnIndex("AdditionalData2")));

                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    // Select queue Information
    public NotificationModel GetNotificationById(int id) {
        Cursor c = db.rawQuery("SELECT * FROM Notification WHERE ID ="+id, null);
        int cnt = 1;
        NotificationModel result = new NotificationModel();
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    NotificationModel o = new NotificationModel();
                    o.setId(c.getInt(c.getColumnIndex("ID")));
                    o.setNotificationId(c.getInt(c.getColumnIndex("NotificationId")));
                    o.setNotificationDate(c.getString(c.getColumnIndex("Date")));
                    o.setNotificationtype(c.getString(c.getColumnIndex("Type")));
                    o.setMessage(c.getString(c.getColumnIndex("Message")));
                    o.setIsRead(c.getString(c.getColumnIndex("IsRead")));
                    o.setAdditionalData1(c.getString(c.getColumnIndex("AdditionalData1")));
                    o.setAdditionalData2(c.getString(c.getColumnIndex("AdditionalData2")));

                    result = o;
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    // Select queue Information
    public NotificationModel GetNotificationByeventname(String id) {
        Cursor c = db.rawQuery("SELECT * FROM Notification WHERE AdditionalData2 ='"+id+"'", null);
        int cnt = 1;
        NotificationModel result = new NotificationModel();
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    NotificationModel o = new NotificationModel();
                    o.setId(c.getInt(c.getColumnIndex("ID")));
                    o.setNotificationId(c.getInt(c.getColumnIndex("NotificationId")));
                    o.setNotificationDate(c.getString(c.getColumnIndex("Date")));
                    o.setNotificationtype(c.getString(c.getColumnIndex("Type")));
                    o.setMessage(c.getString(c.getColumnIndex("Message")));
                    o.setIsRead(c.getString(c.getColumnIndex("IsRead")));
                    o.setAdditionalData1(c.getString(c.getColumnIndex("AdditionalData1")));
                    o.setAdditionalData2(c.getString(c.getColumnIndex("AdditionalData2")));

                    result = o;
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }




    // Select queue Information
    public NotificationModel GetNotificationByUserId(String Uid) {
        Cursor c = db.rawQuery("SELECT * FROM Notification WHERE AdditionalData2 = '"+Uid+"'", null);
        int cnt = 1;
        NotificationModel result = new NotificationModel();
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    NotificationModel o = new NotificationModel();
                    o.setId(c.getInt(c.getColumnIndex("ID")));
                    o.setNotificationId(c.getInt(c.getColumnIndex("NotificationId")));
                    o.setNotificationDate(c.getString(c.getColumnIndex("Date")));
                    o.setNotificationtype(c.getString(c.getColumnIndex("Type")));
                    o.setMessage(c.getString(c.getColumnIndex("Message")));
                    o.setIsRead(c.getString(c.getColumnIndex("IsRead")));
                    o.setAdditionalData1(c.getString(c.getColumnIndex("AdditionalData1")));
                    o.setAdditionalData2(c.getString(c.getColumnIndex("AdditionalData2")));

                    result = o;
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            result.setNotificationId(-1);
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    //Insert Homework data
    public long insertException(ExceptionModel obj)
    {
        ContentValues conV = new ContentValues();
        conV.put("UserId", obj.getUserId());
        conV.put("ExceptionDetails", obj.getExceptionDetails());
        conV.put("DeviceModel",obj.getDeviceModel());
        conV.put("AndroidVersion", obj.getAndroidVersion());
        conV.put("ApplicationSource", obj.getApplicationSource());
        conV.put("DeviceBrand", obj.getDeviceBrand());
        conV.put("HasSyncedUp","false");
        long newRowInserted = db.insert("Exception", null, conV);

        return newRowInserted;
    }

    public long updateException(ExceptionModel obj)
    {

        ContentValues conV = new ContentValues();
        conV.put("ExceptionId", obj.getExceptionID());
        conV.put("UserId", obj.getUserId());
        conV.put("ExceptionDetails", obj.getExceptionDetails());
        conV.put("DeviceModel",obj.getDeviceModel());
        conV.put("AndroidVersion", obj.getAndroidVersion());
        conV.put("ApplicationSource", obj.getApplicationSource());
        conV.put("DeviceBrand", obj.getDeviceBrand());
        conV.put("HasSyncedUp", "true");

        long newRowUpdate = db.update("Exception", conV, "ExceptionId=" + obj.getExceptionID(), null);

        return newRowUpdate;
    }

    public ExceptionModel GetException(int ID) {

        Cursor c = db.rawQuery("SELECT * FROM Exception WHERE ExceptionId="+ID+"", null);
        ExceptionModel result = new ExceptionModel();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    ExceptionModel obj = new ExceptionModel();
                    obj.setExceptionID(c.getInt(c.getColumnIndex("ExceptionId")));
                    obj.setUserId(c.getString(c.getColumnIndex("UserId")));
                    obj.setExceptionDetails(c.getString(c.getColumnIndex("ExceptionDetails")));
                    obj.setDeviceModel(c.getString(c.getColumnIndex("DeviceModel")));
                    obj.setAndroidVersion(c.getString(c.getColumnIndex("AndroidVersion")));
                    obj.setApplicationSource(c.getString(c.getColumnIndex("ApplicationSource")));
                    obj.setDeviceBrand(c.getString(c.getColumnIndex("DeviceBrand")));
                    result = obj;

                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public int getExceptionId() {
        Cursor c = db.rawQuery("SELECT ExceptionId FROM Exception ORDER BY ExceptionId DESC LIMIT 1;", null);
        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return att;
    }


    public void deleteAllData()
    {
        long deleterow =0;
        deleterow = db.delete("StudentInfo",null, null);
        deleterow = db.delete("AnnouncementInfo",null, null);
        deleterow = db.delete("AttendanceInfo",null, null);
        deleterow = db.delete("ConversationInfo",null, null);
        deleterow = db.delete("QueryInfo",null, null);
        deleterow = db.delete("HolidayInfo",null, null);
        deleterow = db.delete("ViewStarInfo",null, null);
        deleterow = db.delete("HomeworkInfo",null, null);
        deleterow = db.delete("Query",null, null);
        deleterow = db.delete("SyncUPQueue",null, null);
        deleterow = db.delete("TeacherInfo",null, null);
        deleterow = db.delete("InitiatedChat",null, null);
        deleterow = db.delete("RememberME",null, null);
        deleterow = db.delete("EventName",null, null);
        deleterow = db.delete("EventImage",null, null);
        deleterow = db.delete("Timetable",null, null);
        deleterow = db.delete("EventImageStore",null, null);
        deleterow = db.delete("EventPathStore",null, null);
        deleterow = db.delete("EventImageStore1",null, null);
        deleterow = db.delete("EventPathStore1",null, null);
        deleterow = db.delete("Notification",null, null);
    }

    public String GetNotificationEvntMsg(Integer id) {
        Cursor c = db.rawQuery("SELECT * FROM Notification where ID="+id+"", null);
        String result ="";

        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    result=c.getString(c.getColumnIndex("Message"));
                    String[] res=result.split("@@");
                    result=res[0];
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public long UpdateNotificationCount(NotificationModel obj,int id)
    {
        ContentValues conV = new ContentValues();
        conV.put("ID", id);
        conV.put("NotificationId", obj.getNotificationId());
        conV.put("Type", obj.getNotificationtype());
        conV.put("Message",obj.getMessage());

        long newRowUpdate = db.update("Notification", conV, "ID=" + id, null);
        return newRowUpdate;
    }

    public String GetNotificationEvntID() {
        Cursor c = db.rawQuery("SELECT * FROM Notification where ID=(Select MAX(ID) from Notification)", null);
        String result ="";

        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    result=c.getString(c.getColumnIndex("ID"));
//                    String[] res=result.split("@@");
                    //                  result=res[0];
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

}
