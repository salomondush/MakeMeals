package com.example.makemeals.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.makemeals.Constant;
import com.example.makemeals.customClasses.OnDoubleTapListener;
import com.example.makemeals.databinding.RecipeItemBinding;
import com.example.makemeals.models.Recipe;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.List;

import cz.msebera.android.httpclient.cookie.params.CookieSpecPNames;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    final private List<Recipe> recipes;
    final private Context context;
    private RecipeItemBinding binding;
    private OnItemClickListener onItemClickListener;
    private final int page;

    public RecipeAdapter(List<Recipe> recipes, Context context, int page) {
        this.recipes = recipes;
        this.context = context;
        this.page = page;
    }

    public RecipeAdapter(List<Recipe> recipes, Context context) {
        this(recipes, context, Constant.NEUTRAL);
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
        private final TextView calsValue;
        private final TextView proteinsValue;
        private final TextView carbsValue;
        private final TextView fatsValue;
        private final TextView calsUnit;
        private final TextView carbsUnit;
        private final TextView proteinsUnit;
        private final TextView fatsUnit;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = binding.recipeImage;
            recipeTitle = binding.recipeTitle;
            tbSave = binding.tbSave;
            tbFavorite = binding.tbFavorite;
            calsValue = binding.calsValue;
            proteinsValue = binding.proteinsValue;
            carbsValue = binding.carbsValue;
            fatsValue = binding.fatsValue;
            calsUnit = binding.calsUnit;
            carbsUnit = binding.carbsUnit;
            proteinsUnit = binding.proteinsUnit;
            fatsUnit = binding.fatsUnit;
            LinearLayout llNutritionInfo = binding.llNutritionInfo;

            llNutritionInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });

            // set on double click listener for the recipe image
            recipeImage.setOnTouchListener(new OnDoubleTapListener(context) {
                @Override
                public void onDoubleTap(MotionEvent e) {
                    tbFavorite.setChecked(!tbFavorite.isChecked());
                    favoriteRecipe(getAdapterPosition());
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
                            if (e == null) {
                                // update in recipes list
                                if (page == Constant.HOME) {
                                    if (!tbSave.isChecked()) {
                                        recipes.remove(getAdapterPosition());
                                        notifyItemRemoved(getAdapterPosition());
                                    }
                                } else {
                                    recipes.set(getAdapterPosition(), recipe);
                                }
                            } else {
                                // show error and reverse toggle
                                Toast.makeText(context, "Error saving recipe", Toast.LENGTH_SHORT).show();
                                tbSave.setChecked(!tbSave.isChecked());
                            }
                        }
                    });
                }
            });

            tbFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteRecipe(getAdapterPosition());
                }
            });
        }

        private void favoriteRecipe(int position){
            Recipe recipe = recipes.get(position);
            // update or save the recipe
            recipe.setFavorite(!recipe.getFavorite());
            recipe.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        // update in recipes list
                        if (page == Constant.FAVORITES) {
                            if (!tbFavorite.isChecked()){
                                recipes.remove(position);
                                notifyItemRemoved(getAdapterPosition());
                            }
                        } else {
                            recipes.set(getAdapterPosition(), recipe);
                        }
                    } else {
                        // show error and reverse toggle
                        Toast.makeText(context, "Error favoriting recipe", Toast.LENGTH_SHORT).show();
                        tbFavorite.setChecked(!tbSave.isChecked());
                    }
                }
            });
        }

        public void bind(Recipe recipe) {

            JSONArray nutrients = recipe.getNutrients();
            for (int i = 0; i < nutrients.length(); i++) {
                try {
                    if (nutrients.getJSONObject(i).getString("name").equals("Calories")) {
                        calsValue.setText(nutrients.getJSONObject(i).getString("amount"));
                        calsUnit.setText(nutrients.getJSONObject(i).getString("unit"));
                    } else if (nutrients.getJSONObject(i).getString("name").equals("Protein")) {
                        proteinsValue.setText(nutrients.getJSONObject(i).getString("amount"));
                        proteinsUnit.setText(nutrients.getJSONObject(i).getString("unit"));
                    } else if (nutrients.getJSONObject(i).getString("name").equals("Carbohydrates")) {
                        carbsValue.setText(nutrients.getJSONObject(i).getString("amount"));
                        carbsUnit.setText(nutrients.getJSONObject(i).getString("unit"));
                    } else if (nutrients.getJSONObject(i).getString("name").equals("Fat")) {
                        fatsValue.setText(nutrients.getJSONObject(i).getString("amount"));
                        fatsUnit.setText(nutrients.getJSONObject(i).getString("unit"));
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error getting nutrients", Toast.LENGTH_SHORT).show();
                }
            }


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
