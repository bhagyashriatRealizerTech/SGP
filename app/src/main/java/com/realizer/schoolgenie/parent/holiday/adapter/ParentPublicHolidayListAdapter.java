package com.realizer.schoolgenie.parent.holiday.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.holiday.model.ParentPublicHolidayListModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Win on 11/20/2015.
 */
public class ParentPublicHolidayListAdapter extends BaseAdapter {


    private static ArrayList<ParentPublicHolidayListModel> pList;
    private LayoutInflater publicholidayDetails;
    private Context context1;
    boolean isImageFitToScreen;
    View convrtview;
    PhotoViewAttacher mAttacher;


    public ParentPublicHolidayListAdapter(Context context, ArrayList<ParentPublicHolidayListModel> dicatationlist) {
        pList = dicatationlist;
        publicholidayDetails = LayoutInflater.from(context);
        context1 = context;
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
            convertView = publicholidayDetails.inflate(R.layout.teacher_publicholiday_list_layout, null);
            holder = new ViewHolder();
            holder.textDP = (TextView) convertView.findViewById(R.id.txtinitial);
            holder.textHolidayName = (TextView) convertView.findViewById(R.id.txtholidayname);
            holder.textHolidayStartDate = (TextView) convertView.findViewById(R.id.txtstartdate);
            holder.textHolidayEndDate = (TextView) convertView.findViewById(R.id.txtenddate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String timestamp =  pList.get(position).getStartDate().split("\\(")[1].split("\\-")[0];
        Date createdOn = new Date(Long.parseLong(timestamp));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String formattedDate = sdf.format(createdOn);
        holder.textHolidayStartDate.setText(formattedDate);

        holder.textHolidayName.setText(pList.get(position).getDesc());

        String timestamp1 =  pList.get(position).getEndDate().split("\\(")[1].split("\\-")[0];
        Date createdOn1 = new Date(Long.parseLong(timestamp1));
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM yyyy");
        String formattedDate1 = sdf1.format(createdOn1);
        holder.textHolidayEndDate.setText(formattedDate1);

        return convertView;
    }

    static class ViewHolder {
        TextView textDP;
        TextView textHolidayName;
        TextView textHolidayStartDate,textHolidayEndDate;
    }
}
