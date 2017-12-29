package com.meek;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
 * Created by User on 23-Dec-17.
 */

public class ProfileSet extends Fragment{
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.view=inflater.inflate(R.layout.profile_box,container,false);

        return view;
    }
    void setDP(String localFilename) throws FileNotFoundException {
        CircleImageView dp = (CircleImageView) view.findViewById(R.id.imageView1);
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(localFilename)));
        dp.setImageBitmap(bitmap);
    }
    void saveActivity(String uid)
    {
        EditText activity=(EditText)view.findViewById(R.id.editText);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users");
        userRef.child(uid).child("Activity").setValue(activity.getText().toString());
    }
    void setActivity(String uid)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference activityRef = database.getReference("Users").child(uid).child("Activity");
        activityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EditText activity=(EditText)view.findViewById(R.id.editText);
                activity.setText((String)dataSnapshot.getValue());
                activity.selectAll();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
