package com.realizer.schoolgenie.parent.holiday.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.realizer.schoolgenie.parent.backend.SqliteHelper;
import com.realizer.schoolgenie.parent.holiday.model.ParentPublicHolidayListModel;
import com.realizer.schoolgenie.parent.utils.Singleton;

import java.util.ArrayList;

/**
 * Created by shree on 1/7/2016.
 */
public class DALHoliday {
    SQLiteDatabase db;
    Context context;

    public DALHoliday(Context context) {
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

    public long insertHolidayInfo(String createdby,String holiday,String enddate,String startdate)
    {
        ContentValues conV = new ContentValues();
        conV.put("CreatedBy", createdby);
        conV.put("Holiday", holiday);
        conV.put("EndDate", enddate);
        conV.put("StartDate", startdate);
        long newRowInserted = db.insert("HolidayInfo", null, conV);
        return newRowInserted;
    }

    public ArrayList<ParentPublicHolidayListModel> GetHolidayData() {
        Cursor c = db.rawQuery("SELECT * FROM HolidayInfo ORDER BY StartDate ASC", null);
        ArrayList<ParentPublicHolidayListModel> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    ParentPublicHolidayListModel o = new ParentPublicHolidayListModel();
                    o.setDesc(c.getString(c.getColumnIndex("Holiday")));
                    o.setStartDate(c.getString(c.getColumnIndex("StartDate")));
                    o.setEndDate(c.getString(c.getColumnIndex("EndDate")));
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return result;
    }
}

