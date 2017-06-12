package com.realizer.schoolgenie.parent.generalcommunication.asynctask;

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
 * Created by shree on 1/4/2016.
 */
public class ParentGeneralCommunicationAsyncTaskGet extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    String uName, schoolCode,std,division,year;
    Context myContext;
    private OnTaskCompleted callback;
    String CurYear,CurMonth,curDay,curHour,curMin,curSecond;

    public ParentGeneralCommunicationAsyncTaskGet(String uName, String schoolCode, String std, String division, String year, Context myContext, OnTaskCompleted cb,
                                                  String CurYear, String CurMonth, String curDay,
                                                  String curHour, String curMin, String curSecond) {
        this.uName = uName.toLowerCase();
        this.schoolCode = schoolCode;
        this.std = std;
        this.division = division;
        this.year = year;
        this.myContext = myContext;
        this.callback = cb;
        this.CurYear=CurYear;
        this.CurMonth = CurMonth;
        this.curDay = curDay;
        this.curHour = curHour;
        this.curMin = curMin;
        this.curSecond=curSecond;
    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();
        //dialog=ProgressDialog.show(myContext,"","Loading data...");
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();

        String my= Config.URL+"StudentSyncUp/"+ uName + "/" +schoolCode+"/"+std+"/"+division+"/"+year+"/"+
                CurYear+"/"+ CurMonth + "/" +curDay+"/"+curHour+"/"+curMin+"/"+curSecond;
        /*String my="http://45.35.4.250/SJRestWCF/svcEmp.svc/StudentSyncUp/ramchandra/RZT/LKG/A/2016/2016/10/24/5/25/1";*/
        Log.d("URL", my);
        HttpGet httpGet = new HttpGet(my);
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
        }
        return resultLogin;

    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
       // dialog.dismiss();
        //Pass here result of async task
        stringBuilder.append("@@@AlertNdAttendance");
        callback.onTaskCompleted(stringBuilder.toString());

    }

}

