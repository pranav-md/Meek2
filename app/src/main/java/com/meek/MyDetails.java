package com.meek;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;


import java.util.Date;


/**
 * Created by User on 30-May-18.
 */

public class MyDetails  {
    String lat,lng;
    Date cur_date;
    String my_dp_uri;

    public String getLat() {
        return lat;
    }

    public String getMy_dp() {
        return my_dp_uri;
    }

    public void setMy_dp(String my_dp) {
        this.my_dp_uri = my_dp;
    }

    public String getLng() {
        return lng;
    }

    public void setCur_location(String lat,String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Date getCur_date() {

        return cur_date;
    }

    public void setCur_date(Date cur_date) {
        this.cur_date = cur_date;
    }
}
