package com.meek.Authentication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.meek.MainActivity;
import com.meek.R;

import static com.meek.MainActivity.MY_PERMISSIONS_READ_CONTACTS;

/**
 * Created by User on 28-Apr-19.
 */

public class PermissionAsker extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_asker);
        Button btn=(Button)findViewById(R.id.permission);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermission();
            }
        });

    }

    void askPermission()
    {
        try {
            if (ActivityCompat.checkSelfPermission(PermissionAsker.this,
                    android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {//Checking permission

               /* con_sync.putString("CON_FLAG", "SYNCED");
                sc = "SYNCED";
                con_sync.commit();
                */
            } else {
                //Ask for READ_CONTACTS permission
                ActivityCompat.requestPermissions(PermissionAsker.this, new String[]{android.Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, MY_PERMISSIONS_READ_CONTACTS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show();
        startActivity(new Intent(this,AuthenticationActivity.class));

    }
}
