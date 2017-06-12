package com.realizer.schoolgenie.parent.pupil.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.pupil.ParentAttendanceCalendarFragment;
import com.realizer.schoolgenie.parent.utils.Singleton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends BaseAdapter {
	private Context mContext;

	private java.util.Calendar month;
	public GregorianCalendar pmonth; // calendar instance for previous month
	/**
	 * calendar instance for previous month for getting complete view
	 */
	public GregorianCalendar pmonthmaxset;
	private GregorianCalendar selectedDate;
	int firstDay;
	int maxWeeknumber;
	int maxP;
	int calMaxP;
	int lastWeekDay;
	int leftDays;
	int mnthlength;
	String itemvalue, curentDateString;
	DateFormat df;
    ArrayList<String> satSun;
	private ArrayList<String> items;
	private ArrayList<String> flag;
	public static List<String> dayString;
	private View previousView;

	public CalendarAdapter(Context c, GregorianCalendar monthCalendar) {
		CalendarAdapter.dayString = new ArrayList<String>();
		Locale.setDefault(Locale.US);
		month = monthCalendar;
		selectedDate = (GregorianCalendar) monthCalendar.clone();
		mContext = c;
		month.set(GregorianCalendar.DAY_OF_MONTH, 1);
		this.items = new ArrayList<String>();
		this.flag = new ArrayList<String>();
		df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		curentDateString = df.format(selectedDate.getTime());
		refreshDays();
	}

	public void setItems(ArrayList<String> items) {
		/*ArrayList<String> itemTemp = new ArrayList<>();
		for (int i = 0; i != 31; i++) {
			itemTemp.add("2016-07-"+(i+1));
		}
		itemTemp.set(0,"2016-07-01");
		itemTemp.set(1,"2016-07-02");
		itemTemp.set(2,"2016-07-03");
		itemTemp.set(3,"2016-07-04");
		itemTemp.set(4,"2016-07-05");
		itemTemp.set(5,"2016-07-06");
		itemTemp.set(6,"2016-07-07");
		itemTemp.set(7,"2016-07-08");
		itemTemp.set(8,"2016-07-09");*/

		for (int i = 0; i != items.size(); i++) {
			if (items.get(i).length() == 1) {
				items.set(i, "0" + items.get(i));
			}
		}
		this.items = items;
	}

	public void setFlag(ArrayList<String> flag)
	{
		/*ArrayList<String> flagTemp = new ArrayList<>();
		for (int i = 0; i != 31; i++) {
			flagTemp.add("true");
		}
		flagTemp.set(5, "false");
		flagTemp.set(19, "false");
		flagTemp.set(20, "false");
		flagTemp.set(29, "false");*/
		for (int i = 0; i != flag.size(); i++) {
			if (flag.get(i).length() == 1) {
				flag.set(i, "0" + flag.get(i));
			}
		}
		this.flag = flag;
	}

	public int getCount() {
		return dayString.size();
	}

	public Object getItem(int position) {
		return dayString.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new view for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		TextView dayView;
		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.calendar_item, null);

		}
		dayView = (TextView) v.findViewById(R.id.date);
		// separates daystring into parts.
		String[] separatedTime = dayString.get(position).split("-");
		// taking last part of date. ie; 2 from 2012-12-02
		String gridvalue = separatedTime[2].replaceFirst("^0*", "");
		// checking whether the day is in current month or not.
		if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
			// setting offdays to white color.
			dayView.setTextColor(Color.WHITE);
			dayView.setClickable(false);
			dayView.setFocusable(false);
		} else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
			dayView.setTextColor(Color.WHITE);
			dayView.setClickable(false);
			dayView.setFocusable(false);
		} else {
            //Singleton.setCurrentMonth(Integer.valueOf(separatedTime[1]));
			// setting curent month's days in blue color.
			dayView.setTextColor(Color.GRAY);
            dayView.setClickable(false);
            dayView.setFocusable(false);
		}

		if (dayString.get(position).equals(curentDateString)) {
			//setSelected(v);
			previousView = v;
		} else {
			v.setBackgroundResource(R.drawable.list_item_background);
		}
		dayView.setText(gridvalue);

		// create date string for comparison
		String date = dayString.get(position);

		if (date.length() == 1) {
			date = "0" + date;
		}
		String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
		if (monthStr.length() == 1) {
			monthStr = "0" + monthStr;
		}

		// show icon if date is not empty and it exists in the items array
		ImageView iw = (ImageView) v.findViewById(R.id.date_icon);
        Log.d("Attendance", "" + flag.size());

        if (date.length() > 0 && items != null && items.contains(date))
		{
            //this is for present and absent dates
           int num = items.indexOf(date);
            if(flag.get(num).equals("true"))
            {
               iw.setImageResource(R.drawable.white_man_green);
            }
            else if(flag.get(num).equals("false"))
            {
                iw.setImageResource(R.drawable.white_man_red);
            }
            else
                iw.setImageResource(R.drawable.white_man);
			iw.setVisibility(View.VISIBLE);

		} else {

            for (int n=0;n<satSun.size();n++)
            {
                //this is for displaying saturday and sunday
                int adapterdate=Integer.valueOf(satSun.get(n).split("-")[2]);
                if (Integer.valueOf(dayString.get(position).split("-")[2])==adapterdate)
                {
                    if (position==0 || position==7 ||position==14 ||
                            position==21 || position==28 || position==35  )
                    {
                        iw.setImageResource(R.drawable.white_man);
                        iw.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                else
                {
                    //this is for displaying for holidays
                    iw.setImageResource(R.drawable.white_man_grey);
                    iw.setVisibility(View.VISIBLE);
                }
            }
		}
		return v;
	}

	public View setSelected(View view) {
		if (previousView != null) {
			previousView.setBackgroundResource(R.drawable.list_item_background);
		}
		previousView = view;
		view.setBackgroundResource(R.drawable.headerbackground);
		return view;
	}

	public void refreshDays() {
		// clear items
		items.clear();
		dayString.clear();
		Locale.setDefault(Locale.US);
		pmonth = (GregorianCalendar) month.clone();
		// month start day. ie; sun, mon, etc
		firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
		// finding number of weeks in current month.
		maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
		// allocating maximum row number for the gridview.
		mnthlength = maxWeeknumber * 7;
		maxP = getMaxP(); // previous month maximum day 31,30....
		calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
        int currentMont=month.get(GregorianCalendar.MONTH);
		/**
		 * Calendar instance for getting a complete gridview including the three
		 * month's (previous,current,next) dates.
		 */
		pmonthmaxset = (GregorianCalendar) pmonth.clone();
		/**
		 * setting the start date as previous month's required date.
		 */
		pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

		/**
		 * filling calendar gridview.
		 */
        satSun=new ArrayList<>();
		for (int n = 0; n < mnthlength; n++) {

			itemvalue = df.format(pmonthmaxset.getTime());
			pmonthmaxset.add(GregorianCalendar.DATE, 1);
			dayString.add(itemvalue);
            if (n==0 || n==6 ||n==7 || n==13 ||n==14 || n==20 ||
                    n==21 || n==27 ||n==28 || n==34 ||n==35 || n==41 )
            {
                satSun.add(itemvalue);
            }
		}
        String prev="";
        String current="";
        int counter=0;
       // ArrayList<Integer> montharr=new ArrayList<>();
       // ArrayList<Integer> yeararr=new ArrayList<>();
        ArrayList<String> wholedata=new ArrayList<>();
        for (int i=0;i<dayString.size();i++)
        {
            if (i==0)
            {
                prev=dayString.get(i).split("-")[1];
               // montharr.add(Integer.valueOf(prev));
               // yeararr.add(Integer.valueOf(dayString.get(i).split("-")[0]));
                counter++;
            }
            else
            {
                current=dayString.get(i).split("-")[1];
                if (i == dayString.size()-1)
                {
                    String data=prev+"@@"+counter+"@@"+dayString.get(i).split("-")[0];
                    wholedata.add(data);
                }
                else
                {
                    if (!prev.equalsIgnoreCase(current))
                    {
                        String data=prev+"@@"+counter+"@@"+dayString.get(i).split("-")[0];
                        wholedata.add(data);
                        counter=0;
                        //montharr.add(Integer.valueOf(current));
                        // yeararr.add(Integer.valueOf(dayString.get(i).split("-")[0]));
                    }
                    else
                    {
                        counter++;
                    }
                }
                prev=current;
            }
        }
        ArrayList<Integer> maxData=new ArrayList<>();
        for (int m=0;m<wholedata.size();m++)
        {
            maxData.add(Integer.valueOf(wholedata.get(m).split("@@")[1]));
        }
        int maxn= Collections.max(maxData);
        for (int m=0;m<wholedata.size();m++)
        {
           if (maxn == Integer.valueOf(wholedata.get(m).split("@@")[1]))
           {
               Singleton.setCurrentYear(Integer.valueOf(wholedata.get(m).split("@@")[2]));
               Singleton.setCurrentMonth(Integer.valueOf(wholedata.get(m).split("@@")[0]));
               break;
           }
        }
	}

	private int getMaxP() {
		int maxP;
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			pmonth.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}
		maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

		return maxP;
	}

}