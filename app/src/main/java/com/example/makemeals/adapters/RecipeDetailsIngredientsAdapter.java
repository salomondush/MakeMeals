package com.example.makemeals.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.makemeals.Constant;
import com.example.makemeals.databinding.RecipeDetailIngredientItemBinding;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class RecipeDetailsIngredientsAdapter extends RecyclerView.Adapter<RecipeDetailsIngredientsAdapter.ViewHolder> {
    private List<JSONObject> ingredients;
    private Context context;
    private RecipeDetailIngredientItemBinding binding;

    public RecipeDetailsIngredientsAdapter(List<JSONObject> ingredients, Context context) {
        this.ingredients = ingredients;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeDetailsIngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RecipeDetailIngredientItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeDetailsIngredientsAdapter.ViewHolder holder, int position) {
        JSONObject ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final private ImageView ivIngredientImage;
        private final TextView tvDetailIngredientName;
        private final TextView tvIngredientQuantity;
        private final String IMAGE_BASE_URL = "https://spoonacular.com/cdn/ingredients_100x100/";

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDetailIngredientName = binding.tvDetailIngredientName;
            tvIngredientQuantity = binding.tvIngredientQuantity;
            ivIngredientImage = binding.ivIngredientImage;
        }

        public void bind(JSONObject ingredient) {
            tvDetailIngredientName.setText(ingredient.optString("name"));
            JSONObject measure = Objects.requireNonNull(ingredient.optJSONObject("measures")).optJSONObject("us");
            Log.i("measure", measure.toString());
            String quantity = Objects.requireNonNull(measure).optInt("amount") + " " + measure.optString("unitShort");
            tvIngredientQuantity.setText(quantity);

            String image = ingredient.optString("image");
            Glide.with(context).load(IMAGE_BASE_URL + image)
                    .centerCrop()
                    .transform(new RoundedCorners(Constant.IMAGE_RADIUS))
                    .into(ivIngredientImage);
        }
    }
}
