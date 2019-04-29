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

import com.meek.Encryption.FingerPrintActivity;
import com.meek.R;

public class StartScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_start_screen);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        String uid=pref.getString("uid", "");
        if (!pref.getString("uid", "").equals("")) {

            Runnable runnable = new Runnable() {
                public void run() {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                    //    new ContactSync().syncContact(AuthenticationActivity.this,pref.getString("uid",""));
                }
            };
            Thread mythread = new Thread(runnable);
            mythread.start();

            startActivity(new Intent(StartScreen.this, FingerPrintActivity.class));
            finish();
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

}
