package com.meek.Messaging;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meek.Database.MessageDBHelper;
import com.meek.Database.PeopleDBHelper;
import com.meek.Encryption.AES;

import static com.meek.TabFragment.extractor;

import java.util.ArrayList;

/**
 * Created by User on 08-Apr-19.
 */

public class MessageService extends Service
{
    String serverkey;
    public static String MY_ACTION="UPDATE_MSGS";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serverkey = intent.getStringExtra("ServerKey");
        Log.e("oncreate","MessageService");
        if(!new MessageDBHelper(this,serverkey).checkTable())
            new MessageDBHelper(this,serverkey).createTable();

        SharedPreferences userPrefs=getSharedPreferences("UserDetails",MODE_PRIVATE);
        String uid=userPrefs.getString("uid","");
        listenMsgs(uid);
        return super.onStartCommand(intent, flags, startId);
    }

    void listenMsgs(final String uid)
    {
        Log.e("oncreate","ListenMsgs");
        DatabaseReference msgctr_ref = FirebaseDatabase.getInstance().getReference();
        msgctr_ref.child("Users").child(uid).child("Message_counter").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null)
                    return;
                String msg_ctr=dataSnapshot.getValue().toString();
                ArrayList<String> msg_uid=extractor(msg_ctr);
                retrieveMsgs(msg_uid,uid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void retrieveMsgs(ArrayList<String> uids, final String uid)
    {
        MessageDBHelper msg_db=new MessageDBHelper(this,serverkey);
        msg_db.getWritableDatabase();
        for(final String id:uids)
        {
            Log.e("retrieveMsgs","messgages of"+id);
            final DatabaseReference msg_ref = FirebaseDatabase.getInstance().getReference();
            msg_ref.child("Messages_DB")
                    .child(msgID(uid,id))
                    .child("sent_msgs:"+id+":")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue()!=null)
                            {
                                String xml_msgs = dataSnapshot.getValue().toString();
                                Log.e("Retrieve msg","msg texts="+xml_msgs);
                                msg_ref.child("Messages_DB")
                                        .child(msgID(uid, id))
                                        .child("sent_msgs:" + id + ":").setValue("<AllMessages><Message></Message></AllMessages>");
                                while (xml_msgs.contains("<Message>") && (!xml_msgs.contains("<Message></Message>")))
                                {
                                    String msg_text = parseXML(parseXML(parseXML(xml_msgs, "AllMessages"), "Message"), "text");
                                    String msg_date = parseXML(parseXML(parseXML(xml_msgs, "AllMessages"), "Message"), "timestamp");
                                    Log.e("MESGSS DPLAY","MSG_TEXT="+msg_text+"  MSG_DATE="+msg_date);
                                    Log.e("MESGSS DPLAY","MSG_TE KEY IS="+new PeopleDBHelper(MessageService.this,serverkey).getEncKey(id));
                                    new MessageDBHelper(MessageService.this,serverkey).insertMessage(msgID(uid, id), id,new AES().decrypt( msg_text,new PeopleDBHelper(MessageService.this,serverkey).getEncKey(id)), msg_date);
                                    xml_msgs = xml_msgs.replace("<Message>" + parseXML(parseXML(xml_msgs, "AllMessages"), "Message") + "</Message>", "");
                                }
                                Intent intent = new Intent();
                                intent.setAction(MY_ACTION);
                                intent.putExtra("uid", uid);
                                intent.putExtra("sender_id", id);
                                sendBroadcast(intent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


        }

    }
    String msgID(String uid, String r_uid)
    {
        if(Integer.parseInt(uid)<Integer.parseInt(r_uid))
            return  "user"+uid+":user"+r_uid;
        else
            return "user"+r_uid+":user"+uid;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public String parseXML(String source, String tag){
        int flag=0;
        int startIndex = source.indexOf("<"+tag+">") + ("<"+tag+">").length();
        int endIndex = source.indexOf("</"+tag+">");
        if(startIndex ==-1 || endIndex==-1)
            return null;
        String out = source.substring(startIndex,endIndex);
        return out;
    }

    public void onDestroy()
    {
        startService(new Intent(this,MessageService.class));
        Log.e("service started","destroy msgservice");
    }


    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        startService(new Intent(this,MessageService.class));
        Log.e("service started","destroy msgservice");
    }
}
