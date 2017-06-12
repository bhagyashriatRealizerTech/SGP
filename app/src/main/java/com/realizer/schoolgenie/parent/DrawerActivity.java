package com.realizer.schoolgenie.parent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.realizer.schoolgenie.parent.chat.TeacherQueryFragment1;
import com.realizer.schoolgenie.parent.chat.TeacherQueryViewFragment;
import com.realizer.schoolgenie.parent.funcenter.ParentFunCenterFolderFragment;
import com.realizer.schoolgenie.parent.funcenter.backend.DLAFuncenter;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterModel;
import com.realizer.schoolgenie.parent.generalcommunication.ParentGeneralCommunicationFragment;
import com.realizer.schoolgenie.parent.holiday.ParentPublicHolidayFragment;
import com.realizer.schoolgenie.parent.homework.ParentHomeWorkFragment;
import com.realizer.schoolgenie.parent.pupil.ParentAttendanceCalendarFragment;
import com.realizer.schoolgenie.parent.pupil.ParentMyPupilInfoFragment;
import com.realizer.schoolgenie.parent.service.ManualSyncService;
import com.realizer.schoolgenie.parent.timetable.ParentTimeTableFragment;
import com.realizer.schoolgenie.parent.trackpupil.TrackShowMap;
import com.realizer.schoolgenie.parent.trackpupil.TrackingDialogBoxActivity;
import com.realizer.schoolgenie.parent.trackpupil.asynctask.TrackingAsyckTaskGet;
import com.realizer.schoolgenie.parent.utils.Config;

import com.realizer.schoolgenie.parent.utils.GetImages;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.utils.Utility;
import com.realizer.schoolgenie.parent.viewstar.ParentViewStarFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnTaskCompleted{

    private static final String TAG = ParentHomeWorkFragment.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    Bitmap bitmap;
    private int counter=0;
    ImageView iv;
    TextView profile_text;
    TextView dispname;
    StringBuilder result;
    String newString;
    Fragment fragment;
    DrawerLayout drawer;
    TextView userName;
    ImageView userImage;
    TextView userInitials;

    private Uri fileUri; // file url to store image/video
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Singleton.setContext(DrawerActivity.this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       /* ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);*/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideSoftKeyboard();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                hideSoftKeyboard();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                hideSoftKeyboard();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);

        userName = (TextView)header.findViewById(R.id.txt_user_name);
        userImage = (ImageView) header.findViewById(R.id.img_user_image);
        userInitials = (TextView) header.findViewById(R.id.img_user_text_image);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userName.setText(preferences.getString("DisplayName",""));

        final String urlString = preferences.getString("ThumbnailID","");
        Log.d("Image URL",urlString);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(urlString.equals("") || urlString.equalsIgnoreCase("null"))
                {
                    userImage.setVisibility(View.GONE);
                    userInitials.setVisibility(View.VISIBLE);
                    String name[]=userName.getText().toString().split(" ");
                    String fname = name[0].charAt(0)+"";
                    if(name.length>1)
                    {
                        String lname = name[1].charAt(0)+"";
                        userInitials.setText(fname+lname);
                    }
                    else
                        userInitials.setText(fname);

                }
                else
                {
                    userImage.setVisibility(View.VISIBLE);
                    userInitials.setVisibility(View.GONE);

                    String newURL=Utility.getURLImage(urlString);
                    if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                        new GetImages(newURL,userImage,newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
                    else
                    {
                        File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length-1]);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                        //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                        userImage.setImageBitmap(bitmap);
                    }

                }

            }
        }, 2000);

        /* Display First Fragment at Launch*/
        navigationView.setCheckedItem(R.id.nav_home);

        String notificationFragment=preferences.getString("FragName","");

        Fragment frag=null;
        if (notificationFragment.equals("Dashboard"))
        {
            frag = new ParentDashboardFragment();
        }
        else
        {
            if(notificationFragment.equals("Announcement"))
            {
                frag = new  ParentGeneralCommunicationFragment();
            }
            else if(notificationFragment.equals("Attendance"))
            {
                frag = new ParentMyPupilInfoFragment();
            }
            else if(notificationFragment.equals("ViewStar"))
            {
                frag = new ParentViewStarFragment();
            }
            else if(notificationFragment.equals("ConverSation"))
            {
                frag = new TeacherQueryFragment1();
            }
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("FragName", "Dashboard");
            edit.commit();
            new GCMIntentService().setCountZero(notificationFragment);
        }

        Singleton.setFragment(frag);
        Singleton.setMainFragment(frag);
        if (frag != null)
        {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, frag).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if(Singleton.getFragment() instanceof ParentDashboardFragment)
        {
            moveTaskToBack(true);
            finishAffinity();
        }
        else if (Singleton.getFragment() != null && Singleton.getFragment() instanceof FragmentBackPressedListener) {
            ((FragmentBackPressedListener) Singleton.getFragment()).onFragmentBackPressed();
        }

        hideSoftKeyboard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify search_layout parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // update the main content by replacing fragments
        fragment= null;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragment = new ParentDashboardFragment();
            Singleton.setFragment(fragment);
            Singleton.setMainFragment(fragment);
        }
        else if (id == R.id.nav_myclass)
        {
            //fragment = MyClass(1);
            fragment = new ParentMyPupilInfoFragment();
            Singleton.setFragment(fragment);
            Singleton.setMainFragment(fragment);
        }
        else if (id == R.id.nav_homework)
        {
            fragment = HomeworkList("Homework");
            Singleton.setFragment(fragment);
            Singleton.setMainFragment(fragment);
        }
        else if (id == R.id.nav_timetable)
        {
            fragment =Syllabus();
            Singleton.setFragment(fragment);
            Singleton.setMainFragment(fragment);
        }
        else if (id == R.id.nav_classwork)
        {
            fragment = HomeworkList("Classwork");
            Singleton.setFragment(fragment);
            Singleton.setMainFragment(fragment);
        }
        else if (id == R.id.nav_chat)
        {
            fragment = Quries();
            Singleton.setFragment(fragment);
            Singleton.setMainFragment(fragment);
        }
        else if (id == R.id.nav_alert)
        {
            fragment = GeneralCommunicationList();
            Singleton.setFragment(fragment);
            Singleton.setMainFragment(fragment);
        }
        else if (id == R.id.nav_funcenter)
        {
            fragment = FunCenter();
            Singleton.setFragment(fragment);
            Singleton.setMainFragment(fragment);
        }
        else if (id == R.id.nav_star)
        {
            fragment = GiveStar();
            Singleton.setFragment(fragment);
            Singleton.setMainFragment(fragment);
        }
        else if (id == R.id.nav_logout)
        {
            Logout();
        }

        else if (id == R.id.nav_holiday)
        {
            fragment = PublicHoliday();
            Singleton.setFragment(fragment);
            Singleton.setMainFragment(fragment);
        }
        else if (id == R.id.nav_tracking)
        {
           // TrackPupil();
            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String isfirsttime = sharedpreferences.getString("Tracking","");
            if (isfirsttime.equals("true") || isfirsttime.equals(""))
            {
                TrackPupil();
            }
            else
            {
                String drivername = sharedpreferences.getString("USERNAME","");
                String driverid = sharedpreferences.getString("USERID","");
                String accessToken=sharedpreferences.getString("AccessToken","");
                String deviceid=sharedpreferences.getString("DWEVICEID","");
                String userId=sharedpreferences.getString("StudentUserID","");
                TrackingAsyckTaskGet obj = new TrackingAsyckTaskGet(drivername,driverid, DrawerActivity.this,accessToken,deviceid,userId,DrawerActivity.this);
                obj.execute();
            }
        }

        else if (id == R.id.nav_sync)
        {
            fragment = Singleton.getFragment();
            Intent service = new Intent(DrawerActivity.this,ManualSyncService.class);
            Singleton.setManualserviceIntent(service);
            startService(service);
        }
        else if (id==R.id.nav_about)
        {
            fragment = AboutApp();
            Singleton.setFragment(fragment);
            Singleton.setMainFragment(fragment);
        }
        if (fragment != null)
        {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // for My Class List
    public Fragment MyClass(int i)
    {
        return fragment;
    }

    // for Homework List
    public Fragment HomeworkList(String name)
    {
        String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        fragment = new ParentHomeWorkFragment();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT", name);
        fragment.setArguments(bundle);
        return fragment;
    }


    //General Quries
    public Fragment GiveStar()
    {
        String dailyHomeworkList = "English,,Miss.Priya Shah,,Well in English Grammar,,12/10/2015";
        fragment = new ParentViewStarFragment();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        bundle.putString("AnswerSchoolStar", dailyHomeworkList);
        fragment.setArguments(bundle);
        return fragment;
    }
    //General Communication
    public Fragment GeneralCommunicationList()
    {
        String communication = "20/11/2015,,sports day on 21 november,,Sports Day_19/11/2015,,paraents teacher meeting at 2 pm on 20 november,,PTA_18/11/2015,,Story reading competition,,Other_17/11/2015,,paraents teacher meeting at 2 pm on 18 november,,PTA" +
                "_15/11/2015,,Singing talent competition,,Other_14/11/2015,,sports day on 15 november,,Sports Day_13/11/2015,,paraents teacher meeting at 2 pm on 14 november,,PTA_12/11/2015,,sports day on 13 november,,Sports Day";
        fragment = new ParentGeneralCommunicationFragment();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        bundle.putString("GeneralCommunicationList", communication);
        fragment.setArguments(bundle);
        return fragment;
    }


    // for Syllabus List
    public Fragment Syllabus()
    {
        String syllabuslist =  "Time Table 1,,LKG_B_Suvarna,,04-04-2016@@@Time Table 2,,UKG_B_Manjusha,,12-04-2016@@@Time Table 3,,LKG_A_Sachin,,24-04-2016";
        fragment = new ParentTimeTableFragment();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        bundle.putString("SyllabusList", syllabuslist);
        fragment.setArguments(bundle);
        return fragment;
    }

    //General Quries
    public Fragment Quries()
    {
        String quries = "ClassTeacher,,Mr.A.K.Bhosale_History,,Miss.B.N.Jadhav_English,,Mr.K.P.Patil_Hindi,,Mr.T.S.Kale" ;
        fragment = new TeacherQueryFragment1();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        bundle.putString("TeacherNameList", quries);
        fragment.setArguments(bundle);
        return fragment;
    }

    // for Public Holiday List
    public Fragment PublicHoliday()
    {
        String publicholiday = "Independence Day,,15/08/2015,,15/08/2015_Ganesh Chaturthi,,17/09/2015,,17/09/2015_Gandhi Jayanti,,02/10/2015,,02/10/2015_Dussehra" +
                ",,22/10/2015,,22/10/2015_Diwali Holiday,,09/11/2015,,26/11/2015_Merry Christmas,,25/12/2015,,25/12/2015_Republic Day,,26/01/2016,,26/01/2016_Mahashivratri,,17/02/2016,,17/02/2016_Holi,,06/03/2016,,06/03/2016_Gudi Padawa,,21/03/2016,,21/03/2016" +
                "_Good Friday,,03/04/2016,,03/04/2016";
        fragment = new ParentPublicHolidayFragment();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        bundle.putString("PublicHolidayList", publicholiday);
        fragment.setArguments(bundle);
        return fragment;
    }
    // for Fun Center List
    public Fragment FunCenter()
    {
        DLAFuncenter dla=new DLAFuncenter(DrawerActivity.this);
        final ArrayList<ParentFunCenterModel> allData1=dla.GetEventInfoData();
        Singleton.setFuncenterEvents(null);
        Singleton.setFuncenterEvents(allData1);
        String images = "Gathering@@@Arts@@@Designs@@@Sports@@@Plantation@@@Gathering@@@Arts@@@Designs@@@Sports@@@Plantation";
        fragment = new ParentFunCenterFolderFragment();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        bundle.putString("ImageActivityList", images);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void Logout()
    {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putString("Login", "false");
        edit.putString("LogChk", "true");
        edit.commit();
        Intent intent = new Intent(DrawerActivity.this,LoginActivity.class);
        startActivity(intent);
        //unregister the device from server
        Singleton.setContext(DrawerActivity.this);
        GCMRegistrar.unregister(DrawerActivity.this);
        finish();
    }

    // For Track My Pupil
    public void TrackPupil()
    {
        FragmentManager fragmentManager = getFragmentManager();
        TrackingDialogBoxActivity fragment = new TrackingDialogBoxActivity();
        fragment.setCancelable(true);
        fragment.show(fragmentManager, "Dialog!");
    }
   /* // for View Star List

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }*/

    public Fragment AboutApp()
    {
        fragment = new AboutAppActivity();
        Singleton.setFragment(fragment);
        Singleton.setMainFragment(fragment);
        return fragment;
    }
    public void getOption() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        galleryIntent.setType("image/*");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Choose Action");

        Intent[] intentArray = {cameraIntent};

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooser, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {

        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create search_layout media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");




        return mediaFile;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {



        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                if(data==null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    // down sizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                    Log.d("PATH", fileUri.getPath());
                    setPhoto(bitmap);
                    iv.setImageBitmap(bitmap);
                    String path = encodephoto(bitmap);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("ProfilePicPath",path);
                    editor.commit();
                    //launchUploadActivity(data);
                }
                else
                    launchUploadActivity(data);



            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }


    private void launchUploadActivity(Intent data){

        if(data.getData()!=null)
        {
            try
            {
                if (bitmap != null)
                {
                    //bitmap.recycle();
                }

                InputStream stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
                iv.setImageBitmap(bitmap);
                String path = encodephoto(bitmap);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ProfilePicPath",path);
                editor.commit();
            }

            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        else
        {
            bitmap=(Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(bitmap);
        }
    }

    //Encode image to Base64 to send to server
    private void setPhoto(Bitmap bitmapm) {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");

            }
        }
        else {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //4
            File file = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpeg");
            try {
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                //5
                fo.write(bytes.toByteArray());
                fo.close();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file)));

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
    }
    //Encode image to Base64 to send to server
    private String encodephoto(Bitmap bitmapm) {
        String imagebase64string="";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] byteArrayImage = baos.toByteArray();
            imagebase64string = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagebase64string;
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Lock Drawer Avoid opening it
     */
    public void lockDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * UnLockDrawer and allow opening it
     */
    public void unlockDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void showMyActionBar() {
        getSupportActionBar().show();
    }

    @Override
    public void onTaskCompleted(String s) {
        try {
            //JSONObject obj = new JSONObject(s);
            if(s.equalsIgnoreCase("[]"))
            {
                Toast.makeText(DrawerActivity.this,"Server Not Responding Please Try After Some Time",Toast.LENGTH_LONG).show();
            }
            else
            {
                JSONArray locList = new JSONArray(s.toString());
                //  for(int i=0;i<locList.length();i++) {
                JSONObject obj1 = locList.getJSONObject(locList.length()-1);
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(DrawerActivity.this);
                String drivername = sharedpreferences.getString("USERNAME","");
                String driverid = sharedpreferences.getString("USERID","");

                Intent intent = new Intent(DrawerActivity.this, TrackShowMap.class);
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
}