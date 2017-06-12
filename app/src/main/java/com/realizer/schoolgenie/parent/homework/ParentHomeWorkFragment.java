package com.realizer.schoolgenie.parent.homework;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.chat.backend.DALQueris;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.homework.adapter.ParentHomeworkListAdapter;
import com.realizer.schoolgenie.parent.homework.asynctask.ClassworkAsyncTaskPost;
import com.realizer.schoolgenie.parent.homework.asynctask.HomeworkAsyncTaskPost;
import com.realizer.schoolgenie.parent.homework.backend.DALHomework;
import com.realizer.schoolgenie.parent.homework.model.ParentHomeworkListModel;
import com.realizer.schoolgenie.parent.pupil.backend.DALMyPupilInfo;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.utils.StoreBitmapImages;
import com.realizer.schoolgenie.parent.utils.Utility;
import com.realizer.schoolgenie.parent.view.FullImageViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Win on 11/9/2015.
 */
public class ParentHomeWorkFragment extends Fragment implements View.OnClickListener ,OnTaskCompleted,FragmentBackPressedListener {
    DALHomework qr;
    Spinner datespinner;
    ParentHomeworkListModel obj ;
    String label;
    ListView listHomewrok;
    TextView noHwMsg;
    SharedPreferences sharedpreferences;
    DALMyPupilInfo DAP;
    String htext="Homework";

    ProgressDialog pDialog;
    static int counter=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.parent_mainlist_homework_layout, container, false);
        setHasOptionsMenu(true);
        final Bundle b = getArguments();
        htext = b.getString("HEADERTEXT");
        if(htext.equalsIgnoreCase("Homework"))
        {
            ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Homework", getActivity()));
            ((DrawerActivity) getActivity()).getSupportActionBar().show();
        }
        else if(htext.equalsIgnoreCase("Classwork"))
        {
            ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Classwork", getActivity()));
            ((DrawerActivity) getActivity()).getSupportActionBar().show();
        }


        datespinner  = (Spinner)rootView.findViewById(R.id.spLeaveType);
        listHomewrok = (ListView) rootView.findViewById(R.id.lstthomework);
        noHwMsg=(TextView) rootView.findViewById(R.id.tvNoDataMsg);

        DAP=new DALMyPupilInfo(getActivity());
        qr = new DALHomework(getActivity());

        ArrayList<String> datespin = qr.GetHWDate();
        ArrayList<String> newDateFormatList=new ArrayList<>();

        for (int k=0;k<datespin.size();k++)
        {
            if (! datespin.get(k).equalsIgnoreCase("null"))
            {
                SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
                Date date = null;
                try {
                    date = fmt.parse(datespin.get(k));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MMM-yyyy");
                newDateFormatList.add(fmtOut.format(date));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, newDateFormatList);
        adapter.setDropDownViewResource(R.layout.viewstar_subject_spiner);
        datespinner.setAdapter(adapter);
        datespinner.setSelection(newDateFormatList.size() - 1);

        if (newDateFormatList.size()>0) {
            ArrayList<ParentHomeworkListModel> homewok = GetHomeWorkList(newDateFormatList.get(newDateFormatList.size()-1));

            if (homewok.size() != 0) {
                listHomewrok.setVisibility(View.VISIBLE);
                listHomewrok.setAdapter(new ParentHomeworkListAdapter(getActivity(), homewok));
                noHwMsg.setVisibility(View.GONE);

            } else {
                if (htext.equalsIgnoreCase("Homework"))
                    noHwMsg.setText("No Homework Provided.");
                else
                    noHwMsg.setText("No Classwork Provided.");
                noHwMsg.setVisibility(View.VISIBLE);
                listHomewrok.setVisibility(View.GONE);
            }
        }
        else
        {
            if (htext.equalsIgnoreCase("Homework"))
                noHwMsg.setText("No Homework Provided.");
            else
                noHwMsg.setText("No Classwork Provided.");
            noHwMsg.setVisibility(View.VISIBLE);
            listHomewrok.setVisibility(View.GONE);
        }
        datespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ArrayList<ParentHomeworkListModel> homewok = GetHomeWorkList(datespinner.getSelectedItem().toString());

                if (homewok.size() != 0) {
                    listHomewrok.setVisibility(View.VISIBLE);
                    listHomewrok.setAdapter(new ParentHomeworkListAdapter(getActivity(), homewok));
                    noHwMsg.setVisibility(View.GONE);

                } else {
                    if (htext.equalsIgnoreCase("Homework"))
                        noHwMsg.setText("No Homework Provided.");
                    else
                        noHwMsg.setText("No Classwork Provided.");
                    noHwMsg.setVisibility(View.VISIBLE);
                    listHomewrok.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listHomewrok.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listHomewrok.getItemAtPosition(position);
                ParentHomeworkListModel homeworkObj = (ParentHomeworkListModel) o;
                ParentHomeworkDetailsFragment fragment = new ParentHomeworkDetailsFragment();
                Singleton.setFragment(fragment);
                Bundle bundle = new Bundle();
                bundle.putString("HEADERTEXT", b.getString("HEADERTEXT"));
                bundle.putString("SubjectName", homeworkObj.getSubject());
                bundle.putString("HomeworkDate", homeworkObj.getHwdate());
                bundle.putString("TeacherName", homeworkObj.getgivenBy());
                bundle.putString("Status", "Done");
                bundle.putString("HomeworkImage", homeworkObj.getImage());
                bundle.putInt("Imageid", homeworkObj.getImgId());
                bundle.putString("HWUUid", homeworkObj.getHwUUID());
                bundle.putString("HomeworkText", homeworkObj.getHomework());
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
               /* if (!homeworkObj.getImage().equalsIgnoreCase("NoImage")) {
                    Intent i = new Intent(getActivity(), FullImageViewPager.class);
                    i.putExtra("HEADERTEXT", htext);
                    i.putExtra("HWUUID", homeworkObj.getHwUUID());
                    startActivity(i);
                } else {
                    ParentHomeworkDetailFragment fragment = new ParentHomeworkDetailFragment();
                    Singleton.setFragment(fragment);
                    Bundle bundle = new Bundle();
                    bundle.putString("HEADERTEXT", b.getString("HEADERTEXT"));
                    bundle.putString("SubjectName", homeworkObj.getSubject());
                    bundle.putString("HomeworkDate", homeworkObj.getHwdate());
                    bundle.putString("TeacherName", homeworkObj.getgivenBy());
                    bundle.putString("Status", "Done");
                    bundle.putString("HomeworkImage", homeworkObj.getImage());
                    bundle.putInt("Imageid", homeworkObj.getImgId());
                    bundle.putString("HomeworkText", homeworkObj.getHomework());
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }*/
            }
        });

        return rootView;
    }


    public void GetHomWrk()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/d/yyyy");
        String datetody = df.format(calendar.getTime());
        String UserData[]=  DAP.GetSTDDIVData();
        DALQueris qrt = new DALQueris(getActivity());
        ArrayList<String> subjects = qrt.GetAllSub();

        for (int k = 0; k < subjects.size(); k++) {

            ParentHomeworkListModel home = new ParentHomeworkListModel();
            home.setschoolcode(UserData[2]);
            home.setstandard(UserData[0]);
            home.setdivision(UserData[1]);
            home.setHwdate(datetody);
            home.setSubject(subjects.get(k));
            if(isConnectingToInternet()) {
                if(htext.equalsIgnoreCase("Homework"))
                {
                    HomeworkAsyncTaskPost obj1 = new HomeworkAsyncTaskPost(home, getActivity(), ParentHomeWorkFragment.this);
                    obj1.execute();
                }
                else if(htext.equalsIgnoreCase("Classwork"))
                {
                    ClassworkAsyncTaskPost obj1 = new ClassworkAsyncTaskPost(home, getActivity(), ParentHomeWorkFragment.this);
                    obj1.execute();
                }
            }
            else
            {
                Toast.makeText(getActivity(), "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayList<ParentHomeworkListModel> GetHomeWorkList(String sdate)
    {
        String selectedDate=sdate;
               // datespinner.getSelectedItem().toString();

        SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        try {
            date = fmt.parse(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("MM/dd/yyyy");
        selectedDate= fmtOut.format(date);
      //  ArrayList<ParentHomeworkListModel> allresults=qr.GetHomeworkAllInfoData(selectedDate);
        ArrayList<ParentHomeworkListModel> results = new ArrayList<>();
        ArrayList<ParentHomeworkListModel> hwlst = qr.GetHomeworkInfoData(selectedDate,htext);
        String prevUUID="";
        String currentUUID="";
        for(int i=0;i<hwlst.size();i++)
        {
            if (i==0)
            {
                prevUUID=hwlst.get(i).getHwUUID();
                ParentHomeworkListModel hDetail = new ParentHomeworkListModel();
                ParentHomeworkListModel obj = hwlst.get(i);
                hDetail.setSubject(obj.getSubject());
                if(obj.getHomework().length()==0)
                    hDetail.setHomework("NoText");
                else
                    hDetail.setHomework(obj.getHomework());

                if(obj.getImage().equals("[]") || obj.getImage().equals(""))
                    hDetail.setImage("NoImage");
                else
                {
                    hDetail.setImage(obj.getImage());
                   /* String newPath = new Utility().getURLImage(obj.getImage());
                    if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                        new StoreBitmapImages(newPath, newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                    }*/
                }

                hDetail.setdivision(obj.getdivision());
                hDetail.setHwdate(obj.getHwdate());
                hDetail.setgivenBy(obj.getgivenBy());
                hDetail.setHwUUID(obj.getHwUUID());
                results.add(hDetail);
            }
            else
            {
                currentUUID=hwlst.get(i).getHwUUID();
                if (!prevUUID.equals(currentUUID))
                {
                    ParentHomeworkListModel hDetail = new ParentHomeworkListModel();
                    ParentHomeworkListModel obj = hwlst.get(i);
                    hDetail.setSubject(obj.getSubject());
                    if(obj.getHomework().length()==0)
                        hDetail.setHomework("NoText");
                    else
                        hDetail.setHomework(obj.getHomework());

                    if(obj.getImage().equals("[]") || obj.getImage().equals(""))
                        hDetail.setImage("NoImage");
                    else
                    {
                        hDetail.setImage(obj.getImage());
                       /* String newPath = new Utility().getURLImage(obj.getImage());
                        if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                            new StoreBitmapImages(newPath, newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                        }*/
                    }

                    hDetail.setdivision(obj.getdivision());
                    hDetail.setHwdate(obj.getHwdate());
                    hDetail.setgivenBy(obj.getgivenBy());
                    hDetail.setHwUUID(obj.getHwUUID());
                    results.add(hDetail);
                    prevUUID=currentUUID;
                }
            }

        }
        return results;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTaskCompleted(String s)
    {
        boolean b = false;
        b = parsData(s);
        if(b==true)
        {
            DALHomework hw= new DALHomework(getActivity());
            ArrayList<String> datespin = hw.GetHWDate();
            ArrayList<String> newDateFormatList=new ArrayList<>();

            for (int k=0;k<datespin.size();k++)
            {
                SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
                Date date = null;
                try {
                    date = fmt.parse(datespin.get(k));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MMM-yyyy");
                newDateFormatList.add(fmtOut.format(date));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, newDateFormatList);
            adapter.setDropDownViewResource(R.layout.viewstar_subject_spiner);
            datespinner.setAdapter(adapter);
            datespinner.setSelection(newDateFormatList.size()-1);
            pDialog.dismiss();
        }
        else {
            pDialog.dismiss();
            //Toast.makeText(getActivity(), "Not Homework inserted,Pls Try again!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean parsData(String json) {
        long n =-1;
        DALHomework dla = new DALHomework(getActivity());

        JSONObject rootObj = null;
        Log.d("String", json);
        try {
            if(htext.equalsIgnoreCase("Homework"))
            {
                rootObj = new JSONObject(json);
                JSONObject obj=rootObj.getJSONObject("fetchHomeWorkResult");
                String schoolCode= obj.getString("SchoolCode");
                String std= obj.getString("Std");
                String division= obj.getString("div");
                String givenby= obj.getString("givenBy");
                String hwdate= obj.getString("hwDate");
                JSONArray img  = obj.getJSONArray("hwImage64Lst");
                JSONArray text  = obj.getJSONArray("hwTxtLst");
                String subject= obj.getString("subject");
                if(img.length()==0 && text.length()==0)
                {
                    //pDialog.dismiss();
                }
                else {

                    ParentHomeworkListModel model=new ParentHomeworkListModel();
                    model.setschoolcode(schoolCode);
                    model.setstandard(std);
                    model.setdivision(division);
                    model.setgivenBy(givenby);
                    model.setHwdate(hwdate);
                    model.setImage(img.toString());
                    model.setHomework(text.toString());
                    model.setSubject(subject);
                    model.setWork(htext);
                    String[] IMG=new String[img.length()];
                    for (int i=0;i<img.length();i++)
                    {
                        IMG[i] = img.getString(i);
                    }


                    for (int i=0;i<IMG.length;i++) {
                        String newPath = new Utility().getURLImage(IMG[i]);
                        if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                            new StoreBitmapImages(newPath,newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                            counter++;
                        }
                    }

                    if (counter==IMG.length)
                    {
                        dla.insertHomeworkInfo(schoolCode, std, division, givenby, hwdate, img.toString(), text.toString(), subject,htext,"");
                        counter=0;
                    }
                    else
                    {
                       // pDialog.hide();
                    }
                }

            }
            else if(htext.equalsIgnoreCase("Classwork"))
            {
                rootObj = new JSONObject(json);
                JSONObject obj=rootObj.getJSONObject("fetchClassWorkResult");
                String schoolCode= obj.getString("SchoolCode");
                String std= obj.getString("Std");
                String division= obj.getString("div");
                String givenby= obj.getString("givenBy");
                String hwdate= obj.getString("cwDate");
                JSONArray img  = obj.getJSONArray("cwImage64Lst");
                JSONArray text  = obj.getJSONArray("CwTxtLst");
                String subject= obj.getString("subject");
                if(img.length()==0 && text.length()==0)
                {
                   // pDialog.dismiss();
                }
                else {

                    ParentHomeworkListModel model=new ParentHomeworkListModel();
                    model.setschoolcode(schoolCode);
                    model.setstandard(std);
                    model.setdivision(division);
                    model.setgivenBy(givenby);
                    model.setHwdate(hwdate);
                    model.setImage(img.toString());
                    model.setHomework(text.toString());
                    model.setSubject(subject);
                    model.setWork(htext);
                    String[] IMG=new String[img.length()];
                    for (int i=0;i<img.length();i++)
                    {
                        IMG[i] = img.getString(i);
                    }

                    for (int i=0;i<IMG.length;i++) {
                        String newPath = new Utility().getURLImage(IMG[i]);
                        if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                            new StoreBitmapImages(newPath,newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                            counter++;
                        }
                    }

                    if (counter==IMG.length)
                    {
                        dla.insertHomeworkInfo(schoolCode, std, division, givenby, hwdate, img.toString(), text.toString(), subject,htext,"");
                        counter=0;
                    }
                    else
                    {
                       // pDialog.hide();
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.toString());
            Log.e("HWJ(LocalizedMessage)", e.getLocalizedMessage());
            Log.e("HWJ(StackTrace)", e.getStackTrace().toString());
            Log.e("HWJ(Cause)", e.getCause().toString());
        }
        return true;
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
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.sync_manually_data, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync_data:
                if(htext.equalsIgnoreCase("Homework"))
                {
                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Please wait Homework is Loading...");
                    pDialog.show();
                    GetHomWrk();
                }
                else if(htext.equalsIgnoreCase("Classwork"))
                {
                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Please wait Classwork is Loading...");
                    pDialog.show();
                    GetHomWrk();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

}


