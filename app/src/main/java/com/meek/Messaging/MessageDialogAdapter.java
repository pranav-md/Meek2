package com.meek.Messaging;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meek.MainActivity;
import com.meek.R;

import java.util.ArrayList;

/**
 * Created by User on 12-Jun-18.
 */

public class MessageDialogAdapter extends BaseAdapter {
    ArrayList<MsgPPL> msgPPLS;
    Context context;
    public MessageDialogAdapter(Context context)
    {
        this.context=context;
    }

    public void getData(ArrayList<MsgPPL> msgPPLS)
    {
        this.msgPPLS=msgPPLS;
    }

    @Override
    public int getCount() {
        return msgPPLS.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.msg_options, null);

        TextView name=(TextView)view.findViewById(R.id.name);
        TextView badge=(TextView)view.findViewById(R.id.badge);
        name.setText(msgPPLS.get(i).name);

        if(msgPPLS.get(i).num_unread==0)
            badge.setVisibility(View.INVISIBLE);
        else
        {
            badge.setText(msgPPLS.get(i).num_unread + "");
        }

        view.setTag(msgPPLS.get(i).sender_id);
        view.setTag(R.integer.name,msgPPLS.get(i).name);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String r_uid=view.getTag().toString();
                Log.v("Message clicked","r_uid= "+r_uid);
                Intent intent=new Intent(context,MessageListMaker.class);
                intent.putExtra("r_uid",r_uid);
                MainActivity mainActivity=(MainActivity)context;
                intent.putExtra("ServerKey",mainActivity.server_key);
            //    intent.putExtra("name",view.getTag(R.integer.name).toString());
                context.startActivity(intent);
            }
        });
        return view;
    }
}
