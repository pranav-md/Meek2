package com.meek;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 25-Dec-17.
 */

@SuppressLint("ValidFragment")
public class ContactsListFrag extends Fragment {
        View view;
        Context context;
        @SuppressLint("ValidFragment")
        ContactsListFrag(Context context)
        {
            this.context=context;
        }
        ContactsListFrag()
        {

        }
      @Override
      public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
      {
        this.view=inflater.inflate(R.layout.contacts_tab,container,false);
        requestSetter();
        meekFriendsSetter();
        contactPeopleSetter();
        return view;
      }
    void requestSetter()
    {
        TextView req_tv=(TextView)view.findViewById(R.id.request_tv);
        AdaptHelper[] requestedPeople=new AdaptHelper[150];
        SharedPreferences pref = context.getSharedPreferences("UserDetails", MODE_PRIVATE);
        String uids=pref.getString("Requests","");
        int req_num=pref.getInt("Request_number",0);
        if(req_num==0)
        {
            req_tv.setVisibility(View.INVISIBLE);
        }
        else
        {
            for(int i=0;i<req_num;++i)
            {
                uids=uids.substring(1);
                int pos=uids.indexOf(':');
                String uid=uids.substring(0,pos);
                uids=uids.substring(pos);
                requestedPeople[i].setTheValues(uid,pref.getString("Request_name"+uid,""),
                                                    pref.getString("Request_dpno"+uid,""),
                                                    pref.getString("Request_phone_no"+uid,""));
            }
            ListView lv=(ListView) view.findViewById(R.id.request_list);
            lv.setAdapter(new RequestAdapter(requestedPeople,context));
        }
    }
    void removeUid(String uid)
    {
        SharedPreferences pref = context.getSharedPreferences("UserDetails", MODE_PRIVATE);
        SharedPreferences.Editor remove_uid=pref.edit();
        remove_uid.putString("Requests",pref.getString("Requests","").replace(":"+uid,""));
        remove_uid.commit();
    }

    void meekFriendsSetter()
    {
        TextView req_tv=(TextView)view.findViewById(R.id.meeked_tv);
        AdaptHelper[] meekFriends=new AdaptHelper[150];
        SharedPreferences pref = context.getSharedPreferences("UserDetails", MODE_PRIVATE);
        String uids=pref.getString("Meek_Friends","");
        int meek_number=pref.getInt("Meek_number",0);
        if(meek_number==0)
        {
            req_tv.setText("No meek friends");
        }
        else
        {
            for(int i=0;i<meek_number;++i)
            {
                meekFriends[i]=new AdaptHelper();
                uids=uids.substring(1);
                int pos=uids.indexOf(':');
                String uid=uids.substring(0,pos);
                uids=uids.substring(pos);
                meekFriends[i].setTheValues(uid,pref.getString("Meeked_user_name"+uid,""),
                        pref.getString("Meeked_user_dpno"+uid,""),
                        pref.getString("Meeked_user_name"+uid,""),
                        pref.getString("Meeked_user_activity"+uid,""));
            }
            ListView meek_list=(ListView)view.findViewById(R.id.meek_list);
            meek_list.setAdapter(new MeekFriendsAdapter(meekFriends,context));
            meek_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position,
                                        long arg3)
                {
                   ///bottom sheet loader
                }
            });
        }
    }
    void contactPeopleSetter()
    {
        TextView con_tv=(TextView)view.findViewById(R.id.contacts_tv);
        AdaptHelper[] meekcontacts=new AdaptHelper[150];
        SharedPreferences pref = context.getSharedPreferences("UserDetails", MODE_PRIVATE);
        String uids=pref.getString("Non_meeked_users","");
        int num_non_meek=pref.getInt("Num_non_meek",0);
        if(num_non_meek==0)
        {
            con_tv.setText("No one in your contact is in meek");
        }
        else
        {
            for(int i=0;i<num_non_meek;++i)
            {
                meekcontacts[i]=new AdaptHelper();
                uids=uids.substring(1);
                int pos=uids.indexOf(':');
                String uid=uids.substring(0,pos);
                uids=uids.substring(pos);
                meekcontacts[i].setTheValues(uid,pref.getString("Meeked_user_name"+uid,""),
                        pref.getString("Meeked_user_dpno"+uid,""));
            }
            ListView meek_list=(ListView)view.findViewById(R.id.contacts_list);
            meek_list.setAdapter(new MeekFriendsAdapter(meekcontacts,context));

        }
    }
}
