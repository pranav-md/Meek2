package com.meek.AccountManage;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.meek.MainActivity;
import com.meek.R;

public class EnterKey extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_key);
        Button enter_key=(Button)findViewById(R.id.enter_key);
        enter_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EnterKey.this, MainActivity.class));
            }
        });

    }
    void setKey()
    {
        SharedPreferences getPref=getSharedPreferences("USERKEY",MODE_PRIVATE);
       // SharedPreferences.Editor
    }
}
