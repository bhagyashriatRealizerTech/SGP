package com.realizer.schoolgenie.parent.funcenter.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterModel;
import com.realizer.schoolgenie.parent.funcenter.model.RowItem;

import java.io.InputStream;
import java.net.URL;
import java.util.List;


/**
 * Created by Win on 30/03/2016.
 */
public class ParentFunCenterFolderAdapter extends BaseAdapter
{
    ImageView image;
    Bitmap bitmap1;
    TextView text;
    String data;
    ParentFunCenterModel getImage1;
    DatabaseQueries qr;
    private LayoutInflater mInflater;
    Context context;
    String eventname;
    ProgressDialog pDialog;
    Bitmap bitmap;
    List<RowItem> rowItems;

    public ParentFunCenterFolderAdapter(Context context,List<RowItem> items)
    {
        this.rowItems = items;
        mInflater = LayoutInflater.from(context);
        this.context=context;
    }

    @Override
    public int getCount()
    {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        convertView = mInflater.inflate(R.layout.teacher_funcenter_folder, null);

        image = (ImageView) convertView.findViewById(R.id.gallery_item_image);
        text= (TextView) convertView.findViewById(R.id.image_name);
        RowItem rowItem = (RowItem) getItem(position);
        image.setImageBitmap(rowItem.getBitmapImage());
        text.setText("Sample");
       /* qr=new DatabaseQueries(context);
       SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(context);
        String cls = preferences1.getString("Images2", "");

        String image1=getImage1.getImage();
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<image1.length();i++)
        {
            char c='\\';
            if (image1.charAt(i) =='\\')
            {
                image1.replace("\"","");
                sb.append("/");
            }
            else
            {
                sb.append(image1.charAt(i));
            }
        }
        String whole=sb.toString();*/
        //image1.replaceAll("\"","/");
       /* String eventnm=getImage1.getText();
        int evntid=getImage1.getEventid();*/

       /* byte[] decodedString = Base64.decode(image1, Base64.DEFAULT);
        bitmap1 = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        image.setImageBitmap(bitmap1);
        image.invalidate();
*/
       /* options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.ic_add)
            .showImageForEmptyUri(R.drawable.ic_media_pause)
            .cacheInMemory()
            .cacheOnDisc()
            .build();
        imageLoader.displayImage(whole, image, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(Bitmap loadedImage) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                image.setAnimation(anim);
                anim.start();
            }
        });*/

       /* try {
            bitmap = BitmapFactory.decodeStream((InputStream)new URL(whole).getContent());
            image.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
       //new LoadImage().execute(getImage1.getImage());


      /*  Bitmap imageBitmap = null;
        try {
            URL imageURL = new URL(whole);
            imageBitmap = BitmapFactory.decodeStream(imageURL.openStream());
            image.setImageBitmap(imageBitmap);
        } catch (IOException e) {
            image.setImageResource(R.drawable.dot_green);
        }
*/
       // text.setText(eventnm);

        return convertView;
    }
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* pDialog = new ProgressDialog(context);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();
*/
        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image1) {

            if(image1 != null){
                image.setImageBitmap(image1);
                //pDialog.dismiss();

            }else{
               // pDialog.dismiss();
            }
        }
    }


}