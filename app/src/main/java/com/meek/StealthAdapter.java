package com.meek;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
 * Created by User on 30-Dec-17.
 */

public class StealthAdapter extends BaseAdapter {
    AdaptHelper[] stealth_list;
    Context context;
    private static LayoutInflater inflater=null;
    String uid;
    public StealthAdapter(AdaptHelper[] stealth_list, Context context,String uid) {
        this.stealth_list=stealth_list;
        this.context=context;
        this.uid=uid;
    }

    @Override
    public int getCount() {
        return stealth_list.length;
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
        CircleImageView stealth_dp;
        ImageView stealth_on_off;
        TextView stealth_name;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        SharedPreferences pref = context.getSharedPreferences("UserDetails", MODE_PRIVATE);
        final String[] stealth_me = new String[1];
        final String[] stealth_to_me = new String[1];
        String uid=pref.getString("uid","");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users");
        userRef.child(uid).child("stealth_me").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stealth_me[0] =dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        userRef.child(uid).child("stealth_to_me").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stealth_to_me[0] =dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        StealthAdapter.Holder holder=new StealthAdapter.Holder();
        final View rowView;
        rowView = inflater.inflate(R.layout.stealth_list_element, null);
        rowView.setTag(stealth_list[position].uid);

        holder.stealth_dp=(CircleImageView) rowView.findViewById(R.id.stealth_dp);
        holder.stealth_name=(TextView)rowView.findViewById(R.id.stealth_name);
        holder.stealth_on_off=(ImageView)rowView.findViewById(R.id.stealth_mode);
        holder.stealth_name.setText(stealth_list[position].name);
        String sFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meek/Friends/" +stealth_list[position].dpno+ ".jpg";
        File f = new File(sFolder);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            holder.stealth_dp.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            holder.stealth_dp.setImageResource(R.drawable.defaultdp);
            e.printStackTrace();
        }
        if(stealth_me[0].contains(stealth_list[position].uid))      //ppl user stealth
        {
            holder.stealth_name.setTextColor(Color.parseColor("#ffffff"));
            rowView.setBackgroundColor(Color.parseColor("#000000"));
            Drawable stealth_on = context.getResources().getDrawable(R.drawable.stealth_on);
            holder.stealth_on_off.setImageDrawable(stealth_on);
            rowView.setTag(1,1);
            rowView.setTag(2,stealth_to_me[0]);
        }
        else if(stealth_to_me[0].contains(stealth_list[position].uid))  //ppl who stealth me
        {
            holder.stealth_name.setTextColor(Color.parseColor("#424242"));
            rowView.setBackgroundColor(Color.parseColor("#BDBDBD"));
            Drawable stealth_on = context.getResources().getDrawable(R.drawable.stealth_on);
            holder.stealth_on_off.setImageDrawable(stealth_on);
            rowView.setTag(1,1);
        }
        else                    //not stealth ppl
        {
            holder.stealth_name.setTextColor(Color.parseColor("#000000"));
            rowView.setBackgroundColor(Color.parseColor("#ffffff"));
            Drawable stealth_off = context.getResources().getDrawable(R.drawable.stealth_off);
            holder.stealth_on_off.setImageDrawable(stealth_off);
            rowView.setTag(1,0);

        }
        return rowView;
    }
}