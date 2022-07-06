package com.example.makemeals;

import android.content.Context;
import android.util.Log;

import com.example.makemeals.models.Ingredient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RestClient extends AsyncHttpClient {
    public static final String SPOONACULAR_BASE_URL = "https://api.spoonacular.com/";
    public static final String DYNAPICTURES_BASE_URL = "https://api.dynapictures.com/designs/25851a4c32";
    public static final String SPN_API_KEY = BuildConfig.SPN_API_KEY;
    public static final String DNP_API_KEY = BuildConfig.DNP_API_KEY;
    public static final int MAX_RESULTS = 10;
    Context context;

    public RestClient(Context context) {
        this.context = context;
    }

    public void complexSearch(List<String> ingredients, String type, String diet, JsonHttpResponseHandler handler) {
        String searchUrl = String.format("%s/recipes/complexSearch", SPOONACULAR_BASE_URL);

        RequestParams params = new RequestParams();
        params.put("apiKey", SPN_API_KEY);
        params.put("number", MAX_RESULTS);
        params.put("includeIngredients", String.join(",", ingredients));
        params.put("type", type);
        params.put("diet", diet);
        params.put("fillIngredients", true);
        params.put("addRecipeInformation", true);
        params.put("addRecipeNutrition", true);
        params.put("instructionsRequired", true);
        get(searchUrl, params, handler);
    }

    public void getSharableImage(JSONObject body, JsonHttpResponseHandler handler) {
        // attach bearer token to request
        String bearerToken = String.format("Bearer %s", DNP_API_KEY);
        addHeader("Authorization", bearerToken);
        addHeader("Content-Type", "application/json");

        Log.i("RestClient", "body: " + body.toString());

        try {
            HttpEntity entity = new StringEntity(body.toString());
            // make request
            post(context, DYNAPICTURES_BASE_URL, entity, "application/json", handler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
