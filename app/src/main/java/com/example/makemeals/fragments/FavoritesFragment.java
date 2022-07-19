package com.example.makemeals.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.makemeals.Constant;
import com.example.makemeals.R;
import com.example.makemeals.ViewModel.RecipesDetailsSharedViewModel;
import com.example.makemeals.databinding.FragmentFavoritesBinding;
import com.example.makemeals.models.Recipe;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {
    private Fragment recipesListFragment;
    private CircularProgressIndicator progressIndicator;
    public static final String FAVORITE = "favorite";


    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment FavoritesFragment.
     */
    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
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
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentFavoritesBinding binding = FragmentFavoritesBinding.bind(view);
        progressIndicator = binding.progressIndicator;

        RecipesDetailsSharedViewModel model = new ViewModelProvider(requireActivity()).get(RecipesDetailsSharedViewModel.class);
        showProgressBar();
        model.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            // Update the UI.
            hideProgressBar();
            recipesListFragment = RecipesListFragment.newInstance(recipes, Constant.FAVORITES);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.flFavoriteRecipesContainer, recipesListFragment).commit();
        });
    }

    private void showProgressBar() {
        // Show progress item
        progressIndicator.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        // Hide progress item
        progressIndicator.setVisibility(View.GONE);
    }
}