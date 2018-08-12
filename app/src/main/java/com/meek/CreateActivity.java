package com.meek;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.*;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by User on 06-Jul-18.
 */

public class CreateActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks  {
    private GeoDataClient mGeoDataClient;
    int CAM_CODE=1,VID_REQ=2,SET_DEST=3;
    ActivityVideo act_vid;
    ActivityImage act_img;

    private PlaceDetectionClient mPlaceDetectionClient;
    Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);
        context=CreateActivity.this;

        mGeoDataClient = com.google.android.gms.location.places.Places.getGeoDataClient(context, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(context, null);
        setActivtiyTab();
    }


    void setActivtiyTab()
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayout= inflater.inflate(R.layout.activity_tab, null, false);

        setActFeatureButton();
        setTabsSetActivity();
    }
    void setActFeatureButton()
    {
        LinearLayout functions=(LinearLayout)findViewById(R.id.functions);
        Button actbtn=(Button)functions.findViewById(R.id.activity);
        Button musicbtn=(Button)functions.findViewById(R.id.music);


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

    void setTabsSetActivity()
    {
        Log.v("Tab daa", "tab is set YOO");
        SharedPreferences actPrefs= getSharedPreferences("ActPrefs", MODE_PRIVATE);
        int curr_stat=actPrefs.getInt("curr_stat",11);
        final NonSwipeableActivityTabs mviewPager = (NonSwipeableActivityTabs)findViewById(R.id.tab_container);
        ActivityTabAdapter activityTabAdapter = new ActivityTabAdapter(getSupportFragmentManager());
        act_vid=new ActivityVideo();
        act_img=new ActivityImage();
        final ActivityText act_txt=new ActivityText();

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

        TextView set_dest=(TextView) findViewById(R.id.set_dest);
        set_dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivityForResult(new Intent(context,DestinationPlace.class),SET_DEST);
            }
        });
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
                            case DialogInterface.BUTTON_POSITIVE: if(position==0)
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
