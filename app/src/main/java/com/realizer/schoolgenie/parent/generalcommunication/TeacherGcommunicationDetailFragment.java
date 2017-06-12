package com.realizer.schoolgenie.parent.generalcommunication;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.chat.backend.DALQueris;
import com.realizer.schoolgenie.parent.chat.model.ParentQueriesTeacherNameListModel;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.Singleton;


/**
 * Created by Bhagyashri on 8/29/2016.
 */
public class TeacherGcommunicationDetailFragment extends Fragment implements FragmentBackPressedListener {

    TextView txtCategory,txtDate,txtTeacherName,txtDescription;

    DALQueris dbQ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.teacher_generalcommunication_detail_layout, container, false);
        initiateView(rootView);

        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");
        txtCategory.setTypeface(face);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Bundle bundle= getArguments();
        dbQ=new DALQueris(getActivity());
        ParentQueriesTeacherNameListModel result=dbQ.GetQueryTableData(bundle.getString("TeacherName"));

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Alert", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        if(bundle.getString("CategoryName").equals("CA")) {
            txtCategory.setText("Cultural Activity");
        }

        else if(bundle.getString("CategoryName").equals("SD")) {
            txtCategory.setText("Sports Day");
        }
        else if(bundle.getString("CategoryName").equals("FDC")) {
            txtCategory.setText("Fancy Dress Competitions");
        }
        else if(bundle.getString("CategoryName").equals("CM")) {
            txtCategory.setText("Class Meeting");
        }
        else
        {
            txtCategory.setText("Others");
        }

        String sdate=bundle.getString("AlertDate").split(" ")[0];
        String[] ttdate=sdate.split("/");
        int month=Integer.valueOf(ttdate[0]);
        String mon= Config.getMonth(month);

        txtDate.setText(ttdate[1]+" "+mon+" "+ttdate[2]);
        txtTeacherName.setText(result.getName());
        txtDescription.setText(bundle.getString("AlertText"));

        return rootView;
    }

    public void initiateView(View view)
    {
        txtCategory = (TextView)view.findViewById(R.id.txtcatname);
        txtDate = (TextView)view.findViewById(R.id.txtalertdate);
        txtTeacherName = (TextView)view.findViewById(R.id.txtteacherName);
        txtDescription = (TextView)view.findViewById(R.id.txtdescription);


    }


    @Override
    public void onFragmentBackPressed() {
        Singleton.setFragment(Singleton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }
}
