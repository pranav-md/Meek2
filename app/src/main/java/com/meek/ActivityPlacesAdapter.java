package com.meek;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 12-Jul-18.
 */

public class ActivityPlacesAdapter extends BaseAdapter {
    Context context;
    ArrayList<ListPlace> plc_lst;
    void getData(ArrayList<ListPlace>  plc_lst, Context context)
    {
        this.plc_lst=plc_lst;
        this.context=context;
    }
    @Override
    public int getCount() {
        return plc_lst.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.plc_list, null);
        TextView plc_name=(TextView)view.findViewById(R.id.plc_name);
        plc_name.setText(plc_lst.get(i).name);
        return view;
    }
}
