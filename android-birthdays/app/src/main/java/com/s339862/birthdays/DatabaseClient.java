package com.s339862.birthdays;

import android.content.Context;
import androidx.room.Room;

// This class ensures that only one instance of the database is created
// throughout the application's lifecycle
public class DatabaseClient {

    // This variable holds the single instance of the DatabaseClient
    // It's used to check if the database has been created
    // and to return the Room AppDatabase
    private static DatabaseClient mInstance;

    // This variable holds the Room database
    final private AppDatabase appDatabase;

    // This constructor creates the Room database
    private DatabaseClient(Context mCtx) {
        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, "Friend").build();
    }

    // If the instance is null, create a new DatabaseClient
    // Otherwise, return the existing instance
    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    // Return the Room database to make database operations
    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
