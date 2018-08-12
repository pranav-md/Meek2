package com.meek;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

/**
 * Created by User on 15-Jun-18.
 */

public class PlaceListAdapter extends BaseAdapter {
   ArrayList<Places> place_list;
   Context context;


    void getData(ArrayList<Places> place_list, Context context)
   {
       this.place_list=place_list;
       this.context=context;
   }


    @Override
    public int getCount() {
        return place_list.size();
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.place_list_item, null);
        TextView name=(TextView)view.findViewById(R.id.place_name);
        Drawable typ = null;
        switch (place_list.get(i).type)
        {
            case 1: typ=context.getResources().getDrawable(R.drawable.plc_home);
                    break;
            case 2: typ=context.getResources().getDrawable(R.drawable.plc_restaurant);
                    break;
            case 3: typ=context.getResources().getDrawable(R.drawable.plc_entertainment);
                    break;
            case 4: typ=context.getResources().getDrawable(R.drawable.plc_work);
                    break;
            case 5: typ=context.getResources().getDrawable(R.drawable.plc_education);
                    break;
            case 6: typ=context.getResources().getDrawable(R.drawable.plc_shopping);
                    break;
            case 7: typ=context.getResources().getDrawable(R.drawable.plc_terrain);
                    break;
            case 8: typ=context.getResources().getDrawable(R.drawable.plc_more);
                    break;
        }
        ImageView plc_type=(ImageView)view.findViewById(R.id.place_type);
        plc_type.setImageDrawable(typ);
        name.setText(place_list.get(i).name);
        if(place_list.get(i).visibility!=0)
            view.findViewById(R.id.lock).setVisibility(View.INVISIBLE);
        return view;
    }
}
