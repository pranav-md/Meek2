package com.meek.Messaging;



import java.util.Date;

/**
 * Created by User on 22-May-18.
 */

public class Message{
    String msg_id;
    String sender_id,text;
    String msgdate;
    public Message(String sender_id,String text, String msgdate)
    {
        this.sender_id=sender_id;
        this.text=text;
        this.msgdate=msgdate;
    }
    public Message(String msg_id)
    {
        this.msg_id=msg_id;
    }

    public void setMsgDlg(String sender_id,String text)
    {
        this.sender_id=sender_id;
        this.text=text;
    }
    public String getSender_id()
    {
        return sender_id;
    }

    public String getMsg_id()
    {
        return msg_id;
    }

    public String getText() {
        return text;
    }


    public String getCreatedAt() {
        return msgdate;
    }
}
