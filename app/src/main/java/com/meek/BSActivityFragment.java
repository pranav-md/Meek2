package com.meek;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by User on 11-Jun-18.
 */

@SuppressLint("ValidFragment")
public class BSActivityFragment extends Fragment
{
    String uid,act_id;
    View view;

    @SuppressLint("ValidFragment")
    BSActivityFragment(String uid,String act_id)
    {
        this.act_id=act_id;
        this.uid=uid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.activity_card, container, false);

      //  getDataActivity();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    void getDataActivity()
    {
        DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();

        ppl_ref.child("Activities").child(uid).child(act_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                        ///shimmer

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
