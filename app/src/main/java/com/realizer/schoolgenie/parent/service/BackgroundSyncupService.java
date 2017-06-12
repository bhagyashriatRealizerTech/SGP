package com.realizer.schoolgenie.parent.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.chat.asynctask.TeacherQueryAsyncTaskPost;
import com.realizer.schoolgenie.parent.chat.model.TeacherQuerySendModel;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.funcenter.model.QueueListModel;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shree on 1/18/2016.
 */
public class BackgroundSyncupService extends Service implements OnTaskCompleted {
    SharedPreferences sharedpreferences;
    Context context;
    DatabaseQueries qr;
    int qid;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Start", " onCreate() ");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));

        qr = new DatabaseQueries(getApplicationContext());
        int roll_min=60000;
        int total_min=3600000+roll_min;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new AutoSyncServerDataTrack(),5000, total_min);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onTaskCompleted(String s) {
        boolean b = false;

        if(s.equals("trueQuery"))
        {
            long n = qr.deleteQueueRow(qid,"Query");

            if(n>0) {
               // Toast.makeText(context, "Queue deleted Successfully", Toast.LENGTH_SHORT).show();
                TeacherQuerySendModel o = qr.GetQuery(qid);
                n = -1;
                n = qr.updateQurySyncFlag(o);
            }
        }

    }



    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d("service in onStart ", "Start");
    }

    class AutoSyncServerDataTrack extends TimerTask
    {
        @Override
        public void run() {
            initializeTimerTask();
        }
    }

    public void  initializeTimerTask() {
        Log.d("Timer", "" + Calendar.getInstance().getTime());

        if(isConnectingToInternet())
        {

           sendQueData();
        }
        else
        {
            Toast.makeText(context, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            //Utils.alertDialog(BackgroundSyncupService.this, "", Utils.actionBarTitle(getString(R.string.LoginNoInternate)).toString());
        }
    }

    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public void  sendQueData()
    {

            ArrayList<QueueListModel> lst = qr.GetQueueData("Query");

            if (lst.size() == 0) {

            } else {
                for (int num = 0; num < lst.size(); num++) {
                    TeacherQuerySendModel obj = qr.GetQuery(lst.get(num).getId());
                    qid = obj.getConversationId();
                    TeacherQueryAsyncTaskPost asyncobj = new TeacherQueryAsyncTaskPost(obj, context, BackgroundSyncupService.this);
                    asyncobj.execute();
                }
            }
        }

}
