package com.meek.Messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meek.Database.MessageDBHelper;
import com.meek.Database.PeopleDBHelper;
import com.meek.R;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by User on 09-Jun-18.
 */

public class MessageListMaker extends AppCompatActivity
{
    ArrayList<Message> msgList;
    String uid,r_uid,msg_id;
    int upr_bound=0,lwr_bound=0;
    ListView messagesList;
    MessageListAdapter messageListAdapter=null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_list);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MessageService.MY_ACTION);
        registerReceiver(updateMsgsBCR, intentFilter);

        msgList=new ArrayList<Message>();
        uid=pref.getString("uid", "");
        Intent intentExtra= getIntent();
        Bundle bundle=intentExtra.getExtras();
        r_uid=bundle.getString("r_uid");
        String name=bundle.getString("name");
        msg_id=msgID(uid,r_uid);

        messagesList=findViewById(R.id.messagesList);

        getMSGS(msg_id);
        setTop(name);
    }
    void setTop(String name)
    {
        TextView nm=(TextView)findViewById(R.id.name);
        nm.setText(name);
    }
    void getMSGS(String msg_id)
    {
        msgList=new ArrayList<Message>();
        Cursor allMsgs=new MessageDBHelper(this).getMessages(msg_id);
        allMsgs.moveToFirst();
        Message newone=new Message(allMsgs.getString(0),
                                   allMsgs.getString(1),
                                   new PeopleDBHelper(this).getName(allMsgs.getString(0)),
                                    allMsgs.getString(2));
        msgList.add(newone);
        while(allMsgs.moveToNext())
        {
            newone=new Message(allMsgs.getString(0),
                    allMsgs.getString(1),
                    new PeopleDBHelper(this).getName(allMsgs.getString(0)),
                    allMsgs.getString(2));
            msgList.add(newone);
        }
        if(messageListAdapter==null)
        {
            messageListAdapter=new MessageListAdapter(this,msgList,uid);
            messagesList.setAdapter(messageListAdapter);
        }
        else
        {
            messageListAdapter.getData(msgList);
            messageListAdapter.notifyDataSetChanged();
        }
    }

    String msgID(String uid,String r_uid)
    {
        if(Integer.parseInt(uid)<Integer.parseInt(r_uid))
            return  "user"+uid+":user"+r_uid;
        else
            return "user"+r_uid+":user"+uid;
    }

    void noMsgYet(boolean flg)
    {
        if(flg)
            findViewById(R.id.nomsg).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.nomsg).setVisibility(View.INVISIBLE);
    }

    void sendMessage()
    {
        EditText msg=(EditText)findViewById(R.id.input);
        String text=msg.getText().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy kk:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date=simpleDateFormat.format(new Date())+"";

        DatabaseReference msg_set_ref = FirebaseDatabase.getInstance().getReference();
        msg_set_ref.child("Messages_DB").child(msg_id).child("received_msg:"+uid+":").child("msg_timestamp").setValue(date);
        msg_set_ref.child("Messages_DB").child(msg_id).child("received_msg:"+uid+":").child("msg_text").setValue(text);

        new MessageDBHelper(this).insertMessage(msg_id,uid,text,date);
        getMSGS(msg_id);
    }

    BroadcastReceiver updateMsgsBCR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle bundle = intent.getExtras();
            String sender_id = bundle.getString("sender_id");
            String uid = bundle.getString("uid");
            getMSGS(msgID(uid,sender_id));
        }
    };

    @Override
    protected void onDestroy() {
        updateMsgsBCR.abortBroadcast();
    }
}



