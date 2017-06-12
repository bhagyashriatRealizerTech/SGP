package com.realizer.schoolgenie.parent.pupil;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.pupil.backend.DALMyPupilInfo;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.GetImages;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.utils.Utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Win on 11/9/2015.
 */
public class ParentMyPupilInfoFragment extends Fragment implements FragmentBackPressedListener {
    TextView name,studclass,studdiv,rollno,dob,hobbies,bloodgroup,teachername,contactno,address,profile_init,emergencyno;
    //Button viewAttendance;
    MenuItem search,done;
    DALMyPupilInfo qr;
    ImageView profile_pic;
    SharedPreferences sharedpreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.parent_myclass_mypupil_layout, container, false);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("My Pupil", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        name = (TextView)rootView.findViewById(R.id.txtPupilName);
        studclass = (TextView)rootView.findViewById(R.id.txtClass);
        studdiv = (TextView)rootView.findViewById(R.id.txtDivision);
        rollno = (TextView)rootView.findViewById(R.id.txtRollNo);
        dob = (TextView)rootView.findViewById(R.id.txtDOB);
        hobbies = (TextView)rootView.findViewById(R.id.txthobbies);
        bloodgroup = (TextView)rootView.findViewById(R.id.txtBloodGroup);
        teachername = (TextView)rootView.findViewById(R.id.txtClassTeacherName);
        contactno = (TextView)rootView.findViewById(R.id.txtContactNo);
        address = (TextView)rootView.findViewById(R.id.txtAddress);
        emergencyno=(TextView)rootView.findViewById(R.id.txtEmergencyContactNo);
        profile_pic = (ImageView) rootView.findViewById(R.id.profilepicpupil);
        profile_init = (TextView)rootView.findViewById(R.id.txtinitialPupil);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String getValueBack = sharedpreferences.getString("UserName", "");

        qr = new DALMyPupilInfo(getActivity());
        String stud[]= qr.GetAllTableData(getValueBack);
        name.setText(stud[0]+" "+stud[1]+" "+stud[2]);
        studclass.setText(stud[3]);
        studdiv.setText(stud[4]);
        //rollno.setText(stud[5]);
        rollno.setText(stud[5]);

       /* String timestamp = stud[6].split("\\(")[1].split("\\-")[0];
        Date createdOn = new Date(Long.parseLong(timestamp));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String formattedDate = sdf.format(createdOn);
        dob.setText(formattedDate);*/
        if( !stud[6].equals("") &&  !stud[6].equals(null) &&  !stud[6].equals("null")) {
            String timestamp = stud[6].trim().split("\\(")[1].trim().split("\\-")[0];
            if(timestamp.isEmpty())
                timestamp = stud[6].trim().split("\\(")[1].trim().split("\\-")[1];
            if(!timestamp.isEmpty()) {
                Date createdOn = new Date(Long.parseLong(timestamp));
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                String formattedDate = sdf.format(createdOn);
                dob.setText(formattedDate);
            }
            else
            {
                dob.setText("-");
            }
        }
        else
            dob.setText("No Birthday Found");

        hobbies.setText(stud[7]);
        bloodgroup.setText(stud[8]);
        teachername.setText("Mr. Aditya Ghoman");
        contactno.setText(stud[10]);
        address.setText(stud[11]);
        emergencyno.setText(stud[15]);
        //setting dp
        String thumbnail=stud[14];
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String urlString = preferences.getString("ThumbnailID","");
        if (urlString!=null && !urlString.equalsIgnoreCase("null"))
        {
            profile_init.setVisibility(View.GONE);
            profile_pic.setVisibility(View.VISIBLE);
            String newURL= Utility.getURLImage(urlString);
            if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                new GetImages(newURL,profile_pic,newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
            else
            {
                File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                profile_pic.setImageBitmap(bitmap);
            }
        }
        else
        {
            String firstName=stud[0];
            String lastName=stud[2];
            profile_pic.setVisibility(View.GONE);
            profile_init.setVisibility(View.VISIBLE);
            profile_init.setText( String.valueOf(firstName.charAt(0)).toUpperCase()+ String.valueOf(lastName.charAt(0)).toUpperCase());
        }

      /*  viewAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParentAttendanceCalendarFragment fragment = new ParentAttendanceCalendarFragment();
                Singleton.setFragment(fragment);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.commit();
            }
        });*/
        return rootView;
    }

    @Override
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_view_attendance, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.view_attendance:

                ParentAttendanceCalendarFragment fragment = new ParentAttendanceCalendarFragment();
                Singleton.setFragment(fragment);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.commit();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
