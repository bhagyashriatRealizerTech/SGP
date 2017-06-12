package com.realizer.schoolgenie.parent.funcenter.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterModel;
import com.realizer.schoolgenie.parent.utils.GetImages;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by shree on 8/29/2016.
 */
public class CustomListViewAdapter extends BaseAdapter {

    Context context;
    List<ParentFunCenterModel> rowItems;
    private static Bitmap bitmap;

    public CustomListViewAdapter(Context context, List<ParentFunCenterModel> items) {
        this.context = context;
        this.rowItems = items;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textView;
        ProgressBar firstBar;
    }


    public int getCount() {
        return rowItems.size();
    }

    public Object getItem(int position) {
        return rowItems.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
       final ViewHolder holder;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.teacher_funcenter_folder, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.gallery_item_image);
            holder.textView= (TextView) convertView.findViewById(R.id.image_name);
            holder.firstBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        ParentFunCenterModel rowItem = (ParentFunCenterModel) getItem(position);
        if (rowItem.getImage() != null)
        {
            final String newPath=new Utility().getURLImage(rowItems.get(position).getImage());

            if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1]))
                //new GetFuncenterEvents(newURL,holder.imageView,newURL.split("/")[newURL.split("/").length-1]).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,newURL);
            {
                Picasso.with(context).load(newPath).error(R.mipmap.ic_launcher)
                        .into(holder.imageView, new Callback.EmptyCallback() {
                            @Override
                            public void onSuccess() {
                                holder.firstBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                holder.firstBar.setVisibility(View.GONE);
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
                                        if(bitmap != null) {
                                            if (!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                                                ImageStorage.saveToSdCard(bitmap, newPath.split("/")[newPath.split("/").length - 1]);
                                            }
                                        }
                                       /* File sdcard = Environment.getExternalStorageDirectory() ;

                                        File folder = new File(sdcard.getAbsoluteFile(), ".UserDP");
                                        folder.mkdir();
                                        File file = new File(folder.getAbsoluteFile(), newPath.split("/")[newPath.split("/").length - 1]) ;

                                        if (file.exists())
                                            //file.delete();
                                            try {
                                                // file.mkdir();
                                                FileOutputStream ostream = new FileOutputStream(file);
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                                ostream.close();
                                                ostream.flush();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }*/
                                        //holder.firstBar.setVisibility(View.GONE);
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
                File image = ImageStorage.getImage(newPath.split("/")[newPath.split("/").length-1]);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = null;
                if(image != null)
                bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);

                if (bitmap != null)
                holder.imageView.setImageBitmap(bitmap);
                else
                bitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.sorryimage);
                holder.imageView.setImageBitmap(bitmap);
                holder.firstBar.setVisibility(View.GONE);
            }

            holder.textView.setText(rowItems.get(position).getText());
            holder.imageView.setVisibility(View.VISIBLE);
            holder.textView.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.imageView.setVisibility(View.GONE);
            holder.textView.setVisibility(View.GONE);
            holder.firstBar.setVisibility(View.GONE);
        }

        return convertView;
    }
}
