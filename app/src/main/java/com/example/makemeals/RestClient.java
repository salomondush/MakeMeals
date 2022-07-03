package com.example.makemeals;

import com.example.makemeals.models.Ingredient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.util.List;

public class RestClient extends AsyncHttpClient {
    public static final String BASE_URL = "https://api.spoonacular.com/";
    public static final String API_KEY = BuildConfig.API_KEY;
    public static final int MAX_RESULTS = 10;

    // todo: add query functions later


    public void complexSearch(List<String> ingredients, String type, String diet, JsonHttpResponseHandler handler) {
        String searchUrl = String.format("%s/recipes/complexSearch", BASE_URL);

        RequestParams params = new RequestParams();
        params.put("apiKey", API_KEY);
        params.put("number", MAX_RESULTS);
        params.put("includeIngredients", String.join(",", ingredients));
        params.put("type", type);
        params.put("diet", diet);
        get(searchUrl, params, handler);
    }
}
