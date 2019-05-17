package com.meek.Messaging;

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
    public String sender_id;
    public String name;
    public String last_msg;
    public String date;
    int num_unread;

    public MsgPPL()
    {

    }

}
