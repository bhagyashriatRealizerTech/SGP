package com.realizer.schoolgenie.parent.timetable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.Utility;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by shree on 8/26/2016.
 */
public class TimeTableFullViewActivity extends Activity {

    ImageView imageView;
    TextView timetablename;
    ProgressBar progressBar;
    Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the layout from image.xml
        setContentView(R.layout.fullimageview_parent);

        // Locate the ImageView in activity_main.xml
        imageView = (ImageView) findViewById(R.id.imageView);
        timetablename = (TextView) findViewById(R.id.txtcounter);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        Intent intent = getIntent();
        String URL = intent.getStringExtra("Path");
        String ttname = intent.getStringExtra("TTName");
        timetablename.setText(ttname);
        // Execute DownloadImage AsyncTask
        //new LoadImage().execute(URL);
        String newPath = new Utility().getURLImage( intent.getStringExtra("Path"));
        File image = ImageStorage.getImage(newPath.split("/")[newPath.split("/").length - 1]);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = null;
        if(image != null)
         bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
        if (bitmap != null)
        imageView.setImageBitmap(bitmap);
        else
            bitmap=BitmapFactory.decodeResource(this.getResources(), R.drawable.sorryimage);
        imageView.setImageBitmap(bitmap);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* pDialog = new ProgressDialog(TimeTableFullViewActivity.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();*/

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image1) {

            if(image1 != null){
                imageView.setImageBitmap(image1);
                //pDialog.dismiss();

            }else{
               // pDialog.dismiss();
            }
        }
    }
}
