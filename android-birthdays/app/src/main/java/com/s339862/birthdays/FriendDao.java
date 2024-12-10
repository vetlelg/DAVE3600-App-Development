package com.s339862.birthdays;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// Data Access Object for Room database
// Contains methods for interacting with the database
@Dao
public interface FriendDao {
    @Query("SELECT * FROM friend")
    List<Friend> getAll();

    @Update
    void update(Friend friend);

    @Insert
    void insert(Friend friend);

    @Delete
    void delete(Friend friend);
}
