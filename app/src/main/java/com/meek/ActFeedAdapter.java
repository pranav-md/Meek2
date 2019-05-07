package com.meek;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

/**
 * Created by User on 20-Jun-18.
 */

public class ActFeedAdapter extends BaseAdapter {

    ArrayList<ActFeed> actFeeds;
    FragmentManager fragmentManager;
    Context context;
    String serverkey;
    boolean exp_chk=false;

    void getData(ArrayList<ActFeed> actFeeds, Context context, FragmentManager fragmentManager,String serverkey)
   {
       this.actFeeds=actFeeds;
       this.context=context;
        this.fragmentManager=fragmentManager;
        this.serverkey=serverkey;
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
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        final MapActivitiesPageAdapter pg_adapter=new MapActivitiesPageAdapter(fragmentManager,actFeeds.get(i).a_uid,serverkey);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.act_feed_item, null);
        final ExpandableLayout act_views=view.findViewById(R.id.act_expand_layout);
        act_views.setExpanded(false);
        final ViewPager viewPager=view.findViewById(R.id.act_page);
        TextView name=view.findViewById(R.id.act_name);
        name.setText(actFeeds.get(i).name);
        View lin_lyt=(LinearLayout)view.findViewById(R.id.lin_lyt);
        lin_lyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!act_views.isExpanded())
                {
                    act_views.expand();
                    if(!exp_chk)
                    {
                        pg_adapter.setData(actFeeds.get(i).activities);
                        viewPager.setAdapter(pg_adapter);
                        exp_chk=false;
                    }
                }
                else
                    act_views.collapse();
            }
        });

        return view;
    }
}
