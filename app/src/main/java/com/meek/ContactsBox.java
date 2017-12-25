package com.meek;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by User on 24-Dec-17.
 */

public class ContactsBox extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.users_box,container,false);
        new ContactsTabSet(view).tabSetter();

        return view;
    }

}
class ContactsTabSet extends FragmentActivity {
    View view;
    ContactsTabSet(View view)
    {
        this.view=view;
    }

    public void tabSetter()
    {
        ViewPager mviewPager=(ViewPager)view.findViewById(R.id.tab_container);
        ContactTabAdapter contactTabAdapter=new ContactTabAdapter(getSupportFragmentManager());
        contactTabAdapter.addFragment(new ContactsListFrag(),"Contacts");
        contactTabAdapter.addFragment(new StealthListFrag(),"Stealth");
        mviewPager.setAdapter(contactTabAdapter);
        final TabLayout tabLayout=(TabLayout)view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mviewPager);

    }

}

