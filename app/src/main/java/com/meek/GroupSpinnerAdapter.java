package com.meek;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 26-Dec-17.
 */

public class GroupSpinnerAdapter extends BaseAdapter {
    Context context;
    int flags[];
    String[] countryNames;
    LayoutInflater inflter;
    public GroupSpinnerAdapter(Context applicationContext, int[] flags, String[] countryNames) {
        this.context = applicationContext;
        this.flags = flags;
        this.countryNames = countryNames;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return flags.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view=inflter.inflate(R.layout.group_spinner,null);
        CircleImageView icon = (CircleImageView) view.findViewById(R.id.grp_dp);
        TextView names = (TextView) view.findViewById(R.id.grp_name);
        icon.setImageResource(flags[position]);
        names.setText(countryNames[position]);
        return view;
    }
}
