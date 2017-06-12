package com.realizer.schoolgenie.parent.chat;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.chat.adapter.TeacherQueryAddedContactListAdapter;
import com.realizer.schoolgenie.parent.chat.adapter.TeacherQueryAutoCompleteListAdapter;
import com.realizer.schoolgenie.parent.chat.asynctask.TeacherQueryAsyncTaskPost;
import com.realizer.schoolgenie.parent.chat.model.AddedContactModel;
import com.realizer.schoolgenie.parent.chat.model.ParentQueriesTeacherNameListModel;
import com.realizer.schoolgenie.parent.chat.model.TeacherQueriesTeacherNameListModel;
import com.realizer.schoolgenie.parent.chat.model.TeacherQuerySendModel;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.utils.ChatSectionIndexer;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.view.ProgressWheel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Win on 11/26/2015.
 */
public class TeacherQueryFragment extends Fragment implements OnTaskCompleted,FragmentBackPressedListener {
    ArrayList<String> sendTo;
    String studinfo;
    int LisTCount;
    int qid[];
    String univsersalid;
    DatabaseQueries qr;
    TeacherQueryAddedContactListAdapter adapter;
    TeacherQueryAutoCompleteListAdapter autoCompleteAdapter;
    ImageView selectStudent;
    EditText autocomplteTextView;
    EditText message;
    TextView send;
    ListView addedStudent,nameList;
    ArrayList<AddedContactModel> teacherList,selectedList;
    MenuItem search,done;
    String stdC,divC;
    String stud;
    TextView nodata;
    ProgressWheel loading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.teacher_queries_layout, container, false);
        setHasOptionsMenu(true);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("New Message", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        stdC=sharedpreferences.getString("SyncStd", "");
        divC = sharedpreferences.getString("SyncDiv", "");
        stud = sharedpreferences.getString("UidName", "");
        initiateView(rootView);
        final TeacherQueryFragment tq;
        qr = new DatabaseQueries(getActivity());
        teacherList = new ArrayList<>();
        teacherList = GetTeacherList();

        selectedList = new ArrayList<AddedContactModel>();
        qr = new DatabaseQueries(getActivity());
        Singleton.setSelectedStudentList(selectedList);

        Collections.sort(teacherList, new ChatNoCaseComparator());
        autoCompleteAdapter = new TeacherQueryAutoCompleteListAdapter(getActivity(),teacherList);
        Config.hideSoftKeyboardWithoutReq(getActivity(), autocomplteTextView);
        Config.hideSoftKeyboardWithoutReq(getActivity(), message);
        autocomplteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() > 0) {
                    /*ArrayList<AddedContactModel> listClone = new ArrayList<AddedContactModel>();
                    for (AddedContactModel d : teacherList) {
//                        if (d.getUserName() != null && d.getUserName().toLowerCase().contains(s.toString()))
                            if (d.getUserName().toLowerCase(Locale.getDefault()).contains(s.toString()))
                            //something here
                            listClone.add(d);
                    }

                    if (listClone.size() > 0) {
                        nameList.setVisibility(View.VISIBLE);
                        autoCompleteAdapter = new TeacherQueryAutoCompleteListAdapter(getActivity(), listClone);
                        nameList.setAdapter(autoCompleteAdapter);
                    }*/
                    if (s.toString() != null)
                        new SelectContactFilter().getFilter().filter(s.toString());

                } else {
                    addedStudent.setVisibility(View.VISIBLE);
                    nameList.setVisibility(View.GONE);
                   // nodata.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = nameList.getItemAtPosition(position);

                AddedContactModel addedContactModel = (AddedContactModel) o;
                selectedList.add(addedContactModel);
                Singleton.setSelectedStudentList(selectedList);

                adapter = new TeacherQueryAddedContactListAdapter(getActivity(), selectedList);
                addedStudent.setAdapter(adapter);
                addedStudent.setVisibility(View.VISIBLE);
                nameList.setVisibility(View.GONE);
                Config.hideSoftKeyboardWithoutReq(getActivity(), autocomplteTextView);
                autocomplteTextView.setText("");
            }
        });

        selectStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ChatSectionIndexer.class);
                getActivity().startActivity(intent);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.hideSoftKeyboardWithoutReq(getActivity(), message);
                if (addedStudent.getCount() == 0) {
                    Toast.makeText(getActivity(), "No Student Added", Toast.LENGTH_SHORT).show();
                } else if (message.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Message", Toast.LENGTH_SHORT).show();
                } else {
                    // sendTo = getList();
                    if (selectedList.size() == 0 || message.getText().toString().trim().length() == 0) {

                    } else {
                        loading.setVisibility(View.VISIBLE);
                        Singleton.setMessageCenter(loading);
                        //String uidstud = "";
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                        String date = df.format(calendar.getTime());
                        Date sendDate = new Date();
                        try {
                            sendDate = df.parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // String uidstudarr[] = new String[selectedList.size()];
                        int qidcount = 0;

                        try {

                            qid = new int[selectedList.size()];

                            long n = -1;

                            for (int k = 0; k < selectedList.size(); k++) {
                                n= qr.insertQuery("false",stud,selectedList.get(k).getUserId(),message.getText().toString(),date,"true",sendDate);
                                //n = qr.insertQuery("true", uidname, selectedList.get(k).getUserId(), message.getText().toString().trim(), date, "true", sendDate);
                                if (n > 0) {
                                    // Toast.makeText(getActivity(), "Query Inserted Successfully", Toast.LENGTH_SHORT).show();
                                    n = -1;

                                    qid[qidcount] = qr.getQueryId();
                                    n = qr.insertQueue(qid[qidcount], "Query", "2", date);

                                    qidcount = qidcount + 1;

                                }
                            }

                            if(n>0) {
                                //Toast.makeText(getActivity(), "Queue Inserted Successfully", Toast.LENGTH_SHORT).show();
                                n = -1;
                                if (isConnectingToInternet())
                                {
                                    for (int i = 0; i < qid.length; i++) {
                                        TeacherQuerySendModel obj = qr.GetQuery(qid[i]);
                                        TeacherQueryAsyncTaskPost asyncobj = new TeacherQueryAsyncTaskPost(obj, getActivity(), TeacherQueryFragment.this);
                                        asyncobj.execute();
                                    }
                                }
                                else
                                {
                                  /*  String uid[] =univsersalid.split(",");
                                    for(int i=0;i<uid.length;i++)
                                    {
                                        String uname = qr.Getuname(uid[i]);
                                        n = qr.updateInitiatechat(stdC, divC.toString(), uname, "true", uid[i], 0);
                                    }*/
                                    for (int i = 0; i < selectedList.size(); i++) {
                                        String uname[] = qr.Getuname(selectedList.get(i).getUserId()).split("@@@");
                                        String imageurl = null;
                                        if(uname.length>1)
                                            imageurl = uname[1];
                                        n = qr.updateInitiatechat(stdC, divC.toString(), uname[0], "true", selectedList.get(i).getUserId(), 0,imageurl);
                                    }
                                    if (n > 0) {
                                        TeacherQueryFragment1 fragment = new TeacherQueryFragment1();
                                        Singleton.setFragment(fragment);
                                        Singleton.setMainFragment(fragment);
                                        Bundle bundle = new Bundle();
                                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                        fragment.setArguments(bundle);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.replace(R.id.frame_container, fragment);
                                        fragmentTransaction.commit();
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

        });

        /*ArrayList<TeacherQueriesTeacherNameListModel> studentname = GetStudentName();
        Bundle b = getArguments();
        int k = b.getInt("FLAG",0);
          if(k==1) {
              LisTCount =1;
              ArrayList<String> stud = new ArrayList<String>();
              for(int i=0;i<studentname.size();i++)
              {
                    stud.add(studentname.get(i).getName());
              }

              ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,stud);
              addedStudent.setAdapter(adapter);
              addedStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                      String temp = String.valueOf(addedStudent.getItemAtPosition(position));
                      open(temp);
                  }
              });
          }

        tq= this;*/

      /*  autoCompleteAdapter = new TeacherQueryAutoCompleteListAdapter(getActivity(),studentList);
        Config.hideSoftKeyboardWithoutReq(getActivity(), autocomplteTextView);
        Config.hideSoftKeyboardWithoutReq(getActivity(), message);
*/

       /* TextView select = (TextView) rootView.findViewById(R.id.txtselect);
        Button sendquery = (Button) rootView.findViewById(R.id.btnsendquery);
        final EditText msg = (EditText)rootView.findViewById(R.id.edtmessage);
        final TextView msgcount =(TextView)rootView.findViewById(R.id.txtmsgcount);*/

      /*  msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                msgcount.setText("" + msg.getText().length() + "/300");
            }
        });*/
       /* sendquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendTo = getList();
                String uidstud="";
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                String date = df.format(calendar.getTime());
                Date sendDate = new Date();
                try {
                    sendDate = df.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ArrayList<ParentQueriesTeacherNameListModel> tnamelist =  qr.GetQueryTableData();

                for(int i=0;i<sendTo.size();i++)
                {

                    String arr[] = sendTo.get(i).split("\t");

                    for(int j=0;j<tnamelist.size();j++)
                    {
                        if(arr[0].equals(tnamelist.get(j).getName()))
                        {

                            if(i==sendTo.size()-1)
                            {
                                uidstud = uidstud + tnamelist.get(j).getTeacherid();
                            }
                            else {
                                uidstud = uidstud + tnamelist.get(j).getTeacherid() + ",";
                            }
                            break;
                        }
                    }

                }

                univsersalid  = uidstud;

                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String stud = sharedpreferences.getString("UidName", "");
                String stdC=sharedpreferences.getString("SyncStd", "");
                String divC = sharedpreferences.getString("SyncDiv", "");

                long n= qr.insertQuery("false",stud,uidstud,message.getText().toString(),date,"true",sendDate);
                if(n>0)
                {
                   // Toast.makeText(getActivity(), "Query Inserted Successfully", Toast.LENGTH_SHORT).show();
                    n=-1;

                    qid = qr.getQueryId();
                    n = qr.insertQueue(qid,"Query","2",date);
                    if(n>0) {
                        //Toast.makeText(getActivity(), "Queue Inserted Successfully", Toast.LENGTH_SHORT).show();
                        n = -1;
                        if (isConnectingToInternet())
                        {
                            TeacherQuerySendModel obj = qr.GetQuery(qid);
                            TeacherQueryAsyncTaskPost asyncobj = new TeacherQueryAsyncTaskPost(obj, getActivity(), TeacherQueryFragment.this);
                            asyncobj.execute();

                        }
                        else
                        {
                            String uid[] =univsersalid.split(",");
                            for(int i=0;i<uid.length;i++)
                            {
                                String uname = qr.Getuname(uid[i]);
                                n = qr.updateInitiatechat(stdC, divC.toString(), uname, "true", uid[i], 0);
                            }
                            TeacherQueryFragment1 fragment = new TeacherQueryFragment1();
                            Bundle bundle = new Bundle();
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragment.setArguments(bundle);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.replace(R.id.frame_container,fragment);
                            fragmentTransaction.commit();
                        }
                    }
                }




            }
        });*/

       /* select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getstudinfo();
                FragmentManager fragmentManager = getFragmentManager();
                TeacherQuerySelectStudentDialogFragment newTermDialogFragment = new TeacherQuerySelectStudentDialogFragment();
                Bundle b =new Bundle();
                Bundle b1 = getArguments();
               // b.putString("NameList",studinfo);
                b.putInt("Status",1);
                b.putInt("STAT",LisTCount);
                b.putInt("FRAG",1);
                newTermDialogFragment.setArguments(b);
                newTermDialogFragment.setCancelable(false);
                newTermDialogFragment.show(fragmentManager, "Dialog!");

            }
        });*/

        return rootView;
    }
    public void setSendTo(ArrayList<String> snd)
    {
        sendTo = snd;
    }

    public class ChatNoCaseComparator implements Comparator<AddedContactModel> {
        public int compare(AddedContactModel s1, AddedContactModel s2) {
            return s1.getUserName().compareToIgnoreCase(s2.getUserName());
        }
    }

    private ArrayList<AddedContactModel> GetTeacherList()
    {
        ArrayList<AddedContactModel> results = new ArrayList<>();
        DatabaseQueries qr = new DatabaseQueries(getActivity());
        ArrayList<ParentQueriesTeacherNameListModel> tnamelst = qr.GetQueryTableData();
        boolean memberPresentInList = false;
        for(int i=0;i<tnamelst.size();i++)
        {
            if (null != results)
                for (int k = 0; k < results.size(); k++) {
                    memberPresentInList = results.get(k).getUserId().equals(tnamelst.get(i).getTeacherid());
                    if (memberPresentInList)
                        break;
                }

            if (!memberPresentInList) {
                AddedContactModel contactModel = new AddedContactModel();
                ParentQueriesTeacherNameListModel tDetails = tnamelst.get(i);
                contactModel.setUserName(tDetails.getName());
                contactModel.setUserId(tDetails.getTeacherid());
                contactModel.setProfileimage(tDetails.getThumbnail());
                results.add(contactModel);
            }
        }

        return results;
    }

    private class SelectContactFilter implements Filterable {
        @Override
        public Filter getFilter() {
            return new ListFilter();
        }
    }

    /**
     * ListFilter will work from SelectRecipientsAdapter.
     * filter class for search to employees
     */
    public class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //string  method replaceAll(" +", " ") will replace the multiple space to single space between two string
            String constraintStr = constraint.toString().trim().replaceAll(" +", " ").toLowerCase(Locale.getDefault());
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                List<AddedContactModel> filterItems = new ArrayList<>();
                synchronized (this) {
                    for (AddedContactModel item : teacherList) {
                        if (item.getUserName().toLowerCase(Locale.getDefault()).contains(constraintStr)) {
                            filterItems.add(item);
                        }
                    }
                    result.count = filterItems.size();
                    result.values = filterItems;
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<AddedContactModel> filtered = (ArrayList<AddedContactModel>) results.values;
            if (results.values != null) {
                if (filtered.isEmpty()) {
                    nodata.setVisibility(View.VISIBLE);
                    nameList.setVisibility(View.GONE);
                } else {
                    nodata.setVisibility(View.GONE);
                    nameList.setVisibility(View.VISIBLE);
                }

                autoCompleteAdapter = new TeacherQueryAutoCompleteListAdapter(getActivity(), filtered);
                nameList.setAdapter(autoCompleteAdapter);
            } else {
                nodata.setVisibility(View.VISIBLE);
                nameList.setVisibility(View.GONE);
            }
        }
    }

    private ArrayList<TeacherQueriesTeacherNameListModel> GetStudentName()
    {
        Bundle b = getArguments();
        int k = b.getInt("FLAG",0);
        ArrayList<TeacherQueriesTeacherNameListModel> results = new ArrayList<>();
        if(k==1)
        {
           ArrayList<String> temp = b.getStringArrayList("NameList");
            for(int i=0;i<temp.size();i++)
            {
                TeacherQueriesTeacherNameListModel tDetails = new TeacherQueriesTeacherNameListModel();
                tDetails.setName(temp.get(i));
                results.add(tDetails);
            }
        }
        else {

            DatabaseQueries qr = new DatabaseQueries(getActivity());
            ArrayList<ParentQueriesTeacherNameListModel> tnamelst = qr.GetQueryTableData();

            for(int i=0;i<tnamelst.size();i++)
            {
                TeacherQueriesTeacherNameListModel tDetails = new TeacherQueriesTeacherNameListModel();
                tDetails.setSubname(tnamelst.get(i).getSubname());
                tDetails.setName(tnamelst.get(i).getName());
                results.add(tDetails);

            }

          

        }
        return results;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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


    public void open(String s){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(s);
        final String temp = s;
        alertDialogBuilder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                removStud(temp);
                LisTCount =1;
                ArrayList<String> stud = getList();

                ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,stud);
                addedStudent.setAdapter(adapter);
                addedStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String temp = String.valueOf(addedStudent.getItemAtPosition(position));
                        open(temp);
                    }
                });

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public ArrayList<String> getList()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> set = preferences.getStringSet("NameList", null);
        Set<String> set1 = preferences.getStringSet("SetList", null);
        Set<String> set2 = preferences.getStringSet("SearchList", null);
        ArrayList<String> result = new ArrayList<String>();

        if (set == null) {

        } else {
            ArrayList<String> sample = new ArrayList<String>(set);
            result.addAll(sample);
        }

        if (set1 == null) {

        } else {
            ArrayList<String> sample1 = new ArrayList<String>(set1);
            result.addAll(sample1);
        }

        if (set2 == null) {

        } else {
            ArrayList<String> sample2 = new ArrayList<String>(set2);
            result.addAll(sample2);
        }
        return result;
    }

    public void removStud(String s)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> setall  = preferences.getStringSet("NameList", null);
        Set<String> setset = preferences.getStringSet("SetList", null);
        Set<String> setsearch = preferences.getStringSet("SearchList", null);
        SharedPreferences.Editor editor = preferences.edit();
        if(setall!=null && !setall.isEmpty()) {
            for (String temp : setall)
                if (s.equals(temp)) {
                    setall.remove(temp);
                    editor.putStringSet("NameList", setall);
                    break;
                }
        }

        if(setsearch!=null && !setsearch.isEmpty()) {
            for (String temp : setsearch)
                if (s.equals(temp)) {
                    setsearch.remove(temp);
                    editor.putStringSet("SearchList", setsearch);
                    break;
                }
        }
        if(setset!=null && !setset.isEmpty()) {
            for (String temp : setset)
                if (s.equals(temp)) {
                    setset.remove(temp);
                    editor.putStringSet("SetList", setset);
                    break;
                }
        }
        editor.commit();
    }


    @Override
    public void onTaskCompleted(String s) {
        Log.d("String", s);
       /* if(s.equals("trueQuery"))
        {
            long n = qr.deleteQueueRow(qid,"Query");

            if(n>0)
            {
                //Toast.makeText(getActivity(), "Queue deleted Successfully", Toast.LENGTH_SHORT).show();
                TeacherQuerySendModel o = qr.GetQuery(qid);
                n=-1;
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                final String stdC=sharedpreferences.getString("SyncStd", "");
                final String divC = sharedpreferences.getString("SyncDiv", "");
                n = qr.updateQurySyncFlag(o);

                if(n>0)
                {
                    String uid[] =univsersalid.split(",");
                    for(int i=0;i<uid.length;i++) {
                        String uname = qr.Getuname(uid[i]);
                        n = qr.updateInitiatechat(stdC, divC.toString(), uname, "true",uid[i], 0);
                    }
                    TeacherQueryFragment1 fragment = new TeacherQueryFragment1();
                    Bundle bundle = new Bundle();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame_container,fragment);
                    fragmentTransaction.commit();
                }

            }
        }
*/

        if(s.equals("trueQuery"))
        {
            for(int i=0;i<qid.length;i++)
            {
                long n = qr.deleteQueueRow(qid[i],"Query");

                if(n>0)
                {

                    TeacherQuerySendModel o = qr.GetQuery(qid[i]);
                    n=-1;
                    n = qr.updateQurySyncFlag(o);

                    if(n>0) {
                        // Toast.makeText(getActivity(), "Query updated Successfully", Toast.LENGTH_SHORT).show();
                        n = -1;
                        // for (int i = 0; i < uid.length; i++) {
                        String uname[] = qr.Getuname(o.getTo()).split("@@@");
                        String imageurl = null;
                        if(uname.length>1)
                            imageurl = uname[1];
                        n = qr.updateInitiatechat(stdC,divC.toString(), uname[0], "true",o.getTo(),0,imageurl);

                        //}
                    }
                }
            }
            loading.setVisibility(View.GONE);
            Singleton.setMessageCenter(null);

            TeacherQueryFragment1 fragment = new TeacherQueryFragment1();
            Singleton.setFragment(fragment);
            Singleton.setMainFragment(fragment);
            Bundle bundle = new Bundle();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragment.setArguments(bundle);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.frame_container,fragment);
            fragmentTransaction.commit();
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

    public void initiateView(View view)
    {
        selectStudent = (ImageView) view.findViewById(R.id.imgbtnAddContact);
        autocomplteTextView = (EditText) view.findViewById(R.id.edt_select_contact);
        send = (TextView) view.findViewById(R.id.btnSendText);
        message = (EditText)view.findViewById(R.id.edtmsgtxt);
        addedStudent = (ListView) view.findViewById(R.id.ivaddedContact);
        nameList = (ListView)view.findViewById(R.id.lvstudentnamelist);
        nodata=(TextView) view.findViewById(R.id.noDataFound);
        loading = (ProgressWheel)view.findViewById(R.id.loading);
    }

    @Override
    public void onFragmentBackPressed() {
        Singleton.setFragment(Singleton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Singleton.isDonclick())
        {
            Singleton.setIsDonclick(Boolean.FALSE);
            if(Singleton.getSelectedStudentList() != null ) {
                selectedList = Singleton.getSelectedStudentList();
                adapter = new TeacherQueryAddedContactListAdapter(getActivity(), selectedList);
                if(Singleton.getSelectedStudentList().size()>0)
                    addedStudent.setAdapter(adapter);
                else
                    addedStudent.setAdapter(null);
                addedStudent.setVisibility(View.VISIBLE);
                nameList.setVisibility(View.GONE);
                nodata.setVisibility(View.GONE);
            }
            else
            {
                nodata.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            selectedList = Singleton.getSelectedStudentList();
        }

        if(done != null)
            done.setVisible(false);
        if(search != null)
            search.setVisible(false);


        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}
