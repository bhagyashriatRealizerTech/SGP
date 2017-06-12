package com.realizer.schoolgenie.parent.funcenter.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.realizer.schoolgenie.parent.backend.SqliteHelper;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterGalleryModel;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterModel;
import com.realizer.schoolgenie.parent.utils.Singleton;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Win on 03/05/2016.
 */
public class DLAFuncenter
{
    SQLiteDatabase db;
    SqliteHelper myhelper;
    Context context;

    public DLAFuncenter(Context context) {
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

    public long insertEventInfo(String createTS,String evntname,String eventDate,String evntid,String thmbnail,String thumbNailPath)
    {
        ContentValues conV = new ContentValues();
        conV.put("createTS", createTS);
        conV.put("evntname", evntname);
        conV.put("eventDate", eventDate);
        conV.put("evntid", evntid);
        conV.put("thmbnail", thmbnail);
        conV.put("thumbNailPath", thumbNailPath);
        conV.put("HasSyncedUp", "false");

        long newRowInserted = db.insert("EventName", null, conV);
        return newRowInserted;
    }

    //Update Event
    public long updateEventSyncFlag(int eventID,String createTS,String evntname,String eventDate,String evntid,String thmbnail,String thumbNailPath) {
        ContentValues conV = new ContentValues();
        conV.put("Event_Id", eventID);
        conV.put("createTS", createTS);
        conV.put("evntname", evntname);
        conV.put("eventDate", eventDate);
        conV.put("evntid", evntid);
        conV.put("thmbnail", thmbnail);
        conV.put("thumbNailPath", thumbNailPath);
        conV.put("HasSyncedUp", "true");

        long newRowUpdate = db.update("EventName", conV, "Event_Id=" + eventID, null);

        return newRowUpdate;
    }

    public void tableDelete() {

        int newrow = db.delete("EventName", null, null);
        System.out.println(newrow);
    }

    public ArrayList<ParentFunCenterModel> GetEventInfoData() {

        Cursor c = db.rawQuery("SELECT *  FROM EventName ORDER BY Event_Id DESC", null);
        ArrayList<ParentFunCenterModel> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    ParentFunCenterModel o = new ParentFunCenterModel();
                    o.setText(c.getString(c.getColumnIndex("evntname")));
                    o.setImage(c.getString(c.getColumnIndex("thumbNailPath")));
                    o.setEventUUID(c.getString(c.getColumnIndex("evntid")));
                    o.setEventid(c.getInt(c.getColumnIndex("Event_Id")));
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

    // get ID
    public int getEventId() {
        Cursor c = db.rawQuery("SELECT Event FROM EventName ORDER BY Event_Id ASC", null);
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
        else
        {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return att;
    }

    //Get Event by ID
    public ParentFunCenterModel GetEventByID(String evntid)
    {

        Cursor c = db.rawQuery("SELECT * FROM EventName WHERE evntid = '"+evntid+"'",null);
        ParentFunCenterModel o = new ParentFunCenterModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setEventid(c.getInt(c.getColumnIndex("Event_Id")));
                    o.setEventUUID(c.getString(c.getColumnIndex("evntid")));
                    o.setText(c.getString(c.getColumnIndex("evntname")));

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



    //Insert Event Image info (createTS,evntid, imgcaption, imgid,srno,thumbNailPath,uploadDate)
    public long InsertImage(String createTS,String evntid,String imgcaption,String imgid,String srno,String thumbNailPath,String uploadDate)
    {
        //  delete();
        ContentValues conV = new ContentValues();

        conV.put("createTS", createTS);
        conV.put("evntid",evntid);
        conV.put("imgcaption", imgcaption);
        conV.put("imgid", imgid);
        conV.put("srno",srno);
        conV.put("thumbNailPath",thumbNailPath);
        conV.put("uploadDate",uploadDate);
        conV.put("HasSyncedUp", "false");

        long newRow1 = db.insert("EventImage", null, conV);

        return newRow1;
    }


    //Get Images
    public ArrayList<ParentFunCenterGalleryModel> GetImage(String evntid)
    {
        Cursor c = db.rawQuery("SELECT Image_id,imgid,thumbNailPath FROM EventImage WHERE evntid = '"+evntid+"'"+"ORDER BY Image_id DESC",null);

        ArrayList<ParentFunCenterGalleryModel> images= new ArrayList<ParentFunCenterGalleryModel>();
        images.clear();
        try {
            if (c != null && c.getCount() > 0) {

                if (c.moveToFirst()) {
                    do {

                        ParentFunCenterGalleryModel o = new ParentFunCenterGalleryModel();

                        String image = c.getString(c.getColumnIndex("thumbNailPath"));
                        String imageuuid = c.getString(c.getColumnIndex("imgid"));

                        o.setImage(image);
                        o.setImguuid(imageuuid);
                        o.setImageid(c.getString(c.getColumnIndex("Image_id")));
                        images.add(o);

                    }
                    while (c.moveToNext());
                }

            } else {
                // mToast("Table Has No contain");
            }
            c.close();
            // db.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return images;
    }


    //Get Images
    public ArrayList<UUID> GetImageUUID(String evntid)
    {
        Cursor c = db.rawQuery("SELECT imgid FROM EventImage WHERE evntid = '"+evntid+"'",null);

        ArrayList<UUID> images= new ArrayList<UUID>();
        images.clear();
        try {
            if (c != null && c.getCount() > 0) {

                if (c.moveToFirst()) {
                    do {

                        images.add(UUID.fromString(c.getString(c.getColumnIndex("imgid"))));

                    }
                    while (c.moveToNext());
                }

            } else {
                // mToast("Table Has No contain");
            }
            c.close();
            // db.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return images;
    }



    //Update Images
    public long updateImageSyncFlag(int imgId,String createTS,String evntid,String imgcaption,String imgid,String srno,String thumbNailPath,String uploadDate)
    {
        ContentValues conV = new ContentValues();
        conV.put("Image_id", imgId);
        conV.put("createTS", createTS);
        conV.put("evntid",evntid);
        conV.put("imgcaption", imgcaption);
        conV.put("imgid", imgid);
        conV.put("srno",srno);
        conV.put("thumbNailPath",thumbNailPath);
        conV.put("uploadDate",uploadDate);
        conV.put("HasSyncedUp", "true");

        long newRowUpdate = db.update("EventImage", conV, "Image_id=" + imgId, null);
        return newRowUpdate;
    }

    public ParentFunCenterGalleryModel getImageById(String imageid) {
        Cursor c = db.rawQuery("SELECT * FROM EventImage WHERE imgid = '"+imageid+"'", null);
        int cnt = 1;
        ParentFunCenterGalleryModel o=new ParentFunCenterGalleryModel();
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setImageid(c.getString(c.getColumnIndex("Image_id")));
                   /* o.setEventId(c.getString(c.getColumnIndex("Eventid")));
                    o.setImage(c.getString(c.getColumnIndex("Image")));
                    o.setUpload_Date(c.getString(c.getColumnIndex("Upload_Date")));
                    o.setAcademic_year(c.getString(c.getColumnIndex("AcademicYear")));
                    o.setSrno(c.getString(c.getColumnIndex("SrNo")));
                    o.setImagecaption(c.getString(c.getColumnIndex("ImageCaption")));
                    o.setImguuid(c.getString(c.getColumnIndex("Imguuid")));*/

                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        else
        {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }


    //Delete Row from Queue
    public long deleteQueueRow(int id,String type)
    {
        long deleterow = db.delete("SyncUPQueue", "Id=" + id + " and Type='" + type + "'", null);
        return deleterow;
    }

    public long InsertEventPath(String imgid,String eventName)
    {
        ContentValues conV = new ContentValues();
        conV.put("uploadEventPath",imgid);
        conV.put("uploadEventName",eventName);
        long newRow1 = db.insert("EventPathStore", null, conV);
        if (newRow1>0)
            Toast.makeText(context, "Bitmap img inserted", Toast.LENGTH_LONG).show();

        return newRow1;
    }

    public ArrayList<ParentFunCenterModel> getEventPath()
    {
        Cursor c = db.rawQuery("SELECT DISTINCT * FROM EventPathStore ", null);
        int cnt = 1;
        ArrayList<ParentFunCenterModel> images= new ArrayList<ParentFunCenterModel>();
        images.clear();
        try {
            if (c != null && c.getCount() > 0) {

                if (c.moveToFirst()) {
                    do {

                        ParentFunCenterModel o = new ParentFunCenterModel();
                        o.setThumbnail(c.getString(c.getColumnIndex("uploadEventPath")));
                        o.setText(c.getString(c.getColumnIndex("uploadEventName")));
                        images.add(o);

                    }
                    while (c.moveToNext());
                }

            } else {
                // mToast("Table Has No contain");
            }
            c.close();
            // db.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return images;
    }

    public long InsertImagePath(String imgid,String activity)
    {
        ContentValues conV = new ContentValues();
        conV.put("uploadimg",imgid);
        conV.put("activityName",activity);

        long newRow1 = db.insert("EventImageStore", null, conV);
       /* if (newRow1>0)
            Toast.makeText(context, "Bitmap img inserted", Toast.LENGTH_LONG).show();*/

        return newRow1;
    }

    public long InsertImagePath1(String imgid,String activity)
    {
        ContentValues conV = new ContentValues();
        conV.put("uploadimg",imgid);
        conV.put("activityName",activity);

        long newRow1 = db.insert("EventImageStore1", null, conV);
       /* if (newRow1>0)
            Toast.makeText(context, "Bitmap img inserted", Toast.LENGTH_LONG).show();*/

        return newRow1;
    }

    public ArrayList<String> getImagePath1(String activity)
    {
        Cursor c = db.rawQuery("SELECT * FROM EventImageStore1 WHERE activityName='"+activity+"' ", null);
        int cnt = 1;
        ArrayList<String> images= new ArrayList<String>();
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    images.add(c.getString(c.getColumnIndex("uploadimg")));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return images;
    }

    public ArrayList<String> getImagePath(String activity)
    {
        Cursor c = db.rawQuery("SELECT * FROM EventImageStore WHERE activityName='"+activity+"' ", null);
        int cnt = 1;
        ArrayList<String> images= new ArrayList<String>();
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    images.add(c.getString(c.getColumnIndex("uploadimg")));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return images;
    }

    public  void delete()
    {
        db.delete("EventImageStore",null,null);
    }

    public  void delete1()
    {
        db.delete("EventImageStore1",null,null);
    }
}
