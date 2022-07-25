package com.example.makemeals.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.makemeals.Constant;
import com.example.makemeals.MainActivity;
import com.example.makemeals.R;
import com.example.makemeals.adapters.ShoppingListAdapter;
import com.example.makemeals.databinding.FragmentShoppingListBinding;
import com.example.makemeals.models.ShoppingItem;
import com.google.android.material.button.MaterialButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment {
    public final int CHECKED_ITEMS_INITIAL_VALUE = 0;

    private final String SHOPPING_LIST_KEY = "shoppingList";
    private final String SHOPPING_ITEM = "ShoppingItem";
    private List<ShoppingItem> shoppingList;
    private ShoppingListAdapter shoppingListAdapter;
    private MaterialButton clearShoppingListMaterialButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int checkedItems;
    private TextView uncheckedCountTextView;
    private TextView checkedCountTextView;


    public ShoppingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ShoppingListFragment.
     */
    public static ShoppingListFragment newInstance() {
        ShoppingListFragment fragment = new ShoppingListFragment();
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
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkedItems = CHECKED_ITEMS_INITIAL_VALUE;
        FragmentShoppingListBinding binding = FragmentShoppingListBinding.bind(view);
        clearShoppingListMaterialButton = binding.clearShoppingListMaterialButton;
        uncheckedCountTextView = binding.uncheckedCountTextView;
        checkedCountTextView = binding.checkedCountTextView;
        RecyclerView shoppingListRecyclerView = binding.shoppingListRecyclerView;

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(this::getShoppingList);

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        shoppingList = new ArrayList<>();
        shoppingListAdapter = new ShoppingListAdapter(shoppingList, getContext());
        shoppingListRecyclerView.setAdapter(shoppingListAdapter);
        shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

        clearShoppingListMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show dialog to confirm clear shopping list
                showClearShoppingListDialog();
            }
        });
    }

    private void getShoppingList() {
       // get shopping list field from the current user
        swipeRefreshLayout.setRefreshing(true);

        ParseUser.getQuery()
                .setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK)
                .getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    // add all the ingredients from object to the shopping list
                    List<String> shoppingListIds = object.getList(SHOPPING_LIST_KEY);
                    int cartItemIdCount = Objects.requireNonNull(shoppingListIds).size();
                    ((MainActivity) requireActivity()).setCartItemCount(cartItemIdCount);

                    // get shoppingItems from Parse with matching ids
                    ParseQuery<ShoppingItem> query = ParseQuery.getQuery(SHOPPING_ITEM);
                    query.whereContainedIn(Constant.OBJECT_ID, shoppingListIds);
                    query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                    query.findInBackground((shoppingItems, err) -> {

                        resetShoppingListState();

                        if (err == null && shoppingItems != null) {
                            // add all the shopping items to the shopping list
                            swipeRefreshLayout.setRefreshing(false);
                            shoppingList.clear();
                            shoppingList.addAll(shoppingItems);
                            shoppingListAdapter.notifyDataSetChanged();

                            for (ShoppingItem shoppingItem : shoppingItems) {
                                if (shoppingItem.getIsChecked()) {
                                    incrementCheckedItems();
                                }
                            }
                        } else if (shoppingItems != null) {
                            Toast.makeText(getContext(), requireContext().getString(R.string.error_getting_shopping_list), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void showClearShoppingListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(requireContext().getString(R.string.clear_shopping_list));
        builder.setMessage(requireContext().getString(R.string.clear_shopping_list_confirmation));
        builder.setPositiveButton(requireContext().getString(R.string.yes), (dialog, which) -> {
            clearShoppingList();
        });
        builder.setNegativeButton(requireContext().getString(R.string.no), (dialog, which) -> {
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
                ParseQuery<ShoppingItem> query = ParseQuery.getQuery(SHOPPING_ITEM);
                query.whereContainedIn(Constant.OBJECT_ID, shoppingListIds);
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
                        Toast.makeText(getContext(), requireContext().getString(R.string.error_deleting_shopping_items), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), requireContext().getString(R.string.error_clearing_shopping_list), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void resetShoppingListState() {
        checkedItems = CHECKED_ITEMS_INITIAL_VALUE;
        uncheckedCountTextView.setText(String.valueOf(checkedItems));
        checkedCountTextView.setText(String.valueOf(shoppingList.size() - checkedItems));
        clearShoppingListMaterialButton.setEnabled(false);
    }

    private void incrementCheckedItems() {
        checkedItems++;
        checkedCountTextView.setText(String.valueOf(checkedItems));
        uncheckedCountTextView.setText(String.valueOf(shoppingList.size() - checkedItems));
        if (checkedItems == shoppingList.size()) {
            clearShoppingListMaterialButton.setEnabled(true);
        }
    }

    private void decrementCheckedItems() {
        checkedItems--;
        checkedCountTextView.setText(String.valueOf(checkedItems));
        uncheckedCountTextView.setText(String.valueOf(shoppingList.size() - checkedItems));
        if (checkedItems == 0 || checkedItems < shoppingList.size()) {
            clearShoppingListMaterialButton.setEnabled(false);
        }
    }
}