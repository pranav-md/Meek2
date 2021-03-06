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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meek.Database.MessageDBHelper;
import com.meek.Database.PeopleDBHelper;
import com.meek.Encryption.AES;
import com.meek.R;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.meek.TabFragment.extractor;

/**
 * Created by User on 09-Jun-18.
 */

public class MessageListMaker extends AppCompatActivity
{
    ArrayList<Message> msgList;
    String uid,r_uid,msg_id;
    int upr_bound=0,lwr_bound=0;
    ListView messagesList;
    String serverkey,key;
    MessageListAdapter messageListAdapter=null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        serverkey = extras.getString("ServerKey");
        setContentView(R.layout.message_list);

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
        Log.e("MSG_ID is",msg_id+" msg id is here");
        messagesList=findViewById(R.id.messagesList);
        SharedPreferences getPref=getSharedPreferences("USERKEY",MODE_PRIVATE);
        key=new AES().decrypt(getPref.getString("KEY",""),serverkey);
        Log.e("ENC KEY IS","KEY IS EQL="+key);
        listenMsgs(uid);
        getMSGS(msg_id);
        setTop(name);

        Button send_button=(Button)findViewById(R.id.msg_send);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }
    void setTop(String name)
    {
        TextView nm=(TextView)findViewById(R.id.name);
        nm.setText(name);
    }
    void getMSGS(String msg_id)
    {
        ArrayList<Message> allMsgs=new MessageDBHelper(this,serverkey).getMessages(msg_id);
        msgList=allMsgs;
            if (messageListAdapter == null) {
                messageListAdapter = new MessageListAdapter(this, msgList, uid);
                messagesList.setAdapter(messageListAdapter);
            } else {
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
        msg.setText("");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

        new MessageDBHelper(this,serverkey).insertMessage(msg_id,uid,text,calendar.getTimeInMillis()+"");
        Log.e("ENC KEY IS","KEY IS EQL="+key);
        DatabaseReference msg_set_ref = FirebaseDatabase.getInstance().getReference();
        msg_set_ref.child("Messages_DB").child(msg_id).child(uid).child("received_msg").child("msg_text").setValue(new AES().encrypt(text,key));
        msg_set_ref.child("Messages_DB").child(msg_id).child(uid).child("received_msg").child("msg_date").setValue(new AES().encrypt(calendar.getTimeInMillis()+"",key));
        msg_set_ref.child("Messages_DB").child(msg_id).child(uid).child("msg_trigger").setValue(new AES().encrypt(calendar.getTimeInMillis()+"",key));

        getMSGS(msg_id);
    }

    BroadcastReceiver updateMsgsBCR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle bundle = intent.getExtras();
            String sender_id = bundle.getString("sender_id");
            String uid = bundle.getString("uid");

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        updateMsgsBCR.abortBroadcast();
        unregisterReceiver(updateMsgsBCR);
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
                                    Log.e("MESGSS DPLAY","MSG_TE KEY IS="+new PeopleDBHelper(MessageListMaker.this,serverkey).getEncKey(id));
                                    new MessageDBHelper(MessageListMaker.this,serverkey).insertMessage(msgID(uid, id), id,new AES().decrypt( msg_text,new PeopleDBHelper(MessageListMaker.this,serverkey).getEncKey(id)), msg_date);
                                    xml_msgs = xml_msgs.replace("<Message>" + parseXML(parseXML(xml_msgs, "AllMessages"), "Message") + "</Message>", "");
                                }
                                getMSGS(msgID(uid,r_uid));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


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
    String msgTime(String time)
    {

        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));

        SimpleDateFormat df2 = new SimpleDateFormat(" HH:mm: a");
        df2.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = df2.format(date);
        return formattedDate;
    }

}



