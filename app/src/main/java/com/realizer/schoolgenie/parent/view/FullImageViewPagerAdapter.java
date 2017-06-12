package com.realizer.schoolgenie.parent.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.homework.model.ParentHomeworkListModel;
import com.realizer.schoolgenie.parent.utils.GetImages;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.StoreBitmapImages;
import com.realizer.schoolgenie.parent.utils.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

public class FullImageViewPagerAdapter extends PagerAdapter {

    private static List<ParentHomeworkListModel> attachmentList;
    private Context context;
    private LayoutInflater inflater;
    private ViewHolder holder;
    String[] IMG;
    private ProgressBar firstBar = null;

    public FullImageViewPagerAdapter(Context context, List<ParentHomeworkListModel> attachmentList) {
        this.context = context;
        FullImageViewPagerAdapter.attachmentList = attachmentList;
        IMG = new String[attachmentList.size()];
    }

    @Override
    public int getCount() {
        return attachmentList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        //Inflate the view
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.fullimageview_parent, container, false);

        holder = new ViewHolder();

        holder.imgview = (ImageView) itemView.findViewById(R.id.imageView);
       // holder.imgview.setTag(position);
        holder.txtcnt = (TextView) itemView.findViewById(R.id.txtcounter);
        holder.txtcnt.setMovementMethod(new ScrollingMovementMethod());
        holder.firstBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        //holder.firstBar.setTag(position);
        //holder.txtcnt.setText("" + (position + 1) + " / " + attachmentList.size());
        holder.txtcnt.setText(attachmentList.get(position).getHomework());

        final String newPath=new Utility().getURLImage(attachmentList.get(position).getImage());

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
               // new GetHCImages(newPath,position).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                holder.firstBar.setVisibility(View.VISIBLE);
                holder.imgview.setVisibility(View.GONE);
                Picasso.with(context).load(newPath).error(R.mipmap.ic_launcher)
                        .into(holder.imgview, new Callback.EmptyCallback() {
                            @Override
                            public void onSuccess() {
                                holder.firstBar.setVisibility(View.GONE);
                                holder.imgview.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                holder.firstBar.setVisibility(View.GONE);
                                holder.imgview.setVisibility(View.VISIBLE);
                            }
                        });

                Picasso.with(context)
                        .load(newPath)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                // not being called the first time
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                       /* if(bitmap != null) {
                                            if (!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                                                ImageStorage.saveToSdCard(bitmap, newPath.split("/")[newPath.split("/").length - 1]);
                                            }
                                            holder.imgview.setVisibility(View.VISIBLE);
                                        }*/
                                        new GetImages(newPath,holder.imgview,newPath.split("/")[newPath.split("/").length - 1]).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,newPath);
                                        //holder.imgview.setVisibility(View.VISIBLE);
                                    }
                                }).start();
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });

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
    }

    public class GetHCImages extends AsyncTask<Object, Integer, Object> {
        private String requestUrl;
        private int position;
        private Bitmap bitmap ;
        private FileOutputStream fos;
        //int i=0;
        public GetHCImages(String requestUrl,int pos) {
            this.requestUrl = requestUrl;
            this.position = pos;
        }

        @Override
        protected void onPreExecute() {
            holder.imgview.setVisibility(View.GONE);
            holder.firstBar.setVisibility(View.VISIBLE);
            /*ObjectAnimator anim = ObjectAnimator.ofInt(holder.firstBar, "progress", 0, 100);
            anim.setDuration(5000);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.start();*/
        }


        @Override
        protected Object doInBackground(Object... objects) {
            InputStream inputStream = null;
            BufferedOutputStream outputStream = null;
            int count = 0;
            try {
                URL url = new URL(requestUrl);
                URLConnection conn = url.openConnection();
                // bitmap = BitmapFactory.decodeStream(conn.getInputStream());

                int lenghtOfFile = conn.getContentLength();
                inputStream = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

                outputStream = new BufferedOutputStream(dataStream);


                byte data[] = new byte[512];
                long total = 0;

                while ((count = inputStream.read(data)) != -1) {
                    total += count;
		            /*publishing progress update on UI thread.
		            Invokes onProgressUpdate()*/
                    publishProgress((int)((total*100)/lenghtOfFile));

                    // writing data to byte array stream
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 1;

                byte[] bytes = dataStream.toByteArray();
               // bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,bmOptions);
               bitmap = BitmapFactory.decodeStream(conn.getInputStream());



            } catch (Exception ex) {
                ex.printStackTrace();
            }



            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            holder.firstBar.setProgress(values[0]);
            notifyDataSetChanged();
            if(values[0] == 100)
                Log.d("Progress 100 for position "+position,": "+new Date().toString());

        }


        @Override
        protected void onPostExecute(Object o) {
            Bitmap bmp = (Bitmap)o;
            Log.d("Progress OnPost for Position "+position,": "+new Date().toString());
            holder.firstBar.setVisibility(View.GONE);
            holder.imgview.setVisibility(View.VISIBLE);
            if(bmp != null) {
                holder.imgview.setImageBitmap(bmp);
                if (!ImageStorage.checkifImageExists(requestUrl.split("/")[requestUrl.split("/").length - 1])) {
                    ImageStorage.saveToSdCard(bitmap, requestUrl.split("/")[requestUrl.split("/").length - 1]);
                }
            }

            notifyDataSetChanged();
        }
    }
}
