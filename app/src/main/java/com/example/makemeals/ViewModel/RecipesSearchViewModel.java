package com.example.makemeals.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.makemeals.models.Recipe;

import java.util.List;

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

    public void setRecipes(List<Recipe> recipes) {
        this.recipes.postValue(recipes);
    }
}
