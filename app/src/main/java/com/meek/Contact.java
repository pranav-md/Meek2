package com.meek;

import io.realm.RealmObject;

/**
 * Created by User on 28-May-18.
 */

public class Contact extends RealmObject {
   private String id,name,phnum,status;

    public void setID(String ID) {
        this.id = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhnum(String phnum) {
        this.phnum = phnum;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhnum() {
        return phnum;
    }

    public String getStatus() {
        return status;
    }
}
