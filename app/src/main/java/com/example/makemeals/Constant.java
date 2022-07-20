package com.example.makemeals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Constant {
    public final static int IMAGE_RADIUS = 10;
    public final static String IMAGE_BASE_URL = "https://spoonacular.com/cdn/ingredients_100x100/";
    public final static int HOME = 0;
    public final static int SEARCH = 1;
    public final static int FAVORITES = 2;
    public final static int NEUTRAL = -1;
    public final static String OBJECT_ID = "objectId";
    public final static String USER = "user";
    public static final String SPOONACULAR_BASE_URL = "https://api.spoonacular.com/";
    public static final String SPN_API_KEY = BuildConfig.SPN_API_KEY;
    public static final int MAX_RESULTS = 10;
    public static final String RESULTS = "results";
    public static final String RECIPE_SEARCH_URL = String.format("%s/recipes/complexSearch", SPOONACULAR_BASE_URL);
    public static final int MAX_CATEGORY_RECOMMENDATIONS = 3;
    public static final int MAX_CATEGORY_RECOMMENDATIONS_RESULTS = 5;
    public static final int REQUEST_LIMIT = 20;
    public static final int RANDOM_MAX = 10;
    public static final String API_KEY = "apiKey";
    public static final String CUISINE = "cuisine";
    public static final String DIET = "diet";
    public static final String TYPE = "type";
    public static final String NUMBER = "number";
    public static final String INCLUDE_INGREDIENTS = "includeIngredients";
    public static final String FILL_INGREDIENTS = "fillIngredients";
    public static final String ADD_RECIPE_INFORMATION = "addRecipeInformation";
    public static final String ADD_RECIPE_NUTRITION = "addRecipeNutrition";
    public static final String INSTRUCTIONS_REQUIRED = "instructionsRequired";
    public static final String OFFSET = "offset";
}
