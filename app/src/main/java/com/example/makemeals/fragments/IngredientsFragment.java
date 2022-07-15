package com.example.makemeals.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.makemeals.R;
import com.example.makemeals.adapters.IngredientsPageAdapter;
import com.example.makemeals.adapters.IngredientsDialogAdapter;
import com.example.makemeals.models.Ingredient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IngredientsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IngredientsFragment extends Fragment {
    private static final String USER = "user";
    private static final String FORMAT = "image/jpeg";
    private RecyclerView rvIngredients;
    private IngredientsPageAdapter adapter;
    private List<Ingredient> ingredients;
    private List<String> dialogIngredients;
    private EditText etIngredientName;
    private ImageButton addButton;
    private LinearLayout addLayout;


    private static final String REST_URL = "http://172.20.9.185:3200/file/analyse";
    private static final int MINIMUM_LENGTH = 2;
    private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 3;

    private CircularProgressIndicator progressIndicator;


    // material popup
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;

    public IngredientsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment IngredientsFragment.
     */
    public static IngredientsFragment newInstance() {
        IngredientsFragment fragment = new IngredientsFragment();
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
        return inflater.inflate(R.layout.fragment_ingredients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());

        rvIngredients = view.findViewById(R.id.rvIngredients);
        addButton = view.findViewById(R.id.addButton);
        addLayout = view.findViewById(R.id.addLayout);
        ImageButton buttonAddIngredient = view.findViewById(R.id.buttonAddIngredient);
        etIngredientName = view.findViewById(R.id.etIngredientName);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        ImageButton galleryButton = view.findViewById(R.id.galleryButton);

        ingredients = new ArrayList<>();
        adapter = new IngredientsPageAdapter(ingredients, getContext(), false);
        rvIngredients.setAdapter(adapter);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new IngredientsPageAdapter.SwipeHelper(adapter, rvIngredients));
        itemTouchHelper.attachToRecyclerView(rvIngredients);


        adapter.setOnRemoveIngredientClickListener(new IngredientsPageAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View itemView, int position) {
                adapter.deleteItem(rvIngredients, position);
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addLayout.getVisibility() == View.GONE) {
                    addLayout.setVisibility(View.VISIBLE);
                    addButton.setBackground
                            (ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.make_meals_remove_icon, null));
                } else {
                    addLayout.setVisibility(View.GONE);
                    addButton.setBackground
                            (ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.add_icon, null));
                }
            }
        });

        buttonAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredientName = etIngredientName.getText().toString();
                if (ingredientName.isEmpty()) {
                    etIngredientName.setError(requireContext().getString(R.string.ingredient_name_is_required));
                    return;
                }
                Ingredient ingredient = new Ingredient();
                ingredient.setName(ingredientName);
                ingredient.setUser(ParseUser.getCurrentUser());
                ingredient.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            etIngredientName.setText("");
                            ingredients.add(ingredient);
                            adapter.notifyItemInserted(ingredients.size() - 1);
                        } else {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        queryIngredients();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                showProgressBar();
                getImageText(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void launchAddIngredientDialog(List<String> ingredientItems) {
        View addIngredientsDialogView = getLayoutInflater().
                inflate(R.layout.add_ingredients_dialog, null, false);

        dialogIngredients = new ArrayList<>(ingredientItems);

        RecyclerView rvDialogIngredients = addIngredientsDialogView.findViewById(R.id.rvDialogIngredients);
        IngredientsDialogAdapter dialogAdapter = new IngredientsDialogAdapter(dialogIngredients, getContext());
        rvDialogIngredients.setAdapter(dialogAdapter);
        rvDialogIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new IngredientsDialogAdapter.SwipeHelper(dialogAdapter, rvDialogIngredients));
        itemTouchHelper.attachToRecyclerView(rvDialogIngredients);

        materialAlertDialogBuilder.setView(addIngredientsDialogView);
        materialAlertDialogBuilder.setTitle(requireContext().getString(R.string.add_ingredients));
        materialAlertDialogBuilder.setMessage(requireContext().getString(R.string.swipe_left_to_remove_item));
        materialAlertDialogBuilder.setPositiveButton(requireContext().getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgressBar();

                List<Ingredient> newIngredients = new ArrayList<>();

                // save all ingredients in rvDialogIngredients to dialogIngredients
                for (int i = 0; i < dialogIngredients.size(); i++) {
                    String ingredientName = dialogIngredients.get(i);
                    if (!ingredientName.isEmpty()) {
                        Ingredient ingredient = new Ingredient();
                        ingredient.setName(ingredientName);
                        ingredient.setUser(ParseUser.getCurrentUser());
                        newIngredients.add(ingredient);
                    }
                }

                // save all ingredients in dialogIngredients to ingredients
                Ingredient.saveAllInBackground(newIngredients, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            ingredients.addAll(newIngredients);
                            adapter.notifyDataSetChanged();
                            hideProgressBar();
                        } else {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void queryIngredients() {
        showProgressBar();
        ParseQuery<Ingredient> query = ParseQuery.getQuery(Ingredient.class);
        query.whereEqualTo(USER, ParseUser.getCurrentUser());
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground(new FindCallback<Ingredient>() {
            @Override
            public void done(List<Ingredient> objects, ParseException e) {
                if (e == null) {
                    ingredients.clear();
                    ingredients.addAll(objects);
                    adapter.notifyDataSetChanged();
                    hideProgressBar();
                } else {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Method that takes an image from the camera and makes a query to the rest API to get the
     * image's text
     *
     * @param bitmap
     */
    private void getImageText(Bitmap bitmap) {
        AsyncHttpClient client = new AsyncHttpClient();

        // convert bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        RequestBody body = RequestBody.create(byteArray, MediaType.parse(FORMAT));

        String FILE_NAME = "image.jpg";
        String IMAGE_FIELD_NAME = "image";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(IMAGE_FIELD_NAME, FILE_NAME, body)
                .build();

        client.post(REST_URL, new RequestHeaders(), new RequestParams(), requestBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    hideProgressBar();
                    launchAddIngredientDialog(extractItemsFromCSV(json.jsonObject.getString("response")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                        Toast.makeText(getContext(), requireContext().getString(R.string.failed_to_get_image_text), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private List<String> extractItemsFromCSV(String csv) {

        List<String> items = new ArrayList<>();

        String[] lines = csv.split("\n");
        for (String line : lines) {
            String[] item = line.split(",");
            if (item.length >= MINIMUM_LENGTH) {
                // todo: index depends on the type of receipt
                items.add(item[1]);
            }
        }
        return items;
    }

    private void showProgressBar() {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressIndicator.setVisibility(View.GONE);
    }
}