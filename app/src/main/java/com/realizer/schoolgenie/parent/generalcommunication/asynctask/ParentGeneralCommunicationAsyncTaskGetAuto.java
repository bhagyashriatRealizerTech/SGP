package com.realizer.schoolgenie.parent.generalcommunication.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by shree on 1/5/2016.
 */
public class ParentGeneralCommunicationAsyncTaskGetAuto extends AsyncTask<Void, Void,StringBuilder> {

    //Declare controls
    ProgressDialog dialog;

    // Declare Variables
    StringBuilder resultLogin;
    String uName, schoolCode,std,division,year;
    Context myContext;
    String CurYear,CurMonth,curDay,curHour,curMin,curSecond;
    private OnTaskCompleted callback;

     /* StudentSyncUp(string StudentCode, string SchoolCode, string Std, String Div,
                string AccYear, string CurYear, string CurMonth, string curDay,
                string curHour, string curMin, string curSecond);*/

    public ParentGeneralCommunicationAsyncTaskGetAuto(String uName, String schoolCode, String std, String division, String year, Context myContext, OnTaskCompleted cb,
                                                      String CurYear, String CurMonth, String curDay,
                                                      String curHour, String curMin, String curSecond) {
        this.uName = uName;
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
        super.onPreExecute();
        dialog= ProgressDialog.show(myContext, "", "Please wait Downloading Data ...");
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        // Url to get leave details
        //"http://104.217.254.180/RestWCF/svcEmp.svc/GetEmpMonthlyAttendence/"+ empId+"/"+month+"/"+year;
        String my= Config.URL+"StudentSyncUp/"+ uName + "/" +schoolCode+"/"+std+"/"+division+"/"+year+"/"
                +CurYear+"/"+ CurMonth + "/" +curDay+"/"+curHour+"/"+curMin+"/"+curSecond;
        Log.d("URLAttend:", my);
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
                Log.e("Error", "Failed to Login");
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
        dialog.dismiss();
        callback.onTaskCompleted(stringBuilder.toString());
    }

}


