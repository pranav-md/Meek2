package com.meek;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.i18n.phonenumbers.NumberParseException;
import com.meek.Fragments.MyProfileFrag;
import com.meek.Service.ActivityService;
import com.meek.Service.LocationService;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,AdapterView.OnItemSelectedListener, AdapterView.OnItemLongClickListener, View.OnLongClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mMap;
    GoogleApiClient mApiClient;
    MapStyleOptions style;
    boolean map_tab_flg = false;
    public final static int MY_PERMISSIONS_READ_CONTACTS = 0x1;
    String sc;
    SharedPreferences sp;
    TabFragment tabFragment;
    CircleImageView dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);
        dp = (CircleImageView) findViewById(R.id.dp);
        tabFragment = new TabFragment(MainActivity.this);
        /////////
      //  CompressAsyncTask task = new CompressAsyncTask(this);
    //    task.execute();
        /////////////////
        FragmentManager tabfm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = tabfm.beginTransaction();
        fragmentTransaction.replace(R.id.frg_container, tabFragment);
        fragmentTransaction.commit();

      //  startService(new Intent(MainActivity.this, LocationService.class));

        sp = getSharedPreferences("CONTACT_SYNC", Context.MODE_PRIVATE);
        sc = sp.getString("CON_FLAG", "");
        if (sc.equals("")) {

            try {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED) {//Checking permission
                    SharedPreferences.Editor con_sync = sp.edit();
                    con_sync.putString("CON_FLAG", "SYNCED");
                    sc = "SYNCED";
                    con_sync.commit();
                } else {
                    //Ask for READ_CONTACTS permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_READ_CONTACTS);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        final Button map_tab_btn = findViewById(R.id.map_tab_btn);
        map_tab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map_tab_flg = !map_tab_flg;
                if (map_tab_flg) {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.flip_right_in, R.animator.flip_right_out, R.animator.flip_left_in, R.animator.flip_left_out)
                            .replace(R.id.frg_container, new MapsFragment())
                            .commit();
                } else {
                    tabFragment=new TabFragment(MainActivity.this);
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.flip_right_in, R.animator.flip_right_out, R.animator.flip_left_in, R.animator.flip_left_out)
                            .replace(R.id.frg_container,tabFragment )
                            .commit();
                }
            }
        });

        final View view = findViewById(R.id.app_bar);
        ImageView img = findViewById(R.id.imageView);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences mypref = getSharedPreferences("UserDetails", MODE_PRIVATE);
                String uid=mypref.getString("uid","");
                setColor(1);
                //         new ContactSync().syncContact("update",MainActivity.this,uid);
                /*try {
                    new ContactSync().syncContact("update", MainActivity.this,uid);

                } catch (NumberParseException e) {
                    e.printStackTrace();
                }

*/
            }
        });

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
    //    startContactLookService();
        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyProfileFrag.class);
                startActivity(intent);
            }

        });
    }
    void setColor(int i)
    {
        View view = findViewById(R.id.app_bar);
        SharedPreferences mypref = getSharedPreferences("UserDetails", MODE_PRIVATE);
        if(i==1)
        {
            SharedPreferences.Editor edt_clr=mypref.edit();
            view.setBackground(ContextCompat.getDrawable(MainActivity.this,getResources().getIdentifier("clr_trans"+mypref.getInt("clr",0),"drawable","com.meek")));
            TransitionDrawable transition = (TransitionDrawable) view.getBackground();
            transition.startTransition(1000);
           Window up_bar=getWindow();
           up_bar.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
           up_bar.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            up_bar.setStatusBarColor(getResources().getIdentifier("clr"+((mypref.getInt("clr",0)+1)%7),"color","com.meek"));
            edt_clr.putInt("clr",(mypref.getInt("clr",0)+1)%7);
            edt_clr.commit();

        }
        else
        {
            int clr=mypref.getInt("clr",0);
            view.setBackground(ContextCompat.getDrawable(MainActivity.this,getResources().getIdentifier("clr_trans"+clr,"drawable","com.meek")));
        }

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //If permission granted
        if (requestCode == MY_PERMISSIONS_READ_CONTACTS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (sc.equals("")) {

                SharedPreferences.Editor con_sync = sp.edit();
                con_sync.putString("CON_FLAG", "SYNCED");
                sc = "SYNCED";
                con_sync.commit();
            }
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("Camera daa", "handle mainactivty activity");
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment f : fragments) {
                if (f instanceof TabFragment) {
                    Log.v("Camera daa", "instance is okay");
                    tabFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

}