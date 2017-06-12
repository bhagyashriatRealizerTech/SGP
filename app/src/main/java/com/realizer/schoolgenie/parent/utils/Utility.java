/*
 * Copyright (C) 2014 Mukesh Y authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.realizer.schoolgenie.parent.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.realizer.schoolgenie.parent.generalcommunication.backend.DALGeneralCommunication;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Mukesh Y
 */
public class Utility {
	public static ArrayList<String> nameOfEvent = new ArrayList<String>();
	public static ArrayList<String> startDates = new ArrayList<String>();
	public static ArrayList<String> endDates = new ArrayList<String>();
	public static ArrayList<String> descriptions = new ArrayList<String>();

	public static ArrayList<String> readCalendarEvent(Context context) {
		/*Cursor cursor = context.getContentResolver()
				.query(Uri.parse("content://com.android.calendar/events"),
						new String[] { "calendar_id", "title", "description",
								"dtstart", "dtend", "eventLocation" }, null,
						null, null);
		cursor.moveToFirst();*/
		// fetching calendars name
		//String CNames[] = new String[cursor.getCount()];
		DALGeneralCommunication qr = new DALGeneralCommunication(context);


        ArrayList<String> CNames = qr.GetAttDateTableData();

		//String CNames[]= new String[3];
		String name[] = new String[CNames.size()];

		// fetching calendars id
		nameOfEvent.clear();
		startDates.clear();
		endDates.clear();
		descriptions.clear();
		for (int i = 0; i < CNames.size(); i++) {

			String C[] = CNames.get(i).split("@@@");
			nameOfEvent.add(C[1]);
            String timestamp="";
            if (C[0].contains("Date")) {
               timestamp = C[0].split("\\(")[1].split("\\-")[0];
            }else
            {
                timestamp=C[0].split("-")[0];
            }
            Date createdOn = new Date(Long.parseLong(timestamp));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(createdOn);

			startDates.add(formattedDate);
			endDates.add(formattedDate);
			descriptions.add(C[1]);
			name[i] = C[1];
		}

		return nameOfEvent;
	}

	public static String getDate(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    public static String getURLImage(String url)
    {
        StringBuilder sb=new StringBuilder();
        for(int k=0;k<url.length();k++)
        {

            if (url.charAt(k) =='\\')
            {
                url.replace("\"","");
                sb.append("/");
            }
            else
            {
                sb.append(url.charAt(k));
            }
        }
        return sb.toString();
    }

    //Set List View Height Based on Children in List
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup)
                listItem.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
