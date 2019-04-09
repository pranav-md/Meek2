package com.meek.Messaging;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meek.Database.MessageDBHelper;

import java.util.ArrayList;

/**
 * Created by User on 08-Apr-19.
 */

public class MessageService extends Service
{

    public static String MY_ACTION="UPDATE_MSGS";


    @Override
    public void onCreate()
    {
            SharedPreferences userPrefs=getSharedPreferences("UserDetails",MODE_PRIVATE);
            String uid=userPrefs.getString("uid","");
            listenMsgs(uid);
    }

    void listenMsgs(final String uid)
    {
        DatabaseReference msgctr_ref = FirebaseDatabase.getInstance().getReference();
        msgctr_ref.child("Users").child(uid).child("Message_counter").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
        for(final String id:uids)
        {
            DatabaseReference msg_ref = FirebaseDatabase.getInstance().getReference();
            msg_ref.child("Messages_DB")
                    .child(msgID(uid,id))
                    .child("Messages")
                    .child("sent_msgs:"+id+":")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String xml_msgs=dataSnapshot.getValue().toString();
                            while(xml_msgs.contains("<Message>"))
                            {
                                String msg_text = parseXML(parseXML(parseXML(xml_msgs, "AllMessages"), "Message"), "text");
                                String msg_date = parseXML(parseXML(parseXML(xml_msgs, "AllMessages"), "Message"), "date");
                                new MessageDBHelper(MessageService.this).insertMessage(msgID(uid, id), id, msg_text, msg_date);
                                xml_msgs.replace("<Message>" + parseXML(parseXML(xml_msgs, "AllMessages"), "Message") + "</Message>", "");
                            }
                            Intent intent = new Intent();
                            intent.setAction(MY_ACTION);
                            intent.putExtra("uid",uid);
                            intent.putExtra("sender_id",id);
                            sendBroadcast(intent);
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

    public String parseXML(String source, String tag){
        int flag=0;
        int startIndex = source.indexOf("<"+tag+">") + ("<"+tag+">").length();
        int endIndex = source.indexOf("</"+tag+">");
        if(startIndex ==-1 || endIndex==-1)
            return null;
        String out = source.substring(startIndex,endIndex);
        return out;
    }


}
