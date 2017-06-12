package com.realizer.schoolgenie.parent.chat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.chat.model.TeacherQueryViewListModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Win on 11/26/2015.
 */
public class TeacherQueryViewListAdapter extends BaseAdapter {


    private static ArrayList<TeacherQueryViewListModel> hList;
    private LayoutInflater mhomeworkdetails;
    private String Currentdate;
    String date;
    int counter;
    ViewHolder holder;
    int datepos;

    public TeacherQueryViewListAdapter(Context context, ArrayList<TeacherQueryViewListModel> homeworklist) {
        hList = homeworklist;
        mhomeworkdetails = LayoutInflater.from(context);
        Calendar c = Calendar.getInstance();
        counter = 0;
        datepos = -1;
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Currentdate = df.format(c.getTime());
        date = Currentdate;
        Log.d("Date", Currentdate);

    }
    @Override
    public int getCount() {
        return hList.size();
    }

    @Override
    public Object getItem(int position) {

        return hList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getViewTypeCount() {
        return hList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = mhomeworkdetails.inflate(R.layout.teacher_queryview_list_layout, null);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.txtdate);
            holder.date.setTag(position);
            holder.msgP = (TextView) convertView.findViewById(R.id.txtmsgp);
            holder.msgT = (TextView) convertView.findViewById(R.id.txtmsgt);
            holder.msgPt = (TextView) convertView.findViewById(R.id.txtpt);
            holder.msgTt = (TextView) convertView.findViewById(R.id.txttt);
            holder.lp = (LinearLayout) convertView.findViewById(R.id.linp);
            holder.lt = (LinearLayout) convertView.findViewById(R.id.lint);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.d("Date", Currentdate);
        Log.d("Date1", hList.get(position).getSenddate());

        if(position==0)
        {
            if(hList.get(position).getSenddate().equals(Currentdate))
            {
                int datetag = (Integer)holder.date.getTag();
                if (counter == 0 || datetag == datepos)
                {
                    hideShowDate(true,"Today");
                    counter = counter + 1;
                    datepos= (Integer)holder.date.getTag();
                }
                else
                {
                    hideShowDate(false, "");
                }
            }
        }
        else if(position>0) {
            if (hList.get(position - 1).getSenddate().equals(hList.get(position).getSenddate()) )
            {
                hideShowDate(false, "");
            }
            else
            {
                int datetag = (Integer)holder.date.getTag();
                if (counter == 0 || datetag == datepos)
                {
                    hideShowDate(true,"Today");
                    counter = counter + 1;
                    datepos= (Integer)holder.date.getTag();
                }
                else
                {
                    hideShowDate(true,hList.get(position).getSenddate());
                }

            }
        }

       if(hList.get(position).getFlag().equals("T"))
       {

           holder.msgT.setText(hList.get(position).getMsg()+"\t");
           holder.msgTt.setText("\t"+hList.get(position).getTime());
           holder.lt.setBackgroundResource(R.drawable.out_message_bg);
           holder.msgP.setText("");
           holder.msgPt.setText("");
           holder.lp.setBackground(null);

       }
        else if(hList.get(position).getFlag().equals("P"))
       {
           holder.msgP.setText(hList.get(position).getMsg()+"\t");
           holder.msgPt.setText(hList.get(position).getTime());
           holder.lp.setBackgroundResource(R.drawable.in_message_bg);
           holder.msgT.setText("");
           holder.msgTt.setText("");
           holder.lt.setBackground(null);
       }

        return convertView;
    }

    public void hideShowDate(boolean flag,String setdate)
    {
        if(flag)
        {
            android.view.ViewGroup.LayoutParams layoutParams = holder.date.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.date.setLayoutParams(layoutParams);
            holder.date.setPadding(10, 10, 10, 10);
            holder.date.setTextSize(12);
            holder.date.setText(setdate);
            counter = counter + 1;
            Log.d("HERE", "Hi" + counter);
            holder.date.setBackgroundResource(R.drawable.backsqforqueries);
        }

        else
        {

            android.view.ViewGroup.LayoutParams layoutParams = holder.date.getLayoutParams();
            layoutParams.width = 0;
            layoutParams.height = 0;
            holder.date.setLayoutParams(layoutParams);
            holder.date.setText("");

        }
    }



    static class ViewHolder
    {
        TextView date;
        TextView msgP;
        TextView msgT;
        TextView msgPt;
        TextView msgTt;
        LinearLayout lp,lt;

    }
}

