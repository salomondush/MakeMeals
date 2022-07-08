package com.example.makemeals.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.makemeals.MainActivity;
import com.example.makemeals.R;
import com.example.makemeals.adapters.RecipeIngredientsAdapter;
import com.example.makemeals.adapters.ShoppingListAdapter;
import com.example.makemeals.databinding.FragmentShoppingListBinding;
import com.example.makemeals.models.ShoppingItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.boltsinternal.Task;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment {
    public final String TAG = "ShoppingListFragment";
    public final String SHOPPING_LIST_KEY = "shoppingList";
    public final int CHECKED_ITEMS_INITIAL_VALUE = 0;
    private List<ShoppingItem> shoppingList;
    private ShoppingListAdapter shoppingListAdapter;
    private FragmentShoppingListBinding binding;
    private MaterialButton btnClearShoppingList;
    private CircularProgressIndicator progressIndicator;
    private int checkedItems;
    private TextView tvUncheckedCount;
    private TextView tvCheckedCount;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingListFragment newInstance(String param1, String param2) {
        ShoppingListFragment fragment = new ShoppingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkedItems = CHECKED_ITEMS_INITIAL_VALUE;
        binding = FragmentShoppingListBinding.bind(view);
        btnClearShoppingList = binding.btnClearShoppingList;
        tvUncheckedCount = binding.tvUncheckedCount;
        tvCheckedCount = binding.tvCheckedCount;
        progressIndicator = binding.progressIndicator;
        RecyclerView rvShoppingList = binding.rvShoppingList;

        shoppingList = new ArrayList<>();
        shoppingListAdapter = new ShoppingListAdapter(shoppingList, getContext());
        rvShoppingList.setAdapter(shoppingListAdapter);
        rvShoppingList.setLayoutManager(new LinearLayoutManager(getContext()));
        getShoppingList();

        shoppingListAdapter.setOnIsCheckedListener(new ShoppingListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                ShoppingItem shoppingItem = shoppingList.get(position);
                shoppingItem.setIsChecked(!shoppingItem.getIsChecked());
                if (shoppingItem.getIsChecked()) {
                    incrementCheckedItems();
                } else {
                    decrementCheckedItems();
                }

                shoppingItem.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            // reverse the checked state on shopping item in the adapter
                            shoppingItem.setIsChecked(!shoppingItem.getIsChecked());
                            shoppingListAdapter.notifyItemChanged(position);
                        }
                    }
                });
            }
        });

        btnClearShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show dialog to confirm clear shopping list
                showClearShoppingListDialog();
            }
        });
    }

    public void getShoppingList() {
       // get shopping list field from the current user
        showProgressBar();
        ParseUser.getQuery().getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    // add all the ingredients from object to the shopping list
                    List<String> shoppingListIds = object.getList(SHOPPING_LIST_KEY);
                    int cartItemIdCount = Objects.requireNonNull(shoppingListIds).size();
                    ((MainActivity) requireActivity()).setCartItemCount(cartItemIdCount);

                    // get shoppingItems from Parse with matching ids
                    ParseQuery<ShoppingItem> query = ParseQuery.getQuery("ShoppingItem");
                    query.whereContainedIn("objectId", shoppingListIds);
                    query.findInBackground((shoppingItems, err) -> {

                        for (ShoppingItem shoppingItem : shoppingItems) {
                            if (shoppingItem.getIsChecked()) {
                                incrementCheckedItems();
                            }
                        }

                        if (err == null) {
                            // add all the shopping items to the shopping list
                            hideProgressBar();
                            shoppingList.addAll(shoppingItems);
                            shoppingListAdapter.notifyDataSetChanged();

                           initializeShoppingListState();
                        } else {
                            Log.e(TAG, "Error getting shopping list: " + err.getMessage());
                        }
                    });
                }
            }
        });
    }

    public void showClearShoppingListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Clear Shopping List");
        builder.setMessage("Are you sure you want to clear your shopping list?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            clearShoppingList();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private void clearShoppingList() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> shoppingListIds = currentUser.getList(SHOPPING_LIST_KEY);
        currentUser.removeAll(SHOPPING_LIST_KEY, shoppingListIds);
        currentUser.saveInBackground((e -> {
            if (e == null) {
                // delete shopping items from ShoppingItem table
                ParseQuery<ShoppingItem> query = ParseQuery.getQuery("ShoppingItem");
                query.whereContainedIn("objectId", shoppingListIds);
                query.findInBackground((shoppingItems, err) -> {
                    if (err == null) {
                        for (ShoppingItem shoppingItem : shoppingItems) {
                            shoppingItem.deleteInBackground();
                        }
                        shoppingList.clear();
                        shoppingListAdapter.notifyDataSetChanged();
                        ((MainActivity) requireActivity()).setCartItemCount(0);
                        resetShoppingListState();
                    } else {
                        Log.e(TAG, "Error deleting shopping items: " + err.getMessage());
                    }
                });
            } else {
                Log.e(TAG, "Error clearing shopping list: " + e.getMessage());
            }
        }));
    }

    private void showProgressBar() {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressIndicator.setVisibility(View.GONE);
    }

    private void resetShoppingListState() {
        checkedItems = CHECKED_ITEMS_INITIAL_VALUE;
        tvUncheckedCount.setText(String.valueOf(checkedItems));
        tvCheckedCount.setText(String.valueOf(shoppingList.size() - checkedItems));
    }

    private void initializeShoppingListState(){
        btnClearShoppingList.setEnabled(checkedItems == shoppingList.size());
        tvCheckedCount.setText(String.valueOf(checkedItems));
        tvUncheckedCount.setText(String.valueOf(shoppingList.size() - checkedItems));
    }

    private void incrementCheckedItems() {
        checkedItems++;
        tvCheckedCount.setText(String.valueOf(checkedItems));
        tvUncheckedCount.setText(String.valueOf(shoppingList.size() - checkedItems));
        if (checkedItems == shoppingList.size()) {
            btnClearShoppingList.setEnabled(true);
        }
    }

    private void decrementCheckedItems() {
        checkedItems--;
        tvCheckedCount.setText(String.valueOf(checkedItems));
        tvUncheckedCount.setText(String.valueOf(shoppingList.size() - checkedItems));
        if (checkedItems == 0) {
            btnClearShoppingList.setEnabled(false);
        }
    }
}