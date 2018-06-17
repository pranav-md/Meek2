package com.meek;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Pranav on 12-Jun-18.
 */

public class MsgPPL
{
    String r_uid,name,last_msg,my_uid,last_msg_id;
    int num_unread;

    MsgPPL(String msg_content,String my_uid)
    {
        this.my_uid=my_uid;
        int num=0;
        for( int i=0; i<msg_content.length(); i++ ) {
            if (msg_content.charAt(i) == '.') {
                num++;
            }
        }
        num-=1;
        num_unread=num;

            msg_content = msg_content.substring(1);
            int pos = msg_content.indexOf('.');
            if(pos==-1)
                pos=msg_content.length();
            r_uid = msg_content.substring(0, pos); //retrieved the r_uid
            Log.v("String check", "msg_content" + msg_content + "  r_uid:" + r_uid+"  numunread"+num_unread);
        if(num_unread>0)
        {
            pos = msg_content.lastIndexOf('.');
            last_msg_id = msg_content.substring(pos, msg_content.length() - 2);
        }
        else
        {
            last_msg="";
            num_unread=0;
        }
        //getName();

    }
    void get_lastMsg()
    {

        DatabaseReference msg_ref = FirebaseDatabase.getInstance().getReference();
        msg_ref.child("Messages_DB").child(msgID(my_uid,r_uid)).child("Messages").child(last_msg_id).child("msg_text").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                last_msg=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void getName()
    {
        DatabaseReference name_ref = FirebaseDatabase.getInstance().getReference();
        name_ref.child("Users").child(r_uid).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name=dataSnapshot.getValue().toString();
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

}
