package com.meek;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.victorminerva.widget.edittext.AutofitEdittext;

/**
 * Created by User on 07-Jun-18.
 */

public class ActivityText extends Fragment {
    boolean active;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.text_activity, container, false);

        final AutofitEdittext act_edit=(AutofitEdittext)view.findViewById(R.id.act_edit);
        act_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(act_edit.getText().length()!=0)
                    active=true;
                return false;
            }
        });

        return view;
    }

}
