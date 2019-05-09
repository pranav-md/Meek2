package com.meek;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.meek.Database.MessageDBHelper;
import com.meek.Database.PeopleDBHelper;
import com.meek.Encryption.AES;
import com.meek.Encryption.RSAKeyExchange;
import com.meek.Messaging.Message;
import com.meek.Messaging.MessageDialogAdapter;
import com.meek.Messaging.MessageService;
import com.meek.Messaging.MsgPPL;
import com.meek.Services.ConnectionService;
import com.myhexaville.smartimagepicker.ImagePicker;

import android.widget.Button;


import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;


import de.hdodenhof.circleimageview.CircleImageView;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


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
    LinearLayout act_feed=null;
    ExpandableLayout img_exp;
    int curr_tab=5;
    boolean tab_act;
    //////////////

    ValueEventListener msg_listener=null;
    ArrayList<MsgPPL> msgPPLS;
    static  public  String server_key;
    int shown_md_num=0;
    int tot_msg_ppl;
    MessageDialogAdapter mg_dg_adapter;
    View btnView;
    //////////////////////
    ConnectionAdapter connectionAdapter=null;
    StickyListHeadersListView con_list=null;

    @SuppressLint("WrongViewCast") ListView mg_dg_View;
    @SuppressLint("ValidFragment")
    public TabFragment(Context context,String server_key)
    {
        super.onAttach(context);
        this.context=context;
        this.server_key=server_key;
        Log.e("TABFRag",server_key+" is server key");

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
        Log.e("CREATEVIEW",server_key+" is server key");

        btm_nav.setSelectedIndex(2,true);
        btnView=view;

        context=getContext();
        tabcontainer=view.findViewById(R.id.tabscontainer);
        // Construct a GeoDataClient.
        //mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        connectionAdapter=new ConnectionAdapter();
        SharedPreferences pref = getContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        uid=pref.getString("uid", "");
        setActivtiyTab();

        Log.e("UID value","uid="+uid);
        setConnection();
        // Construct a PlaceDetectionClient.
      //  mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);

        btm_nav.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(int i, int i1, boolean b) {
                Toast.makeText(context,"Activity",Toast.LENGTH_LONG);
                if(btm_nav.getSelectedIndex()==2) {
                    unseen_adapted = false;
                    setActivtiyTab();
                    curr_tab=2;
                    Toast.makeText(context, "Activity", Toast.LENGTH_LONG);
                }
                else if(btm_nav.getSelectedIndex()==1) {
                    Toast.makeText(context, "Chat", Toast.LENGTH_LONG);
                   // seen_adapted = false;
                    curr_tab=1;
                    setMsgTab();
                }
                else if(btm_nav.getSelectedIndex()==0) {
                    Toast.makeText(getContext(), "Contact", Toast.LENGTH_LONG);
                    curr_tab=0;
                   // seen_adapted = false;
                    setPplTab();
                }
            }

            @Override
            public void onMenuItemReselect(int i, int i1, boolean b) {

            }
        });
        return view;
    }

    void retrieveConnections()
    {
        Runnable runnable = new Runnable() {
            public void run() {

            }

        };
        Thread mythread = new Thread(runnable);
        mythread.start();

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

                Intent intent=new Intent(getContext(),CreateActivity.class);
                Log.e("CREATEACT",server_key+" is server key");
                intent.putExtra("ServerKey",server_key);
                startActivity(intent);
            }
        });
        //  setActFeatureButton();
        //   setTabsSetActivity();

          setUsersActivity(inflatedLayout);
    }

      void setUsersActivity(View inf_layout)
      {
          act_nonseen_list=(ListView)inf_layout.findViewById(R.id.unseen_activity_feed);
          if(act_feed==null)
            act_feed=(LinearLayout)inf_layout.findViewById(R.id.act_lin_lyt);
          else
            act_feed.removeAllViews();
          feedListen();
      }

    void feedListen()
    {

        DatabaseReference act_feed_ref = FirebaseDatabase.getInstance().getReference();

        act_feed_ref.child("Users").child(uid).child("activity_feed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String act_yet_to_seen=dataSnapshot.child("activity_yet_to_seen").getValue().toString();
                int num_act=0;
                act_non_feed=new ArrayList<ActFeed>();
                adaptActFeed(act_yet_to_seen);
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
         /*   Log.v("SET DEST","yoyoyo");
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
            */
        }
    }

    void adaptActFeed(String actFeed)
    {
        while(!actFeed.equals(":"))
        {
            String a_uid=actFeed.substring(1,actFeed.indexOf('.'));
            String act_content=actFeed.substring(3,actFeed.substring(1).indexOf(':')+1);
            actFeed=actFeed.substring(actFeed.substring(1).indexOf(':')+1);
            Log.v("Adapt Act","act_uid="+a_uid+"  act_content="+act_content);
            act_non_feed.add(new ActFeed("."+act_content+".",a_uid,context,server_key));
        }

        FragmentActivity activity = getActivity();
        if(activity != null)
        {
          //  actUnSeenAdapter.getData(act_non_feed, getContext(), getChildFragmentManager(),server_key);
           /* if (unseen_adapted)
            {
                  actUnSeenAdapter.notifyDataSetChanged();
            }
            else
            {
                  unseen_adapted = true;
                  act_nonseen_list.setAdapter(actUnSeenAdapter);
            }*/
           for(final ActFeed act:act_non_feed) {
               final MapActivitiesPageAdapter pg_adapter=new MapActivitiesPageAdapter(getChildFragmentManager(),act.a_uid,server_key);
               View view = getLayoutInflater().inflate(R.layout.act_feed_item, null);
               final ExpandableLayout act_views = view.findViewById(R.id.act_expand_layout);
               act_views.setExpanded(false);
               final ViewPager viewPager = view.findViewById(R.id.act_page);
               TextView name = view.findViewById(R.id.act_name);
               name.setText(act.name);
               View lin_lyt = (LinearLayout) view.findViewById(R.id.lin_lyt);
               lin_lyt.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       if (!act_views.isExpanded()) {
                           act_views.expand();
                               pg_adapter.setData(act.activities);
                               viewPager.setAdapter(pg_adapter);
                       } else
                           act_views.collapse();
                   }
               });
               act_feed.addView(view);
           }
        }
    }

    @SuppressLint("WrongViewCast")
    void setMsgTab()
    {
        shown_md_num=0;
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayout= inflater.inflate(R.layout.msg_dialog_list, null, false);
        tabcontainer.removeAllViews();
        tabcontainer.addView(inflatedLayout);
        mg_dg_View=(ListView) inflatedLayout.findViewById(R.id.msg_d_list);
        mg_dg_adapter=new MessageDialogAdapter(getContext());

        if(!new MessageDBHelper(getContext(),server_key).checkTable())
            new MessageDBHelper(getContext(),server_key).createTable();
        ArrayList<Message> msgDlgs=new MessageDBHelper(getContext(),server_key).getMessageDialogs();

        if(msgDlgs.size()==0)
        {
            noMsgYet(true,inflatedLayout);
        }
        else {
            noMsgYet(false, inflatedLayout);
            setMsgDialogs(msgDlgs);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MessageService.MY_ACTION);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(ConnectionService.MY_ACTION);

        getContext().registerReceiver(updateDlgsBCR, intentFilter);
   //     getContext().registerReceiver(updateConnection, intentFilter2);

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


    void setMsgDialogs(ArrayList<Message> msgDlgs)
    {
          if(!new PeopleDBHelper(getContext(),server_key).checkTable())
            new PeopleDBHelper(getContext(),server_key).createTable();

            msgPPLS=new ArrayList<MsgPPL>();
            MsgPPL nextppl;
            for(Message newone:msgDlgs)
            {
                 nextppl=new MsgPPL();
                 nextppl.sender_id=newone.getSender_id();
                 nextppl.name=getOtherUsername(newone.getMsg_id());
                 nextppl.last_msg=newone.getText();
                 nextppl.date=newone.getCreatedAt();
                 msgPPLS.add(nextppl);
                 Log.e("INSIDE MSGDLGS LOOP","senderid="+nextppl.sender_id+"name="+nextppl.name+"lastmsg="+nextppl.last_msg);
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

    String getOtherUsername(String msg_id)
    {
        String name;
        if(msg_id.substring(4,msg_id.indexOf(":")).equals(uid))
        {
            name= new PeopleDBHelper(getContext(),server_key)
                    .getName(msg_id.substring(msg_id.indexOf(":")+5));
        }
        else
        {
            name= new PeopleDBHelper(getContext(),server_key)
                    .getName(msg_id.substring(4,msg_id.indexOf(":")));
        }
        return name;
    }

    void setPplTab()
    {
        Log.e("PPLTAB","SETPPLTAB");
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayout= inflater.inflate(R.layout.people_list, null, false);
        final SwipeRefreshLayout pullToRefresh = (SwipeRefreshLayout) inflatedLayout.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ContactSync().syncContact(context,uid);
                setConnectionList();

            }
        });
        current_layout=inflatedLayout;
        tabcontainer.removeAllViews();
        tabcontainer.addView(inflatedLayout);
        con_list=(StickyListHeadersListView) inflatedLayout.findViewById(R.id.ppl_list);
        setConnectionList();
    }


    ////meekcons=1  activitycon=2   loc_con=3   act_sent_rqst=4    act_rcv_rqst=5      loc_rcv_rqst=6  act_acc_key=7
    void setConnection()
    {
        if(!new PeopleDBHelper(getContext(),server_key).checkTable())
            new PeopleDBHelper(getContext(),server_key).createTable();
        final DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();
        ppl_ref.child("Users").child(uid).child("Connections").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                ArrayList<String> meek_cons=dSnapshotExtractor(dataSnapshot.child("con_meek"));
                ArrayList<String> activity_cons=dSnapshotExtractor(dataSnapshot.child("activity_meek"));
                ArrayList<String> loc_cons=dSnapshotExtractor(dataSnapshot.child("location_meek"));
                ArrayList<String> sent_req_cons=dSnapshotExtractor(dataSnapshot.child("act_request_sent"));
                ArrayList<String> rcv_req_cons=dSnapshotExtractor(dataSnapshot.child("act_request_received"));
                ArrayList<String> act_acc_key=dSnapshotExtractor(dataSnapshot.child("act_acc_key"));

                ////meekcons=1  activitycon=2   loc_con=3   act_sent_rqst=4    act_rcv_rqst=5      loc_rcv_rqst=6  act_acc_key=7
                ////meekcons=2,1  activitycon=3,2   loc_con=4,3   act_sent_rqst=1,4    act_rcv_rqst=5      loc_rcv_rqst=6

                new PeopleDBHelper(getContext(),server_key);
                //if(meek_cons!=null)
                for(final String id:meek_cons)
                {
                    Log.e("MEEK CONS","id="+id);
                    checkInsertPerson(id,2);
                }
                ///////////
               // if(activity_cons!=null)
                for(final String id:activity_cons)
                {
                    Log.e("ACTIVITY CONS","id="+id);
                    checkInsertPerson(id,3);
                }

             //   if(loc_cons!=null)
                for(final String id:loc_cons)
                {
                    Log.e("LOC CONS","id="+id);
                    checkInsertPerson(id,4);
                }

              //  if(sent_req_cons!=null)
                for(final String id:sent_req_cons)
                {
                    Log.e("ACT RQ SNT","id="+id);
                    checkInsertPerson(id,1);
                }

            //    if(rcv_req_cons!=null)
                for(final String id:rcv_req_cons)
                {
                    Log.e("ACT RQ SNT","id="+id);
                    checkInsertPerson(id,5);
                }

            //    if(act_acc_key!=null)
                for(final String id:act_acc_key)
                {
                    Log.e("ACT KEY RTV","id="+id);
                    checkInsertPerson(id,3);
                    getEncKey(id);
                }

                 setConnectionList();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void checkInsertPerson(final String id, final int con_stat)
    {
        final DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();
        if(!(new PeopleDBHelper(getContext(),server_key).checkUID(id)))
        {
            Log.e("Conn activity setting","current id="+id);
            ppl_ref.child("Users").child(id).child("Info").child("phno").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String phnm=dataSnapshot.getValue().toString();
                    if(phnm!=null){
                        final DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();
                        ppl_ref.child("NAMES").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name =dataSnapshot.getValue().toString();
                                new PeopleDBHelper(getContext(),server_key).insertPerson(id,name,phnm,con_stat);
                                new PeopleDBHelper(getContext(),server_key).updateName(name,id);
                                Log.e("USERNAME","inside dbref name= "+name);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if(!(new PeopleDBHelper(getContext(),server_key).checkUID(id,con_stat)))
        {
            new PeopleDBHelper(getContext(),server_key).changePersonStatus(id,con_stat);
        }
    }

    void getEncKey(String id)
    {
        DatabaseReference key_ref = FirebaseDatabase.getInstance().getReference();
        key_ref.child("Key_Exchange").child(uid).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_key=dataSnapshot.getValue().toString();
                Log.e("Key SHOT","dB Key="+user_key);
                PrivateKey myPrivateKey=new RSAKeyExchange(context,uid).myPrivateKey();
                Log.e("PRIVATE KEY","myprivatekey="+Base64.encodeToString(myPrivateKey.getEncoded(),Base64.NO_WRAP)+"  privatekeyformat="+myPrivateKey.getFormat());
                try {
                    String str_real_key=new RSAKeyExchange(context,uid).decrypt(myPrivateKey,user_key);

                    if(new PeopleDBHelper(context,server_key).checkUID(dataSnapshot.getKey()))
                        new PeopleDBHelper(context,server_key).updateEncKeyPerson(dataSnapshot.getKey(),new AES().encrypt(str_real_key,server_key));

                    Log.e("ACT RQ AC","KEY IS="+str_real_key);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ERROR STUCKED","ERROR IS="+e);

                }
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
    public static ArrayList<String> dSnapshotExtractor(DataSnapshot all_uid)
    {
        ArrayList<String> uids=new ArrayList<String>() ;

        if(all_uid.exists())
        for(DataSnapshot ds:all_uid.getChildren())
        {
            uids.add(ds.getKey());
        }

        return uids;
    }

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
        Log.e("PPLTAB","SETCONNECTIONLIST1");
        ArrayList<Contact> conn_list=new ArrayList<Contact>();
        if(!new PeopleDBHelper(getContext(),server_key).checkTable())
            new PeopleDBHelper(getContext(),server_key).createTable();

        conn_list=new PeopleDBHelper(context,server_key).getAllConnections();
        Log.e("PPLTAB","SETCONNECTIONLIST2");

        connectionAdapter.getData(conn_list,getContext(),uid,server_key);
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

            setConnectionList();

        }
    };

    BroadcastReceiver updateConnection = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle bundle = intent.getExtras();
            String sender_id = bundle.getString("sender_id");
            String uid = bundle.getString("uid");
            ArrayList<Message> msgDlgs=new MessageDBHelper(getContext(),server_key).getMessageDialogs();
            setMsgDialogs(msgDlgs);
        }
    };
}


class ConnectionAdapter extends BaseAdapter implements StickyListHeadersAdapter
{
    ArrayList<Contact> conn_ppl;
    Context context;
    String uid,serverkey;
    void getData(ArrayList<Contact> conn_ppl,Context context,String id,String serverkey)
    {
        this.serverkey=serverkey;
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

    ////meekcons=2,1  activitycon=3,2   loc_con=4,3   act_sent_rqst=1,4    act_rcv_rqst=5      loc_rcv_rqst=6
    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.e("CONN ADAPTER","NAME:"+conn_ppl.get(i).getName()+"   UID:"+conn_ppl.get(i).getUID());
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch(conn_ppl.get(i).conn_level)
        {
            case 6: view = inflater.inflate(R.layout.request_list_item, null);
                final CircleImageView ok=(CircleImageView)view.findViewById(R.id.rq_okay);
                final CircleImageView cancl=(CircleImageView)view.findViewById(R.id.rq_cancel);
                ok.setTag(conn_ppl.get(i).getUID());
                cancl.setTag(conn_ppl.get(i).getUID());
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

                        DatabaseReference data_ref = FirebaseDatabase.getInstance().getReference();
                        data_ref.child("Users")
                                .child(uid)
                                .child("Connections")
                                .child("location_meek")
                                .child(view.getTag().toString()).setValue("key");

                        data_ref.child("Users")
                                .child(view.getTag().toString())
                                .child("Connections")
                                .child("location_meek")
                                .child(uid).setValue("key");

                        data_ref.child("Users")
                                .child(view.getTag().toString())
                                .child("Connections")
                                .child("loc_request_sent")
                                .child(uid).removeValue();

                        data_ref.child("Users")
                                .child(uid)
                                .child("Connections")
                                .child("loc_request_received")
                                .child(view.getTag().toString()).removeValue();

                        new PeopleDBHelper(context,serverkey).changePersonStatus(view.getTag().toString(),4);
                        MainActivity ma=(MainActivity) context ;
                        ma.tabFragment.setConnectionList();

                    }
                });

                cancl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final DatabaseReference trigger_ref = FirebaseDatabase.getInstance().getReference();

                        final DatabaseReference data_ref = FirebaseDatabase.getInstance().getReference();

                        data_ref.child("Users")
                                .child(view.getTag().toString())
                                .child("Connections")
                                .child("loc_request_sent")
                                .child(uid).removeValue();

                        data_ref.child("Users")
                                .child(uid)
                                .child("Connections")
                                .child("loc_request_received")
                                .child(view.getTag().toString()).removeValue();



                        new PeopleDBHelper(context,serverkey).changePersonStatus(view.getTag().toString(),3);
                        MainActivity ma=(MainActivity) context ;
                        ma.tabFragment.setConnectionList();
                    }
                });
                break;

            case 5: view = inflater.inflate(R.layout.request_list_item, null);
                    final CircleImageView okay=(CircleImageView)view.findViewById(R.id.rq_okay);
                    final CircleImageView cancel=(CircleImageView)view.findViewById(R.id.rq_cancel);
                    okay.setTag(conn_ppl.get(i).getUID());
                    cancel.setTag(conn_ppl.get(i).getUID());
                    okay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            try {
                                DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();
                                String file_name=view.getTag().toString()+".pub";
                                final String u_id=view.getTag().toString();
                                final File localFile;
                                try {
                                    localFile = File.createTempFile(file_name, "pub");
                                    ppl_ref.child("PublicKey").child(view.getTag().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            PublicKey pubkey=null;
                                            try{
                                                byte[] byteKey = Base64.decode(dataSnapshot.getValue().toString().getBytes(), Base64.NO_WRAP);
                                                X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
                                                KeyFactory kf = KeyFactory.getInstance("RSA");
                                                pubkey=kf.generatePublic(X509publicKey);

                                                SharedPreferences getPref=context.getSharedPreferences("USERKEY",MODE_PRIVATE);
                                                final String key=new AES().decrypt(getPref.getString("KEY",""),serverkey);
                                                Log.e("AES KEY","The KEY="+key);
                                                String encrypted = new RSAKeyExchange(context,uid).encrypt(pubkey, key);     ///set the key in it
                                                Log.e("AES KEY","The KEY after encryption="+encrypted);
                                                DatabaseReference key_ref = FirebaseDatabase.getInstance().getReference();
                                                key_ref.child("Key_Exchange").child(view.getTag().toString()).child(uid).setValue(encrypted);

                                                final DatabaseReference data_ref = FirebaseDatabase.getInstance().getReference();
                                                data_ref.child("Users")
                                                        .child(uid)
                                                        .child("Connections")
                                                        .child("activity_meek")
                                                        .child(view.getTag().toString()).setValue("key");

                                                data_ref.child("Users")
                                                        .child(view.getTag().toString())
                                                        .child("Connections")
                                                        .child("activity_meek")
                                                        .child(uid).setValue("key");
                                                data_ref.child("Users")
                                                        .child(view.getTag().toString())
                                                        .child("Connections")
                                                        .child("act_acc_key")
                                                        .child(uid).setValue("key");


                                                data_ref.child("Users")
                                                        .child(view.getTag().toString())
                                                        .child("Connections")
                                                        .child("act_request_sent")
                                                        .child(uid).removeValue();

                                                data_ref.child("Users")
                                                        .child(uid)
                                                        .child("Connections")
                                                        .child("act_request_received")
                                                        .child(view.getTag().toString()).removeValue();


                                                new PeopleDBHelper(context,serverkey).changePersonStatus(view.getTag().toString(),3);
                                                MainActivity ma=(MainActivity) context ;
                                                ma.tabFragment.setConnectionList();
                                                ma.tabFragment.getEncKey(view.getTag().toString());
                                            } catch (NoSuchAlgorithmException e) {
                                                e.printStackTrace();
                                            } catch (InvalidKeySpecException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final DatabaseReference trigger_ref = FirebaseDatabase.getInstance().getReference();

                            final DatabaseReference data_ref = FirebaseDatabase.getInstance().getReference();


                            data_ref.child("Users")
                                    .child(view.getTag().toString())
                                    .child("Connections")
                                    .child("act_request_sent")
                                    .child(uid).removeValue();

                            data_ref.child("Users")
                                    .child(uid)
                                    .child("Connections")
                                    .child("act_request_received")
                                    .child(view.getTag().toString()).removeValue();


                            new PeopleDBHelper(context,serverkey).changePersonStatus(view.getTag().toString(),2);
                            MainActivity ma=(MainActivity) context ;
                            ma.tabFragment.setConnectionList();
                        }
                    });
                    break;

            case 4:  view = inflater.inflate(R.layout.viewer_list_item, null);
                     view.setTag(conn_ppl.get(i).getUID());
                     view.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view)
                         {
                                Intent intent=new Intent(context,UserProfile.class);
                                intent.putExtra("ServerKey",serverkey);
                                intent.putExtra("Stat","LOC");
                                intent.putExtra("r_uid",view.getTag().toString());
                                context.startActivity(intent);
                         }
                     });
                    break;
                    ///Location connected


            case 3: view = inflater.inflate(R.layout.viewer_list_item, null);
                    view.setTag(conn_ppl.get(i).getUID());

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            Intent intent=new Intent(context,UserProfile.class);
                            intent.putExtra("ServerKey",serverkey);
                            intent.putExtra("Stat","ACT");
                            intent.putExtra("r_uid",view.getTag().toString());
                            context.startActivity(intent);
                        }
                    });
                    break;

            case 2: /*view = inflater.inflate(R.layout.viewer_list_item, null);
                    view.setBackgroundResource(R.drawable.bg_conmeek);*/
                    view = inflater.inflate(R.layout.meek_con_item, null);
                    final CircleImageView btn=(CircleImageView)view.findViewById(R.id.add);
                    btn.setTag(conn_ppl.get(i).getUID());
                    btn.setTag(R.integer.stat,"0");
                    btn.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.DONUT)
                        @Override
                        public void onClick(final View view) {
                            final String usr_id=view.getTag().toString();
                            if(view.getTag(R.integer.stat).toString().equals("0"))
                            {
                                SharedPreferences getPref=context.getSharedPreferences("USERKEY",MODE_PRIVATE);
                                final String key=new AES().decrypt(getPref.getString("KEY",""),serverkey);

                                final DatabaseReference data_ref = FirebaseDatabase.getInstance().getReference();
                                data_ref.child("Users")
                                        .child(uid)
                                        .child("Connections")
                                        .child("act_request_sent")
                                        .child(view.getTag().toString()).setValue("key");

                                data_ref.child("Users")
                                        .child(view.getTag().toString())
                                        .child("Connections")
                                        .child("act_request_received")
                                        .child(uid).setValue("key");

                                btn.setTag(R.integer.stat,"1");
                                btn.setImageResource(R.drawable.cancel_cross);
                                final DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();
                                String file_name=view.getTag().toString()+".pub";
                                final String u_id=view.getTag().toString();
                                final File localFile;
                                ppl_ref.child("PublicKey").child(view.getTag().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.DONUT)
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        PublicKey pubkey=null;
                                        try{
                                            byte[] byteKey = Base64.decode(dataSnapshot.getValue().toString().getBytes(), Base64.NO_WRAP);
                                            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
                                            KeyFactory kf = KeyFactory.getInstance("RSA");
                                            pubkey=kf.generatePublic(X509publicKey);
                                            Log.e("PUBLICKEY","Retrieved pkey="+Base64.encodeToString(pubkey.getEncoded(),Base64.NO_WRAP));
                                            SharedPreferences getPref=context.getSharedPreferences("USERKEY",MODE_PRIVATE);
                                            final String key=new AES().decrypt(getPref.getString("KEY",""),serverkey);
                                            Log.e("AES KEY","The KEY="+key);
                                            String encrypted = new RSAKeyExchange(context,uid).encrypt(pubkey, key);     ///set the key in it
                                            Log.e("AES KEY","The KEY after encryption="+key);
                                            DatabaseReference key_ref = FirebaseDatabase.getInstance().getReference();
                                            key_ref.child("Key_Exchange").child(u_id).child(uid).setValue(encrypted);
                                            new PeopleDBHelper(context,serverkey).changePersonStatus(u_id,1);
                                            view.setTag(R.integer.stat,"1");

                                        } catch (NoSuchAlgorithmException e) {
                                            e.printStackTrace();
                                        } catch (InvalidKeySpecException e) {
                                            e.printStackTrace();
                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                            else
                            {
                                final DatabaseReference data_ref = FirebaseDatabase.getInstance().getReference();
                                data_ref.child("Users")
                                        .child(uid)
                                        .child("Connections")
                                        .child("act_request_sent")
                                        .child(view.getTag().toString()).removeValue();

                                data_ref.child("Users")
                                        .child(view.getTag().toString())
                                        .child("Connections")
                                        .child("act_request_received")
                                        .child(uid).removeValue();


                                btn.setTag(R.integer.stat,"0");
                                new PeopleDBHelper(context,serverkey).changePersonStatus(view.getTag().toString(),1);
                                view.setTag(R.integer.stat,"0");
                                btn.setImageResource(R.drawable.add_img);
                            }
                        }
                    });
                        break;
            case 1: ///
                    view = inflater.inflate(R.layout.meek_con_item, null);
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

                                            new PeopleDBHelper(context,serverkey).changePersonStatus(usr_id,2);
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
                   ////NEED TO FIX THINGS HERE
                    });
                    break;
                    default:return null;

        }
        ////meekcons=1  activitycon=2   loc_con=3   act_sent_rqst=4    act_rcv_rqst=5

        TextView name=(TextView) view.findViewById(R.id.name);
        if(conn_ppl.get(i).getName()==null)
        {
            String u_name=new PeopleDBHelper(context,serverkey).getName(conn_ppl.get(i).getUID());
            name.setText(u_name);

            Log.e("LISTNAME","Inside if the name is="+u_name+" uid="+conn_ppl.get(i).getUID());
        }
        else
        {
            name.setText(conn_ppl.get(i).getName());
            Log.e("LISTNAME","outside if the name is="+conn_ppl.get(i).getName());
        } return view;
    }
    ////meekcons=2,1  activitycon=3,2   loc_con=4,3   act_sent_rqst=1,4    act_rcv_rqst=5      loc_rcv_rqst=6

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.viewer_list_header, null);
        TextView name=(TextView) view.findViewById(R.id.header);
        switch(conn_ppl.get(position).conn_level)
        {
            case 6:
            case 5: name.setText("Requests");
                    view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.meek_loccon) );
                    break;
            case 4:
            case 3: name.setText("Connections");
                    view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.meek_loccon) );
                    break;
            case 2: name.setText("Contacts");
                    view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.meek_actcon) );
                    break;
            case 1: view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_conmeek) );
                    name.setText("Sent Requests");
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
    String name;
    ArrayList<Activities> activities;


    ActFeed(String act_content,String a_uid,Context context,String serverkey)
    {
        activities=new ArrayList<Activities>();
        this.a_uid=a_uid;
        name=new PeopleDBHelper(context,serverkey).getName(a_uid);
        int num=0;
        while(!act_content.equals("."))
        {
            Log.v("IN ACTFEED","uid="+a_uid+"  act_id=");
            Activities newone= new Activities();
            newone.act_id=act_content.substring(1,act_content.substring(1).indexOf('.')+1);
            Log.v("IN ACTFEED","uid="+a_uid+"  act_id="+newone.act_id);
            act_content=act_content.substring(act_content.substring(1).indexOf('.')+1);
            if(!newone.act_id.equals(""))
                activities.add(newone);
        }
    }

}

