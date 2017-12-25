package com.meek;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.widget.Toast.LENGTH_LONG;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MapStyleOptions style;
    String date,time,uid;
    boolean visible=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
      //  uid=pref.getString("uid", "");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        startService(new Intent(getBaseContext(), LocationService.class));

        mapFragment.getMapAsync(this);
        try {
            styleMap();
        } catch (ParseException e) {
            Toast.makeText(MapsActivity.this, "something wrong", LENGTH_LONG);
            e.printStackTrace();
        }
    }
    private void dataUpdater()
    {
        final String[] meekusers = new String[1];

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref=ref.child("Users").child(uid).child("recieved_requests");
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

        ref=ref.child("Users").child(uid).child("meeked_users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                meekusers[0] = (String) dataSnapshot.getValue();
                meekUpdater(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                meekUpdater(dataSnapshot);

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
        for(int i=0;i<num_all_meek;++i)
        {
            con_all_meek=con_all_meek.substring(1);
            int pos=con_all_meek.indexOf(':');
            String m_all_uid=con_all_meek.substring(0,pos);
            con_all_meek=con_all_meek.substring(pos);
            if(!meekusers[0].contains(m_all_uid))
            {
                non_meek.putString("Non_meeked_users",userpref.getString("Non_meeked_users","")+":");
                non_meek.commit();
            }

        }
    }
    void meekUpdater(DataSnapshot ds)
    {
        String meek_uid= (String) ds.getValue();
        SharedPreferences meeked = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        SharedPreferences.Editor meekpref=meeked.edit();
        meekpref.putString("Requests", meek_uid);
        meekpref.commit();
        int numRequest = 0;
        for( int i=0; i<meek_uid.length(); i++ ) {
            if( meek_uid.charAt(i) == ':' ) {
                numRequest++;
            }
        }
        numRequest--;
        for (int i=0;i<numRequest;++i)
        {
            meek_uid=meek_uid.substring(1);
            int pos=meek_uid.indexOf(':');
            String m_uid=meek_uid.substring(0,pos);
            meek_uid=meek_uid.substring(pos);
            DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
            db_ref=db_ref.child("Users").child(m_uid);
            db_ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    SharedPreferences meeked = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                    SharedPreferences contacts = getSharedPreferences("Phone Contacts", MODE_PRIVATE);
                    SharedPreferences.Editor meekdpref=meeked.edit();

                    meekdpref.putString("Meeked_user_name"+dataSnapshot.child("User_no").getValue(),contacts.getString((String) dataSnapshot.child("Phone no").getValue(),""));
                    meekdpref.putString("Meeked_user_lat"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("lat").getValue(String.class));
                    meekdpref.putString("Meeked_user_lng"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("lng").getValue(String.class));
                    meekdpref.putString("Meeked_user_acc"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("acc").getValue(String.class));
                    meekdpref.putString("Meeked_user_activity"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("activity").getValue(String.class));
                    meekdpref.putString("Meeked_user_dpno"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("dpno").getValue(String.class));
                    meekdpref.putString("Meeked_user_address"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("address").getValue(String.class));
                    meekdpref.putString("Meeked_user_time"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("time").getValue(String.class));
                    meekdpref.putString("Meeked_user_date"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("date").getValue(String.class));
                    meekdpref.commit();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    SharedPreferences req = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                    SharedPreferences contacts = getSharedPreferences("Phone Contacts", MODE_PRIVATE);
                    SharedPreferences.Editor reqpref=req.edit();
                    reqpref.putString("Request_name"+dataSnapshot.child("User_no").getValue(),contacts.getString(dataSnapshot.child("Phone_no").getValue(String.class),""));
                    reqpref.putString("Request_phone_no"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("Phone_no").getValue(String.class));
                    reqpref.putString("Request_dpno"+dataSnapshot.child("User_no").getValue(),dataSnapshot.child("dpno").getValue(String.class));
                    reqpref.commit();
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
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @SuppressLint("ResourceType")
            @Override
            public void onMapLongClick(LatLng arg0) {
                if(!visible)
                {
                   visible=true;
                    android.app.FragmentManager fragmentManager = getFragmentManager ();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction ().setCustomAnimations(R.animator.leftright,R.animator.rightleft);

                    ProfileSet profileSet = new ProfileSet();  //your fragment

                    // work here to add, remove, etc
                    fragmentTransaction.add (R.id.frag_layout, profileSet);
                    fragmentTransaction.commit ();
                    View view=findViewById(R.id.buttonview);
                    view.setVisibility(View.VISIBLE);
                    Animation anmtn =  AnimationUtils.loadAnimation(getApplicationContext(), R.animator.rightleft_button);
                    view.setAnimation(anmtn);
                    FloatingActionButton flip_change=(FloatingActionButton)findViewById(R.id.fabb1);
                    flip_change.setOnClickListener(new View.OnClickListener() {public void onClick(View v)
                                                   {
                       getFragmentManager().beginTransaction().setCustomAnimations(R.animator.flip_right_in,R.animator.flip_right_out,R.animator.flip_left_in,R.animator.flip_left_out)
                       .replace(R.id.frag_layout, new ContactsBox())
                       .commit();
                      }}
                    );
                    Toast.makeText(MapsActivity.this,"LOng pressed",LENGTH_LONG).show();
                }
            }});
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}
