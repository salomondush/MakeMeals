package com.example.makemeals.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.makemeals.Constant;
import com.example.makemeals.R;
import com.example.makemeals.RestClient;
import com.example.makemeals.adapters.IngredientsPageAdapter;
import com.example.makemeals.models.Ingredient;
import com.example.makemeals.models.Recipe;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private IngredientsPageAdapter ingredientsPageAdapter;
    private List<Ingredient> ingredients;
    private List<String> searchIngredientsNames;
    private AutoCompleteTextView recipeDiet;
    private AutoCompleteTextView recipeType;
    private LinearLayout llSearchResultBlock;
    private LinearLayout llSearchBlock;
    private Fragment recipesListFragment;
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

        MaterialButton searchButton = view.findViewById(R.id.searchButton);
        recipeDiet = view.findViewById(R.id.recipeDiet);
        recipeType = view.findViewById(R.id.recipeType);
        RecyclerView rvSearchIngredients = view.findViewById(R.id.rvSearchIngredients);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        TextView tvSearchBar = view.findViewById(R.id.tvSearchBar);
        llSearchResultBlock = view.findViewById(R.id.llSearchResultBlock);
        llSearchBlock = view.findViewById(R.id.llSearchBlock);
        ImageButton ibHideSearchBlock = view.findViewById(R.id.ibHideSearchBlock);

        // set and attach ingredients adapter to rvSearchIngredients recyclerView
        ingredients = new ArrayList<>();
        searchIngredientsNames = new ArrayList<>();
        ingredientsPageAdapter = new IngredientsPageAdapter(ingredients, getContext(), true);
        rvSearchIngredients.setAdapter(ingredientsPageAdapter);
        rvSearchIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        // initialize the child fragment that displays result recipes in a recyclerView
        List<Recipe> resultRecipes = new ArrayList<>();
        recipesListFragment = RecipesListFragment.newInstance(resultRecipes, Constant.SEARCH);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.flSearchResultsContainer, recipesListFragment).commit();

        // set up autocomplete text views
        ArrayAdapter<String> optionsAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, DIET_OPTIONS);
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, TYPE_OPTIONS);
        recipeDiet.setAdapter(optionsAdapter);
        recipeType.setAdapter(typesAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = recipeType.getText().toString();
                String diet = recipeDiet.getText().toString();
                searchRecipes(type, diet);
            }
        });

        ingredientsPageAdapter.setOnSelectIngredientClickListener(new IngredientsPageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (searchIngredientsNames.contains(ingredients.get(position).getName())) {
                    searchIngredientsNames.remove(ingredients.get(position).getName());
                } else {
                    searchIngredientsNames.add(ingredients.get(position).getName());
                }
            }
        });

        tvSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchBlock();
            }
        });

        ibHideSearchBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               hideSearchBlock();
            }
        });

        querySearchIngredients();
    }

    private void searchRecipes(String type, String diet) {
        showProgressBar();
        RestClient restClient = new RestClient(getContext());
        restClient.complexSearch(searchIngredientsNames, type, diet, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    ((RecipesListFragment) recipesListFragment).updateRecipes(Recipe.fromJsonArray(response.getJSONArray("results")));
                    hideProgressBar();
                    hideSearchBlock();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getContext(), "Error searching recipes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void querySearchIngredients() {
        showProgressBar();
        ParseQuery<Ingredient> query = ParseQuery.getQuery(Ingredient.class);
        query.findInBackground(new FindCallback<Ingredient>() {
            @Override
            public void done(List<Ingredient> objects, ParseException e) {
                if (e == null) {
                    ingredients.addAll(objects);
                    ingredientsPageAdapter.notifyDataSetChanged();
                    hideProgressBar();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void hideSearchBlock(){
        llSearchBlock.setVisibility(View.GONE);
        llSearchResultBlock.setVisibility(View.VISIBLE);
    }

    private void showSearchBlock(){
        llSearchBlock.setVisibility(View.VISIBLE);
        llSearchResultBlock.setVisibility(View.GONE);
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