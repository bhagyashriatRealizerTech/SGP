package com.realizer.schoolgenie.parent.funcenter;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.realizer.schoolgenie.parent.DrawerActivity;
import com.realizer.schoolgenie.parent.FragmentBackPressedListener;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.funcenter.adapter.CustomListViewAdapter;
import com.realizer.schoolgenie.parent.funcenter.adapter.ParentFunCenterFolderAdapter;
import com.realizer.schoolgenie.parent.funcenter.backend.DLAFuncenter;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterGalleryModel;
import com.realizer.schoolgenie.parent.funcenter.model.ParentFunCenterModel;
import com.realizer.schoolgenie.parent.pupil.backend.DALMyPupilInfo;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.ImageStorage;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.utils.Singleton;
import com.realizer.schoolgenie.parent.utils.StoreBitmapImages;
import com.realizer.schoolgenie.parent.utils.Utility;
import com.realizer.schoolgenie.parent.view.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Win on 18/04/2016.
 */
public class ParentFunCenterFolderFragment extends Fragment implements OnTaskCompleted,FragmentBackPressedListener
{
    TextView newevent;
    GridView folderdgridview;
    ParentFunCenterFolderAdapter adapter2;
    DLAFuncenter dla;
    DALMyPupilInfo DAP;
    ArrayList<ParentFunCenterModel> allData1;
    int eid;
   // ProgressWheel progressWheel;
    TextView noData;
    long n;
    CustomListViewAdapter listViewAdapter;
    ListView listView;
    //ProgressDialog progressDialog;
    ArrayList<ParentFunCenterModel> allNewData;
    ArrayList<ParentFunCenterModel> allUpdatedData;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.teacher_funcenter_folder_fragment, container, false);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Fun Center", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        //newevent= (TextView) rootView.findViewById(R.id.txtnewthumbnail);
        folderdgridview= (GridView) rootView.findViewById(R.id.foldergridView);
        noData = (TextView) rootView.findViewById(R.id.tvNoDataMsg);
        //progressWheel=(ProgressWheel) rootView.findViewById(R.id.loading);
        dla=new DLAFuncenter(getActivity());
        allData1=new ArrayList<>();
        Bundle b = getArguments();
        String htext = b.getString("HEADERTEXT");

        allData1=Singleton.getFuncenterEvents();
        if (allData1.size()>0) {
            listViewAdapter = new CustomListViewAdapter(getActivity(), allData1);
            folderdgridview.setAdapter(listViewAdapter);
            listViewAdapter.notifyDataSetChanged();
            noData.setVisibility(View.GONE);
            folderdgridview.setVisibility(View.VISIBLE);
          //  progressWheel.setVisibility(View.GONE);
        }
        else
        {
            noData.setVisibility(View.VISIBLE);
            folderdgridview.setVisibility(View.GONE);
           // progressWheel.setVisibility(View.GONE);
        }

        folderdgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ArrayList<ParentFunCenterGalleryModel> allData=dla.GetImage(allData1.get(position).getEventUUID());
                Singleton.setFuncenterEventImages(null);
                Singleton.setFuncenterEventImages(allData);
                Bundle b = new Bundle();
                ParentFunCenterGalleryFragment fragment = new ParentFunCenterGalleryFragment();
                Singleton.setFragment(fragment);
                //Singleton.setMainFragment(fragment);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                int putid = allData1.get(position).getEventid();
                String evntname = allData1.get(position).getText();
                b.putInt("EventPos", position);
                b.putString("EventName", evntname);
                b.putString("EventUUID", allData1.get(position).getEventUUID());
                fragment.setArguments(b);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }

    @Override
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public boolean parsData(String json) {
        long n =-1;
        DLAFuncenter dla = new DLAFuncenter(getActivity());
        allNewData =new ArrayList<>();
        allUpdatedData =new ArrayList<>();
        JSONObject rootObj = null;
        Log.d("String", json);
        try {
            rootObj = new JSONObject(json);
            JSONArray jsonArray = rootObj.getJSONArray("eventMDLst");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String createTS  = obj.getString("CreateTS");
                String evntname  = obj.getString("Event");
                String eventDate= obj.getString("EventDate");
                String evntuuid= obj.getString("EventId");
                String thmbnail= obj.getString("ThumbNailImage");
                String thumbNailPath= obj.getString("ThumbNailPath");

                ArrayList<ParentFunCenterModel> getEventData=dla.GetEventInfoData();
                boolean isPresent=false;
                int eventId=0;
                for (int j=0;j<getEventData.size();j++)
                {
                    if (getEventData.get(j).getEventUUID().equalsIgnoreCase(evntuuid))
                    {
                        isPresent=true;
                        eventId=getEventData.get(j).getEventid();
                        break;
                    }
                }
                if (isPresent)
                {
                    dla.updateEventSyncFlag(eventId, createTS, evntname, eventDate, evntuuid, thmbnail, thumbNailPath);
                }
                else
                {
                    String newPath = new Utility().getURLImage(thumbNailPath);
                    if(!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                        new StoreBitmapImages(newPath,newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                    }
                    dla.insertEventInfo(createTS, evntname, eventDate, evntuuid, thmbnail, thumbNailPath);
                }
            }

            final ArrayList<ParentFunCenterModel> allData1=dla.GetEventInfoData();
            if (allData1.size()>0 ||allData1 !=null) {
                listViewAdapter = new CustomListViewAdapter(getActivity(), allData1);
                folderdgridview.setAdapter(listViewAdapter);
                listViewAdapter.notifyDataSetChanged();
                //progressDialog.dismiss();
            }
            else
            {
               // progressDialog.dismiss();
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

    @Override
    public void onTaskCompleted(String s)
    {
        boolean b = false;
        b = parsData(s);
    }

    public boolean isConnectingToInternet(){

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
}
