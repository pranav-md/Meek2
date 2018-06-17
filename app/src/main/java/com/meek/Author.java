package com.meek;

import com.stfalcon.chatkit.commons.models.IUser;

/**
 * Created by User on 22-May-18.
 */

public class Author implements IUser {
    String id,name;
    public Author(String id, String name)
    {
        this.id=id;
        this.name=name;
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return null;
    }
}
