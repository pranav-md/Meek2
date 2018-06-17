package com.meek;

import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Created by User on 08-Jun-18.
 */

public class MyPlaces extends AppCompatActivity implements OnMapReadyCallback,AdapterView.OnItemSelectedListener, AdapterView.OnItemLongClickListener, View.OnLongClickListener {
    ArrayList<Places> plcs_list;
    boolean list_on=false;
    String uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_places);
        SharedPreferences pref = this.getSharedPreferences("UserDetails", MODE_PRIVATE);
        uid=pref.getString("uid", "");

        plcs_list=new ArrayList<Places>();
        checkRealm();
        Button save_place=(Button)findViewById(R.id.save_place);
        save_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null)
                {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                DialogFragment dialogFragment = new AddPlaceDialog();
                dialogFragment.show(ft, "dialog");
            }
        });


    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {


    }
    void checkFB()
    {
        final DatabaseReference plc_ref = FirebaseDatabase.getInstance().getReference();
        plc_ref.child("Places_DB").child(uid).child("Info").child("num_places").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Realm myRealm= Realm.getDefaultInstance();
                int num = Integer.parseInt( dataSnapshot.getValue().toString());
                final RealmResults<Places> myplcs= myRealm.where(Places.class).findAll();
                if(num==0)
                {
                    TextView no_plc=(TextView)findViewById(R.id.no_place);
                    no_plc.setVisibility(View.VISIBLE);
                    ProgressBar pbar=(ProgressBar)findViewById(R.id.place_loading);
                    pbar.setVisibility(View.INVISIBLE);
                }
                else if(num>0)
                {
                    plc_ref.child("Places_DB").child(uid).child("Places").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            plcs_list=new ArrayList<Places>();
                           Places my_plc;
                           DataSnapshot ds1 = dataSnapshot.child("Hidden");
                           DataSnapshot ds2 = dataSnapshot.child("Loc_friends");
                            myplcs.deleteAllFromRealm();

                           for(DataSnapshot ds:ds1.getChildren())
                           {
                               my_plc=new Places();
                               my_plc.p_num = Integer.parseInt(ds.getKey().toString());
                               my_plc.name = ds.child("name").getValue().toString();
                               my_plc.type = Integer.parseInt(ds.child("type").getValue().toString());
                               my_plc.visibility =0;
                               my_plc.timestamp = ds.child("date_time_stamp").getValue().toString();
                               my_plc.lat=Double.parseDouble(ds.child("lat").getValue().toString());
                               my_plc.lng=Double.parseDouble(ds.child("lng").getValue().toString());
                               plcs_list.add(my_plc);
                               Log.v("Places checkkk","name="+my_plc.name+"  type"+my_plc.type);
                               final Places finalMy_plc = my_plc;
                               myRealm.executeTransaction(new Realm.Transaction() {
                                   @Override
                                   public void execute(Realm realm)
                                   {
                                       Places plcs=myRealm.createObject(Places.class);
                                       plcs= finalMy_plc;
                                   }
                               });
                           }
                            for(DataSnapshot ds:ds2.getChildren())
                            {
                                my_plc=new Places();
                                my_plc.p_num = Integer.parseInt(ds.getKey().toString());
                                my_plc.name = ds.child("name").getValue().toString();
                                my_plc.type = Integer.parseInt(ds.child("type").getValue().toString());
                                my_plc.visibility =1;
                                my_plc.timestamp = ds.child("date_time_stamp").getValue().toString();
                                my_plc.lat=Double.parseDouble(ds.child("lat").getValue().toString());
                                my_plc.lng=Double.parseDouble(ds.child("lng").getValue().toString());
                                plcs_list.add(my_plc);
                                Log.v("Places checkkk","name="+my_plc.name+"  type"+my_plc.type);
                                final Places finalMy_plc = my_plc;
                                myRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm)
                                    {
                                        Places plcs=myRealm.createObject(Places.class);
                                        plcs= finalMy_plc;
                                    }
                                });
                            }
                            setPlaceList();

                            TextView no_plc=(TextView)findViewById(R.id.no_place);
                            no_plc.setVisibility(View.INVISIBLE);
                            ProgressBar pbar=(ProgressBar)findViewById(R.id.place_loading);
                            pbar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void checkRealm()
    {
        Realm.init(this);
        final Realm myRealm= Realm.getDefaultInstance();
        final RealmResults<Places> myplcs= myRealm.where(Places.class).findAll();
        if(myplcs.size()!=0)
        {
            Log.v("Places checkkk","check realm myplcs size="+myplcs.size());
            for(int i=0;i<myplcs.size();++i)
            {
                plcs_list.add(myplcs.get(i));
                setPlaceList();
            }
        }
        else
        {
            checkFB();
            TextView no_plc=(TextView)findViewById(R.id.no_place);
            no_plc.setVisibility(View.INVISIBLE);
            ProgressBar pbar=(ProgressBar)findViewById(R.id.place_loading);
            pbar.setVisibility(View.VISIBLE);
        }
    }

    void setPlaceList()
    {
        TextView no_plc=(TextView)findViewById(R.id.no_place);
        no_plc.setVisibility(View.INVISIBLE);
        ProgressBar pbar=(ProgressBar)findViewById(R.id.place_loading);
        pbar.setVisibility(View.INVISIBLE);
        ListView plc_list=(ListView)findViewById(R.id.place_list);
        PlaceListAdapter plc_adptr=new PlaceListAdapter();
        plc_adptr.getData(plcs_list,MyPlaces.this);
        if(!list_on)
            plc_list.setAdapter(plc_adptr);
        else
            plc_adptr.notifyDataSetChanged();
        list_on=true;
    }
}
