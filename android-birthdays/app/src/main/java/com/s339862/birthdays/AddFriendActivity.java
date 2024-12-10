package com.s339862.birthdays;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddFriendActivity extends AppCompatActivity {

    // These variables can be accessed from any method in the class
    private EditText nameEditText, birthdayEditText, phoneEditText;
    private AppDatabase database;

    // Gets values from input fields and adds a new friend to the database
    private void addNewFriend() {
        String name = nameEditText.getText().toString();
        String birthday = birthdayEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        Friend friend = new Friend(name, birthday, phone);

        // Execute the database operation on a separate thread
        Executors.newSingleThreadExecutor().execute(() -> {
            database.friendDao().insert(friend);

            // Finish the activity on the main thread
            // Using runOnUiThread instead of Handler.post because it's a shorter way to run code on the main thread
            runOnUiThread(this::finish);
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int defaultYear = calendar.get(Calendar.YEAR);
        int defaultMonth = calendar.get(Calendar.MONTH);
        int defaultDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format(Locale.getDefault(),"%02d/%02d/%04d", day, month+1, year);
            birthdayEditText.setText(date);
        }, defaultYear, defaultMonth, defaultDay);
        dialog.show();
    }

    // Runs when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Makes the app use the entire screen including system bars etc.
        EdgeToEdge.enable(this);

        // Show the activity_add_friend.xml layout
        setContentView(R.layout.activity_add_friend);

        // Set padding to the layout to avoid the system bars overlapping the content
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addFriend), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Gets an instance of the database to make database operations
        database = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();

        // Get the input fields from the layout
        nameEditText = findViewById(R.id.nameEditText);
        birthdayEditText = findViewById(R.id.birthdayEditText);
        phoneEditText = findViewById(R.id.phoneEditText);

        // Add click event listener to the save button
        // Adds a friend when the button is clicked
        findViewById(R.id.saveButton).setOnClickListener(v -> addNewFriend());
        birthdayEditText.setOnClickListener(v -> showDatePickerDialog());
    }
}
