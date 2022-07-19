package com.example.makemeals.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makemeals.Constant;
import com.example.makemeals.R;
import com.example.makemeals.databinding.RecommendationItemBinding;
import com.example.makemeals.fragments.RecipesListFragment;
import com.example.makemeals.models.Ingredient;
import com.example.makemeals.models.Recipe;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.ViewHolder>{
    private RecommendationItemBinding binding;
    private final List<HashMap<String, List<Recipe>>> recommendations;
    private final Context context;

    public RecommendationsAdapter(List<HashMap<String, List<Recipe>>> recommendations, Context context) {
        this.recommendations = recommendations;
        this.context = context;
    }

    @NonNull
    @Override
    public RecommendationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RecommendationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecommendationsAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationsAdapter.ViewHolder holder, int position) {
        HashMap<String, List<Recipe>> recommendation = recommendations.get(position);

        // if no recommendations, hide the view
        String key = recommendation.keySet().iterator().next();
        if (Objects.requireNonNull(recommendation.get(key)).size() == 0) {
           holder.itemView.setVisibility(View.GONE);
        } else {
            holder.bind(recommendation);
        }
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryTitleTextView;
        private final RecyclerView recyclerViewRecommendedRecipes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTitleTextView = binding.categoryTitleTextView;
            recyclerViewRecommendedRecipes = binding.recyclerViewRecommendedRecipes;
        }

        public void bind(HashMap<String, List<Recipe>> recommendation) {

            String typeName = recommendation.keySet().toArray()[0].toString();

            categoryTitleTextView.setText(typeName.split("\\*")[1]);

            RecommendedRecipesAdapter recommendedRecipesAdapter = new RecommendedRecipesAdapter(recommendation.get(typeName),
                    context);
            recyclerViewRecommendedRecipes.setAdapter(recommendedRecipesAdapter);
            recyclerViewRecommendedRecipes.setLayoutManager(new LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false));

        }
    }
}
