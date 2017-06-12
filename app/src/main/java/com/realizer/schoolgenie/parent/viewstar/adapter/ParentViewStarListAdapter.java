package com.realizer.schoolgenie.parent.viewstar.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.viewstar.model.ParentViewStarModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by shree on 11/21/2015.
 */
public class ParentViewStarListAdapter extends BaseAdapter {
    private static ArrayList<ParentViewStarModel> dailyAttendance;
    private LayoutInflater inflaterDailyAtt;
    Context context;

    public ParentViewStarListAdapter(Context _context, ArrayList<ParentViewStarModel> _dailyAttendance)
    {
        context =_context;
        dailyAttendance = _dailyAttendance;
        inflaterDailyAtt = LayoutInflater.from(_context);
    }
    @Override
    public int getCount() {
        return dailyAttendance.size();
    }

    @Override
    public Object getItem(int position) {
        return dailyAttendance.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflaterDailyAtt.inflate(R.layout.parent_viewstar_list_layout, null);
            holder = new ViewHolder();

            holder.txtComment = (TextView) convertView.findViewById(R.id.txtcomnts);
            holder.txtname = (TextView) convertView.findViewById(R.id.txtnames);
            holder.imgViewStar = (ImageView) convertView.findViewById(R.id.imgstar);
            holder.initial = (TextView)convertView.findViewById(R.id.txtinitial);
            holder.profilepic = (ImageView) convertView.findViewById(R.id.profile_image_view);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.initial.setVisibility(View.GONE);
        holder.profilepic.setVisibility(View.GONE);
        holder.txtComment.setText(dailyAttendance.get(position).getcomment());

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = fmt.parse(dailyAttendance.get(position).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MMM-yyyy");
        holder.txtname.setText(fmtOut.format(date));
        String stargiven=dailyAttendance.get(position).getgivenStar();

        if(stargiven.equals(context.getString(R.string.ViewstarVeryGood)))
        {
            Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.verygood_new)).getBitmap();
            holder.imgViewStar.setImageBitmap(bitmap);
        }
        else if(stargiven.equals(context.getString(R.string.ViewstarGreat)))
        {
            Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.great_new)).getBitmap();
            holder.imgViewStar.setImageBitmap(bitmap);
        }
        else if(stargiven.equals(context.getString(R.string.ViewstarNiceWork)))
        {
            Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.nicework_new)).getBitmap();
            holder.imgViewStar.setImageBitmap(bitmap);
        }
        else if(stargiven.equals(context.getString(R.string.ViewstarTerrific)))
        {
            Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.terrific_new)).getBitmap();
            holder.imgViewStar.setImageBitmap(bitmap);
        }
        else if(stargiven.equals(context.getString(R.string.ViewstarSupreStar)))
        {
            Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.super_new)).getBitmap();
            holder.imgViewStar.setImageBitmap(bitmap);
        }
        else if(stargiven.equals(context.getString(R.string.ViewstarWellDone)))
        {
            Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.welldone_new)).getBitmap();
            holder.imgViewStar.setImageBitmap(bitmap);
        }

        return convertView;
    }
    class ViewHolder
    {
        TextView txtComment;
        TextView txtname,initial;
        ImageView imgViewStar, profilepic;;
    }
}
