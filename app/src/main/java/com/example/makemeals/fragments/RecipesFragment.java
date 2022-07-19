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

import com.example.makemeals.Constant;
import com.example.makemeals.R;
import com.example.makemeals.ViewModel.RecipesDetailsSharedViewModel;
import com.example.makemeals.databinding.FragmentRecipesBinding;
import com.google.android.material.progressindicator.CircularProgressIndicator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipesFragment extends Fragment {
    private Fragment recipesListFragment;
    private CircularProgressIndicator progressIndicator;
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
        FragmentRecipesBinding binding = FragmentRecipesBinding.bind(view);
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