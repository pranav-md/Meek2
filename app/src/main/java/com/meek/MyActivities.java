package com.meek;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meek.Fragments.DatePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by User on 18-Jun-18.
 */

public class MyActivities extends AppCompatActivity {
    Date curr_date, lwr_gmt, upr_gmt;
    String lwr_dt, upr_dt, uid;
    boolean comp_lw, comp_up;
    LinearLayout no_act;
    Calendar cal;
    MapActivitiesPageAdapter activitiesPageAdapter=null;
    ViewPager actvity_pgs;
    ArrayList<Activities> activities=null;
    boolean changed;
    String serverkey;

    DialogFragment picker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myactivities);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        serverkey = extras.getString("ServerKey");

        SharedPreferences mypref = getSharedPreferences("UserDetails", MODE_PRIVATE);
        uid = mypref.getString("uid", "");
        LinearLayout date_setter=(LinearLayout)findViewById(R.id.date_setter);
        no_act=(LinearLayout)findViewById(R.id.no_act);
        actvity_pgs=(ViewPager)findViewById(R.id.act_pgs);
        no_act.setVisibility(View.INVISIBLE);
        changed=false;
      //  activitiesPageAdapter=new MapActivitiesPageAdapter(getSupportFragmentManager(),uid);

        date_setter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                picker = new DatePickerFragment(curr_date);
                picker.show(getSupportFragmentManager(), "datePicker");
            }
        });
        curr_date = new Date();
        activities = new ArrayList<Activities>();
        cal = Calendar.getInstance();
        setDateBar(curr_date);
        fetchDateData();
    }
    void setShimmer()
    {
        ArrayList<Activities> shimmer=new ArrayList<Activities>();
        Activities newone=new Activities();
        newone.act_id="0";
        shimmer.add(newone);
        activitiesPageAdapter.setData(shimmer);
        if(changed==false)
            actvity_pgs.setAdapter(activitiesPageAdapter);
        else
            activitiesPageAdapter.notifyDataSetChanged();
        changed=true;
    }
    public void setDate(Date date)
    {
        activities=null;
        curr_date=date;
        setDateBar(curr_date);
        fetchDateData();
    }

    void setDateBar(Date c_date)
    {
        TextView day = (TextView) findViewById(R.id.day);
        TextView month = (TextView) findViewById(R.id.month);
        TextView year = (TextView) findViewById(R.id.year);
        String daystr          = (String) DateFormat.format("dd",   c_date); // 20
        String monthstr = (String) DateFormat.format("MMM",  c_date); // Jun
        String yearstr        = (String) DateFormat.format("yyyy", c_date); // 2013

        year.setText( yearstr);
        month.setText(monthstr);
        day.setText(daystr);
        //Log.e("Date attributes", cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
    }

    void fetchDateData() {
      //  setShimmer();
        setGMTdates();
        comp_up = comp_lw = false;
        activities=new ArrayList<Activities>();
        DatabaseReference act_ref = FirebaseDatabase.getInstance().getReference();
        Log.e("ACT FTC DTA","r_uid="+uid+"   lwr_dt="+lwr_dt+"   upr_dt"+upr_dt);
            act_ref.child("Activities").child(uid).child("pg_view").child(lwr_dt).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.v("Inside act_ref", "lwr dt called");
                    if (dataSnapshot.exists()) {
                        SimpleDateFormat d_format = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
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
            act_ref.child("Activities").child(uid).child("pg_view").child(upr_dt).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    Log.v("Inside act_ref", "upr dt called");
                    if (dataSnapshot.exists())
                    {
                        while(comp_lw==false);
                        SimpleDateFormat d_format = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
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
                activitiesPageAdapter=new MapActivitiesPageAdapter(getSupportFragmentManager(),uid,serverkey);
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
        SimpleDateFormat d_format=new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
        SimpleDateFormat default_tz=new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
        default_tz.setTimeZone(TimeZone.getDefault());
        d_format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            lwr_gmt=default_tz.parse(DateFormat.format("dd",   curr_date).toString()+"-"
                    +(Integer.parseInt(DateFormat.format("MM",   curr_date).toString()))
                    +"-"+DateFormat.format("yyyy",   curr_date)+" 00:00:01");

            lwr_dt=d_format.format(lwr_gmt).substring(0,d_format.format(lwr_gmt).indexOf(" ")).trim();

            lwr_gmt=d_format.parse(d_format.format(lwr_gmt));


            upr_gmt=default_tz.parse(DateFormat.format("dd",   curr_date).toString()+"-"
                    +(Integer.parseInt(DateFormat.format("MM",   curr_date).toString()))
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
