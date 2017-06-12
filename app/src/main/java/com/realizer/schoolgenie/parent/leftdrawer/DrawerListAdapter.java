package com.realizer.schoolgenie.parent.leftdrawer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;

import java.util.ArrayList;

public class DrawerListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<DrawerItem> navDrawerItems;
	
	public DrawerListAdapter(Context context, ArrayList<DrawerItem> navDrawerItems){
		this.context = context;
		this.navDrawerItems = navDrawerItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
        ViewHolder holder;
		if (convertView == null) 
		{
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
            holder = new ViewHolder();
            holder.imgIcon = (ImageView) convertView.findViewById(R.id.icon);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtCount = (TextView) convertView.findViewById(R.id.counter);
            holder.rl = (RelativeLayout)convertView.findViewById(R.id.drawerlistlayout);
            convertView.setTag(holder);

        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }





        holder.imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
        holder.txtTitle.setText(navDrawerItems.get(position).getTitle());

        if(holder.txtTitle.getText().equals("Manager Self Service") ||holder.txtTitle.getText().equals("Employee Self Service") || holder.txtTitle.getText().equals("Other Services")) {
            holder.txtTitle.setTypeface(null, Typeface.BOLD);
            holder.rl.setBackgroundColor(Color.parseColor("#33578D"));
            holder.txtTitle.setTextSize(20);
            holder.txtTitle.setTextColor(Color.WHITE);
        }
        
        // displaying count
        // check whether it set visible or not
        if(navDrawerItems.get(position).getCounterVisibility())
        {
            holder.txtCount.setText(navDrawerItems.get(position).getCount());
        }
        else
        {
           	holder.txtCount.setVisibility(View.GONE);
        }
        
        return convertView;
	}
    static class ViewHolder
    {
        ImageView imgIcon;
        TextView txtTitle ;
        TextView txtCount;
        RelativeLayout rl;
    }

}
