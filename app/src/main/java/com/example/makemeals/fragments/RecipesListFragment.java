package com.example.makemeals.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.makemeals.MainActivity;
import com.example.makemeals.R;
import com.example.makemeals.adapters.RecipeAdapter;
import com.example.makemeals.models.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipesListFragment extends Fragment {
    private RecipeAdapter recipeAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private List<Recipe> recipes;
    private String mParam2;

    public RecipesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipes Parameter 1.
     * @return A new instance of fragment RecipesListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipesListFragment newInstance(List<Recipe> recipes) {
        RecipesListFragment fragment = new RecipesListFragment();
        Bundle args = new Bundle();
        // initialize the recipes list
        args.putParcelableArrayList(ARG_PARAM1, (ArrayList<? extends Parcelable>) recipes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipes = getArguments().getParcelableArrayList(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvRecipeList = view.findViewById(R.id.rvRecipeList);
        recipeAdapter = new RecipeAdapter(recipes, getContext());


        recipeAdapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
             @Override
             public void onItemClick(View itemView, int position) {

                 // call the activity to show the recipe details
                 ((MainActivity) requireActivity()).showRecipeDetails(recipes.get(position));

             }
        });


        rvRecipeList.setAdapter(recipeAdapter);
        rvRecipeList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void updateRecipes(List<Recipe> recipes) {
        this.recipes.addAll(recipes);
        recipeAdapter.notifyDataSetChanged();
    }


}