package com.realizer.schoolgenie.parent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

/**
 * Created by shree on 5/29/2017.
 */
public class TermsConditionActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_condition_activity);

        final CheckBox chkAgree= (CheckBox) findViewById(R.id.chkagree);
        Button btnSubmit= (Button) findViewById(R.id.btnSubmitTerms);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkAgree.isChecked()==true)
                {

                }
            }
        });


    }
}
