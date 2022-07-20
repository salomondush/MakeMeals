package com.example.makemeals.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONObject;

@ParseClassName("Recommendation")
public class Recommendation extends ParseObject {
    private static final String DIETS = "diets";
    private static final String CUISINES = "cuisines";

    public static final String USER = "user";

    public Recommendation() {
        super();
    }

    public void setUser(ParseUser user) {
        put(USER, user);
    }

    public ParseUser getUser() {
        return getParseUser(USER);
    }

    public void setDiets(JSONObject diets) {
        put(DIETS, diets);
    }

    public JSONObject getDiets() {
        return getJSONObject(DIETS);
    }

    public void setCuisines(JSONObject cuisines) {
        put(CUISINES, cuisines);
    }

    public JSONObject getCuisines() {
        return getJSONObject(CUISINES);
    }
}
