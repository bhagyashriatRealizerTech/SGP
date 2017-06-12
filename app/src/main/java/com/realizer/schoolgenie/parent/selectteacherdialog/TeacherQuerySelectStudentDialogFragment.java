package com.realizer.schoolgenie.parent.selectteacherdialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.chat.TeacherQueryFragment;
import com.realizer.schoolgenie.parent.chat.model.TeacherQuery1model;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.pupil.backend.DALMyPupilInfo;
import com.realizer.schoolgenie.parent.selectteacherdialog.adapter.TeacherMyClassStudentListAdapter;
import com.realizer.schoolgenie.parent.selectteacherdialog.model.TeacherMyClassStudentModel;

import java.util.ArrayList;
import java.util.Set;


public class TeacherQuerySelectStudentDialogFragment extends DialogFragment {
    ArrayList<String> mAllData;
    ArrayAdapter<String> mAdapter;
    ArrayList<TeacherMyClassStudentModel> studentNameList;
    Button seletedstud;
    ArrayList<String> sendTo;
    ListView lststudentname;
    int stat;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.teacher_queryview_selectstudent_layout, null);
        lststudentname = (ListView) view.findViewById(R.id.lsttselctstudentname);
        seletedstud = (Button) view.findViewById(R.id.btnselectstud);
        sendTo = new ArrayList<String>();
        Bundle b1 = getArguments();
        stat = b1.getInt("STAT",0);
          Log.d("STAT", "" + stat);
        if (b1.getInt("Status", 0) == 1) {
            //setData();

                studentNameList = GetStudentName();



            TeacherQuerySelectStudentDialogFragment tq = new TeacherQuerySelectStudentDialogFragment();
            lststudentname.setAdapter(new TeacherMyClassStudentListAdapter(getActivity(), studentNameList, "All", tq,getList(),stat));
            final EditText inputsearch = (EditText) view.findViewById(R.id.inputSearch);
            inputsearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (0 != inputsearch.getText().length()) {
                        String spnId = inputsearch.getText().toString();
                        setSearchResult(spnId);
                    } else {
                        setData();
                    }

                }
            });

            seletedstud.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<String> result = getList();

                   // Toast.makeText(getActivity(), " " + result.size(), Toast.LENGTH_SHORT).show();
                    Fragment fragment =null;


                    Bundle bundle = new Bundle();
                    Bundle b = getArguments();
                    if(b.getInt("FRAG",0)==1){
                       fragment = new TeacherQueryFragment();
                    }


                    bundle.putStringArrayList("NameList",result);
                    bundle.putInt("FLAG",1);
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame_container,fragment);
                    fragmentTransaction.commit();

                    dismiss();

                }

            });

            builder.setTitle("Select Student");
            builder.setView(view);
        }
            return builder.create();
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


    public void setData() {

        Bundle b1 =getArguments();
            if(b1.getInt("FRAG",0)==1) {
                studentNameList = GetStudentName();
            }
            else  if(b1.getInt("FRAG",0)==2){
                studentNameList = GetStudentName1();
            }
        TeacherQuerySelectStudentDialogFragment tq = new TeacherQuerySelectStudentDialogFragment();
        mAllData = new ArrayList<String>();
        for(int i=0;i<studentNameList.size();i++)
        {
            mAllData.add(studentNameList.get(i).getSrnoStd());
            mAllData.add(studentNameList.get(i).getStdName());
        }
        lststudentname.setAdapter(new TeacherMyClassStudentListAdapter(getActivity(),studentNameList,"set",tq,getList(),stat));


    }
    public void setSendTo(ArrayList<String> snd)
    {
        sendTo = snd;
        Log.d("Size", "" + sendTo.size());
    }

    public void setSearchResult(String str) {
        ArrayList mSearch = new ArrayList<TeacherMyClassStudentModel>();
        Bundle b1 =getArguments();
        if(b1.getInt("FRAG",0)==1) {
            studentNameList = GetStudentName();
        }
        else  if(b1.getInt("FRAG",0)==2){
            studentNameList = GetStudentName1();
        }
        TeacherQuerySelectStudentDialogFragment tq = new TeacherQuerySelectStudentDialogFragment();
        mAllData = new ArrayList<>();
        for(int i=0;i<studentNameList.size();i++)
        {

            mAllData.add (studentNameList.get(i).getStdName().toString());
        }


        for (String temp : mAllData) {
            if (temp.toLowerCase().contains(str.toLowerCase())) {
                TeacherMyClassStudentModel tDetails = new TeacherMyClassStudentModel();
                 tDetails.setStdName(temp);
                 mSearch.add(tDetails);
            }
        }
        lststudentname.setAdapter(new TeacherMyClassStudentListAdapter(getActivity(),mSearch,"search",tq,getList(),stat));

    }

    private ArrayList<TeacherMyClassStudentModel> GetStudentName()
    {

        Bundle b = getArguments();
        String sdata =b.getString("NameList");
        ArrayList<TeacherMyClassStudentModel> results = new ArrayList<>();
        DatabaseQueries qr = new DatabaseQueries(getActivity());
        ArrayList<TeacherQuery1model> temp = qr.GetInitiatedChat("false");
        for(int i =0;i<temp.size();i++)
        {
            TeacherMyClassStudentModel tDetails = new TeacherMyClassStudentModel();
            TeacherQuery1model o1= temp.get(i);
            tDetails.setStdName(o1.getUname());
            results.add(tDetails);
        }

        return results;
    }

    private ArrayList<TeacherMyClassStudentModel> GetStudentName1()
    {

        Bundle b = getArguments();
        String sdata =b.getString("NameList");
        ArrayList<TeacherMyClassStudentModel> results = new ArrayList<>();
        DatabaseQueries qr = new DatabaseQueries(getActivity());
        DALMyPupilInfo dal = new DALMyPupilInfo(getActivity());
        String UserD[]= dal.GetSTDDIVData();

        ArrayList<TeacherQuery1model> temp = qr.GetstudList(UserD[0],UserD[1]);
        for(int i =0;i<temp.size();i++)
        {
            TeacherMyClassStudentModel tDetails = new TeacherMyClassStudentModel();
            TeacherQuery1model o1= temp.get(i);
            tDetails.setStdName(o1.getUname());
            results.add(tDetails);
        }

        return results;
    }



    @Override
    public void onResume()
    {
        super.onResume();

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if ((keyCode ==  KeyEvent.KEYCODE_BACK))
                {
                    dialog.dismiss();
                    //This is the filter
                    if (event.getAction()!= KeyEvent.ACTION_DOWN)
                        return true;
                    else
                    {
                        //Hide your keyboard here!!!!!!
                        return true; // pretend we've processed it
                    }
                }
                else
                    return false; // pass on to be processed as normal
            }
        });
    }
}
