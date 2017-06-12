/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.realizer.schoolgenie.parent;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.realizer.schoolgenie.parent.utils.Config;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    static StringBuilder builder;
    private static Context ctx;

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    static boolean register(final Context context, final String regId,final String empID) {
        //Log.i(TAG, "registering device (regId = " + regId + ")");
        ctx = context;
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(Config.TAG, "Attempt #" + i + " to register");
            try {
               /*displayMessage(context, context.getString(
                        R.string.server_registering, i, MAX_ATTEMPTS));*/

                post(regId,empID);
                GCMRegistrar.setRegisteredOnServer(context, true);
                String message = context.getString(R.string.server_registered);
                Config.displayMessage(context, message);
                return true;
            } catch (Exception e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(Config.TAG, "Failed to register on attempt " + i, e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(Config.TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(Config.TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return false;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        String message = context.getString(R.string.server_register_error,
                MAX_ATTEMPTS);
        Config.displayMessage(context, message);
        return false;
    }

    /**
     * Unregister this account/device pair within the server.
     */
    static void unregister(final Context context, final String regId) {
        Log.i(Config.TAG, "unregistering device (regId = " + regId + ")");
        //String serverUrl = Config.URL + "/deregisterStudentDevice/" + studentID + "/" + regId;
        // Map<String, String> params = new HashMap<String, String>();
        //params.put("regId", regId);
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String did = sharedpreferences.getString("DWEVICEID", "");
        String userid = sharedpreferences.getString("UidName", "");
        String accesstoken = sharedpreferences.getString("AccessToken", "");
        try {
            postUnregister(userid, did,accesstoken);
            //GCMRegistrar.setRegisteredOnServer(context, false);
            String message = context.getString(R.string.server_unregistered);
            Config.displayMessage(context, message);
        } catch (Exception e) {
            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
            String message = context.getString(R.string.server_unregister_error,
                    e.getMessage());
            Config.displayMessage(context, message);
        }
    }


    private static StringBuilder post(String regID,String EmpId) {
        //String my = "http://104.217.254.180/RestWCF/svcEmp.svc/registerDevice/" + EmpId + "/" + regID;
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String did = sharedpreferences.getString("DWEVICEID", "");

        //String my = "http://192.168.1.14/SJRestWCF/registerDevice/" + EmpId + "/" + regID;
       String my = Config.URL+"RegisterStudentDevice/" + EmpId + "/" + did+"/"+regID;
        Log.d("GCMDID", my);
        builder = new StringBuilder();
        HttpGet httpGet = new HttpGet(my);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            Log.d("GCMSTATUS", "" + statusCode);
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("Error", "Failed to Login");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.getConnectionManager().closeExpiredConnections();
            client.getConnectionManager().shutdown();
        }

        return builder;
    }

    private static StringBuilder postUnregister(String EmpId,String did,String access) {

        String my = Config.URL+"deregisterStudentDevice/" + EmpId + "/" + did;
        Log.d("GCMDID", my);
        builder = new StringBuilder();
        HttpGet httpGet = new HttpGet(my);
        HttpClient client = new DefaultHttpClient();
        httpGet.setHeader("AccessToken",access);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            Log.d("GCMSTATUS", "" + statusCode);
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("Error", "Failed to Login");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }finally {
            client.getConnectionManager().closeExpiredConnections();
            client.getConnectionManager().shutdown();
        }

        return builder;
    }
}
