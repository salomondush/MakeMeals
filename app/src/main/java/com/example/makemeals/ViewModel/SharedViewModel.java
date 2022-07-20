package com.example.makemeals.ViewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.makemeals.models.Recipe;

/**
 * View model class that hold's the user's currently selected recipe.
 */
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Recipe> selected = new MutableLiveData<Recipe>();

    public void select(Recipe recipe) {
        selected.setValue(recipe);
    }

    public LiveData<Recipe> getSelected() {
        return selected;
    }
}
