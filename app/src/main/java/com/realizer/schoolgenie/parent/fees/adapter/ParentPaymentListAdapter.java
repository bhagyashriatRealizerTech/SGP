package com.realizer.schoolgenie.parent.fees.adapter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.fees.ParentPaymentDetailsFragment;
import com.realizer.schoolgenie.parent.fees.model.ParentPaymentModel;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by shree on 4/5/2016.
 */
public class ParentPaymentListAdapter extends BaseAdapter {

    private static ArrayList<ParentPaymentModel> pList;
    private LayoutInflater publicholidayDetails;
    private Context context1;
    boolean isImageFitToScreen;
    View convrtview;
    PhotoViewAttacher mAttacher;

    public ParentPaymentListAdapter(Context context, ArrayList<ParentPaymentModel> dicatationlist) {
        pList = dicatationlist;
        publicholidayDetails = LayoutInflater.from(context);
        this.context1 = context;
    }

    @Override
    public int getCount() {
        return pList.size();
    }

    @Override
    public Object getItem(int position) {

        return pList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        convrtview = convertView;
        if (convertView == null) {
            convertView = publicholidayDetails.inflate(R.layout.parent_payment_list_layout, null);
            holder = new ViewHolder();
            holder.semester = (TextView) convertView.findViewById(R.id.tvSem);
            holder.fees = (TextView) convertView.findViewById(R.id.tvFees);
            holder.status = (TextView) convertView.findViewById(R.id.tvStatus);
            holder.status.setTag(position);
            holder.duedate = (TextView) convertView.findViewById(R.id.tvDueDate);
            holder.action = (Button) convertView.findViewById(R.id.btnAction);
            holder.action.setTag(position);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#87CEFF"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        holder.semester.setText(pList.get(position).getSemester());
        holder.fees.setText(pList.get(position).getFees());
        holder.status.setText(pList.get(position).getStatus());
        holder.duedate.setText(pList.get(position).getDuedate());

        if(pList.get(position).getStatus().equals("Paid"))
        {
            holder.action.setText("View");
            holder.status.setTextColor(Color.parseColor("#006400"));
        }
        else
        {
            holder.action.setText("Pay");
            holder.status.setTextColor(Color.RED);
        }
       /* holder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context1.getApplicationContext(),ParentPaymentDetailsFragment.class);
                context1.startActivity(intent);
            }
        });
*/
        holder.action.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int pos = (Integer)v.getTag();

                if(pList.get(pos).getStatus().equalsIgnoreCase("Paid")) {
                   /* Intent intent = new Intent(context1, ParentPaymentDetailsFragment.class);
                    context1.startActivity(intent);*/

                    ParentPaymentDetailsFragment fragment = new ParentPaymentDetailsFragment();
                    FragmentTransaction fragmentTransaction = ((Activity) context1).getFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame_container,fragment);
                    fragmentTransaction.commit();
                }
                else
                Toast.makeText(context1, "Payment Gateway in Progress", Toast.LENGTH_LONG).show();

                return true;
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView semester;
        TextView fees;
        TextView status;
        TextView duedate;
        Button action;
    }
}
