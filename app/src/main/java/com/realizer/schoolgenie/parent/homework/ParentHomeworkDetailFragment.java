package com.realizer.schoolgenie.parent.homework;

import android.app.Fragment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.chat.backend.DALQueris;
import com.realizer.schoolgenie.parent.chat.model.ParentQueriesTeacherNameListModel;
import com.realizer.schoolgenie.parent.homework.backend.DALHomework;
import com.realizer.schoolgenie.parent.homework.model.ParentHomeworkListModel;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.utils.Utility;
import com.realizer.schoolgenie.parent.view.FullImageViewActivity;
import com.realizer.schoolgenie.parent.view.FullImageViewPager;

import org.json.JSONArray;
import org.json.JSONException;

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
 * Created by Bhagyashri on 8/29/2016.
 */
public class ParentHomeworkDetailFragment extends Fragment implements FragmentBackPressedListener {

    TextView txtSubject,txtDate,txtTeacherName,txtDescription,txtdevider;
    TextView txtstd ,txtclss;
    String htext,path;
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
    ArrayList<ParentHomeworkListModel> presentImgList;
    String finalBitmapString="";
    DALQueris dbQ;
    String[] IMG ;
    List<ParentHomeworkListModel> chatDownloadedThumbnailList;
    List<ParentHomeworkListModel> filteredHCList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.teacher_homework_detail_layout, container, false);
        initiateView(rootView);

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

            for (int i=0;i<filteredHCList.size();i++)
            {
                String newPath=new Utility().getURLImage(filteredHCList.get(i).getImage());
                if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                    new GetHCImages(newPath,position).execute(newPath);
                }
                else
                {
                    File image = ImageStorage.getImage(newPath.split("/")[newPath.split("/").length - 1]);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                    if (i==0)
                    {
                        image1.setImageBitmap(bitmap);
                        firstBar.setVisibility(View.GONE);
                    }
                    else if (i==1)
                    {
                        image2.setImageBitmap(bitmap);
                        secondBar.setVisibility(View.GONE);
                    }
                    else if (i==2)
                    {
                        image3.setImageBitmap(bitmap);
                        thirdBar.setVisibility(View.GONE);
                    }
                }
            }

           /* if(filteredHCList.size()==1) {
                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.GONE);
                image3.setVisibility(View.GONE);
            }
            else if(filteredHCList.size()==2) {
                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.VISIBLE);
                image3.setVisibility(View.GONE);

            }
            else if(filteredHCList.size()==3) {
                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.VISIBLE);
                image3.setVisibility(View.VISIBLE);
            }*/
        }

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath = finalBitmapString + "@@@0";
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ImageString", tempPath);
                editor.putString("HomeworkImage", path);
                editor.commit();
                loadPhoto(0);
               /* for(int i=0;i<IMG.length;i++) {
                    String newPath = new Utility().getURLImage(IMG[i]);

                    if (!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                        //new StoreBitmapImages(newPath, newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                        new GetImages(newPath,image1,newPath.split("/")[newPath.split("/").length-1]).execute(newPath);
                       Toast.makeText(getActivity(), "Please wait...Image is loading", Toast.LENGTH_LONG).show();
                    } else {
                        String tempPath = finalBitmapString + "@@@0";
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("ImageString", tempPath);
                        editor.putString("HomeworkImage", path);
                        editor.commit();
                        loadPhoto(0);
                    }
                }*/
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   for(int i=0;i<IMG.length;i++) {
                    String newPath = new Utility().getURLImage(IMG[i]);

                    if (!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                        //new StoreBitmapImages(newPath, newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                        new GetImages(newPath,image2,newPath.split("/")[newPath.split("/").length-1]).execute(newPath);
                        Toast.makeText(getActivity(), "Please wait...Images are loading", Toast.LENGTH_LONG).show();
                    } else {
                        String tempPath =  finalBitmapString+"@@@1";
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("ImageString", tempPath);
                        editor.putString("HomeworkImage", path);
                        editor.commit();
                        loadPhoto(1);
                    }
                }*/
                String tempPath =  finalBitmapString+"@@@1";
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ImageString", tempPath);
                editor.putString("HomeworkImage", path);
                editor.commit();
                loadPhoto(1);
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath =  finalBitmapString+"@@@2";
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ImageString", tempPath);
                editor.putString("HomeworkImage", path);
                editor.commit();
                loadPhoto(2);
                /*for(int i=0;i<IMG.length;i++) {
                    String newPath = new Utility().getURLImage(IMG[i]);

                    if (!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                        //new StoreBitmapImages(newPath, newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                        new GetImages(newPath,image3,newPath.split("/")[newPath.split("/").length-1]).execute(newPath);
                        Toast.makeText(getActivity(), "Please wait...Images are loading", Toast.LENGTH_LONG).show();
                    } else {
                        String tempPath =  finalBitmapString+"@@@2";
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("ImageString", tempPath);
                        editor.putString("HomeworkImage", path);
                        editor.commit();
                        loadPhoto(2);
                    }
                }*/
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
        image1 = (ImageView)view.findViewById(R.id.btnCapturePicture1);
        image2 = (ImageView)view.findViewById(R.id.btnCapturePicture2);
        image3 = (ImageView)view.findViewById(R.id.btnCapturePicture3);
        firstBar = (ProgressBar)view.findViewById(R.id.progressBar1);
        secondBar = (ProgressBar)view.findViewById(R.id.progressBar2);
        thirdBar = (ProgressBar)view.findViewById(R.id.progressBar3);
      /*  image4 = (ImageView)view.findViewById(R.id.btnCapturePicture4);
        image5 = (ImageView)view.findViewById(R.id.btnCapturePicture5);
        image6 = (ImageView)view.findViewById(R.id.btnCapturePicture6);
        image7 = (ImageView)view.findViewById(R.id.btnCapturePicture7);
        image8 = (ImageView)view.findViewById(R.id.btnCapturePicture8);
        image9 = (ImageView)view.findViewById(R.id.btnCapturePicture9);*/
        imagelayout = (LinearLayout)view.findViewById(R.id.imagelayout);
    }


    private void loadPhoto(int pos) {
       /* Intent i = new Intent(getActivity(),FullImageViewActivity.class);
        i.putExtra("FLAG",1);
        i.putExtra("HEADERTEXT",htext);
        i.putExtra("POSITION",pos);
        i.putExtra("ListNo",imgid);
        startActivity(i);*/
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


    public class GetHCImages extends AsyncTask<Object, Integer, Object> {
        private String requestUrl;
        private int position;
        private Bitmap bitmap ;
        private FileOutputStream fos;
        //int i=0;
        public GetHCImages(String requestUrl,int pos) {
            this.requestUrl = requestUrl;
            this.position = pos;
        }

        @Override
        protected void onPreExecute() {
            if (position==0)
            {
                image1.setVisibility(View.GONE);
                firstBar.setVisibility(View.VISIBLE);
                firstBar.setMax(100);
                firstBar.setIndeterminate(false);
            }
            else  if (position==1)
            {
                image2.setVisibility(View.GONE);
                secondBar.setVisibility(View.VISIBLE);
                secondBar.setMax(100);
                secondBar.setIndeterminate(false);
            }
            else
            if (position==2)
            {
                image3.setVisibility(View.GONE);
                thirdBar.setVisibility(View.VISIBLE);
                thirdBar.setMax(100);
                thirdBar.setIndeterminate(false);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (position==0)
            {
                firstBar.setProgress(values[0]);
            }
            else  if (position==1)
            {
                secondBar.setProgress(values[0]);
            }
            else if (position==2)
            {
                thirdBar.setProgress(values[0]);
            }
        }

        @Override
        protected Object doInBackground(Object... objects) {
            InputStream inputStream = null;
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
		            /*publishing progress update on UI thread.
		            Invokes onProgressUpdate()*/
                    publishProgress((int)((total*100)/lenghtOfFile));

                    // writing data to byte array stream
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 1;

                byte[] bytes = dataStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,bmOptions);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (position==0)
            {
                firstBar.setVisibility(View.GONE);
                image1.setImageBitmap(bitmap);
                image1.setVisibility(View.VISIBLE);
            }
            else  if (position==1)
            {
                secondBar.setVisibility(View.GONE);
                image2.setImageBitmap(bitmap);
                image2.setVisibility(View.VISIBLE);
            }
            else if (position==2)
            {
                thirdBar.setVisibility(View.GONE);
                image3.setImageBitmap(bitmap);
                image3.setVisibility(View.VISIBLE);
            }
        }
    }
}
