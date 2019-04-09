package com.meek.Messaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meek.R;

import java.util.ArrayList;

/**
 * Created by User on 25-Mar-19.
 */

public class MessageListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Message> msgList;
    String uid;

    MessageListAdapter(Context context,ArrayList<Message> msgList,String uid)
    {
        this.context=context;
        this.msgList=msgList;
        this.uid=uid;
    }

    void getData(ArrayList<Message> msgList)
    {
        this.msgList=msgList;
    }

    @Override
    public int getCount() {
        return msgList.size();
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
        TextView m_text;
        if(msgList.get(i).sender_id.equals(uid))
        {
            view = inflater.inflate(R.layout.message_sent, null);
            m_text=(TextView)view.findViewById(R.id.senderText);
        } else {
            view = inflater.inflate(R.layout.message_recieve, null);
            m_text=(TextView)view.findViewById(R.id.receiverText);
        }
        m_text.setText(msgList.get(i).text);
        return view;
    }
}
