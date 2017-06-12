package com.realizer.schoolgenie.parent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;

/**
 * Created by Win on 1/15/2016.
 */
public class MainActivity extends Activity {

    private boolean isBackPressed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String getValueBack = sharedpreferences.getString("Login", "");
        if(getValueBack.length()==0)
            getValueBack="false";

        if(getValueBack.equals("false"))
        {
            Intent i = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);
        }
        else
        {
            Intent i = new Intent(MainActivity.this,DrawerActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            isBackPressed = true;
            finish();
        }
        return super.onKeyDown(keyCode, event);

    }
}
