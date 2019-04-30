package com.meek.Authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meek.Encryption.FingerPrintActivity;
import com.meek.MainActivity;
import com.meek.R;

public class StartScreen extends AppCompatActivity {
String uid,serverkey;
boolean keylock=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_start_screen);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        uid=pref.getString("uid", "");
        if (!pref.getString("uid", "").equals("")) {

            Runnable runnable = new Runnable() {
                public void run() {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                    //    new ContactSync().syncContact(AuthenticationActivity.this,pref.getString("uid",""));
                }
            };
            Thread mythread = new Thread(runnable);
            mythread.start();
            findViewById(R.id.get_started).setVisibility(View.INVISIBLE);
            getServerKey();
        }

        Button btn=(Button)findViewById(R.id.get_started);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartScreen.this,PermissionAsker.class));
                finish();
            }
        });

    }

    void getServerKey()
    {
        DatabaseReference act_feed_ref = FirebaseDatabase.getInstance().getReference();

        act_feed_ref.child("Server_Key").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                serverkey=dataSnapshot.getValue().toString();
                keylock=true;
                Intent intent=new Intent(StartScreen.this, MainActivity.class);
                intent.putExtra("ServerKey",serverkey);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
