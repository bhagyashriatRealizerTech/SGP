package com.realizer.schoolgenie.parent.viewstar.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.realizer.schoolgenie.parent.backend.SqliteHelper;
import com.realizer.schoolgenie.parent.viewstar.model.ParentViewStarModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shree on 1/7/2016.
 */
public class DALViewStar {
    SQLiteDatabase db;
    SqliteHelper myhelper;
    Context context;

    public DALViewStar(Context context) {
        this.context = context;
        SQLiteOpenHelper myHelper = new SqliteHelper(context);
        this.db = myHelper.getWritableDatabase();
    }

    public long insertViewStarInfo(String subject,String dategiven,String comment,String teacher,String teacherid,String givenstar)
    {
        ContentValues conV = new ContentValues();
        conV.put("Subject", subject);
        conV.put("DateGiven", dategiven);
        conV.put("Comment", comment);
        conV.put("Teacher", teacher);
        conV.put("TeacherId", teacherid);
        conV.put("GivenStar", givenstar);

        long newRowInserted = db.insert("ViewStarInfo", null, conV);

        return newRowInserted;
    }

    public String[] GetAllViewStarTableData(String subject) {

        Cursor c = db.rawQuery("SELECT * FROM ViewStarInfo where Subject ="+"'"+subject+"'", null);
        String Stud[]=new String[100];
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                        Stud[0] = c.getString(c.getColumnIndex("Subject"));
                        Stud[1] = c.getString(c.getColumnIndex("DateGiven"));
                        Stud[2] = c.getString(c.getColumnIndex("Comment"));
                        Stud[3] = c.getString(c.getColumnIndex("Teacher"));
                        Stud[4] = c.getString(c.getColumnIndex("GivenStar"));
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return Stud;
    }

    public ArrayList<ParentViewStarModel> GetViewstarInfoData(String subject) {
        Cursor c = db.rawQuery("SELECT * FROM ViewStarInfo where Subject ="+"'"+subject+"' ORDER BY DateGiven DESC", null);
        ArrayList<ParentViewStarModel> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    ParentViewStarModel o = new ParentViewStarModel();
                    o.setcomment(c.getString(c.getColumnIndex("Comment")));
                    o.setDate(c.getString(c.getColumnIndex("DateGiven")));
                    o.setgivenStar(c.getString(c.getColumnIndex("GivenStar")));
                    o.setteachername(c.getString(c.getColumnIndex("Teacher")));

                    result.add(o);
                    cnt = cnt + 1;
                    }

                while (c.moveToNext());
            }
        }
        c.close();
        return result;
    }

    public List<String> getSubjects(){
        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT DISTINCT Subject FROM ViewStarInfo" ;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex("Subject")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}

