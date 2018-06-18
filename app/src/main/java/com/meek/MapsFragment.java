package com.meek;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.*;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

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
    ArrayList<Marker> ppl_marker;
    ArrayList<Marker> act_marker;
    ArrayList<MapPeople> mapPeople;
    BottomSheetBehavior<View> mBottomSheetBehavior1;
    Marker Activities;
    ProfilePageAdapter ppl_page_adapter;
    LatLng cur_location;
    String uid,current_ppl=":";
    EasyFlipView flip_bs;
    View bottomSheet,view;
    ViewPager profile_pg,Activities_pg;
    boolean bs,bs_act,bs_prof;
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
        bottomSheetSetup();
        profile_pg=(ViewPager)view.findViewById(R.id.profile_fp_view).findViewById(R.id.bs_viewpgr);
        Activities_pg=(ViewPager)view.findViewById(R.id.activity_fp_view).findViewById(R.id.bs_viewpgr);
        flip_bs=(EasyFlipView)view.findViewById(R.id.prof_act_flipper);
        flip_bs.setFlipDuration(200);
        SharedPreferences pref = getContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        uid=pref.getString("uid", "");
        ppl_marker=new ArrayList<Marker>();
        mapPeople=new ArrayList<MapPeople>();
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        bs_prof=false;
        bs=false;
        bs_act=false;
        mapFragment.getMapAsync( this);
        return view;

    }
    private void bottomSheetSetup() {

        bottomSheet = view.findViewById(R.id.btm_sheet);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(0);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED)
                {
                    // mBottomSheetBehavior1.setPeekHeight(bottomSheet.getHeight());
                    bs_prof=true;
                    mBottomSheetBehavior1.setPeekHeight(0);
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    ///all the activity markers should get dissapperared
                    //all the hidden markers of other users gets un hidden
                    bs_prof = false;
                    bs=false;
                    bs_act=false;
                    visibleMarkers();
                    removeActivitiesMarkers();

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
            getMeekLocPpl();
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker)
                {
                    if(marker.getSnippet().equals("1"))
                    {
                        if(bs==false)
                        {
                            bs=true;
                            bs_act=false;
                            bs_prof = true;
                            showProfileBottomSheet(marker.getTitle(),(int)marker.getTag());
                            removeOtherPPLMarkers((int)marker.getTag());
                            getActData(marker.getTitle(),(int)marker.getTag());
                        }
                        else if(bs_act==true)
                        {
                            bs_prof=true;
                            bs_act=false;
                            Activities_pg.setCurrentItem((int)marker.getTag());
                            flip_bs.flipTheView();
                        }
                        else if(bs_prof==true)
                        {
                            profile_pg.setCurrentItem((int)marker.getTag());
                         ////change position

                        }
                    }
                    else if(marker.getSnippet().equals("2"))
                    {
                        if(bs_prof==true)
                        {
                            bs_act=true;
                            bs_prof = false;
                            flip_bs.flipTheView();
                            Activities_pg.setCurrentItem((int)marker.getTag());
                        }
                        else
                        {
                            Activities_pg.setCurrentItem((int)marker.getTag());
                        }
                    }


                    return true;
                }
            });
    }

    void setUserMarker()
    {
        Log.e("HAHAH", "setusermarker ");
        current_ppl+=uid+":";
        Realm.init(getContext());
        final Realm myRealm= Realm.getDefaultInstance();
        final MyDetails myDetails=myRealm.where(MyDetails.class).findFirst();
        MapPeople newme=new MapPeople();
        newme.uid=uid;
        if(myDetails!=null)
        {
            LatLng ll = new LatLng(Double.parseDouble(myDetails.getLat()), Double.parseDouble(myDetails.getLng()));
            newme.latLng=ll;
            mapPeople.add(new MapPeople());
            mapPeople.set(0,newme);
            setMarker(ll,0,uid,".Displaypic/pic");
            ppl_page_adapter=new ProfilePageAdapter(getChildFragmentManager());
            ppl_page_adapter.setData(mapPeople);
            profile_pg.setAdapter(ppl_page_adapter);
        }
        else
            Toast.makeText(getContext(),"wait pls",Toast.LENGTH_LONG).show();
    }

    void getActData(String a_uid, final int pos)
    {
        final boolean[] adapt_bit = {false};
        act_marker=new ArrayList<Marker>();
        final ActivitiesPageAdapter activitiesPageAdapter=new ActivitiesPageAdapter(getChildFragmentManager(),mapPeople.get(pos).uid);
        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
        db_ref.child("Activities").child(a_uid).child("mapview").child("loc_friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Activities newone=new Activities();
                    newone.act_id=ds.getKey().toString();
                    newone.latLng=new LatLng(Double.parseDouble(ds.child("lat").getValue().toString()),Double.parseDouble(ds.child("lng").getValue().toString()));
                    newone.color=Integer.parseInt(ds.child("clr").getValue().toString());
                    mapPeople.get(pos).activities.add(newone);
                    setActivitiesMarker(pos,mapPeople.get(pos).activities.size()-1);
                    if(adapt_bit[0] ==false)
                    {
                        adapt_bit[0] =true;
                        activitiesPageAdapter.setData(mapPeople.get(pos).activities);
                        Activities_pg.setAdapter(activitiesPageAdapter);
                    }
                    else
                    {
                        activitiesPageAdapter.setData(mapPeople.get(pos).activities);
                        activitiesPageAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    void setMarker(LatLng latLng,int pos,String uid,String filepoint)
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
        MarkerOptions options = new MarkerOptions().title(mapPeople.get(pos).uid).snippet("1").position(latLng).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), mrker)));

        if(ppl_marker.size()>pos+1)
        {
            Marker mkr=ppl_marker.get(pos);
            mkr.remove();
            mkr=mMap.addMarker(options);
            mkr.setTag(pos);
            ppl_marker.set(pos,mkr);

        }
        else
        {
            Marker mkr=mMap.addMarker(options);
            mkr.setTag(pos);
            ppl_marker.add(mkr);
        }

    }
    void getMeekLocPpl()
    {
        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
        db_ref.child("Users").child(uid).child("meek_loc_ppl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String all_ppl = dataSnapshot.getValue().toString();
                crossChecker(all_ppl);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void crossChecker(String all_ppl)
    {
        final ArrayList<String> al_pl=extractor(all_ppl);
        ArrayList<String> cr_pl=extractor(current_ppl);

        DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();

        for(int i=0;i<al_pl.size();++i)
        {
            if(!current_ppl.contains(al_pl.get(i)))
            {
                final MapPeople newone=new MapPeople();
                newone.uid=al_pl.get(i);
                newone.loc_listener=ppl_ref.child("Users").
                                            child(newone.uid).
                                            child("Details1").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        newone.latLng=new LatLng(Double.parseDouble(dataSnapshot.child("lat").getValue().toString()),Double.parseDouble(dataSnapshot.child("lng").getValue().toString()));
                        newone.color= Integer.parseInt(dataSnapshot.child("clr").getValue().toString());
                        if(!current_ppl.contains(newone.uid))
                        {
                            mapPeople.add(newone);
                            current_ppl += newone.uid + ";";
                            ppl_page_adapter.setData(mapPeople);
                            ppl_page_adapter.notifyDataSetChanged();
                            setMarker(newone.latLng,mapPeople.size()-1,newone.uid,"dp tobe set");

                        }
                        else
                        {
                            for(int i=0;i<mapPeople.size();++i)
                            {
                                if(mapPeople.get(i).uid.equals(newone.uid))
                                {
                                    mapPeople.get(i).latLng=newone.latLng;
                                    mapPeople.get(i).color=newone.color;
                                    setMarker(newone.latLng,i,newone.uid,"dp tobe set");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        ///
        if(cr_pl.size()!=0)
        for(int i=0;i<cr_pl.size();++i)
        {
            if(!all_ppl.contains(cr_pl.get(i)))
            {
                for(int j=0;j<mapPeople.size();++j)
                {
                    if(mapPeople.get(j).equals(cr_pl.get(i)))
                    {
                        mapPeople.remove(j);
                        ppl_marker.get(j).remove();
                        ppl_marker.remove(j);
                        current_ppl.replace(":"+cr_pl.get(i),"");
                        ppl_page_adapter.setData(mapPeople);
                        ppl_page_adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    ArrayList<String> extractor(String all_uid)
    {
        ArrayList<String> uids=new ArrayList<String>() ;
        int numMeek = 0,i;
        for( i=0; i<all_uid.length(); i++ ) {
            if( all_uid.charAt(i) == ':' ) {
                numMeek++;
            }
        }
        numMeek--;
        for (i=0;i<numMeek;++i)
        {
            all_uid=all_uid.substring(1);
            int pos=all_uid.indexOf(':');
            String m_uid=all_uid.substring(0,pos);
            uids.add(m_uid);
            all_uid=all_uid.substring(pos);
        }
        return uids;
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
    void showProfileBottomSheet(String uid,int pos)
    {
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
        profile_pg.setCurrentItem(pos);
    }
    void setActivitiesMarker(int p_pos,int a_pos)
    {
        View mrker = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activty_marker, null);
        MarkerOptions options = new MarkerOptions().title(p_pos+"").snippet("2").position(mapPeople.get(p_pos).activities.get(a_pos).latLng).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), mrker)));
        Marker mkr=mMap.addMarker(options);
        mkr.setTag(a_pos);
        act_marker.add(mkr);
    }

    void removeOtherPPLMarkers(int pos)
    {
        for(Marker mkr:ppl_marker)
        {
            if(ppl_marker.indexOf(mkr)!=pos)
            {
                mkr.setVisible(false);
            }
        }
    }
    void visibleMarkers()
    {
        for(Marker mkr:ppl_marker)
        {
            mkr.setVisible(true);
        }
    }

    void removeActivitiesMarkers()
    {
        for(Marker mkr:act_marker)
        {
            mkr.remove();
        }
        act_marker.clear();
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
        public void onLocationChanged(Location location)
        {
            cur_location = new LatLng(location.getLatitude(), location.getLongitude());
            if (mMap != null)
            {
                mapPeople.get(0).latLng=new LatLng(location.getLatitude(),location.getLongitude());
                setMarker(cur_location,0,uid,".Displaypic/pic");
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

    /////////
    //////view page adapter
    public class ProfilePageAdapter extends FragmentPagerAdapter
    {
        ArrayList<MapPeople> map_ppl;
        public ProfilePageAdapter(ArrayList<MapPeople> map_ppl, FragmentManager fragmentManager)
        {
            super(fragmentManager);
            this.map_ppl=map_ppl;
        }

        public ProfilePageAdapter(FragmentManager childFragmentManager) {
            super(childFragmentManager);
        }
        void setData(ArrayList<MapPeople> map_ppl)
        {
            this.map_ppl=map_ppl;
        }

        @Override
        public Fragment getItem(int position)
        {
            BSProfileFragment profile=new BSProfileFragment(map_ppl.get(position));
            return profile;
        }

        @Override
        public int getCount() {
            return map_ppl.size();
        }
    }

}
