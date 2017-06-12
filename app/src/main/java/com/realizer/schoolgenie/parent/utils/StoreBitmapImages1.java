package com.realizer.schoolgenie.parent.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Win on 17-09-2016.
 */
public class StoreBitmapImages1 extends AsyncTask<Object, Object, Object> {
    private String requestUrl, imagename_;
    private Bitmap bitmap ;
    private FileOutputStream fos;
    private OnTaskCompleted callback;
    String imageType1,imageId1;
    public StoreBitmapImages1(String requestUrl, String _imagename_,String imageType,String imageId,OnTaskCompleted cb) {
        this.requestUrl = requestUrl;
        this.imagename_ = _imagename_ ;
        this.callback = cb;
        imageType1 = imageType;
        imageId1 = imageId;
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
        if(!ImageStorage.checkifImageExists(imagename_))
        {
            ImageStorage.saveToSdCard(bitmap, imagename_);

            callback.onTaskCompleted(imageType1+"@@@"+imageId1);
        }
    }
}

