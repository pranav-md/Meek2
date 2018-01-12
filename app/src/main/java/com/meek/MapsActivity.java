package com.meek;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.soundcloud.android.crop.Crop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import javax.microedition.khronos.opengles.GL;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_LONG;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,AdapterView.OnItemSelectedListener, AdapterView.OnItemLongClickListener {

    GoogleMap mMap;
    MapStyleOptions style;
    String date,time,uid;
    ProfileSet profileSet;
    FragmentManager fragmentManager;
    boolean visible=false;
    boolean bs_prof=true;
    boolean bs_on_off=false;
    boolean prob_vis=true;
    BottomSheetBehavior<View> mBottomSheetBehavior1;
    String current_gp="-1";
    FloatingActionButton fab_btn;
    ArrayList<Marker> addedMarkers=new ArrayList<Marker>();
    ArrayList<String> enteredPersons= new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SharedPreferences pref =
                getSharedPreferences("UserDetails", MODE_PRIVATE);
        uid=pref.getString("uid", "");
        Toast.makeText(MapsActivity.this,"uid is: "+uid,Toast.LENGTH_LONG).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        startService(new Intent(getBaseContext(), LocationService.class));
        fab_btn = (FloatingActionButton) findViewById(R.id.gps_home);
        mapFragment.getMapAsync(this);
        try {
            styleMap();
        } catch (ParseException e) {
            Toast.makeText(MapsActivity.this, "something wrong", LENGTH_LONG);
            e.printStackTrace();
        }
        groupSpinnerSetter();
        myLocationUpdate();
    }
    private void dataUpdater()
    {
        final String[] meekusers = new String[1];
        Log.d(TAG, "dataupdater"+uid);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref=ref.child("Users").child(uid).child("received_requests");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {

                requestUpdater(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                requestUpdater(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference mref = database.getReference();
        mref=mref.child("Users");
        mref.child(uid).child("meeked_users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d(TAG, "num children="+dataSnapshot.getChildrenCount());
                meekusers[0] = (String) dataSnapshot.getValue();
                meekUpdater(dataSnapshot);
                SharedPreferences pref = getApplicationContext().getSharedPreferences("Full Meek Contacts", MODE_PRIVATE);
                String con_all_meek=pref.getString("uids","");
                SharedPreferences userpref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                SharedPreferences.Editor non_meek=userpref.edit();
                non_meek.putString("Non_meeked_users",":");
                int num_all_meek=0;
                for( int i=0; i<con_all_meek.length(); i++ ) {
                    if (con_all_meek.charAt(i) == ':') {
                        num_all_meek++;
                    }
                }
                --num_all_meek;

        int num_meek=0;
        for( int i=0; i<meekusers[0].length(); i++ ) {
            if (meekusers[0].charAt(i) == ':') {
                num_meek++;
            }
        }
        --num_meek;
                Log.d(TAG, "num meeek="+num_meek);
                int num_non_meek=0;
                for(int i=0;i<num_all_meek;++i)
                {
                    con_all_meek=con_all_meek.substring(1);
                    int pos=con_all_meek.indexOf(':');
                    String m_all_uid=con_all_meek.substring(0,pos);
                    con_all_meek=con_all_meek.substring(pos);
                    if(!meekusers[0].contains(m_all_uid))
                    {
                        num_non_meek++;
                        non_meek.putString("Non_meeked_users",userpref.getString("Non_meeked_users",":")+m_all_uid+":");
                        non_meek.commit();
                    }
                    non_meek.putInt("Num_non_meek",num_non_meek);
                    non_meek.commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    void myLocationUpdate()
    {
        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
        db_ref=db_ref.child("Users").child(uid);
        db_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences mypref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                SharedPreferences.Editor editor=mypref.edit();
                editor.putString("lat", dataSnapshot.child("lat").getValue().toString());
                editor.putString("lng",dataSnapshot.child("lng").getValue().toString());
                editor.putString("acc",dataSnapshot.child("acc").getValue().toString());
                editor.putString("date_time", dataSnapshot.child("date_time").getValue().toString());
                editor.putString("date_time", dataSnapshot.child("Address").getValue().toString());
                editor.commit();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void meekUpdater(DataSnapshot ds)
    {
        String meek_uid= (String) ds.getValue();
        SharedPreferences meeked = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        SharedPreferences.Editor meekpref=meeked.edit();
        int numMeek = 0;
        for( int i=0; i<meek_uid.length(); i++ ) {
            if( meek_uid.charAt(i) == ':' ) {
                numMeek++;
            }
        }
        numMeek--;
        Log.d(TAG, meek_uid+" num meeek   "+numMeek);

        meekpref.putString("Meek_Friends", meek_uid);
        meekpref.putInt("Meek_number", numMeek);
        meekpref.commit();
        final int num_meek=numMeek;
        int j = 0;
        for (int i=0;i<numMeek;++i)
        {
            meek_uid=meek_uid.substring(1);
            int pos=meek_uid.indexOf(':');
            String m_uid=meek_uid.substring(0,pos);
            meek_uid=meek_uid.substring(pos);
            DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
            db_ref=db_ref.child("Users").child(m_uid);
            db_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SharedPreferences meeked = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                    SharedPreferences contacts = getSharedPreferences("Phone Contacts", MODE_PRIVATE);
                    SharedPreferences.Editor meekdpref=meeked.edit();
                    Log.d(TAG, "the latitude of "+dataSnapshot.child("User_no").getValue().toString()+" is "+dataSnapshot.child("lat").getValue().toString());

                    meekdpref.putString("Meeked_user_name"+dataSnapshot.child("User_no").getValue(),contacts.getString((String) dataSnapshot.child("Phone no").getValue(),""));
                    meekdpref.putString("Meeked_user_lat"+dataSnapshot.child("User_no").getValue(),""+dataSnapshot.child("lat").getValue().toString());
                    meekdpref.putString("Meeked_user_lng"+dataSnapshot.child("User_no").getValue(),""+dataSnapshot.child("lng").getValue().toString());
                    meekdpref.putString("Meeked_user_acc"+dataSnapshot.child("User_no").getValue(),""+dataSnapshot.child("acc").getValue().toString());
                    meekdpref.putString("Meeked_user_activity"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("activity").getValue(String.class));
                    meekdpref.putString("Meeked_user_dpno"+dataSnapshot.child("User_no").getValue(),""+dataSnapshot.child("dpno").getValue().toString());
                    meekdpref.putString("Meeked_user_address"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("address").getValue(String.class));
                    meekdpref.putString("Meeked_user_date_time"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("date_time").getValue(String.class));
                   // meekdpref.putString("Meeked_user_date"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("date").getValue(String.class));
                    meekdpref.putString("Meeked_user_address"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("Address").getValue(String.class));
                    meekdpref.commit();
                    markerSetter(dataSnapshot.child("User_no").getValue().toString());
                }

             /*   @Override
                public void onChildChanged(DataSnapshot dataSnapshot) {
                    SharedPreferences req = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                    SharedPreferences contacts = getSharedPreferences("Phone Contacts", MODE_PRIVATE);
                    SharedPreferences.Editor reqpref=req.edit();
                    reqpref.putString("Request_name"+dataSnapshot.child("User_no").getValue(),contacts.getString(dataSnapshot.child("Phone_no").getValue(String.class),""));
                    reqpref.putString("Request_phone_no"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("Phone_no").getValue(String.class));
                    reqpref.putString("Request_dpno"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("dpno").getValue(String.class));
                    reqpref.commit();
                }*/

               @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        ///update group marker
    }
    void requestUpdater(DataSnapshot ds)
    {
        String request_uid= (String) ds.getValue();
        SharedPreferences req = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        SharedPreferences.Editor reqpref=req.edit();
        reqpref.putString("Requests", request_uid);
        reqpref.commit();
        int numRequest = 0;
        for( int i=0; i<request_uid.length(); i++ ) {
            if( request_uid.charAt(i) == '$' ) {
                numRequest++;
            }
        }
        reqpref.putInt("Request_number",numRequest);

        for (int i=0;i<numRequest;++i)
        {
            request_uid=request_uid.substring(1);
            int pos=request_uid.indexOf(':');
            String r_uid=request_uid.substring(0,pos);
            request_uid=request_uid.substring(pos);
            DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
            db_ref=db_ref.child("Users").child(r_uid);
            db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SharedPreferences req = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                    SharedPreferences contacts = getSharedPreferences("Phone Contacts", MODE_PRIVATE);
                    SharedPreferences.Editor reqpref=req.edit();
                    reqpref.putString("Request_name"+dataSnapshot.child("User_no").getValue(),contacts.getString(dataSnapshot.child("Phone_no").getValue(String.class),""));
                    reqpref.putString("Request_phone_no"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("Phone_no").getValue(String.class));
                    reqpref.putString("Request_dpno"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("dpno").getValue(String.class));
                    reqpref.commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }
    private void styleMap() throws ParseException {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        date = formattedDate.substring(0, 10);
        time = formattedDate.substring(11);
        Date now = c.getTime();
        String morn, aftern, even;
        morn = date + " 06:00:00";
        aftern = date + " 12:00:00";
        even = date + " 18:30:00";
        Date mrn = df.parse(morn);
        Date aft = df.parse(aftern);
        Date evn = df.parse(even);
        if (now.compareTo(mrn) > 0 && aft.compareTo(now) > 0) {
            style = new MapStyleOptions("[]");
        } else if (now.compareTo(aft) > 0 && evn.compareTo(now) > 0) {
            style = new MapStyleOptions("[\n" +
                    "  {\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#ebe3cd\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#523735\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"elementType\": \"labels.text.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#f5f1e6\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"administrative\",\n" +
                    "    \"elementType\": \"geometry.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#c9b2a6\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"administrative.land_parcel\",\n" +
                    "    \"elementType\": \"geometry.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#dcd2be\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"administrative.land_parcel\",\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#ae9e90\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"landscape.natural\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#dfd2ae\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"poi\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#dfd2ae\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"poi\",\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#93817c\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"poi.park\",\n" +
                    "    \"elementType\": \"geometry.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#a5b076\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"poi.park\",\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#447530\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#f5f1e6\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road.arterial\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#fdfcf8\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road.highway\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#f8c967\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road.highway\",\n" +
                    "    \"elementType\": \"geometry.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#e9bc62\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road.highway.controlled_access\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#e98d58\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road.highway.controlled_access\",\n" +
                    "    \"elementType\": \"geometry.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#db8555\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road.local\",\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#806b63\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"transit.line\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#dfd2ae\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"transit.line\",\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#8f7d77\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"transit.line\",\n" +
                    "    \"elementType\": \"labels.text.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#ebe3cd\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"transit.station\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#dfd2ae\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"water\",\n" +
                    "    \"elementType\": \"geometry.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#b9d3c2\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"water\",\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#92998d\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "]");
        } else {
            style = new MapStyleOptions("[\n" +
                    "  {\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#1d2c4d\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#8ec3b9\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"elementType\": \"labels.text.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#1a3646\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"administrative.country\",\n" +
                    "    \"elementType\": \"geometry.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#4b6878\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"administrative.land_parcel\",\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#64779e\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"administrative.province\",\n" +
                    "    \"elementType\": \"geometry.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#4b6878\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"landscape.man_made\",\n" +
                    "    \"elementType\": \"geometry.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#334e87\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"landscape.natural\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#023e58\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"poi\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#283d6a\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"poi\",\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#6f9ba5\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"poi\",\n" +
                    "    \"elementType\": \"labels.text.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#1d2c4d\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"poi.park\",\n" +
                    "    \"elementType\": \"geometry.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#023e58\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"poi.park\",\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#3C7680\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#304a7d\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road\",\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#98a5be\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road\",\n" +
                    "    \"elementType\": \"labels.text.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#1d2c4d\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road.highway\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#2c6675\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road.highway\",\n" +
                    "    \"elementType\": \"geometry.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#255763\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road.highway\",\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#b0d5ce\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"road.highway\",\n" +
                    "    \"elementType\": \"labels.text.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#023e58\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"transit\",\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#98a5be\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"transit\",\n" +
                    "    \"elementType\": \"labels.text.stroke\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#1d2c4d\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"transit.line\",\n" +
                    "    \"elementType\": \"geometry.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#283d6a\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"transit.station\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#3a4762\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"water\",\n" +
                    "    \"elementType\": \"geometry\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#0e1626\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"featureType\": \"water\",\n" +
                    "    \"elementType\": \"labels.text.fill\",\n" +
                    "    \"stylers\": [\n" +
                    "      {\n" +
                    "        \"color\": \"#4e6d70\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "]");
        }
        if (mMap != null)
            mMap.setMapStyle(style);

}

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(bs_on_off==true)
                {
                    ///animation thing

                }
                else if(bs_on_off==false)
                {

                    ///gps camera move
                    myLocationUpdate();
                    SharedPreferences pref = getSharedPreferences("UserDetails", MODE_PRIVATE);
                    String lat=pref.getString("lat","");
                    String lng=pref.getString("lng","");
                    Toast.makeText(MapsActivity.this,"pressed gps  "+lat,LENGTH_LONG).show();
                    LatLng ll=new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                    CameraUpdate location=CameraUpdateFactory.newLatLngZoom(ll,15);
                    mMap.animateCamera(location);

                }
            }
        });
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @SuppressLint("ResourceType")
            @Override
            public void onMapLongClick(LatLng arg0) {
                //markerSetter();

                if (!visible) {
                    SharedPreferences pref = getSharedPreferences("UserDetails", MODE_PRIVATE);
                    uid = pref.getString("uid", "");
                    visible = true;
                    if (prob_vis) {
                        fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack("Profile frag").setCustomAnimations(R.animator.leftright, R.animator.rightleft);

                        profileSet = new ProfileSet();  //your fragment
                        // work here to add, remove, etc
                        fragmentTransaction.add(R.id.frag_layout, profileSet);
                        fragmentTransaction.commit();
                        profileSet.setActivity(uid);

                    } else {
                        fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack("Profile frag").setCustomAnimations(R.animator.leftright, R.animator.rightleft);

                        ContactsBox contactsBox = new ContactsBox(MapsActivity.this);  //your fragment

                        // work here to add, remove, etc
                        fragmentTransaction.add(R.id.frag_layout, contactsBox);
                        fragmentTransaction.commit();
                    }
                    View view = findViewById(R.id.buttonview);
                    view.setVisibility(View.VISIBLE);
                    Animation anmtn = AnimationUtils.loadAnimation(getApplicationContext(), R.animator.rightleft_button);
                    view.setAnimation(anmtn);
                    FloatingActionButton flip_change = (FloatingActionButton) findViewById(R.id.send_fab);

                    flip_change.setOnClickListener(new View.OnClickListener() {
                                                       public void onClick(View v) {
                                                           prob_vis = !prob_vis;
                                                           if (!prob_vis) {
                                                               getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.flip_right_in, R.animator.flip_right_out, R.animator.flip_left_in, R.animator.flip_left_out)
                                                                       .replace(R.id.frag_layout, new ContactsBox(MapsActivity.this))
                                                                       .commit();
                                                           } else {
                                                               getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.flip_right_in, R.animator.flip_right_out, R.animator.flip_left_in, R.animator.flip_left_out)
                                                                       .replace(R.id.frag_layout, profileSet)
                                                                       .commit();


                                                           }
                                                       }
                                                   }
                    );
                    FloatingActionButton dp_change = (FloatingActionButton) findViewById(R.id.fabb2);
                    dp_change.setOnClickListener(new View.OnClickListener() {
                                                     public void onClick(View v) {
                                                         Crop.pickImage(MapsActivity.this);
                                                     }
                                                 }
                    );
                    FloatingActionButton exit_tick = (FloatingActionButton) findViewById(R.id.fabb3);
                    exit_tick.setOnClickListener(new View.OnClickListener() {
                                                     public void onClick(View v) {
                                                         FragmentManager manager = getSupportFragmentManager();
                                                         FragmentTransaction ft = manager.beginTransaction();
                                                         ft.setCustomAnimations(R.animator.rightleft, R.animator.leftright);
                                                         ft.remove(profileSet);
                                                         ft.commit();
                                                         View view = findViewById(R.id.buttonview);
                                                         view.setVisibility(View.INVISIBLE);
                                                         Animation anmtn = AnimationUtils.loadAnimation(getApplicationContext(), R.animator.leftright_button);
                                                         view.setAnimation(anmtn);
                                                         visible = false;
                                                         profileSet.saveActivity(uid);


                                                     }
                                                 }
                    );
                    Toast.makeText(MapsActivity.this, "LOng pressed", LENGTH_LONG).show();
                }
            }
        });
        // Add a marker in Sydney and move the camera
       // mClusterManager = new ClusterManager<Person>(this, mMap);
        dataUpdater();
        final View bottomSheet = findViewById(R.id.btm_sheet);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(0);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior1.setPeekHeight(0);
                    Drawable drawable = getResources().getDrawable(R.drawable.gps_home);
                    fab_btn.setImageDrawable(drawable);
                    bs_on_off=false;
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                if(bs_prof==true)
                {
                    showProfileBottomSheet(marker);
                    Drawable drawable = getResources().getDrawable(R.drawable.quotes);
                    fab_btn.setImageDrawable(drawable);
                    bs_on_off=true;
                }

            return true;
            }
        });
    }
    ///////////dp change codes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Crop.REQUEST_PICK&&resultCode==RESULT_OK&&data!=null)
        {
            beginCrop(data.getData());
        }
        else if(requestCode==Crop.REQUEST_CROP)
        {
            try {
                handleCrop(resultCode,data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    private void beginCrop(Uri source)
    {
        Uri dest=Uri.fromFile(new File(getCacheDir(),"Cropped"));
        Crop.of(source,dest).asSquare().start(this);
    }
    private void handleCrop(int resultCode,Intent result) throws IOException {
        if(resultCode==RESULT_OK)
        {
            final Uri uri =Crop.getOutput(result);
            File myFile = new File(uri.getPath());
            Uri selectedImage=getImageContentUri(getApplicationContext(),myFile);
            String path = getPath(selectedImage);



            ///////Uploading code
            final FirebaseStorage  storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            final NotificationManager mNotifyManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setContentTitle("DP uploading")
                    .setContentText("Uploading in progress").setSmallIcon(R.drawable.thelogo);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy kk:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date=simpleDateFormat.format(new Date())+"";
            String upld_filename=date.substring(0,2)+date.substring(11,13)+date.substring(14,16)+date.substring(17,19);
            upld_filename=upld_filename+(new Random().nextInt(899)+100);
            StorageReference ref = storageReference.child("Users DP/"+upld_filename+".jpg");
            final String finalUpld_filename = upld_filename;
            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference userRef = database.getReference("Users");
                            userRef.child(uid).child("dp_code").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    String dp_code=dataSnapshot.getValue().toString();
                                    StorageReference storageRef = storage.getReference();
                                    Toast.makeText(MapsActivity.this,"dpcode= "+dp_code,Toast.LENGTH_LONG).show();
                                    StorageReference desertRef = storageRef.child("Users DP/"+dp_code+".jpg");
                                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // File deleted successfully
                                            ////saving dpcode
                                            userRef.child(uid).child("dp_code").setValue(finalUpld_filename);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Uh-oh, an error occurred!
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            ///setting notification
                            mBuilder.setContentText("DP updating Success ! ")
                                    // Removes the progress bar
                                    .setProgress(0,0,false);
                            mNotifyManager.notify(1, mBuilder.build());


                            ////saving in memory
                            String sFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meek/DisplayPic";
                            String localFilename = sFolder  + "/currentDP.jpg";
                            new File(sFolder ).mkdirs();
                            byte[] imageData=new byte[1024];
                            ///deleting if already another exists
                            File fdelete = new File(localFilename);
                            if (fdelete.exists()) {
                                if (fdelete.delete()) {
                                    System.out.println("file Deleted :");
                                } else {
                                    System.out.println("file not Deleted :");
                                }
                            }
                            try {
                                File img = new File(localFilename);
                            OutputStream out = new BufferedOutputStream(new FileOutputStream(img));
                            InputStream in = getContentResolver().openInputStream(uri);
                            int bytesread;
                                while((bytesread=in.read(imageData))>0)
                                {
                                    out.write(Arrays.copyOfRange(imageData,0,Math.max(0,bytesread)));
                                }
                                in.close();
                                out.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mBuilder.setContentText("DP updating failed ! ")
                                    // Removes the progress bar
                                    .setProgress(0,0,false);
                            mNotifyManager.notify(1, mBuilder.build());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int incr = (int) (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                                                .getTotalByteCount());
                            mBuilder.setProgress(100, incr, false);
                            // Displays the progress bar for the first time.
                            mNotifyManager.notify(1, mBuilder.build());                        }
                    });
            /////
            String sFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meek/DisplayPic";
            String localFilename = sFolder  + "/currentDP.jpg";
            profileSet.setDP(localFilename);
        }
        else if(resultCode==Crop.RESULT_ERROR)
            Toast.makeText(this,Crop.getError(result).getMessage(),Toast.LENGTH_LONG).show();

    }
    public String getPath(Uri uri) {
        Cursor cursor1 = getContentResolver().query(uri, null, null, null, null);
        cursor1.moveToFirst();
        String document_id = cursor1.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor1.close();

        cursor1 = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor1.moveToFirst();
        String path = cursor1.getString(cursor1.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor1.close();

        return path;
    }
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }}

    /////////////////////

    void groupSpinnerSetter()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users");
        userRef.child(uid).child("Groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                groupDataSetter(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                groupDataSetter(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        int[] gp_dp={R.drawable.defaultdp,R.drawable.the_user};
        String[] gp_name={"Default","User"};

    }
    String groupDataSetter(DataSnapshot ds)
    {
        String gp_ids=ds.getValue().toString();
        String gp_ids_temp=gp_ids;
        SharedPreferences grp_pref = getSharedPreferences("Groups", MODE_PRIVATE);
        final SharedPreferences.Editor editor = grp_pref.edit();

        int num_groups=0;
        final AdaptHelper[] gps=new AdaptHelper[50];
        if(gp_ids.length()>2)
        {
            editor.putString("Groups_id",gp_ids);
            editor.commit();
            for (int i = 0; i < gp_ids.length(); i++) {
                if (gp_ids.charAt(i) == ':')
                {
                    num_groups++;
                }
            }
            --num_groups;
            editor.putInt("Num_groups",num_groups);
            editor.commit();
            for(int i=0;i<num_groups;++i)
            {
                gp_ids=gp_ids.substring(1);
                int pos=gp_ids.indexOf(':');
                String gp_id=gp_ids.substring(0,pos);
                gp_ids=gp_ids.substring(pos);
                final int position=i;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("Groups");
                userRef.child(gp_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String gp_no=dataSnapshot.child("Group_no").getValue().toString();
                        String gp_name=dataSnapshot.child("Group_name").getValue().toString();
                        String gp_members=dataSnapshot.child("Group_members").getValue().toString();
                        String gp_dpcode=dataSnapshot.child("Group_dpcode").getValue().toString();
                        editor.putString("Group_name"+gp_no,gp_name);
                        editor.putString("Group_members"+gp_no,gp_members);
                        editor.putString("Group_dpcode"+gp_no,gp_dpcode);
                        int num_gp_mems=0;
                        for (int i = 0; i < gp_members.length(); i++) {
                            if (gp_members.charAt(i) == ':')
                            {
                                num_gp_mems++;
                            }
                        }
                        --num_gp_mems;
                        editor.putInt("Group_num_mems"+gp_no,num_gp_mems);
                        editor.commit();
                        gps[position].uid=gp_no;
                        gps[position].name=gp_name;
                        gps[position].dpno=gp_dpcode;

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }
        Spinner spin = (Spinner) findViewById(R.id.spinner2);
        spin.setOnItemSelectedListener( MapsActivity.this);
        spin.setOnItemLongClickListener(MapsActivity.this);
        GroupSpinnerAdapter customAdapter=new GroupSpinnerAdapter(getApplicationContext(),gps);
        spin.setAdapter(customAdapter);
        return gp_ids;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String gp_id= (String) view.getTag();
        SharedPreferences grp_pref=getSharedPreferences("Groups",MODE_PRIVATE);
        String gp_members=grp_pref.getString("Group_members"+gp_id,"");
        int num_membrs=grp_pref.getInt("Num_groups",0);

        for (int i=0;i<num_membrs;++i)
        {
            gp_members = gp_members.substring(1);
            int pos = gp_members.indexOf(':');
            String mem_uid = gp_members.substring(0, pos);
            gp_members = gp_members.substring(pos);
            markerSetter(mem_uid);
        }
        /* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(50);
                String i= marker.getTitle();
                String activity=marker.getSnippet();
                final Typeface bold_font = Typeface.createFromAsset(getAssets(), "Prime Regular.otf");
                final Typeface light_font = Typeface.createFromAsset(getAssets(), "Prime Light.otf");
                View bottomSheet = findViewById(R.id.bottom_sheet);
                TextView btname=(TextView)findViewById(R.id.bottom_name);
                btname.setTypeface(bold_font);
                TextView btactivity=(TextView)findViewById(R.id.bottom_activity);
                btactivity.setTypeface(light_font);
                TextView btdate=(TextView)findViewById(R.id.bottom_date);
                btdate.setTypeface(light_font);
                TextView bttime=(TextView)findViewById(R.id.bottom_time);
                bttime.setTypeface(light_font);
                TextView btacc=(TextView)findViewById(R.id.textView2);
                bttime.setTypeface(light_font);
                SharedPreferences userdata = getSharedPreferences("User data", MODE_PRIVATE);
                CircleImageView imguser=(CircleImageView)findViewById(R.id.imageView2);
                String sFolderuser = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Meek/Friends/"+userdata.getString("dpcode"+i,"")+".jpg";
                File fuser=new File(sFolderuser);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(fuser));
                    imguser.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.defaultdp);
                    imguser.setImageBitmap(icon);
                    e.printStackTrace();
                }
                BottomSheetBehavior mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                btname.setText(userdata.getString("name"+i,""));
                btactivity.setText(activity);
                btacc.setText("Accuracy: "+userdata.getString("acc"+i,"")+"m ");
                btdate.setText(userdata.getString("date"+i,""));
                bttime.setText(userdata.getString("time"+i,""));
                if(i.equals("ME")&&activity.equals(""))
                {
                    CircleImageView img=(CircleImageView)findViewById(R.id.imageView2);
                    String sFolder= Environment.getExternalStorageDirectory().getAbsolutePath()+"/Meek/DisplayPic/currentDP.jpg";
                    File f=new File(sFolder);
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                        img.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.defaultdp);
                        img.setImageBitmap(icon);
                        e.printStackTrace();
                    }
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                    btname.setText("IT'S ME");
                    btactivity.setText(userdata.getString("activity",""));
                    btdate.setText(userdata.getString("date",""));
                    bttime.setText(userdata.getString("time",""));
                    btacc.setText("Accuracy: "+userdata.getString("acc","")+" m");
                }
                return true;
            }

            //   mapFragment.getMapAsync(this);
        });*/

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
    void markerSetter(final String p_uid)
    {
        String gp_members;
        int gp_mem_num;
        final SharedPreferences userdetails=getSharedPreferences("UserDetails",MODE_PRIVATE);
        final SharedPreferences.Editor urisetter=userdetails.edit();
        if(!(current_gp.equals("-1")||userdetails.getString("Group_members"+current_gp,"").contains(uid)))
        {
            return;
        }
        if(mMap==null)
            return;
        final Uri[] dpuri = new Uri[1];
       // mMap.clear();
/*        LatLng myplace = new LatLng(Double.parseDouble(userdetails.getString("lat","")),Double.parseDouble(userdetails.getString("lng","")));
        View mrkeruser = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);
        CircleImageView fdpuser = (CircleImageView)mrkeruser.findViewById(R.id.imageView1);
        String sFolderuser = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Meek/DisplayPic/currentDP.jpg";
        File fuser=new File(sFolderuser);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(fuser));
            fdpuser.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.defaultdp);
            fdpuser.setImageBitmap(icon);
            e.printStackTrace();
        }
        MarkerOptions myops=new MarkerOptions().title("ME").position(myplace).snippet("").icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, mrkeruser)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myplace,10));
        mMap.addMarker(myops);*/

                Log.d(TAG, "the marker user uid"+p_uid);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final LatLng ll = new LatLng(Double.parseDouble(userdetails.getString("Meeked_user_lat"+p_uid, "")), Double.parseDouble(userdetails.getString("Meeked_user_lng"+p_uid, "")));

        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageReference.child("Users DP/"+userdetails.getString("Meeked_user_dpno"+p_uid, "")+".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    View mrker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);
                    final CircleImageView rdp=(CircleImageView) mrker.findViewById(R.id.imageView1);
                    rdp.setImageBitmap(bitmap);
                    MarkerOptions options=new MarkerOptions().title(p_uid).snippet(userdetails.getString("Meeked_user_activity"+p_uid, "")).position(ll).snippet("").icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
                    boolean enterflg=false;
                    ArrayList<Marker> tmp=new ArrayList<Marker>();
                    tmp.addAll(addedMarkers);
                    Marker remkr=null;
                    for(Marker mkr:tmp)
                    {
                        if(mkr.getTitle().equals(p_uid))
                        {
                            Log.d(TAG,"the location change"+p_uid);
                            enterflg=true;
                            // mkr.remove();
                            remkr=mkr;
                            mkr.setVisible(false);
                            Marker m=mMap.addMarker(options);
                            addedMarkers.remove(mkr);
                            addedMarkers.add(m);
                        }
                    }
                    if(enterflg==false)
                    {
                        Marker m= mMap.addMarker(options);
                        addedMarkers.add(m);
                    }
                    else
                        remkr.remove();
                    Log.d(TAG, p_uid+" lat="+Double.parseDouble(userdetails.getString("Meeked_user_lat"+p_uid, ""))+" lng="+Double.parseDouble(userdetails.getString("Meeked_user_lng"+p_uid, "")));


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e ) {}
        //Glide.with(MapsActivity.this).
                storageReference.child("Users DP/"+userdetails.getString("Meeked_user_dpno"+p_uid, "")+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        Log.d(TAG, "the image uri"+uri);
                        urisetter.putString("Meeked_user_uri"+p_uid,uri.toString());
                        urisetter.commit();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception exception)
                 {
                     Log.d(TAG, "Something wrong with uri thing" );

                 }
                });





        //     CameraUpdate update=CameraUpdateFactory.newLatLng(ll);
        //     mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
        //      Toast.makeText(this, "goo daa  "+nums, Toast.LENGTH_LONG).show();
//
    }

    // Convert a view to bitmap
    public Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
    void showProfileBottomSheet(Marker marker) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(50);
        String i = marker.getTitle();
        String activity = marker.getSnippet();
        View bottomSheet = findViewById(R.id.btm_sheet);
        TextView btname = (TextView) bottomSheet.findViewById(R.id.bs_name);
        //btname.setTypeface(bold_font);
        TextView btactivity = (TextView) bottomSheet.findViewById(R.id.bs_activity);
        //btactivity.setTypeface(light_font);
        TextView btdate = (TextView) bottomSheet.findViewById(R.id.bs_time_date);
        //btdate.setTypeface(light_font);
        TextView bttime = (TextView) bottomSheet.findViewById(R.id.bs_address);
        //bttime.setTypeface(light_font);
        TextView btacc = (TextView) bottomSheet.findViewById(R.id.textView2);
        //bttime.setTypeface(light_font);
        SharedPreferences userdata = getSharedPreferences("UserDetails", MODE_PRIVATE);
        CircleImageView imguser = (CircleImageView) bottomSheet.findViewById(R.id.imageView1);
        Glide.with(this).load(userdata.getString("Meeked_user_uri"+i,"")).override(50,50).into(imguser);
        String sFolderuser = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meek/Friends/" + userdata.getString("dpcode" + i, "") + ".jpg";
      /*  File fuser = new File(sFolderuser);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(fuser));
            imguser.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.defaultdp);
            imguser.setImageBitmap(icon);
            e.printStackTrace();
        }*/
        BottomSheetBehavior mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
        btname.setText(userdata.getString("Meeked_user_name" + i, ""));
        btactivity.setText(userdata.getString("Meeked_user_activity" + i, ""));
      /*  Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(Double.parseDouble(userdata.getString("Meeked_user_lat" + i, "")), Double.parseDouble(userdata.getString("Meeked_user_lng" + i, ""))))
                .radius(Float.parseFloat(userdata.getString("Meeked_user_acc" + i, "")))
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));*/
        btdate.setText(userdata.getString("Meeked_user_date_time" + i, ""));
        if (i.equals("ME") && activity.equals("")) {
            CircleImageView img = (CircleImageView) bottomSheet.findViewById(R.id.imageView1);
            String sFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meek/DisplayPic/currentDP.jpg";
            File f = new File(sFolder);
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                img.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.defaultdp);
                img.setImageBitmap(icon);
                e.printStackTrace();
            }
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
            btname.setText("IT'S ME");
            btactivity.setText(userdata.getString("activity", ""));
            btdate.setText(userdata.getString("date", ""));
            bttime.setText(userdata.getString("time", ""));
            btacc.setText("Accuracy: " + userdata.getString("acc", "") + " m");

        }
    }
}


