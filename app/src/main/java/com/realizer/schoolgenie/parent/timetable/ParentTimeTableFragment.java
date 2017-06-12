package com.realizer.schoolgenie.parent.timetable;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.pupil.backend.DALMyPupilInfo;
import com.realizer.schoolgenie.parent.timetable.adapter.ParentTimeTableExamListAdapter;
import com.realizer.schoolgenie.parent.timetable.model.ParentTimeTableExamListModel;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.utils.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Win on 11/20/2015.
 */
public class ParentTimeTableFragment extends Fragment implements FragmentBackPressedListener {

    View root;
    ListView listsyllabus;
    DALMyPupilInfo DAP;
    TextView noData;
    ArrayList<ParentTimeTableExamListModel> results;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.teacher_timetable_layout, container, false);
        root = rootView;

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Time Table", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        listsyllabus = (ListView) rootView.findViewById(R.id.lstexamsyllabus);
        noData = (TextView) root.findViewById(R.id.tvNoDataMsg);
        //progressWheel=(ProgressWheel) root.findViewById(R.id.loading);
        DAP=new DALMyPupilInfo(getActivity());
        results=new ArrayList<>();
        DatabaseQueries dla = new DatabaseQueries(getActivity());
        try {

            results =dla.getTimeTableData();
            if(results.size() > 0) {
                listsyllabus.setVisibility(View.VISIBLE);
                listsyllabus.setAdapter(new ParentTimeTableExamListAdapter(getActivity(), results));
                noData.setVisibility(View.GONE);
            }
            else
            {
                noData.setVisibility(View.VISIBLE);
                listsyllabus.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }

        listsyllabus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listsyllabus.getItemAtPosition(position);
                ParentTimeTableExamListModel homeworkObj = (ParentTimeTableExamListModel) o;

                ParentTimeTableDetailFragment fragment = new ParentTimeTableDetailFragment();
                Singleton.setFragment(fragment);
                Bundle bundle = new Bundle();
                bundle.putString("Title",homeworkObj.getTitle());
                bundle.putString("TimeTableDate",homeworkObj.getDate());
                bundle.putString("TeacherName",homeworkObj.getTeacher());
                bundle.putString("TimeTableImage",homeworkObj.getImage());
                bundle.putString("TimeTableText",homeworkObj.getDescription());
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        return rootView;

    }

    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
    public void onResume() {
        super.onResume();

        DatabaseQueries dla = new DatabaseQueries(getActivity());
        ArrayList<ParentTimeTableExamListModel> results =dla.getTimeTableData();

        if(results != null && results.size() > 0) {
            listsyllabus.setVisibility(View.VISIBLE);
            listsyllabus.setAdapter(new ParentTimeTableExamListAdapter(getActivity(), results));
            noData.setVisibility(View.GONE);
        }
        else
        {
            noData.setVisibility(View.VISIBLE);
            listsyllabus.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }
}
