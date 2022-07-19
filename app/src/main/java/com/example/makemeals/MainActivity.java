package com.example.makemeals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.makemeals.databinding.ActivityMainBinding;
import com.example.makemeals.fragments.RecipesFragment;
import com.example.makemeals.fragments.HomeFragment;
import com.example.makemeals.fragments.IngredientsFragment;
import com.example.makemeals.fragments.ProfileFragment;
import com.example.makemeals.fragments.RecipeDetailsFragment;
import com.example.makemeals.fragments.SearchFragment;
import com.example.makemeals.fragments.ShareRecipeFragment;
import com.example.makemeals.fragments.ShoppingListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private TextView tvOnlineStatus;
    private TextView textCartItemCount;
    private int cartItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvOnlineStatus = binding.tvOnlineStatus;

        cartItemCount = 0;
        getShoppingListCount();

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = binding.bottomNavigation;

        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        bottomNavigationView.setSelectedItemId(R.id.home);


        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            // run on ui thread

            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                // run on ui thread
                runOnUiThread(() -> {
                    tvOnlineStatus.setVisibility(View.GONE);
                });
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                runOnUiThread(() -> {
                    tvOnlineStatus.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
                final boolean unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
            }
        };

        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);

        // show if the device is online or not
        if (connectivityManager.getActiveNetworkInfo() != null) {
            tvOnlineStatus.setVisibility(View.GONE);
        } else {
            tvOnlineStatus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem menuItem = menu.findItem(R.id.shoppingCart);
        View actionView = menuItem.getActionView();
        textCartItemCount = (TextView) actionView.findViewById(R.id.tvShoppingCount);
        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.ingredients) {
            Fragment fragment = new IngredientsFragment();
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        } else if (item.getItemId() == R.id.shoppingCart) {
            Fragment fragment = new ShoppingListFragment();
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

    public void showRecipeDetails() {
        Fragment fragment = new RecipeDetailsFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void showRecipeSharingFragment() {
        Fragment fragment = new ShareRecipeFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupBadge() {
        if (textCartItemCount != null) {
            if (cartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                int MAX_NOTIFICATIONS = 99;
                textCartItemCount.setText(String.valueOf(Math.min(cartItemCount, MAX_NOTIFICATIONS)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void getShoppingListCount() {
        ParseUser.getQuery()
            .setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK)
            .getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if (e == null) {
                        // add all the ingredients from object to the shopping list
                        setCartItemCount(Objects.requireNonNull(object.getJSONArray("shoppingList")).length());
                    } else {
                        e.printStackTrace();
                    }
                }
            });
    }

    public int getCartItemCount() {
        return cartItemCount;
    }

    public void setCartItemCount(int mCartItemCount) {
        this.cartItemCount = mCartItemCount;
        setupBadge();
    }

    public void incrementCartItemCount(int count) {
        cartItemCount += count;
        setupBadge();
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.search:
                fragment = new SearchFragment();
                break;
            case R.id.favorites:
                fragment = new RecipesFragment();
                break;
            case R.id.profile:
                fragment = new ProfileFragment();
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
    }
}