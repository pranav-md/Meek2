package com.meek;



/**
 * Created by User on 28-May-18.
 */

public class Contact {
    private String id, name, phnum, status, uid,dpno;
    int conn_level;
    boolean in_meek;

    public String getDpno() {
        return dpno;
    }

    public void setDpno(String dpno) {
        this.dpno = dpno;
    }

    public void setID(String ID) {
        this.id = ID;
    }

    public Contact()
    {
        this.uid="0";
    }
    public Contact(String uid,String name,int con_level)

    {
        this.uid=uid;
        this.name=uid;
        this.conn_level=con_level;
    }

    public void setUid(String uid)
    {
        this.uid=uid;
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

    public String getUID() {
        return uid;
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

    public String getUid() {
        return uid;
    }
}
