package com.meek;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by User on 25-Dec-17.
 */

public class ContactsListFrag extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.contacts_tab,container,false);

        return view;
    }
}
