package com.meek;

import io.realm.RealmObject;

/**
 * Created by User on 15-Jun-18.
 */

public class Places extends RealmObject
{
    int p_num,type;
    String name;
    int visibility;
    double lat,lng;
    String timestamp;
}
