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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.makemeals.R;
import com.example.makemeals.adapters.IngredientsAdapter;
import com.example.makemeals.models.Ingredient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.OnSelectionChangedListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
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
    private List<String> searchIngredientsNames;
    // private List<Recipes> resultRecipes;
    private MaterialButton searchButton;
    private AutoCompleteTextView recipeDiet;
    private AutoCompleteTextView recipeType;

    private CircularProgressIndicator progressIndicator;

    private static final List<String> DIET_OPTIONS = Arrays.asList("Gluten Free", "Ketogenic",
            "Vegetarian", "Lacto-Vegetarian", "Ovo-Vegetarian", "Vegan", "Pescetarian",
            "Paleo", "Primal", "Whole30", "Low FODMAP");

    private static final List<String> TYPE_OPTIONS = Arrays.asList("main course", "side dish",
            "dessert", "appetizer", "salad", "breakfast", "soup", "beverage", "sauce", "drink");



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
        progressIndicator = view.findViewById(R.id.progressIndicator);

        // set and attach ingredients adapter to rvSearchIngredients recyclerView
        ingredients = new ArrayList<>();
        searchIngredientsNames = new ArrayList<>();
        ingredientsAdapter = new IngredientsAdapter(ingredients, getContext(), true);
        rvSearchIngredients.setAdapter(ingredientsAdapter);
        rvSearchIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayAdapter<String> optionsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, DIET_OPTIONS);
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, TYPE_OPTIONS);

        recipeDiet.setAdapter(optionsAdapter);
        recipeType.setAdapter(typesAdapter);


        // todo: set and attach recipe adapter to rvSearchResults
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = recipeType.getText().toString();
                String diet = recipeDiet.getText().toString();

                Log.i("SearchFragment", "type: " + type);
                Log.i("SearchFragment", "diet: " + diet);
                Log.i("SearchFragment", "ingredients: " + searchIngredientsNames);
            }
        });

        ingredientsAdapter.setOnSelectIngredientClickListener(new IngredientsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (searchIngredientsNames.contains(ingredients.get(position).getName())) {
                    searchIngredientsNames.remove(ingredients.get(position).getName());
                } else {
                    searchIngredientsNames.add(ingredients.get(position).getName());
                }
            }
        });

        querySearchIngredients();
    }

    private void querySearchIngredients() {
        showProgressBar();
        ParseQuery<Ingredient> query = ParseQuery.getQuery(Ingredient.class);
        query.findInBackground(new FindCallback<Ingredient>() {
            @Override
            public void done(List<Ingredient> objects, ParseException e) {
                if (e == null) {
                    ingredients.addAll(objects);
                    ingredientsAdapter.notifyDataSetChanged();
                    hideProgressBar();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    public void showProgressBar() {
        // Show progress item
        progressIndicator.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        // Hide progress item
        progressIndicator.setVisibility(View.GONE);
    }
}