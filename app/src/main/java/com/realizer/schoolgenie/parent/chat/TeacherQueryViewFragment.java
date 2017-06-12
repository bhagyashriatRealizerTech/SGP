package com.realizer.schoolgenie.parent.chat;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.chat.adapter.TeacherQueryMessageCenterListAdapter;
import com.realizer.schoolgenie.parent.chat.asynctask.TeacherQueryAsyncTaskPost;
import com.realizer.schoolgenie.parent.chat.model.TeacherQuerySendModel;
import com.realizer.schoolgenie.parent.chat.model.TeacherQueryViewListModel;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.view.ProgressWheel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;


/**
 * Created by Win on 11/26/2015.
 */
public class TeacherQueryViewFragment extends Fragment implements AbsListView.OnScrollListener,OnTaskCompleted,FragmentBackPressedListener {

    DatabaseQueries qr;
    Timer timer;
    Parcelable state;
    ProgressWheel loading;
    int currentPosition;
    ListView lsttname;
    int qid;
    int mCurrentX ;
    int  mCurrentY;
    TextView send;
    EditText msg;
    int lstsize;
    String stdC;
    String divC;
    String sname;
    //TextView sendername;
    TeacherQueryMessageCenterListAdapter adapter;
    MessageResultReceiver resultReceiver;
    Context context;
    String urlImag="";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        //StrictMode for smooth list scroll
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        View rootView = inflater.inflate(R.layout.teacher_queryview_layout, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        qr =new DatabaseQueries(getActivity());
        qid=0;

        lsttname = (ListView) rootView.findViewById(R.id.lstviewquery);
        msg = (EditText) rootView.findViewById(R.id.edtmsgtxt);
        send = (TextView) rootView.findViewById(R.id.btnSendText);
        loading = (ProgressWheel)rootView.findViewById(R.id.loading);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        stdC=preferences.getString("SyncStd", "");
        divC = preferences.getString("SyncDiv", "");

        // sendername = (TextView) rootView.findViewById(R.id.txtnameq);
        Bundle b = getArguments();
        sname = b.getString("SENDERNAME");
        urlImag=b.getString("UrlImage");
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(sname, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        // sendername.setText(sname);

        ArrayList<TeacherQueryViewListModel> teachernames = GetQuery();
        lstsize = teachernames.size();

         adapter = new TeacherQueryMessageCenterListAdapter(getActivity(),teachernames);
        lsttname.setAdapter(adapter);

        lsttname.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        //lsttname.setFastScrollEnabled(true);
        //lsttname.setScrollY(lsttname.getCount());
        lsttname.setSelection(lsttname.getCount() - 1);
        //lsttname.smoothScrollToPosition(lsttname.getCount());
        lsttname.setOnScrollListener(this);
        lstsize =  teachernames.size();

        Singleton obj = Singleton.getInstance();
        resultReceiver = new MessageResultReceiver(null);
        obj.setResultReceiver(resultReceiver);



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(msg.getText().length()!=0)
                {
                    loading.setVisibility(View.VISIBLE);
                    msg.setEnabled(false);
                    Singleton.setMessageCenter(loading);
                    Bundle b = getArguments();
                    String uidstud = b.getString("USERID");
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                    String date = df.format(calendar.getTime());
                    Date sendDate =  new Date();
                    try {
                        sendDate = df.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String stud = sharedpreferences.getString("UidName", "");

                    long n = qr.insertQuery("false", stud, uidstud, msg.getText().toString(), date, "true",sendDate);
                    if (n > 0) {
                        // Toast.makeText(getActivity(), "Query Inserted Successfully", Toast.LENGTH_SHORT).show();
                        n = -1;
                        qid = qr.getQueryId();
                        n = qr.insertQueue(qid, "Query", "1", date);
                        if (n > 0) {
                            //Toast.makeText(getActivity(), "Queue Inserted Successfully", Toast.LENGTH_SHORT).show();
                            n = -1;
                            msg.setText("");
                            if(isConnectingToInternet()) {
                                TeacherQuerySendModel obj = qr.GetQuery(qid);
                                TeacherQueryAsyncTaskPost asyncobj = new TeacherQueryAsyncTaskPost(obj, getActivity(), TeacherQueryViewFragment.this);
                                asyncobj.execute();
                            }
                            else
                            {
                                resultReceiver.send(200,null);
                            }
                        }
                    }
                    Log.d("DIFICULTARR", uidstud);
                }
            }
        });

        return rootView;
    }


    private ArrayList<TeacherQueryViewListModel> GetQuery()
    {

        Bundle b = this.getArguments();
        String uid = b.getString("USERID");
        ArrayList<TeacherQueryViewListModel> results = new ArrayList<>();
        ArrayList<TeacherQuerySendModel> qlst = qr.GetQueuryData(uid);
        String tp="AM";

        for(int i=0;i<qlst.size();i++)
        {
            TeacherQuerySendModel obj = qlst.get(i);
            TeacherQueryViewListModel tDetails = new TeacherQueryViewListModel();
            String datet[] = obj.getSentTime().split(" ");
            tDetails.setSenddate(datet[0]);
            String time[] = datet[1].split(":");
            int t1 = Integer.valueOf(time[0]);
            if (t1==12)
            {
                tp = "PM";
                tDetails.setTime(""+t1+":"+time[1]+" "+tp);
            }
            else if(t1>12)
            {
                int t2 = t1-12;
                tp = "PM";
                tDetails.setTime(""+t2+":"+time[1]+" "+tp);
            }
            else
            {
                tp = "AM";
                tDetails.setTime(time[0]+":"+time[1]+" "+tp);
            }

            if(uid.equals(obj.getFrom()))
                tDetails.setFlag("T");
            else
                tDetails.setFlag("P");
            tDetails.setMsg(obj.getText());
            tDetails.setTname(obj.getFrom());
            tDetails.setProfileImage(urlImag);
            results.add(tDetails);
            Log.d("MSGTXT", obj.getText() + "  " + obj.getSentTime());

        }

        return results;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {



    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        mCurrentX = view.getScrollX();
        mCurrentY = view.getScrollY();
        currentPosition = lsttname.getSelectedItemPosition();
        Log.d("Position", "" + currentPosition);

    }


    @Override
    public void onTaskCompleted(String s) {
        if(s.equals("trueQuery"))
        {
            long n = qr.deleteQueueRow(qid,"Query");

            if(n>0)
            {
                // Toast.makeText(getActivity(), "Queue deleted Successfully", Toast.LENGTH_SHORT).show();
                TeacherQuerySendModel o = qr.GetQuery(qid);
                n=-1;

                n = qr.updateQurySyncFlag(o);

                if(n>0)
                {
                    // Toast.makeText(getActivity(), "Query updated Successfully", Toast.LENGTH_SHORT).show();
                    msg.setText("");
                    resultReceiver.send(200, null);
                }

            }
        }

    }


    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) getActivity().getSystemService(
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

    @Override
    public void onFragmentBackPressed() {
        Singleton.setFragment(Singleton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }

    class UpdateUI implements Runnable {
        String update;

        public UpdateUI(String update) {

            this.update = update;
        }

        public void run() {

            if(update.equals("RecieveMessage")) {
                ArrayList<TeacherQueryViewListModel> teachernames = GetQuery();
                Log.d("SIZEOFLST", "" + teachernames.size());
                if(teachernames.size()!=lstsize)
                {

                    Bundle b = getArguments();
                    String uid= b.getString("USERID");
                    String sname = b.getString("SENDERNAME");
                    String thumbnailurl =  b.getString("UrlImage");
                    qr.updateInitiatechat(stdC,divC,sname,"true", uid,0,thumbnailurl);

                    adapter = new TeacherQueryMessageCenterListAdapter(getActivity(), teachernames);
                    lsttname.setAdapter(adapter);
                    lsttname.setFastScrollEnabled(true);
                    lsttname.setScrollY(lsttname.getCount());
                    lsttname.setSelection(lsttname.getCount() - 1);
                    lsttname.smoothScrollToPosition(lsttname.getCount());
                    lstsize =  teachernames.size();

                }
            }

            else if(update.equals("SendMessageMessage")) {
                ArrayList<TeacherQueryViewListModel> teachernames = GetQuery();
                Log.d("SIZEOFLST", "" + teachernames.size());
                if(teachernames.size() !=lstsize)
                {
                    adapter = new TeacherQueryMessageCenterListAdapter(getActivity(), teachernames);
                    lsttname.setAdapter(adapter);
                    lsttname.setFastScrollEnabled(true);
                    lsttname.setScrollY(lsttname.getCount());
                    lsttname.setSelection(lsttname.getCount()-1);
                    lsttname.smoothScrollToPosition(lsttname.getCount());
                    lstsize =  teachernames.size();
                    loading.setVisibility(View.GONE);
                    msg.setEnabled(true);
                    Singleton.setMessageCenter(null);
                }
            }
        }
    }

    //Recive the result when new Message Arrives
    class MessageResultReceiver extends ResultReceiver
    {
        public MessageResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if(resultCode == 100){
                getActivity().runOnUiThread(new UpdateUI("RecieveMessage"));
            }
            if(resultCode == 200){
                getActivity().runOnUiThread(new UpdateUI("SendMessageMessage"));
            }

        }
    }
}
