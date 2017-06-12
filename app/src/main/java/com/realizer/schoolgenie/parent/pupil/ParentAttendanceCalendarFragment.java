package com.realizer.schoolgenie.parent.pupil;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.pupil.adapter.CalendarAdapter;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.utils.Utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Bhagyashri on 2/5/2016.
 */


public class ParentAttendanceCalendarFragment extends Fragment implements FragmentBackPressedListener {
    public GregorianCalendar month, itemmonth;// calendar instances.

    public CalendarAdapter adapter;// adapter instance
    public Handler handler;// for grabbing some event values for showing the dot
    // marker.
    public ArrayList<String> items;
    public ArrayList<String> flags;// container to store calendar items which
    // needs showing the event marker
    ArrayList<String> event;
    TextView attendance_info,present_info,absent_info,holiday_info;
    ArrayList<String> date;
    ArrayList<String> desc;
    View rootview;
    //TextView tvDateinfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.calendar, container, false);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("My Pupil Attendance", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        Locale.setDefault(Locale.US);
        //tvDateinfo=(TextView)rootview.findViewById(R.id.tvDateinfo);
        //rLayout = (LinearLayout) findViewById(R.id.text);
        month = (GregorianCalendar) GregorianCalendar.getInstance();
        itemmonth = (GregorianCalendar) month.clone();

        items = new ArrayList<String>();
        flags = new ArrayList<String>();

        adapter = new CalendarAdapter(getActivity(), month);

        GridView gridview = (GridView) rootview.findViewById(R.id.gridview);
        attendance_info = (TextView) rootview.findViewById(R.id.attedance_info);
        present_info = (TextView) rootview.findViewById(R.id.present_info);
        absent_info = (TextView) rootview.findViewById(R.id.absent_info);
        holiday_info  = (TextView) rootview.findViewById(R.id.holiday_info);
        gridview.setAdapter(adapter);

        handler = new Handler();
        handler.post(calendarUpdater);

        TextView title = (TextView) rootview.findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        RelativeLayout previous = (RelativeLayout) rootview.findViewById(R.id.previous);

        previous.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setPreviousMonth();
                refreshCalendar();
            }
        });

        RelativeLayout next = (RelativeLayout) rootview.findViewById(R.id.next);
        next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setNextMonth();
                refreshCalendar();

            }
        });

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // removing the previous view if added
				/*if (((LinearLayout) rLayout).getChildCount() > 0) {
					((LinearLayout) rLayout).removeAllViews();
				}*/
                desc = new ArrayList<String>();
                date = new ArrayList<String>();
                //((CalendarAdapter) parent.getAdapter()).setSelected(v);
                String selectedGridDate = CalendarAdapter.dayString
                        .get(position);
                String isPresent="Holiday";
                String[] separatedTime = selectedGridDate.split("-");
                String gridvalueString = separatedTime[2].replaceFirst("^0*",
                        "");// taking last part of date. ie; 2 from 2012-12-02.
                int gridvalue = Integer.parseInt(gridvalueString);
                // navigate to next or previous month on clicking offdays.
                if ((gridvalue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar();
                } else if ((gridvalue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar();
                }
                //((CalendarAdapter) parent.getAdapter()).setSelected(v);

                for (int i = 0; i < Utility.startDates.size(); i++) {
                    if (Utility.startDates.get(i).equalsIgnoreCase(selectedGridDate)) {
                        desc.add(Utility.nameOfEvent.get(i));
                        if (Utility.descriptions.get(i).equalsIgnoreCase("true")) {
                            isPresent = "Present";
                            break;
                        }
                        else {
                            isPresent = "Absent";
                            break;
                        }
                    }
                    else
                    {
                        isPresent="Holiday";
                    }
                }

                //tvDateinfo.setText(selectedGridDate +"\t\t:\t"+isPresent);
                //tvDateinfo.setShadowLayer(1, 2, 4, Color.GRAY);
                /*if (desc.size() > 0) {
                    for (int i = 0; i < desc.size(); i++) {
						*//*TextView rowTextView = new TextView(CalendarView.this);

						// set some properties of rowTextView or something
						rowTextView.setText("Event:" + desc.get(i));
						rowTextView.setTextColor(Color.BLACK);*//*

                        // add the textview to the linearlayout
                        //rLayout.addView(rowTextView);

                    }

                }*/

                desc = null;

            }

        });


        return rootview;
    }


    protected void setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1),
                    month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) + 1);
        }

    }

    protected void setPreviousMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }
        int currentMont=month.get(GregorianCalendar.MONTH);

    }

    protected void showToast(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();

    }

    public void refreshCalendar() {
        TextView title = (TextView) rootview.findViewById(R.id.title);

        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        handler.post(calendarUpdater); // generate some calendar items

        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
        settingAttendaceInfo();

    }

    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {
            items.clear();
            flags.clear();

            // Print dates of the current week
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String itemvalue;
            event = Utility.readCalendarEvent(getActivity());

            //Setting present/absent for current date
            String isPresent="Holiday";
            for (int i = 0; i < Utility.startDates.size(); i++) {
                if (Utility.startDates.get(i).equalsIgnoreCase(Utility.getDate(System.currentTimeMillis()))) {

                    if (Utility.descriptions.get(i).equalsIgnoreCase("true")) {
                        isPresent = "Present";
                        break;
                    }
                    else {
                        isPresent = "Absent";
                        break;
                    }
                }
                else
                {
                    isPresent="Holiday";
                }
            }
            //tvDateinfo.setText(Utility.getDate(System.currentTimeMillis()) +"\t\t:\t"+isPresent);
            //tvDateinfo.setShadowLayer(1, 2, 4, Color.GRAY);
            Log.d("=====Event====", event.toString());
            Log.d("=====Date ARRAY====", Utility.startDates.toString());
            ArrayList<ParentAttendanceModel> attList=new ArrayList<>();
            for (int i = 0; i < Utility.startDates.size(); i++) {
                itemvalue = df.format(itemmonth.getTime());
                itemmonth.add(GregorianCalendar.DATE, 1);
                items.add(Utility.startDates.get(i).toString());
                flags.add(Utility.nameOfEvent.get(i).toLowerCase());
                ParentAttendanceModel p=new ParentAttendanceModel();
                p.setAttendanceDate(Utility.startDates.get(i).toString());
                p.setFlag(Utility.nameOfEvent.get(i).toLowerCase());
                attList.add(p);
            }
            adapter.setItems(items);
            adapter.setFlag(flags);
            adapter.notifyDataSetChanged();
            settingAttendaceInfo();
        }
    };

    @Override
    public void onFragmentBackPressed() {
        Singleton.setFragment(Singleton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }

    public void settingAttendaceInfo()
    {
        int presentD=0;
        int absentD=0;
        int currentmonth= Singleton.getCurrentMonth();
        int currentYear= Singleton.getCurrentYear();
        for (int k=0;k<items.size();k++)
        {
            String getcurrentMonth=items.get(k);
            if (currentmonth==Integer.valueOf(getcurrentMonth.split("-")[1]))
            {
                for (int i=0;i<flags.size();i++) {
                    if (flags.get(i).equalsIgnoreCase("true"))
                    {
                        presentD++;
                    }
                    else
                    {
                        absentD++;
                    }
                }
                attendance_info.setText("Total Working School Days:"+items.size());
                present_info.setText("Present:"+presentD);
                absent_info.setText("Absent:"+absentD);
            }
            else
            {
                attendance_info.setText("Total Working School Days:0");
                present_info.setText("Present:"+presentD);
                absent_info.setText("Absent:"+absentD);
            }
        }

        Calendar calendar = Calendar.getInstance();
        // Note that month is 0-based in calendar, bizarrely.
        //calendar.set(calendar.get(currentYear), currentmonth-1, 1);
        calendar.set(Calendar.YEAR, currentYear);
        calendar.set(Calendar.MONTH, currentmonth-1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int holiday=daysInMonth-(presentD+absentD);

        int count = 0;
        /*for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(calendar.get(Calendar.YEAR), currentmonth, day);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
                count++;
            }
        }*/
        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(calendar.get(Calendar.YEAR), currentmonth, day);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SUNDAY ) {
                count++;
            }
        }

        // int holiday=daysInMonth-(items.size()+count);
        //int holiday=count;
        attendance_info.setText("Total Working School Days:"+items.size());
        present_info.setText("Present:"+presentD);
        absent_info.setText("Absent:"+absentD);
        holiday_info.setText("Holiday:"+holiday);
    }
}
