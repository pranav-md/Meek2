package com.meek;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

/**
 * Created by User on 12-Jun-18.
 */

public class MsgDialogAdapter extends BaseAdapter {
    ArrayList<MsgPPL> msgPPLS;
    Context context;
    MsgDialogAdapter(Context context)
    {
        this.context=context;
    }

    void getData(ArrayList<MsgPPL> msgPPLS)
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.msg_options, null);

        TextView name=(TextView)view.findViewById(R.id.name);
        TextView badge=(TextView)view.findViewById(R.id.badge);
        TextView msg_came=(TextView)view.findViewById(R.id.msg_came);
        EditText msg_taker=(EditText)view.findViewById(R.id.reply_box);
        Button reply_expand=(Button) view.findViewById(R.id.reply_expand);
        Button reply_send=(Button) view.findViewById(R.id.reply_bttn);
        final ExpandableLayout reply_view=(ExpandableLayout)view.findViewById(R.id.reply_expandable_layout);
        name.setText(msgPPLS.get(i).name);

        if(msgPPLS.get(i).num_unread==0)
            badge.setVisibility(View.INVISIBLE);
        else
        {
            badge.setText(msgPPLS.get(i).num_unread + "");
            msg_came.setText(msgPPLS.get(i).last_msg);
        }
        reply_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reply_view.toggle();
            }
        });

        return view;
    }
}
