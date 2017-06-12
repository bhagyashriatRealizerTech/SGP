package com.realizer.schoolgenie.parent;

import android.*;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.backend.SqliteHelper;
import com.realizer.schoolgenie.parent.chat.backend.DALQueris;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.forgotpassword.SetMagicWordAsyncTaskGet;
import com.realizer.schoolgenie.parent.forgotpassword.SetPasswordAsyncTaskGet;
import com.realizer.schoolgenie.parent.forgotpassword.SetPasswordByEmailAsyncTaskGet;
import com.realizer.schoolgenie.parent.forgotpassword.ValidateMagicWordAsyncTaskGet;
import com.realizer.schoolgenie.parent.generalcommunication.backend.DALGeneralCommunication;
import com.realizer.schoolgenie.parent.holiday.backend.DALHoliday;
import com.realizer.schoolgenie.parent.pupil.backend.DALMyPupilInfo;
import com.realizer.schoolgenie.parent.service.AutoSyncService;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.utils.StoreBitmapImages;
import com.realizer.schoolgenie.parent.utils.Utility;
import com.realizer.schoolgenie.parent.view.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class LoginActivity extends Activity implements OnTaskCompleted {

    EditText userName, password;
    Button loginButton;
    AlertDialog.Builder adbdialog;
    CheckBox checkBox;
    SqliteHelper myhelper;
    DatabaseQueries dbqr;
    ProgressWheel loading;
    TextView txtForgetPswrd;
    int num;
    String defaultMagicWord;
    SharedPreferences sharedpreferences;
    boolean isTermAgree=false;
    TextView copyRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.login_activity);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);

        userName = (EditText) findViewById(R.id.edtEmpId);
        password = (EditText) findViewById((R.id.edtPassword));
        txtForgetPswrd = (TextView) findViewById((R.id.txtForgetPswrd));
        loginButton = (Button) findViewById(R.id.btnLogin);
        loading = (ProgressWheel) findViewById(R.id.loading);
        Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");
        loginButton.setTypeface(face);
        checkBox = (CheckBox) findViewById(R.id.checkBox1);
        num =0;
        dbqr=new DatabaseQueries(getApplicationContext());
        defaultMagicWord="";
        copyRight = (TextView) findViewById(R.id.copyrightyear);

        //permission for marsh mallow
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

        String copyYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        copyRight.setText("© "+ copyYear +" RealizerTech");

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    userName.setText(result);
                    userName.setSelection(result.length());
                    // alert the user
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    password.setText(result);
                    password.setSelection(result.length());
                    // alert the user
                }
            }
        });


        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = sharedpreferences.edit();
                if (checkBox.isChecked()) {

                    edit.putString("UserName", userName.getText().toString().trim());
                    edit.putString("Password", password.getText().toString().trim());
                    edit.putString("CHKSTATE", "true");
                    edit.commit();
                }
                else
                {
                    edit.putString("UserName", "");
                    edit.putString("Password", "");
                    edit.putString("CHKSTATE", "false");
                    edit.commit();
                }
            }
        });


        String chk = sharedpreferences.getString("CHKSTATE","");
        Log.d("CHECKED", chk);
        if(chk.equals("true"))
        {
            checkBox.setChecked(true);
            userName.setText(sharedpreferences.getString("UserName",""));
            password.setText(sharedpreferences.getString("Password", ""));
        }
        else
        {
            checkBox.setChecked(false);
            userName.setText("");
            password.setText("");
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean res = isConnectingToInternet();
                if (res == false) {
                    Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    //Utils.alertDialog(LoginActivity.this, "", Utils.actionBarTitle(getString(R.string.LoginNoInternate)).toString());
                } else if (userName.getText().toString().equals("") && password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Username/Password", Toast.LENGTH_LONG).show();
                    //Utils.alertDialog(LoginActivity.this, "", Utils.actionBarTitle(getString(R.string.LoginEnterUserPswd)).toString());
                } else if (userName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Username", Toast.LENGTH_LONG).show();
                    //Utils.alertDialog(LoginActivity.this, "", Utils.actionBarTitle(getString(R.string.LoginEnterUsername)).toString());
                } else if (password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
                    //Utils.alertDialog(LoginActivity.this, "", Utils.actionBarTitle(getString(R.string.LoginEnterPassword)).toString());
                } else {

                    final SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    String logchk = sharedpreferences.getString("LogChk", "");
                    String uname = sharedpreferences.getString("UserName","");
                    if(logchk.equals("true") && !uname.equals(userName.getText().toString().trim()))
                    {
                        adbdialog = new AlertDialog.Builder(LoginActivity.this);
                        adbdialog.setTitle("Login Alert");
                        adbdialog.setMessage("All the Data of Previous User will be Deleted,\nDo You want to Proceed?");
                        adbdialog.setIcon(android.R.drawable.ic_dialog_alert);
                        adbdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                loading.setVisibility(View.VISIBLE);

                                new NewLoginAsync().execute();

                            } });


                        adbdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                userName.setText("");
                                password.setText("");
                            } });
                        adbdialog.show();

                    }
                    else {
                        if(Config.isConnectingToInternet(LoginActivity.this)) {
                            loading.setVisibility(View.VISIBLE);
                            LoginAsyncTaskGet obj = new LoginAsyncTaskGet(userName.getText().toString(), password.getText().toString(), LoginActivity.this, LoginActivity.this);
                            obj.execute();
                        }
                        else
                        {
                            Config.alertDialog(LoginActivity.this, "Network Error", "Please check your internet connection");
                        }
                    }
                }
            }
        });

        txtForgetPswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");

                LayoutInflater inflater = getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.forgotpwd_recoveryoption, null);
                Button submit = (Button)dialoglayout.findViewById(R.id.btn_submit);
                Button cancel = (Button)dialoglayout.findViewById(R.id.btn_cancel);
                final RadioButton mail = (RadioButton)dialoglayout.findViewById(R.id.rb_option_mail);
                final RadioButton magicword = (RadioButton)dialoglayout.findViewById(R.id.rb_option_magic_word);
                submit.setTypeface(face);
                cancel.setTypeface(face);
                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setView(dialoglayout);

                final AlertDialog alertDialog = builder.create();
                mail.setChecked(true);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mail.isChecked()) {
                            alertDialog.dismiss();
                            recoverPasswordByEmail();
                        }
                        else if (magicword.isChecked()) {
                            alertDialog.dismiss();
                            recoverPasswordByMagicWord("ForgotPassword",false,"");
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Please select anyone option for recovery password.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

            }
        });
    }

    public void recoverPasswordByEmail()
    {
        final Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.forgotpwd_rmailpassword, null);
        Button submit = (Button)dialoglayout.findViewById(R.id.btn_submit);
        Button cancel = (Button)dialoglayout.findViewById(R.id.btn_cancel);
        final EditText userID = (EditText)dialoglayout.findViewById(R.id.edtuserid);
        final EditText email = (EditText)dialoglayout.findViewById(R.id.edtmailid);
        submit.setTypeface(face);
        cancel.setTypeface(face);
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(dialoglayout);
        final AlertDialog alertDialog = builder.create();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userID.getText().toString().trim();
                String userEmail =  email.getText().toString().trim();

                if(userID.length()>0 && userEmail.length()>0)
                {
                    loading.setVisibility(View.VISIBLE);
                    new SetPasswordByEmailAsyncTaskGet(userId,userEmail,LoginActivity.this,LoginActivity.this).execute();
                    alertDialog.dismiss();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter User ID/Email ID...", Toast.LENGTH_LONG).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public void recoverPasswordByMagicWord(final String from, boolean b1,final String s)
    {
        final Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.forgotpwd_mwordpassword, null);
        Button submit = (Button)dialoglayout.findViewById(R.id.btn_submit);
        Button cancel = (Button)dialoglayout.findViewById(R.id.btn_cancel);
        final EditText userID = (EditText)dialoglayout.findViewById(R.id.edtuserid);
        userID.setText(userName.getText().toString());
        final EditText magicWord = (EditText)dialoglayout.findViewById(R.id.edtmagicword);

        final TextView titledialog = (TextView)dialoglayout.findViewById(R.id.dialogTitle);
        final TextView infodialog = (TextView)dialoglayout.findViewById(R.id.infodialog);

        submit.setTypeface(face);
        cancel.setTypeface(face);
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(dialoglayout);
        if(from.equalsIgnoreCase("FirstLogin"))
        {
            userID.setEnabled(false);
            titledialog.setText("Set Magic Word");
            infodialog.setText("You  are Logged in First Time ,Please Set Your Magic Word");
            builder.setCancelable(false);
        }
        else
        {
            userID.setEnabled(true);
        }

        final AlertDialog alertDialog = builder.create();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userID.getText().toString().trim();
                String wordMagic =  magicWord.getText().toString().trim();
                alertDialog.dismiss();
                if (from.equalsIgnoreCase("FirstLogin")) {
                    if(userId.length()>0 && wordMagic.length()>0)

                        new SetMagicWordAsyncTaskGet(userId,wordMagic,s,LoginActivity.this,LoginActivity.this).execute();
                }
                else
                {
                    loading.setVisibility(View.VISIBLE);
                    new ValidateMagicWordAsyncTaskGet(userId,wordMagic,LoginActivity.this,LoginActivity.this).execute();
                }
                //resetPassword();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                if (from.equalsIgnoreCase("FirstLogin")) {
                    boolean b = parsData(s);
                    if (b == true) {
                        loading.setVisibility(View.GONE);
                        GCMReg();
                        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        SharedPreferences.Editor edit = sharedpreferences.edit();
                        edit.putString("Login", "true");
                        edit.commit();
                        Intent ser = new Intent(LoginActivity.this, AutoSyncService.class);
                        Singleton.setAutoserviceIntent(ser);
                        startService(ser);
                        Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                        startActivity(i);

                    } else {
                        if (num == 0)
                            Toast.makeText(getApplicationContext(), "Invalid credentials, Pls Try again!", Toast.LENGTH_LONG).show();
                        else if (num == 1)
                            Toast.makeText(getApplicationContext(), "Server Not Responding Please Try After Some Time", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        alertDialog.show();
    }


    @Override
    public void onTaskCompleted(String s)  {
        loading.setVisibility(View.GONE);
        String onTaskResult[]=s.split("@@@");
        if (onTaskResult[1].contains("LoginIN"))
        {
            boolean b = false;

            String logchk = sharedpreferences.getString("LogChk","");
            String mWord = "";
            String validate = "";
            JSONObject emp=null;
            JSONObject studentInfo=null;
            String accesstoken ="";
            try {
                JSONObject rootObj = new JSONObject(s);
                emp=rootObj.getJSONObject("StudentloginResult");
                validate  = emp.getString("loginResult");
                studentInfo  = emp.getJSONObject("studentDtls");
                mWord =studentInfo.getString("magicWord");
                accesstoken =  emp.getString("AccessToken");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = sharedpreferences.edit();
            edit.putString("UidName", userName.getText().toString().toLowerCase());
            edit.putString("UserName", userName.getText().toString().trim().toLowerCase());
            edit.putString("Password", password.getText().toString().trim());
            edit.putString("AccessToken",accesstoken);
            edit.putString("FragName", "Dashboard");
            edit.commit();
            if(logchk.equals("true"))
            {
                try {

                    if (validate.equals("valid")) {
                        b = true;
                    } else {
                        String Schoolcode = studentInfo.getString("schoolCode");
                        if (Schoolcode.equals("null")) {
                            num = 1;
                        }
                        b = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(mWord.trim().length()>0) {
                    if (b == true) {

                        loading.setVisibility(View.GONE);

                        isTermAgree=Boolean.parseBoolean(sharedpreferences.getString("isTermAgree",""));
                        if (isTermAgree)
                        {
                            GCMReg();
                            edit.putString("Login", "true");

                            try {
                                String thumbnailurl = studentInfo.getString("ThumbnailURL");
                                edit.putString("ThumbnailID", thumbnailurl);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            edit.commit();
                            Intent ser = new Intent(LoginActivity.this, AutoSyncService.class);
                            Singleton.setAutoserviceIntent(ser);
                            startService(ser);
                        /*Intent i2 = new Intent(getApplicationContext(), BackgroundSyncupService.class);
                        startService(i2);*/

                            Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                            i.putExtra("FragName", "NoValue");
                            startActivity(i);
                        }
                        else
                        {
                            SetTermsAndCondition(studentInfo,true,b,"");
                        }
                    } else {
                        loading.setVisibility(View.GONE);
                        if (num == 0)
                            Toast.makeText(getApplicationContext(), "Invalid credentials, Pls Try again!", Toast.LENGTH_LONG).show();
                        else if (num == 1)
                            Toast.makeText(getApplicationContext(), "Server Not Responding Please Try After Some Time", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    SetTermsAndCondition(studentInfo,false,b,s);
                }
            }
            else
            {
                if(mWord.trim().length()>0 && !mWord.equalsIgnoreCase("null"))
                {
                    try {

                        if (validate.equals("valid")) {
                        } else {
                            String Schoolcode = studentInfo.getString("schoolCode");
                            if (Schoolcode.equals("null")) {
                                num = 1;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    b = parsData(onTaskResult[0]);
                    if (b == true) {
                        loading.setVisibility(View.GONE);
                        GCMReg();
                        edit.putString("Login", "true");
                        edit.commit();
                        Intent ser = new Intent(LoginActivity.this, AutoSyncService.class);
                        Singleton.setAutoserviceIntent(ser);
                        startService(ser);

                        Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                        i.putExtra("FragName", "NoValue");
                        startActivity(i);

                    } else {
                        loading.setVisibility(View.GONE);
                        if (num == 0)
                            Toast.makeText(getApplicationContext(), "Invalid credentials, Pls Try again!", Toast.LENGTH_LONG).show();
                        else if (num == 1)
                            Toast.makeText(getApplicationContext(), "Server Not Responding Please Try After Some Time", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                if (validate.equals("valid")) {
//                            Config.alertDialog(this,"Alert","Login Successfully");
                    //Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    SetTermsAndCondition(studentInfo, false, b, s);
//                    recoverPasswordByMagicWord("FirstLogin", b, s);

                } else {
                    // Toast.makeText(getApplicationContext(), "Invalid credentials, Pls Try again!", Toast.LENGTH_LONG).show();
                    Config.alertDialog(this, "Error", "Invalid credentials, Pls Try again");
                }
                //recoverPasswordByMagicWord("FirstLogin", b, s);
            }
        }
        else if (onTaskResult[1].contains("SetMagicWord")) {
            if(onTaskResult[0].equalsIgnoreCase("true"))
            {

            }
            boolean b1 = parsData(onTaskResult[2]);
            if (b1 == true) {
                loading.setVisibility(View.GONE);
                GCMReg();
                SharedPreferences sharedpreferences1 = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor edit1 = sharedpreferences1.edit();
                edit1.putString("Login", "true");
                edit1.commit();
                Intent ser = new Intent(LoginActivity.this, AutoSyncService.class);
                Singleton.setAutoserviceIntent(ser);
                startService(ser);
                Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                i.putExtra("FragName", "NoValue");
                startActivity(i);

            } else {
                loading.setVisibility(View.GONE);
                if (num == 0)
                    Toast.makeText(getApplicationContext(), "Invalid credentials, Pls Try again!", Toast.LENGTH_LONG).show();
                else if (num == 1)
                    Toast.makeText(getApplicationContext(), "Server Not Responding Please Try After Some Time", Toast.LENGTH_SHORT).show();
            }
        }
        else  if (onTaskResult[1].contains("ValidateMagicWord")) {
            loading.setVisibility(View.GONE);
            if(onTaskResult[0].equalsIgnoreCase("true"))
            {
                resetPassword();
            }
            else
            {
                Toast.makeText(LoginActivity.this, "Wrong User ID / Wrong Magic Word Entered", Toast.LENGTH_SHORT).show();
            }

        }
        else  if (onTaskResult[1].contains("SetPassword")) {
            loading.setVisibility(View.GONE);
            if(onTaskResult[0].equalsIgnoreCase("true"))
            {
                Toast.makeText(LoginActivity.this, "Reset Password Successfully.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(LoginActivity.this, "Fail to Reset Password.", Toast.LENGTH_SHORT).show();
            }

        }
        else  if (onTaskResult[1].equalsIgnoreCase("SendEmail")) {
            loading.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
            if(s.equalsIgnoreCase("true"))
            {
                Toast.makeText(LoginActivity.this, "Email Sent Successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(LoginActivity.this, "Fail to Send Email", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void GCMReg()
    {
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(Config.DISPLAY_MESSAGE_ACTION));
        GCMRegistrar.register(LoginActivity.this, Config.SENDER_ID);
    }



    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
                    //Toast.makeText(context,newMessage,Toast.LENGTH_SHORT).show();
                }
            };

    public boolean parsData(String json) {
        DALMyPupilInfo qr = new DALMyPupilInfo(getApplicationContext());
        DALQueris Qdal = new DALQueris(getApplicationContext());
        DALHoliday Hdal = new DALHoliday(getApplicationContext());
        DALGeneralCommunication dla = new DALGeneralCommunication(getApplicationContext());

        String validate="";
        JSONObject rootObj = null;
        Log.d("String", json);
        try {

            rootObj = new JSONObject(json);
            JSONObject emp=rootObj.getJSONObject("StudentloginResult");
            validate = emp.getString("loginResult");

            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = sharedpreferences.edit();

            JSONObject sdlist = emp.getJSONObject("studentDtls");
            String userId= sdlist.getString("userId");
            String pwd= sdlist.getString("pwd");
            String std= sdlist.getString("std");
            String division= sdlist.getString("division");
            String classrollno= sdlist.getString("classRollNo");
            String rollno= sdlist.getString("classRollNo");
            String fname= sdlist.getString("fName");
            String mname= sdlist.getString("mName");
            String lname= sdlist.getString("lName");
            String dob= sdlist.getString("dob");
            String bldgrp= sdlist.getString("bldGrp");
            String fathername= sdlist.getString("fatherName");
            String mothername= sdlist.getString("motherName");
            String contactno= sdlist.getString("contactNo");
            String emergencycontactno= sdlist.getString("emergencyContactNo");
            String address= sdlist.getString("address");
            String hobbies= sdlist.getString("hobbies");
            String comment= sdlist.getString("comments");
            String isactive= sdlist.getString("isActive");
            String Activetill= sdlist.getString("ActiveTill");
            String registrationcode= sdlist.getString("RegistrationCodes");
            String acdyear= sdlist.getString("academicYear");
            String schoolcode= sdlist.getString("schoolCode");
            String thumbnailurl= sdlist.getString("ThumbnailURL");
            String magicWord=sdlist.getString("magicWord");
            edit.putInt("SyncRNO", Integer.valueOf(rollno));
            edit.putString("SyncStd", std);
            edit.putString("SyncDiv", division);
            edit.putString("SyncScode", schoolcode);
            edit.putString("SyncAyear", acdyear);
            edit.putString("DisplayName", fname.replace(" ","")+" "+lname.replace(" ",""));
            edit.putString("FirstNameStudent", fname);
            edit.putString("ThumbnailID", thumbnailurl);
            edit.commit();
            String newURL= Utility.getURLImage(thumbnailurl);
            if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                new StoreBitmapImages(newURL,newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
            long n =qr.insertStudInfo(userId.toLowerCase(), pwd, std, division, classrollno, rollno, fname, mname, lname, dob, bldgrp, fathername, mothername, contactno,
                    emergencycontactno, address, hobbies, comment, isactive, Activetill, registrationcode, acdyear, schoolcode,magicWord);
            if(n>=0)
            {
                Log.d("Student", " Done!!!");
            }else {
                Log.d("Student", " Not Done!!!");
            }


            JSONObject stddiv = emp.getJSONObject("sa");
            JSONArray subAllocation = stddiv.getJSONArray("subjAllocation");
            String teacherid="",Stndard="",division1="",teachername="",teachersubject="",teacherthumbnailurl="";
            int i=subAllocation.length();

            DatabaseQueries chat = new DatabaseQueries(getApplicationContext());
            for(int j=0;j<i;j++)
            {
                JSONObject obj = subAllocation.getJSONObject(j);
                teacherid = obj.getString("TeacherUserId");
                Stndard=obj.getString("Std");
                division1=obj.getString("division");
                teachername=obj.getString("TeacherName");
                teachersubject=obj.getString("subject");
                teacherthumbnailurl=obj.getString("ThumbNailURL");
                long n1= Qdal.insertTeacherSubInfo(Stndard, teachername, teacherid, division1, teachersubject,teacherthumbnailurl);
                if(n1>=0)
                {
                   n=0;

                    n = chat.insertInitiatechat(teachername,"false",teacherid,0,teacherthumbnailurl);
                    Log.d("Teacher", " Done!!!");
                    if(n>=0) {

                        Log.d("Initiate", " " + teacherid + " " + teachername);
                    }

                }else {
                    Log.d("Teacher", " Not Done!!!");
                }
            }

            JSONArray hldList = emp.getJSONArray("Phs");
            for(int k =0;k<hldList.length();k++)
            {
                JSONObject obj = hldList.getJSONObject(k);
                String createdby= obj.getString("CreatedBy");
                String holiday= obj.getString("Holiday");
                String enddate= obj.getString("PHEndDate");
                String startdate= obj.getString("PHStartDate");

                long n2 =Hdal.insertHolidayInfo(createdby, holiday, enddate, startdate);
                if(n2>=0)
                {
                    Log.d("Holiday", " Done!!!");
                }else {
                    Log.d("Holiday", " Not Done!!!");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            num = 1;
            Log.e("JSON", e.toString());
            Log.e("Login.JLocalizedMessage", e.getLocalizedMessage());
            Log.e("Login(JStackTrace)", e.getStackTrace().toString());
            Log.e("Login(JCause)", e.getCause().toString());
            Log.wtf("Login(JMsg)", e.getMessage());
        }

        if(validate.equals("valid"))
            return true;
        else
            return false;
    }


    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }

    public void resetPassword()
    {
        final Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.forgotpwd_resetpassword, null);
        Button submit = (Button)dialoglayout.findViewById(R.id.btn_submit);
        Button cancel = (Button)dialoglayout.findViewById(R.id.btn_cancel);
        final EditText userID = (EditText)dialoglayout.findViewById(R.id.edtuserid);
        final EditText pwd = (EditText)dialoglayout.findViewById(R.id.edtpwd);
        final EditText cPwd = (EditText)dialoglayout.findViewById(R.id.edtconfirmpwd);
        submit.setTypeface(face);
        cancel.setTypeface(face);
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(dialoglayout);
        final AlertDialog alertDialog = builder.create();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userID.getText().toString().trim();
                String password =  pwd.getText().toString().trim();
                String cPassword =  cPwd.getText().toString().trim();

                alertDialog.dismiss();

                if(password.equals(cPassword))
                    new SetPasswordAsyncTaskGet(userId,password,LoginActivity.this,LoginActivity.this).execute();
                else
                    Toast.makeText(LoginActivity.this,"Password Mismatch",Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public class NewLoginAsync extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

        dbqr.deleteAllData();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            SharedPreferences.Editor edit = sharedpreferences.edit();
            edit.putString("LogChk", "false");
            edit.commit();
            LoginAsyncTaskGet obj = new LoginAsyncTaskGet(userName.getText().toString(), password.getText().toString(), LoginActivity.this, LoginActivity.this);
            obj.execute();
        }
    }
    public void SetTermsAndCondition(final JSONObject studentInfo, final boolean isMagicWord,final boolean b,final String s)
    {
        final Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.terms_condition_activity, null);
        final CheckBox chkAgree= (CheckBox) dialoglayout.findViewById(R.id.chkagree);
        Button btnSubmitTerms = (Button)dialoglayout.findViewById(R.id.btnSubmitTerms);

        btnSubmitTerms.setTypeface(face);
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(dialoglayout);
        final AlertDialog alertDialog = builder.create();

        btnSubmitTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor edit = sharedpreferences.edit();
                if (chkAgree.isChecked()==true)
                {
                    if (isMagicWord) {
                        GCMReg();
                        edit.putString("Login", "true");
                        edit.putString("isTermAgree", "true");

                        try {
                            String thumbnailurl = studentInfo.getString("ThumbnailURL");
                            edit.putString("ThumbnailID", thumbnailurl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        edit.commit();
                        Intent ser = new Intent(LoginActivity.this, AutoSyncService.class);
                        Singleton.setAutoserviceIntent(ser);
                        startService(ser);
                        /*Intent i2 = new Intent(getApplicationContext(), BackgroundSyncupService.class);
                        startService(i2);*/

                        Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                        i.putExtra("FragName", "NoValue");
                        startActivity(i);
                    }
                    else{
                        recoverPasswordByMagicWord("FirstLogin", b, s);
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Please accept terms and conditions!", Toast.LENGTH_SHORT).show();
                }
            }
            });

        alertDialog.show();
    }
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {android.Manifest.permission.GET_ACCOUNTS,
                        android.Manifest.permission.RECEIVE_SMS,
                        android.Manifest.permission.READ_SMS,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.WAKE_LOCK,
                        android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.READ_CALENDAR,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.VIBRATE,
                        android.Manifest.permission.CHANGE_NETWORK_STATE,
                        android.Manifest.permission.REQUEST_INSTALL_PACKAGES,
                        android.Manifest.permission.SET_TIME_ZONE,
                        android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        android.Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                        android.Manifest.permission.CHANGE_WIFI_STATE,


                }, 101);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    SharedPreferences.Editor edit = sharedpreferences.edit();
                    edit.putString("DWEVICEID", telephonyManager.getDeviceId());
                    edit.commit();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
