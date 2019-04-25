package com.meek.AccountManage;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.meek.Encryption.AES;
import com.meek.MainActivity;
import com.meek.R;

public class EnterKey extends Activity {

    String server_key;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_key);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        server_key = extras.getString("ServerKey");

        Button enter_key=(Button)findViewById(R.id.enter_key);
        enter_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText enterkey=(EditText)findViewById(R.id.enterkey);
                String key=enterkey.getText().toString();
                setKey(key);
                Intent intent=new Intent(EnterKey.this,MainActivity.class);
                intent.putExtra("ServerKey",server_key);
                startActivity(intent);
                finish();
            }
        });

    }
    void setKey(String key)
    {
        SharedPreferences getPref=getSharedPreferences("USERKEY",MODE_PRIVATE);
        SharedPreferences.Editor setPrefs=getPref.edit();

        setPrefs.putString("KEY",new AES().encrypt(key,server_key));
        setPrefs.commit();
    }
}
