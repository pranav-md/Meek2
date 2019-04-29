package com.meek;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.meek.Encryption.AES;
import com.victorminerva.widget.edittext.AutofitEdittext;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 07-Jun-18.
 */

@SuppressLint("ValidFragment")
public class ActivityText extends Fragment {
    boolean active;
    String serverkey;
    @SuppressLint("ValidFragment")
    public ActivityText(String serverkey)
    {
        this.serverkey=serverkey;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.text_activity, container, false);
        view.setTag(5);

        final SharedPreferences actPrefs= getContext().getSharedPreferences("ActPrefs", MODE_PRIVATE);
        final int curr_stat=actPrefs.getInt("curr_stat",11);
        final SharedPreferences.Editor actPrefEdit=actPrefs.edit();

        final AutofitEdittext act_edit=(AutofitEdittext)view.findViewById(R.id.act_edit);
        act_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.v("Text change","txt changedd");
                if(charSequence.length()!=0) {
                    active = true;
                    actPrefEdit.putInt("curr_stat",25);
                    actPrefEdit.commit();
                }
                else
                {
                    actPrefEdit.putInt("curr_stat",5);
                    actPrefEdit.commit();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                CreateActivity act_funcall = (CreateActivity) getContext();
                SharedPreferences getPref=getContext().getSharedPreferences("USERKEY",MODE_PRIVATE);
                String key=new AES().decrypt(getPref.getString("KEY",""),serverkey);

                act_funcall.setCaptionText(new AES().encrypt(act_edit.getText().toString(),key));
            }
        });

        return view;
    }

}

