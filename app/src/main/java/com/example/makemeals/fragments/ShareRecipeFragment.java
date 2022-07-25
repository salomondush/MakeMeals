package com.example.makemeals.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.makemeals.Constant;
import com.example.makemeals.R;
import com.example.makemeals.ViewModel.SharedViewModel;
import com.example.makemeals.adapters.RecipeIngredientsAdapter;
import com.example.makemeals.adapters.RecipeInstructionsAdapter;
import com.example.makemeals.customClasses.PinchRecyclerView;
import com.example.makemeals.databinding.FragmentShareRecipeBinding;
import com.example.makemeals.models.Recipe;
import com.google.android.material.button.MaterialButton;

import java.text.MessageFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShareRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShareRecipeFragment extends Fragment {

    private Recipe recipe;
    private LinearLayout nutritionInfoLinearLayout;
    private Float scale = 1f;
    private int currentX;
    private int currentY;
    private ConstraintLayout llSharableRecipeInfo;
    private final String SHARE_TYPE_IMAGE = "image/*";

    private float oldDistImage = 1f;
    private float oldDistLlNutritionInfo = 1f;
    private int mode = NONE;


    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;

    public ShareRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ShareRecipeFragment.
     */
    public static ShareRecipeFragment newInstance() {
        ShareRecipeFragment fragment = new ShareRecipeFragment();
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
        return inflater.inflate(R.layout.fragment_share_recipe, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentShareRecipeBinding binding = FragmentShareRecipeBinding.bind(view);

        SharedViewModel model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        model.getSelected().observe(getViewLifecycleOwner(), recipe -> {
            if (recipe != null) {
                this.recipe = recipe;


                ImageView recipeImageView = binding.recipeImageView;
                TextView recipeTitleTextView = binding.recipeTitleTextView;
                TextView readyInTimeTextView = binding.readyInTimeTextView;
                TextView typesTextView = binding.typesTextView;
                TextView servingsTextView = binding.servingsTextView;
                TextView dietsTextView = binding.dietsTextView;
                nutritionInfoLinearLayout = binding.nutritionInfoLinearLayout;
                MaterialButton buttonShareRecipe = binding.buttonShareRecipe;
                MaterialButton buttonCancelSharing = binding.buttonCancelSharing;
                llSharableRecipeInfo = binding.llSharableRecipeInfo;


                nutritionInfoLinearLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
                            @Override
                            public boolean onScale(ScaleGestureDetector detector) {
                                scale *= detector.getScaleFactor();
                                scale = Math.max(0.1f, Math.min(scale, 5.0f));
                                nutritionInfoLinearLayout.setScaleX(scale);
                                nutritionInfoLinearLayout.setScaleY(scale);
                                return true;
                            }

                            @Override
                            public boolean onScaleBegin(ScaleGestureDetector detector) {
                                return true;
                            }

                            @Override
                            public void onScaleEnd(ScaleGestureDetector detector) {
                            }
                        });
                        scaleGestureDetector.onTouchEvent(event);

                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_DOWN: // first finger down
                                currentX = (int) event.getX();
                                currentY = (int) event.getY();
                                mode = DRAG;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (mode == DRAG) {
                                    int x = (int) event.getX();
                                    int y = (int) event.getY();
                                    int deltaX = x - currentX;
                                    int deltaY = y - currentY;
                                    currentX = x;
                                    currentY = y;
                                    nutritionInfoLinearLayout.setX(nutritionInfoLinearLayout.getX() + deltaX);
                                    nutritionInfoLinearLayout.setY(nutritionInfoLinearLayout.getY() + deltaY);
                                } else if (mode == ZOOM) {
                                    float newDist = spacing(event);
                                    if (newDist > 5f) {
                                        float scale = newDist / oldDistLlNutritionInfo;

                                        // if scale > 1, zoom in image. If scale < 1, zoom out image based on midpoint of image
                                        nutritionInfoLinearLayout.setScaleX(scale);
                                        nutritionInfoLinearLayout.setScaleY(scale);
                                    }
                                }
                                break;
                            case MotionEvent.ACTION_UP: // first finger up
                                break;

                            case MotionEvent.ACTION_POINTER_UP: // second finger up
                                mode = NONE;
                                break;

                            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down
                                // distance between two fingers
                                oldDistLlNutritionInfo = spacing(event);
                                if (oldDistLlNutritionInfo > 5f) {
                                    mode = ZOOM;
                                }
                                break;
                        }
                        return true;
                    }
                });


                recipeImageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
                            @Override
                            public boolean onScale(ScaleGestureDetector detector) {
                                scale *= detector.getScaleFactor();
                                scale = Math.max(0.1f, Math.min(scale, 5.0f));
                                recipeImageView.setScaleX(scale);
                                recipeImageView.setScaleY(scale);
                                return true;
                            }

                            @Override
                            public boolean onScaleBegin(ScaleGestureDetector detector) {
                                return true;
                            }

                            @Override
                            public void onScaleEnd(ScaleGestureDetector detector) {
                            }
                        });
                        scaleGestureDetector.onTouchEvent(event);

                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_DOWN: // first finger down
                                currentX = (int) event.getX();
                                currentY = (int) event.getY();
                                mode = DRAG;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (mode == DRAG) {
                                    int x = (int) event.getX();
                                    int y = (int) event.getY();
                                    int deltaX = x - currentX;
                                    int deltaY = y - currentY;
                                    currentX = x;
                                    currentY = y;
                                    recipeImageView.setX(recipeImageView.getX() + deltaX);
                                    recipeImageView.setY(recipeImageView.getY() + deltaY);
                                } else if (mode == ZOOM) {
                                    float newDist = spacing(event);
                                    if (newDist > 5f) {
                                        float scale = newDist / oldDistImage;
                                        // if scale > 1, zoom in image. If scale < 1, zoom out image based on midpoint of image
                                        recipeImageView.setScaleX(scale);
                                        recipeImageView.setScaleY(scale);
                                    }
                                }
                                break;
                            case MotionEvent.ACTION_UP: // first finger up
                                break;

                            case MotionEvent.ACTION_POINTER_UP: // second finger up
                                mode = NONE;
                                break;

                            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down
                                // distance between two fingers
                                oldDistImage = spacing(event);
                                if (oldDistImage > 5f) {
                                    mode = ZOOM;
                                }
                                break;
                        }
                        return true;
                    }
                });


                buttonShareRecipe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap bitmap = getBitmapFromView(llSharableRecipeInfo);
                        String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(),
                                bitmap, recipe.getTitle(), null);
                        Uri bmpUri = Uri.parse(path);
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                        shareIntent.setType(SHARE_TYPE_IMAGE);
                        requireContext().startActivity(Intent.createChooser(shareIntent, requireContext().getString(R.string.share_image)));
                    }
                });

                buttonCancelSharing.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // rollback to previous fragment
                        (requireActivity()).onBackPressed();
                    }
                });


                PinchRecyclerView recipeDetailIngredientsRecyclerView =
                        binding.recipeDetailIngredientsRecyclerView;
                PinchRecyclerView recipeDetailInstructionsRecyclerView =
                        binding.recipeDetailInstructionsRecyclerView;


                Glide.with(requireContext()).load(recipe.getImageUrl())
                        .placeholder(R.drawable.recipe_image_placeholder)
                        .centerCrop()
                        .transform(new RoundedCorners(Constant.IMAGE_RADIUS))
                        .into(recipeImageView);
                recipeTitleTextView.setText(recipe.getTitle());
                readyInTimeTextView.setText(MessageFormat.format("{0}m", recipe.getReadyInMinutes()));
                typesTextView.setText(RecipeDetailsFragment.getDishTypesStringFromRecipe(recipe));
                servingsTextView.setText(String.valueOf(recipe.getServings()));
                dietsTextView.setText(RecipeDetailsFragment.getDietStringFromRecipe(recipe));

                // get ingredients from jsonArray and display them using adapter
                RecipeIngredientsAdapter recipeIngredientsAdapter = new RecipeIngredientsAdapter(recipe.getExtendedIngredients(), getContext());
                recipeDetailIngredientsRecyclerView.setAdapter(recipeIngredientsAdapter);
                recipeDetailIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


                // get instructions from jsonArray and display them using adapter
                RecipeInstructionsAdapter recipeInstructionsAdapter = new RecipeInstructionsAdapter(recipe.getAnalyzedInstructions(), getContext());
                recipeDetailInstructionsRecyclerView.setAdapter(recipeInstructionsAdapter);
                recipeDetailInstructionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
    }


    /**
     * checks the spacing between the two fingers on touch
     * @param event
     * @return distance between two fingers as a float
     */
    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private Bitmap getBitmapFromView(View shareRecipeView) {
        Bitmap bitmap = Bitmap.createBitmap(shareRecipeView.getWidth(), shareRecipeView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        shareRecipeView.draw(canvas);
        return bitmap;
    }
}