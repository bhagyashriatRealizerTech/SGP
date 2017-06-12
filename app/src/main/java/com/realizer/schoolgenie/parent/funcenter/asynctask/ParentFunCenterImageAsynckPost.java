package com.realizer.schoolgenie.parent.funcenter.asynctask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.realizer.schoolgenie.parent.exceptionhandler.NetworkException;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterModel;
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
 * Created by Win on 02/05/2016.
 */
public class ParentFunCenterImageAsynckPost extends AsyncTask<Void, Void,StringBuilder>
{

    //ProgressDialog dialog;
    StringBuilder resultLogin;
    ParentFunCenterModel obj ;
    Context myContext;
    private OnTaskCompleted callback;

    public ParentFunCenterImageAsynckPost(ParentFunCenterModel o,Context myContext, OnTaskCompleted cb)
    {
        this.myContext = myContext;
        this.callback = cb;
        obj =o;
    }

    @Override
    protected void onPreExecute() {
        //dialog= ProgressDialog.show(myContext, "", "Download Data please wait...");
    }

    @Override
    protected StringBuilder doInBackground(Void... params)
    {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL + "fetchDateSchoolEventImages";
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);

        String json = "";
        StringEntity se = null;

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        String scode = sharedpreferences.getString("SchoolCode", "");

        JSONObject jobj = new JSONObject();
        try {

            jobj.put("schoolCode",obj.getSchoolCode());
            jobj.put("AcademicYear",obj.getAcademicYear());
            jobj.put("Std",obj.getStd());
            jobj.put("Div",obj.getDiv());
            jobj.put("syncDate",obj.getEventDate());

            json = jobj.toString();

            Log.d("STRINGOP", json);
            se = new StringEntity(json);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            httpPost.setEntity(se);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            Log.d("StatusCode", "" + statusCode);
            if (statusCode == 200)
            {
                HttpEntity entity = httpResponse.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null)
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

                exceptionString.append("URL: "+url.toString()+"\nInput: "+json+"\nException: ");

                while((line=reader.readLine()) != null)
                {
                    exceptionString.append(line);
                }

                NetworkException.insertNetworkException(myContext, exceptionString.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return resultLogin;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
        //dialog.dismiss();
        Log.d("RESULTASYNC", stringBuilder.toString());
        //Pass here result of async task
        stringBuilder.append("@@@FuncenterEventsImages");
        callback.onTaskCompleted(stringBuilder.toString());

    }
}