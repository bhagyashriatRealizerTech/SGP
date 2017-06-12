package com.realizer.schoolgenie.parent.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.homework.backend.DALHomework;
import com.realizer.schoolgenie.parent.homework.model.ParentHomeworkListModel;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.StoreBitmapImages;
import com.realizer.schoolgenie.parent.utils.Utility;

import java.util.List;

public class FullImageViewPager extends FragmentActivity {

	public Context mContext;
	// Declare Variable
	String uuid;
    int position=0;
    FullImageViewPagerAdapter pageradapter;
	String activityName;
    DALHomework db;
	List<ParentHomeworkListModel> chatDownloadedThumbnailList;


	@Override
	public void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler( FullImageViewPager.this));
		super.onCreate(savedInstanceState);
		// Get the view from view_pager.xml
		setContentView(R.layout.fragment_page);
        db=new DALHomework(FullImageViewPager.this);
		// Retrieve data from MainActivity on item click event
        Bundle bundle = getIntent().getExtras();
        uuid = bundle.getString("HWUUID");
        activityName = bundle.getString("HEADERTEXT");
		mContext = FullImageViewPager.this;

		final ViewPager viewpager = (ViewPager) findViewById(R.id.pager);

		chatDownloadedThumbnailList=db.GetAllHomeworkByWork(activityName);
      //getting current image position
        for (int i=0;i<chatDownloadedThumbnailList.size();i++)
        {
            if (uuid.equals(chatDownloadedThumbnailList.get(i).getHwUUID()))
            {
                position=i;
                break;
            }
        }
		// Set the images into ViewPager
		pageradapter = new FullImageViewPagerAdapter(mContext, chatDownloadedThumbnailList);
		viewpager.setAdapter(pageradapter);
		// Show images following the position
		viewpager.setCurrentItem(position);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
           @Override
           public void onPageScrolled(int i, float v, int i2) {
               Log.d("onPageScrolled", viewpager.getCurrentItem()+"");
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