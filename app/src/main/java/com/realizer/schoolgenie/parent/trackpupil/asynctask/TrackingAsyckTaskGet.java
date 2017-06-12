package com.realizer.schoolgenie.parent.trackpupil.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.realizer.schoolgenie.parent.exceptionhandler.NetworkException;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by shree on 11/17/2015.
 */
public class TrackingAsyckTaskGet extends AsyncTask<Void, Void,StringBuilder> {

    //Declare controls
    ProgressDialog dialog;

    // Declare Variables
    StringBuilder resultLogin;
    String DriverUserId;
    String UniqueNo;
    Context myContext;
    String accessToken,deviceid,userId;
    OnTaskCompleted call;

    public TrackingAsyckTaskGet(String username,String driverid, Context _myContext,String accesstoken,String deviceid,String userid,OnTaskCompleted cb) {
        this.DriverUserId = username;
        this.UniqueNo = driverid;
        this.myContext = _myContext;
        this.accessToken=accesstoken;
        this.deviceid=deviceid;
        this.userId=userid.toLowerCase();
        this.call=cb;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog= ProgressDialog.show(myContext, "", "loading Data ...");
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();

        String my= Config.URL+"retrievePupilLocation/"+DriverUserId+"/"+UniqueNo+"/"+userId+"/"+deviceid;
        HttpGet httpGet = new HttpGet(my);
        httpGet.setHeader("AccessToken",accessToken);
        HttpClient client = new DefaultHttpClient();
        try
        {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200)
            {
                HttpEntity entity = response.getEntity();
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
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                exceptionString.append("URL: "+my.toString()+"\nInput: Get Method \nException: ");

                while((line=reader.readLine()) != null)
                {
                    exceptionString.append(line);
                }

                NetworkException.insertNetworkException(myContext, exceptionString.toString());
            }
        }
        catch(ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            client.getConnectionManager().closeExpiredConnections();
            client.getConnectionManager().shutdown();
            dialog.dismiss();
        }
        return resultLogin;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
        dialog.dismiss();
        call.onTaskCompleted(stringBuilder.toString());
    }
}

