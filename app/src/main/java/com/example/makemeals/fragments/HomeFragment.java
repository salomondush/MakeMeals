package com.example.makemeals.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.makemeals.Constant;
import com.example.makemeals.R;
import com.example.makemeals.models.Recipe;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private Fragment recipesListFragment;
    private CircularProgressIndicator progressIndicator;
    private static final String SAVED = "saved";

    public static final int REQUEST_LIMIT = 20;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set an exit transition animation
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressIndicator = view.findViewById(R.id.progressIndicator);

        List<Recipe> savedRecipes = new ArrayList<>();
        recipesListFragment = RecipesListFragment.newInstance(savedRecipes, Constant.HOME);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.flSavedRecipesContainer, recipesListFragment).commit();
        querySavedRecipes();
    }

    private void querySavedRecipes() {
        showProgressBar();
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        // only get 20 most recent Recipes
        query.setLimit(REQUEST_LIMIT);
        query.orderByDescending(Recipe.KEY_CREATED_AT);
        query.whereEqualTo(Constant.USER, ParseUser.getCurrentUser());
        query.whereEqualTo(SAVED, true);
        query.findInBackground((recipes, e) -> {
            if (e == null) {
                hideProgressBar();
                ((RecipesListFragment) recipesListFragment).updateRecipes(recipes);
            } else {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressBar() {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressIndicator.setVisibility(View.GONE);
    }
}