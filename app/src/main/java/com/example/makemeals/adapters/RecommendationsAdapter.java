package com.example.makemeals.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makemeals.Constant;
import com.example.makemeals.R;
import com.example.makemeals.databinding.RecommendationItemBinding;
import com.example.makemeals.fragments.RecipesListFragment;
import com.example.makemeals.models.Ingredient;
import com.example.makemeals.models.Recipe;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.ViewHolder>{
    private static final String TAG =  "RecommendationsAdapter";
    private RecommendationItemBinding binding;
    private final List<HashMap<String, String>> categories;
    private final Context context;
    private final String includeIngredients;
    private final OkHttpClient client;
    private final String mealType;

    public RecommendationsAdapter(List<HashMap<String, String>> categories, Context context,
                                  String includeIngredients, String mealType) {
        this.categories = categories;
        this.context = context;
        this.includeIngredients = includeIngredients;
        client = new OkHttpClient();
        this.mealType = mealType;
    }

    @NonNull
    @Override
    public RecommendationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RecommendationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecommendationsAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationsAdapter.ViewHolder holder, int position) {
        HashMap<String, String> category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryTitleTextView;
        private final RecyclerView recyclerViewRecommendedRecipes;
        private List<Recipe> recipes;
        private RecommendedRecipesAdapter recommendedRecipesAdapter;
        private CircularProgressIndicator progressIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTitleTextView = binding.categoryTitleTextView;
            recyclerViewRecommendedRecipes = binding.recyclerViewRecommendedRecipes;
            progressIndicator = binding.progressIndicator;
        }

        public void bind(HashMap<String, String> category) {
            categoryTitleTextView.setText(category.containsKey("diet") ? category.get("diet") : category.get("cuisine"));

            recipes = new ArrayList<>();
            recommendedRecipesAdapter = new RecommendedRecipesAdapter(recipes, context);
            recyclerViewRecommendedRecipes.setAdapter(recommendedRecipesAdapter);
            recyclerViewRecommendedRecipes.setLayoutManager(new LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false));

//            getRecipesByCategory(category);
            querySavedRecipes();
        }

        private void querySavedRecipes() {
            showProgressIndicator();
            ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
            // only get 20 most recent Recipes
            query.setLimit(5);
            query.orderByDescending(Recipe.KEY_CREATED_AT);
            query.whereEqualTo(Constant.USER, ParseUser.getCurrentUser());
            query.whereEqualTo("saved", true);
            query.findInBackground((recipesResult, e) -> {
                hideProgressIndicator();
                if (e == null) {
                    recipes.clear();
                    recipes.addAll(recipesResult);
                    recommendedRecipesAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "querySavedRecipes: ", e);
                }
            });
        }

        private void showProgressIndicator() {
            progressIndicator.setVisibility(View.VISIBLE);
        }

        private void hideProgressIndicator() {
            progressIndicator.setVisibility(View.GONE);
        }


        /**
         * Query recipes satisfying the recommended diet and cuisine categories from the
         * Spoonacular API
         *
         * @param category
         */
        private void getRecipesByCategory(HashMap<String, String> category) {
            showProgressIndicator();
            HttpUrl.Builder urlBuilder =
                    Objects.requireNonNull(HttpUrl.parse(Constant.RECIPE_SEARCH_URL)).newBuilder();
            urlBuilder.addQueryParameter(Constant.API_KEY, Constant.SPN_API_KEY);
            urlBuilder.addQueryParameter(Constant.TYPE, mealType);
            if (category.containsKey(Constant.DIET)) {
                urlBuilder.addQueryParameter(Constant.DIET, category.get(Constant.DIET));
            } else {
                urlBuilder.addQueryParameter(Constant.CUISINE, category.get(Constant.CUISINE));
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
                    ((Activity) context).runOnUiThread(() -> {
                        hideProgressIndicator();
                    });
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseJson =
                                    new JSONObject(responseData);
                            recipes.clear();
                            recipes.addAll(Recipe.fromJsonArray(responseJson.getJSONArray(Constant.RESULTS)));

                            ((Activity) context).runOnUiThread(() -> {
                                recommendedRecipesAdapter.notifyDataSetChanged();
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(context, context.getString(R.string.error_getting_recipes),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
