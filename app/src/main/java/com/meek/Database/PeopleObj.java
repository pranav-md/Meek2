package com.meek.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by User on 08-Jan-19.
 */

public class PeopleObj
{
    private int uid;
    private String name,hashed_phnum;
    private String enc_key=null;
    Double lat,lng;
    int conn_level;
    boolean loc_access=false;

    public PeopleObj() {


    }

    public PeopleObj(int uid, String name, String hashed_phnum, String enc_key, Double lat, Double lng) {
        this.uid = uid;
        this.name = name;
        this.hashed_phnum = hashed_phnum;
        this.enc_key = enc_key;
        this.lat = lat;
        this.lng = lng;

    }

    public String getName() {
        return name;
    }
}