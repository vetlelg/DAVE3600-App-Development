package com.s339862.mattespill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    // Runs when the application starts
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Show the main activity layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set onclick event listeners for the start, about and preferences buttons
        // Start the activity when the buttons are clicked
        findViewById(R.id.start_button).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, GameActivity.class));
        });
        findViewById(R.id.about_button).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        });
        findViewById(R.id.preferences_button).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });


    }


}