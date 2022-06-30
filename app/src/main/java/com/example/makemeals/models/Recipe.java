package com.example.makemeals.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Recipe")
public class Recipe extends ParseObject {
    public static final String SPN_ID = "spoonacularId";
    public static final String TITLE = "title";
    public static final String IMAGE_URL = "imageUrl";
    public static final String FAVORITE = "favorite";
    public static final String SAVED = "saved";
    public static final String KEY_USER = "user";

    // implement the default constructor
    public Recipe() {
        super();
    }

    public Recipe (JSONObject jsonObject) throws JSONException {
        setSpnId(jsonObject.getInt("id"));
        setTitle(jsonObject.getString("title"));
        setImageUrl(jsonObject.getString("image"));
        setFavorite(false);
        setSaved(false);
        setUser(ParseUser.getCurrentUser());
    }

    public static List<Recipe> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            recipes.add(new Recipe(jsonArray.getJSONObject(i)));
        }
        return recipes;
    }

    public int getSpnId() {
        return getInt(SPN_ID);
    }

    public void setSpnId(int spnId) {
        put(SPN_ID, spnId);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getTitle() {
        return getString(TITLE);
    }

    public void setTitle(String title) {
        put(TITLE, title);
    }

    public String getImageUrl() {
        return getString(IMAGE_URL);
    }

    public void setImageUrl(String imageUrl) {
        put(IMAGE_URL, imageUrl);
    }

    public boolean getFavorite() {
        return getBoolean(FAVORITE);
    }

    public void setFavorite(boolean favorite) {
        put(FAVORITE, favorite);
    }

    public boolean getSaved() {
        return getBoolean(SAVED);
    }

    public void setSaved(boolean saved) {
        put(SAVED, saved);
    }
}
