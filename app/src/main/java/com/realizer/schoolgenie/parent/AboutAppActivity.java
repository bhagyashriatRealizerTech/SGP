package com.realizer.schoolgenie.parent;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.utils.Config;

/**
 * Created by Win on 29/05/2017.
 */
public class AboutAppActivity extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.activity_aboutapp, container, false);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("App Info", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        return rootView;

    }
}
