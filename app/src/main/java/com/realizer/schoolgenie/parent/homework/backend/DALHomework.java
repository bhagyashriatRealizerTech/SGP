package com.realizer.schoolgenie.parent.homework.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.realizer.schoolgenie.parent.backend.SqliteHelper;
import com.realizer.schoolgenie.parent.homework.model.ParentHomeworkListModel;
import com.realizer.schoolgenie.parent.utils.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shree on 1/8/2016.
 */
public class DALHomework {
    SQLiteDatabase db;
    SqliteHelper myhelper;
    Context context;

    public DALHomework(Context context) {
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

    public long insertHomeworkInfo(String schoolCode,String standard,String division,String givenBy,
                                   String homeworkDate,String hwImage64Lst,String hwTxtLst,String subject,String work,String uuid)
    {
        ContentValues conV = new ContentValues();
        conV.put("SchoolCode", schoolCode);
        conV.put("Standard", standard);
        conV.put("Division", division);
        conV.put("GivenBy", givenBy);
        conV.put("HomeworkDate", homeworkDate);
        conV.put("HwImage64Lst", hwImage64Lst);
        conV.put("HwTxtLst", hwTxtLst);
        conV.put("Subject", subject);
        conV.put("Work", work);
        conV.put("HomeworkUUID", uuid);

        long newRowInserted = db.insert("HomeworkInfo", null, conV);
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


    //Update Event
    public long updateHomeworkSyncFlag(ParentHomeworkListModel obj){
        ContentValues conV = new ContentValues();
        conV.put("HW_Id", obj.getImgId());
        conV.put("SchoolCode", obj.getschoolcode());
        conV.put("Standard", obj.getstandard());
        conV.put("Division", obj.getdivision());
        conV.put("GivenBy", obj.getgivenBy());
        conV.put("HomeworkDate", obj.getHwdate());
        conV.put("HwImage64Lst", obj.getImage());
        conV.put("HwTxtLst", obj.getHomework());
        conV.put("Subject", obj.getSubject());
        conV.put("Work", obj.getWork());
        conV.put("HomeworkUUID",obj.getHwUUID());
        long newRowUpdate = db.update("HomeworkInfo", conV, "HW_Id=" + obj.getImgId(), null);

        return newRowUpdate;
    }


    public ArrayList<ParentHomeworkListModel> GetHomeworkInfoData(String date,String work) {

        Cursor c = db.rawQuery("SELECT * FROM HomeworkInfo where HomeworkDate = "+"'"+date+"' AND Work='"+work+"' "+"ORDER BY HW_Id DESC", null);
        ArrayList<ParentHomeworkListModel> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    ParentHomeworkListModel o = new ParentHomeworkListModel();
                    o.setImgId(c.getInt(c.getColumnIndex("HW_Id")));
                    o.setschoolcode(c.getString(c.getColumnIndex("SchoolCode")));
                    o.setHomework(c.getString(c.getColumnIndex("HwTxtLst")));
                    o.setImage(c.getString(c.getColumnIndex("HwImage64Lst")));
                    o.setSubject(c.getString(c.getColumnIndex("Subject")));
                    o.setstandard(c.getString(c.getColumnIndex("Standard")));
                    o.setdivision(c.getString(c.getColumnIndex("Division")));
                    o.setgivenBy(c.getString(c.getColumnIndex("GivenBy")));
                    o.setHwdate(c.getString(c.getColumnIndex("HomeworkDate")));
                    o.setWork(c.getString(c.getColumnIndex("Work")));
                    o.setHwUUID(c.getString(c.getColumnIndex("HomeworkUUID")));
                    result.add(o);
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } /*else {
            mToast("Table Has No contain");
        }*/
        c.close();
        return result;
    }

    public ArrayList<ParentHomeworkListModel> GetHomeworkAllInfoData(String date) {

        Cursor c = db.rawQuery("SELECT * FROM HomeworkInfo where HomeworkDate = "+"'"+date+"'", null);
        ArrayList<ParentHomeworkListModel> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    ParentHomeworkListModel o = new ParentHomeworkListModel();
                    o.setImgId(c.getInt(c.getColumnIndex("HW_Id")));
                    o.setschoolcode(c.getString(c.getColumnIndex("SchoolCode")));
                    o.setHomework(c.getString(c.getColumnIndex("HwTxtLst")));
                    o.setImage(c.getString(c.getColumnIndex("HwImage64Lst")));
                    o.setSubject(c.getString(c.getColumnIndex("Subject")));
                    o.setstandard(c.getString(c.getColumnIndex("Standard")));
                    o.setdivision(c.getString(c.getColumnIndex("Division")));
                    o.setgivenBy(c.getString(c.getColumnIndex("GivenBy")));
                    o.setHwdate(c.getString(c.getColumnIndex("HomeworkDate")));
                    o.setWork(c.getString(c.getColumnIndex("Work")));
                    o.setHwUUID(c.getString(c.getColumnIndex("HomeworkUUID")));
                    result.add(o);
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } /*else {
            mToast("Table Has No contain");
        }*/
        c.close();
        return result;
    }

    public ArrayList<ParentHomeworkListModel> GetAllHomeworkByWork(String work) {

        Cursor c = db.rawQuery("SELECT * FROM HomeworkInfo where Work = "+"'"+work+"'", null);
        ArrayList<ParentHomeworkListModel> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    ParentHomeworkListModel o = new ParentHomeworkListModel();
                    o.setImgId(c.getInt(c.getColumnIndex("HW_Id")));
                    o.setschoolcode(c.getString(c.getColumnIndex("SchoolCode")));
                    o.setHomework(c.getString(c.getColumnIndex("HwTxtLst")));
                    o.setImage(c.getString(c.getColumnIndex("HwImage64Lst")));
                    o.setSubject(c.getString(c.getColumnIndex("Subject")));
                    o.setstandard(c.getString(c.getColumnIndex("Standard")));
                    o.setdivision(c.getString(c.getColumnIndex("Division")));
                    o.setgivenBy(c.getString(c.getColumnIndex("GivenBy")));
                    o.setHwdate(c.getString(c.getColumnIndex("HomeworkDate")));
                    o.setWork(c.getString(c.getColumnIndex("Work")));
                    o.setHwUUID(c.getString(c.getColumnIndex("HomeworkUUID")));
                    if (!c.getString(c.getColumnIndex("HwImage64Lst")).equalsIgnoreCase(""))
                    result.add(o);
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } /*else {
            mToast("Table Has No contain");
        }*/
        c.close();
        return result;
    }

    public boolean CheckIsDataAlreadyInDBorNot(String imgPath) {

        Cursor cursor = db.rawQuery("SELECT * FROM HomeworkInfo where HwImage64Lst = "+"'"+imgPath+"'", null);

        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public List<String> GetHomeDateTableData() {
        List<String> list = new ArrayList<String>();
        Cursor c = db.rawQuery("SELECT * FROM HomeworkInfo ", null);

        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    list.add(c.getString(c.getColumnIndex("HomeworkDate")));
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return list;
    }


    public byte []GetImageTableData(String herbs) {


        Cursor c = db.rawQuery("SELECT * FROM HomeworkInfo where HomeworkDate = "+"'"+herbs+"'", null);

        byte img[]=new byte[10];
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    img = c.getBlob(c.getColumnIndex("HwImage64Lst"));
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return img;
    }
    public ParentHomeworkListModel GetHomeworkTableData(int id) {

        Cursor c = db.rawQuery("SELECT * FROM HomeworkInfo where HW_Id="+id, null);
        //ArrayList<ParentHomeworkListModel> result = new ArrayList<>();
        ParentHomeworkListModel o = new ParentHomeworkListModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    o.setImgId(c.getInt(c.getColumnIndex("HW_Id")));
                    o.setschoolcode(c.getString(c.getColumnIndex("SchoolCode")));
                    o.setHomework(c.getString(c.getColumnIndex("HwTxtLst")));
                    o.setImage(c.getString(c.getColumnIndex("HwImage64Lst")));
                    o.setSubject(c.getString(c.getColumnIndex("Subject")));
                    o.setstandard(c.getString(c.getColumnIndex("Standard")));
                    o.setdivision(c.getString(c.getColumnIndex("Division")));
                    o.setgivenBy(c.getString(c.getColumnIndex("GivenBy")));
                    o.setHwdate(c.getString(c.getColumnIndex("HomeworkDate")));
                    o.setWork(c.getString(c.getColumnIndex("Work")));
                    o.setHwUUID(c.getString(c.getColumnIndex("HomeworkUUID")));
                    //result.add(o);
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } /*else {
            mToast("Table Has No contain");
        }*/
        c.close();
        return o ;
    }


    public ParentHomeworkListModel GetHomeworkTableId(String hwimages) {

        Cursor c = db.rawQuery("SELECT * FROM HomeworkInfo  WHERE HwImage64Lst='"+hwimages+"'", null);
        //ArrayList<ParentHomeworkListModel> result = new ArrayList<>();
        ParentHomeworkListModel o = new ParentHomeworkListModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    o.setImgId(c.getInt(c.getColumnIndex("HW_Id")));
                    o.setschoolcode(c.getString(c.getColumnIndex("SchoolCode")));
                    o.setHomework(c.getString(c.getColumnIndex("HwTxtLst")));
                    o.setImage(c.getString(c.getColumnIndex("HwImage64Lst")));
                    o.setSubject(c.getString(c.getColumnIndex("Subject")));
                    o.setstandard(c.getString(c.getColumnIndex("Standard")));
                    o.setdivision(c.getString(c.getColumnIndex("Division")));
                    o.setgivenBy(c.getString(c.getColumnIndex("GivenBy")));
                    o.setHwdate(c.getString(c.getColumnIndex("HomeworkDate")));
                    o.setWork(c.getString(c.getColumnIndex("Work")));
                    o.setHwUUID(c.getString(c.getColumnIndex("HomeworkUUID")));
                    //result.add(o);
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } /*else {
            mToast("Table Has No contain");
        }*/
        c.close();
        return o ;
    }


    public String GetLastSyncHomeworkDate() {

        Cursor c = db.rawQuery("SELECT HomeworkDate FROM HomeworkInfo ORDER BY HomeworkDate ASC", null);
        String lstdate="";
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    lstdate = c.getString(c.getColumnIndex("HomeworkDate"));

                    Log.d("SELDATE", lstdate);
                    //result.add(o);
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } /*else {
            mToast("Table Has No contain");
        }*/
        c.close();
        return lstdate ;
    }


    private void mToast(String text)
    {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

    }

    public ArrayList<String> GetHWSub(String date,String work) {

        Cursor c = db.rawQuery("SELECT Subject FROM HomeworkInfo where HomeworkDate = " + "'" + date +"' AND Work='"+work+"' ", null);

        ArrayList<String> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    String sub = c.getString(c.getColumnIndex("Subject"));
                    result.add(sub);
                    cnt = cnt + 1;

                }
                while (c.moveToNext());
            }
        } /*else {
            mToast("Table Has No contain");
        }*/
        c.close();
        return result;
    }

    public ArrayList<String> GetHWDate() {

        Cursor c = db.query(true, "HomeworkInfo", new String[] { "HomeworkDate" }, null, null, "HomeworkDate",null,"HomeworkDate",null);

        ArrayList<String> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    String sub = c.getString(c.getColumnIndex("HomeworkDate"));
                    result.add(sub);
                    cnt = cnt + 1;

                }
                while (c.moveToNext());
            }
        } /*else {
            mToast("Table Has No contain");
        }*/
        c.close();
        return result;
    }
}