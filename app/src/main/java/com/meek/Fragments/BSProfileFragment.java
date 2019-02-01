package com.meek.Fragments;

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
import com.meek.MapPeople;
import com.meek.R;

/**
 * Created by User on 11-Jun-18.
 */

@SuppressLint("ValidFragment")
public class BSProfileFragment extends Fragment
{
    MapPeople mapPeople;
    View view;

    @SuppressLint("ValidFragment")
    public BSProfileFragment(MapPeople mapPeople)
    {
        this.mapPeople=mapPeople;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bs_profile, container, false);

        setData();


        return view;
    }

    void setData()
    {
        DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();

        ppl_ref.child("Users").child(mapPeople.uid).child("Details1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //shimmer it until it loads up
                ///retrieve and decrypt and set the datas in the "view"

                //

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
