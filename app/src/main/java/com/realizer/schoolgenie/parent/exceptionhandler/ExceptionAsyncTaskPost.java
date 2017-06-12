package com.realizer.schoolgenie.parent.exceptionhandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Win on 07-09-2016.
 */
public class ExceptionAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    ExceptionModel obj ;
    Context myContext;
    private OnTaskCompleted callback;

    public ExceptionAsyncTaskPost(ExceptionModel o, Context myContext, OnTaskCompleted cb) {
        this.myContext = myContext;
        this.callback = cb;
        obj =o;
    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();
       // dialog= ProgressDialog.show(myContext, "", "Please wait Downloading Classwork...");
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL+"LogException";
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);
        String json = "";
        StringEntity se = null;
        JSONObject jsonobj = new JSONObject();
        try {
            jsonobj.put("UserId",obj.getUserId());
            jsonobj.put("ExceptionDetails",obj.getExceptionDetails());
            jsonobj.put("DeviceModel",obj.getDeviceModel());
            jsonobj.put("AndroidVersion",obj.getAndroidVersion());
            jsonobj.put("ApplicationSource",obj.getApplicationSource());
            jsonobj.put("DeviceBrand",obj.getDeviceBrand());

            json = jsonobj.toString();
            Log.d("RES", json);
            se = new StringEntity(json);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            httpPost.setEntity(se);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            Log.d("StatusCode", "" + statusCode);
            if(statusCode == 200)
            {
                HttpEntity entity = httpResponse.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line=reader.readLine()) != null)
                {
                    resultLogin.append(line);
                }
            }
            else
            {
                StringBuilder exceptionString = new StringBuilder();
                HttpEntity entity = httpResponse.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line=reader.readLine()) != null)
                {
                    exceptionString.append(line);
                }

                NetworkException.insertNetworkException(myContext, exceptionString.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return resultLogin;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
       // dialog.dismiss();
        Log.d("RESULTASYNC", stringBuilder.toString());
        //Pass here result of async task
        String s = stringBuilder.toString()+"@@@"+"Exception"+"@@@"+obj.getExceptionID();
        callback.onTaskCompleted(s);

    }
}
