package com.meek;

import android.animation.Animator;
import android.app.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.labo.kaji.fragmentanimations.FlipAnimation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static android.content.ContentValues.TAG;

/**
 * Created by User on 25-May-18.
 */

public class MapsFragment extends Fragment implements OnMapReadyCallback,AdapterView.OnItemSelectedListener, AdapterView.OnItemLongClickListener, View.OnLongClickListener {

    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    public LocationManager mLocationManager = null;
    GoogleMap mMap;
    UserMarker my_marker;
    SupportMapFragment mapFragment;
    BottomSheetBehavior<View> mBottomSheetBehavior1;
    Marker activity;
    LatLng cur_location;
    View bottomSheet,view;
    boolean bs_prof;
    class UserMarker
    {
        Marker marker;
        String uid;
    }
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return FlipAnimation.create(FlipAnimation.RIGHT, enter, 500);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.mapfrag, container, false);
        my_marker=new UserMarker();
        my_marker.marker=null;
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        bs_prof=false;
        mapFragment.getMapAsync( this);

        return view;

    }
    private void bottomSheetSetup() {
        bottomSheet = view.findViewById(R.id.btm_sheet);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(0);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // mBottomSheetBehavior1.setPeekHeight(bottomSheet.getHeight());
                   mBottomSheetBehavior1.setPeekHeight(0);
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    ///all the activity markers should get dissapperared
                    //all the hidden markers of other users gets un hidden
                    bs_prof = false;

                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });
    }
    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
            mMap=googleMap;
            setUserMarker();
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker)
                {
                    if(marker.getSnippet().equals("1"))
                    {
                        if(bs_prof==false)
                        {
                            bs_prof = true;
                            showProfileBottomSheet(marker.getTitle());
                            removeOtherPPLMarkers(marker.getTitle());
                            setActivityMarkers(marker.getTitle());
                        }
                        else
                        {
                            ///flip the view from activity_box to profile_box

                        }
                    }
                    else
                    {
                        if(bs_prof==true)
                        {
                            bs_prof = false;
                            ///flip the view to activity_box from profile
                            //show the specific content in the acitivty marker
                        }
                        else
                        {
                            //refresh the contents in the activity box to new acitvity selected
                            //no flippiing
                        }
                    }


                    return true;
                }
            });
    }

    void setUserMarker()
    {
        Log.e("HAHAH", "setusermarker ");

        Realm.init(getContext());
        final Realm myRealm= Realm.getDefaultInstance();
        final MyDetails myDetails=myRealm.where(MyDetails.class).findFirst();
        if(myDetails!=null) {
            LatLng ll = new LatLng(Double.parseDouble(myDetails.getLat()), Double.parseDouble(myDetails.getLng()));
            setMarker(ll,"0",".Displaypic/pic");
        }
        else
            Toast.makeText(getContext(),"wait pls",Toast.LENGTH_LONG).show();

    }
    void setMarker(LatLng latLng,String uid,String filepoint)
    {
        Toast.makeText(getContext(),"Set marker?",Toast.LENGTH_LONG).show();

        View mrker = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);
        final CircleImageView rdp = (CircleImageView) mrker.findViewById(R.id.imageView1);
        String sFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meek/"+filepoint+".jpg";
        File f = new File(sFolder);
        try
        {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            rdp.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException e) {
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.defaultdp);
            rdp.setImageBitmap(icon);
            e.printStackTrace();
        }
        MarkerOptions options = new MarkerOptions().title(uid).snippet("1").position(latLng).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), mrker)));

        if(my_marker.marker!=null)
            my_marker.marker.remove();
        my_marker.marker=mMap.addMarker(options);
    }

    public Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((android.app.Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
    void showProfileBottomSheet(String uid)
    {
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
        /////the details and data setup..
    }
    void setActivityMarkers(String uid)
    {
        ///arraylist of the activity markers with location
        //plotting those markers
        //
    }
    void removeOtherPPLMarkers(String uid)
    {
        //  already have a arraylist of ppl with the location
        //  hide the markers except the uid
    }

    ///////////////location marker
    void locationListenSet() {
        initializeLocationManager();
        MapsFragment.LocationListener[] mLocationListeners = new MapsFragment.LocationListener[]{
                new MapsFragment.LocationListener(LocationManager.GPS_PROVIDER),
                new MapsFragment.LocationListener(LocationManager.NETWORK_PROVIDER)
        };
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
    public class LocationListener implements android.location.LocationListener {
        public Location mLastLocation;
        int i = 0;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);

        }

        @Override
        public void onLocationChanged(Location location) {
            cur_location = new LatLng(location.getLatitude(), location.getLongitude());
            if (mMap != null)
            {
                setMarker(cur_location,"0",".Displaypic/pic");
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }


    }



}
