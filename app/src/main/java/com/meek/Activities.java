package com.meek;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by User on 12-Jun-18.
 */

public class Activities implements Comparable{
    int act_code,color;
    LatLng latLng;
    String visiblity,act_text;
    Long act_date;
    boolean act_music;
    String act_activity;
    String act_id;

    String dest_lat,dest_lng,dest_name;
    String curr_place;
    Activities()
    {
        this.dest_name="";
        this.curr_place="";
        this.act_music=false;
    }

    long getTime()
    {
        return act_date;
    }

    @Override
    public int compareTo(@NonNull Object act_obj)
    {
        long compareage=((Activities)act_obj).getTime();
        return (int) (this.act_date-compareage);
    }
}
