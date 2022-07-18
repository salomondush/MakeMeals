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
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.makemeals.Constant;
import com.example.makemeals.R;
import com.example.makemeals.customClasses.OnDoubleTapListener;
import com.example.makemeals.databinding.RecipeItemBinding;
import com.example.makemeals.models.Recipe;
import com.example.makemeals.models.Recommendation;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.cookie.params.CookieSpecPNames;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private static final String NAME = "name";
    private static final String UNIT = "unit";
    private static final String AMOUNT = "amount";
    private static final String CALORIES = "Calories";
    private static final String PROTEIN = "Protein";
    private static final String FAT = "Fat";
    private static final String CARBS = "Carbohydrates";
    private static final String TAG = "RecipeAdapter";
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

            // set on double click listener for the recipe image to favorite/unfavorite the recipe
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
                    saveRecipe(getAdapterPosition());

                }
            });

            tbFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteRecipe(getAdapterPosition());
                }
            });
        }

        private void saveRecipe(int position){
            // update or save the recipe
            Recipe recipe = recipes.get(position);
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

                        updateRecommendations(getAdapterPosition(), recipe.getSaved());
                    } else {
                        // show error and reverse toggle
                        Toast.makeText(context, context.getString(R.string.error_saving_recipe), Toast.LENGTH_SHORT).show();
                        tbSave.setChecked(!tbSave.isChecked());
                    }
                }
            });
        }

        private void favoriteRecipe(int position) {
            Recipe recipe = recipes.get(position);
            // update or save the recipe
            recipe.setFavorite(!recipe.getFavorite());
            recipe.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // update in recipes list
                        if (page == Constant.FAVORITES) {
                            if (!tbFavorite.isChecked()) {
                                recipes.remove(position);
                                notifyItemRemoved(getAdapterPosition());
                            }
                        } else {
                            recipes.set(getAdapterPosition(), recipe);
                        }

                        updateRecommendations(getAdapterPosition(), recipe.getFavorite());
                    } else {
                        // show error and reverse toggle
                        Toast.makeText(context, context.getString(R.string.error_favoriting_recipe), Toast.LENGTH_SHORT).show();
                        tbFavorite.setChecked(!tbSave.isChecked());
                    }
                }
            });
        }

        public void updateRecommendations(int position, boolean increment){
            Recipe recipe = recipes.get(position);

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
                                                    diets.optInt(diet) + 1:
                                                    diets.optInt(diet) == 0? 0:
                                                            diets.optInt(diet) - 1);
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
                                                    cuisines.optInt(cuisine) + 1:
                                                    cuisines.optInt(cuisine) == 0? 0:
                                                            cuisines.optInt(cuisine) - 1);

                                } catch (JSONException e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }

                        recommendation.setDiets(diets);
                        recommendation.setCuisines(cuisines);
                        recommendation.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.i(TAG, "Error saving " +
                                            "recommendation");
                                } else {
                                    Log.i(TAG, "Recommendation saved:" +
                                            recommendation.getDiets().toString());

                                    Log.i(TAG, "Recommendation saved:" +
                                            recommendation.getCuisines().toString());
                                }
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
            tbSave.setChecked(recipe.getSaved());
            tbFavorite.setChecked(recipe.getFavorite());
            Glide.with(context).load(recipe.getImageUrl())
                    .centerCrop()
                    .transform(new RoundedCorners(Constant.IMAGE_RADIUS))
                    .into(recipeImage);
        }
    }
}
