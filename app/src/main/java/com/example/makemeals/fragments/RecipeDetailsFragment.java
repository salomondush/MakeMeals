package com.example.makemeals.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.transition.TransitionInflater;
import android.util.Log;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.makemeals.MainActivity;
import com.example.makemeals.R;
import com.example.makemeals.RestClient;
import com.example.makemeals.adapters.IngredientsDialogAdapter;
import com.example.makemeals.adapters.RecipeIngredientsAdapter;
import com.example.makemeals.adapters.RecipeInstructionsAdapter;
import com.example.makemeals.databinding.FragmentRecipeDetailsBinding;
import com.example.makemeals.models.Ingredient;
import com.example.makemeals.models.Recipe;
import com.example.makemeals.models.ShoppingItem;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailsFragment extends Fragment {
    public static final String SHOPPING_LIST = "shoppingList";
    private CircularProgressIndicator progressIndicator;
    private ToggleButton tbSave;
    private ToggleButton tbFavorite;
    private LinearLayout llNutritionInfo;
    private LinearLayout llIngredients;
    private LinearLayout llInstructions;
    private Recipe recipe;


    // material popup
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;


    public final static int IMAGE_RADIUS = 10;
    private static final String ARG_PARAM1 = "recipe";


    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param recipe Parameter 1.
     * @return A new instance of fragment RecipeDetailsFragment.
     */
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

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
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
        com.example.makemeals.databinding.FragmentRecipeDetailsBinding binding = FragmentRecipeDetailsBinding.bind(view);

        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        progressIndicator = binding.progressIndicator;

        ImageView ivRecipeImage = binding.ivRecipeImage;
        TextView tvRecipeTitle = binding.tvRecipeTitle;
        TextView tvReadyInTime = binding.tvReadyInTime;
        TextView tvTypes = binding.tvTypes;
        TextView tvServings = binding.tvServings;
        TextView tvDiets = binding.tvDiets;

        RecyclerView rvRecipeDetailIngredients = binding.rvRecipeDetailIngredients;
        RecyclerView rvRecipeDetailInstructions = binding.rvRecipeDetailInstructions;

        tbSave = binding.tbSave;
        tbFavorite = binding.tbFavorite;
        ImageButton ibShare = binding.ibShare;
        ImageButton ibShoppingList = binding.ibShoppingList;

        MaterialButtonToggleGroup toggleButtonRecipeInfo = binding.toggleButtonRecipeInfo;
        llNutritionInfo = binding.llNutritionInfo;
        llIngredients = binding.llIngredients;
        llInstructions = binding.llInstructions;

        Glide.with(requireContext()).load(recipe.getImageUrl())
                .centerCrop()
                .transform(new RoundedCorners(IMAGE_RADIUS))
                .into(ivRecipeImage);
        tvRecipeTitle.setText(recipe.getTitle());
        tvReadyInTime.setText(MessageFormat.format("{0}m", recipe.getReadyInMinutes()));
        tvTypes.setText(getDishTypesStringFromRecipe(recipe));
        tvServings.setText(String.valueOf(recipe.getServings()));
        tvDiets.setText(getDietStringFromRecipe(recipe));

        // get ingredients from jsonArray and display them using adapter
        RecipeIngredientsAdapter recipeIngredientsAdapter = new RecipeIngredientsAdapter(recipe.getExtendedIngredients(), getContext());
        rvRecipeDetailIngredients.setAdapter(recipeIngredientsAdapter);
        rvRecipeDetailIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        // get instructions from jsonArray and display them using adapter
        RecipeInstructionsAdapter recipeInstructionsAdapter = new RecipeInstructionsAdapter(recipe.getAnalyzedInstructions(), getContext(), true);
        rvRecipeDetailInstructions.setAdapter(recipeInstructionsAdapter);
        rvRecipeDetailInstructions.setLayoutManager(new LinearLayoutManager(getContext()));

        toggleButtonRecipeInfo.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked){
                    if (checkedId == R.id.buttonNutrition) {
                        llNutritionInfo.setVisibility(View.VISIBLE);
                        llIngredients.setVisibility(View.GONE);
                        llInstructions.setVisibility(View.GONE);
                    } else if (checkedId == R.id.buttonIngredients) {
                        llNutritionInfo.setVisibility(View.GONE);
                        llIngredients.setVisibility(View.VISIBLE);
                        llInstructions.setVisibility(View.GONE);
                    } else if (checkedId == R.id.buttonInstructions) {
                        llNutritionInfo.setVisibility(View.GONE);
                        llIngredients.setVisibility(View.GONE);
                        llInstructions.setVisibility(View.VISIBLE);
                    }
                }
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
                        Toast.makeText(getContext(),
                                (isChecked? requireContext().getString(R.string.error_saving_recipe):
                                        requireContext().getString(R.string.error_unsaving_recipe)), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), (isChecked?
                                requireContext().getString(R.string.recipe_saved):
                                requireContext().getString(R.string.recipe_unsaved)), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), (isChecked? requireContext().getString(R.string.error_favoriting_recipe):
                                requireContext().getString(R.string.error_unfavoriting_recipe)), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), (isChecked?
                                requireContext().getString(R.string.recipe_favorited):
                                requireContext().getString(R.string.recipe_unfavorited)), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        ibShare.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).showRecipeSharingFragment(recipe);
        });

        ibShoppingList.setOnClickListener(v -> {
            launchAddToShoppingListDialog();
        });
    }


    public static String getDietStringFromRecipe(Recipe recipe){
        StringBuilder diets = new StringBuilder();
        for (int i = 0; i < recipe.getDiets().length(); ++i){
            diets.append(recipe.getDiets().optString(i));
            if (i != recipe.getDiets().length() - 1){
                diets.append(", ");
            }
        }
        return diets.toString();
    }

    public static String getDishTypesStringFromRecipe(Recipe recipe){
        StringBuilder types = new StringBuilder();
        for (int i = 0; i < recipe.getDishTypes().length(); ++i){
            types.append(recipe.getDishTypes().optString(i));
            if (i != recipe.getDishTypes().length() - 1){
                types.append(", ");
            }
        }
        return types.toString();
    }

    private void launchAddToShoppingListDialog() {
        View addShoppingIngredientsDialogView = getLayoutInflater().
                inflate(R.layout.add_ingredients_dialog, null, false);

        List<JSONObject> shoppingIngredients = recipe.getExtendedIngredients();

        RecyclerView rvDialogIngredients = addShoppingIngredientsDialogView.findViewById(R.id.rvDialogIngredients);
        RecipeIngredientsAdapter shoppingIngredientsDialogAdapter = new RecipeIngredientsAdapter(shoppingIngredients, getContext());
        rvDialogIngredients.setAdapter(shoppingIngredientsDialogAdapter);
        rvDialogIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecipeIngredientsAdapter.SwipeHelper(shoppingIngredientsDialogAdapter, rvDialogIngredients));
        itemTouchHelper.attachToRecyclerView(rvDialogIngredients);

        materialAlertDialogBuilder.setView(addShoppingIngredientsDialogView);
        materialAlertDialogBuilder.setTitle(requireContext().getString(R.string.add_ingredients_to_shopping_list));
        materialAlertDialogBuilder.setMessage(requireContext().getString(R.string.swipe_left_to_remove_item));
        materialAlertDialogBuilder.setPositiveButton(requireContext().getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgressBar();

                List<ShoppingItem> shoppingItems = new ArrayList<>();
                List<String> shoppingItemIds = new ArrayList<>();
                for (int i = 0; i < shoppingIngredients.size(); ++i){
                    JSONObject ingredientFields = shoppingIngredients.get(i);
                    ShoppingItem shoppingItem = new ShoppingItem(ingredientFields);
                    shoppingItems.add(shoppingItem);
                }

                ShoppingItem.saveAllInBackground(shoppingItems, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            for (ShoppingItem shoppingItem : shoppingItems){
                                shoppingItemIds.add(shoppingItem.getObjectId());
                            }

                            ParseUser.getCurrentUser().addAll(SHOPPING_LIST, shoppingItemIds);
                            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    hideProgressBar();
                                    if (e == null){
                                        ((MainActivity) requireActivity()).incrementCartItemCount(shoppingItemIds.size());
                                        Toast.makeText(getContext(), requireContext().getString(R.string.ingredients_added_to_shopping_list), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), requireContext().getString(R.string.error_adding_ingredients_to_shopping_list), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), requireContext().getString(R.string.error_saving_shopping_items), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });
        materialAlertDialogBuilder.setNegativeButton(requireContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        materialAlertDialogBuilder.show();
    }


    private void showProgressBar() {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressIndicator.setVisibility(View.GONE);
    }
}