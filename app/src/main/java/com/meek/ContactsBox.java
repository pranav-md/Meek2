package com.meek;

import android.support.v4.app.Fragment;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by User on 24-Dec-17.
 */

public class ContactsBox extends Fragment {

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.users_box, container, false);
        tabSetter();

        return view;
    }


    public void tabSetter() {
        ViewPager mviewPager = (ViewPager) view.findViewById(R.id.tab_container);
        ContactTabAdapter contactTabAdapter = new ContactTabAdapter(getChildFragmentManager());
        contactTabAdapter.addFragment(new ContactsListFrag(), "Contacts");
        contactTabAdapter.addFragment(new StealthListFrag(), "Stealth");
        mviewPager.setAdapter(contactTabAdapter);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mviewPager);

    }


}