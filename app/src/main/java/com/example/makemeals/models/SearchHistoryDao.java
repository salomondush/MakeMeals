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

    // get the last 5 search history
    @Query("SELECT * FROM searchhistory ORDER BY id DESC LIMIT 5")
    List<SearchHistory> getRecent();

    @Insert
    void insertAll(SearchHistory... searchHistories);

    @Query("DELETE FROM searchhistory")
    void deleteAll();

    // size of the search history
    @Query("SELECT COUNT(*) FROM searchhistory")
    int getSize();

    // delete n least recent search history
    @Query("DELETE FROM searchhistory WHERE id IN (SELECT id FROM searchhistory ORDER BY id ASC LIMIT :limit)")
    void deleteNLeastRecent(int limit);
}
