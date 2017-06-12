package com.realizer.schoolgenie.parent.chat;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.chat.adapter.TeacherQueryModel1ListAdapter;
import com.realizer.schoolgenie.parent.chat.model.TeacherQuery1model;
import com.realizer.schoolgenie.parent.chat.model.TeacherQuerySendModel;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.utils.Singleton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Win on 11/26/2015.
 */
public class TeacherQueryFragment1 extends Fragment implements OnTaskCompleted,FragmentBackPressedListener {
    ArrayList<String> sendTo;
    String studinfo;
    int LisTCount;
    ListView lsttname;
    ImageButton initiate;
    int qid;
    DatabaseQueries qr;

    ArrayAdapter<String> adapter;
    MessageResultReceiver resultReceiver;
    MenuItem search,done;
    TextView noData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.teacher_queries_layout1, container, false);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Chat", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        lsttname = (ListView) rootView.findViewById(R.id.lsttname);
        initiate = (FloatingActionButton) rootView.findViewById(R.id.txtinitiatechat);

        noData = (TextView) rootView.findViewById(R.id.tvNoDataMsg);
        setHasOptionsMenu(true);
        qr =new DatabaseQueries(getActivity());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String stdC=preferences.getString("SyncStd", "");
        final String divC = preferences.getString("SyncDiv", "");

        Singleton obj = Singleton.getInstance();
        resultReceiver = new MessageResultReceiver(null);
        obj.setResultReceiver(resultReceiver);

         ArrayList<TeacherQuery1model> temp = qr.GetInitiatedChat("true");
        ArrayList<TeacherQuery1model> chat =getThreadList(temp);
        if(temp.size()!=0)
        {
            lsttname.setAdapter(new TeacherQueryModel1ListAdapter(getActivity(), chat));
            noData.setVisibility(View.GONE);
            lsttname.setVisibility(View.VISIBLE);
        }
        else
        {
            noData.setVisibility(View.VISIBLE);
            lsttname.setVisibility(View.GONE);
        }

        lsttname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object o = lsttname.getItemAtPosition(position);

                TeacherQuery1model homeworkObj = (TeacherQuery1model)o;
                String uid = homeworkObj.getUid();
                String sname = homeworkObj.getUname();

                    qr.updateInitiatechat(stdC,divC,homeworkObj.getUname(),"true",uid,0,homeworkObj.getProfileImg());

                    Bundle bundle = new Bundle();
                    bundle.putString("USERID", uid);
                    bundle.putString("SENDERNAME",sname);
                    bundle.putString("UrlImage",homeworkObj.getProfileImg());
                    TeacherQueryViewFragment fragment = new TeacherQueryViewFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    Singleton.setFragment(fragment);
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.frame_container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();


            }
        });

        initiate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
                TeacherQueryFragment fragment = new TeacherQueryFragment();
                Singleton.setFragment(fragment);
                Bundle bundle = new Bundle();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                //bundle.putString("HomeworkList", homewrklist);
                fragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_main, menu);
        done = menu.findItem(R.id.action_done);
        done.setVisible(false);
        search = menu.findItem(R.id.action_search);
        search.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }

    //Update UI
    class UpdateUI implements Runnable {
        String update;

        public UpdateUI(String update) {

            this.update = update;
        }

        public void run() {

            if(update.equals("RecieveMessage")) {
                ArrayList<TeacherQuery1model> temp = qr.GetInitiatedChat("true");
                ArrayList<TeacherQuery1model> chat =getThreadList(temp);
                if(temp.size()!=0)
                {
                    lsttname.setAdapter(new TeacherQueryModel1ListAdapter(getActivity(), chat));
                    noData.setVisibility(View.GONE);
                    lsttname.setVisibility(View.VISIBLE);
                }
                else
                {
                    noData.setVisibility(View.VISIBLE);
                    lsttname.setVisibility(View.GONE);
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

    @Override
    public void onTaskCompleted(String s) {
    }

    @Override
    public void onResume() {
        super.onResume();

        if(search != null)
            search.setVisible(false);
        if(done != null)
            done.setVisible(false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public ArrayList<TeacherQuery1model> getThreadList(ArrayList<TeacherQuery1model> userId)
    {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
        ArrayList<TeacherQuery1model> temp = new ArrayList<>();
        boolean memberPresentInList = false;
        for(int i=0;i<userId.size();i++) {

            if (null != temp)
                for (int k = 0; k < temp.size(); k++) {
                    memberPresentInList = temp.get(k).getUid().equals(userId.get(i).getUid());
                    if (memberPresentInList)
                        break;
                }

            if (!memberPresentInList) {
                TeacherQuery1model temp1 =  new TeacherQuery1model();
                TeacherQuerySendModel qlst = qr.GetLastMessageData(userId.get(i).getUid());
                temp1.setUid(userId.get(i).getUid());
                String name[] = userId.get(i).getUname().split(" ");
                String userName = "";
                for(int j=0;j<name.length;j++)
                {
                    userName = userName+" "+name[j];
                }
                temp1.setUname(userName);
                temp1.setUnreadCount(userId.get(i).getUnreadCount());
                temp1.setDate(qlst.getSentTime());
                temp1.setLastMessage(qlst.getText());
                temp1.setSendername(qlst.getFrom());
                temp1.setProfileImg(userId.get(i).getProfileImg());
                try {
                    temp1.setSenddate(df.parse(qlst.getSentTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                temp.add(temp1);
            }

        }

        Collections.sort(temp, new ChatNoCaseComparator());
        return temp;
    }

    public class ChatNoCaseComparator implements Comparator<TeacherQuery1model> {
        public int compare(TeacherQuery1model s1, TeacherQuery1model s2) {
            return s2.getSenddate().compareTo(s1.getSenddate());
        }
    }
}
