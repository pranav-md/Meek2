package com.meek;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MapStyleOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,AdapterView.OnItemSelectedListener, AdapterView.OnItemLongClickListener, View.OnLongClickListener {

    GoogleMap mMap;
    MapStyleOptions style;
    boolean map_tab_flg=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.container);

        final Button map_tab_btn=findViewById(R.id.map_tab_btn);
        map_tab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map_tab_flg=!map_tab_flg;
                if(map_tab_flg)
                {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.flip_right_in, R.animator.flip_right_out, R.animator.flip_left_in, R.animator.flip_left_out)
                            .replace(R.id.frg_container, new MapsFragment())
                            .commit();
                }
                else
                {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.flip_right_in, R.animator.flip_right_out, R.animator.flip_left_in, R.animator.flip_left_out)
                            .replace(R.id.frg_container, new TabFragment(MainActivity.this))
                            .commit();
                }
            }
        });

        final View view=findViewById(R.id.app_bar);
        ImageView img=findViewById(R.id.imageView);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionDrawable transition = (TransitionDrawable) view.getBackground();
                transition.startTransition(1000);


            }
        });

        TabFragment tabFragment=new TabFragment(MainActivity.this);
        FragmentManager tabfm=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=tabfm.beginTransaction();
        fragmentTransaction.replace(R.id.frg_container,tabFragment);
        fragmentTransaction.commit();

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
}

