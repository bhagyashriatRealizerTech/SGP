package com.realizer.schoolgenie.parent.funcenter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
import com.realizer.schoolgenie.parent.funcenter.adapter.ParentFunCenterGalleryAdapter;
import com.realizer.schoolgenie.parent.funcenter.backend.DLAFuncenter;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterGalleryModel;
import com.realizer.schoolgenie.parent.funcenter.model.RowItem;
import com.realizer.schoolgenie.parent.pupil.backend.DALMyPupilInfo;
import com.realizer.schoolgenie.parent.utils.FileUtils;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.utils.StoreBitmapImages;
import com.realizer.schoolgenie.parent.utils.Utility;
import com.realizer.schoolgenie.parent.view.FullImageViewActivity;
import com.realizer.schoolgenie.parent.view.FullImageviewFunCenterPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Win on 07/04/2016.
 */
public class ParentFunCenterGalleryFragment extends Fragment implements OnTaskCompleted,FragmentBackPressedListener
{
    GridView gridView;
    DatabaseQueries qr;
    ParentFunCenterGalleryAdapter adapter1;
    TextView noData;
    DLAFuncenter dla;
    String aa;
    int eid;
    DALMyPupilInfo DAP;
    String evntid="";
    String path="";
    ParentFunCenterGalleryAdapter listViewAdapter;
    //ProgressDialog progressDialog;
    ArrayList<ParentFunCenterGalleryModel> allData;
    String eventUUID;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.showimage_gallery_fragment, container, false);
        gridView= (GridView) rootView.findViewById(R.id.gallerygridView);
        noData = (TextView) rootView.findViewById(R.id.tvNoDataMsg);
        //btnadd= (TextView) rootView.findViewById(R.id.btnaddimage);

        dla=new DLAFuncenter(getActivity());

        Bundle bundle1=getArguments();
        int getPos=bundle1.getInt("EventPos");
        String getevntName=bundle1.getString("EventName");
        eventUUID=bundle1.getString("EventUUID");
        allData=Singleton.getFuncenterEventImages();
        if (allData.size()>0) {
            listViewAdapter = new ParentFunCenterGalleryAdapter(getActivity(), allData,getPos);
            gridView.setAdapter(listViewAdapter);
            listViewAdapter.notifyDataSetChanged();

            StringBuilder sb=new StringBuilder();
            for (int k=0;k<allData.size();k++)
            {
                sb.append(allData.get(k).getImage());
                sb.append("@@@");
            }
            path=sb.toString();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("HomeworkImage", path);
            editor.commit();
            noData.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            //progressDialog.dismiss();
        }
        else
        {
            noData.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            //progressDialog.dismiss();
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
               //String selectedItem = parent.getItemAtPosition(position).toString();
                Intent i = new Intent(getActivity(),FullImageviewFunCenterPager.class);
                /*i.putExtra("FLAG",1);
                i.putExtra("HEADERTEXT","FuncenterImages");
                i.putExtra("POSITION",position);
                startActivity(i);*/
                i.putExtra("POSITION",position);
                i.putExtra("HEADERTEXT","FuncenterImages");
                i.putExtra("HWUUID", allData.get(position).getImguuid());
                startActivity(i);
            }
        });

        return rootView;
    }

    @Override
    public void onFragmentBackPressed() {
        Singleton.setFragment(Singleton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }

    public boolean isConnectingToInternet()
    {

        ConnectivityManager connectivity =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    @Override
    public void onTaskCompleted(String s)
    {
        boolean b = false;
        b = parsData(s);
      /*  if (b==true)
        {
            allData=dla.GetImage(eventUUID);
            if (allData != null && allData.size() > 0) {
                GetXMLTask task = new GetXMLTask(getActivity(), allData);
                task.execute();
            }
        }*/
    }

    public boolean parsData(String json) {
        long n =-1;
        DLAFuncenter dla = new DLAFuncenter(getActivity());

        JSONObject rootObj = null;
        Log.d("String", json);
        try {
            rootObj = new JSONObject(json);
            JSONArray jsonArray = rootObj.getJSONArray("Images");

            ArrayList<UUID> imgUUID=new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String createTS  = obj.getString("CreateTime");
                evntid= obj.getString("EventId");
                String imgcaption  = obj.getString("ImageCaption");
                String imgid= obj.getString("ImageId");
                String srno= obj.getString("SrNo");
                String thumbNailPath= obj.getString("fileName");
                String uploadDate= obj.getString("uploadDate");

                allData=dla.GetImage(eventUUID);

                boolean isPresent=false;
                int imgId=0;
                for (int j=0;j<allData.size();j++)
                {
                    if (imgid.equalsIgnoreCase(allData.get(j).getImguuid()))
                    {
                        isPresent=true;
                        imgId= Integer.valueOf(allData.get(j).getImageid());
                        break;
                    }
                }

                if (isPresent)
                {
                    dla.updateImageSyncFlag(imgId,createTS, evntid, imgcaption, imgid, srno, thumbNailPath, uploadDate);

                    /*String newPath = new Utility().getURLImage(thumbNailPath);
                    if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                        new StoreBitmapImages(newPath,newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                    }*/
                }
                else
                {
                    dla.InsertImage(createTS, evntid, imgcaption, imgid, srno, thumbNailPath, uploadDate);

                    String newPath = new Utility().getURLImage(thumbNailPath);
                    if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                        new StoreBitmapImages(newPath,newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                    }
                }

            }
            allData=dla.GetImage(eventUUID);
            if (allData.size()>0 ||allData !=null) {
                listViewAdapter = new ParentFunCenterGalleryAdapter(getActivity(), allData,0);
                gridView.setAdapter(listViewAdapter);
                listViewAdapter.notifyDataSetChanged();

                StringBuilder sb=new StringBuilder();
                for (int k=0;k<allData.size();k++)
                {
                    sb.append(allData.get(k).getImage());
                    sb.append("@@@");
                }
                path=sb.toString();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("HomeworkImage", path);
                editor.commit();
                //progressDialog.dismiss();
            }
            else
            {
                //progressDialog.dismiss();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.toString());
            Log.e("HWJ(LocalizedMessage)", e.getLocalizedMessage());
            Log.e("HWJ(StackTrace)", e.getStackTrace().toString());
            Log.e("HWJ(Cause)", e.getCause().toString());
        }
        return true;
    }
}
