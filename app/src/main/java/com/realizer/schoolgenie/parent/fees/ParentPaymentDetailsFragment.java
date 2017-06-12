package com.realizer.schoolgenie.parent.fees;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.realizer.schoolgenie.parent.R;

/**
 * Created by shree on 4/5/2016.
 */
public class ParentPaymentDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.parent_publicholiday_layout, container, false);
        return rootView;
    }
}
