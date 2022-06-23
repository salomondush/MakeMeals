package com.example.makemeals.adapters;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makemeals.databinding.IngredientItemBinding;
import com.example.makemeals.models.Ingredient;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {
    private List<Ingredient> ingredients;
    private Context context;
    private IngredientItemBinding binding;
    private OnItemClickListener removeIngredientClickListener;

    public IngredientsAdapter(List<Ingredient> ingredients, Context context) {
        this.ingredients = ingredients;
        this.context = context;
    }

    @NonNull
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = IngredientItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new IngredientsAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsAdapter.ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnRemoveIngredientClickListener(OnItemClickListener listener) {
        this.removeIngredientClickListener = listener;
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView date;
        private ImageButton removeIngredient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = binding.ingredientName;
            date = binding.ingredientDate;
            removeIngredient = binding.removeButton;

            removeIngredient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeIngredientClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });
        }

        public void bind(Ingredient ingredient) {
            name.setText(ingredient.getName());
            date.setText(ingredient.getCreatedAt().toString());
        }
    }
}
