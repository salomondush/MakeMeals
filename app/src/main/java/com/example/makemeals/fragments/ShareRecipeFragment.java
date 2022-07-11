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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
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
import com.example.makemeals.MainActivity;
import com.example.makemeals.R;
import com.example.makemeals.adapters.RecipeIngredientsAdapter;
import com.example.makemeals.adapters.RecipeInstructionsAdapter;
import com.example.makemeals.databinding.FragmentShareRecipeBinding;
import com.example.makemeals.models.Recipe;
import com.google.android.material.button.MaterialButton;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShareRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShareRecipeFragment extends Fragment {
    private Recipe recipe;
    private LinearLayout llNutritionInfo;
    private Float scale = 1f;
    private int currentX;
    private int currentY;
    private ConstraintLayout llSharableRecipeInfo;
    private static final String ARG_PARAM1 = "recipe";

    // these PointF objects are used to record the point(s) the user is touching
//    PointF start = new PointF();
    float oldDistImage = 1f;
    float oldDistLlNutritionInfo = 1f;
    float oldDistIngredients = 1f;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    public ShareRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipe Parameter 1.
     * @return A new instance of fragment ShareRecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShareRecipeFragment newInstance(Recipe recipe) {
        ShareRecipeFragment fragment = new ShareRecipeFragment();
        Bundle args = new Bundle();
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
        return inflater.inflate(R.layout.fragment_share_recipe, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentShareRecipeBinding binding = FragmentShareRecipeBinding.bind(view);


        ImageView ivRecipeImage = binding.ivRecipeImage;
        TextView tvRecipeTitle = binding.tvRecipeTitle;
        TextView tvReadyInTime = binding.tvReadyInTime;
        TextView tvTypes = binding.tvTypes;
        TextView tvServings = binding.tvServings;
        TextView tvDiets = binding.tvDiets;
        llNutritionInfo = binding.llNutritionInfo;
        MaterialButton btnShareRecipe = binding.btnShareRecipe;
        MaterialButton btnCancelSharing = binding.btnCancelSharing;
        llSharableRecipeInfo = binding.llSharableRecipeInfo;


        llNutritionInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        scale *= detector.getScaleFactor();
                        scale = Math.max(0.1f, Math.min(scale, 5.0f));
                        llNutritionInfo.setScaleX(scale);
                        llNutritionInfo.setScaleY(scale);
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
                            llNutritionInfo.setX(llNutritionInfo.getX() + deltaX);
                            llNutritionInfo.setY(llNutritionInfo.getY() + deltaY);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 5f) {
                                float scale = newDist / oldDistLlNutritionInfo;

                                // if scale > 1, zoom in image. If scale < 1, zoom out image based on midpoint of image
                                if (scale > 1) {
                                    llNutritionInfo.setScaleX(scale);
                                    llNutritionInfo.setScaleY(scale);

                                } else {
                                    float midX = llNutritionInfo.getX() + llNutritionInfo.getWidth() / 2f;
                                    float midY = llNutritionInfo.getY() + llNutritionInfo.getHeight() / 2f;
                                    llNutritionInfo.setScaleX(scale);
                                    llNutritionInfo.setScaleY(scale);
                                    llNutritionInfo.setX(midX - llNutritionInfo.getWidth() / 2f);
                                    llNutritionInfo.setY(midY - llNutritionInfo.getHeight() / 2f);
                                }
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
                        Log.i("TAG", "ACTION_POINTER_DOWN");
                        oldDistLlNutritionInfo = spacing(event);
                        if (oldDistLlNutritionInfo > 5f) {
                            mode = ZOOM;
                        }
                        break;
                }
                return true;
            }
        });


        ivRecipeImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        scale *= detector.getScaleFactor();
                        scale = Math.max(0.1f, Math.min(scale, 5.0f));
                        ivRecipeImage.setScaleX(scale);
                        ivRecipeImage.setScaleY(scale);
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
                            ivRecipeImage.setX(ivRecipeImage.getX() + deltaX);
                            ivRecipeImage.setY(ivRecipeImage.getY() + deltaY);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 5f) {
                                float scale = newDist / oldDistImage;

                                // if scale > 1, zoom in image. If scale < 1, zoom out image based on midpoint of image
                                if (scale > 1) {
                                    ivRecipeImage.setScaleX(scale);
                                    ivRecipeImage.setScaleY(scale);

                                } else {
                                    float midX = ivRecipeImage.getX() + ivRecipeImage.getWidth() / 2f;
                                    float midY = ivRecipeImage.getY() + ivRecipeImage.getHeight() / 2f;
                                    ivRecipeImage.setScaleX(scale);
                                    ivRecipeImage.setScaleY(scale);
                                    ivRecipeImage.setX(midX - ivRecipeImage.getWidth() / 2f);
                                    ivRecipeImage.setY(midY - ivRecipeImage.getHeight() / 2f);
                                }
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
                        Log.i("TAG", "ACTION_POINTER_DOWN");
                        oldDistImage = spacing(event);
                        if (oldDistImage > 5f) {
                            mode = ZOOM;
                        }
                        break;
                }
                return true;
            }
        });


        btnShareRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = getBitmapFromView(llSharableRecipeInfo);
                String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(),
                        bitmap, recipe.getTitle(), null);
                Uri bmpUri = Uri.parse(path);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("image/*");
                requireContext().startActivity(Intent.createChooser(shareIntent, "Share Image"));
            }
        });

        btnCancelSharing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // rollback to previous fragment
                ((MainActivity) requireActivity()).onBackPressed();
            }
        });


        RecyclerView rvRecipeDetailIngredients = binding.rvRecipeDetailIngredients;
        RecyclerView rvRecipeDetailInstructions = binding.rvRecipeDetailInstructions;


        Glide.with(requireContext()).load(recipe.getImageUrl())
                .centerCrop()
                .transform(new RoundedCorners(Constant.IMAGE_RADIUS))
                .into(ivRecipeImage);
        tvRecipeTitle.setText(recipe.getTitle());
        tvReadyInTime.setText(MessageFormat.format("{0}m", recipe.getReadyInMinutes()));
        tvTypes.setText(RecipeDetailsFragment.getDishTypesStringFromRecipe(recipe));
        tvServings.setText(String.valueOf(recipe.getServings()));
        tvDiets.setText(RecipeDetailsFragment.getDietStringFromRecipe(recipe));

        // get ingredients from jsonArray and display them using adapter
        RecipeIngredientsAdapter recipeIngredientsAdapter = new RecipeIngredientsAdapter(recipe.getExtendedIngredients(), getContext());
        rvRecipeDetailIngredients.setAdapter(recipeIngredientsAdapter);
        rvRecipeDetailIngredients.setLayoutManager(new LinearLayoutManager(getContext()));


        // get instructions from jsonArray and display them using adapter
        RecipeInstructionsAdapter recipeInstructionsAdapter = new RecipeInstructionsAdapter(recipe.getAnalyzedInstructions(), getContext());
        rvRecipeDetailInstructions.setAdapter(recipeInstructionsAdapter);
        rvRecipeDetailInstructions.setLayoutManager(new LinearLayoutManager(getContext()));

        //  detect pinch to zoom on recycler view and resize all items in recycler view
        rvRecipeDetailIngredients.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        scale *= detector.getScaleFactor();
                        scale = Math.max(0.1f, Math.min(scale, 5.0f));
                        rvRecipeDetailIngredients.setScaleX(scale);
                        rvRecipeDetailIngredients.setScaleY(scale);
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
                    case MotionEvent.ACTION_MOVE:
                        if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 5f) {
                                float scale = newDist / oldDistLlNutritionInfo;

                                Log.i("TAG", "scale: " + scale);

                                // if scale > 1, zoom in image. If scale < 1, zoom out image based on midpoint of image
                                if (scale > 1) {
                                    // scale up all items in recycler view
                                    recipeIngredientsAdapter.scaleAll(1.2f);
                                } else {
                                    // scale down all items in recycler view

                                }
                            }
                        }
                        break;

                    case MotionEvent.ACTION_POINTER_UP: // second finger up
                        mode = NONE;
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down
                        // distance between two fingers
                        Log.i("TAG", "ingredients ACTION_POINTER_DOWN");
                        oldDistImage = spacing(event);
                        if (oldDistImage > 5f) {
                            mode = ZOOM;
                        }
                        break;
                }
                return true;
            }
        });
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
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