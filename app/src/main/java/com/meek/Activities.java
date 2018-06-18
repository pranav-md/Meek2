package com.meek;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by User on 12-Jun-18.
 */

public class Activities {
    int act_code,color;
    LatLng latLng;
    String visiblity,act_text;
    Date act_date;
    boolean act_music,act_activity;
    String act_id;

    String dest_lat,dest_lng,dest_name;
    String curr_place;
    Activities()
    {
        this.dest_name="";
        this.curr_place="";
        this.act_activity=false;
        this.act_music=false;
    }
}
