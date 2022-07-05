package com.example.makemeals.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.makemeals.Constant;
import com.example.makemeals.databinding.RecipeItemBinding;
import com.example.makemeals.models.Recipe;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    final private List<Recipe> recipes;
    final private Context context;
    private RecipeItemBinding binding;
    private OnItemClickListener onItemClickListener;

    public RecipeAdapter(List<Recipe> recipes, Context context) {
        this.recipes = recipes;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RecipeItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new RecipeAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);
    }


    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final private ImageView recipeImage;
        final private TextView recipeTitle;
        final private ToggleButton tbSave;
        final private ToggleButton tbFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = binding.recipeImage;
            recipeTitle = binding.recipeTitle;
            tbSave = binding.tbSave;
            tbFavorite = binding.tbFavorite;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });

            tbSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Recipe recipe = recipes.get(getAdapterPosition());
                    // update or save the recipe
                    recipe.setSaved(!recipe.getSaved());
                    recipe.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                // update in recipes list
                                recipes.set(getAdapterPosition(), recipe);
                            } else {
                                // show error and reverse toggle
                                Toast.makeText(context, "Error saving recipe", Toast.LENGTH_SHORT).show();
                                tbSave.setChecked(!tbSave.isChecked());
                                Log.e("RecipeAdapter", "Error saving recipe: ", e);
                            }
                        }
                    });
                }
            });

            tbFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Recipe recipe = recipes.get(getAdapterPosition());
                    // update or save the recipe
                    recipe.setFavorite(!recipe.getFavorite());
                    recipe.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                // update in recipes list
                                recipes.set(getAdapterPosition(), recipe);
                            } else {
                                // show error and reverse toggle
                                Toast.makeText(context, "Error favoriting recipe", Toast.LENGTH_SHORT).show();
                                tbFavorite.setChecked(!tbSave.isChecked());
                            }
                        }
                    });
                }
            });
        }

        public void bind(Recipe recipe) {
            recipeTitle.setText(recipe.getTitle());
            tbSave.setChecked(recipe.getSaved());
            tbFavorite.setChecked(recipe.getFavorite());
            Glide.with(context).load(recipe.getImageUrl())
                    .centerCrop()
                    .transform(new RoundedCorners(Constant.IMAGE_RADIUS))
                    .into(recipeImage);
        }
    }
}
