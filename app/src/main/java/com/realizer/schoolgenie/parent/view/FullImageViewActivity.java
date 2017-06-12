package com.realizer.schoolgenie.parent.view;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.utils.GetImages;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.utils.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by Win on 11/25/2015.
 */
public class FullImageViewActivity extends FragmentActivity {
    static int NUM_ITEMS ;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    static String[] IMG;
    static ImageView imageView;
    static  ProgressBar firstBar;
    static ActionBar bar;
    static Bitmap decodedByte;
    static TextView txtcnt;
    static Bitmap barr[];
    static ImageView imgv[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.fragment_page);
        Bundle bundle = getIntent().getExtras();
        String headertext = bundle.getString("HEADERTEXT");
        int position= bundle.getInt("POSITION");
        int positionList= bundle.getInt("ListNo");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String path = preferences.getString("HomeworkImage", "");
        try {

            try {
                if (headertext.equalsIgnoreCase("Homework") || headertext.equalsIgnoreCase("Classwork"))
                {
                    JSONArray jarr = new JSONArray(path);
                    NUM_ITEMS = jarr.length();
                    IMG = new String[NUM_ITEMS];
                    barr = new Bitmap[NUM_ITEMS];
                    imgv = new ImageView[NUM_ITEMS];

                    for(int i=0;i<NUM_ITEMS;i++)
                    {
                        IMG[i] = jarr.getString(i);
                    }
                }
                else
                {
                    IMG=path.split("@@@");
                    NUM_ITEMS = IMG.length;
                    //IMG = new String[NUM_ITEMS];
                    barr = new Bitmap[NUM_ITEMS];
                    imgv = new ImageView[NUM_ITEMS];
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext());
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
        viewPager.setAdapter(imageFragmentPagerAdapter);
        viewPager.setCurrentItem(position);
    }



    @Override
    public void onBackPressed() {
        /*Singleton.setFragment(Singleton.getMainFragment());
        finish();*/
        Singleton.setFragment(Singleton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();

        finish();
    }

    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.fullimageview_parent, container, false);
            imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            txtcnt = (TextView) swipeView.findViewById(R.id.txtcounter);
            firstBar = (ProgressBar) swipeView.findViewById(R.id.progressBar);
            firstBar.setVisibility(View.GONE);
            Bundle bundle = getArguments();
            final int position = bundle.getInt("position");
            Log.d("FILENAME", "" + IMG[position]);

            String filePath = IMG[position];

            if (filePath.equalsIgnoreCase("NoData"))
            {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.image_not_found));
                txtcnt.setText("" + (position + 1) + " / " + NUM_ITEMS);
            }
            else {
                final String newPath = new Utility().getURLImage(filePath);

                if (newPath.equalsIgnoreCase("")) {
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.sorryimage);
                    imageView.setImageBitmap(icon);
                    firstBar.setVisibility(View.GONE);
                } else {
                    if (!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                        // new GetHCImages(newPath,position).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                        firstBar.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.GONE);
                        Picasso.with(getActivity()).load(newPath).error(R.mipmap.ic_launcher)
                                .into(imageView, new Callback.EmptyCallback() {
                                    @Override
                                    public void onSuccess() {
                                        firstBar.setVisibility(View.GONE);
                                        imageView.setVisibility(View.VISIBLE);
                                        barr[position] = decodedByte;
                                        imageView.setImageBitmap(decodedByte);
                                        txtcnt.setText("" + (position + 1) + " / " + NUM_ITEMS);
                                        imgv[position] = imageView;
                                    }

                                    @Override
                                    public void onError() {
                                        firstBar.setVisibility(View.GONE);
                                        imageView.setVisibility(View.VISIBLE);
                                    }
                                });

                        Picasso.with(getActivity())
                                .load(newPath)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                        // not being called the first time
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                       /* if(bitmap != null) {
                                            if (!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                                                ImageStorage.saveToSdCard(bitmap, newPath.split("/")[newPath.split("/").length - 1]);
                                            }
                                            holder.imgview.setVisibility(View.VISIBLE);
                                        }*/
                                                new GetImages(newPath, imageView, newPath.split("/")[newPath.split("/").length - 1]).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newPath);
                                                //holder.imgview.setVisibility(View.VISIBLE);
                                            }
                                        }).start();
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });

                    }
                    else
                    {
                        File image = ImageStorage.getImage(newPath.split("/")[newPath.split("/").length-1]);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        decodedByte = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                        barr[position] = decodedByte;
                        imageView.setImageBitmap(decodedByte);
                        txtcnt.setText("" + (position + 1) + " / " + NUM_ITEMS);
                        imgv[position] = imageView;
                    }
               /* String newPath=new Utility().getURLImage(filePath);

                File image = ImageStorage.getImage(newPath.split("/")[newPath.split("/").length-1]);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                decodedByte = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);

                if(decodedByte !=null)
                {
                    barr[position] = decodedByte;
                    imageView.setImageBitmap(decodedByte);
                    txtcnt.setText("" + (position + 1) + " / " + NUM_ITEMS);
                    imgv[position] = imageView;
                }
                else
                {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.image_not_found));
                    txtcnt.setText("" + (position + 1) + " / " + NUM_ITEMS);
                }*/
                }
            }

            return swipeView;
        }

        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_imageview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rotate:
                RotateImg();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void RotateImg()
    {

        Bitmap bitmap = barr[viewPager.getCurrentItem()];
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        imgv[viewPager.getCurrentItem()].setImageBitmap(rotated);
    }
}
