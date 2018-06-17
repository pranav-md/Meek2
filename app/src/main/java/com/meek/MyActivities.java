package com.meek;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by User on 18-Jun-18.
 */

public class MyActivities extends AppCompatActivity {
    Date curr_date,lwr_gmt,upr_gmt;

    ArrayList<Date> checked_date;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myactivities);
        curr_date=new Date();
        setDateBar();

        fetchDateData();
    }

    void setDateBar()
    {
        TextView day=(TextView)findViewById(R.id.day);
        TextView month=(TextView)findViewById(R.id.month);
        TextView year=(TextView)findViewById(R.id.year);

        day.setText(curr_date.getDate());
        String mnth="";
        switch (curr_date.getMonth())
        {
            case 1: mnth="JAN";
                    break;
            case 2: mnth="FEB";
                    break;
            case 3: mnth="MAR";
                    break;
            case 4: mnth="APR";
                    break;
            case 5: mnth="MAY";
                    break;
            case 6: mnth="JUN";
                    break;
            case 7: mnth="JUL";
                    break;
            case 8: mnth="AUG";
                    break;
            case 9: mnth="SEP";
                    break;
            case 10:mnth="OCT";
                    break;
            case 11:mnth="NOV";
                    break;
            case 12:mnth="DEC";
                    break;

        }
        year.setText(curr_date.getYear());
    }
    void fetchDateData()
    {
        setGMTdates();



    }
    void setGMTdates()
    {
        SimpleDateFormat d_format=new SimpleDateFormat("dd/M/yyyy kk:mm:ss");
        d_format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            lwr_gmt=d_format.parse(d_format.format(curr_date.getDate()+"/"+curr_date.getMonth()+"/"+curr_date.getYear()+" 00:00:01"));
            upr_gmt=d_format.parse(d_format.format(curr_date.getDate()+"/"+curr_date.getMonth()+"/"+curr_date.getYear()+" 23:59:59"));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.v("GMT time daa","lwr:"+lwr_gmt+"  upr_gmt"+upr_gmt);
    }
}
