package com.realizer.schoolgenie.parent.Notification;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.chat.backend.DALQueris;
import com.realizer.schoolgenie.parent.chat.model.ParentQueriesTeacherNameListModel;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.GetImages;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.SwipeLayout;


import java.io.File;
import java.util.ArrayList;

public class TeacherNotificationListAdapter extends BaseAdapter {

   private static ArrayList<NotificationModel> notifications;
   private LayoutInflater mNotification;
   private Context context1;
   boolean isImageFitToScreen;
   public SwipeLayout prevSwipedLayout;
   public SwipeLayout swipeLayout;
   View convrtview;
   float x1, x2;

    public TeacherNotificationListAdapter(Context context, ArrayList<NotificationModel> notificationList) {
            notifications = notificationList;
            mNotification = LayoutInflater.from(context);
            context1 = context;
            swipeLayout = null;
            prevSwipedLayout = null;
        }

        @Override
        public int getCount() {
            return notifications.size();
        }

        @Override
        public Object getItem(int position) {

            return notifications.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

    @Override
    public int getViewTypeCount() {
        return notifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        convrtview = convertView;

        if (convertView == null) {
            convertView = mNotification.inflate(R.layout.teacher_notification_list_layout, null);
            holder = new ViewHolder();
            holder.notificationText = (TextView) convertView.findViewById(R.id.txtmessage);
            holder.notificationDate = (TextView) convertView.findViewById(R.id.txtdate);
            holder.type = (TextView) convertView.findViewById(R.id.txtnotificationtype);
            holder.unreadCount = (TextView) convertView.findViewById(R.id.txtunreadcount);
            holder.notificationImage = (ImageView) convertView.findViewById(R.id.img_user_image);
           /* holder.swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipeLayout);
            holder.swipeLayout.setTag(position);*/

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

       /* holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.tvNoDataMsg));
        holder.swipeLayout.setLeftSwipeEnabled(false);
        holder.swipeLayout.setRightSwipeEnabled(true);
        holder.swipeLayout.mViewBoundCache.clear();
        holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
                Log.d("Swipe","Close");
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
                Log.d("Swipe","swiping");
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                Log.d("Swipe","startopen");
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
                if (prevSwipedLayout != null && layout != prevSwipedLayout) {
                    prevSwipedLayout.close();
                }
                prevSwipedLayout = layout;
                Log.d("Swipe","open");
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
                Log.d("Swipe","release");
            }

        });
*/

        String notificationData = "";
        String date = notifications.get(position).getNotificationDate();
        holder.unreadCount.setVisibility(View.INVISIBLE);

        if(notifications.get(position).getNotificationtype().equalsIgnoreCase("Homework") || notifications.get(position).getNotificationtype().equalsIgnoreCase("Classwork"))
        {
            DALQueris dbQ=new DALQueris(context1);
            ParentQueriesTeacherNameListModel result=dbQ.GetQueryTableData(notifications.get(position).getAdditionalData1());
            notificationData = "Downloaded "+notifications.get(position).getNotificationtype()+" For "+
                    notifications.get(position).getMessage()+" From "+result.getName();
            holder.notificationImage.setImageResource(R.drawable.homework_icon);
           /* notificationData = notifications.get(position).getAdditionalData1().split("@@@")[2]+" "+
                    notifications.get(position).getNotificationtype()+" "+notifications.get(position).getMessage()
                    +" "+notifications.get(position).getAdditionalData1().split("@@@")[0]+" "+
                    notifications.get(position).getAdditionalData1().split("@@@")[1];

            holder.notificationImage.setImageResource(R.drawable.homework_icon);*/
        }
        else if(notifications.get(position).getNotificationtype().equalsIgnoreCase("TimeTable"))
        {
            DALQueris dbQ=new DALQueris(context1);
            ParentQueriesTeacherNameListModel result=dbQ.GetQueryTableData(notifications.get(position).getAdditionalData1());
            notificationData = "Downloaded Timetable For "+
                    notifications.get(position).getMessage()+" From "+result.getName();
            holder.notificationImage.setImageResource(R.drawable.timetable_icon);
        }
        else if(notifications.get(position).getNotificationtype().equalsIgnoreCase("Star"))
        {
            notificationData = "Received Star From "+notifications.get(position).getAdditionalData1().split("@@@")[0]+" For "+
                    notifications.get(position).getAdditionalData2()+" '"+notifications.get(position).getMessage()+"'.";
            holder.notificationImage.setImageResource(R.drawable.viewstar_icon);
        }
        else if(notifications.get(position).getNotificationtype().equalsIgnoreCase("Alerts"))
        {
            notificationData = "Received Alert For "+
                    notifications.get(position).getAdditionalData2()+" '"+notifications.get(position).getMessage()+"'.";
            holder.notificationImage.setImageResource(R.drawable.annoucement_icon);
        }
        else if(notifications.get(position).getNotificationtype().equalsIgnoreCase("Attendance"))
        {
            notificationData =notifications.get(position).getMessage();
            holder.notificationImage.setImageResource(R.drawable.annoucement_icon);
        }
        else if(notifications.get(position).getNotificationtype().equalsIgnoreCase("Holiday"))
        {
            notificationData ="Tomorrow Is Holiday For "+notifications.get(position).getMessage();
            holder.notificationImage.setImageResource(R.drawable.holiday_icon);
        }
        else if(notifications.get(position).getNotificationtype().equalsIgnoreCase("Fun Center"))
        {
            String Img[]=notifications.get(position).getMessage().split("@@");
            if (notifications.get(position).getAdditionalData1().equals("Events"))
            {
                notificationData = "Downloaded Event '"+
                        Img[0]+"'.";
            }
            else
            {
                if(Integer.valueOf(Img[1])==1)
                    notificationData = "Downloaded "+Img[1]+" Image for '"+Img[0]+"' Event";
                else
                    notificationData = "Downloaded "+Img[1]+" Images for '"+Img[0]+"' Event";
            }

            holder.notificationImage.setImageResource(R.drawable.funcenter_icon);
        }
        else if(notifications.get(position).getNotificationtype().equalsIgnoreCase("Message"))
        {
            notificationData = "Recieved Message From "+
                    notifications.get(position).getAdditionalData1().split("@@@")[0]+"\nMessage : "+notifications.get(position).getMessage().split(":")[1];

            String imageurl[]= notifications.get(position).getAdditionalData1().trim().split("@@@");
            if(imageurl.length == 3) {
                if (imageurl[2] != null && !imageurl[2].equals("") && !imageurl[2].equalsIgnoreCase("null")) {
                    String urlString = imageurl[2];
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < urlString.length(); i++) {
                        char c = '\\';
                        if (urlString.charAt(i) == '\\') {
                            urlString.replace("\"", "");
                            sb.append("/");
                        } else {
                            sb.append(urlString.charAt(i));
                        }
                    }
                    String newURL = sb.toString();
                    holder.notificationImage.setVisibility(View.VISIBLE);
                    if (!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                        new GetImages(newURL, holder.notificationImage, newURL.split("/")[newURL.split("/").length - 1]).execute(newURL);
                    else {
                        File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                        holder.notificationImage.setImageBitmap(bitmap);
                    }
                } else
                    holder.notificationImage.setImageResource(R.drawable.chat_icon);
            }
            else
                holder.notificationImage.setImageResource(R.drawable.chat_icon);

            if(notifications.get(position).getAdditionalData1().split("@@@")[1].equals("0"))
            {
                holder.unreadCount.setVisibility(View.INVISIBLE);
            }
            else
            {
                holder.unreadCount.setVisibility(View.VISIBLE);
                holder.unreadCount.setText(notifications.get(position).getAdditionalData1().split("@@@")[1]);
            }
        }

        date = notifications.get(position).getNotificationDate().trim().split(" ")[0];
        String sdate[]=date.split("/");
        String newDate=sdate[1]+"/"+sdate[0]+"/"+sdate[2];

        holder.type.setText(notifications.get(position).getNotificationtype());
        holder.notificationDate.setText(Config.getDate(newDate, "D"));
        holder.notificationText.setText(notificationData);

        return convertView;
    }

    static class ViewHolder
        {
            TextView notificationText,notificationDate,unreadCount,type;
            ImageView notificationImage;
            SwipeLayout swipeLayout;
        }
    }

