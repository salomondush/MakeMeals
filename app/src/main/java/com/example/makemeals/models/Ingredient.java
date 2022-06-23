package com.example.makemeals.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Ingredient")
public class Ingredient extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String CREATED_AT = "createdAt";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }
}
