package com.meek.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.meek.MyActivities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by User on 18-Jun-18.
 */

@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    Date cur_date;

    @SuppressLint("ValidFragment")
    public DatePickerFragment(Date cur_date)
    {
        this.cur_date=cur_date;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new DatePickerDialog(getActivity(), this, Integer.parseInt(DateFormat.format("yyyy",   cur_date).toString()),
                                    (Integer.parseInt(DateFormat.format("M",   cur_date).toString())-1),
                                        Integer.parseInt(DateFormat.format("dd",   cur_date).toString()));

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Log.e("DATE SET","on date set worked");
        Log.e("DATE SET","date="+datePicker.getMaxDate()+"  month"+datePicker.getDayOfMonth());
        MyActivities dateset=(MyActivities)getContext();
        SimpleDateFormat d_frmt=new SimpleDateFormat("dd/MM/yyyy");
        try {
            dateset.setDate(d_frmt.parse(datePicker.getDayOfMonth()+"/"+(datePicker.getMonth()+1)+"/"+datePicker.getYear()));
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("DATE EXCEPTION","DATE="+datePicker.getDayOfMonth()+"/"+(datePicker.getMonth()+1)+"/"+datePicker.getYear());
        }
    }
}
