package com.realizer.schoolgenie.parent.generalcommunication.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.realizer.schoolgenie.parent.backend.SqliteHelper;
import com.realizer.schoolgenie.parent.generalcommunication.model.ParentGeneralCommunicationListModel;
import com.realizer.schoolgenie.parent.utils.Singleton;

import java.util.ArrayList;

/**
 * Created by shree on 1/4/2016.
 */
public class DALGeneralCommunication {
    SQLiteDatabase db;
    SqliteHelper myhelper;
    Context context;

    public DALGeneralCommunication(Context context) {
        this.context = context;
        SQLiteOpenHelper myHelper =  SqliteHelper.getInstance(context);
        //this.db = myHelper.getWritableDatabase();
        this.db = Singleton.getDb();

        if(this.db == null)
        {
            this.db = myHelper.getWritableDatabase();
            Singleton.setDb(this.db);
        }
    }

    public long insertAnnouncementInfo(String schoolCode,String announcementId,String std,String division,String academicYr,
                               String announcementText,String category,String sentBy,String createts)
    {
        ContentValues conV = new ContentValues();
        conV.put("SchoolCode", schoolCode);
        conV.put("AnnouncementId", announcementId);
        conV.put("Std", std);
        conV.put("Division", division);
        conV.put("AcademicYr", academicYr);
        conV.put("AnnouncementText", announcementText);
        conV.put("Category", category);
        conV.put("SentBy", sentBy);
        conV.put("CreateTs", createts);
        long newRowInserted = db.insert("AnnouncementInfo", null, conV);
        return newRowInserted;
    }

    public long insertAttendInfo(String date,String ispresent)
    {
        ContentValues conV = new ContentValues();
        conV.put("AttendDate", date);
        conV.put("IsPresent", ispresent);
        long newRowInserted = db.insert("AttendanceInfo", null, conV);
        if(newRowInserted >= 0)
        {
            Log.d("Homework", " Done successful");
        }
        else
        {
            Log.d("Homework", "Not Done");
        }

        return newRowInserted;
    }

    public ArrayList<ParentGeneralCommunicationListModel> GetGCTableData(String User) {
        Cursor c = db.rawQuery("SELECT * FROM AnnouncementInfo  ORDER BY CreateTs DESC", null);
        ArrayList<ParentGeneralCommunicationListModel> result = new ArrayList<>();
       // int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                        ParentGeneralCommunicationListModel o = new ParentGeneralCommunicationListModel();
                        o.setCategory(c.getString(c.getColumnIndex("Category")));
                        o.setAcademicYr(c.getString(c.getColumnIndex("AcademicYr")));
                        o.setAnnouncementText(c.getString(c.getColumnIndex("AnnouncementText")));
                        o.setsentBy(c.getString(c.getColumnIndex("SentBy")));
                        o.setAnnouncementTime(c.getString(c.getColumnIndex("CreateTs")));
                        o.setAnnouncementId(c.getString(c.getColumnIndex("AnnouncementId")));
                    result.add(o);
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return result;
    }

    public ArrayList<String> GetAttDateTableData() {
        ArrayList<String> list = new ArrayList<String>();

        Cursor c = db.rawQuery("SELECT * FROM AttendanceInfo ", null);
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    list.add(c.getString(c.getColumnIndex("AttendDate"))+"@@@"+c.getString(c.getColumnIndex("IsPresent")));
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return list;
    }


    //Update Attendance Syncup Flag
    public long updateAttendanceData(String date,String isprsnt) {
        ContentValues conV = new ContentValues();
        conV.put("AttendDate", date);
        conV.put("IsPresent", isprsnt);
        //long newRowUpdate = db.update("AttendanceInfo", conV, "AttendDate=" + date, null);
        long newRowUpdate = db.update("AttendanceInfo", conV, "AttendDate=" + date, null);
        return newRowUpdate;
    }

    public String GetDate(String date)
    {
        String res="false";
        Cursor c = db.rawQuery("SELECT AttendDate FROM AttendanceInfo ", null);
        if (c != null) {
            if (c.moveToFirst()) {

                do {
                    String timestamp = date;
                    String timestamp1 = c.getString(c.getColumnIndex("AttendDate"));
                    if(timestamp.equals(timestamp1))
                    {
                        res="true";
                        break;
                    }
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return res;
    }

}
