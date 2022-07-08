package com.example.makemeals.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONObject;

@ParseClassName("ShoppingItem")
public class ShoppingItem extends ParseObject {
    public static final String FIELDS = "fields";
    public static final String KEY_IS_CHECKED = "isChecked";

    public ShoppingItem() {
        super();
    }

    public ShoppingItem(JSONObject jsonObject) {
        setFields(jsonObject);
        setIsChecked(false);
    }

    public JSONObject getFields() {
        return getJSONObject(FIELDS);
    }

    public void setFields(JSONObject jsonObject) {
        put(FIELDS, jsonObject);
    }

    public boolean getIsChecked() {
        return getBoolean(KEY_IS_CHECKED);
    }

    public void setIsChecked(boolean isChecked) {
        put(KEY_IS_CHECKED, isChecked);
    }
}
