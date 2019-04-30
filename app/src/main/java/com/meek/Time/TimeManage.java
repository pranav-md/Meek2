package com.meek.Time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by User on 19-Apr-19.
 */

public class TimeManage {
    public String setTimeString(Long milli_time)
    {
        long days,hours,min;
        String returnMsg=" ";
        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        long difference = Long.parseLong(df.format(new Date())) - milli_time;
        days = (int) (difference / (1000*60*60*24));
        hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
        hours = (hours < 0 ? -hours : hours);
        if(min==0)
            returnMsg="Just now";
        else if(min/60==0)
            returnMsg=min+" mins ago";
        else if(days==0)
            returnMsg=hours+" hrs ago";
        else if(days/7==0)
            returnMsg=days+" days ago";
        else
            returnMsg=days/7+" weeks ago";
        return  returnMsg;
    }

}
