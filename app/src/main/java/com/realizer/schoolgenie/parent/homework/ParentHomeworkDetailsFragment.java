package com.realizer.schoolgenie.parent.homework;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.chat.backend.DALQueris;
import com.realizer.schoolgenie.parent.chat.model.ParentQueriesTeacherNameListModel;
import com.realizer.schoolgenie.parent.funcenter.ParentFunCenterGalleryFragment;
import com.realizer.schoolgenie.parent.funcenter.adapter.CustomListViewAdapter;
import com.realizer.schoolgenie.parent.funcenter.adapter.ParentHomeworkDetailsFragmentAdapter;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterGalleryModel;
import com.realizer.schoolgenie.parent.homework.backend.DALHomework;
import com.realizer.schoolgenie.parent.homework.model.ParentHomeworkListModel;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.utils.Utility;
import com.realizer.schoolgenie.parent.view.FullImageViewPager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by shree on 11/25/2016.
 */
public class ParentHomeworkDetailsFragment extends Fragment implements FragmentBackPressedListener {

    TextView txtSubject,txtDate,txtTeacherName,txtDescription,txtdevider;
    TextView txtstd ,txtclss;
    String htext,path;
    GridView folderdgridview;
    // ImageView image1,image2,image3,image4,image5,image6,image7,image8,image9;
    ImageView image1,image2,image3;
    private ProgressBar firstBar = null;
    private ProgressBar secondBar = null;
    private ProgressBar thirdBar = null;
    LinearLayout imagelayout;
    ProgressDialog progressDialog;
    DALHomework qr;
    String hwDate;
    String hwuuid;
    int imgid=0;
    ParentHomeworkDetailsFragmentAdapter listViewAdapter;
    ArrayList<ParentHomeworkListModel> presentImgList;
    String finalBitmapString="";
    DALQueris dbQ;
    String[] IMG ;
    List<ParentHomeworkListModel> chatDownloadedThumbnailList;
    List<ParentHomeworkListModel> filteredHCList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.parent_homework_details_layout, container, false);
        initiateView(rootView);
        folderdgridview= (GridView) rootView.findViewById(R.id.homeworkgridView);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("SyncStd", ""));
        txtclss.setText(preferences.getString("SyncDiv", ""));

        Bundle bundle= getArguments();
        htext = bundle.getString("HEADERTEXT");
        path = bundle.getString("HomeworkImage");
        hwuuid = bundle.getString("HWUUid");
        imgid=bundle.getInt("Imageid");
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        presentImgList=new ArrayList<>();
        txtSubject.setText(bundle.getString("SubjectName"));

        String oldDate=bundle.getString("HomeworkDate");
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;
        try {
            date = fmt.parse(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MMM-yyyy");
        oldDate= fmtOut.format(date);
        txtDate.setText(oldDate);
        hwDate=bundle.getString("HomeworkDate");

        dbQ=new DALQueris(getActivity());
        ParentQueriesTeacherNameListModel result=dbQ.GetQueryTableData(bundle.getString("TeacherName"));
        txtTeacherName.setText( result.getName());
        String hwText=bundle.getString("HomeworkText");
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<hwText.length();i++)
        {
            char c='\\';
            if (hwText.charAt(i) =='[' || hwText.charAt(i) ==']' || hwText.charAt(i) =='"')
            {
                hwText.replace("[]\"", "");
            }
            else
            {
                sb.append(hwText.charAt(i));
            }
        }

        txtDescription.setText(sb.toString());

        if(path.equalsIgnoreCase("NoImage")) {
          /*  frameimageClik.setVisibility(View.GONE);*/
            txtdevider.setVisibility(View.GONE);
            imagelayout.setVisibility(View.GONE);
        }
        else {

          /*  frameimageClik.setVisibility(View.VISIBLE);*/
            txtdevider.setVisibility(View.VISIBLE);
            imagelayout.setVisibility(View.VISIBLE);
            DALHomework db=new DALHomework(getActivity());
            chatDownloadedThumbnailList=db.GetAllHomeworkByWork(htext);
            //getting current image position
            filteredHCList=new ArrayList<>();
            int position=0;
            for (int i=0;i<chatDownloadedThumbnailList.size();i++)
            {
                if (hwuuid.equals(chatDownloadedThumbnailList.get(i).getHwUUID()))
                {
                    filteredHCList.add(chatDownloadedThumbnailList.get(i));
                }
            }

            if (filteredHCList.size()>0)
            {
                listViewAdapter = new ParentHomeworkDetailsFragmentAdapter(getActivity(), filteredHCList);
                folderdgridview.setAdapter(listViewAdapter);
                listViewAdapter.notifyDataSetChanged();
            }
        }

        folderdgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), FullImageViewPager.class);
                i.putExtra("HEADERTEXT", htext);
                i.putExtra("HWUUID", hwuuid);
                startActivity(i);
            }
        });


        return rootView;
    }

    public void initiateView(View view)
    {
        txtSubject = (TextView)view.findViewById(R.id.txtsubject);
        txtDate = (TextView)view.findViewById(R.id.txthomeworkdate);
        txtTeacherName = (TextView)view.findViewById(R.id.txtteacherName);
        txtDescription = (TextView)view.findViewById(R.id.txtdescription);
        txtstd  = (TextView) view.findViewById(R.id.txttclassname);
        txtclss = (TextView) view.findViewById(R.id.txttdivname);
        /*frameimageClik = (FrameLayout) view.findViewById(R.id.frameimageClik);*/
        txtdevider = (TextView)view.findViewById(R.id.txtDivider);
        firstBar = (ProgressBar)view.findViewById(R.id.progressBar1);
        imagelayout = (LinearLayout)view.findViewById(R.id.imagelayout);
    }


    private void loadPhoto(int pos) {
        Intent i = new Intent(getActivity(), FullImageViewPager.class);
        i.putExtra("HEADERTEXT", htext);
        i.putExtra("HWUUID", hwuuid);
        startActivity(i);
    }

    @Override
    public void onFragmentBackPressed() {
        Singleton.setFragment(Singleton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }
}
