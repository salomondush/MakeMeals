package com.example.makemeals.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.makemeals.models.Recipe;

import java.util.List;
import java.util.Objects;

/**
 * Abstract class inheriting from ViewModel that defines the behavior of all
 * view model subclasses that hold user recipe data.
 */
public abstract class RecipesViewModel extends ViewModel {
    protected MutableLiveData<List<Recipe>> recipes;

    abstract LiveData<List<Recipe>> getRecipes();

    /**
     * Updates given recipe with new data in the viewModel's live data.
     */
    public void updateRecipe(Recipe recipe) {
        List<Recipe> currentRecipes = recipes.getValue();
        for (int i = 0; i < Objects.requireNonNull(currentRecipes).size(); i++) {
            if (currentRecipes.get(i).getObjectId().equals(recipe.getObjectId())) {
                currentRecipes.set(i, recipe);
                recipes.setValue(currentRecipes);
                break;
            }
        }
    }
}