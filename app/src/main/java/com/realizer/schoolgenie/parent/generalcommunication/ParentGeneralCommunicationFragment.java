package com.realizer.schoolgenie.parent.generalcommunication;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.generalcommunication.adapter.ParentGeneralCommunicationListAdapter;
import com.realizer.schoolgenie.parent.generalcommunication.backend.DALGeneralCommunication;
import com.realizer.schoolgenie.parent.generalcommunication.model.ParentGeneralCommunicationListModel;
import com.realizer.schoolgenie.parent.pupil.backend.DALMyPupilInfo;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.utils.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Win on 11/17/2015.
 */
public class ParentGeneralCommunicationFragment extends Fragment implements OnTaskCompleted,FragmentBackPressedListener {
    DALMyPupilInfo qr;
    DALGeneralCommunication GCdla;
    public String getValueBack;
    TextView txtClassName,division;
    String roll_no,Std,schoolCode,Year,Division;
    Intent gpsTrackerIntent;
    SharedPreferences sharedpreferences;
    ListView listHoliday;
    TextView noData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.parent_generalcommunication_layout, container, false);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Alerts", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        listHoliday = (ListView) rootView.findViewById(R.id.lsttgeneralcommunication);
        noData = (TextView) rootView.findViewById(R.id.tvNoDataMsg);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getValueBack = sharedpreferences.getString("UserName", "");
        GCdla = new DALGeneralCommunication(getActivity());
        ArrayList<ParentGeneralCommunicationListModel> msg= GCdla.GetGCTableData(getValueBack);
        if (msg.size() > 0) {
            listHoliday.setAdapter(new ParentGeneralCommunicationListAdapter(getActivity(), msg));
            listHoliday.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
        }
        else
        {
            noData.setVisibility(View.VISIBLE);
            listHoliday.setVisibility(View.GONE);
        }

        listHoliday.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listHoliday.getItemAtPosition(position);
                ParentGeneralCommunicationListModel gcommunication = (ParentGeneralCommunicationListModel) o;

                TeacherGcommunicationDetailFragment fragment = new TeacherGcommunicationDetailFragment();
                Singleton.setFragment(fragment);
                Bundle bundle = new Bundle();
                bundle.putString("CategoryName",gcommunication.getCategory());
                bundle.putString("AlertDate",gcommunication.getAnnouncementTime());
                bundle.putString("TeacherName",gcommunication.getsentBy());
                bundle.putString("AlertText",gcommunication.getAnnouncementText());
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }

    @Override
    public void onTaskCompleted(String s) {
        boolean b = false;
        if (! s.equals(""))
        {
            b = parsData(s);
        }
        if(b==true)
        {
            //Toast.makeText(getActivity(), "GC Done!!!", Toast.LENGTH_LONG).show();
            GCdla = new DALGeneralCommunication(getActivity());
            ArrayList<ParentGeneralCommunicationListModel> msg= GCdla.GetGCTableData(getValueBack);
            if (msg !=null && msg.size() > 0) {
                listHoliday.setAdapter(new ParentGeneralCommunicationListAdapter(getActivity(), msg));
            }
            Log.d("GC", "Done");
        }
    }

    public boolean parsData(String json) {
        long n =-1;
        DALGeneralCommunication dla = new DALGeneralCommunication(getActivity());

        JSONObject rootObj = null;
        Log.d("String", json);
        try {
            rootObj = new JSONObject(json);

            JSONArray sdlist = rootObj.getJSONArray("annLst");
            for(int i =0;i<sdlist.length();i++)
            {
                JSONObject obj = sdlist.getJSONObject(i);
                String schoolCode= obj.getString("SchoolCode");
                String announcementId= obj.getString("AnnouncementId");
                String std= obj.getString("Std");
                String division= obj.getString("division");
                String academicYr= obj.getString("AcademicYr");
                String announcementText= obj.getString("AnnouncementText");
                String category= obj.getString("Category");
                String sentBy= obj.getString("sentBy");
                String createTS= obj.getString("createTS");
                n =dla.insertAnnouncementInfo(schoolCode,announcementId,std,division,academicYr,announcementText,category,sentBy,createTS);
                if(n>=0)
                {
                    //Toast.makeText(getActivity(),"Announcement Inserted Successfully", Toast.LENGTH_SHORT).show();
                    n=-1;
                }
            }

            JSONArray attendList = rootObj.getJSONArray("attLst");
            String attendanceDate="",isPresent="";
            int i=attendList.length();
            for(int j=0;j<i;j++)
            {
                JSONObject obj = attendList.getJSONObject(j);
                attendanceDate = obj.getString("attDate");
                isPresent=obj.getString("isPresent");

                if(dla.GetDate(attendanceDate).equals("true")) {
                    n = dla.insertAttendInfo(attendanceDate, isPresent);
                }
                else
                {
                    n = dla.insertAttendInfo(attendanceDate, isPresent);
                   // n= dla.updateAttendanceData(attendanceDate,isPresent);
                }
                if(n>=0)
                {
                    //Toast.makeText(getActivity(),"Attendence Inserted Successfully",Toast.LENGTH_SHORT).show();
                    n=-1;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.toString());
            Log.e("GC(LocalizedMessage)", e.getLocalizedMessage());
            Log.e("GC(StackTrace)", e.getStackTrace().toString());
            Log.e("GC(Cause)", e.getCause().toString());
        }
        return true;
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
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }
}
