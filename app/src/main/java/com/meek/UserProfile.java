package com.meek;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by User on 19-Jun-18.
 */

public class UserProfile extends AppCompatActivity {
    String r_uid;
    Date curr_date, lwr_gmt, upr_gmt;
    String lwr_dt, upr_dt;
    boolean comp_up,comp_lw;
    Calendar cal;
    LinearLayout no_act;
    ArrayList<Activities> activities;
    MapActivitiesPageAdapter activitiesPageAdapter;
    ViewPager actvity_pgs;
    String serverkey,loc_stat;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        serverkey = extras.getString("ServerKey");
        loc_stat = extras.getString("Stat");


        r_uid="";
        activities=new ArrayList<Activities>();
        LinearLayout date_setter=(LinearLayout)findViewById(R.id.date_setter);
        no_act=(LinearLayout)findViewById(R.id.no_act);
        cal = Calendar.getInstance();
        activitiesPageAdapter=new MapActivitiesPageAdapter(getSupportFragmentManager(),r_uid,serverkey);
        actvity_pgs=(ViewPager)findViewById(R.id.act_pgs);
        no_act.setVisibility(View.INVISIBLE);
        fetchDateData();
    }
    void fetchDateData() {
        //  setShimmer();
        setGMTdates();
        comp_up = comp_lw = false;
        activities=new ArrayList<Activities>();
        DatabaseReference act_ref = FirebaseDatabase.getInstance().getReference();
        Log.e("ACT FTC DTA","r_uid="+r_uid+"   lwr_dt="+lwr_dt+"   upr_dt"+upr_dt);
        act_ref.child("Activities").child(r_uid).child("pg_view").child(lwr_dt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    SimpleDateFormat d_format = new SimpleDateFormat("dd-M-yyyy kk:mm:ss");
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        try {

                            Date act_date = d_format.parse(ds.getValue().toString());
                            Log.v("My act_pg set", "upr date = " + act_date);
                            if (act_date.after(lwr_gmt))
                            {
                                Activities newone=new Activities();
                                newone.act_id=ds.getKey().toString();
                                Log.v("My act_pg set", "Inside date before");
                                activities.add(newone);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }
                comp_lw = true;
                if (comp_lw == true && comp_up == true) {
                    fetchActivityData(activities);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(!lwr_dt.equals(upr_dt))
            act_ref.child("Activities").child(r_uid).child("pg_view").child(upr_dt).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if (dataSnapshot != null)
                    {
                        while(comp_lw==false);
                        SimpleDateFormat d_format = new SimpleDateFormat("dd-M-yyyy kk:mm:ss");
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            try {
                                Date act_date = d_format.parse(ds.getValue().toString());
                                Log.v("My act_pg set", "lwr date = " + act_date);
                                if (act_date.before(upr_gmt))
                                {
                                    Activities newone=new Activities();
                                    newone.act_id=ds.getKey().toString();
                                    Log.v("My act_pg set", "Inside date after");
                                    activities.add(newone);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    comp_up = true;

                    if (comp_lw == true && comp_up == true)
                    {

                        fetchActivityData(activities);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        else
            comp_up=true;

    }

    void fetchActivityData(ArrayList<Activities> act_ids)
    {

        if(act_ids!=null)
        {
            actvity_pgs.setVisibility(View.VISIBLE);
            if(activitiesPageAdapter==null)
            {
                activitiesPageAdapter=new MapActivitiesPageAdapter(getSupportFragmentManager(),r_uid,serverkey);
                activitiesPageAdapter.setData(act_ids);
                actvity_pgs.setAdapter(activitiesPageAdapter);
            }
            else
            {
                activitiesPageAdapter.setData(act_ids);
                activitiesPageAdapter.notifyDataSetChanged();
            }
            CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
            indicator.setViewPager(actvity_pgs);
        }
        else
        {
            setNoActivityFound();
        }

    }

    void setGMTdates()
    {
        SimpleDateFormat d_format=new SimpleDateFormat("dd-M-yyyy kk:mm:ss");
        SimpleDateFormat default_tz=new SimpleDateFormat("dd-M-yyyy kk:mm:ss");
        default_tz.setTimeZone(TimeZone.getDefault());
        d_format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            lwr_gmt=default_tz.parse(DateFormat.format("dd",   curr_date).toString()+"-"
                    +(Integer.parseInt(DateFormat.format("M",   curr_date).toString()))
                    +"-"+DateFormat.format("yyyy",   curr_date)+" 00:00:01");

            lwr_dt=d_format.format(lwr_gmt).substring(0,d_format.format(lwr_gmt).indexOf(" ")).trim();

            lwr_gmt=d_format.parse(d_format.format(lwr_gmt));


            upr_gmt=default_tz.parse(DateFormat.format("dd",   curr_date).toString()+"-"
                    +(Integer.parseInt(DateFormat.format("M",   curr_date).toString()))
                    +"-"+DateFormat.format("yyyy",   curr_date)+" 23:59:59");

            upr_dt=d_format.format(upr_gmt).substring(0,d_format.format(upr_gmt).indexOf(" ")).trim();

            upr_gmt=d_format.parse(d_format.format(upr_gmt));

        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        Log.v("GMT time daa","lwr:"+lwr_dt+"  upr_gmt"+upr_dt);
    }
    void setNoActivityFound()
    {
        no_act.setVisibility(View.INVISIBLE);
    }



}
