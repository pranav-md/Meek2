package com.meek;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.karumi.expandableselector.ExpandableItem;
import com.karumi.expandableselector.ExpandableSelector;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by User on 08-Jun-18.
 */

public class AddPlaceDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_place, container, false);

        Button ok=(Button)view.findViewById(R.id.done);
        Button cancel=(Button)view.findViewById(R.id.done);
        Spinner plc_type=(Spinner)view.findViewById(R.id.place_type);
        plc_type.setAdapter(new TypeAdapter(getContext()));
        ExpandableSelector privacy = (ExpandableSelector)view.findViewById(R.id.privacy_setting);
        List<ExpandableItem> expandableItems = new ArrayList<ExpandableItem>();
        expandableItems.add(new ExpandableItem(R.drawable.locked));
        expandableItems.add(new ExpandableItem(R.drawable.vis_others));
        privacy.showExpandableItems(expandableItems);
        ok.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view)
            {
                ///Firebase insert
                ///Realm insert too
                ///update the list too
                getActivity().getFragmentManager().popBackStack();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view)
            {

                getActivity().getFragmentManager().popBackStack();
            }
        });


        return view;
    }
}
class TypeAdapter extends BaseAdapter{
    Context context;

    TypeAdapter(Context context)
    {
        this.context=context;
    }
    @Override
    public int getCount() {
        return 8;
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
        TextView name=(TextView)view.findViewById(R.id.plc_name);
        Drawable typ = null;
        String txt="";
        switch (i)
        {
            case 1: txt="Home";
                    typ=context.getResources().getDrawable(R.drawable.plc_home);
                    break;
            case 2: txt="Restaurant";
                    typ=context.getResources().getDrawable(R.drawable.plc_restaurant);
                    break;
            case 3: txt="Entertainment";
                    typ=context.getResources().getDrawable(R.drawable.plc_entertainment);
                    break;
            case 4: txt="Work";
                    typ=context.getResources().getDrawable(R.drawable.plc_work);
                    break;
            case 5: txt="Education";
                    typ=context.getResources().getDrawable(R.drawable.plc_education);
                    break;
            case 6: txt="Shopping";
                    typ=context.getResources().getDrawable(R.drawable.plc_shopping);
                    break;
            case 7: txt="Tripping spot";
                     typ=context.getResources().getDrawable(R.drawable.plc_terrain);
                     break;
            case 8: txt="Others";
                    typ=context.getResources().getDrawable(R.drawable.plc_more);
                    break;
        }
        ImageView plc_type=(ImageView)view.findViewById(R.id.plc_icon);
        plc_type.setImageDrawable(typ);
        name.setText(txt);

        return view;
    }
}