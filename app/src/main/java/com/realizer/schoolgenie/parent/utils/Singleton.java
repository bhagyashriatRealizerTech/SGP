package com.realizer.schoolgenie.parent.utils;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.ResultReceiver;

import com.realizer.schoolgenie.parent.chat.model.AddedContactModel;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterGalleryModel;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterModel;
import com.realizer.schoolgenie.parent.timetable.model.ParentTimeTableExamListModel;
import com.realizer.schoolgenie.parent.view.ProgressWheel;

import java.util.ArrayList;

public class Singleton
{
    private static Singleton instance;
    public static String Rno=" ";
    public static String div=" ";
    public static String std=" ";
    public static String scode=" ";
    public static String Ayear=" ";
    public static ProgressWheel messageCenter = null;
    public static Intent autoserviceIntent = null;
    public static ResultReceiver resultReceiver;
    public static Fragment mainFragment;
    public static boolean isShowMap=false;
    public static ArrayList<ParentFunCenterModel> funcenterEvents;
    public static ArrayList<ParentFunCenterGalleryModel> funcenterEventImages;
    public static int currentMonth=0;
    public static int currentYear=2016;
    public static ArrayList<ParentTimeTableExamListModel> timetable;
    public  static SQLiteDatabase db = null;

    public static boolean isIsShowMap() {
        return isShowMap;
    }

    public static void setIsShowMap(boolean isShowMap) {
        Singleton.isShowMap = isShowMap;
    }

    public static ProgressWheel getMessageCenter() {
        return messageCenter;
    }



    public static void setMessageCenter(ProgressWheel messageCenter) {
        Singleton.messageCenter = messageCenter;
    }

    public static Intent getAutoserviceIntent() {
        return autoserviceIntent;
    }

    public static void setAutoserviceIntent(Intent autoserviceIntent) {
        Singleton.autoserviceIntent = autoserviceIntent;
    }

    public static ArrayList<AddedContactModel> getSelectedStudeonBackKeyPress() {
        return selectedStudeonBackKeyPress;
    }

    public static void setSelectedStudeonBackKeyPress(ArrayList<AddedContactModel> selectedStudeonBackKeyPress) {
        Singleton.selectedStudeonBackKeyPress = selectedStudeonBackKeyPress;
    }

    public static ArrayList<AddedContactModel> selectedStudeonBackKeyPress = new ArrayList<>();
    public static Fragment getMainFragment() {
        return mainFragment;
    }

    public static void setMainFragment(Fragment mainFragment) {
        Singleton.mainFragment = mainFragment;
    }

    public static Intent getManualserviceIntent() {
        return manualserviceIntent;
    }

    public static void setManualserviceIntent(Intent manualserviceIntent) {
        Singleton.manualserviceIntent = manualserviceIntent;
    }

    public static Intent manualserviceIntent = null;
    public static Fragment fragment;

    public static Fragment getFragment() {
        return fragment;
    }

    public static void setFragment(Fragment fragment) {
        Singleton.fragment = fragment;
    }

    public static ArrayList<AddedContactModel> getSelectedStudentList() {
        return selectedStudentList;
    }

    public static void setSelectedStudentList(ArrayList<AddedContactModel> selectedStudentList) {
        Singleton.selectedStudentList = selectedStudentList;
    }

    public static boolean isDonclick() {
        return isDonclick;
    }

    public static void setIsDonclick(boolean isDonclick) {
        Singleton.isDonclick = isDonclick;
    }

    public static ArrayList<AddedContactModel> selectedStudentList = new ArrayList<>();
    public static boolean isDonclick = Boolean.FALSE;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        Singleton.context = context;
    }

    public static Context context;

    public static void initInstance()
    {
        if (instance == null)
        {
            // Create the instance
            instance = new Singleton();
        }
    }

    public static Singleton getInstance()
    {
        // Return the instance
        initInstance();
        return instance;
    }

    private Singleton()
    {
        // Constructor hidden because this is a singleton
    }


    public static String getRno() {
        return Rno;
    }

    public static void setRno(String rno) {
        Rno = rno;
    }

    public static String getDiv() {
        return div;
    }

    public static void setDiv(String div) {
        Singleton.div = div;
    }

    public static String getStd() {
        return std;
    }

    public static void setStd(String std) {
        Singleton.std = std;
    }

    public static String getScode() {
        return scode;
    }

    public static void setScode(String scode) {
        Singleton.scode = scode;
    }

    public static String getAyear() {
        return Ayear;
    }

    public static void setAyear(String ayear) {
        Ayear = ayear;
    }

    public static ResultReceiver getResultReceiver() {
        return resultReceiver;
    }

    public static void setResultReceiver(ResultReceiver resultReceiver) {
        Singleton.resultReceiver = resultReceiver;
    }

    public static ArrayList<ParentFunCenterModel> getFuncenterEvents() {
        return funcenterEvents;
    }

    public static void setFuncenterEvents(ArrayList<ParentFunCenterModel> funcenterEvents) {
        Singleton.funcenterEvents = funcenterEvents;
    }

    public static ArrayList<ParentFunCenterGalleryModel> getFuncenterEventImages() {
        return funcenterEventImages;
    }

    public static void setFuncenterEventImages(ArrayList<ParentFunCenterGalleryModel> funcenterEventImages) {
        Singleton.funcenterEventImages = funcenterEventImages;
    }

    public static int getCurrentMonth() {
        return currentMonth;
    }

    public static void setCurrentMonth(int currentMonth) {
        Singleton.currentMonth = currentMonth;
    }

    public static int getCurrentYear() {
        return currentYear;
    }

    public static void setCurrentYear(int currentYear) {
        Singleton.currentYear = currentYear;
    }

    public static ArrayList<ParentTimeTableExamListModel> getTimetable() {
        return timetable;
    }

    public static void setTimetable(ArrayList<ParentTimeTableExamListModel> timetable) {
        Singleton.timetable = timetable;
    }

    public static SQLiteDatabase getDb() {
        return db;
    }

    public static void setDb(SQLiteDatabase db) {
        Singleton.db = db;
    }
}