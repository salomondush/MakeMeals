package com.example.makemeals.ViewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.makemeals.Constant;
import com.example.makemeals.models.Recipe;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class RecipesSharedViewModel extends RecipesViewModel {
    @Override
    public LiveData<List<Recipe>> getRecipes() {
        if (recipes == null) {
            recipes = new MutableLiveData<>();
            loadRecipes();
        }
        return recipes;
    }

    /**
     * Loads user recipes from Parse
     */
    public void loadRecipes() {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        // only get 20 most recent Recipes
        query.setLimit(Constant.REQUEST_LIMIT);
        query.orderByDescending(Recipe.KEY_CREATED_AT);
        query.whereEqualTo(Constant.USER, ParseUser.getCurrentUser());
        query.findInBackground((resultRecipes, e) -> {
            if (e == null) {
                recipes.setValue(resultRecipes);
            } else {
                e.printStackTrace();
            }
        });
    }
}
