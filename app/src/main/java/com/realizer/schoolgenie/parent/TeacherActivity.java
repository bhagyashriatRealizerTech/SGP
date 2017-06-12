package com.realizer.schoolgenie.parent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.realizer.schoolgenie.parent.ParentDashboardFragment;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.leftdrawer.DrawerItem;
import com.realizer.schoolgenie.parent.leftdrawer.DrawerListAdapter;
import com.realizer.schoolgenie.parent.utils.Config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class TeacherActivity extends AppCompatActivity {
    private static final String TAG = "Farhan";
    private DrawerLayout mDrawerLayout;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    Bitmap bitmap;
    private int counter=0;

    private Uri fileUri; // file url to store image/video
    private ListView mDrawerList;
    Fragment fragment;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<DrawerItem> navDrawerItems;
    private DrawerListAdapter adapter;
    LinearLayout drawerll;
    ImageView iv;
    TextView profile_text;
    TextView dispname;
    StringBuilder result;
    String newString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.teacher_activity);

        mTitle = mDrawerTitle = getTitle();

        iv = (ImageView)findViewById(R.id.profilepic);
        profile_text=(TextView)findViewById(R.id.txtinitialPupil);
          /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String temp = preferences.getString("ProfilePicPath","");
            Bitmap icon = decodeBase64(temp);
          */
      /*  Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.timage);
        iv.setImageBitmap(icon);*/
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOption();
            }
        });
        iv.setVisibility(View.GONE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        dispname = (TextView)findViewById(R.id.txtdispName);
        String dpname= preferences.getString("DisplayName","");
        String thumbnailurl= preferences.getString("ThumbnailID", "");
        if (thumbnailurl!=null)
        {

        }
        else
        {
            String name[]=dpname.split(" ");
            if (name.length>1) {
                profile_text.setText( String.valueOf(name[0].charAt(0)) + String.valueOf(name[1].charAt(0)));
            }
        }

        dispname.setText(dpname);

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.drawer_items);

        // nav drawer icons from resources

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        drawerll = (LinearLayout) findViewById(R.id.drawerll);

        navDrawerItems = new ArrayList<DrawerItem>();
        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.drawer_icons);

        // adding nav drawer items to array
        // Home
        //navDrawerItems
        navDrawerItems.add(new DrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[6],navMenuIcons.getResourceId(6, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[7],navMenuIcons.getResourceId(7, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[8],navMenuIcons.getResourceId(8, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[10],navMenuIcons.getResourceId(10, -1)));

        // Recycle the typed array

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new DrawerListAdapter(getApplicationContext(),	navDrawerItems);

        mDrawerList.setAdapter(adapter);


        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {

            public void onDrawerClosed(View view)
            {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = df.format(c.getTime());
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView)
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = df.format(c.getTime());
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null)
        {
            // on first time display view for first nav item
            displayView(0);
        }

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putString("LogChk", "true");
        edit.commit();
    }
    private class SlideMenuClickListener implements	ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public void onBackPressed()
    {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawers();
        }
        else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


   /*// public void SwitchClass()
    {
        MyClass(3);
    }
*/

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(drawerll);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        MenuItem item= menu.findItem(R.id.action_settings);
        MenuItem item1= menu.findItem(R.id.action_switchclass);
        item.setVisible(false);
        item1.setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }


    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position)
    {
        // update the main content by replacing fragments
        Fragment fragment = null;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        switch (position)
        {
            case 0:
                fragment = new ParentDashboardFragment();
                break;
            case 1:
                //MyClass(1);
                // update selected item and title, then close the drawer
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(drawerll);
                break;
            case 2:
                //fragment = HomeworkList("Homework");
                break;
            case 3:
                //fragment = GiveStar();
                break;
            case 4:
                //fragment =Syllabus();
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(drawerll);
                break;
            case 5:
                //fragment = Quries();
                break;
            case 6:
                //fragment = FunCenter();
                break;
            case 7:
                //fragment = GeneralCommunicationList();
                break;
            case 8:
               //fragment = HomeworkList("Classwork");
                break;
            case 9:
                //fragment = PublicHoliday();
                break;
            case 10:
                Logout();
                break;

            default:
                break;
        }

        if (fragment != null)
        {
            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(drawerll);

        }
        else
        {
            // error in creating fragment
            //Log.e("TeacherActivity", "Error in creating fragment");
        }
    }

    public void Logout()

    {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putString("Login","false" );
        edit.putString("LogChk","true");
        edit.commit();
        finish();
    }

    @Override
    public void setTitle(CharSequence title)
    {
        mTitle = title;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

/*    // for My Class List
    public void MyClass(int i)
    {
       *//* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*//*
        DatabaseQueries qr  = new DatabaseQueries(getApplicationContext());
       // ArrayList<TeacherMyClassModel> res= qr.GetAllSubDivStd();

        String classList="1.,,2nd,,B,,Hindi@@@2.,,4th,,A,,English@@@3.,,2nd,,A,,English@@@4.,,7th,,B,,History@@@5.,,3rd,,B,,English@@@6.,,6th,,B,,History";
        TeacherMyClassDialogBoxActivity newTermDialogFragment = new TeacherMyClassDialogBoxActivity();
        FragmentManager fragmentManager = getFragmentManager();
        Bundle b =new Bundle();
        b.putString("StudentClassList", classList);
        b.putInt("MYCLASS",i);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");
    }

    // for Homework List
    public Fragment HomeworkList(String name)
    {
       *//* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*//*

        String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        fragment = new TeacherHomeworkFragment();
        Bundle bundle = new Bundle();
        bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT",name);
        fragment.setArguments(bundle);
        return fragment;
    }



    //General Quries
    public Fragment GiveStar()
    {
       *//* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*//*

        String quries = "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi_" +
                "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi" ;
        fragment = new TeacherGiveStarFragment();
        Bundle bundle = new Bundle();
        bundle.putString("StudentNameList", quries);
        fragment.setArguments(bundle);
        return fragment;
    }
    //General Communication
    public Fragment GeneralCommunicationList()
    {
       *//* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*//*

        String communication = "20/11/2015,,sports day on 21 november,,Sports Day_19/11/2015,,paraents teacher1 meeting at 2 pm on 20 november,,PTA_18/11/2015,,Story reading competition,,Other_17/11/2015,,paraents teacher1 meeting at 2 pm on 18 november,,PTA" +
                "_15/11/2015,,Singing talent competition,,Other_14/11/2015,,sports day on 15 november,,Sports Day_13/11/2015,,paraents teacher1 meeting at 2 pm on 14 november,,PTA_12/11/2015,,sports day on 13 november,,Sports Day";
        fragment = new TeacherGeneralCommunicationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GeneralCommunicationList", communication);
        fragment.setArguments(bundle);
        return fragment;
    }


    // for Syllabus List
    public Fragment Syllabus()
    {
       *//* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*//*

        String syllabuslist = "First Unit Test Time Table,,NoImage_First Unit Test Syllabus,,Image_Second Unit Test Time Table,,Image_Second Unit Test Syllabus,,Image_First Semester Time Table,,NoImage_First Semester Syllabus,,Image" +
                "_Third Unit Test Time Table,,NoImage_Third unit Test Syllabus,,NoImage_Fourth Unit Test Time Table,,Image_Fourth Unit Test Syllabus,,Image_Second Semester Time Table,,Image_Second sSemester Syllabus,,Image";
        String dictationlist = "Marathi,,lesson1 and 2_Hindi,,lesson no 4 and 5_English,,lesson no 7,8 and9_History,,lesson no 1,2,3,4 and 5";
        fragment = new TeacherTimeTableFragment();
        Bundle bundle = new Bundle();
        bundle.putString("SyllabusList", syllabuslist);
        bundle.putString("DictationList",dictationlist);
        bundle.putInt("Checked", 1);
        fragment.setArguments(bundle);
        return fragment;
    }

    //General Quries
    public Fragment Quries()
    {
       *//* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*//*


        String syllabuslist = "First Unit Test Time Table,,NoImage_First Unit Test Syllabus,,Image_Second Unit Test Time Table,,Image_Second Unit Test Syllabus,,Image_First Semester Time Table,,NoImage_First Semester Syllabus,,Image" +
                "_Third Unit Test Time Table,,NoImage_Third unit Test Syllabus,,NoImage_Fourth Unit Test Time Table,,Image_Fourth Unit Test Syllabus,,Image_Second Semester Time Table,,Image_Second sSemester Syllabus,,Image";
        String dictationlist = "Marathi,,lesson1 and 2_Hindi,,lesson no 4 and 5_English,,lesson no 7,8 and9_History,,lesson no 1,2,3,4 and 5";
        fragment = new TeacherTimeTableFragment();
        Bundle bundle = new Bundle();
        bundle.putString("SyllabusList", syllabuslist);
        bundle.putString("DictationList",dictationlist);
        bundle.putInt("Checked", 1);
        fragment.setArguments(bundle);
        return fragment;
    }

    // for Public Holiday List
    public Fragment PublicHoliday()
    {
       *//* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*//*

        String publicholiday = "Independence Day,,15/08/2015,,15/08/2015_Ganesh Chaturthi,,17/09/2015,,17/09/2015_Gandhi Jayanti,,02/10/2015,,02/10/2015_Dussehra" +
                ",,22/10/2015,,22/10/2015_Diwali Holiday,,09/11/2015,,26/11/2015_Merry Christmas,,25/12/2015,,25/12/2015_Republic Day,,26/01/2016,,26/01/2016_Mahashivratri,,17/02/2016,,17/02/2016_Holi,,06/03/2016,,06/03/2016_Gudi Padawa,,21/03/2016,,21/03/2016" +
                "_Good Friday,,03/04/2016,,03/04/2016";
        fragment = new TeacherPublicHolidayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("PublicHolidayList", publicholiday);
        fragment.setArguments(bundle);
        return fragment;
    }
    // for Fun Center List
    public Fragment FunCenter()
    {
       *//* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*//*

        String classList="1.,,2nd,,B,,Hindi@@@2.,,4th,,A,,English@@@3.,,2nd,,A,,English@@@4.,,7th,,B,,History@@@5.,,3rd,,B,,English@@@6.,,6th,,B,,History";
        TeacherMyClassDialogBoxActivity newTermDialogFragment = new TeacherMyClassDialogBoxActivity();
        FragmentManager fragmentManager = getFragmentManager();
        Bundle b =new Bundle();
        b.putString("StudentClassList", classList);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");
        return fragment;
    }*/
   /* // for View Star List
    public void TrackPupil()
    {
        FragmentManager fragmentManager = getFragmentManager();
        TeacherTrackingDialogBoxActivity fragment = new TeacherTrackingDialogBoxActivity();
        fragment.setCancelable(false);
        fragment.show(fragmentManager, "Dialog!");

    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }*/


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

}
