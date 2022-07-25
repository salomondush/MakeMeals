package com.example.makemeals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.makemeals.Constant;
import com.example.makemeals.MainActivity;
import com.example.makemeals.R;
import com.example.makemeals.ViewModel.SharedViewModel;
import com.example.makemeals.databinding.RecommendationRecipeItemBinding;
import com.example.makemeals.models.Recipe;

import java.util.List;

public class RecommendedRecipesAdapter extends RecyclerView.Adapter<RecommendedRecipesAdapter.ViewHolder> {
    private RecommendationRecipeItemBinding binding;
    private final List<Recipe> recipes;
    private final Context context;

    public RecommendedRecipesAdapter(List<Recipe> recipes, Context context) {
        this.recipes = recipes;
        this.context = context;
    }

    @NonNull
    @Override
    public RecommendedRecipesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RecommendationRecipeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecommendedRecipesAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedRecipesAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView recipeImageView;
        private final TextView recipeTitleTextView;
        private final SharedViewModel model;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeImageView = binding.recipeImageView;
            recipeTitleTextView = binding.recipeTitleTextView;

            model = new ViewModelProvider(((MainActivity) context)).get(SharedViewModel.class);
            recipeImageView.setOnClickListener(v -> {
                model.select(recipes.get(getAdapterPosition()));
                ((MainActivity) context).showRecipeDetails();
            });
        }

        public void  bind(Recipe recipe) {
            recipeTitleTextView.setText(recipe.getTitle());
            Glide.with(context).load(recipe.getImageUrl())
                    .placeholder(R.drawable.recipe_image_placeholder)
                    .centerCrop()
                    .transform(new RoundedCorners(Constant.IMAGE_RADIUS))
                    .into(recipeImageView);
        }
    }
}
