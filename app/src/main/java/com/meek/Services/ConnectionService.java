package com.meek.Services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meek.Database.PeopleDBHelper;
import com.meek.MainActivity;

import java.util.ArrayList;

import static com.meek.TabFragment.extractor;

/**
 * Created by User on 28-Apr-19.
 */

public class ConnectionService extends Service {

    String server_key,uid;
    public static String MY_ACTION="connection";

    public ConnectionService()
    {
    //    super();

    }
    public ConnectionService(String name) {


    }

    @Override
    public void onCreate() {
        super.onCreate();

//        MainActivity main=(MainActivity)getBaseContext();
 //       server_key = main.server_key;

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        SharedPreferences mypref = getSharedPreferences("UserDetails", MODE_PRIVATE);
        uid=mypref.getString("uid","");

        server_key=intent.getStringExtra("ServerKey");
        setConnection();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    void setConnection()
    {
        try {
            if(!new PeopleDBHelper(this,server_key).checkTable())
                new PeopleDBHelper(this,server_key).createTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

               // new PeopleDBHelper(this,server_key);
                for(final String id:meek_cons)
                {
                    Log.e("MEEK CONS","id="+id);
                    if(!(new PeopleDBHelper(ConnectionService.this,server_key).checkUID(id)))
                    {
                        Log.e("Conn meek_con setting","current id="+id);
                        ppl_ref.child("Users").child(id).child("Info").child("phno").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                final String phnm=dataSnapshot.getValue().toString();
                                if(phnm!=null)
                                {
                                    String name=new PeopleDBHelper(ConnectionService.this,server_key).userName(id);
                                    new PeopleDBHelper(ConnectionService.this,server_key).insertPerson(id,name,phnm,1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else if(!(new PeopleDBHelper(ConnectionService.this,server_key).checkUID(id,1)))
                    {
                        new PeopleDBHelper(ConnectionService.this,server_key).changePersonStatus(id,1);
                    }
                }
                ///////////
                for(final String id:activity_cons)
                {
                    Log.e("ACTIVITY CONS","id="+id);
                    if(!(new PeopleDBHelper(ConnectionService.this,server_key).checkUID(id)))
                    {
                        Log.e("Conn activity setting","current id="+id);
                        ppl_ref.child("Users").child(id).child("Info").child("phno").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String phnm=dataSnapshot.getValue().toString();
                                if(phnm!=null){
                                    String name=new PeopleDBHelper(ConnectionService.this,server_key).userName(id);
                                    new PeopleDBHelper(ConnectionService.this,server_key).insertPerson(id,name,phnm,2);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else if(!(new PeopleDBHelper(ConnectionService.this,server_key).checkUID(id,2)))
                    {
                        new PeopleDBHelper(ConnectionService.this,server_key).changePersonStatus(id,2);
                    }
                }

                for(final String id:loc_cons)
                {
                    Log.e("LOC CONS","id="+id);
                    if(!(new PeopleDBHelper(ConnectionService.this,server_key).checkUID(id)))
                    {
                        Log.e("Checked loc CONS","id="+id);
                        ppl_ref.child("Users").child(id).child("Info").child("phno").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String phnm=dataSnapshot.getValue().toString();
                                if(phnm!=null){
                                    String name=new PeopleDBHelper(ConnectionService.this,server_key).userName(id);
                                    new PeopleDBHelper(ConnectionService.this,server_key).insertPerson(id,name,phnm,3);
                                }

                                Log.e("INSIDE datasnapshot",phnm+"_phone num retrieved");
                                //  setConnectionList();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else if(!(new PeopleDBHelper(ConnectionService.this,server_key).checkUID(id,3)))
                    {
                        new PeopleDBHelper(ConnectionService.this,server_key).changePersonStatus(id,3);
                    }
                }

                for(final String id:sent_req_cons)
                {
                    Log.e("ACT RQ SNT","id="+id);
                    if(!(new PeopleDBHelper(ConnectionService.this,server_key).checkUID(id)))
                    {
                        Log.e("Conn activity setting","current id="+id);
                        ppl_ref.child("Users").child(id).child("Info").child("phno").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String phnm = dataSnapshot.getValue().toString();
                                if (phnm != null) {
                                    String name=new PeopleDBHelper(ConnectionService.this,server_key).userName(id);
                                    new PeopleDBHelper(ConnectionService.this,server_key).insertPerson(id, name, phnm, 4);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else if(!(new PeopleDBHelper(ConnectionService.this,server_key).checkUID(id,4)))
                    {
                        new PeopleDBHelper(ConnectionService.this,server_key).changePersonStatus(id,4);
                    }
                }

                for(final String id:rcv_req_cons)
                {
                    Log.e("ACT RQ SNT","id="+id);
                    if(!(new PeopleDBHelper(ConnectionService.this,server_key).checkUID(id)))
                    {
                        Log.e("Conn activity setting","current id="+id);
                        ppl_ref.child("Users").child(id).child("Info").child("phno").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String phnm=dataSnapshot.getValue().toString();
                                if(phnm!=null){
                                    String name=new PeopleDBHelper(ConnectionService.this,server_key).userName(id);
                                    new PeopleDBHelper(ConnectionService.this,server_key).insertPerson(id,name,phnm,5);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else if(!(new PeopleDBHelper(ConnectionService.this,server_key).checkUID(id,5)))
                    {
                        new PeopleDBHelper(ConnectionService.this,server_key).changePersonStatus(id,5);
                    }
                }
                Intent intent = new Intent();
                intent.setAction(MY_ACTION);
                sendBroadcast(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
