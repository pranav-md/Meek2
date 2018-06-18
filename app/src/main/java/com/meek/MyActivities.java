package com.meek;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Checkable;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by User on 18-Jun-18.
 */

public class MyActivities extends AppCompatActivity {
    Date curr_date, lwr_gmt, upr_gmt;
    String lwr_dt, upr_dt, uid;
    boolean comp_lw, comp_up;
    Calendar cal;
    CheckActivity curr_act;
    ArrayList<CheckActivity> checked_activities;
    ArrayList<Activities> activities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myactivities);

        SharedPreferences mypref = getSharedPreferences("UserDetails", MODE_PRIVATE);
        uid = mypref.getString("uid", "");

        curr_date = new Date();
        checked_activities = new ArrayList<CheckActivity>();
        activities = new ArrayList<Activities>();
        cal = Calendar.getInstance();
        setDateBar();

        fetchDateData();
    }

    void setDateBar() {
        TextView day = (TextView) findViewById(R.id.day);
        TextView month = (TextView) findViewById(R.id.month);
        TextView year = (TextView) findViewById(R.id.year);

        String mnth = "";
        switch (curr_date.getMonth()) {
            case 1:
                mnth = "JAN";
                break;
            case 2:
                mnth = "FEB";
                break;
            case 3:
                mnth = "MAR";
                break;
            case 4:
                mnth = "APR";
                break;
            case 5:
                mnth = "MAY";
                break;
            case 6:
                mnth = "JUN";
                break;
            case 7:
                mnth = "JUL";
                break;
            case 8:
                mnth = "AUG";
                break;
            case 9:
                mnth = "SEP";
                break;
            case 10:
                mnth = "OCT";
                break;
            case 11:
                mnth = "NOV";
                break;
            case 12:
                mnth = "DEC";
                break;
        }
        year.setText(curr_date.getYear() + "");
        Log.v("Date attributes", cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
    }

    void fetchDateData() {
        setGMTdates();
        comp_up = comp_lw = false;
        DatabaseReference act_ref = FirebaseDatabase.getInstance().getReference();
        for (int i = 0; i < checked_activities.size(); ++i) {
            if (checked_activities.get(i).date == curr_date) {
                curr_act = checked_activities.get(i);
                break;
            } else
                curr_act = null;
        }
        if (curr_act == null) {
            act_ref.child("Activities").child(uid).child("pg_view").child(upr_dt).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (curr_act == null) {
                        curr_act = new CheckActivity();
                        curr_act.date = curr_date;
                    }
                    if (dataSnapshot != null) {
                        SimpleDateFormat d_format = new SimpleDateFormat("dd/M/yyyy kk:mm:ss");
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            try {
                                Date act_date = d_format.parse(ds.getValue().toString());
                                if (act_date.before(upr_gmt)) {
                                    curr_act.act_id.add(ds.getKey().toString());
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                     }
                    comp_up=true;
                    if(comp_lw==true&&comp_up==true)
                        fetchActivityData();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            act_ref.child("Activities").child(uid).child("pg_view").child(lwr_dt).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (curr_act == null) {
                        curr_act = new CheckActivity();
                        curr_act.date = curr_date;
                    }
                    if (dataSnapshot != null) {
                        SimpleDateFormat d_format = new SimpleDateFormat("dd/M/yyyy kk:mm:ss");
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            try {
                                Date act_date = d_format.parse(ds.getValue().toString());
                                if (act_date.after(lwr_gmt)) {
                                    curr_act.act_id.add(ds.getKey().toString());
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    comp_lw=true;
                    if(comp_lw==true&&comp_up==true)
                        fetchActivityData();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            /////
            ///checked the activity already
        }

    }

    void fetchActivityData()
    {
        final SimpleDateFormat d_format = new SimpleDateFormat("dd/M/yyyy kk:mm:ss");
        DatabaseReference act_ref = FirebaseDatabase.getInstance().getReference();
        for(int i=0;i<curr_act.act_id.size();++i)
        act_ref.child("Activities").child(uid).child("All_Activities").child(curr_act.act_id.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Activities newone=new Activities();
                newone.act_id=dataSnapshot.getKey().toString();
                newone.act_text=dataSnapshot.child("act_text").getValue().toString();
                try {
                    newone.act_date=d_format.parse(dataSnapshot.child("act_date").getValue().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                newone.dest_name=dataSnapshot.child("act_dest").getValue().toString();
                if(!newone.dest_name.equals(""))
                {
                    newone.dest_lat=dataSnapshot.child("dest_lat").getValue().toString();
                    newone.dest_lng=dataSnapshot.child("dest_lng").getValue().toString();
                }
                newone.visiblity=dataSnapshot.child("act_visibility").getValue().toString();
                newone.curr_place=dataSnapshot.child("act_current_place").getValue().toString();
                newone.latLng=new LatLng(Double.parseDouble(dataSnapshot.child("act_lat").getValue().toString()),
                        Double.parseDouble(dataSnapshot.child("act_lng").getValue().toString()));
                newone.act_music= (boolean) dataSnapshot.child("act_music").getValue();
                activities.add(newone);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void setPages()
    {

    }

    void setGMTdates()
    {
        SimpleDateFormat d_format=new SimpleDateFormat("dd/M/yyyy kk:mm:ss");
        d_format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            lwr_gmt=d_format.parse(cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR)+" 00:00:01");
            upr_gmt=d_format.parse(cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR)+" 23:59:59");

            lwr_dt=d_format.format(lwr_gmt).substring(0,d_format.format(lwr_gmt).indexOf(" ")).trim();
            upr_dt=d_format.format(upr_gmt).substring(0,d_format.format(upr_gmt).indexOf(" ")).trim();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        Log.v("GMT time daa","lwr:"+lwr_dt+"  upr_gmt"+upr_dt);
    }
    void setNoActivityFound()
    {

    }

    class CheckActivity
    {
        Date date;
        ArrayList<String> act_id;
    }
}
