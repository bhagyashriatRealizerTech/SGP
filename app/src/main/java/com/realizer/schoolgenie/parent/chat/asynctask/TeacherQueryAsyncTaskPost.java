package com.realizer.schoolgenie.parent.chat.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.realizer.schoolgenie.parent.chat.model.TeacherQuerySendModel;
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
public class TeacherQueryAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    TeacherQuerySendModel obj ;
    Context myContext;
    private OnTaskCompleted callback;

    public TeacherQueryAsyncTaskPost(TeacherQuerySendModel o, Context myContext, OnTaskCompleted cb) {
        this.myContext = myContext;
        this.callback = cb;
        obj =o;
    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();

        //dialog=ProgressDialog.show(myContext,"","Authenticating credentials...");

    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL+"AddConversation";
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);



        String json = "";
       /* Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String date = df.format(calendar.getTime());*/
        StringEntity se = null;
        JSONObject jsonobj = new JSONObject();
        try {


            jsonobj.put("SchoolCode",obj.getSchoolCode());
            jsonobj.put("ConversationId",obj.getConversationId());
            jsonobj.put("fromTeacher",obj.getFromTeacher());
            jsonobj.put("from",obj.getFrom());
            jsonobj.put("to",obj.getTo());
            jsonobj.put("text",obj.getText());
            String date = obj.getSentTime();
            String date1[] = date.split("/");
            String resdate = date1[1]+"/"+date1[0]+"/"+date1[2];
            jsonobj.put("sentTime",resdate);

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
                // Log.e("Error", "Failed to Login");
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
        String res= stringBuilder.toString()+"Query";
        callback.onTaskCompleted(stringBuilder.toString()+"Query");

    }
}
