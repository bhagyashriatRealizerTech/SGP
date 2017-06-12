package com.realizer.schoolgenie.parent.timetable.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.chat.backend.DALQueris;
import com.realizer.schoolgenie.parent.chat.model.ParentQueriesTeacherNameListModel;
import com.realizer.schoolgenie.parent.timetable.model.ParentTimeTableExamListModel;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.StoreBitmapImages;
import com.realizer.schoolgenie.parent.utils.Utility;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Win on 11/20/2015.
 */
public class ParentTimeTableExamListAdapter extends BaseAdapter {


    private static ArrayList<ParentTimeTableExamListModel> sList;
    private LayoutInflater syllabusDetails;
    private Context context1;
    boolean isImageFitToScreen;
    View convrtview;
    PhotoViewAttacher mAttacher;
    DALQueris dbQ;

    public ParentTimeTableExamListAdapter(Context context, ArrayList<ParentTimeTableExamListModel> syllabuslist) {
        sList = syllabuslist;
        syllabusDetails = LayoutInflater.from(context);
        context1 = context;
        dbQ=new DALQueris(context);
    }
    @Override
    public int getCount() {
        return sList.size();
    }

    @Override
    public Object getItem(int position) {

        return sList.get(position);
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
            convertView = syllabusDetails.inflate(R.layout.parent_timetable_exam_list_layout, null);
            holder = new ViewHolder();
            holder.TTName = (TextView) convertView.findViewById(R.id.timetablename);
            holder.TTDate = (TextView) convertView.findViewById(R.id.timetableDate);
            holder.TTTeacher = (TextView) convertView.findViewById(R.id.tachername);
            //  holder.syllabus = (ImageView) convertView.findViewById(R.id.imgsyllabus);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParentQueriesTeacherNameListModel result=dbQ.GetQueryTableData(sList.get(position).getTeacher());

        holder.TTName.setText(result.getName());
//        holder.TTName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_attachment, 0);
//        holder.TTName.setCompoundDrawablePadding(7);
        holder.TTDate.setText(Config.getDate(sList.get(position).getDate(), "D"));

        if (sList.get(position).getTitle().equals(""))
        {
            holder.TTTeacher.setVisibility(View.GONE);
        }
        else
        {
            holder.TTTeacher.setVisibility(View.VISIBLE);
            holder.TTTeacher.setText(sList.get(position).getTitle());
        }
        holder.TTTeacher.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_attachment, 0);
        holder.TTTeacher.setCompoundDrawablePadding(7);
        String newPath = new Utility().getURLImage(sList.get(position).getImage());
        if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
            new StoreBitmapImages(newPath, newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
        }
        return convertView;
    }

    static class ViewHolder
    {
        TextView TTName;
        TextView TTDate;
        TextView TTTeacher;
    }
}
