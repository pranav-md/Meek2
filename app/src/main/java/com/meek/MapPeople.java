package com.meek;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by User on 10-Jun-18.
 */

public class MapPeople
{
    public String uid;
    String name;
    LatLng latLng;
    ValueEventListener loc_listener;
    int color,num_activities;
    ArrayList<Activities> activities;
    MapPeople()
    {
        activities=new ArrayList<Activities>();
    }
    public MapPeople(String uid,String name,LatLng latLng)
    {
        this.uid=uid;
        this.name=name;
        this.latLng=latLng;
        activities=new ArrayList<Activities>();
    }

}
