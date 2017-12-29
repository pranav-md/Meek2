package com.meek;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 29-Dec-17.
 */

public class AllContactsAdapter extends BaseAdapter {
    AdaptHelper[] all_contact;
    Context context;
    private static LayoutInflater inflater=null;
    String uid;
    public AllContactsAdapter(AdaptHelper[] all_contact, Context context) {
        this.all_contact=all_contact;
        this.context=context;
    }

    @Override
    public int getCount() {
        return all_contact.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Listelements
    {
        String id,name;
        boolean state;
    }

    public class Holder
    {
        CircleImageView imgview;
        TextView textView;
        Button send_request,cancel_request;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final AllContactsAdapter.Holder holder=new AllContactsAdapter.Holder();
        final View rowView;
        rowView = inflater.inflate(R.layout.contacts_list, null);
        holder.imgview=(CircleImageView) rowView.findViewById(R.id.imageView1);
        holder.textView=(TextView)rowView.findViewById(R.id.textView6);
        holder.send_request=(Button) rowView.findViewById(R.id.send_rqst);
        holder.cancel_request=(Button) rowView.findViewById(R.id.cancel_rqst);
        holder.textView.setText(all_contact[position].name);
        SharedPreferences pref = context.getSharedPreferences("UserDetails", MODE_PRIVATE);
        uid=pref.getString("uid","");
        final SharedPreferences.Editor editPref=pref.edit();
        final String uids=pref.getString("Sent_requests","");
        if(uids.contains(all_contact[position].uid))
        {
            View rqst_canceler=rowView.findViewById(R.id.rqst_canceler);
            rqst_canceler.setVisibility(View.VISIBLE);
            holder.send_request.setVisibility(View.INVISIBLE);
        }
        else
        {
            View rqst_canceler=rowView.findViewById(R.id.rqst_canceler);
            rqst_canceler.setVisibility(View.INVISIBLE);
            holder.send_request.setVisibility(View.VISIBLE);
        }
        String sFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meek/Friends/" +all_contact[position].dpno+ ".jpg";
        File f = new File(sFolder);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            holder.imgview.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            holder.imgview.setImageResource(R.drawable.defaultdp);
            e.printStackTrace();
        }

        holder.send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ///adding the uid to the preference
                editPref.putString("Sent_requests",uids+":"+all_contact[position].uid+":");
                editPref.commit();
                ///changing the view
                View rqst_canceler=rowView.findViewById(R.id.rqst_canceler);
                rqst_canceler.setVisibility(View.VISIBLE);
                holder.send_request.setVisibility(View.INVISIBLE);
                ///firebase daa
                sendRequestFB(uid,all_contact[position].uid);
            }
        });
        holder.cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout resultview=(LinearLayout)rowView.findViewById(R.id.result_view);
                TextView resultText=(TextView)rowView.findViewById(R.id.textView5);
                CircleImageView resultdp=(CircleImageView)rowView.findViewById(R.id.result_dp);
                resultview.setVisibility(View.VISIBLE);
                @SuppressLint("ResourceType") Animation anmtn =  AnimationUtils.loadAnimation(context, R.animator.slidedown);
                resultview.setAnimation(anmtn);
                resultText.setText("Cancelled");
                resultview.setBackgroundColor(new Color().parseColor ("#F44336"));
                resultdp.setVisibility(View.INVISIBLE);
            }
        });
        return rowView;
    }
    public void sendRequestFB(final String sender_uid, final String reqstd_uid)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users");
        userRef.child(sender_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String sent_requests=dataSnapshot.child("sent_requests").getValue().toString();
                if(sent_requests.equals("")){
                    sent_requests=":"+sent_requests;
                }
                userRef.child(sender_uid).child("sent_requests").setValue(sent_requests+reqstd_uid+":");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userRef.child(reqstd_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String received_requests=dataSnapshot.child("received_requests").getValue().toString();
                if(received_requests.equals(""))
                    received_requests=":"+received_requests;
                userRef.child(reqstd_uid).child("received_requests").setValue(received_requests+sender_uid+":");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void cancelRequestFB(final String sender_uid, final String reqstd_uid)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users");
        userRef.child(sender_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String sent_requests=dataSnapshot.child("sent_requests").getValue().toString();
                userRef.child(sender_uid).child("sent_requests").setValue(sent_requests.replace(reqstd_uid+":",""));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userRef.child(reqstd_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String received_requests=dataSnapshot.child("received_requests").getValue().toString();
                userRef.child(reqstd_uid).child("received_requests").setValue(received_requests.replace(reqstd_uid+":",""));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}