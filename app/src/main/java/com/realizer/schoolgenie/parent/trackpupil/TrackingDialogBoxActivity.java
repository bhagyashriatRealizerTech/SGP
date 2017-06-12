package com.realizer.schoolgenie.parent.trackpupil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.trackpupil.asynctask.TrackingAsyckTaskGet;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.utils.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by Win on 11/30/2015.
 */
public class TrackingDialogBoxActivity extends DialogFragment implements OnTaskCompleted{
    String Latitude;
    String Longitude;
    EditText username;
    EditText userid;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.parent_tracking_alert_dailog, null);

        username=(EditText)view.findViewById(R.id.driverUsername);
        userid=(EditText)view.findViewById(R.id.driverUserID);

        Button shoiwmap = (Button) view.findViewById(R.id.ShowMap);
        Button cancel = (Button) view.findViewById(R.id.Cancel);

        shoiwmap.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick (View v){
                                            if (username.getText().toString().equals("")) {
                                                Toast.makeText(getActivity(), "Please enter username...", Toast.LENGTH_LONG).show();
                                            }
                                            else  if (userid.getText().toString().equals("")) {
                                                Toast.makeText(getActivity(), "Please enter user id...", Toast.LENGTH_LONG).show();
                                            }else {
                                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                                String accessToken=preferences.getString("AccessToken","");
                                                String deviceid=preferences.getString("DWEVICEID","");
                                                String userId=preferences.getString("UidName","");

                                                if (Config.isConnectingToInternet(getActivity()))
                                                {
                                                    TrackingAsyckTaskGet obj = new TrackingAsyckTaskGet(username.getText().toString(),userid.getText().toString(), getActivity(),accessToken,deviceid,userId,TrackingDialogBoxActivity.this);
                                                    obj.execute();
                                                }
                                            }
                                        }
                                    }

        );


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor edit = sharedpreferences.edit();
                if (Singleton.isIsShowMap())
                {
                    edit.putString("Tracking", "false");
                }
                else
                {
                    edit.putString("Tracking", "true");
                }
                edit.commit();
                dismiss();
            }
        });

        builder.setTitle("Pupil Tracking");
        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onTaskCompleted(String s) {
        try {
            //JSONObject obj = new JSONObject(s);
            if(s.equalsIgnoreCase("[]"))
            {
                Toast.makeText(getActivity(),"Server Not Responding Please Try After Some Time",Toast.LENGTH_LONG).show();
               // dismiss();
            }
            else
            {
                JSONArray locList = new JSONArray(s.toString());
                //  for(int i=0;i<locList.length();i++) {
                JSONObject obj1 = locList.getJSONObject(locList.length()-1);

                Intent intent = new Intent(getActivity(), TrackShowMap.class);
                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", username.getText().toString());
                bundle.putString("USERID", userid.getText().toString());
                bundle.putString("LATITUDE", obj1.getString("latitude"));
                bundle.putString("LONGITUDE", obj1.getString("longitude"));
                intent.putExtras(bundle);
                startActivity(intent);

                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.putString("USERNAME", username.getText().toString());
                edit.putString("USERID", userid.getText().toString());
                edit.putString("Tracking", "false");
                edit.commit();
                Singleton.setIsShowMap(true);
                dismiss();
            }

            //  }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
