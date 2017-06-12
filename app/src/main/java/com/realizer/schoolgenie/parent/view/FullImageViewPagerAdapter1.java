package com.realizer.schoolgenie.parent.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.homework.model.ParentHomeworkListModel;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.Utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by shree on 11/24/2016.
 */
public class FullImageViewPagerAdapter1 extends PagerAdapter {
    private static List<ParentHomeworkListModel> attachmentList;
    private Context context;
    private LayoutInflater inflater;
    private ViewHolder holder;
    String[] IMG;
    private ProgressBar firstBar = null;
    DownloadImageAsync downloadImageAsync = null ;

    public FullImageViewPagerAdapter1(Context context, List<ParentHomeworkListModel> attachmentList) {
        this.context = context;
        FullImageViewPagerAdapter1.attachmentList = attachmentList;
        IMG = new String[attachmentList.size()];
    }

    @Override
    public int getCount() {
        return attachmentList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.fullimageview_parent, container, false);

        holder = new ViewHolder();

        holder.imgview = (ImageView) itemView.findViewById(R.id.imageView);
        holder.imgview.setTag(position);
        holder.txtcnt = (TextView) itemView.findViewById(R.id.txtcounter);
        holder.txtcnt.setMovementMethod(new ScrollingMovementMethod());
        holder.firstBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        holder.firstBar.setTag(position);
        //holder.txtcnt.setText("" + (position + 1) + " / " + attachmentList.size());
        holder.txtcnt.setText(attachmentList.get(position).getHomework());
        holder.bitmap = null;
        itemView.setTag(holder);

        holder = (ViewHolder) itemView.getTag();

        String newPath=new Utility().getURLImage(attachmentList.get(position).getImage());

        if (newPath.equalsIgnoreCase(""))
        {
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sorryimage);
            holder.imgview.setImageBitmap(icon);
            holder.firstBar.setVisibility(View.GONE);
        }
        else
        {

            if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {

                if(downloadImageAsync != null) {
                    if (downloadImageAsync.getStatus() == AsyncTask.Status.FINISHED) {
                        downloadImageAsync = new DownloadImageAsync(newPath, position);
                        downloadImageAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, holder);
                    }
                }
                else
                {
                    downloadImageAsync = new DownloadImageAsync(newPath, position);
                    downloadImageAsync.execute(holder);
                }

            }
            else
            {
                holder.firstBar.setVisibility(View.GONE);
                holder.imgview.setVisibility(View.VISIBLE);
                File image = ImageStorage.getImage(newPath.split("/")[newPath.split("/").length - 1]);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                holder.imgview.setImageBitmap(bitmap);
            }

        }
        // Add viewpager_item.xml to ViewPager
        (container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        container.removeView((LinearLayout) object);
    }

    class ViewHolder {
        TextView txtcnt;
        ImageView imgview;
        ProgressBar firstBar;
        Bitmap bitmap;
        Integer progress;
    }


    class DownloadImageAsync extends AsyncTask<ViewHolder,ViewHolder,ViewHolder>
    {

        private String requestUrl;
        private int position;
        private Bitmap bitmap ;
        private FileOutputStream fos;

        public DownloadImageAsync(String requestUrl,int pos) {
            this.requestUrl = requestUrl;
            this.position = pos;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ViewHolder doInBackground(ViewHolder... viewHolders) {
            ViewHolder viewHolder = viewHolders[0];
            InputStream inputStream = null;
            BufferedOutputStream outputStream = null;
            int count = 0;
            try {
                URL imageURL = new URL(requestUrl);
                URLConnection conn = imageURL.openConnection();

                int lenghtOfFile = conn.getContentLength();
                inputStream = new BufferedInputStream(imageURL.openStream());
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

                outputStream = new BufferedOutputStream(dataStream);


                byte data[] = new byte[512];
                long total = 0;

                while ((count = inputStream.read(data)) != -1) {
                    total += count;
		            /*publishing progress update on UI thread.
		            Invokes onProgressUpdate()*/
                    viewHolder.progress = (int)((total*100)/lenghtOfFile);
                    publishProgress(viewHolder);

                    // writing data to byte array stream
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();

                viewHolder.bitmap = BitmapFactory.decodeStream(conn.getInputStream());

            } catch (IOException e) {
                // TODO: handle exception
                Log.e("error", "Downloading Image Failed");
                viewHolder.bitmap = null;
            }
            return viewHolder;

        }


        @Override
        protected void onProgressUpdate(ViewHolder... values) {
            super.onProgressUpdate(values);
            ViewHolder viewholder = values[0];
           viewholder.firstBar.setProgress(viewholder.progress);

        }

        @Override
        protected void onPostExecute(ViewHolder viewHolder) {
            super.onPostExecute(viewHolder);
            viewHolder.firstBar.setVisibility(View.GONE);
            viewHolder.imgview.setVisibility(View.VISIBLE);
            if(viewHolder.bitmap != null) {
                viewHolder.imgview.setImageBitmap(viewHolder.bitmap);
                if (!ImageStorage.checkifImageExists(requestUrl.split("/")[requestUrl.split("/").length - 1])) {
                    ImageStorage.saveToSdCard(viewHolder.bitmap, requestUrl.split("/")[requestUrl.split("/").length - 1]);
                }
            }
        }

    }
}
