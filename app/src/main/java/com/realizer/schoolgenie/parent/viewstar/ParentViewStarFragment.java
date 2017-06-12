package com.realizer.schoolgenie.parent.viewstar;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.chat.backend.DALQueris;
import com.realizer.schoolgenie.parent.chat.model.ParentQueriesTeacherNameListModel;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.GetImages;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.viewstar.adapter.ParentViewStarListAdapter;
import com.realizer.schoolgenie.parent.viewstar.backend.DALViewStar;
import com.realizer.schoolgenie.parent.viewstar.model.ParentViewStarModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ParentViewStarFragment extends Fragment implements AdapterView.OnItemSelectedListener,FragmentBackPressedListener {
    ImageView blinkingImageView,StarGiven1,StarGiven2,StarGiven3;
    String label;
    TextView txtusername, txtteacher, txtNoStar;
    DALViewStar qr;
    String getValueBack;
    Spinner SpinnerExample;
    LinearLayout outLayout;
    ArrayList<ParentViewStarModel> viewStarList;
    ListView lsttname;
    SharedPreferences sharedpreferences;
    DALQueris dbQ;
	public ParentViewStarFragment(){}
    TextView initial;
    ImageView profilepic;;
    ParentQueriesTeacherNameListModel result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.parent_viewstar_layout, container, false);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("View Star", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        blinkingImageView = (ImageView) rootView.findViewById(R.id.ImageView01);
        SpinnerExample = (Spinner) rootView.findViewById(R.id.spinner);
        //txtusername = (TextView) rootView.findViewById(R.id.txtusername);
        lsttname = (ListView) rootView.findViewById(R.id.lstviewStar);
        txtteacher = (TextView) rootView.findViewById(R.id.txtnames);
        txtNoStar = (TextView) rootView.findViewById(R.id.tvNoDataMsg);
        outLayout = (LinearLayout) rootView.findViewById(R.id.outerLayout);
        profilepic = (ImageView) rootView.findViewById(R.id.profile_image_view);
        initial = (TextView) rootView.findViewById(R.id.txtinitial);

        dbQ=new DALQueris(getActivity());
        result=new ParentQueriesTeacherNameListModel();


        final Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        blinkingImageView.startAnimation(animation);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getValueBack = sharedpreferences.getString("UserName", "");
        qr = new DALViewStar(getActivity());

        List<String> labels = qr.getSubjects();
        if (labels.size()!=0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, labels);
            dataAdapter.setDropDownViewResource(R.layout.viewstar_subject_spiner);
            SpinnerExample.setAdapter(dataAdapter);
            SpinnerExample.setSelection(0);
            SpinnerExample.setOnItemSelectedListener(this);
        }
        else
        {
            SpinnerExample.setVisibility(View.GONE);
            outLayout.setVisibility(View.GONE);
            lsttname.setVisibility(View.GONE);
            txtNoStar.setVisibility(View.VISIBLE);
        }

    return rootView;
    }
    public static Point getScreenDimensions(Context context) {
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        return new Point(width, height);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        label  = parent.getItemAtPosition(position).toString();
        String stud[]= qr.GetAllViewStarTableData(label);

        String subject = (stud[0]);
        String date = (stud[1]);
        String comment = (stud[2]);
        String teacher = (stud[3]);
        String stargiven = (stud[4]);
        txtteacher.setText(teacher);
        //txtcomment.setText(comment);
        result=dbQ.GetTeacherData(subject);
        viewStarList= qr.GetViewstarInfoData(label);
        lsttname.setAdapter(new ParentViewStarListAdapter(getActivity(), viewStarList));
        //txtusername.setText(subject);


        String name[] = teacher.trim().split(" ");
        if(result.getThumbnail() != null && !result.getThumbnail().equals("") && !result.getThumbnail().equalsIgnoreCase("null"))
        {
            String urlString = result.getThumbnail();
            StringBuilder sb=new StringBuilder();
            for(int i=0;i<urlString.length();i++)
            {
                char c='\\';
                if (urlString.charAt(i) =='\\')
                {
                    urlString.replace("\"","");
                    sb.append("/");
                }
                else
                {
                    sb.append(urlString.charAt(i));
                }
            }
            String newURL=sb.toString();
            initial.setVisibility(View.GONE);
            profilepic.setVisibility(View.VISIBLE);
            if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                new GetImages(newURL,profilepic,newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
            else
            {
                File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                initial.setVisibility(View.GONE);
                profilepic.setVisibility(View.VISIBLE);
                profilepic.setImageBitmap(bitmap);
            }
        }
        else {
            initial.setVisibility(View.VISIBLE);
            profilepic.setVisibility(View.GONE);
            char fchar = name[0].toUpperCase().charAt(0);
            char lchar = name[0].toUpperCase().charAt(0);
            for (int i = 0; i < name.length; i++) {
                if (!name[i].equals("") && i == 0)
                    fchar = name[i].toUpperCase().charAt(0);
                else if (!name.equals("") && i == (name.length - 1))
                    lchar = name[i].toUpperCase().charAt(0);

            }

            initial.setText(fchar + "" + lchar);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }
}
