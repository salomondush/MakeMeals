package com.example.makemeals.ViewModel;

import static com.example.makemeals.fragments.HomeFragment.TIME_FORMAT;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.makemeals.Constant;
import com.example.makemeals.models.Recipe;
import com.example.makemeals.models.Recommendation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * View model to query and hold user recommendations data
 *
 * Stores user recommendations in an ArrayList with format List<HashMap<String, List<Recipe>>>
 * where the String key for the hashmap has format "category_type*category_name"
 *
 * So for example, if we have a category with type "diet" and name "vegetarian",
 * the string would be "diet*vegetarian"
 *
 * Then we can get the list of recipes for that category using the name and type of that category
 */
public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<HashMap<String, List<Recipe>>>> recommendationData;
    private final List<HashMap<String, List<Recipe>>> recommendations = new ArrayList<>();

    private final OkHttpClient client = new OkHttpClient();
    private String mealType;
    private static final String BREAKFAST = "breakfast";
    private static final String LUNCH = "lunch";
    private static final String DINNER = "dinner";
    private static final String SNACK = "snack";


    public MutableLiveData<List<HashMap<String, List<Recipe>>>> getRecommendedRecipes() {
        if (recommendationData == null) {
            recommendationData = new MutableLiveData<>();
            // call loadRecipes method with call back to get the data
//            processRecommendations();
        }
        return recommendationData;
    }

    public void processRecommendations() {
//        recommendationData

        // get local HOUR of the day
        String localTime = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(new Date());
        int hour = Integer.parseInt(localTime);

        if (hour >= 6 && hour <= 10) {
            mealType = BREAKFAST;
        } else if (hour >= 11 && hour <= 14) {
            mealType = LUNCH;
        } else if (hour >= 15 && hour <= 18) {
            mealType = DINNER;
        } else {
            mealType = SNACK;
        }
        getUserRecommendations();
    }

    /**
     * Get the user's recommendations object and Ingredients from Parse, get top recommendations,
     * and get recipes for each category
     */
    private void getUserRecommendations() {
        ParseQuery<Recommendation> query = ParseQuery.getQuery(Recommendation.class);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        query.whereEqualTo(Recommendation.USER, ParseUser.getCurrentUser());
        query.getFirstInBackground((recommendation, e) -> {
            if (e == null) {
                JSONObject diets = recommendation.getDiets();
                JSONObject cuisines = recommendation.getCuisines();
                // get top recommendations from the two categories
                addTopRecommendations(diets, cuisines);

                for (int i = 0; i < recommendations.size(); i++) {
                    HashMap<String, List<Recipe>> category = recommendations.get(i);
                    getRecipesByCategory(category, i);
                }
            } else {
                e.printStackTrace();
            }
        });
    }

    /**
     * Get top MAX_CATEGORY_RECOMMENDATIONS from each category and add them to the recommendation
     * Adapter's List.
     *
     * @param diets
     * @param cuisines
     */
    private void addTopRecommendations(JSONObject diets, JSONObject cuisines) {
        // get the first 3 elements with the highest value in diets
        recommendations.clear(); // for new recommendation data
        HashMap<String, Integer> dietsMap = new HashMap<>();
        HashMap<String, Integer> cuisinesMap = new HashMap<>();

        for (int i = 0; i < diets.length(); i++) {
            String key = Objects.requireNonNull(diets.names()).optString(i);
            int value = diets.optInt(key);
            dietsMap.put(key, value);
        }

        for (int i = 0; i < cuisines.length(); i++) {
            String key = Objects.requireNonNull(cuisines.names()).optString(i);
            int value = cuisines.optInt(key);
            cuisinesMap.put(key, value);
        }

        // create a sortedList of the dietsMap
        List<HashMap.Entry<String, Integer>> dietsList = new ArrayList<>(dietsMap.entrySet());
        dietsList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        // create a sortedList of the cuisinesMap
        List<HashMap.Entry<String, Integer>> cuisinesList = new ArrayList<>(cuisinesMap.entrySet());
        cuisinesList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        // add the MAX_CATEGORY_RECOMMENDATIONS diets to the categories list
        for (int i = 0; i < Constant.MAX_CATEGORY_RECOMMENDATIONS; i++) {
            HashMap<String, List<Recipe>> map = new HashMap<>();
            // format string key "diet*vegetarian"
            map.put(Constant.DIET + "*" + dietsList.get(i).getKey(), new ArrayList<>());
            recommendations.add(map);
        }

        // add the MAX_CATEGORY_RECOMMENDATIONS cuisines to the categories list
        for (int i = 0; i < Constant.MAX_CATEGORY_RECOMMENDATIONS; i++) {
            HashMap<String, List<Recipe>> map = new HashMap<>();
            map.put(Constant.CUISINE + "*" + cuisinesList.get(i).getKey(), new ArrayList<>());
            recommendations.add(map);
        }

        Log.i("HomeViewModel", "recommendations: " + recommendations);
    }

    /**
     * Query recipes satisfying the recommended diet and cuisine categories from the
     * Spoonacular API
     *
     * @param category
     * @param index
     */
    private void getRecipesByCategory(HashMap<String, List<Recipe>> category, int index) {
        HttpUrl.Builder urlBuilder =
                Objects.requireNonNull(HttpUrl.parse(Constant.RECIPE_SEARCH_URL)).newBuilder();
        urlBuilder.addQueryParameter(Constant.API_KEY, Constant.SPN_API_KEY);
        urlBuilder.addQueryParameter(Constant.TYPE, mealType);

        // check if hashmap key contains substring "diet"
        String typeName = category.keySet().toArray()[0].toString();
        if (typeName.contains(Constant.DIET)) {
            urlBuilder.addQueryParameter(Constant.DIET, typeName.split("\\*")[1]);
        } else {
            urlBuilder.addQueryParameter(Constant.CUISINE, typeName.split("\\*")[1]);
        }

        urlBuilder.addQueryParameter(Constant.NUMBER,
                String.valueOf(Constant.MAX_CATEGORY_RECOMMENDATIONS_RESULTS));

        urlBuilder.addQueryParameter(Constant.FILL_INGREDIENTS, String.valueOf(true));
        urlBuilder.addQueryParameter(Constant.ADD_RECIPE_INFORMATION, String.valueOf(true));
        urlBuilder.addQueryParameter(Constant.ADD_RECIPE_NUTRITION, String.valueOf(true));
        urlBuilder.addQueryParameter(Constant.INSTRUCTIONS_REQUIRED, String.valueOf(true));
        urlBuilder.addQueryParameter(Constant.OFFSET,
                String.valueOf((int) (Math.random() * Constant.RANDOM_MAX)));

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                final String responseData = Objects.requireNonNull(response.body()).string();
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJson =
                                new JSONObject(responseData);

                        category.put(typeName, Recipe.fromJsonArray(responseJson.getJSONArray(Constant.RESULTS)));
                        recommendations.set(index, category);

                        // after getting data for the last category, call the callback
                        if (index == recommendations.size() - 1) {
                            recommendationData.postValue(recommendations);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
