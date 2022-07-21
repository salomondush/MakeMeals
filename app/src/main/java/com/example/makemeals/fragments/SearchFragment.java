package com.example.makemeals.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.makemeals.Constant;
import com.example.makemeals.MainActivity;
import com.example.makemeals.ParseApplication;
import com.example.makemeals.R;
import com.example.makemeals.RestClient;
import com.example.makemeals.ViewModel.RecipesSearchViewModel;
import com.example.makemeals.ViewModel.SharedViewModel;
import com.example.makemeals.adapters.IngredientsPageAdapter;
import com.example.makemeals.adapters.RecipeAdapter;
import com.example.makemeals.databinding.FragmentSearchBinding;
import com.example.makemeals.models.Ingredient;
import com.example.makemeals.models.Recipe;
import com.example.makemeals.models.SearchHistory;
import com.example.makemeals.models.SearchHistoryDao;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private IngredientsPageAdapter ingredientsPageAdapter;
    private List<Ingredient> ingredients;
    private List<String> searchIngredientsNames;
    private AutoCompleteTextView recipeDiet;
    private AutoCompleteTextView recipeType;
    private TextView tvSearchBar;
    private MaterialButton searchButton;
    private ImageButton ibHideSearchBlock;
    private LinearLayout llSearchResultBlock;
    private LinearLayout llSearchBlock;
    private AutoCompleteTextView etSearchText;
    private CircularProgressIndicator progressIndicator;
    private List<SearchHistory> searchHistories;
    private SearchHistoryDao searchHistoryDao;
    private ArrayAdapter<String> searchQueryAdapter;
    private RecyclerView recipeListRecyclerView;
    private RecipeAdapter recipeAdapter;
    private ArrayList<Recipe> recipes;

    private RecipesSearchViewModel recipesSearchViewModel;

    private static final List<String> DIET_OPTIONS = Arrays.asList("Gluten Free", "Ketogenic",
            "Vegetarian", "Lacto-Vegetarian", "Ovo-Vegetarian", "Vegan", "Pescetarian",
            "Paleo", "Primal", "Whole30", "Low FODMAP");

    private static final List<String> TYPE_OPTIONS = Arrays.asList("main course", "side dish",
            "dessert", "appetizer", "salad", "breakfast", "soup", "beverage", "sauce", "drink");

    private  List<String> searchOptions;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentSearchBinding binding = FragmentSearchBinding.bind(view);

        recipesSearchViewModel = new ViewModelProvider(requireActivity()).get(RecipesSearchViewModel.class);
        SharedViewModel sharedViewModel =
                new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        searchButton = binding.searchButton;
        recipeDiet = binding.recipeDiet;
        recipeType = binding.recipeType;
        RecyclerView rvSearchIngredients = binding.rvSearchIngredients;
        progressIndicator = binding.progressIndicator;
        tvSearchBar = binding.tvSearchBar;
        llSearchResultBlock = binding.llSearchResultBlock;
        llSearchBlock = binding.llSearchBlock;
        ibHideSearchBlock = binding.ibHideSearchBlock;
        etSearchText = binding.etSearchText;
        recipeListRecyclerView = binding.recipeListRecyclerView;

        // define our db and delete any excess search history data
        searchHistoryDao =
                ((ParseApplication) requireActivity().getApplicationContext()).getSearchHistoryDataBase().searchHistoryDao();
        AsyncTask.execute(() -> {
            if (searchHistoryDao.getSize() > Constant.VISIBLE_THRESHOLD) {
                searchHistoryDao.deleteNLeastRecent(searchHistoryDao.getSize() - Constant.VISIBLE_THRESHOLD);
            }
        });

        recipes = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipes, getContext(), recipesSearchViewModel);
        recipeListRecyclerView.setAdapter(recipeAdapter);
        recipeListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipeAdapter.setOnItemClickListener((itemView, position) -> {

            // call the activity to show the recipe details
            sharedViewModel.select(recipes.get(position));
            ((MainActivity) requireActivity()).showRecipeDetails();
        });

        recipesSearchViewModel.getRecipes().observe(getViewLifecycleOwner(), updateRecipes -> {
            if (recipes != null) {
                recipes.clear();
                recipes.addAll(updateRecipes);
                recipeAdapter.notifyDataSetChanged();
            }
        });


        // set and attach ingredients adapter to rvSearchIngredients recyclerView
        ingredients = new ArrayList<>();
        searchIngredientsNames = new ArrayList<>();
        ingredientsPageAdapter = new IngredientsPageAdapter(ingredients, getContext(), true);
        rvSearchIngredients.setAdapter(ingredientsPageAdapter);
        rvSearchIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        // setup data for the search drop down options for search, diet, and type
        setupDropdownOptions();

        setupClickListeners();

        querySearchIngredients();
    }

    private void setupClickListeners() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = recipeType.getText().toString();
                String diet = recipeDiet.getText().toString();
                String query = etSearchText.getText().toString();

                AsyncTask.execute(() -> {
                    searchHistoryDao.insert(SearchHistory.createEntry(query, type, diet));
                    searchOptions.add(query);
                    searchQueryAdapter.notifyDataSetChanged();
                });

                searchRecipes(type, diet, query);
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
    }

    private void setupDropdownOptions() {
        // set up autocomplete text views
        searchOptions = new ArrayList<>();
        searchQueryAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, searchOptions);
        etSearchText.setAdapter(searchQueryAdapter);

        ArrayAdapter<String> optionsAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, DIET_OPTIONS);
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, TYPE_OPTIONS);
        recipeDiet.setAdapter(optionsAdapter);
        recipeType.setAdapter(typesAdapter);

        AsyncTask.execute(() -> {
            searchHistories = searchHistoryDao.getRecent(ParseUser.getCurrentUser().getObjectId()
                    , Constant.VISIBLE_THRESHOLD);
            for (SearchHistory searchHistory : searchHistories) {
                searchOptions.add(searchHistory.searchQuery);
            }
            searchQueryAdapter.notifyDataSetChanged();
        });


        etSearchText.setOnItemClickListener((parent, view, position, id) -> {

            String queryName = searchOptions.get(position);
            searchHistories.forEach(searchHistory -> {
                if (searchHistory.searchQuery.equals(queryName)) {
                    if (!searchHistory.searchDiet.isEmpty()) {
                        recipeDiet.setText(searchHistory.searchDiet);
                    }
                    if (!searchHistory.searchType.isEmpty()) {
                        recipeType.setText(searchHistory.searchType);
                    }
                }
            });
        });
    }

    private void searchRecipes(String type, String diet, String query) {
        showProgressBar();

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder =
                Objects.requireNonNull(HttpUrl.parse(Constant.RECIPE_SEARCH_URL)).newBuilder();
        urlBuilder.addQueryParameter(Constant.API_KEY, Constant.SPN_API_KEY);
        urlBuilder.addQueryParameter(Constant.NUMBER, String.valueOf(Constant.MAX_RESULTS));
        urlBuilder.addQueryParameter(Constant.INCLUDE_INGREDIENTS, TextUtils.join(",", searchIngredientsNames));
        urlBuilder.addQueryParameter(Constant.TYPE, type);
        urlBuilder.addQueryParameter(Constant.QUERY, query);
        urlBuilder.addQueryParameter(Constant.DIET, diet);
        urlBuilder.addQueryParameter(Constant.FILL_INGREDIENTS, String.valueOf(true));
        urlBuilder.addQueryParameter(Constant.ADD_RECIPE_INFORMATION, String.valueOf(true));
        urlBuilder.addQueryParameter(Constant.ADD_RECIPE_NUTRITION, String.valueOf(true));
        urlBuilder.addQueryParameter(Constant.INSTRUCTIONS_REQUIRED, String.valueOf(true));

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                requireActivity().runOnUiThread(() -> {
                    hideProgressBar();
                });

                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJson =
                                new JSONObject(Objects.requireNonNull(response.body()).string());

                        requireActivity().runOnUiThread(() -> {
                            try {
                                recipesSearchViewModel.setRecipes((Recipe.fromJsonArray(responseJson.getJSONArray(Constant.RESULTS))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });

                        requireActivity().runOnUiThread(() -> {
                            hideSearchBlock();
                        });
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(),
                                requireContext().getString(R.string.connection_failed),
                                Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    private void querySearchIngredients() {
        showProgressBar();
        ParseQuery<Ingredient> query = ParseQuery.getQuery(Ingredient.class);
        query.whereEqualTo(Constant.USER, ParseUser.getCurrentUser());
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground(new FindCallback<Ingredient>() {
            @Override
            public void done(List<Ingredient> objects, ParseException e) {
                if (e == null && objects != null) {
                    ingredients.clear();
                    ingredients.addAll(objects);
                    ingredientsPageAdapter.notifyDataSetChanged();
                    hideProgressBar();
                } else if (objects != null) {
                    Toast.makeText(getContext(), requireContext().getString(R.string.error_getting_search_ingredients), Toast.LENGTH_SHORT).show();
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

    private void showProgressBar() {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressIndicator.setVisibility(View.GONE);
    }

}