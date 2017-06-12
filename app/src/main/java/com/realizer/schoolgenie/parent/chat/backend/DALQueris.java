package com.realizer.schoolgenie.parent.chat.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.realizer.schoolgenie.parent.backend.SqliteHelper;
import com.realizer.schoolgenie.parent.chat.model.ParentQueriesTeacherNameListModel;
import com.realizer.schoolgenie.parent.utils.Singleton;

import java.util.ArrayList;

/**
 * Created by shree on 1/7/2016.
 */
public class DALQueris {
    SQLiteDatabase db;
    SqliteHelper myhelper;
    Context context;

    public DALQueris(Context context) {
        this.context = context;
        SQLiteOpenHelper myHelper =  SqliteHelper.getInstance(context);
       // this.db = myHelper.getWritableDatabase();
        this.db = Singleton.getDb();

        if(this.db == null)
        {
            this.db = myHelper.getWritableDatabase();
            Singleton.setDb(this.db);
        }
    }

    public long insertTeacherSubInfo(String Stndard,String teachername,String teacherid,String division,String teachersubject,String thumbnail)
    {
        ContentValues conV = new ContentValues();
        conV.put("Stndard", Stndard);
        conV.put("TeacherName", teachername);
        conV.put("TeacherId", teacherid);
        conV.put("Division", division);
        conV.put("TeacherSubject", teachersubject);
        conV.put("ThumbnailURL", thumbnail);
        long newRowInserted = db.insert("QueryInfo", null, conV);
        return newRowInserted;
    }

    public ParentQueriesTeacherNameListModel GetQueryTableData(String teacherid) {
        Cursor c = db.rawQuery("SELECT * FROM QueryInfo WHERE TeacherId='"+teacherid+"' ", null);
        ParentQueriesTeacherNameListModel result = new ParentQueriesTeacherNameListModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    result.setSubname(c.getString(c.getColumnIndex("TeacherSubject")));
                    result.setName( c.getString(c.getColumnIndex("TeacherName")));
                    result.setTeacherid(c.getString(c.getColumnIndex("TeacherId")));
                    result.setThumbnail(c.getString(c.getColumnIndex("ThumbnailURL")));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return result;
    }

    public ParentQueriesTeacherNameListModel GetTeacherData(String subject) {
        Cursor c = db.rawQuery("SELECT * FROM QueryInfo WHERE TeacherSubject='"+subject+"' ", null);
        ParentQueriesTeacherNameListModel result = new ParentQueriesTeacherNameListModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    result.setSubname(c.getString(c.getColumnIndex("TeacherSubject")));
                    result.setName( c.getString(c.getColumnIndex("TeacherName")));
                    result.setTeacherid(c.getString(c.getColumnIndex("TeacherId")));
                    result.setThumbnail(c.getString(c.getColumnIndex("ThumbnailURL")));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return result;
    }

    public ArrayList<String> GetAllSub()
    {

        Cursor c = db.rawQuery("SELECT TeacherSubject FROM QueryInfo ", null);
        ArrayList<String> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    String subject = c.getString(c.getColumnIndex("TeacherSubject"));

                    result.add(subject);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return  result;
    }
}
