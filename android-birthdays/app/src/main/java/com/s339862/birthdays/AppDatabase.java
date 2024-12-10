package com.s339862.birthdays;

import androidx.room.Database;
import androidx.room.RoomDatabase;

// Room database for storing friends
// Contains a single entity/table for friends
@Database(entities = {Friend.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    // Returns the DAO for the friends table
    // The DAO is defined in FriendDao.java and contains methods for database operations
    public abstract FriendDao friendDao();
}
