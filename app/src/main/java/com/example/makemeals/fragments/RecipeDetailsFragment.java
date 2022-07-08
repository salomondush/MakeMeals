package com.example.makemeals.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
    private static final String TAG = "RecipeDetailsFragment";
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
        tvTypes.setText(getDietDishTypesStringFromRecipe());
        tvServings.setText(String.valueOf(recipe.getServings()));
        tvDiets.setText(getDietStringFromRecipe());

        // get ingredients from jsonArray and display them using adapter
        RecipeIngredientsAdapter recipeIngredientsAdapter = new RecipeIngredientsAdapter(recipe.getExtendedIngredients(), getContext());
        rvRecipeDetailIngredients.setAdapter(recipeIngredientsAdapter);
        rvRecipeDetailIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        // get instructions from jsonArray and display them using adapter
        RecipeInstructionsAdapter recipeInstructionsAdapter = new RecipeInstructionsAdapter(recipe.getAnalyzedInstructions(), getContext());
        rvRecipeDetailInstructions.setAdapter(recipeInstructionsAdapter);
        rvRecipeDetailInstructions.setLayoutManager(new LinearLayoutManager(getContext()));

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

        ibShare.setOnClickListener(v -> {
            showShareRecipeDialog();
        });

        ibShoppingList.setOnClickListener(v -> {
            launchAddToShoppingListDialog();
        });
    }

    private void generateSharableImageAndShareRecipe() {
        showProgressBar();
        JSONObject body = buildJSONBody();
        RestClient client = new RestClient(getContext());

        if (body != null) {
            client.getSharableImage(body, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    String imageUrl = response.optString("imageUrl");
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    shareIntent.setType("image/jpeg");

                    // get image from url and share it
                    Glide.with(requireContext())
                            .load(imageUrl)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                                    String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(),
                                            bitmap, recipe.getTitle(), null);
                                    Uri bmpUri = Uri.parse(path);
                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                    shareIntent.setType("image/*");
                                    requireContext().startActivity(Intent.createChooser(shareIntent, "Share Image"));
                                    hideProgressBar();
                                }
                            });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getContext(), "Error generating sharable image", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Error generating sharable image", Toast.LENGTH_SHORT).show();
        }
    }

    private JSONObject buildJSONBody(){
        JSONObject body = new JSONObject();
        JSONArray params = new JSONArray();

        try {
            params.put(0, new JSONObject().put("name", "recipeImage")
                    .put("imageUrl", recipe.getImageUrl()));

            params.put(1, new JSONObject().put("name", "readyIn")
                    .put("text", recipe.getReadyInMinutes() + " m<br>"));

            params.put(2, new JSONObject().put("name", "types")
                    .put("text", recipe.getDishTypes().join(",")));

            params.put(3, new JSONObject().put("name", "diet")
                    .put("text", recipe.getDiets().join(",")));

            params.put(4, new JSONObject().put("name", "servings")
                    .put("text", recipe.getServings()));

            params.put(5, new JSONObject().put("name", "instructions")
                    .put("text", getInstructionsString()));

            params.put(6, new JSONObject().put("name", "ingredients")
                    .put("text", getIngredientsString()));

            body.put("params", params);
            body.put("format", "jpeg");
            body.put("metadata", "some text");

            return body;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getIngredientsString(){
        StringBuilder ingredients = new StringBuilder();
        JSONArray ingredientsArray = recipe.getExtendedIngredients();
        for (int i = 0; i < ingredientsArray.length(); i++) {
            JSONObject ingredient = ingredientsArray.optJSONObject(i);

            JSONObject measure = Objects.requireNonNull(ingredient.optJSONObject("measures")).optJSONObject("us");
            String quantity = Objects.requireNonNull(measure).optInt("amount") + " " + measure.optString("unitShort");

            ingredients.append("- ").append(ingredient.optString("name")).append(" (")
                    .append(quantity).append(")<br>");
        }
        return ingredients.toString();
    }

    private String getDietStringFromRecipe(){
        StringBuilder diets = new StringBuilder();
        for (int i = 0; i < recipe.getDiets().length(); ++i){
            diets.append(recipe.getDiets().optString(i));
            if (i != recipe.getDiets().length() - 1){
                diets.append(", ");
            }
        }
        return diets.toString();
    }

    private String getDietDishTypesStringFromRecipe(){
        StringBuilder types = new StringBuilder();
        for (int i = 0; i < recipe.getDishTypes().length(); ++i){
            types.append(recipe.getDishTypes().optString(i));
            if (i != recipe.getDishTypes().length() - 1){
                types.append(", ");
            }
        }
        return types.toString();
    }

    private String getInstructionsString(){
        StringBuilder instructions = new StringBuilder();
        JSONArray instructionsArray = recipe.getAnalyzedInstructions();

        for (int i = 0; i < instructionsArray.length(); i++) {
            JSONObject instruction = instructionsArray.optJSONObject(i);
            instructions.append("<b>").append(instruction.optString("name")).append("</b><br>");

            for (int j = 0; j < Objects.requireNonNull(instruction.optJSONArray("steps")).length(); j++) {
                JSONObject step = Objects.requireNonNull(instruction.optJSONArray("steps")).optJSONObject(j);
                instructions.append(step.optInt("number")).append(". ").append(step.optString("step")).append("<br>");
            }
        }
        return instructions.toString();
    }

    private void launchAddToShoppingListDialog() {
        View addShoppingIngredientsDialogView = getLayoutInflater().
                inflate(R.layout.add_ingredients_dialog, null, false);

        JSONArray shoppingIngredients = recipe.getExtendedIngredients();

        RecyclerView rvDialogIngredients = addShoppingIngredientsDialogView.findViewById(R.id.rvDialogIngredients);
        RecipeIngredientsAdapter shoppingIngredientsDialogAdapter = new RecipeIngredientsAdapter(shoppingIngredients, getContext());
        rvDialogIngredients.setAdapter(shoppingIngredientsDialogAdapter);
        rvDialogIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecipeIngredientsAdapter.SwipeHelper(shoppingIngredientsDialogAdapter, rvDialogIngredients));
        itemTouchHelper.attachToRecyclerView(rvDialogIngredients);

        materialAlertDialogBuilder.setView(addShoppingIngredientsDialogView);
        materialAlertDialogBuilder.setTitle("Add ingredients to shopping list");
        materialAlertDialogBuilder.setMessage("Swipe left to remove ingredient from recipe");
        materialAlertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgressBar();

                List<ShoppingItem> shoppingItems = new ArrayList<>();
                List<String> shoppingItemIds = new ArrayList<>();
                for (int i = 0; i < shoppingIngredients.length(); ++i){
                    JSONObject ingredientFields = shoppingIngredients.optJSONObject(i);
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
                                        ((MainActivity) requireActivity()).setCartItemCount(shoppingItemIds.size());
                                        Toast.makeText(getContext(), "Ingredients added to shopping list", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Error adding ingredients to shopping list", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Error saving shopping items", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });
        materialAlertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        materialAlertDialogBuilder.show();
    }

    public void showShareRecipeDialog() {
        materialAlertDialogBuilder.setTitle("Share recipe");
        materialAlertDialogBuilder.setMessage("Share this recipe with your friends!");
        materialAlertDialogBuilder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                generateSharableImageAndShareRecipe();
            }
        });
        materialAlertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        materialAlertDialogBuilder.show();
    }

    public void showProgressBar() {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressIndicator.setVisibility(View.GONE);
    }
}