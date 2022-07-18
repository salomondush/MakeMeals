package com.example.makemeals.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.makemeals.Constant;
import com.example.makemeals.R;
import com.example.makemeals.adapters.RecommendationsAdapter;
import com.example.makemeals.databinding.FragmentHomeBinding;
import com.example.makemeals.models.Ingredient;
import com.example.makemeals.models.Recommendation;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recommendationsRecyclerView;
    private CircularProgressIndicator progressIndicator;
    private List<HashMap<String, String>> categories;
    private RecommendationsAdapter recommendationsAdapter;
    private String mealType;

    private static final String BREAKFAST = "breakfast";
    private static final String LUNCH = "lunch";
    private static final String DINNER = "dinner";
    private static final String SNACK = "snack";
    private static final String TIME_FORMAT = "HH";

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
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
        com.example.makemeals.databinding.FragmentHomeBinding binding = FragmentHomeBinding.bind(view);

        progressIndicator = binding.progressIndicator;
        recommendationsRecyclerView = binding.recommendationsRecyclerView;
        TextView mealTypeTextView = binding.mealTypeTextView;

        // get local HOUR of the day
        String localTime = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(new Date());
        int hour = Integer.parseInt(localTime);

        // if hour is between 6 and 10, set meal type to breakfast
        if (hour >= 6 && hour <= 10) {
            mealTypeTextView.setText(getString(R.string.breakfast));
            mealType = BREAKFAST;
        } else if (hour >= 11 && hour <= 14) {
            // if hour is between 11 and 14, set meal type to lunch
            mealTypeTextView.setText(getString(R.string.lunch));
            mealType = LUNCH;
        } else if (hour >= 15 && hour <= 18) {
            // if hour is between 15 and 18, set meal type to dinner
            mealTypeTextView.setText(getString(R.string.dinner));
            mealType = DINNER;
        } else {
            // if hour is between 19 and 24, set meal type to snack
            mealTypeTextView.setText(getString(R.string.snack));
            mealType = SNACK;
        }

        categories = new ArrayList<>();
        getUserRecommendations();
    }


    /**
     * Get the user's recommendations and Ingredients from Parse
     */
    private void getUserRecommendations() {
        showProgressBar();
        ParseQuery<Recommendation> query = ParseQuery.getQuery(Recommendation.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.whereEqualTo(Recommendation.USER, ParseUser.getCurrentUser());
        query.getFirstInBackground((recommendation, e) -> {
            hideProgressBar();
            if (e == null) {
                JSONObject diets = recommendation.getDiets();
                JSONObject cuisines = recommendation.getCuisines();
                // get top recommendations from the two categories
                addTopRecommendations(diets, cuisines);

                // setup adapter and recycler view
                recommendationsAdapter = new RecommendationsAdapter(categories,
                        getContext(), mealType);
                recommendationsRecyclerView.setAdapter(recommendationsAdapter);
                recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            } else {
                Toast.makeText(requireContext(),
                        requireContext().getString(R.string.error_getting_recommendations),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Get top MAX_CATEGORY_RECOMMENDATIONS from each category and add them to the recommendation
     * Adapter's List.
     *
     * @param diets
     * @param cuisines
     */
    private void addTopRecommendations(JSONObject diets, JSONObject cuisines) {
        // get the first 3 elements with the highest value in diets
        HashMap<String, Integer> dietsMap = new HashMap<>();
        HashMap<String, Integer> cuisinesMap = new HashMap<>();

        for (int i = 0; i < diets.length(); i++) {
            String key = Objects.requireNonNull(diets.names()).optString(i);
            int value = diets.optInt(key);
            dietsMap.put(key, value);
        }

        for (int i = 0; i < cuisines.length(); i++) {
            String key = Objects.requireNonNull(cuisines.names()).optString(i);
            int value = cuisines.optInt(key);
            cuisinesMap.put(key, value);
        }

        // create a sortedList of the dietsMap
        List<HashMap.Entry<String, Integer>> dietsList = new ArrayList<>(dietsMap.entrySet());
        dietsList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        // create a sortedList of the cuisinesMap
        List<HashMap.Entry<String, Integer>> cuisinesList = new ArrayList<>(cuisinesMap.entrySet());
        cuisinesList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        // add the MAX_CATEGORY_RECOMMENDATIONS diets to the categories list
        for (int i = 0; i < Constant.MAX_CATEGORY_RECOMMENDATIONS; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(Constant.DIET, dietsList.get(i).getKey());
            categories.add(map);
        }

        // add the MAX_CATEGORY_RECOMMENDATIONS cuisines to the categories list
        for (int i = 0; i < Constant.MAX_CATEGORY_RECOMMENDATIONS; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(Constant.CUISINE, cuisinesList.get(i).getKey());
            categories.add(map);
        }
    }

    private void showProgressBar() {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressIndicator.setVisibility(View.GONE);
    }
}