package com.realizer.schoolgenie.parent.funcenter.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterGalleryModel;
import com.realizer.schoolgenie.parent.utils.GetImages;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by Win on 08/04/2016.
 */
public class ParentFunCenterGalleryAdapter extends BaseAdapter
{
    private ArrayList<ParentFunCenterGalleryModel> elementDetails;
    private LayoutInflater mInflater;
    Context context;
    int eventPos;
    private static Bitmap bitmap;

    public ParentFunCenterGalleryAdapter(Context context, ArrayList<ParentFunCenterGalleryModel> results,int eventpos)
    {
        elementDetails = results;
        mInflater = LayoutInflater.from(context);
        this.context=context;
        this.eventPos=eventpos;
    }

    @Override
    public int getCount() {
        return elementDetails.size();
    }

    public Object getItem(int position)
    {
        return elementDetails.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return elementDetails.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
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


        ParentFunCenterGalleryModel rowItem = (ParentFunCenterGalleryModel) getItem(position);

        if (rowItem.getImage() != null)
        {
            final String newPath=new Utility().getURLImage(elementDetails.get(position).getImage());

            if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1]))
                //new GetImages(newURL,holder.imageView,newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
            {
                Picasso.with(context).load(newPath).error(R.mipmap.ic_launcher)
                        .into(holder.imageView, new Callback.EmptyCallback() {
                            @Override
                            public void onSuccess() {
                                holder.firstBar.setVisibility(View.GONE);
                                holder.textView.setText("Image_"+eventPos+position+1);
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
                                            holder.textView.setText("Image_"+eventPos+position+1);
                                        }
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
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                holder.imageView.setImageBitmap(bitmap);
                holder.textView.setText("Image_"+eventPos+position+1);
                holder.firstBar.setVisibility(View.GONE);
            }

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

    private class ViewHolder {
        ImageView imageView;
        TextView textView;
        ProgressBar firstBar;
    }
}
