package com.meek;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Calendar;

/**
 * Created by User on 09-Jun-18.
 */

public class MessageListMaker extends AppCompatActivity
{
    MessagesListAdapter<Message> adapter;
    String uid,r_uid,msg_id;
    int upr_bound=0,lwr_bound=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_list);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);

        uid=pref.getString("uid", "");
        Intent intentExtra= getIntent();
        Bundle bundle=intentExtra.getExtras();
        r_uid=bundle.getString("r_uid");
        msg_id=msgID(uid,r_uid);

        MessagesList messagesList=findViewById(R.id.messagesList);
        adapter = new MessagesListAdapter<>("1", null);
        messagesList.setAdapter(adapter);

        getInfo(msg_id);
    }

    String msgID(String uid,String r_uid)
    {
        if(Integer.parseInt(uid)<Integer.parseInt(r_uid))
            return  "user"+uid+":user"+r_uid;
        else
            return "user"+r_uid+":user"+uid;

    }

    void getInfo(String msg_id)
    {
        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();

        db_ref.child("Messages_DB").child(msg_id).child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                    String allMsgs= dataSnapshot.child("uid"+uid+"_msgs").getValue().toString();
                    int msgNum=Integer.parseInt(dataSnapshot.child("tot_msgs").getValue().toString());

                    if(upr_bound==0)
                    {
                        if(msgNum<11)
                        {
                            upr_bound=lwr_bound=1;
                        }
                        else
                        {
                            upr_bound=lwr_bound=msgNum-10;
                        }
                    }
                    getMessages(allMsgs,msgNum);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getMessages(String allMsgs,int msgNum)
    {
        boolean got_msg=false;
        while(lwr_bound<msgNum)
        {
            if(allMsgs.contains(":"+lwr_bound+":"))     ///logic to add for the sent messages
            {
                got_msg=true;
                DatabaseReference msg_ref = FirebaseDatabase.getInstance().getReference();
                msg_ref.child("Messages_DB").child(msg_id).child("Messages").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                            String msgID=dataSnapshot.child("msg_num").getValue().toString();
                            String msgText=dataSnapshot.child("msg_text").getValue().toString();

                            String msgSenderID=dataSnapshot.child("msg_sender_uid").getValue().toString();
                            String msgSave=dataSnapshot.child("msg_text").getValue().toString();

                            String msgDate=dataSnapshot.child("msg_date_time").getValue().toString();

                            // decrypt the msgText
                            //date format conversion

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        }
        if(!got_msg)
        {
            if(msgNum<11)
            {
                upr_bound=lwr_bound=1;
            }
            else
            {
                upr_bound=lwr_bound=msgNum-10;
            }
            getMessages(allMsgs,msgNum);
        }


        Message one = new Message("1", "pmd is cool", "pmd", Calendar.getInstance().getTime());
        adapter.addToStart(one, true);
    }

    void setSendMessage()
    {
        MessageInput inputView=(MessageInput)findViewById(R.id.input);
        inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                //validate and send message
     //        adapter.addToStart(message, true);
                return true;
            }
        });

    }

}
