package com.realizer.schoolgenie.parent.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterGalleryModel;
import com.realizer.schoolgenie.parent.homework.backend.DALHomework;
import com.realizer.schoolgenie.parent.homework.model.ParentHomeworkListModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shree on 11/28/2016.
 */
public class FullImageviewFunCenterPager extends FragmentActivity {

    public Context mContext;
    // Declare Variable
    String uuid;
    int position=0;
    FullImageviewFuncenterPagerAdpater pageradapter;
    String activityName;
    DALHomework db;
    ArrayList<ParentFunCenterGalleryModel> chatDownloadedThumbnailList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler( FullImageviewFunCenterPager.this));
        super.onCreate(savedInstanceState);
        // Get the view from view_pager.xml
        setContentView(R.layout.fragment_page);
        db=new DALHomework(FullImageviewFunCenterPager.this);
        // Retrieve data from MainActivity on item click event
        Bundle bundle = getIntent().getExtras();
        uuid = bundle.getString("HWUUID");
        activityName = bundle.getString("HEADERTEXT");
        position = bundle.getInt("POSITION");
        mContext = FullImageviewFunCenterPager.this;
        final ViewPager viewpager = (ViewPager) findViewById(R.id.pager);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String path = preferences.getString("HomeworkImage", "");
        String[] IMG=path.split("@@@");
        chatDownloadedThumbnailList=new ArrayList<>();
        for (int i=0;i<IMG.length;i++)
        {
            ParentFunCenterGalleryModel obj=new ParentFunCenterGalleryModel();
            obj.setImage(IMG[i]);
            chatDownloadedThumbnailList.add(obj);
        }
       // chatDownloadedThumbnailList=db.GetAllHomeworkByWork(activityName);
        //getting current image position
       /* for (int i=0;i<chatDownloadedThumbnailList.size();i++)
        {
            if (uuid.equals(chatDownloadedThumbnailList.get(i).getHwUUID()))
            {
                position=i;
                break;
            }
        }*/
        // Set the images into ViewPager
        pageradapter = new FullImageviewFuncenterPagerAdpater(mContext, chatDownloadedThumbnailList);
        viewpager.setAdapter(pageradapter);
        // Show images following the position
        viewpager.setCurrentItem(position);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                Log.d("onPageScrolled", viewpager.getCurrentItem() + "");
            }

            @Override
            public void onPageSelected(int i) {
                Log.d("PageFarhan",i+"onPageSelected");
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                Log.d("onPageScrollState",viewpager.getAdapter().getCount()+"");
            }
        });
    }



}
