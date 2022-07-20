package com.example.makemeals.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SearchHistory {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "search_term")
    public String searchQuery;

    @ColumnInfo(name = "search_type")
    public String searchType;

    @ColumnInfo(name = "search_diet")
    public String searchDiet;
}
