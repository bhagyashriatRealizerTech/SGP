package com.realizer.schoolgenie.parent.exceptionhandler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.mailsender.MailSender;
import com.realizer.schoolgenie.parent.utils.Config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExceptionHandler implements
		Thread.UncaughtExceptionHandler {
	private final Context myContext;
	private final String LINE_SEPARATOR = "\n";

	public ExceptionHandler(Context context) {
		myContext = context;
	}

	public void uncaughtException(Thread thread, Throwable exception) {
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		StringBuilder errorReport = new StringBuilder();
		errorReport.append("************ CAUSE OF ERROR ************\n\n");
		errorReport.append(stackTrace.toString());

		errorReport.append("\n************ DEVICE INFORMATION ***********\n");
		errorReport.append("Brand: ");
		errorReport.append(Build.BRAND);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Device: ");
		errorReport.append(Build.DEVICE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Model: ");
		errorReport.append(Build.MODEL);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Id: ");
		errorReport.append(Build.ID);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Product: ");
		errorReport.append(Build.PRODUCT);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("\n************ FIRMWARE ************\n");
		errorReport.append("SDK: ");
		errorReport.append(Build.VERSION.SDK);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Release: ");
		errorReport.append(Build.VERSION.RELEASE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Incremental: ");
		errorReport.append(Build.VERSION.INCREMENTAL);
		errorReport.append(LINE_SEPARATOR);

        DatabaseQueries qr = new DatabaseQueries(myContext);
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        ExceptionModel obj = new ExceptionModel();
        obj.setUserId(sharedpreferences.getString("StudentUserID", ""));
        obj.setExceptionDetails(stackTrace.toString());
        obj.setDeviceModel(Build.MODEL);
        obj.setAndroidVersion(Build.VERSION.SDK);
        obj.setApplicationSource("Parent");
        obj.setDeviceBrand(Build.BRAND);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());

        long n = qr.insertException(obj);
        if(n>0)
        {
            n =0;
            n = qr.insertQueue(qr.getExceptionId(),"Exception","1",date);
        }

        //Config.alertDialog(myContext,"Sorry","The application Parent to Parent has stopped unexpectedly. Please try again.");
        Intent i = new Intent(myContext,DrawerActivity.class);
        myContext.startActivity(i);

        if(Config.isConnectingToInternet(myContext))
            sendEmail(obj);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
	}


    public static void sendEmail(final ExceptionModel obj)
    {
        new Thread(new Runnable() {

            public void run() {

                try {

                    String messageContent = "Application Source: "+obj.getApplicationSource()
                            +"\nDevice Model: "+obj.getDeviceModel()+"\nAndroid Version: "+obj.getAndroidVersion()
                            +"\nDevice Brand: "+obj.getDeviceBrand()+"\nUserID: "+obj.getUserId()
                            +"\nException: "+obj.getExceptionDetails();

                    String TO = "Farhan.bodale@realizertech.com,satish.sawant@realizertech.com";

                    MailSender sender = new MailSender("realizertech1@gmail.com","realizer@15");

                    // sender.addAttachment(Environment.getExternalStorageDirectory().getPath()+"/image.jpg");

                    sender.sendMail("Critical Network Error: Parent App",messageContent,"realizertech1@gmail.com",TO);

                } catch (Exception e) {

                    Log.d("Exception Mail", e.toString());
                    // Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();

                }

            }

        }).start();
    }
}