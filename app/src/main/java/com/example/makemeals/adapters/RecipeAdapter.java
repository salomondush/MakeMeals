package com.example.makemeals.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.makemeals.R;
import com.example.makemeals.ViewModel.RecipesViewModel;
import com.example.makemeals.customClasses.OnDoubleTapListener;
import com.example.makemeals.databinding.RecipeItemBinding;
import com.example.makemeals.models.Recipe;
import com.example.makemeals.models.Recommendation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private static final String NAME = "name";
    private static final String UNIT = "unit";
    private static final String AMOUNT = "amount";
    private static final String CALORIES = "Calories";
    private static final String PROTEIN = "Protein";
    private static final String FAT = "Fat";
    private static final String CARBS = "Carbohydrates";
    final private List<Recipe> recipes;
//    private final RecipesSharedViewModel recipesSharedViewModel;
    final private Context context;
    private RecipeItemBinding binding;
    private OnItemClickListener onItemClickListener;
    private RecipesViewModel recipesViewModel;


    public RecipeAdapter(List<Recipe> recipes, Context context, RecipesViewModel recipesViewModel) {
        this.recipes = recipes;
        this.context = context;
        this.recipesViewModel = recipesViewModel;
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


    /**
     * Defines the listener interface for click events.
     */
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    /**
     * Allows the parent activity or fragment to define a click callback listener.
     *
     * @param listener
     */
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
        final private ToggleButton saveToggleButton;
        final private ToggleButton favoriteToggleButton;
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
            saveToggleButton = binding.saveToggleButton;
            favoriteToggleButton = binding.favoriteToggleButton;
            calsValue = binding.calsValue;
            proteinsValue = binding.proteinsValue;
            carbsValue = binding.carbsValue;
            fatsValue = binding.fatsValue;
            calsUnit = binding.calsUnit;
            carbsUnit = binding.carbsUnit;
            proteinsUnit = binding.proteinsUnit;
            fatsUnit = binding.fatsUnit;
            LinearLayout nutritionInfoLinearLayout = binding.nutritionInfoLinearLayout;

            nutritionInfoLinearLayout.setOnClickListener(v -> onItemClickListener.onItemClick(itemView, getAdapterPosition()));

            // set on double click listener for the recipe image to favorite/unfavorite the recipe
            recipeImage.setOnTouchListener(new OnDoubleTapListener(context) {
                @Override
                public void onDoubleTap(MotionEvent e) {
                    favoriteToggleButton.setChecked(!favoriteToggleButton.isChecked());
                    favoriteRecipe(getAdapterPosition());
                }
            });


            saveToggleButton.setOnClickListener(v -> saveRecipe(getAdapterPosition()));

            favoriteToggleButton.setOnClickListener(v -> favoriteRecipe(getAdapterPosition()));
        }

        private void saveRecipe(int position){
            // update or save the recipe
            Recipe recipe = recipes.get(position);
            recipe.setSaved(!recipe.getSaved());
            recipe.saveInBackground(e -> {
                if (e == null) {
                    // update recipes model
                    recipesViewModel.updateRecipe(recipe);
                    updateRecommendations(recipe, recipe.getSaved(), Constant.SAVE_RECOMMENDATION_NUM);
                } else {
                    // show error and reverse toggle
                    Toast.makeText(context, context.getString(R.string.error_saving_recipe), Toast.LENGTH_SHORT).show();
                    saveToggleButton.setChecked(!saveToggleButton.isChecked());
                }
            });
        }

        private void favoriteRecipe(int position) {
            Recipe recipe = recipes.get(position);
            // update or save the recipe
            recipe.setFavorite(!recipe.getFavorite());
            recipe.saveInBackground(e -> {
                if (e == null) {
                    // update in recipes model
                    recipesViewModel.updateRecipe(recipe);
                    updateRecommendations(recipe, recipe.getFavorite(), Constant.FAVORITE_RECOMMENDATION_NUM);
                } else {
                    // show error and reverse toggle
                    Toast.makeText(context, context.getString(R.string.error_favoriting_recipe), Toast.LENGTH_SHORT).show();
                    favoriteToggleButton.setChecked(!saveToggleButton.isChecked());
                }
            });
        }

        public void updateRecommendations(Recipe recipe, boolean increment, int amount){

            ParseQuery.getQuery(Recommendation.class)
                .whereEqualTo(Constant.USER, ParseUser.getCurrentUser())
                .getFirstInBackground((recommendation, e1) -> {
                    if (e1 == null) {

                        // updates user diet recommendation
                        JSONObject diets = recommendation.getDiets();
                        JSONArray recipeDiets = recipe.getDiets();
                        if (recipeDiets != null) {
                            for (int i = 0; i < recipeDiets.length(); i++) {
                                try {
                                    String diet = recipeDiets.optString(i);
                                    diets.put(diet,
                                            increment?
                                                    diets.optInt(diet) + amount:
                                                    diets.optInt(diet) == 0? 0:
                                                            diets.optInt(diet) - amount);
                                } catch (JSONException e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }

                        // update user cuisine recommendation
                        JSONObject cuisines = recommendation.getCuisines();
                        JSONArray recipeCuisines = recipe.getCuisines();
                        if (recipeCuisines != null) {
                            for (int i = 0; i < recipeCuisines.length(); i++) {
                                try {
                                    String cuisine = recipeCuisines.optString(i);

                                    cuisines.put(cuisine,
                                            increment?
                                                    cuisines.optInt(cuisine) + amount:
                                                    cuisines.optInt(cuisine) == 0? 0:
                                                            cuisines.optInt(cuisine) - amount);

                                } catch (JSONException e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }

                        recommendation.setDiets(diets);
                        recommendation.setCuisines(cuisines);
                        recommendation.saveInBackground(e -> {
                            if (e != null) {
                                Toast.makeText(context, context.getString(R.string.error_saving_recommendation), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
        }

        public void bind(Recipe recipe) {

            JSONArray nutrients = recipe.getNutrients();
            for (int i = 0; i < nutrients.length(); i++) {
                try {
                    if (nutrients.getJSONObject(i).getString(NAME).equals(CALORIES)) {
                        calsValue.setText(nutrients.getJSONObject(i).getString(AMOUNT));
                        calsUnit.setText(nutrients.getJSONObject(i).getString(UNIT));
                    } else if (nutrients.getJSONObject(i).getString(NAME).equals(PROTEIN)) {
                        proteinsValue.setText(nutrients.getJSONObject(i).getString(AMOUNT));
                        proteinsUnit.setText(nutrients.getJSONObject(i).getString(UNIT));
                    } else if (nutrients.getJSONObject(i).getString(NAME).equals(CARBS)) {
                        carbsValue.setText(nutrients.getJSONObject(i).getString(AMOUNT));
                        carbsUnit.setText(nutrients.getJSONObject(i).getString(UNIT));
                    } else if (nutrients.getJSONObject(i).getString(NAME).equals(FAT)) {
                        fatsValue.setText(nutrients.getJSONObject(i).getString(AMOUNT));
                        fatsUnit.setText(nutrients.getJSONObject(i).getString(UNIT));
                    }
                } catch (Exception e) {
                    Toast.makeText(context, context.getString(R.string.error_getting_nutrients), Toast.LENGTH_SHORT).show();
                }
            }


            recipeTitle.setText(recipe.getTitle());
            saveToggleButton.setChecked(recipe.getSaved());
            favoriteToggleButton.setChecked(recipe.getFavorite());
            Glide.with(context).load(recipe.getImageUrl())
                    .placeholder(R.drawable.recipe_image_placeholder)
                    .centerCrop()
                    .transform(new RoundedCorners(Constant.IMAGE_RADIUS))
                    .into(recipeImage);
        }
    }
}
