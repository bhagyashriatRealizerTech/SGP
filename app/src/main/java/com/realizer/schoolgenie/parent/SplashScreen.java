package com.realizer.schoolgenie.parent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Bhagyashri on 4/5/2016.
 */
public class SplashScreen extends Activity {
    int sleeptime=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        setContentView(R.layout.splash_screen);


        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(sleeptime);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
