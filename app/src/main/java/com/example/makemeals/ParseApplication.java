package com.example.makemeals;

import android.app.Application;

import androidx.room.Room;

import com.example.makemeals.models.Ingredient;
import com.example.makemeals.models.Recipe;
import com.example.makemeals.models.Recommendation;
import com.example.makemeals.models.ShoppingItem;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    // Initializes Parse SDK as soon as the application is created
    SearchHistoryDataBase searchHistoryDataBase;

    @Override
    public void onCreate() {
        super.onCreate();

        searchHistoryDataBase = Room.databaseBuilder(this, SearchHistoryDataBase.class,
                        SearchHistoryDataBase.NAME).build();

        ParseObject.registerSubclass(Ingredient.class);
        ParseObject.registerSubclass(Recipe.class);
        ParseObject.registerSubclass(ShoppingItem.class);
        ParseObject.registerSubclass(Recommendation.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("QOsFGMyspfK70xND7ekNVzNrB6KCtbK5PHRydK1b")
                .clientKey("bxisWPUvxAmryuasZQArxvrwZHc0hj7KEQPwKL4J")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }

    public SearchHistoryDataBase getSearchHistoryDataBase() {
        return searchHistoryDataBase;
    }
}
