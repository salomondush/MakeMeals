package com.example.makemeals;

import android.app.Application;

import com.example.makemeals.models.Ingredient;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Ingredient.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("QOsFGMyspfK70xND7ekNVzNrB6KCtbK5PHRydK1b")
                .clientKey("bxisWPUvxAmryuasZQArxvrwZHc0hj7KEQPwKL4J")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
