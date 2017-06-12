package com.realizer.schoolgenie.parent.timetable;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.chat.backend.DALQueris;
import com.realizer.schoolgenie.parent.chat.model.ParentQueriesTeacherNameListModel;
import com.realizer.schoolgenie.parent.funcenter.model.RowItem;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.FileUtils;
import com.realizer.schoolgenie.parent.utils.GIFView;
import com.realizer.schoolgenie.parent.utils.GetImages;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.utils.StoreBitmapImages;
import com.realizer.schoolgenie.parent.utils.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Bhagyashri on 8/29/2016.
 */
public class ParentTimeTableDetailFragment extends Fragment implements FragmentBackPressedListener {

    TextView txtTitle,txtDate,txtTeacherName,txtDescription,txtProgressPercentage;

    String title,image;
    ImageView image1;
    private ProgressBar firstBar = null;
   // Bitmap bitmap;
    DALQueris dbQ;
   // MessageResultReceiver resultReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.teacher_timetable_detail_layout, container, false);
        initiateView(rootView);

        Bundle bundle= getArguments();
        String sdate=bundle.getString("TimeTableDate").split(" ")[0];
        String[] ttdate=sdate.split("/");
        int month=Integer.valueOf(ttdate[0]);
        String mon= Config.getMonth(month);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Time Table", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        dbQ=new DALQueris(getActivity());
        ParentQueriesTeacherNameListModel result=dbQ.GetQueryTableData(bundle.getString("TeacherName"));
        txtTitle.setText(bundle.getString("Title"));
        txtDate.setText(ttdate[1]+" "+mon+" "+ttdate[2]);
        txtTeacherName.setText(result.getName());
        txtDescription.setText(bundle.getString("TimeTableText"));
        //gifimage=(GIFView) rootView.findViewById(R.id.gifimageview);
        title = bundle.getString("Title");
        image = bundle.getString("TimeTableImage");


        final String newPath = new Utility().getURLImage(image);
        if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
            //new GetTTImages(newPath).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newPath);
            //new StoreBitmapImages(newPath,newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);

            Picasso.with(getActivity()).load(newPath).error(R.mipmap.ic_launcher)
                    .into(image1, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            firstBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            firstBar.setVisibility(View.GONE);
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
                                    if(bitmap != null) {
                                        if (!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                                            ImageStorage.saveToSdCard(bitmap, newPath.split("/")[newPath.split("/").length - 1]);
                                        }
                                    }
                                   /* File sdcard = Environment.getExternalStorageDirectory() ;

                                    File folder = new File(sdcard.getAbsoluteFile(), ".UserDP");
                                    folder.mkdir();
                                    File file = new File(folder.getAbsoluteFile(), newPath.split("/")[newPath.split("/").length - 1]) ;

                                    if (file.exists())
                                        //file.delete();
                                    try {
                                       // file.mkdir();
                                        FileOutputStream ostream = new FileOutputStream(file);
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                        ostream.close();
                                        ostream.flush();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }*/
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
            firstBar.setVisibility(View.GONE);
            txtProgressPercentage.setVisibility(View.GONE);
            File image = ImageStorage.getImage(newPath.split("/")[newPath.split("/").length - 1]);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
            image1.setImageBitmap(bitmap);
        }

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPath.contains("http"))
                {
                    Intent i = new Intent(getActivity(),TimeTableFullViewActivity.class);
                    i.putExtra("Path", newPath);
                    i.putExtra("TTName",title);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getActivity(),"Invalid Image..!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    public void initiateView(View view)
    {
        txtTitle = (TextView)view.findViewById(R.id.txttitle);
        txtDate = (TextView)view.findViewById(R.id.txttimetabledate);
        txtTeacherName = (TextView)view.findViewById(R.id.txtteacherName);
        txtDescription = (TextView)view.findViewById(R.id.txtdescription);
        image1 = (ImageView) view.findViewById(R.id.btnCapturePicture1);
        firstBar = (ProgressBar) view.findViewById(R.id.progressBar);
        txtProgressPercentage = (TextView)view.findViewById(R.id.txtProgressPercentage);
    }


    @Override
    public void onFragmentBackPressed() {
        Singleton.setFragment(Singleton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }


    public class GetTTImages extends AsyncTask<Object, Integer, Object> {
        private String requestUrl;
        int progress;
        private Bitmap bitmap ;
        private FileOutputStream fos;
        //int i=0;
        public GetTTImages(String requestUrl) {
            this.requestUrl = requestUrl;
        }

        @Override
        protected void onPreExecute() {
            image1.setVisibility(View.GONE);
            firstBar.setVisibility(View.VISIBLE);
            txtProgressPercentage.setVisibility(View.VISIBLE);
            firstBar.setMax(100);
            firstBar.setProgress(1);

          /*  ObjectAnimator anim = ObjectAnimator.ofInt(firstBar, "progress", 0, 100);
            anim.setDuration(15000);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.start();*/
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            firstBar.setProgress(values[0]);
            int value=firstBar.getProgress()+firstBar.getSecondaryProgress();
            Log.d("ProgressValue",value+"");
            txtProgressPercentage.setText(value+"/100");
        }

        @Override
        protected Object doInBackground(Object... objects) {
            InputStream inputStream = null;
            BufferedOutputStream outputStream = null;
            int count = 0;
            try {
                URL imageURL = new URL(requestUrl);
                URLConnection conn = imageURL.openConnection();
                conn.connect();
                int lenghtOfFile = conn.getContentLength();
                inputStream = new BufferedInputStream(imageURL.openStream());
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

                outputStream = new BufferedOutputStream(dataStream);


                byte data[] = new byte[1024];
                long total = 0;

              /*  while ((count = inputStream.read(data)) != -1) {
                    total += count;
		            *//*publishing progress update on UI thread.
		            Invokes onProgressUpdate()*//*
                    progress = (int)((total*100)/lenghtOfFile);
                    publishProgress(progress);

                    // writing data to byte array stream
                    outputStream.write(data, 0, count);
                }*/

                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    publishProgress((int)((total*100)/lenghtOfFile));
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
                bitmap = BitmapFactory.decodeStream(conn.getInputStream());



           /* InputStream inputStream = null;
            BufferedOutputStream outputStream = null;
            int count = 0;
            try {
                URL url = new URL(requestUrl);
                URLConnection conn = url.openConnection();
                // bitmap = BitmapFactory.decodeStream(conn.getInputStream());

                int lenghtOfFile = conn.getContentLength();
                inputStream = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

                outputStream = new BufferedOutputStream(dataStream);

                byte data[] = new byte[512];
                long total = 0;

                while ((count = inputStream.read(data)) != -1) {
                    total += count;
		            *//*publishing progress update on UI thread.
		            Invokes onProgressUpdate()*//*
                    publishProgress((int)((total*100)/lenghtOfFile));

                    // writing data to byte array stream
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 1;

                byte[] bytes = dataStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,bmOptions);*/

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            firstBar.setVisibility(View.GONE);
            txtProgressPercentage.setVisibility(View.GONE);
            image1.setVisibility(View.VISIBLE);
            image1.setImageBitmap(bitmap);
            if(bitmap != null) {
                if (!ImageStorage.checkifImageExists(requestUrl.split("/")[requestUrl.split("/").length - 1])) {
                    ImageStorage.saveToSdCard(bitmap, requestUrl.split("/")[requestUrl.split("/").length - 1]);
                }
            }
        }
    }
}
