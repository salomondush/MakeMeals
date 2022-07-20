package com.example.makemeals;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.makemeals.models.SearchHistory;
import com.example.makemeals.models.SearchHistoryDao;

@Database(entities = {SearchHistory.class}, version = 1)
public abstract class SearchHistoryDataBase extends RoomDatabase {
    public abstract SearchHistoryDao searchHistoryDao();

    public static final String NAME = "SearchHistoryDataBase";
}
