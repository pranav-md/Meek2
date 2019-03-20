package com.meek;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.meek.Fragments.BSActivityFragment;

import java.util.ArrayList;

/**
 * Created by User on 18-Jun-18.

 */
class MapActivitiesPageAdapter extends FragmentPagerAdapter
{
    ArrayList<Activities> activities;
    String u_uid;

    public MapActivitiesPageAdapter(FragmentManager childFragmentManager, String u_uid) {
        super(childFragmentManager);
        this.u_uid=u_uid;
    }
    @Override
    public Fragment getItem(int position)
    {
        BSActivityFragment bsActivitiesFragment=new BSActivityFragment(u_uid,activities.get(position).act_id);
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

