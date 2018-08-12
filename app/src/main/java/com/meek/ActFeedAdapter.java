package com.meek;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by User on 20-Jun-18.
 */

public class ActFeedAdapter extends BaseAdapter {

    ArrayList<ActFeed> actFeeds;
    FragmentManager fragmentManager;
    Context context;
    void getData(ArrayList<ActFeed> actFeeds, Context context, FragmentManager fragmentManager)
   {
       this.actFeeds=actFeeds;
       this.context=context;
        this.fragmentManager=fragmentManager;
   }

    @Override
    public int getCount() {
        return actFeeds.size();
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
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        MapActivitiesPageAdapter pg_adapter=new MapActivitiesPageAdapter(fragmentManager,actFeeds.get(i).a_uid);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.act_feed_item, null);
        ViewPager viewPager=(ViewPager)view.findViewById(R.id.act_page);
        pg_adapter.setData(actFeeds.get(i).activities);
        viewPager.setAdapter(pg_adapter);
        return view;
    }
}
