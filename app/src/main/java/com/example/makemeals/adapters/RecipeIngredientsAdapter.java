package com.example.makemeals.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.makemeals.Constant;
import com.example.makemeals.R;
import com.example.makemeals.databinding.RecipeIngredientItemBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.ViewHolder> {
    private final JSONArray ingredients;
    private final Context context;
    private RecipeIngredientItemBinding binding;

    public RecipeIngredientsAdapter(JSONArray ingredients, Context context) {
        this.ingredients = ingredients;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeIngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RecipeIngredientItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeIngredientsAdapter.ViewHolder holder, int position) {
        JSONObject ingredient = ingredients.optJSONObject(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final private ImageView ivIngredientImage;
        private final TextView tvDetailIngredientName;
        private final TextView tvIngredientQuantity;
        private CheckBox cbIngredient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDetailIngredientName = binding.tvDetailIngredientName;
            tvIngredientQuantity = binding.tvIngredientQuantity;
            ivIngredientImage = binding.ivIngredientImage;
        }

        public void bind(JSONObject ingredient) {
            tvDetailIngredientName.setText(ingredient.optString("name"));
            JSONObject measure = Objects.requireNonNull(ingredient.optJSONObject("measures")).optJSONObject("us");
            String quantity = Objects.requireNonNull(measure).optInt("amount") + " " + measure.optString("unitShort");
            tvIngredientQuantity.setText(quantity);

            String image = ingredient.optString("image");
            Glide.with(context).load(Constant.IMAGE_BASE_URL + image)
                    .centerCrop()
                    .transform(new RoundedCorners(Constant.IMAGE_RADIUS))
                    .into(ivIngredientImage);
        }

        public void scale(float scale) {
            ivIngredientImage.setScaleX(scale);
            ivIngredientImage.setScaleY(scale);
        }
    }

    public static class SwipeHelper extends ItemTouchHelper.SimpleCallback{
        private final RecipeIngredientsAdapter adapter;
        private final RecyclerView parentView;

        public SwipeHelper(RecipeIngredientsAdapter adapter, RecyclerView parentView) {
            super(0, ItemTouchHelper.LEFT);
            this.adapter = adapter;
            this.parentView = parentView;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            adapter.deleteItem(parentView, position);
        }
    }

    private void restoreItem(RecyclerView parentView, JSONObject ingredient, int position) {
        try {
            ingredients.put(position, ingredient);
            notifyItemInserted(position);
            parentView.scrollToPosition(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteItem(RecyclerView parentView, int position) {
        JSONObject ingredient = ingredients.optJSONObject(position);
        ingredients.remove(position);
        notifyItemRemoved(position);

        Snackbar snackbar = Snackbar.make(parentView, context.getString(R.string.ingredient_deleted), Snackbar.LENGTH_LONG)
                .setAction(context.getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restoreItem(parentView, ingredient, position);
                    }
                });
        snackbar.show();
    }
}
