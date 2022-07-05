package com.example.makemeals.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.makemeals.R;
import com.example.makemeals.adapters.RecipeDetailsIngredientsAdapter;
import com.example.makemeals.adapters.RecipeDetailsInstructionsAdapter;
import com.example.makemeals.databinding.FragmentRecipeDetailsBinding;
import com.example.makemeals.models.Recipe;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailsFragment extends Fragment {
    private FragmentRecipeDetailsBinding binding;
    private ImageView ivRecipeImage;
    private TextView tvRecipeTitle;
    private TextView tvReadyInTime;
    private TextView tvTypes;
    private TextView tvServings;
    private TextView tvDiets;

    private ToggleButton tbSave;
    private ToggleButton tbFavorite;
    private ImageButton ibShare;
    private ImageButton ibShoppingList;

    private MaterialButtonToggleGroup toggleButtonRecipeInfo;
    private LinearLayout llNutritionInfo;
    private LinearLayout llIngredients;
    private LinearLayout llInstructions;

    private List<JSONObject> ingredients;
    private RecipeDetailsIngredientsAdapter recipeDetailsIngredientsAdapter;
    private RecyclerView rvRecipeDetailIngredients;
    private List<JSONObject> instructions;
    private RecipeDetailsInstructionsAdapter recipeDetailsInstructionsAdapter;
    private RecyclerView rvRecipeDetailInstructions;

    public final static int IMAGE_RADIUS = 10;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "recipe";

    // TODO: Rename and change types of parameters
    private Recipe recipe;

    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipe Parameter 1.
     * @return A new instance of fragment RecipeDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeDetailsFragment newInstance(Recipe recipe) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        // pass the recipe object to the fragment
        args.putParcelable(ARG_PARAM1, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentRecipeDetailsBinding.bind(view);

        ivRecipeImage = binding.ivRecipeImage;
        tvRecipeTitle = binding.tvRecipeTitle;
        tvReadyInTime = binding.tvReadyInTime;
        tvTypes = binding.tvTypes;
        tvServings = binding.tvServings;
        tvDiets = binding.tvDiets;

        rvRecipeDetailIngredients = binding.rvRecipeDetailIngredients;
        rvRecipeDetailInstructions = binding.rvRecipeDetailInstructions;

        tbSave = binding.tbSave;
        tbFavorite = binding.tbFavorite;
        ibShare = binding.ibShare;
        ibShoppingList = binding.ibShoppingList;

        toggleButtonRecipeInfo = binding.toggleButtonRecipeInfo;
        llNutritionInfo = binding.llNutritionInfo;
        llIngredients = binding.llIngredients;
        llInstructions = binding.llInstructions;

        toggleButtonRecipeInfo.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked){
                    if (checkedId == R.id.btnNutrition) {
                        llNutritionInfo.setVisibility(View.VISIBLE);
                        llIngredients.setVisibility(View.GONE);
                        llInstructions.setVisibility(View.GONE);
                    } else if (checkedId == R.id.btnIngredients) {
                        llNutritionInfo.setVisibility(View.GONE);
                        llIngredients.setVisibility(View.VISIBLE);
                        llInstructions.setVisibility(View.GONE);
                    } else if (checkedId == R.id.btnInstructions) {
                        llNutritionInfo.setVisibility(View.GONE);
                        llIngredients.setVisibility(View.GONE);
                        llInstructions.setVisibility(View.VISIBLE);
                    }
                }
                // highlight the selected button
            }
        });

        tbSave.setChecked(recipe.getSaved());
        tbFavorite.setChecked(recipe.getFavorite());

        tbSave.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recipe.setSaved(isChecked);
            recipe.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null){
                        tbSave.setChecked(!isChecked);
                        Toast.makeText(getContext(), "Error " +
                                (isChecked? "saving": "unsaving") + " recipe", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Recipe " +
                                (isChecked? "saved": "unsaved"), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        tbFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recipe.setFavorite(isChecked);
            recipe.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null){
                        tbFavorite.setChecked(!isChecked);
                        Toast.makeText(getContext(), "Error " +
                                (isChecked? "favoriting": "unfavoriting") + " recipe", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Recipe " +
                                (isChecked? "favorited": "unfavorited"), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });


        Glide.with(requireContext()).load(recipe.getImageUrl())
                .centerCrop()
                .transform(new RoundedCorners(IMAGE_RADIUS))
                .into(ivRecipeImage);
        tvRecipeTitle.setText(recipe.getTitle());
        tvReadyInTime.setText(MessageFormat.format("{0}m", recipe.getReadyInMinutes()));

        // get dishtypes from jsonArray
        StringBuilder types = new StringBuilder();
        for (int i = 0; i < recipe.getDishTypes().length(); ++i){
            types.append(recipe.getDishTypes().optString(i));
            if (i != recipe.getDishTypes().length() - 1){
                types.append(", ");
            }
        }
        tvTypes.setText(types.toString());
        tvServings.setText(String.valueOf(recipe.getServings()));

        // get diets from jsonArray
        StringBuilder diets = new StringBuilder();
        for (int i = 0; i < recipe.getDiets().length(); ++i){
            diets.append(recipe.getDiets().optString(i));
            if (i != recipe.getDiets().length() - 1){
                diets.append(", ");
            }
        }
        tvDiets.setText(diets.toString());

        ingredients = new ArrayList<>();
        JSONArray ingredientsArray = recipe.getExtendedIngredients();
        for (int i = 0; i < ingredientsArray.length(); i++) {
            ingredients.add(ingredientsArray.optJSONObject(i));
        }

        recipeDetailsIngredientsAdapter = new RecipeDetailsIngredientsAdapter(ingredients, getContext());
        rvRecipeDetailIngredients.setAdapter(recipeDetailsIngredientsAdapter);
        rvRecipeDetailIngredients.setLayoutManager(new LinearLayoutManager(getContext()));


        instructions = new ArrayList<>();
        JSONArray instructionsArray = recipe.getAnalyzedInstructions();
        for (int i = 0; i < instructionsArray.length(); i++) {
            instructions.add(instructionsArray.optJSONObject(i));
        }

        recipeDetailsInstructionsAdapter = new RecipeDetailsInstructionsAdapter(instructions, getContext());
        rvRecipeDetailInstructions.setAdapter(recipeDetailsInstructionsAdapter);
        rvRecipeDetailInstructions.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}