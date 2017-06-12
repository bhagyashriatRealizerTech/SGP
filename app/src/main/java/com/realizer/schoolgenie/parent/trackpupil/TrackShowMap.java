package com.realizer.schoolgenie.parent.trackpupil;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.realizer.schoolgenie.parent.R;
import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.parent.trackpupil.asynctask.TrackingAsyncTaskAuto;
import com.realizer.schoolgenie.parent.utils.Config;
import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;
import com.realizer.schoolgenie.parent.view.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shree on 10/29/2015.
 */
public class TrackShowMap extends AppCompatActivity implements OnTaskCompleted,OnMapReadyCallback {
    private GoogleMap mMap;
    String Latitude;
    String Longitude;
    Marker mMarker;
    private PolylineOptions mPolylineOptions;
    ProgressWheel map_loading;
    TextView refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_maps_activity1);
        map_loading= (ProgressWheel) findViewById(R.id.map_loading);
        refresh = (TextView) findViewById(R.id.txt_refresh);
        //getActionBar().setTitle(Config.actionBarTitle("Map", TrackShowMap.this));
        getSupportActionBar().setTitle(Config.actionBarTitle("Map", TrackShowMap.this));
        getSupportActionBar().show();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new AutoSyncServerDataTrack(), 1000, 1000 * 30);


    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                Bundle bundle = getIntent().getExtras();
                setUpMap(bundle.getString("LATITUDE", ""), bundle.getString("LONGITUDE", ""));
            }
        }
    }

    private void setUpMap(String lati, String longi) {
        mMap.setMyLocationEnabled(true);
       // mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
         mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID );
        // mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN );
        // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Bundle bundle=getIntent().getExtras();
        String userName=bundle.getString("USERNAME");
        String currentLatitude =lati;
        String currentLongitude =longi;

        LatLng latLng = new LatLng(Double.parseDouble(currentLatitude), Double.parseDouble(currentLongitude));

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(userName + " is \"" + getDriverCurrentAddress(currentLatitude, currentLongitude) + "\"")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mMarker = mMap.addMarker(options);
        mMarker.setPosition(latLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18.0f));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(Color.RED).width(7);
    }

    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain"};

    private void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }

    @Override
    public void onTaskCompleted(String s) {

        map_loading.setVisibility(View.GONE);
        refresh.setVisibility(View.GONE);
        String dailyDriverList = s;
        if(s.equals(","))
        {
            Toast.makeText(getApplicationContext(), "Server Not Responding ", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                JSONArray locList = new JSONArray(s);
               // for(int i=locList.length()-1;i>=0;i--) {
                    JSONObject obj = locList.getJSONObject((locList.length()-1));
                    LatLng latLng = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));
                    animateMarker(mMarker, latLng, false);
                //}
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;

               // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),16.0f));
                marker.setPosition(new LatLng(lat, lng));
                mMap.addPolyline(mPolylineOptions.add(new LatLng(lat, lng)));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setUpMapIfNeeded();

    }

    class AutoSyncServerDataTrack extends TimerTask
    {
        @Override
        public void run() {

            TrackAsync();
        }
    }
    public void TrackAsync()
    {
        StringBuilder result;
        Bundle bundle=getIntent().getExtras();
        String driverName=bundle.getString("USERNAME");
        String driverId=bundle.getString("USERID");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TrackShowMap.this);
        String accessToken=preferences.getString("AccessToken","");
        String deviceid=preferences.getString("DWEVICEID","");
        String userId=preferences.getString("UidName","");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                map_loading.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.VISIBLE);
            }
        });
        TrackingAsyncTaskAuto obj = new TrackingAsyncTaskAuto(driverName,driverId, TrackShowMap.this,accessToken,deviceid,userId,TrackShowMap.this);
        obj.execute();

    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        //done = menu.findItem(R.id.add_contact_done);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1 :
                showMapTypeSelectorDialog();
                return true;

            case R.id.item_switch:
                TrackingDialogBoxActivity fragment = new TrackingDialogBoxActivity();
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setCancelable(true);
                fragment.show(fragmentManager, "Dialog!");
                return true;

            default :
                return super.onOptionsItemSelected(item);
        }
    }

    public String getDriverCurrentAddress(String lat,String lon)
    {

        Double latitude = Double.parseDouble(lat);
        Double longitude = Double.parseDouble(lon);

        Geocoder geocoder = new Geocoder(TrackShowMap.this, Locale.getDefault());
        String city = "";
        String address="";
        String state = "";
        String country="";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();

        } catch (IOException e) {
            //Toast.makeText(getActivity(), "There is no any address", Toast.LENGTH_SHORT).show();
            Log.e("Track.LocalizedMessage", e.getLocalizedMessage());
            Log.e("Track(StackTrace)", e.getStackTrace().toString());
            Log.e("Track(Cause)", e.getCause().toString());
            Log.wtf("Track(Msg)", e.getMessage());
        }
        return address+","+city+","+country;
    }
}
