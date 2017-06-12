package com.realizer.schoolgenie.parent.backend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shree on 12/30/2015.
 */
public class SqliteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SchoolDiaryParent";
    private static final int DATABASE_VERSION =27;
    SQLiteDatabase db;
    static Context mycontext;
    private static SqliteHelper mInstance = null;

    private static final String STUDINFO =
            "CREATE TABLE StudentInfo(  UserId TEXT," +
                    "Pwd TEXT," +
                    "Std TEXT," +
                    "Division TEXT,"+
                    "ClassRollNo TEXT,"+
                    "RollNo TEXT,"+
                    "FName TEXT,"+
                    "MName TEXT,"+
                    "LName TEXT,"+
                    "Dob TEXT,"+
                    "Bldgrp TEXT,"+
                    "FatherName TEXT,"+
                    "MotherName TEXT,"+
                    "ContactNo TEXT,"+
                    "EmergencyContactNo TEXT,"+
                    "Address TEXT,"+
                    "Hobbies TEXT,"+
                    "Comments TEXT,"+
                    "IsActive TEXT,"+
                    "ActiveTill TEXT,"+
                    "RegistrationCode TEXT,"+
                    "AcademicYear TEXT,"+
                    "SchoolCode TEXT,"+
                    "MagicWord TEXT"+
                    ")";

    private static final String ANNOUNCEMENTINFO =
            "CREATE TABLE AnnouncementInfo(  SchoolCode TEXT," +
                    "AnnouncementId TEXT," +
                    "Std TEXT," +
                    "Division TEXT,"+
                    "AcademicYr TEXT,"+
                    "AnnouncementText TEXT,"+
                    "Category TEXT,"+
                    "SentBy TEXT,"+
                    "CreateTs"+
                    ")";

    private static final String ATTENDANCEINFO =
            "CREATE TABLE AttendanceInfo(AttendDate TEXT,IsPresent TEXT)";

    private static final String CONVERSATIONINFO =
            "CREATE TABLE ConversationInfo(ConversationId TEXT,UserFrom TEXT,UserTo TEXT,UserMessage TEXT)";

    private static final String QUERYINFO =
            "CREATE TABLE QueryInfo(Stndard TEXT,TeacherName TEXT,TeacherId TEXT,Division TEXT,TeacherSubject TEXT,ThumbnailURL TEXT)";

    private static final String HOLIDAYINFO =
            "CREATE TABLE HolidayInfo(CreatedBy TEXT,Holiday TEXT,EndDate TEXT,StartDate TEXT)";

    private static final String VIEWSTARINFO =
            "CREATE TABLE ViewStarInfo(Subject TEXT,DateGiven TEXT,Comment TEXT,Teacher TEXT,TeacherId TEXT,GivenStar TEXT)";

    private static final String HOMEWORKINFO =
            "CREATE TABLE HomeworkInfo(HW_Id INTEGER PRIMARY KEY   AUTOINCREMENT,SchoolCode TEXT,Standard TEXT,Division TEXT,GivenBy TEXT,HomeworkDate TEXT,HwImage64Lst TEXT,HwTxtLst TEXT,Subject TEXT,Work TEXT,HomeworkUUID TEXT)";

    private static final String Query ="CREATE TABLE Query(QueryId INTEGER PRIMARY KEY   AUTOINCREMENT,fromTeacher TEXT,sendfrom TEXT,sendto TEXT,msg TEXT,sentTime TEXT,sentDate INTEGER,HasSyncedUp TEXT)";

    private static final String SyncUPQueue ="CREATE TABLE SyncUPQueue(Id INTEGER,Type TEXT,SyncPriority TEXT,Time TEXT)";

    private static final String TEACHERINFO =
            "CREATE TABLE TeacherInfo(Stndard TEXT,TeacherName TEXT,TeacherId TEXT,Division TEXT,TeacherSubject TEXT)";

    private static final String InitiatedChat ="CREATE TABLE InitiatedChat(Id INTEGER,Useranme TEXT,Initiated TEXT,STD TEXT,Div TEXT,Uid TEXT,UnreadCount INTEGER,ThumbnailURL TEXT)";

    private static final String RememberME ="CREATE TABLE RememberME(UName TEXT,Pwd TEXT, Flag TEXT)";

    private static final String FunCenter ="CREATE TABLE EventName(Event_Id INTEGER PRIMARY KEY   AUTOINCREMENT,createTS TEXT,evntname TEXT,eventDate TEXT,evntid TEXT,HasSyncedUp TEXT,thmbnail INTEGER,thumbNailPath TEXT)";
    private static final String FunCenter1 ="CREATE TABLE EventImage(Image_id INTEGER PRIMARY KEY   AUTOINCREMENT,createTS TEXT,evntid TEXT,imgcaption TEXT,imgid TEXT,srno TEXT,thumbNailPath TEXT,uploadDate TEXT,HasSyncedUp TEXT)";
    private static final String FunCenterSTOREIMAGES ="CREATE TABLE EventImageStore(Image_id INTEGER  PRIMARY KEY   AUTOINCREMENT,uploadimg TEXT,activityName TEXT)";
    private static final String FunCenterSTOREEvents ="CREATE TABLE EventPathStore(Event_id INTEGER  PRIMARY KEY   AUTOINCREMENT,uploadEventPath TEXT,uploadEventName TEXT)";
    private static final String FunCenterSTOREIMAGES1 ="CREATE TABLE EventImageStore1(Image_id INTEGER  PRIMARY KEY   AUTOINCREMENT,uploadimg TEXT,activityName TEXT)";
    private static final String FunCenterSTOREEvents1 ="CREATE TABLE EventPathStore1(Event_id INTEGER  PRIMARY KEY   AUTOINCREMENT,uploadEventPath TEXT,uploadEventName TEXT)";

    private static final String TimeTable ="CREATE TABLE Timetable(TTId INTEGER PRIMARY KEY   AUTOINCREMENT,AcademicYear TEXT,Description TEXT, Std TEXT,TimeTableImage TEXT,TimeTableText TEXT, UploadDate TEXT,UploadedBy TEXT,division TEXT, fileName TEXT,schoolCode TEXT)";
    private static final String Notification ="CREATE TABLE Notification(ID INTEGER PRIMARY KEY   AUTOINCREMENT,NotificationId INTEGER,Type TEXT,Message TEXT,Date TEXT,AdditionalData1 TEXT,AdditionalData2 TEXT,IsRead TEXT)";
    private static final String ExceptionHandler = "CREATE TABLE Exception(ExceptionId INTEGER PRIMARY KEY   AUTOINCREMENT,UserId TEXT,ExceptionDetails TEXT,DeviceModel TEXT,AndroidVersion TEXT,ApplicationSource TEXT,DeviceBrand TEXT,HasSyncedUp TEXT)";

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mycontext = context;
    }

    public static SqliteHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new SqliteHelper(ctx.getApplicationContext());
        }
        mycontext = ctx;
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STUDINFO);
        db.execSQL(ANNOUNCEMENTINFO);
        db.execSQL(ATTENDANCEINFO);
        db.execSQL(CONVERSATIONINFO);
        db.execSQL(QUERYINFO);
        db.execSQL(HOLIDAYINFO);
        db.execSQL(VIEWSTARINFO);
        db.execSQL(HOMEWORKINFO);
        db.execSQL(Query);
        db.execSQL(SyncUPQueue);
        db.execSQL(TEACHERINFO);
        db.execSQL(InitiatedChat);
        db.execSQL(RememberME);
        db.execSQL(FunCenter);
        db.execSQL(FunCenter1);
        db.execSQL(FunCenterSTOREIMAGES);
        db.execSQL(FunCenterSTOREEvents);
        db.execSQL(FunCenterSTOREIMAGES1);
        db.execSQL(FunCenterSTOREEvents1);
        db.execSQL(TimeTable);
        db.execSQL(Notification);
        db.execSQL(ExceptionHandler);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE if exists " + "StudentInfo");
        db.execSQL("DROP TABLE if exists " + "AnnouncementInfo");
        db.execSQL("DROP TABLE if exists " + "AttendanceInfo");
        db.execSQL("DROP TABLE if exists " + "ConversationInfo");
        db.execSQL("DROP TABLE if exists " + "QueryInfo");
        db.execSQL("DROP TABLE if exists " + "HolidayInfo");
        db.execSQL("DROP TABLE if exists " + "ViewStarInfo");
        db.execSQL("DROP TABLE if exists " + "HomeworkInfo");
        db.execSQL("DROP TABLE if exists " + "Query");
        db.execSQL("DROP TABLE if exists " + "SyncUPQueue");
        db.execSQL("DROP TABLE if exists " + "TeacherInfo");
        db.execSQL("DROP TABLE if exists " + "InitiatedChat");
        db.execSQL("DROP TABLE if exists " + "RememberME");
        db.execSQL("DROP TABLE if exists " + "EventName");
        db.execSQL("DROP TABLE if exists " + "EventImage");
        db.execSQL("DROP TABLE if exists " + "Timetable");
        db.execSQL("DROP TABLE if exists " + "EventImageStore");
        db.execSQL("DROP TABLE if exists " + "EventPathStore");
        db.execSQL("DROP TABLE if exists " + "EventImageStore1");
        db.execSQL("DROP TABLE if exists " + "EventPathStore1");
        db.execSQL("DROP TABLE if exists " + "Notification");
        db.execSQL("DROP TABLE if exists " + "Exception");
        onCreate(db);
    }
}