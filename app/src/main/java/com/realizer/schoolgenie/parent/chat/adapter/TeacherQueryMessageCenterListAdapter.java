package com.realizer.schoolgenie.parent.chat.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.chat.backend.DALQueris;
import com.realizer.schoolgenie.parent.chat.model.ParentQueriesTeacherNameListModel;
import com.realizer.schoolgenie.parent.chat.model.TeacherQueryViewListModel;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.GetImages;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.Utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Win on 11/26/2015.
 */
public class TeacherQueryMessageCenterListAdapter extends BaseAdapter {

    DALQueris dbQ;
    private static ArrayList<TeacherQueryViewListModel> messageList;
    private LayoutInflater mhomeworkdetails;
    private String Currentdate;
    String date;
    int counter;
    int datepos;
    ViewHolder holder;
    String username="";
    String thumbnailurl="";
    public TeacherQueryMessageCenterListAdapter(Context context, ArrayList<TeacherQueryViewListModel> messageList1) {
        messageList = messageList1;
        mhomeworkdetails = LayoutInflater.from(context);
        Calendar c = Calendar.getInstance();
        counter = 0;
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Currentdate = df.format(c.getTime());
        date = Currentdate;
        datepos = -1;
        Log.d("Date", Currentdate);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        username = preferences.getString("UserName","");
        thumbnailurl = preferences.getString("ThumbnailID","");
        dbQ=new DALQueris(context);
    }
    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {

        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getViewTypeCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = mhomeworkdetails.inflate(R.layout.chat_messgagecenter_list_layout, null);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.txtdate);
            holder.date.setTag(position);
            holder.sendername = (TextView) convertView.findViewById(R.id.txtsenderName);
            holder.time = (TextView) convertView.findViewById(R.id.txttime);
            holder.message = (TextView) convertView.findViewById(R.id.txtMessage);
            holder.newmessage = (LinearLayout) convertView.findViewById(R.id.linlayoutnewmsgbar);
            holder.datelayout = (LinearLayout) convertView.findViewById(R.id.linlayoutdate);
            holder.initial = (TextView) convertView.findViewById(R.id.txtinitial);
            holder.profilepic = (ImageView) convertView.findViewById(R.id.profile_image_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.d("Date", Currentdate);
        Log.d("Date1", messageList.get(position).getSenddate());

        holder.newmessage.setVisibility(View.GONE);
        if(position==0)
        {
            if(messageList.get(position).getSenddate().equals(Currentdate))
            {
                int datetag = (Integer)holder.date.getTag();
                if (counter == 0 || datetag == datepos)
                {
                    hideShowDate(true,messageList.get(position).getSenddate());
                    counter = counter + 1;
                    datepos= (Integer)holder.date.getTag();
                }
                else
                {
                    hideShowDate(false, "");
                }
            }
            else
            {
                hideShowDate(true,messageList.get(position).getSenddate());
            }
        }
        else if(position>0) {
            if (messageList.get(position - 1).getSenddate().equals(messageList.get(position).getSenddate()) )
            {
                hideShowDate(false, "");
            }
            else
            {
                int datetag = (Integer)holder.date.getTag();
                if (counter == 0 || datetag == datepos)
                {
                    hideShowDate(true,messageList.get(position).getSenddate());
                    counter = counter + 1;
                    datepos= (Integer)holder.date.getTag();
                }
                else
                {
                    hideShowDate(true,messageList.get(position).getSenddate());
                }

            }
        }


        String name[] = messageList.get(position).getTname().trim().split(" ");

        if(messageList.get(position).getProfileImage() != null && !messageList.get(position).getProfileImage().equals("") && !messageList.get(position).getProfileImage().equalsIgnoreCase("null"))
        {
            String urlString="";
            if( username.contains(messageList.get(position).getTname()))
            {
                urlString = thumbnailurl;
            }
            else
            {
                urlString = messageList.get(position).getProfileImage();
            }

            String newURL= Utility.getURLImage(urlString);
            holder.initial.setVisibility(View.GONE);
            holder.profilepic.setVisibility(View.VISIBLE);
            if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                new GetImages(newURL,holder.profilepic,newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
            else
            {
                File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                holder.initial.setVisibility(View.GONE);
                holder.profilepic.setVisibility(View.VISIBLE);
                holder.profilepic.setImageBitmap(bitmap);
            }
        }
        else {
            holder.initial.setVisibility(View.VISIBLE);
            holder.profilepic.setVisibility(View.GONE);

            char fchar = name[0].toUpperCase().charAt(0);
            char lchar = name[0].toUpperCase().charAt(0);
            for (int i = 0; i < name.length; i++) {
                if (!name[i].equals("") && i == 0)
                    fchar = name[i].toUpperCase().charAt(0);
                else if (!name.equals("") && i == (name.length - 1))
                    lchar = name[i].toUpperCase().charAt(0);

            }

            holder.initial.setText(fchar + "" + lchar);

        }

        ParentQueriesTeacherNameListModel result=dbQ.GetQueryTableData(messageList.get(position).getTname());
        if (result.getName().equals(""))
        {
            holder.sendername.setText(messageList.get(position).getTname());
        }
        else
        {
            holder.sendername.setText(result.getName());
        }

        holder.time.setText(messageList.get(position).getTime());

        holder.message.setText(messageList.get(position).getMsg());



/*
        if(hList.get(position).getFlag().equals("T"))
       {

           holder.msgT.setText(hList.get(position).getMsg()+"\t");
           holder.msgTt.setText("\t"+hList.get(position).getTime());
           holder.lt.setBackgroundResource(R.drawable.in_message_bg);
           holder.msgP.setText("");
           holder.msgPt.setText("");
           holder.lp.setBackground(null);

       }
        else if(hList.get(position).getFlag().equals("P"))
       {
           holder.msgP.setText(hList.get(position).getMsg()+"\t");
           holder.msgPt.setText(hList.get(position).getTime());
           holder.lp.setBackgroundResource(R.drawable.out_message_bg);
           holder.msgT.setText("");
           holder.msgTt.setText("");
           holder.lt.setBackground(null);
       }*/

        return convertView;
    }

    //

    public void hideShowDate(boolean flag,String setdate)
    {

        if(flag)
        {
            String qdate[]=setdate.split("/");
            String newDate=qdate[1]+"/"+qdate[0]+"/"+qdate[2];
            ViewGroup.LayoutParams layoutParams = holder.datelayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.datelayout.setLayoutParams(layoutParams);
            holder.date.setText(Config.getDate(newDate, "D"));
            date = newDate;

           /* LinearLayout.LayoutParams layoutmargin = (LinearLayout.LayoutParams)holder.layoutallview.getLayoutParams();
            layoutmargin.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutmargin.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutmargin.setMargins(0,20,0,0);
            holder.layoutallview.setLayoutParams(layoutmargin);*/

        }

        else
        {

            ViewGroup.LayoutParams layoutParams = holder.datelayout.getLayoutParams();
            layoutParams.width = 0;
            layoutParams.height = 0;
            holder.datelayout.setLayoutParams(layoutParams);

            /*LinearLayout.LayoutParams layoutmargin = (LinearLayout.LayoutParams)holder.layoutallview.getLayoutParams();
            layoutmargin.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutmargin.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutmargin.setMargins(0,0,0,0);
            holder.layoutallview.setLayoutParams(layoutmargin);*/


        }
    }


    static class ViewHolder
    {
        TextView date,sendername,time,initial,message;
        LinearLayout newmessage,datelayout;
        ImageView profilepic;

    }
}

