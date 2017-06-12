package com.realizer.schoolgenie.parent.holiday;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.holiday.adapter.ParentPublicHolidayListAdapter;
import com.realizer.schoolgenie.parent.holiday.backend.DALHoliday;
import com.realizer.schoolgenie.parent.holiday.model.ParentPublicHolidayListModel;
import com.realizer.schoolgenie.parent.utils.Config;

import java.util.ArrayList;

/**
 * Created by Win on 11/20/2015.
 */
public class ParentPublicHolidayFragment extends Fragment implements FragmentBackPressedListener {
    DALHoliday qr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.parent_publicholiday_layout, container, false);
        setHasOptionsMenu(true);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Holiday", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        qr = new DALHoliday(getActivity());
        ArrayList<ParentPublicHolidayListModel> publiholiday = qr.GetHolidayData();
        final ListView listpublicholiday = (ListView) rootView.findViewById(R.id.lstpublicholiday);
        listpublicholiday.setAdapter(new ParentPublicHolidayListAdapter(getActivity(), publiholiday));

        return rootView;
    }

    @Override
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }
}
