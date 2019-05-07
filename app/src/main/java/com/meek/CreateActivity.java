package com.meek;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.*;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.meek.Encryption.AES;
import com.polyak.iconswitch.IconSwitch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


import static android.content.ContentValues.TAG;

/**
 * Created by User on 06-Jul-18.
 */

public class CreateActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks  {

    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    public LocationManager mLocationManager = null;
    String place_name,caption,lat,lng;
    private GeoDataClient mGeoDataClient;
    int CAM_CODE=1,VID_REQ=2,SET_DEST=3;
    ActivityVideo act_vid;
    LatLng cur_location;
    ActivityImage act_img;
    String type="3";
    private PlaceDetectionClient mPlaceDetectionClient;
    String uid;
    String serverkey;
    Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);
        context=CreateActivity.this;

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        serverkey = extras.getString("ServerKey");

        mGeoDataClient = Places.getGeoDataClient(context, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(context, null);
        SharedPreferences pref=getApplicationContext().getSharedPreferences("UserDetails",MODE_PRIVATE);
        uid=pref.getString("uid","");
        lat=pref.getString("lat","");
        lng=pref.getString("lng","");
        callPlaceDetectionApi();

        mGeoDataClient = com.google.android.gms.location.places.Places.getGeoDataClient(context, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(context, null);
        setActivtiyTab();
    }
    void setCaptionText(String text)
    {
        caption=text;
    }
    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    void uploadActivity()
    {
        SharedPreferences actPrefs= getSharedPreferences("ActPrefs", MODE_PRIVATE);
        int curr_stat=actPrefs.getInt("curr_stat",11);

        String storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File file = new File(storageDir, "activity.crypt");
        int size = (int) file.length();
        final byte[] bytes = new byte[size];
        try
        {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
                ////
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Activities");
        final ProgressDialog progressBar=new ProgressDialog(CreateActivity.this);
        progressBar.show();
        progressBar.setCancelable(false);
        userRef.child(uid).child("Activity_info").child("Activity_num").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot)
                    {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        SharedPreferences getprefs = getSharedPreferences("ActsPrefs", MODE_PRIVATE);
                        final int curr_stat=getprefs.getInt("curr_stat",11);
                        if(Integer.parseInt(type)<3)
                            storageReference.child("Activity/"+uid+"_"+(Integer.parseInt(dataSnapshot.getValue().toString())+1)+".crypt").putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressBar.dismiss();
                                    setAttributes(Integer.parseInt(dataSnapshot.getValue().toString())+1,curr_stat);
                                    userRef.child(uid).child("Activity_info").child("Activity_num").setValue(Integer.parseInt(dataSnapshot.getValue().toString())+1);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.e("UPLOAD ERROR",exception+"");
                                }
                            });
                        else
                        {
                            progressBar.dismiss();
                            setAttributes(Integer.parseInt(dataSnapshot.getValue().toString())+1,curr_stat);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    void setAttributes(int act_num, int curr_stat)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Activities");
        IconSwitch privacy=(IconSwitch)findViewById(R.id.privacy_status);
        boolean p_stat=privacy.isActivated();
        //EditText caption=(EditText)findViewById(R.id.img_caption);
        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));

        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        String getTime= dateFormatGmt.format(new Date())+"";

        String map_branch;
        int visiblity;
        if(p_stat)
        {
            map_branch="personal";
            visiblity=0;
        }
        else
        {
            visiblity=1;
            map_branch="public";
        }
        SharedPreferences getPref=getSharedPreferences("USERKEY",MODE_PRIVATE);
        Log.e("SETATTRIBUTES",serverkey+" is the server key");
        String key=new AES().decrypt(getPref.getString("KEY",""),serverkey);
        Log.e("SETATTRIBUTES",key+" is the key");
        userRef.child(uid).child("mapview").child(map_branch).child(act_num+"").child("lat").setValue(new AES().encrypt(lat,key));
        userRef.child(uid).child("mapview").child(map_branch).child(act_num+"").child("lng").setValue(new AES().encrypt(lng,key));
        userRef.child(uid).child("pgview").child(getTime.substring(0,getTime.indexOf(" "))).child(act_num+"").setValue(getTime);

        userRef.child(uid).child("All_Activities").child(act_num+"").child("act_visibility").setValue(visiblity);
        userRef.child(uid).child("All_Activities").child(act_num+"").child("act_type").setValue(type);
        userRef.child(uid).child("All_Activities").child(act_num+"").child("act_current_place").setValue(new AES().encrypt(place_name,key));
     //   userRef.child(uid).child("All_Activities").child(act_num+"").child("act_date").setValue(new AES().encrypt(Long.parseLong(df.format(new Date()))+"",key));
        userRef.child(uid).child("All_Activities").child(act_num+"").child("act_date").setValue(new AES().encrypt(df.format(new Date())+"",key));
        userRef.child(uid).child("All_Activities").child(act_num+"").child("act_text").setValue(new AES().encrypt(caption,key));
        userRef.child(uid).child("Activity_info").child("Activity_num").setValue(act_num+"");

    }

    void setActivtiyTab()
    {
        //setActFeatureButton();
        final Button upload=(Button)findViewById(R.id.okay);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadActivity();
            }
        });
        setTabsSetActivity();

    }

    /*     void setActFeatureButton()
      {

      Realm realm = Realm.getDefaultInstance();
          realm.beginTransaction();
          Drawable icon=null;
          com.meek.Activity result = realm.where(com.meek.Activity.class).findFirst();
          if(result!=null)
              switch(result.activity)
              {
                  case DetectedActivity.IN_VEHICLE:   icon=context.getResources().getDrawable(R.drawable.moving);
                      Log.d("HAH","In Vehicle");
                      break;
                  case DetectedActivity.ON_BICYCLE:   icon=context.getResources().getDrawable(R.drawable.moving);
                      Log.d("HAH","ON_BICYCLE");
                      break;

                  case DetectedActivity.ON_FOOT:   icon=context.getResources().getDrawable(R.drawable.footwalk);
                      Log.d("HAH","ON_FOOT");
                      break;

                  case DetectedActivity.RUNNING:  icon=context.getResources().getDrawable(R.drawable.footwalk);
                      Log.d("HAH","RUNNING");
                      break;

                  case DetectedActivity.STILL:    icon=context.getResources().getDrawable(R.drawable.still);
                      Log.d("HAH","STILL");
                      break;

                  case DetectedActivity.WALKING:  icon=context.getResources().getDrawable(R.drawable.footwalk);
                      Log.d("HAH","WALKING");
                      break;

              }

          if(icon!=null)
              actbtn.setBackground(icon);
          else
              actbtn.setVisibility(View.INVISIBLE);
          realm.commitTransaction();
          realm.close();
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        ;
        if(audioManager.isMusicActive()==true) {
            actbtn.setVisibility(View.VISIBLE);
            //   audiotext.setText("Playing  "+ audioManager.getParameters(""));
        }
        else
            actbtn.setVisibility(View.INVISIBLE);

        // else
        //audiotext.setText("Not playing");
    }
              */

    void setTabsSetActivity()
    {
        Log.v("Tab daa", "tab is set YOO");
        SharedPreferences actPrefs= getSharedPreferences("ActPrefs", MODE_PRIVATE);
        int curr_stat=actPrefs.getInt("curr_stat",11);
        final NonSwipeableActivityTabs mviewPager = (NonSwipeableActivityTabs)findViewById(R.id.tab_container);
        ActivityTabAdapter activityTabAdapter = new ActivityTabAdapter(getSupportFragmentManager());
        act_vid=new ActivityVideo(serverkey);
        act_img=new ActivityImage(serverkey);
        final ActivityText act_txt=new ActivityText(serverkey);

        activityTabAdapter.addFragment(act_vid, "Video");
        activityTabAdapter.addFragment(act_img, "Image");
        activityTabAdapter.addFragment(act_txt, "Text");

        mviewPager.setAdapter(activityTabAdapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mviewPager);

        if(curr_stat%2==0)
            mviewPager.setCurrentItem(0);
        else if(curr_stat%3==0)
            mviewPager.setCurrentItem(1);
        else
            mviewPager.setCurrentItem(2);


        mviewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

                Log.v("Page changed","Current page="+position);

                final SharedPreferences actPrefs= context.getSharedPreferences("ActPrefs", MODE_PRIVATE);
                final int curr_stat=actPrefs.getInt("curr_stat",11);
                final SharedPreferences.Editor actPrefEdit=actPrefs.edit();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which)
                        {
                            case DialogInterface.BUTTON_POSITIVE:  type=(position+1)+"";
                                                                   if(position==0)
                                                                        actPrefEdit.putInt("curr_stat",2);
                                                                   else if(position==1)
                                                                        actPrefEdit.putInt("curr_stat",3);
                                                                   else if(position==2)
                                                                        actPrefEdit.putInt("curr_stat",5);
                                                                   actPrefEdit.commit();
                                                                   break;

                            case DialogInterface.BUTTON_NEGATIVE:  if(curr_stat%2==0)
                                mviewPager.setCurrentItem(0);
                            else if(curr_stat%3==0)
                                mviewPager.setCurrentItem(1);
                            else if(curr_stat%5==0)
                                mviewPager.setCurrentItem(2);
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String actvty = "";

                if(curr_stat==4&&position!=0)
                {
                    actvty="video";
                }
                else if(curr_stat==9&&position!=1)
                {
                    actvty="image";
                }
                else if(curr_stat==25&&position!=2)
                {
                    actvty="text";
                }
                if(!actvty.equals(""))
                    builder.setMessage("Want to discard the "+actvty+"?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                else
                    type=(position+1)+"";


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

/*
            @Override
            public void onViewAttachedToWindow(final View view) {

                Log.v("Page changed","Current page="+view.getTag());

            }

            @Override
            public void onViewDetachedFromWindow(View view) {

            }
            */
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    private void callPlaceDetectionApi() throws SecurityException {
        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                try
                {
                    ArrayList<ListPlace> plc_list=new ArrayList<ListPlace>();
                    int i=0;
                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        i++;
                        Log.i("Google places", String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),placeLikelihood.getLikelihood()));
                        plc_list.add(new ListPlace(i,placeLikelihood.getPlace().getName().toString(),placeLikelihood.getPlace().getLatLng()));
                        // placess=placess+placeLikelihood.getPlace().getName().toString()+"\n";
                    }
                    likelyPlaces.release();
                    //  setPlaceList();
                    ActivityPlacesAdapter pla=new ActivityPlacesAdapter();
                    pla.getData(plc_list,CreateActivity.this);
                    Spinner spinner=findViewById(R.id.spinner);
                    spinner.setAdapter(pla);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            TextView plc_view=(TextView)view.findViewById(R.id.plc_name);
                            place_name=plc_view.getText().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
                catch (Exception e)
                {

                }


            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i("Google Places", "Google Places API connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Google Places", "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }
}

class ListPlace
{
    LatLng plc;
    String name;
    int num;
    ListPlace(int num,String place,LatLng plc)
    {
        this.plc=plc;
        this.name=place;
        this.num=num;
    }
}
