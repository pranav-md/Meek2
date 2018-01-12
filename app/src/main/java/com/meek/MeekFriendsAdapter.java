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
 * Created by User on 29-Dec-17.
 */

public class MeekFriendsAdapter extends BaseAdapter {
    AdaptHelper[] meek_list;
    Context context;
    private static LayoutInflater inflater=null;
    String uid;
    public MeekFriendsAdapter(AdaptHelper[] meek_list, Context context) {
        this.meek_list=meek_list;
        this.context=context;
    }

    @Override
    public int getCount() {
        return meek_list.length;
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
        TextView textView,activity;
        Button okay,cancel;
    }
    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        // TODO Auto-generated method stub
        MeekFriendsAdapter.Holder holder = new MeekFriendsAdapter.Holder();
        //View rowView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.meek_list, null);
        holder.imgview = (CircleImageView) rowView.findViewById(R.id.meek_dp);
        holder.textView = (TextView) rowView.findViewById(R.id.meek_name);
        holder.textView = (TextView) rowView.findViewById(R.id.meek_activity);

        holder.textView.setText(meek_list[position].name);
        holder.textView.setText(meek_list[position].activity);
        String sFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meek/Friends/" + meek_list[position].dpno + ".jpg";
        File f = new File(sFolder);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            holder.imgview.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            holder.imgview.setImageResource(R.drawable.defaultdp);
            e.printStackTrace();
        }
        return  rowView;
    }

}