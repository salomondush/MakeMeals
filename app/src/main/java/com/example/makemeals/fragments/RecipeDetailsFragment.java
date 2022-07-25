package com.example.makemeals.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.example.makemeals.MainActivity;
import com.example.makemeals.R;
import com.example.makemeals.ViewModel.SharedViewModel;
import com.example.makemeals.adapters.RecipeIngredientsAdapter;
import com.example.makemeals.adapters.RecipeInstructionsAdapter;
import com.example.makemeals.databinding.FragmentRecipeDetailsBinding;
import com.example.makemeals.models.Recipe;
import com.example.makemeals.models.ShoppingItem;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
    public static final String SHOPPING_LIST = "shoppingList";
    private CircularProgressIndicator progressIndicator;
    private ToggleButton saveToggleButton;
    private ToggleButton favoriteToggleButton;
    private LinearLayout nutritionInfoLinearLayout;
    private LinearLayout ingredientsLinearLayout;
    private LinearLayout instructionsLinearLayout;
    private Recipe recipe;
    private MaterialButtonToggleGroup recipeInfoToggleButton;


    // material popup
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;


    public final static int IMAGE_RADIUS = 10;


    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecipeDetailsFragment.
     */
    public static RecipeDetailsFragment newInstance() {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        FragmentRecipeDetailsBinding binding = FragmentRecipeDetailsBinding.bind(view);

        SharedViewModel model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        model.getSelected().observe(getViewLifecycleOwner(), recipe -> {
            if (recipe != null) {
                this.recipe = recipe;

                materialAlertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
                progressIndicator = binding.progressIndicator;

                ImageView recipeImageView = binding.recipeImageView;
                TextView recipeTitleTextView = binding.recipeTitleTextView;
                TextView readyInTimeTextView = binding.readyInTimeTextView;
                TextView typesTextView = binding.typesTextView;
                TextView servingsTextView = binding.servingsTextView;
                TextView dietsTextView = binding.dietsTextView;

                RecyclerView recipeDetailIngredientsRecyclerView =
                        binding.recipeDetailIngredientsRecyclerView;
                RecyclerView recipeDetailInstructionsRecyclerView =
                        binding.recipeDetailInstructionsRecyclerView;

                saveToggleButton = binding.saveToggleButton;
                favoriteToggleButton = binding.favoriteToggleButton;
                ImageButton shareImageButton = binding.shareImageButton;
                ImageButton shoppingListImageButton = binding.shoppingListImageButton;

                recipeInfoToggleButton = binding.recipeInfoToggleButton;
                nutritionInfoLinearLayout = binding.nutritionInfoLinearLayout;
                ingredientsLinearLayout = binding.ingredientsLinearLayout;
                instructionsLinearLayout = binding.instructionsLinearLayout;

                Glide.with(requireContext()).load(recipe.getImageUrl())
                        .placeholder(R.drawable.recipe_image_placeholder)
                        .centerCrop()
                        .transform(new RoundedCorners(IMAGE_RADIUS))
                        .into(recipeImageView);
                recipeTitleTextView.setText(recipe.getTitle());
                readyInTimeTextView.setText(MessageFormat.format("{0}m", recipe.getReadyInMinutes()));
                typesTextView.setText(getDishTypesStringFromRecipe(recipe));
                servingsTextView.setText(String.valueOf(recipe.getServings()));
                dietsTextView.setText(getDietStringFromRecipe(recipe));

                // get ingredients from jsonArray and display them using adapter
                RecipeIngredientsAdapter recipeIngredientsAdapter = new RecipeIngredientsAdapter(recipe.getExtendedIngredients(), getContext());
                recipeDetailIngredientsRecyclerView.setAdapter(recipeIngredientsAdapter);
                recipeDetailIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                // get instructions from jsonArray and display them using adapter
                RecipeInstructionsAdapter recipeInstructionsAdapter = new RecipeInstructionsAdapter(recipe.getAnalyzedInstructions(), getContext(), true);
                recipeDetailInstructionsRecyclerView.setAdapter(recipeInstructionsAdapter);
                recipeDetailInstructionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                // set up toggle button group for recipe info
                setupToggleButtonGroup();

                saveToggleButton.setChecked(recipe.getSaved());
                favoriteToggleButton.setChecked(recipe.getFavorite());

                saveToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    recipe.setSaved(isChecked);
                    recipe.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null){
                                saveToggleButton.setChecked(!isChecked);
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

                favoriteToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    recipe.setFavorite(isChecked);
                    recipe.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null){
                                favoriteToggleButton.setChecked(!isChecked);
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

                shareImageButton.setOnClickListener(v -> {
                    ((MainActivity) requireActivity()).showRecipeSharingFragment();
                });

                shoppingListImageButton.setOnClickListener(v -> {
                    launchAddToShoppingListDialog();
                });
            }
        });
    }


    /**
     * Sets up the toggle button group for the recipe info.
     */
    private void setupToggleButtonGroup() {
        int initialId = recipeInfoToggleButton.getCheckedButtonId();
        displayCheckedIdInformation(initialId);

        recipeInfoToggleButton.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked){
                displayCheckedIdInformation(checkedId);
            }
        });
    }

    private void displayCheckedIdInformation(int checkedId){
        if (checkedId == R.id.nutritionButton) {
            nutritionInfoLinearLayout.setVisibility(View.VISIBLE);
            ingredientsLinearLayout.setVisibility(View.GONE);
            instructionsLinearLayout.setVisibility(View.GONE);
        } else if (checkedId == R.id.ingredientsButton) {
            nutritionInfoLinearLayout.setVisibility(View.GONE);
            ingredientsLinearLayout.setVisibility(View.VISIBLE);
            instructionsLinearLayout.setVisibility(View.GONE);
        } else if (checkedId == R.id.instructionsButton) {
            nutritionInfoLinearLayout.setVisibility(View.GONE);
            ingredientsLinearLayout.setVisibility(View.GONE);
            instructionsLinearLayout.setVisibility(View.VISIBLE);
        }
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

        RecyclerView rvDialogIngredients = addShoppingIngredientsDialogView.findViewById(R.id.dialogIngredientsRecyclerView);
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