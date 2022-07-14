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
    private static final String TAG = "IngredientsFragment";
    private static final String USER = "user";
    private static final String FORMAT = "image/jpeg";
    private RecyclerView rvIngredients;
    private IngredientsPageAdapter adapter;
    private List<Ingredient> ingredients;
    private List<String> dialogIngredients;
    private EditText etIngredientName;
    private ImageButton addButton;
    private ImageButton cameraButton;
    private RelativeLayout addLayout;

    private File photoFile;

    public static final String REST_URL = "http://172.23.178.111:3200/file/analyse";
    public static final int MINIMUM_LENGTH = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 3;

    private CircularProgressIndicator progressIndicator;


    // material popup
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;

    public IngredientsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
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
        ImageButton btnAddIngredient = view.findViewById(R.id.btnAddIngredient);
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

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredientName = etIngredientName.getText().toString();
                if (ingredientName.isEmpty()) {
                    etIngredientName.setError(requireContext().getString(R.string.ingredient_name_is_required));
                    return;
                }
                Ingredient ingredient = new Ingredient();
                ingredient.setName(ingredientName);
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

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            // by this point we have the camera photo on disk
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            // RESIZE BITMAP, see section below
            // Load the taken image into a preview

        } else { // Result was a failure
            Toast.makeText(getContext(), requireContext().getString(R.string.picture_not_taken), Toast.LENGTH_SHORT).show();
        }
    }

    public void launchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        String PHOTO_FILE_NAME = "photo.jpg";
        photoFile = getPhotoFileUri(PHOTO_FILE_NAME);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Toast.makeText(requireContext(), requireContext().getString(R.string.failed_to_create_directory), Toast.LENGTH_SHORT).show();
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
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
                    e.printStackTrace();
                }
            }
        });
    }

    // method that takes an image from the camera and makes a query to the rest API to get the image's text
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
                Toast.makeText(getContext(), requireContext().getString(R.string.failed_to_get_image_text), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> extractItemsFromCSV(String csv){

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
        // Show progress item
        progressIndicator.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        // Hide progress item
        progressIndicator.setVisibility(View.GONE);
    }
}