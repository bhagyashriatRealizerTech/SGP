package com.realizer.schoolgenie.parent.pupil.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.realizer.schoolgenie.parent.backend.SqliteHelper;
import com.realizer.schoolgenie.parent.utils.Singleton;

/**
 * Created by shree on 12/30/2015.
 */
public class DALMyPupilInfo {
    SQLiteDatabase db;
    SqliteHelper myhelper;
    Context context;

    public DALMyPupilInfo(Context context) {
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

    public long insertStudInfo(String userId,String pwd,String std,String division,String classrollno,String rollno,String fname
            ,String mname,String lname,String dob,String bldgrp,String fathername,String mothername,String contactno,
                               String emergencycontactno,String address,String hobbies,String comment,String isactive,String Activetill,
                               String registrationcode,String acdyear,String schoolcode,String magicword)
    {
        ContentValues conV = new ContentValues();
        conV.put("UserId", userId);
        conV.put("Pwd", pwd);
        conV.put("Std", std);
        conV.put("Division", division);
        conV.put("ClassRollNo", classrollno);
        conV.put("RollNo", rollno);
        conV.put("FName", fname);
        conV.put("MName", mname);
        conV.put("LName", lname);
        conV.put("Dob", dob);
        conV.put("Bldgrp", bldgrp);
        conV.put("FatherName", fathername);
        conV.put("MotherName", mothername);
        conV.put("ContactNo", contactno);
        conV.put("EmergencyContactNo", emergencycontactno);
        conV.put("Address", address);
        conV.put("Hobbies", hobbies);
        conV.put("Comments", comment);
        conV.put("IsActive", isactive);
        conV.put("ActiveTill", Activetill);
        conV.put("RegistrationCode", registrationcode);
        conV.put("AcademicYear", acdyear);
        conV.put("SchoolCode", schoolcode);
        conV.put("MagicWord", magicword);
        long newRowInserted = db.insert("StudentInfo", null, conV);
        return newRowInserted;
    }

    public String[] GetAllTableData(String User) {
        Cursor c = db.rawQuery("SELECT * FROM StudentInfo ", null);
        String Stud[]=new String[20];
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    String user = c.getString(c.getColumnIndex("UserId"));
                    if(user.equals(User)) {
                        Stud[0] = c.getString(c.getColumnIndex("FName"));
                        Stud[1] = c.getString(c.getColumnIndex("MName"));
                        Stud[2] = c.getString(c.getColumnIndex("LName"));
                        Stud[3] = c.getString(c.getColumnIndex("Std"));
                        Stud[4] = c.getString(c.getColumnIndex("Division"));
                        Stud[5] = c.getString(c.getColumnIndex("RollNo"));
                        Stud[6] = c.getString(c.getColumnIndex("Dob"));
                        Stud[7] = c.getString(c.getColumnIndex("Hobbies"));
                        Stud[8] = c.getString(c.getColumnIndex("Bldgrp"));
                        Stud[9] = c.getString(c.getColumnIndex("FatherName"));
                        Stud[10] = c.getString(c.getColumnIndex("ContactNo"));
                        Stud[11] = c.getString(c.getColumnIndex("Address"));
                        Stud[12] = c.getString(c.getColumnIndex("SchoolCode"));
                        Stud[13] = c.getString(c.getColumnIndex("AcademicYear"));
                        Stud[14] = null;//thumbnail
                        Stud[15] = c.getString(c.getColumnIndex("EmergencyContactNo"));
                    }
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return Stud;
    }


    public String[] GetSTDDIVData() {
        Cursor c = db.rawQuery("SELECT * FROM StudentInfo ", null);
        String Stud[]=new String[4];
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    Stud[0] = c.getString(c.getColumnIndex("Std"));
                    Stud[1] = c.getString(c.getColumnIndex("Division"));
                    Stud[2] = c.getString(c.getColumnIndex("SchoolCode"));
                    Stud[3] = c.getString(c.getColumnIndex("AcademicYear"));
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return Stud;
    }

}
