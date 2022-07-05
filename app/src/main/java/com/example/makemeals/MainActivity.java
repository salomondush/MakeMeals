package com.example.makemeals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.makemeals.databinding.ActivityMainBinding;
import com.example.makemeals.fragments.FavoritesFragment;
import com.example.makemeals.fragments.HomeFragment;
import com.example.makemeals.fragments.IngredientsFragment;
import com.example.makemeals.fragments.ProfileFragment;
import com.example.makemeals.fragments.RecipeDetailsFragment;
import com.example.makemeals.fragments.SearchFragment;
import com.example.makemeals.models.Recipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        bottomNavigationView = binding.bottomNavigation;

        bottomNavigationView.setOnItemSelectedListener( item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.search:
                    fragment = new SearchFragment();
                    break;
                case R.id.favorites:
                    fragment = new FavoritesFragment();
                    break;
                case R.id.home:
                default:
                    fragment = new HomeFragment();
                    break;
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.flContainer, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile){
            // navigate to the ProfileFragment
            Fragment fragment = new ProfileFragment();
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        } else if (item.getItemId() == R.id.ingredients){
            Fragment fragment = new IngredientsFragment();
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void showRecipeDetails(Recipe recipe) {
        Fragment fragment = RecipeDetailsFragment.newInstance(recipe);
        fragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}