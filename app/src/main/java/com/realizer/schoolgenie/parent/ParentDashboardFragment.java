package com.realizer.schoolgenie.parent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.realizer.schoolgenie.parent.Notification.NotificationModel;
import com.realizer.schoolgenie.parent.Notification.TeacherNotificationListAdapter;
import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.chat.TeacherQueryFragment1;
import com.realizer.schoolgenie.parent.chat.TeacherQueryViewFragment;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.funcenter.ParentFunCenterFolderFragment;
import com.realizer.schoolgenie.parent.funcenter.backend.DLAFuncenter;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterModel;
import com.realizer.schoolgenie.parent.generalcommunication.ParentGeneralCommunicationFragment;
import com.realizer.schoolgenie.parent.holiday.ParentPublicHolidayFragment;
import com.realizer.schoolgenie.parent.holiday.backend.DALHoliday;
import com.realizer.schoolgenie.parent.holiday.model.ParentPublicHolidayListModel;
import com.realizer.schoolgenie.parent.homework.ParentHomeWorkFragment;
import com.realizer.schoolgenie.parent.pupil.ParentMyPupilInfoFragment;
import com.realizer.schoolgenie.parent.timetable.ParentTimeTableFragment;
import com.realizer.schoolgenie.parent.timetable.model.ParentTimeTableExamListModel;
import com.realizer.schoolgenie.parent.trackpupil.TrackShowMap;
import com.realizer.schoolgenie.parent.trackpupil.TrackingDialogBoxActivity;
import com.realizer.schoolgenie.parent.trackpupil.asynctask.TrackingAsyckTaskGet;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.GetImages;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.utils.StoreBitmapImages;
import com.realizer.schoolgenie.parent.utils.Utility;
import com.realizer.schoolgenie.parent.view.Action;
import com.realizer.schoolgenie.parent.view.SwipeDetector;
import com.realizer.schoolgenie.parent.viewstar.ParentViewStarFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Win on 11/5/2015.
 */
public class ParentDashboardFragment extends Fragment implements View.OnClickListener ,OnTaskCompleted {

    TextView myclass, homework, giveStar, timeTable, queries, funCenter, communication, trackPupil, publicHoliday, classwork;
    ListView notificationList;
    LinearLayout userInfoLayout;
    SwipeDetector swipeDetector;
    ImageView picUser;
    TextView nameUSer,userInitials,textStdDiv;
    ArrayList<NotificationModel> notificationData;
    TeacherNotificationListAdapter notificationAdapter;
    MessageResultReceiver resultReceiver;
    DatabaseQueries qr;
    static int holidayCounter=0;
    ArrayList<NotificationModel> FilteredData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.parent_dashboard_layout, container, false);
        Controls(rootView);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("DashBoard", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        myclass.setOnClickListener(this);
        homework.setOnClickListener(this);
        giveStar.setOnClickListener(this);
        timeTable.setOnClickListener(this);
        queries.setOnClickListener(this);
        funCenter.setOnClickListener(this);
        communication.setOnClickListener(this);
        // trackPupil.setOnClickListener(this);
        publicHoliday.setOnClickListener(this);
        classwork.setOnClickListener(this);
        qr = new DatabaseQueries(getActivity());
        notificationData = new ArrayList<>();

        swipeDetector = new SwipeDetector();
        notificationList.setOnTouchListener(swipeDetector);
        Singleton obj = Singleton.getInstance();
        resultReceiver = new MessageResultReceiver(null);
        obj.setResultReceiver(resultReceiver);

        //showing dp
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String urlString = preferences.getString("ThumbnailID","");
        Log.d("Image URL", urlString);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(urlString.equals("") || urlString.equalsIgnoreCase("null"))
                {
                    picUser.setVisibility(View.GONE);
                    userInitials.setVisibility(View.VISIBLE);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String displayname = preferences.getString("DisplayName","");
                    String name[]=displayname.split(" ");
                    String fname = name[0].trim().toUpperCase().charAt(0)+"";
                    if(name.length>1)
                    {
                        String lname = name[1].trim().toUpperCase().charAt(0)+"";
                        userInitials.setText(fname+lname);
                    }
                    else
                        userInitials.setText(fname);

                }
                else
                {
                    picUser.setVisibility(View.VISIBLE);
                    userInitials.setVisibility(View.GONE);

                    String newURL= Utility.getURLImage(urlString);
                    if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                        new GetImages(newURL,picUser,newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
                    else
                    {
                        File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                        //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                        picUser.setImageBitmap(bitmap);
                    }
                }
            }
        },2000);

        new GetNotificationList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,1);

        notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (swipeDetector.swipeDetected()) {

                    if (swipeDetector.getAction() == Action.LR) {
                        // perform any task
                    }
                    else if (swipeDetector.getAction() == Action.RL) {
                        // perform any task


                        final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_out_left);
                        view.startAnimation(animation);

                        Handler handle = new Handler();
                        handle.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if (FilteredData.size() > 0)
                                {
                                    DatabaseQueries qr = new DatabaseQueries(getActivity());
                                    qr.deleteNotificationRow(FilteredData.get(position).getId());
                                    if(FilteredData.size() ==1)
                                        new GetNotificationList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 3);
                                    else {
                                        FilteredData.remove(position);
                                        notificationAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }, 500);


                    }
                    else if (swipeDetector.getAction() == Action.TB) {
                        // perform any task
                    }
                    else if (swipeDetector.getAction() == Action.BT) {
                        // perform any task
                    }
                    else if (swipeDetector.getAction() == Action.None) {
                        // perform any task

                    }

                }
                else
                {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    DatabaseQueries qr = new DatabaseQueries(getActivity());

                    if(FilteredData.get(position).getNotificationtype().equalsIgnoreCase("Homework")
                            || FilteredData.get(position).getNotificationtype().equalsIgnoreCase("Classwork")) {

                        qr.deleteNotificationRow(FilteredData.get(position).getId());
                        Homework(FilteredData.get(position).getNotificationtype());
                    }
                    else if(FilteredData.get(position).getNotificationtype().equalsIgnoreCase("TimeTable"))
                    {
                        qr.deleteNotificationRow(FilteredData.get(position).getId());
                        TimeTable("b");
                    }
                    else if(FilteredData.get(position).getNotificationtype().equalsIgnoreCase("Star"))
                    {
                        qr.deleteNotificationRow(FilteredData.get(position).getId());
                        ViewStar("b");
                    }
                    else if(FilteredData.get(position).getNotificationtype().equalsIgnoreCase("Attendance"))
                    {
                        qr.deleteNotificationRow(FilteredData.get(position).getId());
                        MyPupil("b");
                    }
                    else if(FilteredData.get(position).getNotificationtype().equalsIgnoreCase("Alerts"))
                    {
                        qr.deleteNotificationRow(FilteredData.get(position).getId());
                        Communication("b");
                    }
                    else if(FilteredData.get(position).getNotificationtype().equalsIgnoreCase("Holiday"))
                    {
                        qr.deleteNotificationRow(FilteredData.get(position).getId());
                        PublicHoliday("b");
                    }

                    else if(FilteredData.get(position).getNotificationtype().equalsIgnoreCase("Fun Center"))
                    {
                        qr.deleteNotificationRow(FilteredData.get(position).getId());
                        FunCenter("b");
                    }
                    else if(FilteredData.get(position).getNotificationtype().equalsIgnoreCase("Message"))
                    {
                        String uid = FilteredData.get(position).getAdditionalData2();
                        String urlImage = null;
                        String userData[] = FilteredData.get(position).getAdditionalData1().trim().split("@@@");
                        if(userData.length >2 )
                            urlImage = userData[2];

                        qr.updateInitiatechat(preferences.getString("SyncStd",""),preferences.getString("SyncDiv",""),userData[0],"true",uid,0,urlImage);
                        Bundle bundle = new Bundle();
                        bundle.putString("USERID", uid);
                        bundle.putString("SENDERNAME",userData[0]);
                        bundle.putString("Stand",preferences.getString("SyncStd",""));
                        bundle.putString("Divi",preferences.getString("SyncDiv",""));
                        bundle.putString("UrlImage",urlImage);

                        qr.deleteNotificationRow(FilteredData.get(position).getId());

                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        TeacherQueryViewFragment fragment = new TeacherQueryViewFragment();
                        Singleton.setFragment(fragment);
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.frame_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                }
            }
        });

        checkTomorrowIsHoliday();

        return rootView;
    }

    public void Controls(View v) {
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");
        myclass = (TextView) v.findViewById(R.id.txttdashmyclass);
        myclass.setTypeface(face);
        homework = (TextView) v.findViewById(R.id.txttdashhomework);
        homework.setTypeface(face);
        giveStar = (TextView) v.findViewById(R.id.txttdashviewstar);
        giveStar.setTypeface(face);
        timeTable = (TextView) v.findViewById(R.id.txttdashtimetable);
        timeTable.setTypeface(face);
        queries = (TextView) v.findViewById(R.id.txttdashqueries);
        queries.setTypeface(face);
        funCenter = (TextView) v.findViewById(R.id.txttdashfuncenter);
        funCenter.setTypeface(face);
        communication = (TextView) v.findViewById(R.id.txttdashcommunication);
        communication.setTypeface(face);
        // trackPupil = (TextView) v.findViewById(R.id.txttdashtrackpupil);
        publicHoliday = (TextView) v.findViewById(R.id.txttdashpublicholiday);
        publicHoliday.setTypeface(face);
        classwork = (TextView) v.findViewById(R.id.txttdashclasswork);
        classwork.setTypeface(face);
        notificationList = (ListView) v.findViewById(R.id.lst_notification);
        userInfoLayout = (LinearLayout)v.findViewById(R.id.linuserlayout);
        picUser = (ImageView)v.findViewById(R.id.iv_uImage);
        nameUSer = (TextView)v.findViewById(R.id.txtuName);
        userInitials = (TextView)v.findViewById(R.id.img_user_text_image);
        textStdDiv= (TextView)v.findViewById(R.id.txtStdDiv);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txttdashmyclass:
                MyPupil("b");
                break;
            case R.id.txttdashhomework:
                Homework("Homework");
                break;
            case R.id.txttdashviewstar:
                ViewStar("b");
                break;
            case R.id.txttdashtimetable:
                TimeTable("b");
                break;
            case R.id.txttdashqueries:
                Queries("b");
                break;
            case R.id.txttdashfuncenter:
                FunCenter("b");
                break;
            case R.id.txttdashcommunication:
                Communication("b");
                break;
            case R.id.txttdashpublicholiday:
                //PublicHoliday("b");
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor edit = sharedpreferences.edit();

                String isfirsttime = sharedpreferences.getString("Tracking","");
                if (isfirsttime.equals("true") || isfirsttime.equals(""))
                {
                    TrackPupil("b");
                }
                else
                {
                    String drivername = sharedpreferences.getString("USERNAME","");
                    String driverid = sharedpreferences.getString("USERID","");
                    String accessToken=sharedpreferences.getString("AccessToken","");
                    String deviceid=sharedpreferences.getString("DWEVICEID","");
                    String userId=sharedpreferences.getString("UidName","");
                    TrackingAsyckTaskGet obj = new TrackingAsyckTaskGet(drivername,driverid, getActivity(),accessToken,deviceid,userId,ParentDashboardFragment.this);
                    obj.execute();
                }
                break;
            case R.id.txttdashclasswork:
                Homework("Classwork");
                break;
        }
    }

    //Pupil Info
    public void MyPupil(String res) {
        ParentMyPupilInfoFragment fragment = new ParentMyPupilInfoFragment();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    // For Homework
    public void Homework(String res) {
        // Get Output as
        String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        ParentHomeWorkFragment fragment = new ParentHomeWorkFragment();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT", res);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }


    // For View Star
    public void ViewStar(String res) {
        String dailyHomeworkList = "English,,Miss.Priya Shah,,Well in English Grammar,,12/10/2015";
        ParentViewStarFragment fragment = new ParentViewStarFragment();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("AnswerSchoolStar", dailyHomeworkList);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    // For Time Table
    public void TimeTable(String res) {
        // Get Output as
        String syllabuslist = "Time Table 1,,LKG_B_Suvarna,,04-04-2016@@@Time Table 2,,UKG_B_Manjusha,,12-04-2016@@@Time Table 3,,LKG_A_Sachin,,24-04-2016";
        ParentTimeTableFragment fragment = new ParentTimeTableFragment();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("SyllabusList", syllabuslist);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    // For Queries
    public void Queries(String res) {
        // Get Output as
        String quries = "ClassTeacher,,Mr.A.K.Bhosale_History,,Miss.B.N.Jadhav_English,,Mr.K.P.Patil_Hindi,,Mr.T.S.Kale";
        TeacherQueryFragment1 fragment = new TeacherQueryFragment1();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("TeacherNameList", quries);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }


    // For General Communication
    public void Communication(String res) {
        ParentGeneralCommunicationFragment fragment = new ParentGeneralCommunicationFragment();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    // For Fun Center
    public void FunCenter(String res) {
        DLAFuncenter dla=new DLAFuncenter(getActivity());
        final ArrayList<ParentFunCenterModel> allData1=dla.GetEventInfoData();
        Singleton.setFuncenterEvents(null);
        Singleton.setFuncenterEvents(allData1);
        String images = "Gathering@@@Arts@@@Designs@@@Sports@@@Plantation@@@Gathering@@@Arts@@@Designs@@@Sports@@@Plantation";
        ParentFunCenterFolderFragment fragment = new ParentFunCenterFolderFragment();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("ImageActivityList", images);
        bundle.putString("HEADERTEXT", res);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    // For Public Holiday
    public void PublicHoliday(String res) {
        ParentPublicHolidayFragment fragment = new ParentPublicHolidayFragment();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }


    // For Track My Pupil
    public void TrackPupil(String res) {

        FragmentManager fragmentManager = getFragmentManager();
        TrackingDialogBoxActivity fragment = new TrackingDialogBoxActivity();
        fragment.setCancelable(true);
        fragment.show(fragmentManager, "Dialog!");
    }

    @Override
    public void onTaskCompleted(String s) {
        try {
            //JSONObject obj = new JSONObject(s);
            if(s.equalsIgnoreCase("[]"))
            {
                Toast.makeText(getActivity(), "Server Not Responding Please Try After Some Time", Toast.LENGTH_LONG).show();
            }
            else
            {
                JSONArray locList = new JSONArray(s.toString());
                //  for(int i=0;i<locList.length();i++) {
                JSONObject obj1 = locList.getJSONObject(locList.length()-1);
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String drivername = sharedpreferences.getString("USERNAME","");
                String driverid = sharedpreferences.getString("USERID","");

                Intent intent = new Intent(getActivity(), TrackShowMap.class);
                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", drivername);
                bundle.putString("USERID", driverid);
                bundle.putString("LATITUDE", obj1.getString("latitude"));
                bundle.putString("LONGITUDE", obj1.getString("longitude"));
                intent.putExtras(bundle);
                startActivity(intent);

                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.putString("USERNAME", drivername);
                edit.putString("USERID", driverid);
                edit.putString("Tracking", "false");
                edit.commit();
                Singleton.setIsShowMap(true);
            }

            //  }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class GetNotificationList extends AsyncTask<Integer, Void,Integer>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showing dp
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String urlString = preferences.getString("ThumbnailID","");
            String displayname = preferences.getString("DisplayName","");
            if(urlString.equals("") || urlString.equalsIgnoreCase("null"))
            {
                picUser.setVisibility(View.GONE);
                userInitials.setVisibility(View.VISIBLE);
                String name[]=displayname.split(" ");
                String fname = name[0].trim().toUpperCase().charAt(0)+"";
                if(name.length>1)
                {
                    String lname = name[1].trim().toUpperCase().charAt(0)+"";
                    userInitials.setText(fname+lname);
                }
                else
                    userInitials.setText(fname);

            }
            else
            {
                picUser.setVisibility(View.VISIBLE);
                userInitials.setVisibility(View.GONE);

                String newURL= Utility.getURLImage(urlString);
                if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                    new GetImages(newURL,picUser,newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
                else
                {
                    File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                    //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                    picUser.setImageBitmap(bitmap);
                }
            }
        }


        @Override
        protected Integer doInBackground(Integer... params) {
            DatabaseQueries qr = new DatabaseQueries(getActivity());
            notificationData = qr.GetNotificationsData();
            return params[0];
        }

        @Override
        protected void onPostExecute(Integer type) {
            super.onPostExecute(type);
            if (type == 1 || type ==2) {
                if(notificationData.size()>0) {
                    FilteredData=new ArrayList<>();
                    int count=0;
                    for (int i=0;i<notificationData.size();i++)
                    {
                        if (notificationData.get(i).getNotificationtype().equalsIgnoreCase("Message"))
                        {
                            count++;
                            if (count==1)
                                FilteredData.add(notificationData.get(i));
                        }
                        else
                        {
                            FilteredData.add(notificationData.get(i));
                        }
                    }

                    notificationList.setVisibility(View.VISIBLE);
                    userInfoLayout.setVisibility(View.GONE);
                    notificationAdapter = new TeacherNotificationListAdapter(getActivity(), FilteredData);
                    notificationList.setAdapter(notificationAdapter);
                    notificationAdapter.notifyDataSetChanged();
                }
                else
                {
                    notificationList.setVisibility(View.GONE);
                    userInfoLayout.setVisibility(View.VISIBLE);
                    Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    nameUSer.setText(preferences.getString("DisplayName", ""));
                    nameUSer.setTypeface(face);
                    textStdDiv.setText(preferences.getString("SyncStd", "")+"   "+preferences.getString("SyncDiv", ""));
                    textStdDiv.setTypeface(face);
                    String urlString = preferences.getString("ThumbnailID","");
                    Log.d("Image URL", urlString);

                    if(urlString.equals("") || urlString.equalsIgnoreCase("null"))
                    {
                        picUser.setVisibility(View.GONE);
                        userInitials.setVisibility(View.VISIBLE);
                        String name[]=nameUSer.getText().toString().split(" ");
                        String fname = name[0].trim().toUpperCase().charAt(0)+"";
                        if(name.length>1)
                        {
                            String lname = name[1].trim().toUpperCase().charAt(0)+"";
                            userInitials.setText(fname+lname);
                        }
                        else
                            userInitials.setText(fname);

                    }
                    else
                    {
                        picUser.setVisibility(View.VISIBLE);
                        userInitials.setVisibility(View.GONE);
                        String newURL= Utility.getURLImage(urlString);
                        if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                            new GetImages(newURL,picUser,newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
                        else
                        {
                            File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                            //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                            picUser.setImageBitmap(bitmap);
                        }
                    }
                }
            }
            else   if (type == 3) {
                notificationList.setVisibility(View.GONE);
                userInfoLayout.setVisibility(View.VISIBLE);
                Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                nameUSer.setText(preferences.getString("DisplayName", ""));
                nameUSer.setTypeface(face);
                textStdDiv.setText(preferences.getString("SyncStd", "")+"   "+preferences.getString("SyncDiv", ""));
                textStdDiv.setTypeface(face);
                String urlString = preferences.getString("ThumbnailID","");
                Log.d("Image URL", urlString);

                if(urlString.equals("") || urlString.equalsIgnoreCase("null"))
                {
                    picUser.setVisibility(View.GONE);
                    userInitials.setVisibility(View.VISIBLE);
                    String name[]=nameUSer.getText().toString().split(" ");
                    String fname = name[0].trim().toUpperCase().charAt(0)+"";
                    if(name.length>1)
                    {
                        String lname = name[1].trim().toUpperCase().charAt(0)+"";
                        userInitials.setText(fname+lname);
                    }
                    else
                        userInitials.setText(fname);

                }
                else
                {
                    picUser.setVisibility(View.VISIBLE);
                    userInitials.setVisibility(View.GONE);
                    String newURL= Utility.getURLImage(urlString);
                    if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                        new GetImages(newURL,picUser,newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
                    else
                    {
                        File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                        //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                        picUser.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }

    class UpdateUI implements Runnable {
        String update;

        public UpdateUI(String update) {

            this.update = update;
        }

        public void run() {

            if (update.equals("UpdateNotification")) {

                new GetNotificationList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,2);
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

            if(resultCode == 1){
                getActivity().runOnUiThread(new UpdateUI("UpdateNotification"));
            }
        }
    }

    public void checkTomorrowIsHoliday()
    {
        if (holidayCounter==0)
        {
            DALHoliday qr1 = new DALHoliday(getActivity());
            ArrayList<ParentPublicHolidayListModel> publiholiday = qr1.GetHolidayData();
            for (int i=0;i<publiholiday.size();i++)
            {
                String timestamp = publiholiday.get(i).getStartDate().split("\\(")[1].split("\\-")[0];
                Date createdOn = new Date(Long.parseLong(timestamp));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = sdf.format(createdOn);
                String holidayArr[]=formattedDate.split("/");

                //Current time
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String currentdate = df.format(calendar.getTime());
                String CurrentdayArr[]=currentdate.split("/");

                if (holidayArr[1].equals(CurrentdayArr[1]) && holidayArr[2].equals(CurrentdayArr[2]))
                {
                    if (Integer.valueOf(holidayArr[0])-Integer.valueOf(CurrentdayArr[0]) == 1)
                    {
                        Calendar calendar1 = Calendar.getInstance();
                        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                        String date = df1.format(calendar1.getTime());

                        NotificationModel notification1 = new NotificationModel();
                        notification1.setNotificationId(1);
                        notification1.setNotificationDate(date);
                        notification1.setNotificationtype("Holiday");
                        notification1.setMessage(publiholiday.get(i).getDesc());
                        notification1.setIsRead("false");
                        notification1.setAdditionalData1(publiholiday.get(i).getStartDate());
                        qr.InsertNotification(notification1);
                        if(Singleton.getResultReceiver() != null)
                            Singleton.getResultReceiver().send(1,null);
                        holidayCounter++;
                    }
                }
            }
        }
    }
}