package com.meek.Messaging;



import java.util.Date;

/**
 * Created by User on 22-May-18.
 */

public class Message{
    String sender_id,text;
    String msgdate;
    public Message(String sender_id,String text, String name, String msgdate)
    {
        this.sender_id=sender_id;
        this.text=text;
        this.msgdate=msgdate;
    }

    public String getText() {
        return text;
    }


    public String getCreatedAt() {
        return msgdate;
    }
}
