package com.realizer.schoolgenie.parent.fees;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.fees.adapter.ParentPaymentListAdapter;
import com.realizer.schoolgenie.parent.fees.model.ParentPaymentModel;

import java.util.ArrayList;

/**
 * Created by shree on 4/5/2016.
 */
public class ParentPaymentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.parent_payment_fragment, container, false);
        setHasOptionsMenu(true);

        final ListView listPayment = (ListView) rootView.findViewById(R.id.paymentList);
        ArrayList<ParentPaymentModel> paymentDetails =GetPaymentList();
        listPayment.setAdapter(new ParentPaymentListAdapter(getActivity(), paymentDetails));
        listPayment.setVisibility(View.VISIBLE);


        return rootView;
    }

    private ArrayList<ParentPaymentModel> GetPaymentList()
    {
        String pay="Seamester 1,,22,470,,Paid,,No@@@Seamester 2,,22,470,,Pending,,15/04/2016";
        String[] syllabuslist =pay.split("@@@");
        ArrayList<ParentPaymentModel> results = new ArrayList<>();
        for(String homework : syllabuslist)
        {
            String[] syllabus = homework.toString().split(",,");
            ParentPaymentModel sDetail = new ParentPaymentModel();

            sDetail.setSemester(syllabus[0]);
            sDetail.setFees(syllabus[1]);
            sDetail.setStatus(syllabus[2]);
            sDetail.setDuedate(syllabus[3]);
            results.add(sDetail);
        }
        return results;
    }
}
