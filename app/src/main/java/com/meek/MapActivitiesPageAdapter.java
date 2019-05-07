package com.meek;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.meek.Fragments.BSActivityFragment;

import java.util.ArrayList;

/**
 * Created by User on 18-Jun-18.

 */
class MapActivitiesPageAdapter extends FragmentPagerAdapter
{
    ArrayList<Activities> activities;
    String u_uid;
    String serverkey;

    public MapActivitiesPageAdapter(FragmentManager childFragmentManager, String u_uid,String serverkey) {
        super(childFragmentManager);
        this.u_uid=u_uid;
        this.serverkey=serverkey;
    }
    @Override
    public Fragment getItem(int position)
    {

        Log.v("MAPSACTADPT","uid="+u_uid+"  actid="+activities.get(position).act_id);
        BSActivityFragment bsActivitiesFragment=new BSActivityFragment(u_uid,activities.get(position).act_id,serverkey);
        return bsActivitiesFragment;
    }
    void setData(ArrayList<Activities> map_activities)
    {
        this.activities=map_activities;
    }

    @Override
    public int getCount()
    {
        return activities.size();
    }
}

