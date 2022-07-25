package com.example.makemeals.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.makemeals.models.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * View model to used to hold user search results
 */
public class RecipesSearchViewModel extends RecipesViewModel {

    @Override
    public LiveData<List<Recipe>> getRecipes() {
        if (recipes == null) {
            recipes = new MutableLiveData<>();
        }
        return recipes;
    }

    public void setRecipes(List<Recipe> recipesUpdate) {
        recipes.setValue(recipesUpdate);
    }

    public void addRecipes(List<Recipe> recipesUpdate) {
        List<Recipe> currentRecipes = recipes.getValue();
        if (currentRecipes == null) {
            currentRecipes = new ArrayList<>();
        }
        currentRecipes.addAll(recipesUpdate);
        recipes.setValue(currentRecipes);
    }
}
