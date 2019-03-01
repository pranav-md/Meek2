package com.meek;

import android.annotation.SuppressLint;
import android.app.*;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.Manifest.permission;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.meek.Database.PeopleDBHelper;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;

import android.widget.Button;
import android.widget.VideoView;


import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 25-May-18.
 */

@SuppressLint("ValidFragment")
public class TabFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    ////
    GoogleApiClient mGoogleApiClient=null;
    int CAM_CODE=1,VID_REQ=2,SET_DEST=3;
    String mCurrentPhotoPath;


    ActivityVideo act_vid;
    ListView act_feed_list;
    ActivityImage act_img;
    ArrayList<ActFeed> actFeeds;
    ActFeedAdapter actFeedAdapter=null;
    boolean adapted=false;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    Context context;
    ImagePicker imagePicker;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    String placess,uid;
    TextView plcs;
    View current_layout;
    RelativeLayout tabcontainer;
    ExpandableLayout img_exp;
    int curr_tab;
    boolean tab_act;
    //////////////

    ValueEventListener msg_listener=null;
    ArrayList<MsgPPL> msgPPLS;
    String dg_msgs;
    int shown_md_num=0;
    int tot_msg_ppl;
    ProgressBar msg_load;
    MsgDialogAdapter mg_dg_adapter;
    View btnView;
    //////////////////////
    ConnectionAdapter connectionAdapter=null;
    StickyListHeadersListView con_list=null;

    @SuppressLint("WrongViewCast") ListView mg_dg_View;
    @SuppressLint("ValidFragment")
    public TabFragment(Context context)
    {
        super.onAttach(context);
        this.context=context;
    }
    public TabFragment()
    {    }
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return FlipAnimation.create(FlipAnimation.RIGHT, enter, 500);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabsfragment, container, false);
        final BottomNavigation btm_nav=(BottomNavigation)view.findViewById(R.id.bottom_nav);
        final BottomNavigation check_sel=btm_nav;
        btm_nav.setSelectedIndex(2,true);
        btnView=view;
       plcs=(TextView)view.findViewById(R.id.placess);
       context=getContext();
        tabcontainer=view.findViewById(R.id.tabscontainer);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        connectionAdapter=new ConnectionAdapter();
        SharedPreferences pref = getContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        uid=pref.getString("uid", "");
        Log.e("UID value","uid="+uid);
        setConnection();

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);

        btm_nav.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(int i, int i1, boolean b) {
                Toast.makeText(context,"Activity",Toast.LENGTH_LONG);
                if(btm_nav.getSelectedIndex()==2) {
                    setActivtiyTab();
                    curr_tab=2;
                    Toast.makeText(context, "Activity", Toast.LENGTH_LONG);
                }
                else if(btm_nav.getSelectedIndex()==1) {
                    Toast.makeText(context, "Chat", Toast.LENGTH_LONG);
                    curr_tab=1;
                    setMsgTab();
                }
                else if(btm_nav.getSelectedIndex()==0) {
                    Toast.makeText(getContext(), "Contact", Toast.LENGTH_LONG);
                    curr_tab=0;
                    setPplTab();
                }
            }

            @Override
            public void onMenuItemReselect(int i, int i1, boolean b) {

            }
        });
        callPlaceDetectionApi();
              return view;
    }

    private void callPlaceDetectionApi() throws SecurityException {
        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i("Google places", String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName()
                            ,
                            placeLikelihood.getLikelihood()));
                    placess=placess+placeLikelihood.getPlace().getName().toString()+"\n";
                }
                likelyPlaces.release();
                setPlaceList();
            }
        });
    }

    void setPlaceList()
    {
        plcs.setText(" ");
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
    void setActivtiyTab()
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayout= inflater.inflate(R.layout.activity_tab, null, false);
        current_layout=inflatedLayout;
        tabcontainer.removeAllViews();
        tabcontainer.addView(inflatedLayout);
        FloatingActionButton add_activity=btnView.findViewById(R.id.add_act);
        add_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),CreateActivity.class));
            }
        });
      //  setActFeatureButton();
     //   setTabsSetActivity();

        setUsersActivity(inflatedLayout);
    }
    void setActFeatureButton()
    {
        View cur_view=getView();
        LinearLayout functions=(LinearLayout)cur_view.findViewById(R.id.functions);
        Button actbtn=(Button)functions.findViewById(R.id.activity);
        Button musicbtn=(Button)functions.findViewById(R.id.music);

        Realm realm = Realm.getDefaultInstance();
        Realm.init(context);
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
        AudioManager audioManager = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
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

    void setUsersActivity(View inf_layout)
    {
        act_feed_list=(ListView)inf_layout.findViewById(R.id.activity_feed);
        actFeedAdapter=new ActFeedAdapter();
        feedListen();
    }

    void feedListen()
    {
        DatabaseReference act_feed_ref = FirebaseDatabase.getInstance().getReference();

        act_feed_ref.child("Users").child(uid).child("activity_feed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String actfeedstr=dataSnapshot.getValue().toString();
                int num_act=0;
                actFeeds=new ArrayList<ActFeed>();
                adaptActFeed(actfeedstr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "camera permission granted", Toast.LENGTH_LONG).show();
                imagePicker.setWithImageCrop(1,1).openCamera();
            } else {
                Toast.makeText(context, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("Camera daa", "handle activity");

        if (requestCode == VID_REQ)
        {
            Log.v("vid cam path", "tabfragmnt");
            act_vid.onActivityResult(requestCode,resultCode,data);
        }
        else if (requestCode == CAM_CODE)
        {
            act_img.onActivityResult(requestCode,resultCode,data);
        }
        else if(requestCode==SET_DEST)
        {
            Log.v("SET DEST","yoyoyo");
            String place_name=data.getStringExtra("Place name");
            final LayoutInflater inflater = LayoutInflater.from(context);
            View inflatedLayout= getView();
            final RelativeLayout dest_set=(RelativeLayout)inflatedLayout.findViewById(R.id.dest_set);
            dest_set.removeAllViews();
            inflatedLayout= inflater.inflate(R.layout.destination_set, null, false);
            TextView dest_name=(TextView)inflatedLayout.findViewById(R.id.dest_name);
            Button close_dest=(Button)inflatedLayout.findViewById(R.id.close_dest);
            dest_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(getContext(),DestinationPlace.class),SET_DEST);
                }
            });
            close_dest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            dest_name.setText(place_name);
            dest_set.addView(inflatedLayout);
        }
    }
    void adaptActFeed(String actFeed)
    {
        while(!actFeed.equals(":"))
        {
            String a_uid=actFeed.substring(1,actFeed.indexOf('.'));
            String act_content=actFeed.substring(1,actFeed.substring(1).indexOf(':')+1);
            actFeed=actFeed.substring(actFeed.substring(1).indexOf(':')+1);
            Log.v("Adapt Act","act_uid="+a_uid+"  act_content="+act_content);
            actFeeds.add(new ActFeed("."+act_content+".",a_uid));
        }
        actFeedAdapter.getData(actFeeds,getContext(),getChildFragmentManager());

        if(adapted==true)
        {
            actFeedAdapter.notifyDataSetChanged();
        }
        else
        {
            adapted=true;
            act_feed_list.setAdapter(actFeedAdapter);
        }
    }

    @SuppressLint("WrongViewCast")
    void setMsgTab()
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayout= inflater.inflate(R.layout.msg_dialog_list, null, false);
        tabcontainer.removeAllViews();
        tabcontainer.addView(inflatedLayout);
        mg_dg_View=(ListView) inflatedLayout.findViewById(R.id.msg_d_list);
        mg_dg_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }

        });
        mg_dg_adapter=new MsgDialogAdapter(getContext());
        msg_load=(ProgressBar)inflatedLayout.findViewById(R.id.msg_load);
        //// firebase listener to the message list

        DatabaseReference msg_ref = FirebaseDatabase.getInstance().getReference();
        if(msg_listener==null)
            msg_listener=msg_ref.child("Users").child(uid).child("Message_counter").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                msg_load.setVisibility(View.VISIBLE);
                tot_msg_ppl =0;
                shown_md_num=0;
                msgPPLS=new ArrayList<MsgPPL>();
                dg_msgs=dataSnapshot.getValue().toString();
                for( int i=0; i<dg_msgs.length(); i++ ) {
                    if (dg_msgs.charAt(i) == ':') {
                        tot_msg_ppl++;
                    }
                }
                --tot_msg_ppl;
                adaptMsgs();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        else
        {
            mg_dg_adapter.getData(msgPPLS);
            mg_dg_View.setAdapter(mg_dg_adapter);

        }


        /// Declare an array list...assign the data of the messages of list one by one
                            /// such a way that, for each uid-> check offline realm data for the name and dp
                            ///if found save to the arraylist... if not found, query to firebase download the dp and save internal and to realm
                            //// query to download the messages and include last 1 of them in the arraylist
            /// repeat until the first six and store the last list item  ///

        ///adapt and shoow... if the listner worked as listener, do the same thing and notifydatachange


    }

    void adaptMsgs()
    {
        int loop_num;
        if(tot_msg_ppl-shown_md_num<6)
            loop_num=tot_msg_ppl;
        else
            loop_num=tot_msg_ppl-shown_md_num;
        int tmp_show=shown_md_num;
        for(int i=shown_md_num;i<tmp_show+loop_num;++i)
        {
            dg_msgs=dg_msgs.substring(1);
            int pos=dg_msgs.indexOf(':');
            String msg_content=dg_msgs.substring(0,pos);
            dg_msgs=dg_msgs.substring(pos);
            MsgPPL msgPPL=new MsgPPL("."+msg_content,uid);
            if(msgPPL.num_unread>0)
                msgPPL.get_lastMsg();
            msgPPL.getName();
            msgPPLS.add(msgPPL);
            mg_dg_adapter.getData(msgPPLS);
            if(shown_md_num==0)
            {
                mg_dg_View.setAdapter(mg_dg_adapter);
            }
            else
                mg_dg_adapter.notifyDataSetChanged();
            ++shown_md_num;
        }
        if(msg_load.getVisibility()==View.VISIBLE)
            msg_load.setVisibility(View.INVISIBLE);


    }


    void setPplTab()
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayout= inflater.inflate(R.layout.people_list, null, false);
        current_layout=inflatedLayout;
        tabcontainer.removeAllViews();
        tabcontainer.addView(inflatedLayout);
        con_list=(StickyListHeadersListView) inflatedLayout.findViewById(R.id.ppl_list);
        setConnectionList();
    }

    void setConnection()
    {
        final DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();
        ppl_ref.child("Users").child(uid).child("Connections").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final String con_meek=dataSnapshot.child("con_meek").getValue().toString();
                String activity_meek=dataSnapshot.child("activity_meek").getValue().toString();
                String location_meek=dataSnapshot.child("location_meek").getValue().toString();

                ArrayList<String> meek_cons=extractor(con_meek);
                ArrayList<String> activity_cons=extractor(activity_meek);
                ArrayList<String> loc_cons=extractor(location_meek);
                new PeopleDBHelper(getContext());
                for(final String id:meek_cons)
                {
                    Log.e("MEEK CONS","id="+id);
                    if(!(new PeopleDBHelper(getContext()).checkUID(id)))
                    {
                        Log.e("Conn meek_con setting","current id="+id);
                        ppl_ref.child("Users").child(id).child("Info").child("phno").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String phnm=dataSnapshot.getValue().toString();
                                if(phnm!=null)
                                    new PeopleDBHelper(getContext()).insertPerson(id,phnm,1);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else if(!(new PeopleDBHelper(getContext()).checkUID(id,1)))
                    {
                        new PeopleDBHelper(getContext()).changePersonStatus(id,1);
                    }
                }
                ///////////
                for(final String id:activity_cons)
                {
                    Log.e("ACTIVITY CONS","id="+id);
                    if(!(new PeopleDBHelper(getContext()).checkUID(id)))
                    {
                        Log.e("Conn activity setting","current id="+id);
                        ppl_ref.child("Users").child(id).child("Info").child("phno").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String phnm=dataSnapshot.getValue().toString();
                                if(phnm!=null);
                                    new PeopleDBHelper(getContext()).insertPerson(id,phnm,2);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else if(!(new PeopleDBHelper(getContext()).checkUID(id,2)))
                    {
                            new PeopleDBHelper(getContext()).changePersonStatus(id,2);
                    }
                }

                for(final String id:loc_cons)
                {
                    Log.e("LOC CONS","id="+id);
                    if(!(new PeopleDBHelper(getContext()).checkUID(id)))
                    {
                        Log.e("Checked loc CONS","id="+id);
                        ppl_ref.child("Users").child(id).child("Info").child("phno").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String phnm=dataSnapshot.getValue().toString();
                                if(phnm!=null);
                                    new PeopleDBHelper(getContext()).insertPerson(id,phnm,3);
                                Log.e("INSIDE datasnapshot",phnm+"_phone num retrieved");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else if(!(new PeopleDBHelper(getContext()).checkUID(id,3)))
                    {
                        new PeopleDBHelper(getContext()).changePersonStatus(id,3);
                    }
                }
                setConnectionList();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private File createImageFile(String ext) throws IOException {
        // Create an image file name
        String imageFileName = "activity";
        String storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File image = new File(storageDir,
                imageFileName+ /* prefix */
                ext /* suffix */
                      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

/*    void setActivityPic()
    {
        ExpandableLayout img_exp=(ExpandableLayout)activity_layout.findViewById(R.id.image_expand);
        img_exp.toggle();
        ImageView act_img=(ImageView)activity_layout.findViewById(R.id.act_img);

        File imgFile = new  File(mCurrentPhotoPath);

        if(imgFile.exists())
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            act_img.setImageBitmap(myBitmap);
        }

    }
    void setActivityVid()
    {
        ExpandableLayout vid_exp=(ExpandableLayout)activity_layout.findViewById(R.id.video_expand);
        vid_exp.toggle();

        VideoView act_player=(VideoView) activity_layout.findViewById(R.id.act_player);
        act_player.setVideoPath(mCurrentPhotoPath);

        //Button play=(Button)activity_layout.findViewById(R.id.play)


    }*/
    void setTabsSetActivity()
    {
        Log.v("Tab daa", "tab is set YOO");
        SharedPreferences actPrefs= getContext().getSharedPreferences("ActPrefs", MODE_PRIVATE);
        int curr_stat=actPrefs.getInt("curr_stat",11);
        final NonSwipeableActivityTabs mviewPager = (NonSwipeableActivityTabs) current_layout.findViewById(R.id.tab_container);
        ActivityTabAdapter activityTabAdapter = new ActivityTabAdapter(getChildFragmentManager());
        act_vid=new ActivityVideo();
        act_img=new ActivityImage();
        final ActivityText act_txt=new ActivityText();

        activityTabAdapter.addFragment(act_vid, "Video");
        activityTabAdapter.addFragment(act_img, "Image");
        activityTabAdapter.addFragment(act_txt, "Text");

        mviewPager.setAdapter(activityTabAdapter);
        final TabLayout tabLayout = (TabLayout) current_layout.findViewById(R.id.tabs);
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

                final SharedPreferences actPrefs= getContext().getSharedPreferences("ActPrefs", MODE_PRIVATE);
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
    void setConnectionList()
    {
        ArrayList<Contact> conn_list=new ArrayList<Contact>();
        conn_list=new PeopleDBHelper(context).getAllConnections();

        connectionAdapter.getData(conn_list,getContext());
        if(curr_tab==0)
        {
            if(con_list!=null)
            if(con_list.getAdapter()==null)
            {
                con_list.setAdapter(connectionAdapter);
            }
            else
            {
                connectionAdapter.notifyDataSetChanged();
            }
        }
    }


}
class ConnectionAdapter extends BaseAdapter implements StickyListHeadersAdapter
{
    ArrayList<Contact> conn_ppl;
    Context context;
    void getData(ArrayList<Contact> conn_ppl,Context context)
    {
        this.context=context;
        this.conn_ppl=conn_ppl;
    }
    @Override
    public int getCount() {
        return conn_ppl.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.e("CONN ADAPTER","NAME:"+conn_ppl.get(i).getName()+"   UID:"+conn_ppl.get(i).getUID());
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.viewer_list_item, null);
        TextView name=(TextView) view.findViewById(R.id.name);
        name.setText(conn_ppl.get(i).getName());
        return view;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.viewer_list_header, null);
        TextView name=(TextView) view.findViewById(R.id.header);
        switch(conn_ppl.get(position).conn_level)
        {
            case 3: name.setText("Location access");
                    break;
            case 2: name.setText("Activity access");
                    break;
            case 1: name.setText("Connected in meek");
                    break;
        }
        return view;
    }

    @Override
    public long getHeaderId(int position)
    {
        return conn_ppl.get(position).conn_level;
    }
}

class ActFeed
{
    String a_uid;
    ArrayList<Activities> activities;

    ActFeed(String act_content,String a_uid)
    {
        activities=new ArrayList<Activities>();
        this.a_uid=a_uid;
        int num=0;
        while(!act_content.equals("."))
        {
            Activities newone= new Activities();
            newone.act_id=act_content.substring(1,act_content.substring(1).indexOf('.')+1);
            act_content=act_content.substring(act_content.substring(1).indexOf('.')+1);
            activities.add(newone);
        }

        //getName();

    }

}