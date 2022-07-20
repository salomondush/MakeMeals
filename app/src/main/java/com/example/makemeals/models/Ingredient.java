package com.example.makemeals.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Ingredient")
public class Ingredient extends ParseObject {
    private static final String KEY_NAME = "name";
    private static final String CREATED_AT = "createdAt";
    private static final String USER = "user";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public void setCreatedAt(Date createdAt) {
        put(CREATED_AT, createdAt);
    }

    public void setUser(ParseUser user) {
        put(USER, user);
    }

    public ParseUser getUser() {
        return getParseUser(USER);
    }
}
