package com.example.makemeals.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SearchHistoryDao {

    @Query("SELECT * FROM searchhistory")
    List<SearchHistory> getAll();

    @Insert
    void insert(SearchHistory searchHistory);

    @Insert
    void insertAll(SearchHistory... searchHistories);

    @Query("DELETE FROM searchhistory")
    void deleteAll();
}
