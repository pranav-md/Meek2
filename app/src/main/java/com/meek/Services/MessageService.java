package com.meek.Services;

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
import com.meek.Messaging.MsgPPL;

import java.util.ArrayList;

import static com.meek.TabFragment.extractor;

/**
 * Created by User on 26-Mar-19.
 */

public class MessageService extends Service {

    String uid;
    ArrayList<MsgPPL> msgPPLS;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences pref = getSharedPreferences("UserDetails", MODE_PRIVATE);
        uid=pref.getString("uid", "");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void listenMSGCounter()
    {
        DatabaseReference msg_ref = FirebaseDatabase.getInstance().getReference();
        msg_ref.child("Users").child(uid).child("Message_counter").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    msgPPLS=new ArrayList<MsgPPL>();
                    String dg_msgs=dataSnapshot.getValue().toString();
                    parseMSGCTR(dg_msgs);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    void pullMessages(String r_id)
    {
        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();

        Log.e("Msglister","inside get info yoo");

        final String mg_id=msgID(uid,r_id);

        db_ref.child("Messages_DB").child(mg_id).child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()) {
                    Log.e("Msglister", "my uid" + uid + "   msg_id=" + mg_id);
                    String allMsgs = dataSnapshot.child("uid" + uid + "_msgs").getValue().toString();
                    parseMSG(allMsgs,mg_id);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    String msgID(String uid,String r_uid)
    {
        if(Integer.parseInt(uid)<Integer.parseInt(r_uid))
            return  "user"+uid+":user"+r_uid;
        else
            return "user"+r_uid+":user"+uid;
    }

    void parseMSGCTR(String ctr_ids)
    {
        int loop_num;
        ArrayList<String> ctr_id_list=extractor(ctr_ids);
        for(String id:ctr_id_list)
            pullMessages(id);

    }
    void parseMSG(String allMsgs,String msg_id)
    {

        while(allMsgs.indexOf("<Message>")!=-1)
        {
            String removetext=parseXML(parseXML(allMsgs,"AllMessages"),"Message");
            String text=parseXML(parseXML(parseXML(allMsgs,"AllMessages"),"Message"),"text" );
            String sender_id=parseXML(parseXML(parseXML(allMsgs,"AllMessages"),"Message"),"sender_id" );
            String timestamp=parseXML(parseXML(parseXML(allMsgs,"AllMessages"),"Message"),"timestamp" );
            new MessageDBHelper(getBaseContext()).insertMessage(msg_id,sender_id,text,timestamp);
            allMsgs=allMsgs.replace("<Message>"+removetext+"</Message>","");
        }

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
