package com.meek;

import com.google.android.gms.maps.model.LatLng;

import io.realm.RealmObject;

/**
 * Created by User on 20-May-18.
 */

public class Activity extends RealmObject{
    int activity;

    public Activity()
    {
        activity=-1;
    }
}