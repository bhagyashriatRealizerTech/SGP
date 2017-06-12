package com.realizer.schoolgenie.parent.homework.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.homework.model.ParentHomeworkListModel;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.StoreBitmapImages;
import com.realizer.schoolgenie.parent.utils.Utility;

import java.util.ArrayList;

public class ParentHomeworkListAdapter extends BaseAdapter {
    private static ArrayList<ParentHomeworkListModel> hList;
    private LayoutInflater mhomeworkdetails;
    private Context context1;
    boolean isImageFitToScreen;
    View convrtview;



    public ParentHomeworkListAdapter(Context context, ArrayList<ParentHomeworkListModel> homeworklist) {
        hList = homeworklist;
        mhomeworkdetails = LayoutInflater.from(context);
        context1 = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        convrtview = convertView;

        if (convertView == null) {
            convertView = mhomeworkdetails.inflate(R.layout.teacher_homework_list_layout, null);
            holder = new ViewHolder();
            holder.subject = (TextView) convertView.findViewById(R.id.txthomeworksubject);
            holder.homework = (TextView) convertView.findViewById(R.id.txthomework1);
            holder.image = (ImageView) convertView.findViewById(R.id.imghomework);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.subject.setText(hList.get(position).getSubject() + " :");

        if(hList.get(position).getHomework().equals(""))
        {
            holder.homework.setText("");
        }
        else
        {
            //try{
                /*JSONArray arr = new JSONArray(hList.get(position).getHomework());*/
                String[] homeworktitl=hList.get(position).getHomework().split(" ");
                if (homeworktitl.length>3)
                {
                    holder.homework.setText(homeworktitl[0]+" "+homeworktitl[1]+" "+homeworktitl[2]+"..");
                }
                else
                {
                    holder.homework.setText(hList.get(position).getHomework().toString());
                }
            /*} catch (JSONException e) {
                e.printStackTrace();
            }*/

        }


        if(hList.get(position).getImage().equals("NoImage"))
        {
            holder.image.setVisibility(View.GONE);
        }
        else {

            holder.image.setVisibility(View.VISIBLE);
           /* String newPath = new Utility().getURLImage(hList.get(position).getImage());
            if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                new StoreBitmapImages(newPath, newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
            }*/
        }

        return convertView;
    }

    static class ViewHolder
    {
        TextView subject;
        TextView homework;
        ImageView image;
    }
}

