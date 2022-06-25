package com.example.makemeals.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.example.makemeals.R;
import com.example.makemeals.adapters.IngredientsAdapter;
import com.example.makemeals.models.Ingredient;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private RecyclerView rvSearchIngredients;
    private RecyclerView rvSearchResults;
    private IngredientsAdapter ingredientsAdapter;
    // private RecipeAdapter recipeAdapter;
    private List<Ingredient> ingredients;
    // private List<Recipes> resultRecipes;
    private MaterialButton searchButton;
    private AutoCompleteTextView recipeDiet;
    private AutoCompleteTextView recipeType;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchButton = view.findViewById(R.id.searchButton);
        recipeDiet = view.findViewById(R.id.recipeDiet);
        recipeType = view.findViewById(R.id.recipeType);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        rvSearchIngredients = view.findViewById(R.id.rvSearchIngredients);

        // set and attach ingredients adapter to rvSearchIngredients recyclerView
        ingredients = new ArrayList<>();
        ingredientsAdapter = new IngredientsAdapter(ingredients, getContext(), true);
        rvSearchIngredients.setAdapter(ingredientsAdapter);
        rvSearchIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        // todo: set and attach recipe adapter to rvSearchResults
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: When the search button is pressed, we fetch recipes, and immediately after getting
                // results, we will set the rvSearchResults visible and display the results
                // additionally, we will have another view with an arrow facing down to pull down the search
                // view again (maybe with some fancy animations)
            }
        });
    }
}