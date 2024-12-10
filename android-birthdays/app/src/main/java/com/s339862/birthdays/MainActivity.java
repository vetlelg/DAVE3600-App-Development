package com.s339862.birthdays;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // Declares the database so it can be accessed from any method in the class
    private AppDatabase database;

    // Defines methods for starting and stopping services and activities
    private void startPeriodicService() {
        Log.d("MainActivity", "Starting PeriodicService");
        Intent intent = new Intent(this, PeriodicService.class);
        startService(intent);
    }
    private void stopPeriodicService() {
        Log.d("MainActivity", "Stopping PeriodicService");
        Intent intent = new Intent(this, PeriodicService.class);
        stopService(intent);
    }
    private void startBroadcastReceiver() {
        Log.d("MainActivity", "Starting Broadcast Receiver");
        BroadcastReceiver broadcastReceiver = new SmsBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.s339862.birthdays.SEND_BIRTHDAY_SMS");
        registerReceiver(broadcastReceiver, filter);
    }
    private void sendSmsBroadcast() {
        Log.d("MainActivity", "Sending SMS Broadcast");
        Intent intent = new Intent("com.s339862.birthdays.SEND_BIRTHDAY_SMS");
        sendBroadcast(intent);
    }
    private void startSendSmsService() {
        Log.d("MainActivity", "Starting SendSmsService");
        Intent intent = new Intent(this, SendSmsService.class);
        startService(intent);
    }
    private void startAddFriendActivity() {
        Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
        startActivity(intent);
    }
    private void startEditFriendActivity(Friend friend) {
        Intent intent = new Intent(MainActivity.this, EditFriendActivity.class);
        intent.putExtra("id", friend.getId());
        startActivity(intent);
    }
    private void startSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    // Uses the apapter to create the recycler view
    // The recycler view is used to display the list of friends
    // The adapter binds the data for each friend to each item in the recycler view
    private void createRecyclerView(FriendAdapter adapter) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    // Uses the list of friends to create the adapter
    // The adapter binds the data for each friend to each item in the recycler view
    private FriendAdapter createFriendAdapter(List<Friend> friends) {
        return new FriendAdapter(friends, new FriendAdapter.OnFriendClickListener() {
            // Implements the methods from the listener interface which is defined in the adapter
            // Deletes friend when delete button is clicked
            @Override
            public void onDeleteClick(Friend friend) {
                // Uses a separate thread to delete the friend from the database
                // Then reloads the friends from the database
                Executors.newSingleThreadExecutor().execute(() -> {
                    database.friendDao().delete(friend);
                    loadFriendsFromDatabase();
                });
            }
            // Starts the EditFriendActivity when the edit button is clicked
            @Override
            public void onEditClick(Friend friend) {
                startEditFriendActivity(friend);
            }
        });
    }

    // Loads the friends from the database
    // then creates the adapter and recycler view
    private void loadFriendsFromDatabase() {
        // Uses a separate thread to get the friends from the database
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Friend> friends = database.friendDao().getAll();
            // Runs the code on the main thread to update the UI
            // This is necessary because the UI can only be updated on the main thread
            // Uses runOnUiThread instead of Handler.post because it's a shorter way to run code on the main thread
            runOnUiThread(() -> {
                FriendAdapter adapter = createFriendAdapter(friends);
                createRecyclerView(adapter);
            });
        });
    }

    // Runs when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Makes the app use the entire screen including system bars etc.
        EdgeToEdge.enable(this);
        // Show the activity_main.xml layout
        setContentView(R.layout.activity_main);
        // Set padding to the layout to avoid the system bars overlapping the content
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set click event listeners for buttons in the layout
        findViewById(R.id.addFriendButton).setOnClickListener(v -> startAddFriendActivity());
        findViewById(R.id.preferencesButton).setOnClickListener(v -> startSettingsActivity());
        /*
        Buttons used for testing and debugging
        findViewById(R.id.startServiceButton).setOnClickListener(v -> startPeriodicService());
        findViewById(R.id.stopServiceButton).setOnClickListener(v -> stopPeriodicService());
        findViewById(R.id.sendSmsBroadcastButton).setOnClickListener(v -> sendSmsBroadcast());
        findViewById(R.id.sendSmsButton).setOnClickListener(v -> startSendSmsService());
        findViewById(R.id.startBroadcastReceiverButton).setOnClickListener(v -> startBroadcastReceiver());

         */

        // Get an instance of the database to make database operations
        database = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();

        // Load the friends from the database when the activity is created
        loadFriendsFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load the friends from the database when the activity is resumed
        // For example when the user navigates back to the main activity from editing a friend
        loadFriendsFromDatabase();
    }
}