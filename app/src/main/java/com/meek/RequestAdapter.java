package com.meek;

import android.annotation.SuppressLint;
import android.content.Context;
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

/**
 * Created by User on 28-Dec-17.
 */

public class RequestAdapter extends BaseAdapter {
   AdaptHelper[] rq_list;
   Context context;
    private static LayoutInflater inflater=null;
    String uid;
    public RequestAdapter(AdaptHelper[] rq_list, Context context) {
            this.rq_list=rq_list;
            this.context=context;
    }

    @Override
    public int getCount() {
        return rq_list.length;
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
        Button okay,cancel;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        final View rowView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.request_checkbox, null);
        holder.imgview=(CircleImageView) rowView.findViewById(R.id.imageView1);
        holder.textView=(TextView)rowView.findViewById(R.id.textView2);
        holder.okay=(Button) rowView.findViewById(R.id.okay);
        holder.cancel=(Button) rowView.findViewById(R.id.cancel);

        holder.textView.setText(rq_list[position].name);
        String sFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meek/Friends/" +rq_list[position].dpno+ ".jpg";
        File f = new File(sFolder);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            holder.imgview.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            holder.imgview.setImageResource(R.drawable.defaultdp);
            e.printStackTrace();
        }

        holder.okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout resultview=(LinearLayout)rowView.findViewById(R.id.result_view);
                TextView resultText=(TextView)rowView.findViewById(R.id.textView5);
                CircleImageView resultdp=(CircleImageView)rowView.findViewById(R.id.result_dp);
                resultview.setVisibility(View.VISIBLE);
                @SuppressLint("ResourceType") Animation anmtn =  AnimationUtils.loadAnimation(context, R.animator.slidedown);
                resultview.setAnimation(anmtn);
                resultText.setText(rq_list[position].name+" is your meek friend ");
                resultview.setBackgroundColor(new Color().parseColor ("#00E676"));
                String sFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meek/Friends/" +rq_list[position].dpno+ ".jpg";
                File f = new File(sFolder);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                    resultdp.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    resultdp.setVisibility(View.INVISIBLE);
                    e.printStackTrace();
                }
                new ContactsListFrag().removeUid(rq_list[position].uid);
                removeFromFB(rq_list[position].uid,uid);
            }
        });
        holder.cancel.setOnClickListener(new View.OnClickListener() {
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
                removeFromFB(rq_list[position].uid,uid);
            }
        });
        return rowView;
    }
    public void removeFromFB(final String sender_uid, final String reqstd_uid)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users");
        userRef.child(sender_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String sent_requests=dataSnapshot.child("sent_requests").getValue().toString();
                String meek_users=dataSnapshot.child("meeked_users").getValue().toString();
                userRef.child(sender_uid).child("sent_requests").setValue(sent_requests.replace(reqstd_uid+":",""));
                userRef.child(sender_uid).child("meeked_users").setValue(meek_users+":"+reqstd_uid+":");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userRef.child(reqstd_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String rcv_requests=dataSnapshot.child("received_requests").getValue().toString();
                String meek_users=dataSnapshot.child("meeked_users").getValue().toString();
                userRef.child(reqstd_uid).child("received_requests").setValue(rcv_requests.replace(sender_uid+":",""));
                userRef.child(reqstd_uid).child("meeked_users").setValue(meek_users+":"+sender_uid+":");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}