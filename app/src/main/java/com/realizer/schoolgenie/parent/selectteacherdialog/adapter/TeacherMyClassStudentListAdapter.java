package com.realizer.schoolgenie.parent.selectteacherdialog.adapter;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.selectteacherdialog.model.TeacherMyClassStudentModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shree on 11/26/2015.
 */
public class TeacherMyClassStudentListAdapter extends BaseAdapter {
    private static ArrayList<TeacherMyClassStudentModel> classList;
    private LayoutInflater inflaterClassList;
    ArrayList<String> selectedStrings = new ArrayList<String>();
    Context context;
    static int m=0;
    boolean[] checkBoxState ;
    String status="";
    Fragment frag;
    SharedPreferences.Editor editor;
    ArrayList<String> chk;
    boolean flag= false;
    int stat;

    public TeacherMyClassStudentListAdapter(Context _context, ArrayList<TeacherMyClassStudentModel> _classnamelist, String _status, Fragment frag, ArrayList<String> chkstate, int stat)
    {
        context =_context;
        classList = _classnamelist;
        inflaterClassList = LayoutInflater.from(context);
        checkBoxState=new boolean[classList.size()];
        status=_status;
        this.frag = frag;
        this.stat = stat;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        if(stat==0) {
            if (status.equals("All"))
            {
                editor.remove("NameList");
                editor.remove("SetList");
                editor.remove("SearchList");
                editor.commit();
            } else
                chk = chkstate;
        }
        else if(stat==1)
        {
            chk = chkstate;
        }
    }
    @Override
    public int getCount() {
        return classList.size();
    }

    @Override
    public Object getItem(int position) {
        return classList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       final ViewHolder holder;
        if (convertView == null) {
            convertView = inflaterClassList.inflate(R.layout.teacher_myclass_student_list_layout, null);
            holder = new ViewHolder();
            holder.chkAttendance = (CheckBox) convertView.findViewById(R.id.checkBoxAttendance1);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#87CEFF"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        if(stat==0) {
            if (status.equals("All"))
                holder.chkAttendance.setText(classList.get(position).getStdName());
            else {
                holder.chkAttendance.setText(classList.get(position).getStdName());
                for (String temp : chk) {
                    if (classList.get(position).getStdName().equals(temp))
                        holder.chkAttendance.setChecked(true);
                }
            }
        }

        else if(stat==1) {

            holder.chkAttendance.setText(classList.get(position).getStdName());
            for (String temp : chk) {
                if (classList.get(position).getStdName().equals(temp))
                    holder.chkAttendance.setChecked(true);
            }
        }


        holder.chkAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                Set<String> setall = new HashSet<String>();
                Set<String> setset = new HashSet<String>();
                Set<String> setsearch = new HashSet<String>();
                Set<String> set = new HashSet<String>();
                if(stat==1) {
                    setall = preferences.getStringSet("NameList", null);
                    setset = preferences.getStringSet("SetList", null);
                    setsearch = preferences.getStringSet("SearchList", null);
                }

                CheckBox cb = (CheckBox) v;
                if (cb.isChecked()) {

                    if (status.equals("All")) {
                        if (setall != null && !setall.isEmpty()) {

                        } else {
                            setall = new HashSet<String>();
                        }
                        selectedStrings.add(cb.getText().toString());
                        setall.addAll(selectedStrings);
                        editor.putStringSet("NameList", setall);

                    } else if (status.equals("set")) {

                        if (setset != null && !setset.isEmpty()) {

                        } else {
                            setset = new HashSet<String>();
                        }
                        selectedStrings.add(cb.getText().toString());
                        setset.addAll(selectedStrings);
                        editor.putStringSet("SetList", setset);

                    } else if (status.equals("search")) {
                        if (setsearch != null && !setsearch.isEmpty()) {

                        } else {
                            setsearch = new HashSet<String>();
                        }

                        selectedStrings.add(cb.getText().toString());
                        setsearch.addAll(selectedStrings);
                        editor.putStringSet("SearchList", setsearch);
                    }
                    editor.commit();
                   // Toast.makeText(context, "Add", Toast.LENGTH_SHORT).show();

                }

                else
                {
                    setall = preferences.getStringSet("NameList", null);
                    setset = preferences.getStringSet("SetList", null);
                    setsearch = preferences.getStringSet("SearchList", null);

                    if(flag == false && setall!=null && !setall.isEmpty()) {
                        for (String temp : setall)
                            if (cb.getText().toString().equals(temp)) {
                                setall.remove(temp);
                                editor.putStringSet("NameList", setall);
                               // Toast.makeText(context, "Remove All", Toast.LENGTH_SHORT).show();
                                flag = true;
                                break;
                            }
                    }


                    if(flag == false && setsearch!=null && !setsearch.isEmpty()) {
                        for (String temp : setsearch)
                            if (cb.getText().toString().equals(temp)) {
                                setsearch.remove(temp);
                                editor.putStringSet("SearchList", setsearch);
                               // Toast.makeText(context, "Remove Search", Toast.LENGTH_SHORT).show();
                                flag = true;
                                break;
                            }
                    }
                    if(flag == false &&setset!=null && !setset.isEmpty()) {
                        for (String temp : setset)
                            if (cb.getText().toString().equals(temp)) {
                                setset.remove(temp);
                                editor.putStringSet("SetList", setset);
                               // Toast.makeText(context, "Remove Set", Toast.LENGTH_SHORT).show();
                                flag = true;
                                break;
                            }
                    }


                    editor.commit();

                }


            }
        });

        holder.chkAttendance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();
                classList.get(getPosition).setSelected(buttonView.isChecked());
            }
        });
        holder.chkAttendance.setTag(position);
        //holder.chkAttendance.setChecked(classList.get(position).isSelected());
        return convertView;
    }

    ArrayList<String> getSelectedString(){
        return selectedStrings;
    }
    class ViewHolder
    {

        CheckBox chkAttendance;
    }
}


