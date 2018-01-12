package com.meek;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Person implements ClusterItem
{
     LatLng mPosition;
     String uid;
     String mTitle;
     String mSnippet;

    public Person(LatLng ltlng,String uid) {
        mPosition = ltlng;
        this.uid=uid;
    }

    public Person(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return "hahah";
    }

    @Override
    public String getSnippet() {
        return "loool";
    }
}
