package com.realizer.schoolgenie.parent.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Win on 17-09-2016.
 */
public class StoreBitmapImages extends AsyncTask<Object, Object, Object> {
    private String requestUrl, imagename_;
    private Bitmap bitmap ;

    public StoreBitmapImages(String requestUrl, String _imagename_) {
        this.requestUrl = requestUrl;
        this.imagename_ = _imagename_ ;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        try {
            URL url = new URL(requestUrl);
            URLConnection conn = url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (Exception ex) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if(bitmap != null) {
            if (!ImageStorage.checkifImageExists(imagename_)) {
                ImageStorage.saveToSdCard(bitmap, imagename_);
            }
        }
    }
}

