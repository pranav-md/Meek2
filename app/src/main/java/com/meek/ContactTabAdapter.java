package com.meek;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 24-Dec-17.
 */

public class ContactTabAdapter extends FragmentPagerAdapter{
    private final List<Fragment> mFragmentList=new ArrayList<>();
    private final List<String> mFragmentTitleList=new ArrayList<>();

    public ContactTabAdapter(FragmentManager fm) {
        super(fm);
    }
    public void addFragment(Fragment fragment,String title)
    {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
