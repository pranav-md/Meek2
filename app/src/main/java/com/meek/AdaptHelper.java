package com.meek;

/**
 * Created by User on 28-Dec-17.
 */

public class AdaptHelper {
    String name,uid,dpno,phno,activity;
    void setTheValues(String uid,String name,String dpno,String phno)
    {
        this.uid=uid;
        this.name=name;
        this.dpno=dpno;
        this.phno=phno;
    }
    void setTheValues(String uid,String name,String dpno,String phno,String activity)
    {
        this.uid=uid;
        this.name=name;
        this.dpno=dpno;
        this.phno=phno;
        this.activity=activity;

    }
}
