package com.example.makemeals.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.makemeals.MainActivity;
import com.example.makemeals.R;
import com.example.makemeals.ViewModel.RecipesSharedViewModel;
import com.example.makemeals.ViewModel.SharedViewModel;
import com.example.makemeals.adapters.RecipeAdapter;
import com.example.makemeals.customClasses.EndlessRecyclerViewScrollListener;
import com.example.makemeals.databinding.FragmentRecipesBinding;
import com.example.makemeals.models.Recipe;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipesFragment extends Fragment {
    private List<Recipe> recipesFilterList;
    private FragmentRecipesBinding binding;
    private MaterialButtonToggleGroup recipesToggleButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecipeAdapter recipeAdapter;


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
        RecipesSharedViewModel recipesSharedViewModel =
                new ViewModelProvider(requireActivity()).get(RecipesSharedViewModel.class);
        SharedViewModel sharedViewModel =
                new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        binding = FragmentRecipesBinding.bind(view);
        recipesToggleButton = binding.recipesToggleButton;
        RecyclerView recipeListRecyclerView = binding.recipeListRecyclerView;

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(recipesSharedViewModel::loadRecipes);
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setRefreshing(true);
        recipesSharedViewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            swipeRefreshLayout.setRefreshing(false);
            setupToggleButtonFilterGroup(recipes);
        });

        recipesFilterList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipesFilterList, getContext(), recipesSharedViewModel);
        recipeListRecyclerView.setAdapter(recipeAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // configure endless scroll listener
        // Triggered only when new data needs to be appended to the list
        // Add whatever code is needed to append new items to the bottom of the list
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromParse(page);
            }
        };
        recipeListRecyclerView.setLayoutManager(linearLayoutManager);
        recipeListRecyclerView.addOnScrollListener(scrollListener);

        recipeAdapter.setOnItemClickListener((itemView, position) -> {

            // call the activity to show the recipe details
            sharedViewModel.select(recipesFilterList.get(position));
            ((MainActivity) requireActivity()).showRecipeDetails();
        });
    }

    private void loadNextDataFromParse(int page) {
    }

    /**
     * Set up the toggle button to filter the recipes byt all/saved/favorite categories depending
     * on the selected button.
     * @param recipes
     */
    private void setupToggleButtonFilterGroup(List<Recipe> recipes) {
        int initialId = recipesToggleButton.getCheckedButtonId();
        displayCheckedIdInformation(initialId, recipes);

        recipesToggleButton.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
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
        if (checkedId == binding.allButton.getId()){
            recipesFilterList.clear();

            // only add saved or favorited recipes to the list
            recipesFilterList.addAll(recipes.stream()
                    .filter(recipe -> recipe.getFavorite() || recipe.getSaved())
                    .collect(Collectors.toList()));
        } else if (checkedId == binding.savedButton.getId()){
            recipesFilterList.clear();
            recipesFilterList.addAll(recipes.stream().filter(Recipe::getSaved)
                    .collect(Collectors.toList()));
        } else if (checkedId == binding.favoritesButton.getId()){
            recipesFilterList.clear();
            recipesFilterList.addAll(recipes.stream().filter(Recipe::getFavorite)
                    .collect(Collectors.toList()));
        }
        recipeAdapter.notifyDataSetChanged();
    }
}