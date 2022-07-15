package com.example.makemeals.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ParseClassName("Recipe")
public class Recipe extends ParseObject {
    public static final String SPN_ID = "spoonacularId";
    public static final String TITLE = "title";
    public static final String IMAGE_URL = "imageUrl";
    public static final String FAVORITE = "favorite";
    public static final String SAVED = "saved";
    public static final String KEY_USER = "user";
    public static final String DIETS = "diets";
    public static final String DISH_TYPES = "dishTypes";
    public static final String READY_IN = "readyInMinutes";
    public static final String SERVINGS = "servings";
    public static final String EXTENDED_INGREDIENTS = "extendedIngredients";
    public static final String NUTRIENTS = "nutrients";
    public static final String ANALYZED_INSTRUCTIONS = "analyzedInstructions";

    // implement the default constructor
    public Recipe() {
        super();
    }

    public Recipe (JSONObject jsonObject) throws JSONException {
        setSpnId(jsonObject.getInt("id"));
        setTitle(jsonObject.getString("title"));
        setImageUrl(jsonObject.getString("image"));
        setDiets(jsonObject.getJSONArray("diets"));
        setDishTypes(jsonObject.getJSONArray("dishTypes"));
        setReadyInMinutes(jsonObject.getInt("readyInMinutes"));
        setServings(jsonObject.getInt("servings"));
        setNutrients(jsonObject.getJSONObject("nutrition").getJSONArray("nutrients"));
        setExtendedIngredients(jsonObject.getJSONArray("extendedIngredients"));
        setAnalyzedInstructions(jsonObject.getJSONArray("analyzedInstructions"));
        setFavorite(false);
        setSaved(false);
        setUser(ParseUser.getCurrentUser());
    }

    public static Recipe newInstanceIfNotExists(JSONObject jsonObject) throws JSONException {
        int spnId = jsonObject.getInt("id");
        Recipe recipe = Recipe.getRecipeBySpnId(spnId);

        if (recipe == null) {
            recipe = new Recipe(jsonObject);
        }
        return recipe;
    }

    private static Recipe getRecipeBySpnId(int spnId) {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.whereEqualTo(SPN_ID, spnId);
        try {
            return query.getFirst();
        } catch (com.parse.ParseException e) {
            return null;
        }
    }

    public static List<Recipe> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            recipes.add(newInstanceIfNotExists(jsonArray.getJSONObject(i)));
        }
        return recipes;
    }

    public static List<JSONObject> getExtendedIngredientsFromJsonArray(JSONArray jsonArray) {
        List<JSONObject> extendedIngredients = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            extendedIngredients.add(jsonArray.optJSONObject(i));
        }
        return extendedIngredients;
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

    public JSONArray getDiets() {
        return getJSONArray(DIETS);
    }

    public void setDiets(JSONArray diets) {
        put(DIETS, diets);
    }

    public JSONArray getDishTypes() {
        return getJSONArray(DISH_TYPES);
    }

    public void setDishTypes(JSONArray dishTypes) {
        put(DISH_TYPES, dishTypes);
    }

    public int getReadyInMinutes() {
        return getInt(READY_IN);
    }

    public void setReadyInMinutes(int readyInMinutes) {
        put(READY_IN, readyInMinutes);
    }

    public int getServings() {
        return getInt(SERVINGS);
    }

    public void setServings(int servings) {
        put(SERVINGS, servings);
    }

    public List<JSONObject> getExtendedIngredients() {
        return getExtendedIngredientsFromJsonArray(Objects.requireNonNull(getJSONArray(EXTENDED_INGREDIENTS)));
    }

    public void setExtendedIngredients(JSONArray extendedIngredients) {
        put(EXTENDED_INGREDIENTS, extendedIngredients);
    }

    public JSONArray getAnalyzedInstructions() {
        return getJSONArray(ANALYZED_INSTRUCTIONS);
    }

    public void setAnalyzedInstructions(JSONArray analyzedInstructions) {
        put(ANALYZED_INSTRUCTIONS, analyzedInstructions);
    }

    public JSONArray getNutrients() {
        return getJSONArray(NUTRIENTS);
    }

    public void setNutrients(JSONArray nutrients) {
        put(NUTRIENTS, nutrients);
    }
}
