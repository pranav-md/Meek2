package com.meek;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 25-Dec-17.
 */

public class StealthListFrag extends Fragment {
    View view;
    String uid;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.stealth_box, container, false);
        return view;
    }
    void stealthSetter(final Context context)
    {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        TextView stl_tv=(TextView)view.findViewById(R.id.textView8);
        Button select_all=(Button)view.findViewById(R.id.select_all);
        AdaptHelper[] stealth_ppls=new AdaptHelper[150];
        SharedPreferences pref = context.getSharedPreferences("UserDetails", MODE_PRIVATE);
        final String uid=pref.getString("uid","");
        final String[] stealth_list = new String[1];
        final String[] stealth_me = new String[1];
        final String[] stealth_to_me = new String[1];
        final DatabaseReference userRef = database.getReference("Users");
        userRef.child(uid).child("stealth_list").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                stealth_list[0] =dataSnapshot.getValue().toString();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                 stealth_list[0] =dataSnapshot.getValue().toString();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        int num_stl_list=0;
        String stl_list=stealth_list[0];
        if(stl_list.length() <3)
        {
            stl_tv.setText("No meek friends for stealth mode"+stealth_list);
            select_all.setVisibility(View.INVISIBLE);
        }
        else
        {
             for (int i = 0; i < stl_list.length(); i++)
             {
                if (stl_list.charAt(i) == ':')
                {
                 num_stl_list++;
                }
             }
             --num_stl_list;

            for(int i=0;i<num_stl_list;++i)
            {
                stl_list=stl_list.substring(1);
                int pos=stl_list.indexOf(':');
                String s_uid=stl_list.substring(0,pos);
                stl_list=stl_list.substring(pos);
                stealth_ppls[i].setTheValues(uid,pref.getString("Meeked_user_name"+s_uid,""),
                        pref.getString("Meeked_user_dpno"+s_uid,""),
                        pref.getString("Meeked_user_name"+s_uid,""));
            }
            ListView stealth_listview=(ListView)view.findViewById(R.id.stealth_list);
            stealth_listview.setAdapter(new StealthAdapter(stealth_ppls,context,uid));
            stealth_listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position,
                                        long arg3)
                {
                        String s_uid= (String) v.getTag();
                        String u_stl_to_me=(String)v.getTag(2);
                        final String[] stealth = new String[1];
                        final String[] stealth_me = new String[1];
                    final DatabaseReference userRef = database.getReference("Users");
                        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            stealth_me[0]=dataSnapshot.child("stealth_me").getValue().toString();
                            stealth_list[0]=dataSnapshot.child("stealth_list").getValue().toString();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    String u_stl=stealth[0];
                    String u_stl_me=stealth_me[0];
                    String u_stl_list=stealth_list[0];
                    userRef.child(s_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            stealth_to_me[0]=dataSnapshot.child("stealth_to_me").getValue().toString();
                            stealth_list[0]=dataSnapshot.child("stealth_list").getValue().toString();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                        String s_stl=stealth[0];
                        String s_stl_to_me=stealth_to_me[0];
                        String s_stl_list=stealth_list[0];
                        int stealth_check= (int) v.getTag(1);
                        if(stealth_check==0)        ///setting stealth mode
                        {
                            if(u_stl_me.equals(""))
                                u_stl_me=":";
                            u_stl_me+=s_uid+":";
                            u_stl_list=":"+s_uid+u_stl_list.substring(0,u_stl_list.length()).replace(s_uid+":","");
                            s_stl_list=s_stl_list.replace(":"+uid,"")+uid+":";
                            if(s_stl_to_me.equals(""))
                                s_stl_to_me=":";
                            s_stl_to_me+=uid+":";
                            userRef.child(uid).child("stealth_me").setValue(u_stl_me);
                            userRef.child(uid).child("stealth_list").setValue(u_stl_list);
                            userRef.child(s_uid).child("stealth_to_me").setValue(s_stl_to_me);
                            userRef.child(s_uid).child("stealth_list").setValue(s_stl_list);

                            TextView s_name=(TextView)v.findViewById(R.id.stealth_name);
                            s_name.setTextColor(Color.parseColor("#ffffff"));
                            v.setBackgroundColor(Color.parseColor("#000000"));
                            Drawable stealth_on = context.getResources().getDrawable(R.drawable.stealth_on);
                            ImageView s_on_off=(ImageView)v.findViewById(R.id.stealth_mode);
                            s_on_off.setImageDrawable(stealth_on);
                        }
                        else                /////cancelling stealth mode
                        {
                            u_stl_me=u_stl_me.replace(s_uid+":","");
                            s_stl_to_me=s_stl_to_me.replace(uid+":","");        //replacing in those s_stl_to_me and u_stl_me

                            u_stl_list.replace(s_uid+":","");
                            u_stl_list=u_stl_me+s_uid+":"+u_stl_list.substring(u_stl_me.length()-1,u_stl_list.length());    //extracting a specific elemnt and setting their

                            s_stl_list=s_stl_list.replace(uid+":","");
                            s_stl_list=s_stl_list.substring(0,s_stl_list.length()-s_stl_to_me.length())+uid+":"+s_stl_to_me;

                            TextView s_name=(TextView)v.findViewById(R.id.stealth_name);

                            if(!u_stl_to_me.contains(s_uid+":")) {
                                s_name.setTextColor(Color.parseColor("#000000"));
                                v.setBackgroundColor(Color.parseColor("#ffffff"));
                                Drawable stealth_off = context.getResources().getDrawable(R.drawable.stealth_off);
                                ImageView s_on_off = (ImageView) v.findViewById(R.id.stealth_mode);
                                s_on_off.setImageDrawable(stealth_off);
                            }
                            else
                            {
                                s_name.setTextColor(Color.parseColor("#424242"));
                                v.setBackgroundColor(Color.parseColor("#BDBDBD"));
                                Drawable stealth_on = context.getResources().getDrawable(R.drawable.stealth_on);
                                ImageView s_on_off = (ImageView) v.findViewById(R.id.stealth_mode);
                                s_on_off.setImageDrawable(stealth_on);
                            }
                        }

                }
            });
        }
    }
}