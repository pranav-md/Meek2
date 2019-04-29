package com.meek.AccountManage;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
                final String key=enterkey.getText().toString();
                final DatabaseReference act_ref = FirebaseDatabase.getInstance().getReference();
                final SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);

                act_ref.child("Users")
                        .child(pref.getString("uid",""))
                        .child("key_check").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            if(dataSnapshot.getValue().toString().equals(new AES().encrypt("meekforever",key)))
                            {
                                Toast.makeText(EnterKey.this,"Success",Toast.LENGTH_LONG).show();

                                nextActivity(key);
                            }
                            else
                            {
                                Toast.makeText(EnterKey.this,"Key mismatched",Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            act_ref.child("Users").child(pref.getString("uid","")).child("key_check").setValue(new AES().encrypt("meekforever",key));
                            nextActivity(key);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
    void nextActivity(String key)
    {
        setKey(key);
        Intent intent=new Intent(EnterKey.this,MainActivity.class);
        intent.putExtra("ServerKey",server_key);
        startActivity(intent);
        finish();
    }
}
