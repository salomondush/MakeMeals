package com.example.makemeals.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.makemeals.Constant;
import com.example.makemeals.R;
import com.example.makemeals.ViewModel.RecipesSharedViewModel;
import com.example.makemeals.databinding.FragmentRecipesBinding;
import com.example.makemeals.models.Recipe;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipesFragment extends Fragment {
    private Fragment recipesListFragment;
    private CircularProgressIndicator progressIndicator;
    private List<Recipe> recipesFilterList;
    FragmentRecipesBinding binding;
    private MaterialButtonToggleGroup toggleButtonRecipes;

    public static final String FAVORITE = "favorite";


    public RecipesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment RecipesFragment.
     */
    public static RecipesFragment newInstance() {
        RecipesFragment fragment = new RecipesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentRecipesBinding.bind(view);
        progressIndicator = binding.progressIndicator;
        toggleButtonRecipes = binding.toggleButtonRecipes;
        recipesFilterList = new ArrayList<>();

        recipesListFragment = RecipesListFragment.newInstance(new ArrayList<>());
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.flRecipesContainer, recipesListFragment).commit();


        RecipesSharedViewModel model = new ViewModelProvider(requireActivity()).get(RecipesSharedViewModel.class);
        showProgressBar();
        model.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            Log.i("RecipesFragment", "new Change: " + recipes.size());
            setupToggleButtonFilterGroup(recipes);
            hideProgressBar();
        });
    }

    /**
     * Set up the toggle button to filter the recipes byt all/saved/favorite categories depending
     * on the selected button.
     * @param recipes
     */
    private void setupToggleButtonFilterGroup(List<Recipe> recipes) {
        int initialId = toggleButtonRecipes.getCheckedButtonId();
        displayCheckedIdInformation(initialId, recipes);

        toggleButtonRecipes.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked){
                displayCheckedIdInformation(checkedId, recipes);
            }
        });
    }

    /**
     * Filter the recipes depending on the selected button.
     * @param checkedId
     * @param recipes
     */
    private void displayCheckedIdInformation(int checkedId, List<Recipe> recipes) {
        if (checkedId == binding.buttonAll.getId()){
            recipesFilterList.clear();

            // only add saved or favorited recipes to the list
            recipesFilterList.addAll(recipes.stream()
                    .filter(recipe -> recipe.getFavorite() || recipe.getSaved())
                    .collect(Collectors.toList()));
        } else if (checkedId == binding.buttonSaved.getId()){
            recipesFilterList.clear();
            recipesFilterList.addAll(recipes.stream().filter(Recipe::getSaved)
                    .collect(Collectors.toList()));
        } else if (checkedId == binding.buttonFavorites.getId()){
            recipesFilterList.clear();
            recipesFilterList.addAll(recipes.stream().filter(Recipe::getFavorite)
                    .collect(Collectors.toList()));
        }
        ((RecipesListFragment) recipesListFragment).updateRecipes(recipesFilterList);
    }


    private void showProgressBar() {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressIndicator.setVisibility(View.GONE);
    }
}