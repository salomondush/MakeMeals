package com.example.makemeals.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.parse.ParseUser;

@Entity
public class SearchHistory {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "user_id")
    public String userId;

    @ColumnInfo(name = "search_term")
    public String searchQuery;

    @ColumnInfo(name = "search_type")
    public String searchType;

    @ColumnInfo(name = "search_diet")
    public String searchDiet;

    public static SearchHistory createEntry(String query, String type, String diet) {
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.searchQuery = query;
        searchHistory.searchType = type;
        searchHistory.searchDiet = diet;
        searchHistory.userId = ParseUser.getCurrentUser().getObjectId();
        return searchHistory;
    }
}
