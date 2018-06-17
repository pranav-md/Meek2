package com.meek;

import com.stfalcon.chatkit.commons.models.IMessage;

import java.util.Date;

/**
 * Created by User on 22-May-18.
 */

public class Message implements IMessage {
    String id,text;
    Author author;
    Date msgdate;
    public Message(String id, String text, String name, Date msgdate)
    {
        this.id=id;
        this.text=text;
        this.author=new Author(id,name);
        this.msgdate=msgdate;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return msgdate;
    }
}
