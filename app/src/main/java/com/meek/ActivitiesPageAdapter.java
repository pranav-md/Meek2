package com.meek;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by User on 18-Jun-18.

 */
class ActivitiesPageAdapter extends FragmentPagerAdapter
{
    ArrayList<Activities> map_activities;
    String u_uid;

    public ActivitiesPageAdapter(FragmentManager childFragmentManager, String u_uid) {
        super(childFragmentManager);
        this.u_uid=u_uid;
    }
    @Override
    public Fragment getItem(int position) {
        BSActivityFragment bsActivitiesFragment=new BSActivityFragment(u_uid,map_activities.get(position).act_id);
        return bsActivitiesFragment;
    }
    void setData(ArrayList<Activities> map_activities)
    {
        this.map_activities=map_activities;
    }

    @Override
    public int getCount() {
        return map_activities.size();
    }
}

