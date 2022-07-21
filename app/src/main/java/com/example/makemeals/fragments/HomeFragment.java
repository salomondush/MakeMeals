package com.example.makemeals.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.makemeals.R;
import com.example.makemeals.ViewModel.HomeViewModel;
import com.example.makemeals.adapters.RecommendationsAdapter;
import com.example.makemeals.databinding.FragmentHomeBinding;
import com.example.makemeals.models.Recipe;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private RecommendationsAdapter recommendationsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HomeViewModel model;

    public static final String TIME_FORMAT = "HH";

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
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
        FragmentHomeBinding binding = FragmentHomeBinding.bind(view);


        RecyclerView recommendationsRecyclerView = binding.recommendationsRecyclerView;
        TextView mealTypeTextView = binding.mealTypeTextView;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        model = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            model.processRecommendations();
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        List<HashMap<String, List<Recipe>>> recommendations = new ArrayList<>();
        recommendationsAdapter = new RecommendationsAdapter(recommendations,
                getContext());
        recommendationsRecyclerView.setAdapter(recommendationsAdapter);
        recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout.setRefreshing(true);
        model.getRecommendedRecipes().observe(getViewLifecycleOwner(), recommendationsUpdate -> {
            swipeRefreshLayout.setRefreshing(false);
            recommendations.clear();
            recommendations.addAll(recommendationsUpdate);
            recommendationsAdapter.notifyDataSetChanged();
        });

        // get local HOUR of the day
        String localTime = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(new Date());
        int hour = Integer.parseInt(localTime);

        // if hour is between 6 and 10, set meal type to breakfast
        if (hour >= 6 && hour <= 10) {
            mealTypeTextView.setText(getString(R.string.breakfast));
        } else if (hour >= 11 && hour <= 14) {
            // if hour is between 11 and 14, set meal type to lunch
            mealTypeTextView.setText(getString(R.string.lunch));
        } else if (hour >= 15 && hour <= 18) {
            // if hour is between 15 and 18, set meal type to dinner
            mealTypeTextView.setText(getString(R.string.dinner));
        } else {
            // if hour is between 19 and 24, set meal type to snack
            mealTypeTextView.setText(getString(R.string.snack));
        }

    }
}