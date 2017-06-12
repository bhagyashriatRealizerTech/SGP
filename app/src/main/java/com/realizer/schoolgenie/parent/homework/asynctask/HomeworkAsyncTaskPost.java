package com.realizer.schoolgenie.parent.homework.asynctask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.realizer.schoolgenie.parent.exceptionhandler.NetworkException;
import com.realizer.schoolgenie.parent.homework.model.ParentHomeworkListModel;
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
 * Created by Win on 1/4/2016.
 */
public class HomeworkAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder>
{
    //ProgressDialog dialog;
    StringBuilder resultLogin;
    ParentHomeworkListModel obj ;
    Context myContext;
    private OnTaskCompleted callback;

    public HomeworkAsyncTaskPost(ParentHomeworkListModel o, Context myContext, OnTaskCompleted cb) {
        this.myContext = myContext;
        this.callback = cb;
        obj =o;
    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();
       // dialog= ProgressDialog.show(myContext, "", "Please wait Homework is Loading...");
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL+"fetchHomeWork";
        HttpPost httpPost = new HttpPost(url);

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(myContext);

        System.out.println(url);
        String json = "";
        StringEntity se = null;
        JSONObject jsonobj = new JSONObject();
        try {
            jsonobj.put("std",obj.getstandard());
            jsonobj.put("hwDate",obj.getHwdate());
            jsonobj.put("schoolCode",obj.getschoolcode());
            jsonobj.put("division",obj.getdivision());
            jsonobj.put("subject",obj.getSubject());
            jsonobj.put("UserId",sharedpreferences.getString("UidName", ""));
            jsonobj.put("DeviceId",sharedpreferences.getString("DWEVICEID",""));
            String accesst=sharedpreferences.getString("AccessToken","");
            json = jsonobj.toString();

            Log.d("RES", json);
            se = new StringEntity(json);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("AccessToken",sharedpreferences.getString("AccessToken",""));
           // httpPost.setHeader("AccessToken","eufPeFnCSV3nGbSrbTrZDTaO49hCJggek8398j2HCk3cKFaaEG55g6PXNMHMy9MQ1sAv4sckQGqJSFXffK__hA2");
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
        stringBuilder.append("@@@Homework");
        callback.onTaskCompleted(stringBuilder.toString());
    }
}
