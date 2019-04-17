package com.meek;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
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
import com.meek.Database.MessageDBHelper;
import com.meek.Database.PeopleDBHelper;
import com.meek.Encryption.RSAKeyExchange;
import com.meek.Messaging.MessageDialogAdapter;
import com.meek.Messaging.MessageService;
import com.meek.Messaging.MsgPPL;
import com.myhexaville.smartimagepicker.ImagePicker;

import android.widget.Button;


import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;


import de.hdodenhof.circleimageview.CircleImageView;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


import static android.content.Context.MODE_PRIVATE;
import static com.meek.Encryption.RSAKeyExchange.decrypt;
import static com.meek.Encryption.RSAKeyExchange.encrypt;

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
    ListView act_nonseen_list,act_seen_list;
    ActivityImage act_img;
    ArrayList<ActFeed> act_seen_feed,act_non_feed;
    ActFeedAdapter actSeenAdapter=null,actUnSeenAdapter=null;
    boolean seen_adapted=false,unseen_adapted=false;
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
    MessageDialogAdapter mg_dg_adapter;
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
        return view;
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
        Button add_activity=btnView.findViewById(R.id.add_act);
        //feedListen();
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

/*        Realm realm = Realm.getDefaultInstance();
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
            */
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
          act_seen_list=(ListView)inf_layout.findViewById(R.id.seen_activities);
          act_nonseen_list=(ListView)inf_layout.findViewById(R.id.unseen_activity_feed);
          feedListen();
      }

    void feedListen()
    {
        actSeenAdapter=new ActFeedAdapter();
        actUnSeenAdapter=new ActFeedAdapter();
        DatabaseReference act_feed_ref = FirebaseDatabase.getInstance().getReference();

        act_feed_ref.child("Users").child(uid).child("activity_feed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String act_yet_to_seen=dataSnapshot.child("activity_yet_to_seen").getValue().toString();
                String act_seen=dataSnapshot.child("activity_seen").getValue().toString();
                int num_act=0;
                act_non_feed=new ArrayList<ActFeed>();
                act_seen_feed=new ArrayList<ActFeed>();
                adaptActFeed(act_yet_to_seen,false);
                adaptActFeed(act_seen,true);
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
    void adaptActFeed(String actFeed,boolean seen)
    {
        while(!actFeed.equals(":"))
        {
            String a_uid=actFeed.substring(1,actFeed.indexOf('.'));
            String act_content=actFeed.substring(1,actFeed.substring(1).indexOf(':')+1);
            actFeed=actFeed.substring(actFeed.substring(1).indexOf(':')+1);
            Log.v("Adapt Act","act_uid="+a_uid+"  act_content="+act_content);
            if(seen)
                act_seen_feed.add(new ActFeed("."+act_content+".",a_uid,seen));
            else
                act_non_feed.add(new ActFeed("."+act_content+".",a_uid,seen));
        }
        actSeenAdapter.getData(act_seen_feed,getContext(),getChildFragmentManager());
        actUnSeenAdapter.getData(act_non_feed,getContext(),getChildFragmentManager());
        if(seen)
        {
            if(seen_adapted)
            {
                actSeenAdapter.notifyDataSetChanged();
            }
            else
            {
                seen_adapted=true;
                act_seen_list.setAdapter(actSeenAdapter);
            }
        }
        else
        {
            if(unseen_adapted)
            {
                actUnSeenAdapter.notifyDataSetChanged();
            }
            else
            {
                unseen_adapted=true;
                act_nonseen_list.setAdapter(actUnSeenAdapter);
            }
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

        mg_dg_adapter=new MessageDialogAdapter(getContext());
        Cursor msgDlgs=new MessageDBHelper(getContext()).getMessageDialogs();
        msgDlgs.moveToFirst();

        if(msgDlgs.getCount()==0)
        {
            noMsgYet(true,inflatedLayout);
        }
        else {
            noMsgYet(false, inflatedLayout);
            setMsgDialogs(msgDlgs);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MessageService.MY_ACTION);
        getContext().registerReceiver(updateDlgsBCR, intentFilter);

        //// firebase listener to the message list


        /// Declare an array list...assign the data of the messages of list one by one
        /// such a way that, for each uid-> check offline realm data for the name and dp
        ///if found save to the arraylist... if not found, query to firebase download the dp and save internal and to realm
        //// query to download the messages and include last 1 of them in the arraylist
        /// repeat until the first six and store the last list item  ///

        ///adapt and shoow... if the listner worked as listener, do the same thing and notifydatachange
    }

    void noMsgYet(boolean flg,View view)
    {
        if(flg)
        {
            view.findViewById(R.id.nomsgyet).setVisibility(View.VISIBLE);
            view.findViewById(R.id.msg_d_list).setVisibility(View.INVISIBLE);
        }
        else
        {
            view.findViewById(R.id.nomsgyet).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.msg_d_list).setVisibility(View.VISIBLE);
        }
    }


    void setMsgDialogs(Cursor msgDlgs)
    {

            msgPPLS=new ArrayList<MsgPPL>();
            MsgPPL newone=new MsgPPL();
            newone.sender_id=msgDlgs.getString(1);
            newone.name=new PeopleDBHelper(getContext()).getName(newone.sender_id);
            newone.last_msg=msgDlgs.getString(2);
            newone.date=msgDlgs.getString(3);
            msgPPLS.add(newone);
            while(msgDlgs.moveToNext())
            {
             newone=new MsgPPL();
             newone.sender_id=msgDlgs.getString(1);
             newone.name=new PeopleDBHelper(getContext()).getName(newone.sender_id);
             newone.last_msg=msgDlgs.getString(2);
             newone.date=msgDlgs.getString(3);
             msgPPLS.add(newone);
            }
            mg_dg_adapter.getData(msgPPLS);
            if(shown_md_num==0)
            {
             mg_dg_View.setAdapter(mg_dg_adapter);
            }
            else
             mg_dg_adapter.notifyDataSetChanged();
            ++shown_md_num;

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
        if(!new PeopleDBHelper(getContext()).checkTable())
            new PeopleDBHelper(getContext()).createTable();
        final DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();
        ppl_ref.child("Users").child(uid).child("Connections").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final String con_meek=dataSnapshot.child("con_meek").getValue().toString();
                String activity_meek=dataSnapshot.child("activity_meek").getValue().toString();
                String location_meek=dataSnapshot.child("location_meek").getValue().toString();
                String sent_request=dataSnapshot.child("act_request_sent").getValue().toString();
                String received_request=dataSnapshot.child("act_request_received").getValue().toString();

                ArrayList<String> meek_cons=extractor(con_meek);
                ArrayList<String> activity_cons=extractor(activity_meek);
                ArrayList<String> loc_cons=extractor(location_meek);
                ArrayList<String> sent_req_cons=extractor(sent_request);
                ArrayList<String> rcv_req_cons=extractor(received_request);

                ////meekcons=1  activitycon=2   loc_con=3   act_sent_rqst=4    act_rcv_rqst=5

                new PeopleDBHelper(getContext());
                for(final String id:meek_cons)
                {
                    Log.e("MEEK CONS","id="+id);
                    if(!(new PeopleDBHelper(context).checkUID(id)))
                    {
                        Log.e("Conn meek_con setting","current id="+id);
                        ppl_ref.child("Users").child(id).child("Info").child("phno").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
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
                              //  setConnectionList();
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

                for(final String id:sent_req_cons)
                {
                Log.e("ACT RQ SNT","id="+id);
                    if(!(new PeopleDBHelper(getContext()).checkUID(id)))
                    {
                        Log.e("Conn activity setting","current id="+id);
                        ppl_ref.child("Users").child(id).child("Info").child("phno").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String phnm=dataSnapshot.getValue().toString();
                                if(phnm!=null);
                                new PeopleDBHelper(getContext()).insertPerson(id,phnm,4);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else if(!(new PeopleDBHelper(getContext()).checkUID(id,4)))
                    {
                        new PeopleDBHelper(getContext()).changePersonStatus(id,4);
                    }
                }

                for(final String id:rcv_req_cons)
                {
                    Log.e("ACT RQ SNT","id="+id);
                    if(!(new PeopleDBHelper(getContext()).checkUID(id)))
                    {
                        Log.e("Conn activity setting","current id="+id);
                        ppl_ref.child("Users").child(id).child("Info").child("phno").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String phnm=dataSnapshot.getValue().toString();
                                if(phnm!=null);
                                new PeopleDBHelper(getContext()).insertPerson(id,phnm,5);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else if(!(new PeopleDBHelper(getContext()).checkUID(id,5)))
                    {
                        new PeopleDBHelper(getContext()).changePersonStatus(id,5);
                    }
                }


                setConnectionList();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private File createImageFile(String ext) throws IOException
    {
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

    public static ArrayList<String> extractor(String all_uid)
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

        connectionAdapter.getData(conn_list,getContext(),uid);
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

    BroadcastReceiver updateDlgsBCR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle bundle = intent.getExtras();
            String sender_id = bundle.getString("sender_id");
            String uid = bundle.getString("uid");
            Cursor msgDlgs=new MessageDBHelper(getContext()).getMessageDialogs();
            setMsgDialogs(msgDlgs);
        }
    };
}


class ConnectionAdapter extends BaseAdapter implements StickyListHeadersAdapter
{
    ArrayList<Contact> conn_ppl;
    Context context;
    String uid;
    void getData(ArrayList<Contact> conn_ppl,Context context,String id)
    {
        this.uid=id;
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

    ////meekcons=1  activitycon=2   loc_con=3   act_sent_rqst=4    act_rcv_rqst=5      loc_rcv_rqst=6
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.e("CONN ADAPTER","NAME:"+conn_ppl.get(i).getName()+"   UID:"+conn_ppl.get(i).getUID());
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch(conn_ppl.get(i).conn_level)
        {
            case 5: view = inflater.inflate(R.layout.request_list_item, null);
                    final CircleImageView okay=(CircleImageView)view.findViewById(R.id.rq_okay);
                    final CircleImageView cancel=(CircleImageView)view.findViewById(R.id.rq_cancel);
                    okay.setTag(conn_ppl.get(i).getUID());
                    cancel.setTag(conn_ppl.get(i).getUID());
                    okay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            try {

                                PublicKey publicKey= new RSAKeyExchange(context,uid).getPublicKey(view.getTag().toString());
                                byte [] encrypted = encrypt(publicKey, "This is a secret message");     ///set the key in it

                                DatabaseReference key_ref = FirebaseDatabase.getInstance().getReference();
                                key_ref.child("Key_Exchange").child(view.getTag().toString()).child(uid).setValue(encrypted.toString());

                                key_ref.child("Key_Exchange").child(uid).child(view.getTag().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String user_key=dataSnapshot.getValue().toString();
                                        PrivateKey myPrivateKey=new RSAKeyExchange(context,uid).myPrivateKey();
                                        try {
                                            byte[] real_key_bytes=decrypt(myPrivateKey,user_key.getBytes());
                                            String str_real_key=real_key_bytes.toString();

                                            /////SAVE IN THE DB
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            /////adding to the other user's activity_meek
                            final DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();
                            ppl_ref.child("Users")
                                    .child(view.getTag().toString())
                                    .child("Connections")
                                    .child("activity_meek")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            String rcv_id=dataSnapshot.getValue().toString();
                                            if(!rcv_id.contains(":"+uid+":"))
                                            {
                                                ppl_ref.child("Users").child(view.getTag().toString())
                                                        .child("Connections")
                                                        .child("activity_meek").setValue(":"+uid+rcv_id);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                            /////adding to the user's activity_meek
                            ppl_ref.child("Users")
                                    .child(uid)
                                    .child("Connections")
                                    .child("activity_meek")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            String rcv_id=dataSnapshot.getValue().toString();
                                            if(!rcv_id.contains(":"+view.getTag().toString()+":"))
                                            {
                                                ppl_ref.child("Users").child(uid)
                                                        .child("Connections")
                                                        .child("activity_meek").setValue(":"+view.getTag().toString()+rcv_id);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                            /////adding to the user's activity_meek
                            ppl_ref.child("Users")
                                    .child(uid)
                                    .child("Connections")
                                    .child("activity_meek")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            String rcv_id=dataSnapshot.getValue().toString();
                                            if(!rcv_id.contains(":"+view.getTag().toString()+":"))
                                            {
                                                ppl_ref.child("Users").child(uid)
                                                        .child("Connections")
                                                        .child("activity_meek").setValue(":"+view.getTag().toString()+rcv_id);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });



                        }
                    });
                    break;

            case 3: view = inflater.inflate(R.layout.viewer_list_item, null);
                    break;

            case 2: view = inflater.inflate(R.layout.viewer_list_item, null);
                    view.setBackgroundResource(R.drawable.bg_conmeek);
                    break;
            case 1: view = inflater.inflate(R.layout.meek_con_item, null);
                    final CircleImageView btn=(CircleImageView)view.findViewById(R.id.add);
                    btn.setTag(conn_ppl.get(i).getUID());
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String usr_id=view.getTag().toString();
                            final DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();
                            ppl_ref.child("Users")
                                    .child(usr_id)
                                    .child("Connections")
                                    .child("act_request_received")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            String rcv_id=dataSnapshot.getValue().toString();
                                            if(!rcv_id.contains(":"+uid+":"))
                                            {
                                                ppl_ref.child("Users").child(usr_id)
                                                        .child("Connections")
                                                        .child("act_request_received").setValue(":"+uid+rcv_id);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                            ppl_ref.child("Users").child(uid)
                                    .child("Connections")
                                    .child("act_request_sent")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            String sent_id=dataSnapshot.getValue().toString();
                                            if(!sent_id.contains(":"+usr_id+":"))
                                            {
                                                ppl_ref.child("Users").child(uid)
                                                        .child("Connections")
                                                        .child("act_request_sent").setValue(":"+sent_id+uid+"");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                            btn.setImageResource(R.drawable.cancel_cross);

                        }
                    });
                    break;
            case 4: view = inflater.inflate(R.layout.meek_con_item, null);
                    final CircleImageView btn2=(CircleImageView)view.findViewById(R.id.add);
                    btn2.setImageResource(R.drawable.cancel_cross);
                    btn2.setTag(conn_ppl.get(i).getUID());
                    btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String usr_id=view.getTag().toString();
                        final DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();
                        ppl_ref.child("Users").child(usr_id)
                                .child("Connections")
                                .child("act_request_received")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        String rcv_id=dataSnapshot.getValue().toString();
                                        ppl_ref.child("Users").child(usr_id)
                                                    .child("Connections")
                                                    .child("act_request_received").setValue(rcv_id.replace(uid+":",""));
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                        ppl_ref.child("Users").child(uid)
                                .child("Connections")
                                .child("act_request_sent")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        String sent_id=dataSnapshot.getValue().toString();
                                        ppl_ref.child("Users").child(uid)
                                                    .child("Connections")
                                                    .child("act_request_sent").setValue(sent_id.replace(":"+usr_id,""));
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                        btn2.setImageResource(R.drawable.cancel_cross);

                    }
                });
                break;
        }
        ////meekcons=1  activitycon=2   loc_con=3   act_sent_rqst=4    act_rcv_rqst=5

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
            case 5: name.setText("Received Activity Requests");
                    view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.meek_loccon) );
                    break;
            case 4: name.setText("Sent Activity Requests");
                    view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.meek_loccon) );
                    break;
            case 3: name.setText("Location access");
                    view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.meek_loccon) );
                    break;
            case 2: name.setText("Activity access");
                    view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.meek_actcon) );
                    break;
            case 1: view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_conmeek) );
                    name.setText("Connected in meek");
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


    ActFeed(String act_content,String a_uid,boolean seen)
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
    }

}

